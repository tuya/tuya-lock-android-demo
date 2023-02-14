package com.tuya.lock.demo.lockmode;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.camera.utils.L;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLock;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.TuyaUnlockType;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.ble.BLELockUser;

public class UnlockModeAddActivity extends AppCompatActivity {

    private ITuyaBleLock tuyaLockDevice;
    private String name;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_mode_add);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText add_name = findViewById(R.id.add_name);
        add_name.setText("无敌风火轮");
        name = "无敌风火轮";
        add_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    name = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText add_password = findViewById(R.id.add_password);
        add_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    password = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLock(deviceId);
        tuyaLockDevice.setUnlockModeListener((devId, userId, unlockModeResponse) -> {
            L.e(Constant.TAG, JSONObject.toJSONString(unlockModeResponse));
            boolean isSuccess = unlockModeResponse.success;
            if (isSuccess) {
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "添加失败：" + unlockModeResponse.status, Toast.LENGTH_SHORT).show();
            }

        });

        findViewById(R.id.unlock_mode_add).setOnClickListener(v -> {
            addUnlockMode();
        });
    }

    private void addUnlockMode() {
        tuyaLockDevice.getCurrentUser(new ITuyaResultCallback<BLELockUser>() {
            @Override
            public void onSuccess(BLELockUser result) {
                tuyaLockDevice.addUnlockMode(TuyaUnlockType.PASSWORD, result, name, password, 0, false);
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }
}
