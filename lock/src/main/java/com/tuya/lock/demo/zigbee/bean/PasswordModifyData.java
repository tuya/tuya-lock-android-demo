package com.tuya.lock.demo.zigbee.bean;

import com.tuya.smart.sdk.optimus.lock.bean.ble.ScheduleBean;

import java.util.ArrayList;
import java.util.List;

public class PasswordModifyData {

    public String devId;
    public List<ScheduleBean> scheduleList = new ArrayList<>();
}
