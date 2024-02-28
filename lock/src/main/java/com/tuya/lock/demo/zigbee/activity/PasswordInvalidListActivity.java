package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.ble.utils.DialogUtils;
import com.tuya.lock.demo.zigbee.adapter.PasswordListAdapter;
import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.lock.demo.R;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.response.PasswordBean;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;


public class PasswordInvalidListActivity extends AppCompatActivity {

    private ITuyaZigBeeLock zigBeeLock;
    private PasswordListAdapter adapter;
    private RecyclerView password_list;
    private TextView error_view;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, PasswordInvalidListActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_invalid_password_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);

        error_view = findViewById(R.id.error_view);
        password_list = findViewById(R.id.password_list);
        password_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new PasswordListAdapter();
        adapter.setDevId(mDevId);
        adapter.hideDelete();
        password_list.setAdapter(adapter);

        findViewById(R.id.password_add).setOnClickListener(v -> {
            DialogUtils.showClear(PasswordInvalidListActivity.this, (dialog, which) -> {
                        clearData();
                    }
            );
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
    }

    /**
     * 密码列表
     */
    private void getOfflineTempPasswordList() {
        zigBeeLock.getInvalidPasswordList(0, 100, new ITuyaResultCallback<PasswordBean>() {
            @Override
            public void onSuccess(PasswordBean result) {
                Log.i(Constant.TAG, "getOnlineTempPasswordList success: " + result);
                adapter.setData(result.getDatas());
                adapter.notifyDataSetChanged();
                if (result.getDatas().size() == 0) {
                    showError("暂无内容");
                } else {
                    password_list.setVisibility(View.VISIBLE);
                    error_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "getOnlineTempPasswordList failed: code = " + errorCode + "  message = " + errorMessage);
                showError(errorMessage);
            }
        });
    }

    private void clearData() {
        zigBeeLock.removeInvalidPassword(new ITuyaResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(PasswordInvalidListActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
                getOfflineTempPasswordList();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void showError(String msg) {
        password_list.setVisibility(View.GONE);
        error_view.setVisibility(View.VISIBLE);
        error_view.setText(msg);
    }
}