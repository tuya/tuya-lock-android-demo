package com.tuya.lock.demo.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tuya.appsdk.sample.resource.HomeModel;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.LockListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.android.ble.builder.BleConnectBuilder;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHome;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class LockListActivity extends AppCompatActivity {

    public LockListAdapter adapter;
    private ITuyaDevice iTuyaDevice;
    private ITuyaHome iTuyaHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new LockListAdapter();
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != iTuyaDevice) {
            iTuyaDevice.onDestroy();
        }
        if (null != iTuyaHome) {
            iTuyaHome.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long homeId = HomeModel.getCurrentHome(this);
        if (null == iTuyaHome) {
            iTuyaHome = TuyaHomeSdk.newHomeInstance(homeId);
        }
        iTuyaHome.getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Log.d("HomeBean", "=====");
                List<BleConnectBuilder> builderList = new ArrayList<>();
                ArrayList<DeviceBean> lockList = new ArrayList<>();
                for (DeviceBean deviceBean : homeBean.getDeviceList()) {
                    Log.d("HomeBean", "devId = " + deviceBean.getDevId() + " / name = " + deviceBean.getName());
                    if (deviceBean.getProductBean().getCategory().contains("ms")) {
                        lockList.add(deviceBean);
                        if (null == iTuyaDevice) {
                            iTuyaDevice = TuyaHomeSdk.newDeviceInstance(deviceBean.devId);
                        }
                        iTuyaDevice.registerDevListener(iDevListener);
                        if (deviceBean.isBluetooth()) {
                            BleConnectBuilder builder = new BleConnectBuilder();
                            builder.setDevId(deviceBean.devId);
                            builderList.add(builder);
                        }
                        if (deviceBean.getIsOnline()) {
                            onSyncBatchData(deviceBean.devId);
                        }
                    }
                }

                if (builderList.size() > 0) {
                    TuyaHomeSdk.getBleManager().connectBleDevice(builderList);
                }

                //如果没有设备，模拟一个验证
                if (lockList.size() == 0) {
                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.setDevId("vioso123osd213oo1");
                    deviceBean.setName("门锁demo");
                    deviceBean.setIsOnline(true);
                    lockList.add(deviceBean);
                }
                if (null != adapter) {
                    adapter.setData(lockList);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(LockListActivity.this,
                        "Activate error-->" + errorMsg,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private final IDevListener iDevListener = new IDevListener() {
        @Override
        public void onDpUpdate(String devId, String dpStr) {

        }

        @Override
        public void onRemoved(String devId) {

        }

        @Override
        public void onStatusChanged(String devId, boolean online) {
            if (adapter != null && adapter.data != null && adapter.data.size() > 0) {
                for (DeviceBean item : adapter.data) {
                    item.setIsOnline(online);
                    adapter.notifyDataSetChanged();
                }
            }
            if (online) {
                onSyncBatchData(devId);
            }
        }

        @Override
        public void onNetworkStatusChanged(String devId, boolean status) {

        }

        @Override
        public void onDevInfoUpdate(String devId) {

        }
    };

    public void onSyncBatchData(String devId) {
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockManager.getBleLockV2(devId).onSyncBatchData();
    }
}