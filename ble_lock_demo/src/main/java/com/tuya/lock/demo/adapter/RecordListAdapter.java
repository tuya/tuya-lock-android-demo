package com.tuya.lock.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.utils.Utils;
import com.tuya.smart.android.device.bean.SchemaBean;
import com.tuya.smart.camera.utils.L;
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
    public RecordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lock_records_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordListAdapter.ViewHolder holder, int position) {
        Record.DataBean itemData = data.get(position);
        if (null != itemData) {
//            try {
//                URL url = new URL(itemData.avatarUrl);
//                holder.userFace.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            if (null == deviceBean || null == deviceBean.getSchemaMap()) {
                L.e(Constant.TAG, "deviceBean OR getSchemaMap is null");
                return;
            }
            for (Map.Entry<String, SchemaBean> schemaBean : deviceBean.getSchemaMap().entrySet()) {
                if (schemaBean.getKey().equals(itemData.dpId)) {
                    SchemaBean schemaItem = schemaBean.getValue();
                    holder.userNameView.setText(schemaItem.name);
                    break;
                }
            }
            holder.unlockTimeView.setText(Utils.getDateDay(itemData.createTime));
            holder.unlockTypeView.setText(itemData.userName);
            if (itemData.tags == 1) {
                holder.unlockTagsView.setText("劫持");
            } else {
                holder.unlockTagsView.setText("");
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
        private final TextView unlockTypeView;
        private final TextView unlockTagsView;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.userName);
            unlockTimeView = itemView.findViewById(R.id.unlockTime);
            unlockTypeView = itemView.findViewById(R.id.unlockType);
            unlockTagsView = itemView.findViewById(R.id.unlockTags);
        }
    }
}
