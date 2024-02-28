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

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thing.smart.sweeper.SweeperActivity;
import com.thingclips.sdk.core.PluginManager;
import com.thingclips.smart.dp.parser.api.ISwitch;
import com.thingclips.smart.interior.api.IAppDpParserPlugin;
import com.thingclips.smart.interior.api.IThingDevicePlugin;
import com.tuya.appsdk.sample.device.mgt.IconFontUtil;
import com.tuya.appsdk.sample.device.mgt.SimpleDevice;
import com.tuya.appsdk.sample.device.mgt.SimpleDp;
import com.tuya.appsdk.sample.device.mgt.SimpleSwitch;
import com.tuya.lock.demo.LockDeviceUtils;
import com.tuya.appsdk.sample.device.mgt.R;
import com.tuya.appsdk.sample.device.mgt.control.activity.DeviceMgtControlActivity;
import com.tuya.appsdk.sample.device.mgt.list.activity.DeviceSubZigbeeActivity;
import com.tuya.smart.android.demo.camera.CameraUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Device list adapter
 *
 * @author chuanfeng <a href="mailto:developer@tuya.com"/>
 * @since 2021/2/21 10:06 AM
 */
public final class DeviceMgtAdapter extends RecyclerView.Adapter<DeviceMgtAdapter.ViewHolder> {

    int type;

    private final Typeface mTypeface;
    private final AsyncListDiffer<SimpleDevice> mDiffer;

    private final SwitchClickListener mSwitchClickListener = position -> {
        IThingDevicePlugin plugin = PluginManager.service(IThingDevicePlugin.class);
        SimpleDevice device = getItem(position);
        if (device == null)
            return;
        try {
            IAppDpParserPlugin parserPlugin = PluginManager.service(IAppDpParserPlugin.class);
            ISwitch iSwitch = parserPlugin.getParser(device.getDevId()).getSwitchDp();
            plugin.newDeviceInstance(device.getDevId()).publishDps(iSwitch.getCommands(!device.getSimpleSwitch().isSwitchOn()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public DeviceMgtAdapter(Context context) {
        DiffUtil.ItemCallback<SimpleDevice> itemCallback = new DiffUtil.ItemCallback<SimpleDevice>() {
            @Override
            public boolean areItemsTheSame(@NonNull SimpleDevice oldItem, @NonNull SimpleDevice newItem) {
                return TextUtils.equals(oldItem.getDevId(), newItem.getDevId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SimpleDevice oldItem, @NonNull SimpleDevice newItem) {
                return oldItem.sameContent(newItem);
            }
        };
        mDiffer = new AsyncListDiffer<>(this, itemCallback);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
    }

    public SimpleDevice getItem(int position) {
        try {
            return mDiffer.getCurrentList().get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    public List<SimpleDevice> getData() {
        return mDiffer.getCurrentList();
    }

    public void setData(ArrayList<SimpleDevice> list, int type) {
        this.type = type;
        mDiffer.submitList(list);
    }

    public void itemClick(View v, int position) {
        SimpleDevice deviceBean = mDiffer.getCurrentList().get(position);
        if (CameraUtils.ipcProcess(v.getContext(), deviceBean.getDevId())) {
            return;
        }
        if (deviceBean.getCategory().contains("sd")) {
            Intent intent = new Intent(v.getContext(), SweeperActivity.class);
            intent.putExtra("deviceId", deviceBean.getDevId());
            v.getContext().startActivity(intent);
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
                intent.putExtra("deviceId", deviceBean.getDevId());
                v.getContext().startActivity(intent);
                break;
            case 2:
                // Navigate to zigBee sub device management
                Intent intent2 = new Intent(v.getContext(), DeviceSubZigbeeActivity.class);
                intent2.putExtra("deviceId", deviceBean.getDevId());
                v.getContext().startActivity(intent2);
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false), mSwitchClickListener);
        holder.itemView.setOnClickListener(v -> {
            RecyclerView recyclerView = (RecyclerView) v.getParent();
            itemClick(v, recyclerView.getChildAdapterPosition(v));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceMgtAdapter.ViewHolder holder, int position) {
        SimpleDevice bean = mDiffer.getCurrentList().get(position);
        Glide.with(holder.itemView).load(bean.getIcon()).into(holder.iconView);
        holder.deviceName.setText(bean.getName());

        if (bean.isOnline()) {
            SimpleSwitch switchDp = bean.getSimpleSwitch();
            if (switchDp != null) {
                holder.switchView.setVisibility(View.VISIBLE);
                holder.switchView.setImageResource(switchDp.isSwitchOn() ? R.drawable.on : R.drawable.off);
            } else {
                holder.switchView.setVisibility(View.GONE);
            }
            List<SimpleDp> displays = bean.getDisplays();
            StringBuilder builder = new StringBuilder();
            for (SimpleDp dp : displays) {
                String content = dp.getStatus();
                if (!TextUtils.isEmpty(content)) {
                    builder.append(getIconFont(dp)).append(content).append(" ");
                }
            }
            if (builder.length() > 0) {
                holder.statusView.setVisibility(View.VISIBLE);
                holder.statusView.setTypeface(mTypeface);
            } else {
                holder.statusView.setVisibility(View.GONE);
                holder.statusView.setTypeface(null);
            }
            holder.statusView.setText(builder);
            List<SimpleDp> operable = bean.getOperates();
            if (operable.isEmpty()) {
                holder.mRecyclerView.setAdapter(null);
                holder.mRecyclerView.setVisibility(View.GONE);
                holder.mDevFunc.setVisibility(View.GONE);
            } else {
                OperableDpAdapter adapter = (OperableDpAdapter) holder.mRecyclerView.getAdapter();
                if (adapter == null) {
                    holder.mRecyclerView.setAdapter(new OperableDpAdapter(operable, mTypeface, v -> itemClick(v, holder.getAdapterPosition())));
                } else {
                    adapter.setOnClickListener(v -> itemClick(v, holder.getAdapterPosition()));
                    adapter.update(operable);
                }
                holder.mDevFunc.setVisibility(View.VISIBLE);
                holder.mRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            holder.statusView.setTypeface(null);
            holder.statusView.setText(R.string.device_mgt_offline);
            holder.mDevFunc.setVisibility(View.GONE);
            holder.statusView.setVisibility(View.VISIBLE);
            holder.switchView.setVisibility(View.GONE);
            holder.mRecyclerView.setVisibility(View.GONE);
        }
    }

    private CharSequence getIconFont(SimpleDp dp) {
        String iF = IconFontUtil.getIconFontContent(dp.getIconFont());
        if (TextUtils.isEmpty(iF))
            return "";
        return Html.fromHtml(iF);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    interface SwitchClickListener {
        void onClicked(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceName;
        private final TextView statusView;
        private final ImageView iconView;
        private final ImageView switchView;
        private final RecyclerView mRecyclerView;
        private final View mDevFunc;

        public ViewHolder(@NotNull View itemView, SwitchClickListener listener) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            statusView = itemView.findViewById(R.id.statusView);
            iconView = itemView.findViewById(R.id.iconView);
            switchView = itemView.findViewById(R.id.switchButton);
            mRecyclerView = itemView.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 4));
            mDevFunc = itemView.findViewById(R.id.devFuncView);
            switchView.setOnClickListener(v -> {
                listener.onClicked(getAdapterPosition());
            });
        }
    }
}