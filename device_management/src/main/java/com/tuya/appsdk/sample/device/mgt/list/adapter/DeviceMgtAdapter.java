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

package com.tuya.appsdk.sample.device.mgt.list.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.appsdk.sample.device.mgt.R;
import com.tuya.appsdk.sample.device.mgt.control.activity.DeviceMgtControlActivity;
import com.tuya.appsdk.sample.device.mgt.list.activity.DeviceSubZigbeeActivity;
import com.tuya.lock.demo.LockDeviceUtils;
import com.tuya.smart.android.demo.camera.CameraUtils;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Device list adapter
 *
 * @author chuanfeng <a href="mailto:developer@tuya.com"/>
 * @since 2021/2/21 10:06 AM
 */
public final class DeviceMgtAdapter extends RecyclerView.Adapter<DeviceMgtAdapter.ViewHolder> {

    public ArrayList<DeviceBean> data = new ArrayList<>();
    int type;
    private Activity mActivity;

    @NotNull
    public final ArrayList<DeviceBean> getData() {
        return this.data;
    }

    public final void setData(Activity activity, ArrayList<DeviceBean> list, int type) {
        this.data = list;
        this.type = type;
        mActivity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_mgt_item, parent, false));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String devId = DeviceMgtAdapter.this.getData().get(holder.getAdapterPosition()).getDevId();
                ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(devId);
                mDevice.removeDevice(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Log.e("removeDevice", "code:" + code + ", error:" + error);
                        mDevice.onDestroy();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
                        mDevice.onDestroy();
                    }
                });
                return false;
            }
        });
        holder.itemView.setOnClickListener(v -> {
            DeviceBean deviceBean = DeviceMgtAdapter.this.getData().get(holder.getAdapterPosition());
            if (CameraUtils.ipcProcess(v.getContext(), deviceBean.getDevId())) {
                return;
            }
            if (LockDeviceUtils.check(v.getContext(), deviceBean.getDevId())) {
                return;
            }
            switch (type) {
                case 1:
                case 3:
                    // Navigate to device management
                    Intent intent = new Intent(v.getContext(), DeviceMgtControlActivity.class);
                    intent.putExtra("deviceId", ((DeviceBean) DeviceMgtAdapter.this.getData().get(holder.getAdapterPosition())).getDevId());
                    v.getContext().startActivity(intent);
                    break;
                case 2:
                    // Navigate to zigBee sub device management
                    Intent intent2 = new Intent(v.getContext(), DeviceSubZigbeeActivity.class);
                    intent2.putExtra("deviceId", ((DeviceBean) DeviceMgtAdapter.this.getData().get(holder.getAdapterPosition())).getDevId());
                    v.getContext().startActivity(intent2);
                    break;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceBean bean = (DeviceBean) data.get(position);
        holder.tvDeviceName.setText(bean.name);
        holder.tvStatus.setText(holder.itemView.getContext().getString(bean.getIsOnline() ? R.string.device_mgt_online : R.string.device_mgt_offline));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeviceName;
        private final TextView tvStatus;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvStatus = itemView.findViewById(R.id.tvDeviceStatus);
        }
    }

}
