package com.tuya.smart.bizubundle.panel.demo.videolock.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.tuyalock.videolock.bean.LogsListBean
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create by blitzfeng on 7/5/22
 */
class LogsAdapter : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {

    val list: MutableList<LogsListBean.LogsInfoBean> = ArrayList()
    var onChildItemClickListener: OnChildItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_log_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logDetailBean = list.get(position)
        holder.mTimeTv.text = Date(logDetailBean.time).toString()
        holder.mItemTv.text = logDetailBean.toString()
        if (logDetailBean.userId.equals("0") && logDetailBean.logCategory.equals("unlock_record")) {
            holder.mBindTv.visibility = View.VISIBLE
            holder.mBindTv.setOnClickListener {
                onChildItemClickListener?.onBindClick(logDetailBean)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTimeTv: TextView = itemView.findViewById(R.id.tv_time)
        val mItemTv: TextView = itemView.findViewById(R.id.tv_item)
        val mBindTv: TextView = itemView.findViewById(R.id.tv_bind)
    }

    interface OnChildItemClickListener {
        fun onBindClick(bean: LogsListBean.LogsInfoBean?)
    }
}