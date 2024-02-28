package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.ble.utils.DialogUtils;
import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.zigbee.adapter.PasswordListAdapter;
import com.tuya.sdk.os.TuyaOSDevice;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.request.PasswordRemoveRequest;
import com.tuya.smart.optimus.lock.api.zigbee.request.PasswordRequest;
import com.tuya.smart.optimus.lock.api.zigbee.response.PasswordBean;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.optimus.lock.bean.ZigBeeDatePoint;


public class PasswordListActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;
    private PasswordListAdapter adapter;
    private RecyclerView password_list;
    private TextView error_view;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, PasswordListActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_password_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);

        error_view = findViewById(R.id.error_view);
        password_list = findViewById(R.id.password_list);
        password_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new PasswordListAdapter();
        adapter.setDevId(mDevId);
        adapter.addCallback(new PasswordListAdapter.Callback() {
            @Override
            public void remove(PasswordBean.DataBean bean, int position) {
                DialogUtils.showDelete(PasswordListActivity.this, (dialog, which) ->
                        deletePasscode(bean, position)
                );
            }

            @Override
            public void freeze(PasswordBean.DataBean bean, int position, boolean isFreeze) {
                freezePasscode(bean, isFreeze);
            }

            @Override
            public void edit(PasswordBean.DataBean bean, int position) {
                PasswordDetailActivity.startEditActivity(PasswordListActivity.this, mDevId, bean);
            }
        });
        password_list.setAdapter(adapter);

        findViewById(R.id.password_add).setOnClickListener(v -> {
            PasswordDetailActivity.startActivity(v.getContext(), mDevId, 0);
        });

        findViewById(R.id.password_add_one).setOnClickListener(v -> {
            PasswordDetailActivity.startActivity(v.getContext(), mDevId, 1);
        });

        //是否支持一次性密码
        DeviceBean deviceBean = TuyaOSDevice.getDeviceBean(mDevId);
        if (deviceBean.getDpCodes().containsKey(ZigBeeDatePoint.SINGLE_USE_PASSWORD)) {
            findViewById(R.id.password_add_one).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.password_add_one).setVisibility(View.GONE);
        }

        findViewById(R.id.invalid_password_list).setOnClickListener(v -> {
            PasswordInvalidListActivity.startActivity(v.getContext(), mDevId);
        });
    }

    private void deletePasscode(PasswordBean.DataBean dataBean, int position) {
        PasswordRemoveRequest removeRequest = new PasswordRemoveRequest();
        removeRequest.setId(dataBean.getId());
        removeRequest.setOneTime(dataBean.getOneTime());
        removeRequest.setName(dataBean.getName());
        removeRequest.setEffectiveTime(dataBean.getEffectiveTime());
        removeRequest.setInvalidTime(dataBean.getInvalidTime());
        zigBeeLock.removeTemporaryPassword(removeRequest, new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(Constant.TAG, "deleteOnlineTempPassword onSuccess: " + result);
                Toast.makeText(getApplicationContext(), "delete success", Toast.LENGTH_SHORT).show();
                if (null != adapter) {
                    adapter.remove(position);
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "deleteOnlineTempPassword failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void freezePasscode(PasswordBean.DataBean dataBean, boolean isFreeze) {
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword(dataBean.getPassword());
        passwordRequest.setName(dataBean.getName());
        passwordRequest.setEffectiveTime(dataBean.getEffectiveTime());
        passwordRequest.setInvalidTime(dataBean.getInvalidTime());
        passwordRequest.setOneTime(dataBean.getOneTime());
        passwordRequest.setId(dataBean.getId());

        if (isFreeze) {
            zigBeeLock.freezeTemporaryPassword(passwordRequest, new ITuyaResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    L.i(Constant.TAG, "freezeTemporaryPassword onSuccess: " + result);
                    Toast.makeText(getApplicationContext(), "freeze success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    L.e(Constant.TAG, "freezeTemporaryPassword failed: code = " + errorCode + "  message = " + errorMessage);
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        zigBeeLock.unfreezeTemporaryPassword(passwordRequest, new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.i(Constant.TAG, "unfreezeTemporaryPassword onSuccess: " + result);
                Toast.makeText(getApplicationContext(), "unfreeze success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                L.e(Constant.TAG, "unfreezeTemporaryPassword failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOfflineTempPasswordList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 密码列表
     */
    private void getOfflineTempPasswordList() {
        zigBeeLock.getPasswordList(0, 50, new ITuyaResultCallback<PasswordBean>() {
            @Override
            public void onSuccess(PasswordBean result) {
                Log.i(Constant.TAG, "getOnlineTempPasswordList success: " + result);
                adapter.setData(result.getDatas());
                adapter.notifyDataSetChanged();
                if (result.getDatas().size() == 0) {
                    showError("暂无内容");
                } else {
                    password_list.setVisibility(View.VISIBLE);
                    error_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "getOnlineTempPasswordList failed: code = " + errorCode + "  message = " + errorMessage);
                showError(errorMessage);
            }
        });
    }

    private void showError(String msg) {
        password_list.setVisibility(View.GONE);
        error_view.setVisibility(View.VISIBLE);
        error_view.setText(msg);
    }
}