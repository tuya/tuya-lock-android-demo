package com.tuya.appsdk.sample.device.mgt;

import android.text.TextUtils;

import java.util.List;

/**
 * create by dongdaqing[mibo] 2023/9/19 10:52
 */
public class SimpleDevice {
    private String devId;
    private String icon;
    private String name;
    private boolean online;
    private String category;
    private List<SimpleDp> displays;
    private List<SimpleDp> operates;
    private SimpleSwitch simpleSwitch;

    public boolean sameContent(SimpleDevice other) {
        return TextUtils.equals(icon, other.icon)
                && TextUtils.equals(name, other.name)
                && TextUtils.equals(category, other.category)
                && online == other.online
                && same(other.simpleSwitch)
                && same(displays, other.displays)
                && same(operates, other.operates);
    }

    private boolean same(SimpleSwitch other) {
        if (other == null && simpleSwitch == null) {
            return true;
        }

        if (other == null || simpleSwitch == null)
            return false;

        return other.isSwitchOn() == simpleSwitch.isSwitchOn();
    }

    private boolean same(List<SimpleDp> a, List<SimpleDp> b) {
        if (a.size() != b.size())
            return false;

        for (int i = 0; i < a.size(); i++) {
            SimpleDp ax = a.get(i);
            SimpleDp bx = b.get(i);
            if (!ax.same(bx))
                return false;
        }
        return true;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public SimpleSwitch getSimpleSwitch() {
        return simpleSwitch;
    }

    public void setSimpleSwitch(SimpleSwitch simpleSwitch) {
        this.simpleSwitch = simpleSwitch;
    }

    public List<SimpleDp> getDisplays() {
        return displays;
    }

    public void setDisplays(List<SimpleDp> displays) {
        this.displays = displays;
    }

    public List<SimpleDp> getOperates() {
        return operates;
    }

    public void setOperates(List<SimpleDp> operates) {
        this.operates = operates;
    }
}
