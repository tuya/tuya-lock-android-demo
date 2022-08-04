package com.tuya.smart.bizubundle.panel.demo.videolock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.tuyalock.videolock.bean.WifiLockUserBean

/**
 * Create by blitzfeng on 7/27/22
 */
class MemberAdapter : RecyclerView.Adapter<MemberAdapter.MemberHolder>() {
    var list: MutableList<WifiLockUserBean> = ArrayList()
    var onItemClickListener:OnItemClickListener? = null
    class MemberHolder : RecyclerView.ViewHolder {
        val mInfoTv: TextView

        constructor(itemView: View) : super(itemView) {
            mInfoTv = itemView.findViewById(R.id.tv_info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_member_item, parent, false)

        return MemberHolder(view)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        val wifiLockUserBean = list.get(position)
        holder.mInfoTv.setText(wifiLockUserBean.toString())
        holder.itemView.setOnClickListener {
            onItemClickListener?.onClick(wifiLockUserBean)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener{
        fun onClick(bean: WifiLockUserBean)
    }
}