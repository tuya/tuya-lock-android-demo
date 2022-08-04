package com.tuya.smart.bizubundle.panel.demo.videolock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.adapter.LogsAdapter
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.LogsPresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.view.ILogView
import com.tuya.tuyalock.videolock.bean.LatestLogBean
import com.tuya.tuyalock.videolock.bean.LogsListBean

class LogsActivity : AppCompatActivity(),ILogView ,View.OnClickListener{
    private var mDevId:String? = null
    private lateinit var mLatestLogBtn:Button
    private lateinit var mLogsListBtn:Button
    private lateinit var mLogsRv:RecyclerView
    private lateinit var mShowNewLogTv:TextView

    private lateinit var mAdapter:LogsAdapter
    private lateinit var mPresenter:LogsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        initView()
        initData()
    }

    private fun initView(){
        mLatestLogBtn = findViewById(R.id.btn_latest_log)
        mLatestLogBtn.setOnClickListener(this)
        mLogsListBtn = findViewById(R.id.btn_log_list)
        mLogsListBtn.setOnClickListener(this)
        mLogsRv = findViewById(R.id.rv_logs_list)
        mShowNewLogTv = findViewById(R.id.tv_show_new_log)

        mAdapter = LogsAdapter()
        mAdapter.onChildItemClickListener = object :LogsAdapter.OnChildItemClickListener{
            override fun onBindClick(bean: LogsListBean.LogsInfoBean?) {
                mPresenter.bindToUser(true,bean)
            }

        }
        mLogsRv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mLogsRv.adapter = mAdapter


    }

    private fun initData(){
        mDevId = intent.getStringExtra("devId");
        mPresenter = LogsPresenter(mDevId,this,this)

    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_latest_log -> {
                mPresenter.getLatestLog(true)
            }
            R.id.btn_log_list -> {
                mPresenter.getLogsList(true)
            }
        }
    }

    override fun onGetLog(logBean: LatestLogBean) {
        mShowNewLogTv.text = logBean.toString()
    }

    override fun onGetLogList(list: List<LogsListBean.LogsInfoBean>) {
        mAdapter.list.clear()
        mAdapter.list.addAll(list)
        mAdapter.notifyDataSetChanged()
    }
}