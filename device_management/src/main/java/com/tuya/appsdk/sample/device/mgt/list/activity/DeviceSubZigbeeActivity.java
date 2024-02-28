/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NO
 */
package com.tuya.appsdk.sample.device.mgt.list.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IThingDataCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.tuya.appsdk.sample.device.mgt.R;
import com.tuya.appsdk.sample.device.mgt.list.DeviceDataHandler;
import com.tuya.appsdk.sample.device.mgt.list.adapter.DeviceMgtAdapter;
import com.tuya.appsdk.sample.device.mgt.list.tag.DeviceListTypePage;

import java.util.List;

/**
 * Device Management initial device data sample
 *
 * @author yueguang <a href="mailto:developer@tuya.com"/>
 * @since 2021/3/4 4:57 PM
 */
public class DeviceSubZigbeeActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private RecyclerView rvList;
    DeviceMgtAdapter deviceMgtAdapter;

    private DeviceDataHandler mDataHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_mgt_activity_list);
        initView();
    }

    private void initView() {
        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topAppBar.setTitle(R.string.device_zb_sub_device_list);

        rvList = (RecyclerView) findViewById(R.id.rvList);

        deviceMgtAdapter = new DeviceMgtAdapter(this);
        rvList.setAdapter(deviceMgtAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false
        ));

        mDataHandler = new DeviceDataHandler(this, deviceMgtAdapter);
        getZbSubDeviceList();

    }

    // Get Sub-devices
    private void getZbSubDeviceList() {
        String deviceId = getIntent().getStringExtra("deviceId");

        ThingHomeSdk.newGatewayInstance(deviceId)
                .getSubDevList(new IThingDataCallback<List<DeviceBean>>() {
                    @Override
                    public void onSuccess(List<DeviceBean> result) {
                        mDataHandler.execute(() -> mDataHandler.updateAdapter(result, DeviceListTypePage.ZIGBEE_SUB_DEVICE_LIST));
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        Toast.makeText(DeviceSubZigbeeActivity.this,
                                "Get Sub-devices error" + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}