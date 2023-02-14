package com.tuya.lock.demo.unlock;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.BLELockUser;

/**
 * 开关锁demo
 */
public class BleSwitchLockActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_and_unlock);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(deviceId);

        /* *
         * 近场开锁
         */
        findViewById(R.id.ble_new_version_unlock).setOnClickListener(v -> bleUnlock());

        /* *
         * 近场关锁
         */
        findViewById(R.id.ble_new_version_lock).setOnClickListener(v -> bleManualLock());

        /* *
         * 远程开锁
         */
        findViewById(R.id.ble_far_unlock).setOnClickListener(v -> farUnlock());

        /* *
         * 远程关锁
         */
        findViewById(R.id.ble_far_lock).setOnClickListener(v -> farLock());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    private void bleUnlock() {
        tuyaLockDevice.getCurrentUser(new ITuyaResultCallback<BLELockUser>() {
            @Override
            public void onSuccess(BLELockUser result) {
                Log.i(Constant.TAG, "getCurrentUser:" + JSONObject.toJSONString(result));

                tuyaLockDevice.bleUnlock(String.valueOf(result.lockUserId), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Log.i(Constant.TAG, "bleUnlock onError code:" + code + ", error:" + error);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "解锁成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "getCurrentUser onError code:" + code + ", error:" + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bleManualLock() {
        tuyaLockDevice.bleManualLock(new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.i(Constant.TAG, "bleManualLock onError code:" + code + ", error:" + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "关锁成功", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void farUnlock() {
        Log.i(Constant.TAG, "remoteSwitchLock");
        tuyaLockDevice.remoteSwitchLock(true, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "remoteSwitchLock unlock onError code:" + code + ", error:" + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "远程开锁成功", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void farLock() {
        tuyaLockDevice.remoteSwitchLock(false, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "remoteSwitchLock lock onError code:" + code + ", error:" + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "远程关锁成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
