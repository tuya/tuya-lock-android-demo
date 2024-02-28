package com.tuya.lock.demo.zigbee.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.DynamicPasswordBean;

public class PasswordDynamicActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;
    private TextView dynamic_number;
    private ProgressBar progress_view;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, PasswordDynamicActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_password_dynamic);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(deviceId);

        dynamic_number = findViewById(R.id.dynamic_number);
        progress_view = findViewById(R.id.progress_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDynamicPasswordData();
    }

    private void getDynamicPasswordData() {
        zigBeeLock.getDynamicPassword(new ITuyaResultCallback<DynamicPasswordBean>() {
            @Override
            public void onSuccess(DynamicPasswordBean result) {
                dynamic_number.setText(result.getDynamicPassword());
                countDownTimer.cancel();
                countDownTimer.start();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                dynamic_number.setText(errorMessage);
                progress_view.setProgress(0);
            }
        });
    }

    private final CountDownTimer countDownTimer = new CountDownTimer(300000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            int value = (int) (millisUntilFinished / 1000);
            progress_view.post(() -> progress_view.setProgress(value));
        }

        @Override
        public void onFinish() {
            getDynamicPasswordData();
        }
    };
}