package com.tuya.smart.bizubundle.panel.demo.videolock.view

import com.tuya.tuyalock.videolock.bean.LatestLogBean
import com.tuya.tuyalock.videolock.bean.LogsListBean

/**
 * Create by blitzfeng on 7/5/22
 */
interface ILogView {

    fun onGetLog(logBean: LatestLogBean)

    fun onGetLogList(list:List<LogsListBean.LogsInfoBean>)

}