package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.ble.constant.Constant;
import com.tuya.lock.demo.R;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.response.PasswordBean;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;

public class PasswordUpdateActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;

    private PasswordBean.DataBean mPasswordData;

    public static void startActivity(Context context, PasswordBean.DataBean passwordItem,
                                     String devId) {
        Intent intent = new Intent(context, PasswordUpdateActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        //编辑的密码数据
        intent.putExtra(Constant.PASSWORD_DATA, JSONObject.toJSONString(passwordItem));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_password_temp_update);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(getString(R.string.submit_edit));

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        mPasswordData = JSONObject.parseObject(getIntent().getStringExtra(Constant.PASSWORD_DATA), PasswordBean.DataBean.class);

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);


        EditText password_name = findViewById(R.id.password_name);
        password_name.setText(mPasswordData.getName());
        password_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mPasswordData.setName(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.password_add).setOnClickListener(v -> {
            createPassword();
        });
    }

    private void createPassword() {
        zigBeeLock.updateTemporaryPassword(mPasswordData.getName(), mPasswordData.getId(), new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}