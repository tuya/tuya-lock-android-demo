package com.tuya.lock.demo.password;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.PasswordOldOnlineListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.TempPasswordBeanV3;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PasswordOldOnlineListActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private PasswordOldOnlineListAdapter adapter;
    private int availTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_old_online_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        String type = getIntent().getStringExtra(Constant.PASSWORD_TYPE);
        if (type.equals(Constant.TYPE_SINGLE)) {
            availTimes = 1;
        } else if (type.equals(Constant.TYPE_MULTIPLE)) {
            availTimes = 0;
        }

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        RecyclerView rvList = findViewById(R.id.password_list);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new PasswordOldOnlineListAdapter();
        adapter.setDevId(mDevId);
        adapter.delete(new IResultCallback<TempPasswordBeanV3>() {
            @Override
            public void onSuccess(TempPasswordBeanV3 tempPasswordBeanV3) {
                tuyaLockDevice.deleteOnlinePassword(tempPasswordBeanV3.passwordId, tempPasswordBeanV3.sn, new ITuyaResultCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.i(Constant.TAG, "deleteOnlineTempPassword onSuccess: " + result);
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        Log.e(Constant.TAG, "deleteOnlineTempPassword failed: code = " + errorCode + "  message = " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@Nullable String s, @Nullable String s1) {

            }
        });
        rvList.setAdapter(adapter);

        findViewById(R.id.password_add).setOnClickListener(v -> {
            PasswordOldOnlineDetailActivity.startActivity(v.getContext(), null,mDevId, 0);
        });
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
        tuyaLockDevice.getOnlinePasswordList(availTimes, new ITuyaResultCallback<ArrayList<TempPasswordBeanV3>>() {
            @Override
            public void onSuccess(ArrayList<TempPasswordBeanV3> result) {
                Log.i(Constant.TAG, "getOnlineTempPasswordList success: " + result);
                adapter.setData(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "getOnlineTempPasswordList failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
