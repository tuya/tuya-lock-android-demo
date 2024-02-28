package com.tuya.lock.demo.ble.utils;

import android.content.Context;

import com.tuya.lock.demo.R;
import com.tuya.smart.optimus.lock.api.TuyaUnlockType;

public class OpModeUtils {

    public static String getTypeName(Context context, String dpCode) {
        String name = dpCode;
        if (dpCode.equals(TuyaUnlockType.FINGERPRINT)) {
            name = context.getString(R.string.mode_fingerprint);
        } else if (dpCode.equals(TuyaUnlockType.CARD)) {
            name = context.getString(R.string.mode_card);
        } else if (dpCode.equals(TuyaUnlockType.PASSWORD)) {
            name = context.getString(R.string.mode_password);
        } else if (dpCode.equals(TuyaUnlockType.VOICE_REMOTE)) {
            name = context.getString(R.string.mode_voice_password);
        }
        return name;
    }
}