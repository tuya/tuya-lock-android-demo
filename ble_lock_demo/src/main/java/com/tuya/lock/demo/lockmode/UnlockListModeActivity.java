package com.tuya.lock.demo.lockmode;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.UnlockModeListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLock;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.TuyaUnlockType;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.UnlockMode;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class UnlockListModeActivity extends AppCompatActivity {

    private ITuyaBleLock tuyaLockDevice;
    private UnlockModeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_mode_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLock(deviceId);


        RadioGroup group_type = findViewById(R.id.group_type);
        group_type.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.unlock_fingerprint) {
                getUnlockModeList(TuyaUnlockType.FINGERPRINT);
            } else if (checkedId == R.id.unlock_ble) {
                getUnlockModeList(TuyaUnlockType.BLE);
            } else if (checkedId == R.id.unlock_password) {
                getUnlockModeList(TuyaUnlockType.PASSWORD);
            } else if (checkedId == R.id.unlock_temporary) {
                getUnlockModeList(TuyaUnlockType.TEMPORARY);
            } else if (checkedId == R.id.unlock_dynamic) {
                getUnlockModeList(TuyaUnlockType.DYNAMIC);
            } else if (checkedId == R.id.unlock_card) {
                getUnlockModeList(TuyaUnlockType.CARD);
            }
        });

        findViewById(R.id.unlock_mode_add).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UnlockModeAddActivity.class);
            intent.putExtra(Constant.DEVICE_ID, deviceId);
            v.getContext().startActivity(intent);
        });

        RecyclerView unlock_records_list = findViewById(R.id.unlock_records_list);
        unlock_records_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new UnlockModeListAdapter();
        adapter.setDevId(deviceId);
        adapter.delete(new IResultCallback<UnlockMode>() {
            @Override
            public void onSuccess(UnlockMode unlockMode) {
                tuyaLockDevice.deleteUnlockMode(unlockMode);
            }

            @Override
            public void onError(@Nullable String s, @Nullable String s1) {

            }
        });
        unlock_records_list.setAdapter(adapter);

        tuyaLockDevice.setUnlockModeListener((devId, userId, unlockModeResponse) -> {
            Log.i(Constant.TAG, "setUnlockModeListener: stage = " + unlockModeResponse.toString());
            Toast.makeText(getApplicationContext(), String.valueOf(unlockModeResponse.stage), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUnlockModeList(TuyaUnlockType.FINGERPRINT);
    }

    /**
     * 获取解锁方式
     */
    private void getUnlockModeList(String type) {
        tuyaLockDevice.getUnlockModeList(type, new ITuyaResultCallback<ArrayList<UnlockMode>>() {
            @Override
            public void onSuccess(ArrayList<UnlockMode> result) {
                Log.i(Constant.TAG, "getUnlockModeList success");
            }

            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "getUnlockModeList failed: code = " + code + "  message = " + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                ArrayList<UnlockMode> unlockModeArrayList = new ArrayList<>();
                UnlockMode unlockMode = new UnlockMode();
                unlockMode.unlockType = TuyaUnlockType.PASSWORD;
                unlockMode.lockUserId = 1;
                unlockMode.phase = 0;
                unlockMode.unlockName = "神奇密码";
                unlockMode.unlockId = "120202";

                unlockModeArrayList.add(unlockMode);
                adapter.setData(unlockModeArrayList);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
