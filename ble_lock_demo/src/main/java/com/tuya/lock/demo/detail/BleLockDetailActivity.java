package com.tuya.lock.demo.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuya.lock.demo.password.PasswordMainActivity;
import com.tuya.lock.demo.records.BleLockProRecordsActivity;
import com.tuya.lock.demo.setting.VoiceSettingActivity;
import com.tuya.lock.demo.unlock.BleSwitchLockActivity;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.records.BleLockRecordsActivity;
import com.tuya.lock.demo.setting.LockSettingActivity;
import com.tuya.lock.demo.state.ConnectStateActivity;
import com.tuya.lock.demo.lockmode.UnlockListModeActivity;
import com.tuya.lock.demo.user.UserMemberActivity;
import com.tuya.lock.demo.utils.CopyLinkTextHelper;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.optimus.lock.api.ITuyaBleLock;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDevice;

public class BleLockDetailActivity extends AppCompatActivity {

    private ITuyaDevice iTuyaDevice;
    private TextView device_state_view;
    private ITuyaBleLockV2 tuyaLockDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_ble_detail);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(deviceId);
        boolean isProDevice = tuyaLockDevice.isProDevice();


        iTuyaDevice = TuyaHomeSdk.newDeviceInstance(deviceId);
        iTuyaDevice.registerDevListener(listener);

        TextView device_info_view = findViewById(R.id.device_info_view);
        device_info_view.setText("设备ID：" + deviceId);

        device_state_view = findViewById(R.id.device_state_view);
        showState();

        findViewById(R.id.btn_get_device_info).setOnClickListener(v -> {
            CopyLinkTextHelper.getInstance(v.getContext()).CopyText(deviceId);
            Toast.makeText(v.getContext(), "复制成功", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.door_lock_member).setOnClickListener(v -> {
            //成员管理
            Intent intent = new Intent(v.getContext(), UserMemberActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.ble_connection).setOnClickListener(v -> {
            //蓝牙连接
            Intent intent = new Intent(v.getContext(), ConnectStateActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.ble_unlock_and_lock).setOnClickListener(v -> {
            //蓝牙解锁和落锁
            Intent intent = new Intent(v.getContext(), BleSwitchLockActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.lock_record_list).setOnClickListener(v -> {
            Intent intent;
            if (isProDevice) {
                //门锁记录 pro
                intent = new Intent(v.getContext(), BleLockProRecordsActivity.class);
            } else {
                //门锁记录 老版本
                intent = new Intent(v.getContext(), BleLockRecordsActivity.class);
            }
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });


        findViewById(R.id.unlock_mode_management).setOnClickListener(v -> {
            //解锁方式管理
            Intent intent = new Intent(v.getContext(), UnlockListModeActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.password_management).setOnClickListener(v -> {
            //临时密码
            Intent intent = new Intent(v.getContext(), PasswordMainActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.door_lock_settings).setOnClickListener(v -> {
            //远程开锁设置
            Intent intent = new Intent(v.getContext(), LockSettingActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        findViewById(R.id.voice_settings).setOnClickListener(v -> {
            //远程语音设置
            Intent intent = new Intent(v.getContext(), VoiceSettingActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });
    }

    private final IDevListener listener = new IDevListener() {
        @Override
        public void onDpUpdate(String devId, String dpStr) {

        }

        @Override
        public void onRemoved(String devId) {

        }

        @Override
        public void onStatusChanged(String devId, boolean online) {
            showState();
        }

        @Override
        public void onNetworkStatusChanged(String devId, boolean status) {

        }

        @Override
        public void onDevInfoUpdate(String devId) {

        }
    };

    private void showState() {
        boolean isBLEConnected = tuyaLockDevice.isBLEConnected();
        boolean isOnline = tuyaLockDevice.isOnline();
        if (!isBLEConnected && isOnline) {
            device_state_view.setText("网关已连接");
        } else if (isBLEConnected && isOnline) {
            device_state_view.setText("蓝牙已连接");
        } else {
            device_state_view.setText("离线");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iTuyaDevice.unRegisterDevListener();
        iTuyaDevice.onDestroy();
    }
}
