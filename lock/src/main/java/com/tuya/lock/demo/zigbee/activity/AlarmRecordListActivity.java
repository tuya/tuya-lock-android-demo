package com.tuya.lock.demo.zigbee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.zigbee.adapter.RecordListAdapter;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.ITuyaZigBeeLock;
import com.tuya.smart.optimus.lock.api.zigbee.response.RecordBean;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.ZigBeeDatePoint;

import java.util.ArrayList;
import java.util.List;

public class AlarmRecordListActivity extends AppCompatActivity {

    private String mDevId;

    private ITuyaZigBeeLock zigBeeLock;
    private RecordListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView error_view;

    public static void startActivity(Context context, String devId) {
        Intent intent = new Intent(context, AlarmRecordListActivity.class);
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

        mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        zigBeeLock = tuyaLockManager.getZigBeeLock(mDevId);

        error_view = findViewById(R.id.error_view);
        recyclerView = findViewById(R.id.record_list_view);
        adapter = new RecordListAdapter();
        adapter.setDevice(mDevId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> dpIds = new ArrayList<>();
        dpIds.add(zigBeeLock.convertCode2Id(ZigBeeDatePoint.HI_JACK));
        dpIds.add(zigBeeLock.convertCode2Id(ZigBeeDatePoint.ALARM_LOCK));
        dpIds.add(zigBeeLock.convertCode2Id(ZigBeeDatePoint.DOORBELL));
        zigBeeLock.getAlarmRecordList(dpIds, 0, 30, new ITuyaResultCallback<RecordBean>() {
            @Override
            public void onSuccess(RecordBean result) {
                adapter.setData(result.getDatas());
                adapter.notifyDataSetChanged();
                if (result.getDatas().size() == 0) {
                    showError(getString(R.string.zigbee_no_content));
                } else {
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
        recyclerView.post(() -> {
            recyclerView.setVisibility(View.GONE);
            error_view.setVisibility(View.VISIBLE);
            error_view.setText(msg);
        });
    }
}
