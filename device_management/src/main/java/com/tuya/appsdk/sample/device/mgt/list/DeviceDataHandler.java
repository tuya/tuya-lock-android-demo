package com.tuya.appsdk.sample.device.mgt.list;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.thingclips.sdk.core.PluginManager;
import com.thingclips.smart.dp.parser.api.IDeviceDpParser;
import com.thingclips.smart.dp.parser.api.IDpParser;
import com.thingclips.smart.dp.parser.api.ISwitch;
import com.thingclips.smart.interior.api.IAppDpParserPlugin;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.tuya.appsdk.sample.device.mgt.SimpleDevice;
import com.tuya.appsdk.sample.device.mgt.SimpleDp;
import com.tuya.appsdk.sample.device.mgt.SimpleSwitch;
import com.tuya.appsdk.sample.device.mgt.list.adapter.DeviceMgtAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create by dongdaqing[mibo] 2023/9/20 13:55
 */
public class DeviceDataHandler implements LifecycleEventObserver {

    private final ExecutorService mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final IAppDpParserPlugin mPlugin;
    private final DeviceMgtAdapter mAdapter;

    public DeviceDataHandler(LifecycleOwner lifecycleOwner, DeviceMgtAdapter adapter) {
        lifecycleOwner.getLifecycle().addObserver(this);
        mExecutor = Executors.newSingleThreadExecutor();
        mPlugin = PluginManager.service(IAppDpParserPlugin.class);
        mAdapter = adapter;
    }

    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    public void updateAdapter(List<DeviceBean> list, int type) {
        ArrayList<SimpleDevice> data = new ArrayList<>();
        for (DeviceBean deviceBean : list) {
            IDeviceDpParser parser = mPlugin.update(deviceBean);
            SimpleDevice simpleDevice = new SimpleDevice();
            simpleDevice.setDevId(deviceBean.devId);
            simpleDevice.setOnline(deviceBean.getIsOnline());
            simpleDevice.setCategory(deviceBean.getProductBean().getCategory());
            simpleDevice.setIcon(deviceBean.getIconUrl());
            simpleDevice.setName(deviceBean.getName());
            simpleDevice.setDisplays(convert(deviceBean.devId, parser.getDisplayDp(), true));
            ISwitch switcher = parser.getSwitchDp();
            if (switcher != null) {
                SimpleSwitch simpleSwitch = new SimpleSwitch();
                simpleSwitch.setSwitchOn(switcher.getSwitchStatus() == ISwitch.SWITCH_STATUS_ON);
                simpleDevice.setSimpleSwitch(simpleSwitch);
            }
            simpleDevice.setOperates(convert(deviceBean.devId, parser.getOperableDp(), false));
            data.add(simpleDevice);
        }

        mHandler.post(() -> mAdapter.setData(data, type));
    }

    private List<SimpleDp> convert(String devId, List<IDpParser<Object>> list, boolean display) {
        ArrayList<SimpleDp> data = new ArrayList<>();
        for (IDpParser<Object> dp : list) {
            SimpleDp simpleDp = new SimpleDp();
            simpleDp.setDpId(dp.getDpId());
            simpleDp.setType(dp.getType());
            simpleDp.setDevId(devId);
            simpleDp.setIconFont(dp.getIconFont());
            if (display) {
                simpleDp.setStatus(dp.getDisplayStatus());
            } else {
                simpleDp.setStatus(dp.getDisplayStatusForQuickOp());
                simpleDp.setDpName(dp.getDisplayTitle());
            }
            data.add(simpleDp);
        }
        return data;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            mExecutor.shutdownNow();
        }
    }
}
