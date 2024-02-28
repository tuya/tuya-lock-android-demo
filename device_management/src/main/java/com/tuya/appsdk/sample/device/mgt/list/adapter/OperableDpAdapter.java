package com.tuya.appsdk.sample.device.mgt.list.adapter;

import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thingclips.sdk.core.PluginManager;
import com.thingclips.smart.dp.parser.api.IDpParser;
import com.thingclips.smart.interior.api.IAppDpParserPlugin;
import com.thingclips.smart.interior.api.IThingDevicePlugin;
import com.tuya.appsdk.sample.device.mgt.IconFontUtil;
import com.tuya.appsdk.sample.device.mgt.R;
import com.tuya.appsdk.sample.device.mgt.SimpleDp;

import java.util.List;

/**
 * create by dongdaqing[mibo] 2023/9/18 17:55
 */
public class OperableDpAdapter extends RecyclerView.Adapter<OperableDpAdapter.DpViewHolder> {

    private final Typeface mTypeface;
    private final AsyncListDiffer<SimpleDp> mDiffer;
    private View.OnClickListener mOnClickListener;

    private final DiffUtil.ItemCallback<SimpleDp> mCallback = new DiffUtil.ItemCallback<SimpleDp>() {
        @Override
        public boolean areItemsTheSame(@NonNull SimpleDp oldItem, @NonNull SimpleDp newItem) {
            return TextUtils.equals(oldItem.getDpId(), newItem.getDpId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SimpleDp oldItem, @NonNull SimpleDp newItem) {
            return oldItem.same(newItem);
        }
    };

    public OperableDpAdapter(List<SimpleDp> list, Typeface typeface, View.OnClickListener listener) {
        mDiffer = new AsyncListDiffer<>(this, mCallback);
        mDiffer.submitList(list);
        mTypeface = typeface;
        mOnClickListener = listener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void update(List<SimpleDp> list) {
        mDiffer.submitList(list);
    }

    @NonNull
    @Override
    public DpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DpViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_mgt_dp_item, parent, false), mTypeface);
    }

    @Override
    public void onBindViewHolder(@NonNull DpViewHolder holder, int position) {
        SimpleDp dp = mDiffer.getCurrentList().get(position);
        if ("bool".equals(dp.getType())) {
            holder.itemView.setOnClickListener(v -> {
                RecyclerView recyclerView = (RecyclerView) v.getParent();
                int p = recyclerView.getChildAdapterPosition(v);
                IThingDevicePlugin plugin = PluginManager.service(IThingDevicePlugin.class);
                SimpleDp device = mDiffer.getCurrentList().get(p);
                if (device == null)
                    return;
                try {
                    IAppDpParserPlugin parserPlugin = PluginManager.service(IAppDpParserPlugin.class);
                    List<IDpParser<Object>> list = parserPlugin.getParser(device.getDevId()).getOperableDp();
                    for (IDpParser<Object> parser : list) {
                        if (parser.getDpId().equals(device.getDpId())) {
                            plugin.newDeviceInstance(device.getDevId()).publishDps(parser.getCommands(!(Boolean) parser.getValue()), null);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            holder.itemView.setOnClickListener(v -> mOnClickListener.onClick(v));
        }
        holder.iconView.setTypeface(mTypeface);
        holder.iconView.setText(getIconFont(dp));
        holder.titleView.setText(dp.getDpName());
        holder.statusView.setText(dp.getStatus());
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

    public static class DpViewHolder extends RecyclerView.ViewHolder {
        private final TextView iconView;
        private final TextView titleView;
        private final TextView statusView;

        public DpViewHolder(@NonNull View itemView, Typeface typeface) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconView);
            titleView = itemView.findViewById(R.id.titleView);
            statusView = itemView.findViewById(R.id.statusView);
        }
    }
}
