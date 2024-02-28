package com.tuya.lock.demo.ble.activity.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.ble.constant.Constant;
import com.tuya.lock.demo.ble.utils.DialogUtils;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;

public class DeleteDeviceActivity extends AppCompatActivity {

    private ITuyaDevice mDevice;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, DeleteDeviceActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_deletel);


        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);

        mDevice = TuyaHomeSdk.newDeviceInstance(deviceId);


        findViewById(R.id.delete_view).setOnClickListener(v -> {
            DialogUtils.showDelete(v.getContext(), getResources().getString(R.string.whether_to_remove_device), (dialog, which) -> mDevice.removeDevice(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    Log.e(Constant.TAG, "removeDevice onError code:" + code + ", error:" + error);
                    Toast.makeText(v.getContext(), error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(v.getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }));
        });

        findViewById(R.id.clear_view).setOnClickListener(v -> {
            DialogUtils.showDelete(v.getContext(), getResources().getString(R.string.whether_to_remove_device_clear), (dialog, which) -> mDevice.resetFactory(new IResultCallback() {
                @Override
                public void onError(String code, String error) {
                    Log.e(Constant.TAG, "removeDevice onError code:" + code + ", error:" + error);
                    Toast.makeText(v.getContext(), error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(v.getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.onDestroy();
    }
}