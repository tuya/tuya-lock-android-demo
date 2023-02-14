package com.tuya.lock.demo.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.user.UserAddActivity;
import com.tuya.lock.demo.user.UserDetailActivity;
import com.tuya.lock.demo.utils.Utils;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.MemberInfoBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserMemberAdapter extends RecyclerView.Adapter<UserMemberAdapter.ViewHolder> {

    public ArrayList<MemberInfoBean> data = new ArrayList<>();
    private IResultCallback<MemberInfoBean> callback;
    private String mDevId;

    @NotNull
    public final ArrayList<MemberInfoBean> getData() {
        return this.data;
    }

    public void setDevId(String devId) {
        mDevId = devId;
    }

    public final void setData(ArrayList<MemberInfoBean> list) {
        this.data = list;
    }

    public void deleteUser(IResultCallback<MemberInfoBean> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public UserMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lock_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserMemberAdapter.ViewHolder holder, int position) {
        MemberInfoBean bean = (MemberInfoBean) data.get(position);
        holder.tvDeviceName.setText(bean.getNickName());
        String statusStr = bean.getTimeScheduleInfo().isPermanent() ? holder.itemView.getContext().getString(R.string.user_in_permanent) : null;
        if (null == statusStr) {
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText(statusStr);
            holder.tvStatus.setVisibility(View.VISIBLE);
        }

        holder.user_delete.setOnClickListener(v -> {
            if (null != callback) {
                callback.onSuccess(bean);
            }
        });

        holder.user_detail.setOnClickListener(v -> {
            UserAddActivity.startActivity(v.getContext(), bean, mDevId, 1);
        });

        holder.user_time.setOnClickListener(v ->{
            UserDetailActivity.startActivity(v.getContext(), bean, mDevId, 1);
        });

        if (!TextUtils.isEmpty(bean.getAvatarUrl())) {
            Utils.showImageUrl(bean.getAvatarUrl(), holder.user_face);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeviceName;
        private final TextView tvStatus;
        private final Button user_delete;
        private final ImageView user_face;
        private final Button user_detail;
        private final Button user_time;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            user_face = itemView.findViewById(R.id.user_face);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvStatus = itemView.findViewById(R.id.tvDeviceStatus);
            user_delete = itemView.findViewById(R.id.user_delete);
            user_detail = itemView.findViewById(R.id.user_detail);
            user_time = itemView.findViewById(R.id.user_time);
        }
    }
}
