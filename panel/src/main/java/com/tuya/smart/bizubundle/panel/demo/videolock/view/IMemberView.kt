package com.tuya.smart.bizubundle.panel.demo.videolock.view

import com.tuya.tuyalock.videolock.bean.WifiLockUserBean

/**
 * Create by blitzfeng on 7/27/22
 */
interface IMemberView {

    fun onGetList(list:ArrayList<WifiLockUserBean>?)
}