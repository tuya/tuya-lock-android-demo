package com.tuya.appsdk.sample.device.mgt;

import android.text.TextUtils;

/**
 * create by dongdaqing[mibo] 2023/9/19 11:47
 */
public class SimpleDp {
    private String devId;
    private String dpId;
    private String iconFont;
    private String status;
    private String dpName;
    private String type;

    public boolean same(SimpleDp other) {
        return TextUtils.equals(dpId, other.dpId)
                && TextUtils.equals(devId, other.devId)
                && TextUtils.equals(iconFont, other.iconFont)
                && TextUtils.equals(status, other.status)
                && TextUtils.equals(dpName, other.dpName);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDpId() {
        return dpId;
    }

    public void setDpId(String dpId) {
        this.dpId = dpId;
    }

    public String getIconFont() {
        return iconFont;
    }

    public void setIconFont(String iconFont) {
        this.iconFont = iconFont;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDpName() {
        return dpName;
    }

    public void setDpName(String dpName) {
        this.dpName = dpName;
    }
}
