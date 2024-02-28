package com.tuya.lock.demo.wifi.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.ble.activity.code.ShowCodeActivity;
import com.tuya.lock.demo.ble.utils.Utils;
import com.tuya.lock.demo.zigbee.utils.Constant;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.device.bean.SchemaBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.optimus.lock.api.bean.Record;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 告警记录adapter
 */
public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> {

    public List<Record.DataBean> data = new ArrayList<>();
    private DeviceBean deviceBean;

    public void setDevice(String devId) {
        deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId);
    }

    public final void setData(List<Record.DataBean> list) {
        this.data = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zigbee_lock_records, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record.DataBean itemData = data.get(position);
        if (null != itemData) {
            if (null == deviceBean || null == deviceBean.getSchemaMap()) {
                L.e(Constant.TAG, "deviceBean OR getSchemaMap is null");
                return;
            }
            for (Map.Entry<String, SchemaBean> schemaBean : deviceBean.getSchemaMap().entrySet()) {
                if (TextUtils.equals(schemaBean.getKey(), String.valueOf(itemData.dpId))) {
                    SchemaBean schemaItem = schemaBean.getValue();
                    String recordTitle = itemData.userName + schemaItem.name + "(" + itemData.dpValue + ")";
                    if (itemData.tags == 1) {
                        recordTitle = "[hiJack]" + recordTitle;
                    }
                    holder.userNameView.setText(recordTitle);
                    break;
                }
            }
            holder.unlockTimeView.setText(Utils.getDateDay(itemData.createTime));
            holder.bindView.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                ShowCodeActivity.startActivity(v.getContext(), JSONObject.toJSONString(itemData));
            });

            if (!TextUtils.isEmpty(itemData.avatarUrl)) {
                Utils.showImageUrl(itemData.avatarUrl, holder.user_face);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userNameView;
        private final TextView unlockTimeView;
        private final Button bindView;
        private final ImageView user_face;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.userName);
            unlockTimeView = itemView.findViewById(R.id.unlockTime);
            user_face = itemView.findViewById(R.id.user_face);
            bindView = itemView.findViewById(R.id.bindView);
        }
    }
}