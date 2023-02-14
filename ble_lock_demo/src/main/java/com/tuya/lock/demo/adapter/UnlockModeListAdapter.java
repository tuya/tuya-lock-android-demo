package com.tuya.lock.demo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.lockmode.UnlockModeEditActivity;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.UnlockMode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UnlockModeListAdapter extends RecyclerView.Adapter<UnlockModeListAdapter.ViewHolder> {

    public ArrayList<UnlockMode> data = new ArrayList<>();
    private IResultCallback<UnlockMode> callback;
    private String mDevId;

    @NotNull
    public final ArrayList<UnlockMode> getData() {
        return this.data;
    }

    public void setDevId(String devId) {
        mDevId = devId;
    }

    public final void setData(ArrayList<UnlockMode> list) {
        this.data = list;
    }

    public void delete(IResultCallback<UnlockMode> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public UnlockModeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.unlock_mode_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UnlockModeListAdapter.ViewHolder holder, int position) {
        UnlockMode bean = (UnlockMode) data.get(position);
        holder.tvDeviceName.setText(bean.unlockName);
        holder.tvStatus.setText(bean.unlockType);
        holder.user_delete.setOnClickListener(v -> {
            if (null != callback) {
                callback.onSuccess(bean);
            }
        });

        holder.user_detail.setOnClickListener(v -> {
            String unlockModeData = JSONObject.toJSONString(bean);
            Intent intent = new Intent(v.getContext(), UnlockModeEditActivity.class);
            intent.putExtra(Constant.UNLOCK_MODE, unlockModeData);
            intent.putExtra(Constant.DEVICE_ID, mDevId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeviceName;
        private final TextView tvStatus;
        private final Button user_delete;
        private final Button user_detail;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvStatus = itemView.findViewById(R.id.tvDeviceStatus);
            user_delete = itemView.findViewById(R.id.user_delete);
            user_detail = itemView.findViewById(R.id.user_detail);
        }
    }
}
