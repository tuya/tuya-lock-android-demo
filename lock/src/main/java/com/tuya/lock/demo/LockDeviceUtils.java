package com.tuya.lock.demo;

import android.content.Context;

import com.tuya.lock.demo.ble.activity.detail.BleLockDetailActivity;
import com.tuya.lock.demo.video.activity.VideoDeviceDetail;
import com.tuya.lock.demo.wifi.activity.WifiDeviceDetail;
import com.tuya.lock.demo.zigbee.activity.DeviceDetail;
import com.tuya.sdk.os.TuyaOSDevice;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.sdk.bean.DeviceBean;

/**
 * Created by HuiYao on 2024/1/16
 */
public class LockDeviceUtils {
    public static boolean check(Context context, String deviceId) {
        DeviceBean mDeviceBean = TuyaOSDevice.getDeviceBean(deviceId);
        String categoryCode = mDeviceBean.getCategoryCode();
        L.i("LockDeviceUtils", "device:" + deviceId + ", categoryCode:" + categoryCode);
        if (categoryCode.contains("jtmspro_2b_2") || categoryCode.contains("ble_ms")) {
            BleLockDetailActivity.startActivity(context, deviceId);
            return true;
        } else if (categoryCode.contains("jtmspro_4z_1") || categoryCode.contains("zig_ms")) {
            DeviceDetail.startActivity(context, deviceId);
            return true;
        } else if (categoryCode.contains("wf_jtms") || categoryCode.contains("wf_ms")) {
            WifiDeviceDetail.startActivity(context, deviceId);
            return true;
        } else if (categoryCode.contains("videolock_1w_1")) {
            VideoDeviceDetail.startActivity(context, deviceId);
            return true;
        }
        return false;
    }

}