package com.tuya.lock.demo.password;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.PasswordSingleRevokeListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.bean.OfflineTempPassword;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.scene.api.IResultCallback;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PasswordSingleRevokeListActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private PasswordSingleRevokeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_single_revoke_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        RecyclerView rvList = findViewById(R.id.password_list);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new PasswordSingleRevokeListAdapter();
        adapter.setDevId(mDevId);
        adapter.delete(new IResultCallback<OfflineTempPassword>() {
            @Override
            public void onSuccess(OfflineTempPassword offlineTempPassword) {

            }

            @Override
            public void onError(@Nullable String s, @Nullable String s1) {

            }
        });
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOfflineTempPasswordList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    /**
     * 获取门锁成员
     */
    private void getOfflineTempPasswordList() {
        tuyaLockDevice.getSingleRevokePasswordList(new ITuyaResultCallback<ArrayList<OfflineTempPassword>>() {
            @Override
            public void onSuccess(ArrayList<OfflineTempPassword> result) {
                Log.i(Constant.TAG, "getSingleRevokePasswordList success: " + result);
                adapter.setData(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "getSingleRevokePasswordList failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
