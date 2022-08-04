package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.IPhotoLock
import com.tuya.tuyalock.videolock.api.IVideoLock
import com.tuya.tuyalock.videolock.bean.OfflineTempPasswordBean
import com.tuya.tuyalock.videolock.bean.ScheduleBean
import com.tuya.tuyalock.videolock.bean.VideoLockTempPasswordBean
import com.tuya.tuyalock.videolock.enums.OfflineTypeEnum
import org.json.JSONObject

/**
 * Create by blitzfeng on 7/29/22
 */
class VideoLockTempPwdPresenter(context: Context, devId: String?) :
    TempPasswordPresenter(context, devId) {

    val TAG = "VideoLockTempPwdPresenter"
    val iVideoLock: IVideoLock?
    var num: Int = 1
    val pwdList: MutableList<String> = ArrayList()
    val offlinePwdList: MutableList<String> = ArrayList()

    init {
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        val iVideoLockManager = iTuyaLockManager?.newVideoLockManagerInstance(mDevId)
        iVideoLock = iVideoLockManager?.videoLockManager
    }

    override fun createOnlineTempPwd() {
        iVideoLock?.getDeviceSN("69", object : ITuyaResultCallback<Int> {
            override fun onSuccess(result: Int?) {
                result?.let {
                    realCreateOnlinePwd(it)
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext, errorMessage)
            }

        })


    }

    private fun realCreateOnlinePwd(sn: Int) {
        val scheduleBean = ScheduleBean()
        scheduleBean.allDay = true
        scheduleBean.workingDay = 127
        val list: MutableList<ScheduleBean> = ArrayList()
        list.add(scheduleBean)
        iVideoLock?.createOnlineTempPassword(sn,
            "123124",
            System.currentTimeMillis()/1000 ,
            System.currentTimeMillis()/1000 + 24 * 60 * 60,
            0,
            "online pwd${num++}",
            list,
            object : ITuyaResultCallback<String> {
                override fun onSuccess(result: String?) {
                    ToastUtil.shortToast(mContext, "pwd :${result}")
                    result?.let {
                        val jsonObject = JSONObject(it)
                        pwdList.add(jsonObject.getString("unlockBindingId"))
                    }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun updateOnlinePwdName() {
        iVideoLock?.updateOnlineTempPasswordName(
            pwdList.get(0),
            "online update ${num}",
            object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    ToastUtil.shortToast(mContext, error)
                }

                override fun onSuccess() {
                    ToastUtil.shortToast(mContext, "update success ")
                }

            })
    }

    override fun updateOnlinePwd() {
        val scheduleBean = ScheduleBean()
        scheduleBean.allDay = true
        scheduleBean.workingDay = 127
        val list: MutableList<ScheduleBean> = ArrayList()
        list.add(scheduleBean)
        iVideoLock?.updateOnlineTempPassword(pwdList.get(0),"2223334",System.currentTimeMillis()/1000,
        System.currentTimeMillis()/1000+24*60*60,"online pwd update${num}",2,list,object :IResultCallback{
                override fun onError(code: String?, error: String?) {
                    ToastUtil.shortToast(mContext, error)
                }

                override fun onSuccess() {
                    ToastUtil.shortToast(mContext, "update success ")
                }

            })
    }

    override fun deleteOnlinePwd() {
        iVideoLock?.deleteOnlineTempPassword(pwdList.get(0), object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                ToastUtil.shortToast(mContext, error)
            }

            override fun onSuccess() {
                ToastUtil.shortToast(mContext, "success ")
            }
        })
    }

    override fun getOnlinePwdList() {
        iVideoLock?.getTempPasswordList(object :
            ITuyaResultCallback<ArrayList<VideoLockTempPasswordBean>> {
            override fun onSuccess(result: ArrayList<VideoLockTempPasswordBean>?) {
                ToastUtil.shortToast(mContext, result?.toString())

            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext, errorMessage)
            }

        })
    }

    override fun getOfflineTempPwd() {
        iVideoLock?.getOfflineTempPassword(System.currentTimeMillis() / 1000,
            System.currentTimeMillis() / 1000 + 48 * 60 * 60,
            OfflineTypeEnum.MULTIPLE,
            "86",
            "offline pwd  test${num++}",
            "",
            object : ITuyaResultCallback<OfflineTempPasswordBean> {
                override fun onSuccess(result: OfflineTempPasswordBean?) {
                    ToastUtil.shortToast(mContext, result?.toString())
                    result?.let {
                        offlinePwdList.add(it.unlockBindingId)
                    }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun updateOfflineTempPwd() {
        iVideoLock?.setOfflineTempPasswordName(
            "Offline pwd tt ${num}",
            offlinePwdList.get(0),
            object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    ToastUtil.shortToast(mContext, error)
                }

                override fun onSuccess() {
                    ToastUtil.shortToast(mContext, "update success")
                }

            })
    }

    override fun getOfflineTempPwdList() {
        //same to Online password list
        getOnlinePwdList()
    }

    override fun getOfflineClearCode() {
        iVideoLock?.getClearCode(
            "clear code",
            offlinePwdList.get(0),
            object : ITuyaResultCallback<OfflineTempPasswordBean> {
                override fun onSuccess(result: OfflineTempPasswordBean?) {
                    ToastUtil.shortToast(mContext, result?.toString())
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext, errorMessage)
                }

            })
    }

    override fun getNoLimitOfflinePwd() {
        ToastUtil.shortToast(mContext, "This lock not support")
    }
}