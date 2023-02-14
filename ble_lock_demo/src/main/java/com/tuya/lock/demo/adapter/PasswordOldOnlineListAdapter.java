package com.tuya.lock.demo.adapter;

import android.util.Log;
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
import com.tuya.lock.demo.password.PasswordOldOnlineDetailActivity;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.TempPasswordBeanV3;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PasswordOldOnlineListAdapter extends RecyclerView.Adapter<PasswordOldOnlineListAdapter.ViewHolder> {

    public ArrayList<TempPasswordBeanV3> data = new ArrayList<>();
    private IResultCallback<TempPasswordBeanV3> callback;
    private String mDevId;

    @NotNull
    public final ArrayList<TempPasswordBeanV3> getData() {
        return this.data;
    }

    public void setDevId(String devId) {
        mDevId = devId;
    }

    public final void setData(ArrayList<TempPasswordBeanV3> list) {
        this.data = list;
    }

    public void delete(IResultCallback<TempPasswordBeanV3> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.password_old_online_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TempPasswordBeanV3 bean = (TempPasswordBeanV3) data.get(position);

        Log.i(Constant.TAG, JSONObject.toJSONString(bean));

        holder.tvDeviceName.setText(bean.name);
        holder.tvDeviceStatus.setText(String.valueOf(bean.passwordId));

        holder.button_delete.setOnClickListener(v -> callback.onSuccess(bean));

        holder.button_edit.setOnClickListener(v -> {
            PasswordOldOnlineDetailActivity.startActivity(v.getContext(), bean, mDevId, 1);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeviceName;
        private final TextView tvDeviceStatus;
        private final Button button_edit;
        private final Button button_delete;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceStatus = itemView.findViewById(R.id.tvDeviceStatus);
            button_edit = itemView.findViewById(R.id.button_edit);
            button_delete = itemView.findViewById(R.id.button_delete);
        }
    }
}
