package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.ble.activity.detail.DeleteDeviceActivity;
import com.tuya.lock.demo.ble.utils.DialogUtils;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.lock.demo.zigbee.view.LockButtonProgressView;
import com.tuya.sdk.os.TuyaOSDevice;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.request.RemotePermissionEnum;
import com.tuya.smart.optimus.lock.api.zigbee.response.MemberInfoBean;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.optimus.lock.bean.ZigBeeDatePoint;
import com.tuya.smart.sdk.optimus.lock.utils.StandardDpConverter;

import java.util.Map;

public class DeviceDetail extends AppCompatActivity {

    private TextView guard_view;
    private LockButtonProgressView unlock_btn;
    private TextView closed_door_view;
    private TextView anti_lock_view;
    private TextView child_lock_view;
    private TextView power_view;
    private TextView alarm_record_view;
    private TextView door_record_view;
    private TextView temporary_password_view;
    private TextView member_list_view;
    private TextView setting_view;
    private TextView dynamic_password_view;
    private String mDevId;
    private ITuyaZigBeeLock zigBeeLock;
    private int isRemoteOpen = -1;//0 免密正常、-1 无此功能、-2 无权限、1 密钥开门 -3 网络异常
    private ITuyaDevice ITuyaDevice;
    private boolean isLoadNum = false;

