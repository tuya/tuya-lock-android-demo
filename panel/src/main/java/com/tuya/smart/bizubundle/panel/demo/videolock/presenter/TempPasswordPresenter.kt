package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.tuyalock.videolock.api.IPhotoLock

/**
 * Create by blitzfeng on 7/29/22
 */
 abstract class TempPasswordPresenter {

     val mDevId: String?
     val mContext: Context

    constructor(context: Context, devId: String?) {
        mContext = context
        mDevId = devId

    }
    abstract fun createOnlineTempPwd()

    abstract fun updateOnlinePwdName()

    abstract fun updateOnlinePwd()

    abstract fun deleteOnlinePwd()

    abstract fun getOnlinePwdList()

    abstract fun getOfflineTempPwd()

    abstract fun updateOfflineTempPwd()

    abstract fun getOfflineTempPwdList()

    abstract fun getOfflineClearCode()

    abstract fun getNoLimitOfflinePwd()

}