package com.tuya.lock.demo.wifi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.ble.constant.Constant;
import com.tuya.lock.demo.wifi.adapter.RecordListAdapter;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaWifiLock;
import com.tuya.smart.optimus.lock.api.bean.Record;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;


/**
 * WIFI 开门记录
 */
public class DoorRecordListActivity extends AppCompatActivity {

    private ITuyaWifiLock wifiLock;
    private RecordListAdapter adapter;

    private RecyclerView recyclerView;
    private TextView error_view;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, DoorRecordListActivity.class);
        intent.putExtra(Constant.DEVICE_ID, devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_record_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(R.string.zigbee_door_record_list_title);

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        wifiLock = tuyaLockManager.getWifiLock(mDevId);

        error_view = findViewById(R.id.error_view);
        recyclerView = findViewById(R.id.record_list_view);
        adapter = new RecordListAdapter();
        adapter.setDevice(mDevId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiLock.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiLock.getUnlockRecords(0, 30, new ITuyaResultCallback<Record>() {
            @Override
            public void onSuccess(Record result) {
                if (result.datas.size() == 0) {
                    showError(getString(R.string.zigbee_no_content));
                } else {
                    adapter.setData(result.datas);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    error_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void showError(String msg) {
        recyclerView.setVisibility(View.GONE);
        error_view.setVisibility(View.VISIBLE);
        error_view.setText(msg);
    }
}
