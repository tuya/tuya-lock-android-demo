package com.tuya.lock.demo.password;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.utils.Utils;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.bean.OfflineTempPassword;
import com.tuya.smart.optimus.lock.api.enums.OfflineTempPasswordType;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;

public class PasswordProOfflineAdd2Activity extends AppCompatActivity {


    private ITuyaBleLockV2 tuyaLockDevice;
    private TextView password_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_offline_add_2);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        password_content = findViewById(R.id.password_content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProSingleRevokeOfflinePassword();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    private void getProSingleRevokeOfflinePassword() {
        String unlockBindingId = getIntent().getStringExtra("unlockBindingId");
        String name = getIntent().getStringExtra("name");

        tuyaLockDevice.getProSingleRevokeOfflinePassword(unlockBindingId, name, new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(Constant.TAG, "setOfflineTempPasswordName success :" + JSONObject.toJSONString(result));
                password_content.setText(result);
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "setOfflineTempPasswordName failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}