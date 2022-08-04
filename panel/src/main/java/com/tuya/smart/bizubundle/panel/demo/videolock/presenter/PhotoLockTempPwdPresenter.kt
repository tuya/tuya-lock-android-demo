package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import com.alibaba.fastjson.JSON
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.bizubundle.panel.demo.videolock.view.ITempPwdView
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.IPhotoLock
import com.tuya.tuyalock.videolock.bean.*
import com.tuya.tuyalock.videolock.enums.OfflinePasswordStatusEnum
import com.tuya.tuyalock.videolock.enums.OfflineTypeEnum

/**
 * Create by blitzfeng on 7/29/22
 */
class PhotoLockTempPwdPresenter(context: Context, devId: String?) :
    TempPasswordPresenter(context, devId) {
    val TAG = "PhotoLockTempPwdPresenter"
    val iPhotoLock: IPhotoLock?
    var num: Int = 1
    val pwdList: MutableList<String> = ArrayList()
    val offlinePwdList: MutableList<String> = ArrayList()

    init {
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        val iVideoLockManager = iTuyaLockManager?.newVideoLockManagerInstance(mDevId)
        iPhotoLock = iVideoLockManager?.photoLockManager
    }

    override fun createOnlineTempPwd() {
        val scheduleBean = ScheduleBean()
        scheduleBean.allDay = true
        scheduleBean.workingDay = 127
        val list:MutableList<ScheduleBean> = ArrayList()
        list.add(scheduleBean)

        iPhotoLock?.createOnlineTempPassword("1231231",
            "86",
            "",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
            0,
            "pwd${num++}",
            list,
            object : ITuyaResultCallback<OnlineTempPassword> {
                override fun onSuccess(result: OnlineTempPassword?) {
                    ToastUtil.shortToast(mContext, "Create Success")
                    result?.let {
                        pwdList.add(it.pwdId)
                    }

                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun updateOnlinePwdName() {
        iPhotoLock?.updateTempPasswordName(
            pwdList.get(0).toString(),
            "testt${num}",
            object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    ToastUtil.shortToast(mContext, error)
                }

                override fun onSuccess() {
                    ToastUtil.shortToast(mContext, "Update Success")
                }

            })
    }

    override fun updateOnlinePwd() {
        ToastUtil.shortToast(mContext, "This lock not support")
    }

    override fun deleteOnlinePwd() {
        iPhotoLock?.deleteTempPassword(pwdList.get(0).toString(), object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                ToastUtil.shortToast(mContext, error)
                pwdList.removeAt(0)
            }

            override fun onSuccess() {
                ToastUtil.shortToast(mContext, "Delete Success")
            }
        })
    }

    override fun getOnlinePwdList() {
        iPhotoLock?.getOnlineTempPasswordList(object :
            ITuyaResultCallback<ArrayList<OnlineTempPasswordListBean>> {
            override fun onSuccess(result: ArrayList<OnlineTempPasswordListBean>?) {
                ToastUtil.shortToast(mContext, "Get List Success")
                L.d(TAG, result?.toString())
                pwdList.clear()
                result?.let {
                    for ( bean in it)
                        pwdList.add(bean.id.toString())
                }

            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext, errorMessage)
            }

        })
    }

    override fun getOfflineTempPwd() {
        iPhotoLock?.getOfflinePassword(System.currentTimeMillis()/1000,
            System.currentTimeMillis()/1000 + 24 * 60 * 60 ,
            OfflineTypeEnum.MULTIPLE,
            "",
            "offline pwd${num++}",
            "",
            object : ITuyaResultCallback<OfflineTempPasswordBean> {
                override fun onSuccess(result: OfflineTempPasswordBean?) {
                    ToastUtil.shortToast(mContext, "Get Pwd Success")
                    result?.let {
                        offlinePwdList.add(it.pwdId)
                    }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun updateOfflineTempPwd() {
        iPhotoLock?.setOfflinePasswordName(offlinePwdList.get(0),
            "offline update ${num}",
            "",
            object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    ToastUtil.shortToast(mContext, error)
                }

                override fun onSuccess() {
                    ToastUtil.shortToast(mContext, "Update Pwd Success")
                }

            })
    }

    override fun getOfflineTempPwdList() {
        iPhotoLock?.getOfflinePasswordList(OfflineTypeEnum.MULTIPLE,
            0,
            40,
            OfflinePasswordStatusEnum.TO_BE_USED,
            object : ITuyaResultCallback<ArrayList<OfflinePasswordListBean>> {
                override fun onSuccess(result: ArrayList<OfflinePasswordListBean>?) {
                    L.d(TAG, result?.toString())
                    ToastUtil.shortToast(mContext, "Get Pwd Success")
                    offlinePwdList.clear()
                    result?.let {
                        for ( bean in it)
                            offlinePwdList.add(bean.pwdId)
                    }

                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun getOfflineClearCode() {
        iPhotoLock?.getClearCode(offlinePwdList.get(0),
            object : ITuyaResultCallback<OfflineTempPasswordBean> {
                override fun onSuccess(result: OfflineTempPasswordBean?) {
                    ToastUtil.shortToast(mContext, " get clear code:${result?.pwd}")
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun getNoLimitOfflinePwd() {
        iPhotoLock?.getNoLimitOfflinePassword(object :
            ITuyaResultCallback<ArrayList<OfflineTempPasswordBean>> {
            override fun onSuccess(result: ArrayList<OfflineTempPasswordBean>?) {
                ToastUtil.shortToast(mContext, "password:${result?.toString()}")
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext, errorMessage)
            }

        })
    }

}