package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.ble.constant.Constant;
import com.tuya.lock.demo.R;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.request.RemotePermissionEnum;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ZigBeeDatePoint;

import java.util.ArrayList;
import java.util.List;


/**
 * 门锁设置
 */
public class SettingActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;
    private String mDevId;
    private TextView remote_set_state;
    private TextView voice_set_state;
    private TextView remote_permissions_state;
    private LinearLayout remote_permissions_view;
    private ListPopupWindow listPopupWindow;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, SettingActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_setting);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);

        remote_set_state = findViewById(R.id.remote_set_state);
        voice_set_state = findViewById(R.id.voice_set_state);
        remote_permissions_state = findViewById(R.id.remote_permissions_state);
        remote_permissions_view = findViewById(R.id.remote_permissions_view);


        findViewById(R.id.remote_set_view).setOnClickListener(v ->
                LockSettingActivity.startActivity(SettingActivity.this, mDevId)
        );
        findViewById(R.id.voice_settings).setOnClickListener(v ->
                VoiceSettingActivity.startActivity(SettingActivity.this, mDevId)
        );
        if (!TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.REMOTE_UNLOCK)) ||
                !TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.REMOTE_NO_DP_KEY))) {
            findViewById(R.id.remote_set_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.remote_set_view).setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.UNLOCK_VOICE_REMOTE))) {
            findViewById(R.id.voice_settings).setVisibility(View.GONE);
            findViewById(R.id.voice_settings_line).setVisibility(View.GONE);
        } else {
            findViewById(R.id.voice_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.voice_settings_line).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.dp_settings).setOnClickListener(v ->
                DpSettingActivity.startActivity(SettingActivity.this, mDevId)
        );

        setRemotePermissionsUi(true);

        remote_permissions_view.setOnClickListener(v -> {
            if (null != listPopupWindow) {
                listPopupWindow.show();
            }
        });
    }

    private void setRemotePermissionsUi(boolean isShow) {
        if (isShow && !TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.REMOTE_UNLOCK))) {
            remote_permissions_view.setVisibility(View.VISIBLE);
            findViewById(R.id.remote_permissions_line).setVisibility(View.VISIBLE);
        } else {
            remote_permissions_view.setVisibility(View.GONE);
            findViewById(R.id.remote_permissions_line).setVisibility(View.GONE);
        }
    }

    private void setVoiceUnlockUi(boolean isShow) {
        if (isShow) {
            findViewById(R.id.voice_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.voice_settings_line).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.voice_settings).setVisibility(View.GONE);
            findViewById(R.id.voice_settings_line).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRemoteUnlockAvailable();
    }

    private void isRemoteUnlockAvailable() {
        zigBeeLock.fetchRemoteUnlockType(new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.i(Constant.TAG, "get remote unlock available success:" + result);
                remote_set_state.post(() -> {
                    if (result) {
                        remote_set_state.setText(getString(R.string.set_voice_password_open));
                        getRemoteUnlockPermissionValue();
                        fetchRemoteVoiceUnlock();
                    } else {
                        remote_set_state.setText(getString(R.string.set_voice_password_close));
                    }
                    setRemotePermissionsUi(result);
                    setVoiceUnlockUi(result);
                });
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "get remote unlock available failed: code = " + code + "  message = " + message);
            }
        });
    }

    private void fetchRemoteVoiceUnlock() {
        if (TextUtils.isEmpty(zigBeeLock.convertCode2Id(ZigBeeDatePoint.UNLOCK_VOICE_REMOTE))) {
            Log.e(Constant.TAG, "fetchRemoteVoiceUnlock UNLOCK_VOICE_REMOTE is not");
            return;
        }
        zigBeeLock.fetchRemoteVoiceUnlock(new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.i(Constant.TAG, "fetchRemoteVoiceUnlock success:" + result);
                voice_set_state.post(() -> {
                    if (result) {
                        voice_set_state.setText(getString(R.string.set_voice_password_open));
                    } else {
                        voice_set_state.setText(getString(R.string.set_voice_password_close));
                    }
                });
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "fetchRemoteVoiceUnlock failed: code = " + code + "  message = " + message);
            }
        });
    }

    private void getRemoteUnlockPermissionValue() {
        zigBeeLock.getRemoteUnlockPermissionValue(new ITuyaResultCallback<RemotePermissionEnum>() {
            @Override
            public void onSuccess(RemotePermissionEnum result) {
                String remotePermissionsStr = "";
                switch (result) {
                    case REMOTE_UNLOCK_ALL:
                        remotePermissionsStr = "所有人含密";
                        break;
                    case REMOTE_UNLOCK_ADMIN:
                        remotePermissionsStr = "管理员含密";
                        break;
                    case REMOTE_NOT_DP_KEY_ALL:
                        remotePermissionsStr = "所有人免密";
                        break;
                    case REMOTE_NOT_DP_KEY_ADMIN:
                        remotePermissionsStr = "管理员免密";
                        break;
                }
                remote_permissions_state.setText(remotePermissionsStr);
                showPupList();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }

    private void showPupList() {
        listPopupWindow = new ListPopupWindow(SettingActivity.this, null, R.attr.listPopupWindowStyle);
        listPopupWindow.setAnchorView(remote_permissions_view);
        List<String> items = new ArrayList<>();
        items.add("管理员免密");
        items.add("所有人免密");
        items.add("管理员含密");
        items.add("所有人含密");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SettingActivity.this, R.layout.device_zigbee_dp_enum_popup_item, items);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                setRemoteOpenState(RemotePermissionEnum.REMOTE_NOT_DP_KEY_ADMIN);
            } else if (position == 1) {
                setRemoteOpenState(RemotePermissionEnum.REMOTE_NOT_DP_KEY_ALL);
            } else if (position == 2) {
                setRemoteOpenState(RemotePermissionEnum.REMOTE_UNLOCK_ADMIN);
            } else if (position == 3) {
                setRemoteOpenState(RemotePermissionEnum.REMOTE_UNLOCK_ALL);
            }
            listPopupWindow.dismiss();
        });
    }

    private void setRemoteOpenState(RemotePermissionEnum permissionEnum) {
        zigBeeLock.setRemoteUnlockPermissionValue(permissionEnum, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                runOnUiThread(() -> Toast.makeText(SettingActivity.this, error, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onSuccess() {
                getRemoteUnlockPermissionValue();
            }
        });
    }
}