package com.tuya.lock.demo.password;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.DynamicPasswordBean;

public class PasswordDynamicActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private TextView dynamic_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_dynamic);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(deviceId);

        dynamic_number = findViewById(R.id.dynamic_number);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDynamicPasswordData();
    }

    private void getDynamicPasswordData() {
        tuyaLockDevice.getLockDynamicPassword(new ITuyaResultCallback<DynamicPasswordBean>() {
            @Override
            public void onSuccess(DynamicPasswordBean result) {
                dynamic_number.setText(result.getDynamicPassword());
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                dynamic_number.setText(errorMessage);
            }
        });
    }
}
