package com.tuya.lock.demo.records;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.RecordProListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.view.FlowRadioGroup;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.bean.ProRecord;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.ble.RecordRequest;

import java.util.ArrayList;


/**
 * 开锁记录和告警记录
 */
public class BleLockProRecordsActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private RecordProListAdapter listAdapter;
    private final RecordRequest request = new RecordRequest();
    private final ArrayList<RecordRequest.LogRecord> logCategories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pro_records);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(deviceId);


        RecyclerView unlock_records_list = findViewById(R.id.unlock_records_list);

        unlock_records_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        listAdapter = new RecordProListAdapter();
        listAdapter.setDevice(deviceId);
        unlock_records_list.setAdapter(listAdapter);

        logCategories.add(RecordRequest.LogRecord.UNLOCK_RECORD);
        logCategories.add(RecordRequest.LogRecord.CLOSE_RECORD);
        logCategories.add(RecordRequest.LogRecord.ALARM_RECORD);
        logCategories.add(RecordRequest.LogRecord.OPERATION);

        FlowRadioGroup records_type = findViewById(R.id.records_type);
        records_type.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.unlock_records) {
                logCategories.clear();
                logCategories.add(RecordRequest.LogRecord.UNLOCK_RECORD);
            } else if (checkedId == R.id.close_records) {
                logCategories.clear();
                logCategories.add(RecordRequest.LogRecord.CLOSE_RECORD);
            } else if (checkedId == R.id.alarm_records) {
                logCategories.clear();
                logCategories.add(RecordRequest.LogRecord.ALARM_RECORD);
            } else if (checkedId == R.id.operation_records) {
                logCategories.clear();
                logCategories.add(RecordRequest.LogRecord.OPERATION);
            }
            getUnlockRecords();
        });

        getUnlockRecords();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getUnlockRecords() {
        request.setLogCategories(logCategories);
        request.setLimit(10);
        tuyaLockDevice.getProUnlockRecordList(request, new ITuyaResultCallback<ProRecord>() {
            @Override
            public void onSuccess(ProRecord result) {
                Log.i(Constant.TAG, "get ProUnlock RecordList success: recordBean = " + JSONObject.toJSONString(result));
                listAdapter.setData(result.records);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "get ProUnlock RecordList failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
