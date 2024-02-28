package com.tuya.lock.demo.zigbee.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tuya.lock.demo.R;
import com.tuya.smart.optimus.lock.api.zigbee.response.PasswordBean;


public class DialogPasswordUtils {

    public static void show(Context context, PasswordBean.DataBean bean, Callback callback) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("选择功能");
        View layout = LayoutInflater.from(context).inflate(R.layout.zigbee_dialog_list, null);
        boolean isOneTime = bean.getOneTime() == 1;

        TextView editView = layout.findViewById(R.id.edit_view);
        editView.setOnClickListener(v -> {
            if (null != callback) {
                callback.edit(bean);
            }
            dialog.dismiss();
        });

        layout.findViewById(R.id.delete_view).setOnClickListener(v -> {
            if (null != callback) {
                callback.delete(bean);
            }
            dialog.dismiss();
        });

        TextView freezeView = layout.findViewById(R.id.freeze_view);
        if (isOneTime) {
            freezeView.setVisibility(View.GONE);
            layout.findViewById(R.id.freeze_view_line).setVisibility(View.GONE);
        } else {
            freezeView.setVisibility(View.VISIBLE);
            layout.findViewById(R.id.freeze_view_line).setVisibility(View.VISIBLE);
        }
        boolean isFreeze;
        if (bean.getPhase() == 3) {
            freezeView.setText("解冻");
            editView.setVisibility(View.GONE);
            isFreeze = false;
        } else {
            freezeView.setText("冻结");
            isFreeze = true;
            if (isOneTime) {
                editView.setVisibility(View.GONE);
            } else {
                editView.setVisibility(View.VISIBLE);
            }
        }
        freezeView.setOnClickListener(v -> {
            if (null != callback) {
                callback.freeze(bean, isFreeze);
            }
            dialog.dismiss();
        });

        layout.findViewById(R.id.rename_view).setOnClickListener(v -> {
            if (null != callback) {
                callback.rename(bean);
            }
            dialog.dismiss();
        });
        layout.findViewById(R.id.showCode_view).setOnClickListener(v -> {
            if (null != callback) {
                callback.showCode(bean);
            }
            dialog.dismiss();
        });
        dialog.setView(layout);
        dialog.show();
    }

    public interface Callback {
        void edit(PasswordBean.DataBean bean);

        void delete(PasswordBean.DataBean bean);

        void rename(PasswordBean.DataBean bean);

        /**
         * @param bean     数据
         * @param isFreeze 冻结或者解冻
         */
        void freeze(PasswordBean.DataBean bean, boolean isFreeze);

        void showCode(PasswordBean.DataBean bean);
    }
}
