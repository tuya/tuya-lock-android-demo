package com.tuya.lock.demo.ble.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.ble.adapter.DeviceListAdapter;
import com.tuya.smart.android.ble.builder.BleConnectBuilder;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.api.ITuyaHome;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.appsdk.sample.resource.HomeModel;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    public DeviceListAdapter adapter;
    private ITuyaDevice ITuyaDevice;
    private ITuyaHome ITuyaHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new DeviceListAdapter();
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != ITuyaDevice) {
            ITuyaDevice.onDestroy();
        }
        if (null != ITuyaHome) {
            ITuyaHome.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long homeId = HomeModel.getCurrentHome(this);
        if (null == ITuyaHome) {
            ITuyaHome = TuyaHomeSdk.newHomeInstance(homeId);
        }
        ITuyaHome.getHomeDetail(new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Log.d("HomeBean", "=====");
                List<BleConnectBuilder> builderList = new ArrayList<>();
                ArrayList<DeviceBean> lockList = new ArrayList<>();
                for (DeviceBean deviceBean : homeBean.getDeviceList()) {
                    Log.d("HomeBean", "devId = " + deviceBean.getDevId() + " / name = " + deviceBean.getName());
                    if (deviceBean.getProductBean().getCategory().contains("ms")) {
                        lockList.add(deviceBean);
                        if (null == ITuyaDevice) {
                            ITuyaDevice = TuyaHomeSdk.newDeviceInstance(deviceBean.devId);
                        }
                        ITuyaDevice.registerDevListener(iDevListener);
                        if (deviceBean.isBluetooth()) {
                            BleConnectBuilder builder = new BleConnectBuilder();
                            builder.setDevId(deviceBean.devId);
                            builderList.add(builder);
                        }
//                        if (deviceBean.getIsOnline()) {
//                            onSyncBatchData(deviceBean.devId);
//                        }
                    }
                }

                if (builderList.size() > 0) {
                    TuyaHomeSdk.getBleManager().connectBleDevice(builderList);
                }

                if (null != adapter) {
                    adapter.setData(lockList);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(DeviceListActivity.this,
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
//            if (online) {
//                onSyncBatchData(devId);
//            }
        }

        @Override
        public void onNetworkStatusChanged(String devId, boolean status) {

        }

        @Override
        public void onDevInfoUpdate(String devId) {

        }
    };

//    public void onSyncBatchData(String devId) {
//        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
//        tuyaLockManager.getBleLockV2(devId).publishSyncBatchData();
//    }
}