    private boolean isOpen = false;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, DeviceDetail.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_device_detail);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);
        ITuyaDevice = TuyaHomeSdk.newDeviceInstance(mDevId);
        ITuyaDevice.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                Map<String, Object> dpData = StandardDpConverter.convertIdToCodeMap(dpStr, StandardDpConverter.getSchemaMap(mDevId));
                dealAddUnlockMode(dpData);
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                deviceOnline();
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });

        initView();
    }


    private void dealAddUnlockMode(Map<String, Object> dpData) {
        for (String key : dpData.keySet()) {
            String lockResponse = String.valueOf(dpData.get(key));
            switch (key) {
                case ZigBeeDatePoint.REMOTE_RESULT:
                    doorOpenSuccess();
                    break;
                case ZigBeeDatePoint.CLOSED_OPENED:
                    checkDoorOpen(lockResponse);
                    break;
                case ZigBeeDatePoint.REVERSE_LOCK:
                    checkReverseOpen(Boolean.parseBoolean(lockResponse));
                    break;
                case ZigBeeDatePoint.CHILD_LOCK:
                    checkChildOpen(Boolean.parseBoolean(lockResponse));
                    break;
                case ZigBeeDatePoint.RESIDUAL_ELECTRICITY:
                    checkResidual(Integer.parseInt(lockResponse));
                    break;
                case ZigBeeDatePoint.HI_JACK:
                case ZigBeeDatePoint.ALARM_LOCK:
                case ZigBeeDatePoint.DOORBELL:
                    //有告警记录上报，查询未读数
                    if (isLoadNum) {
                        return;
                    }
                    isLoadNum = true;
                    getUnRead();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zigBeeLock.onDestroy();
        ITuyaDevice.onDestroy();
    }

    private void initView() {
        guard_view = findViewById(R.id.guard_view);
        unlock_btn = findViewById(R.id.unlock_btn);
        closed_door_view = findViewById(R.id.closed_door_view);
        anti_lock_view = findViewById(R.id.anti_lock_view);
        child_lock_view = findViewById(R.id.child_lock_view);
        power_view = findViewById(R.id.power_view);
        alarm_record_view = findViewById(R.id.alarm_record_view);
        door_record_view = findViewById(R.id.door_record_view);
        temporary_password_view = findViewById(R.id.temporary_password_view);
        setting_view = findViewById(R.id.setting_view);
        member_list_view = findViewById(R.id.member_list_view);
        dynamic_password_view = findViewById(R.id.dynamic_password_view);
    }

    private void initData() {
        DeviceBean deviceBean = TuyaOSDevice.getDeviceBean(mDevId);
        //未读消息
        getUnRead();
        zigBeeLock.getSecurityGuardDays(new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String guard = "已安全守护" + result + "天";
                guard_view.post(() ->
                        guard_view.setText(guard)
                );
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });

        unlock_btn.addClickCallback(() -> {
            if (isRemoteOpen == 0) {
                remoteUnlock();
            } else if (isRemoteOpen == 1) {
                runOnUiThread(() -> {
                    DialogUtils.showInputEdit(DeviceDetail.this, new DialogUtils.InputCallback() {
                        @Override
                        public void input(String password) {
                            remotePasswordUnlock(password);
                        }

                        @Override
                        public void close() {
                            unlock_btn.setTitle(getString(R.string.zigbee_unlock_open));
                            unlock_btn.setProgress(0);
                            unlock_btn.setEnabled(true);
                        }
                    });
                });
            }
        });
        zigBeeLock.fetchRemoteUnlockType(new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                //远程开门关闭
                if (!result) {
                    isRemoteOpen = -1;
                    deviceOnline();
                    return;
                }
                //无远程含密开门权限
                if (TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.REMOTE_UNLOCK))) {
                    isRemoteOpen = 0;
                    deviceOnline();
                    return;
                }
                zigBeeLock.getRemoteUnlockPermissionValue(new ITuyaResultCallback<RemotePermissionEnum>() {
                    @Override
                    public void onSuccess(RemotePermissionEnum result) {
                        switch (result) {
                            case REMOTE_NOT_DP_KEY_ADMIN:
                                zigBeeLock.getMemberInfo(new ITuyaDataCallback<MemberInfoBean>() {
                                    @Override
                                    public void onSuccess(MemberInfoBean result) {
                                        if (result.getUserType() == 10 || result.getUserType() == 50) {
                                            //只有管理员能远程免密开门
                                            isRemoteOpen = 0;
                                        } else {
                                            //无法使用
                                            isRemoteOpen = -2;
                                        }
                                        deviceOnline();
                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMessage) {
                                        isRemoteOpen = -3;
                                        deviceOnline();
                                    }
                                });
                                break;
                            case REMOTE_NOT_DP_KEY_ALL:
                                //所有人可以免密开门
                                isRemoteOpen = 0;
                                deviceOnline();
                                break;
                            case REMOTE_UNLOCK_ADMIN:
                                zigBeeLock.getMemberInfo(new ITuyaDataCallback<MemberInfoBean>() {
                                    @Override
                                    public void onSuccess(MemberInfoBean result) {
                                        //只有管理员可以含密开门
                                        if (result.getUserType() == 10 || result.getUserType() == 50) {
                                            isRemoteOpen = 1;
                                        } else {
                                            //其他人无法操作
                                            isRemoteOpen = -2;
                                        }
                                        deviceOnline();
                                    }

                                    @Override
                                    public void onError(String errorCode, String errorMessage) {
                                        isRemoteOpen = -3;
                                        deviceOnline();
                                    }
                                });
                                break;
                            case REMOTE_UNLOCK_ALL:
                                //所有人含密开门
                                isRemoteOpen = 1;
                                deviceOnline();
                                break;
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        isRemoteOpen = 0;
                        deviceOnline();
                    }
                });

            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                unlock_btn.setEnabled(false);
                unlock_btn.setTitle(errorMessage);
            }
        });

        //门是否关闭
        if (null != deviceBean && deviceBean.getDpCodes().containsKey(ZigBeeDatePoint.CLOSED_OPENED)) {
            String closedCode = (String) deviceBean.getDpCodes().get(ZigBeeDatePoint.CLOSED_OPENED);
            checkDoorOpen(closedCode);
            closed_door_view.setVisibility(View.VISIBLE);
        } else {
            closed_door_view.setVisibility(View.GONE);
        }

        //是否反锁
        if (null != deviceBean && deviceBean.getDpCodes().containsKey(ZigBeeDatePoint.REVERSE_LOCK)) {
            Boolean reverseCode = (Boolean) deviceBean.getDpCodes().get(ZigBeeDatePoint.REVERSE_LOCK);
            checkReverseOpen(reverseCode);
            anti_lock_view.setVisibility(View.VISIBLE);
        } else {
            anti_lock_view.setVisibility(View.GONE);
        }

        //是否童锁
        if (null != deviceBean && deviceBean.getDpCodes().containsKey(ZigBeeDatePoint.CHILD_LOCK)) {
            Boolean childCode = (Boolean) deviceBean.getDpCodes().get(ZigBeeDatePoint.CHILD_LOCK);
            checkChildOpen(childCode);
            child_lock_view.setVisibility(View.VISIBLE);
        } else {
            child_lock_view.setVisibility(View.GONE);
        }

        //电量
        if (null != deviceBean && deviceBean.getDpCodes().containsKey(ZigBeeDatePoint.RESIDUAL_ELECTRICITY)) {
            Integer residualCode = (Integer) deviceBean.getDpCodes().get(ZigBeeDatePoint.RESIDUAL_ELECTRICITY);
            checkResidual(residualCode);
        } else {
            power_view.setText("电量不支持");
        }

        /**
         * 告警记录
         */
        alarm_record_view.setOnClickListener(v -> {
                    isLoadNum = false;
                    AlarmRecordListActivity.startActivity(DeviceDetail.this, mDevId);
                }
        );

        /**
         * 开门记录
         */
        door_record_view.setOnClickListener(v ->
                DoorRecordListActivity.startActivity(DeviceDetail.this, mDevId)
        );

        /**
         * 临时密码
         */
        temporary_password_view.setOnClickListener(v ->
                PasswordListActivity.startActivity(DeviceDetail.this, mDevId)
        );
        /**
         * 动态密码
         */
        dynamic_password_view.setOnClickListener(v ->
                PasswordDynamicActivity.startActivity(DeviceDetail.this, mDevId)
        );
        /**
         * 家庭成员入口
         */
        member_list_view.setOnClickListener(v ->
                MemberListActivity.startActivity(DeviceDetail.this, mDevId)
        );
        /**
         * 设置
         */
        setting_view.setOnClickListener(v ->
                SettingActivity.startActivity(DeviceDetail.this, mDevId)
        );
        /**
         * 解绑
         */
        findViewById(R.id.device_delete).setOnClickListener(v -> {
            DeleteDeviceActivity.startActivity(v.getContext(), mDevId);
        });
    }

    private void deviceOnline() {
        DeviceBean deviceBean = TuyaOSDevice.getDeviceBean(mDevId);
        //是否开启远程开门的功能
        if (isRemoteOpen == 0 || isRemoteOpen == 1) {
            unlock_btn.setEnabled(deviceBean.getIsOnline());
            //设备是否在线
            if (deviceBean.getIsOnline()) {
                unlock_btn.setTitle(getString(R.string.zigbee_unlock_open));
            } else {
                unlock_btn.setTitle(getString(R.string.zigbee_device_offline));
            }
        } else if (isRemoteOpen == -2) {
            unlock_btn.setEnabled(false);
            unlock_btn.setTitle("权限不足");
        } else if (isRemoteOpen == -3) {
            unlock_btn.setEnabled(false);
            unlock_btn.setTitle("网络异常");
        } else {
            unlock_btn.setEnabled(false);
            unlock_btn.setTitle("功能未开启");
        }
    }

    private void checkDoorOpen(String closedOpened) {
        //门是否关闭
        if (TextUtils.equals(closedOpened, "open")) {
            closed_door_view.setText("门未关");
        } else {
            closed_door_view.setText("门已关");
        }
    }

    private void checkReverseOpen(Boolean reverseCode) {
        if (reverseCode) {
            anti_lock_view.setText("已反锁");
        } else {
            anti_lock_view.setText("未反锁");
        }
    }

    private void checkChildOpen(Boolean childCode) {
        if (childCode) {
            child_lock_view.setText("童锁开");
        } else {
            child_lock_view.setText("童锁关");
        }
    }

    private void checkResidual(Integer residualCode) {
        String residual = "电量:" + residualCode + "%";
        power_view.setText(residual);
    }

    private void remoteUnlock() {
        unlock_btn.setTitle("Loading");
        unlock_btn.setEnabled(false);
        zigBeeLock.remoteUnlock(new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                showToast(error);
                unlock_btn.setTitle(getString(R.string.zigbee_unlock_open));
                unlock_btn.setProgress(0);
                unlock_btn.setEnabled(true);
            }

            @Override
            public void onSuccess() {
                isOpen = true;
            }
        });
    }

    private void getUnRead() {
        zigBeeLock.getUnreadAlarmNumber(new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.equals(result, "0")) {
                    String numStr = "告警记录（NEW）";
                    alarm_record_view.post(() -> alarm_record_view.setText(numStr));
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(DeviceDetail.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void remotePasswordUnlock(String password) {
        unlock_btn.setTitle("Loading");
        unlock_btn.setEnabled(false);
        zigBeeLock.remoteUnlock(password, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                showToast(error);
                unlock_btn.setTitle(getString(R.string.zigbee_unlock_open));
                unlock_btn.setProgress(0);
                unlock_btn.setEnabled(true);
            }

            @Override
            public void onSuccess() {
                isOpen = true;
            }
        });
    }

    /**
     * 开门成功提示
     */
    private void doorOpenSuccess() {
        if (isOpen) {
            showToast("开锁成功");
            unlock_btn.setProgress(0);
            unlock_btn.setTitle(getString(R.string.zigbee_unlock_open));
            unlock_btn.setEnabled(true);
            isOpen = false;
        }
    }
}

