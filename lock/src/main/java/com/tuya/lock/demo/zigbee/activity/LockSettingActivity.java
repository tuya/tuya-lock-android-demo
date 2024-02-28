package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.ble.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IResultCallback;

/**
 * 门锁设置
 */
public class LockSettingActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;
    private TextView remote_unlock_available;
    private boolean isOpen = true;
    private RadioButton set_remote_open;
    private RadioButton set_remote_close;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, LockSettingActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setting);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);

        remote_unlock_available = findViewById(R.id.remote_unlock_available);

        RadioGroup set_remote_group = findViewById(R.id.set_remote_group);
        set_remote_open = findViewById(R.id.set_remote_open);
        set_remote_close = findViewById(R.id.set_remote_close);

        set_remote_group.setOnCheckedChangeListener((group, checkedId) -> {
            isOpen = checkedId == R.id.set_remote_open;
        });

        findViewById(R.id.remote_button).setOnClickListener(v -> setVoicePassword());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                remote_unlock_available.setText(String.valueOf(result));

                if (result) {
                    set_remote_open.setChecked(true);
                } else {
                    set_remote_close.setChecked(true);
                }
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "get remote unlock available failed: code = " + code + "  message = " + message);
                remote_unlock_available.setText(message);
            }
        });
    }

    private void setVoicePassword() {
        zigBeeLock.setRemoteUnlockType(isOpen, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "setRemoteUnlockType failed: code = " + code + "  message = " + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                remote_unlock_available.setText(String.valueOf(isOpen));
            }
        });
    }
}