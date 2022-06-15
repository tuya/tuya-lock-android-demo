package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import android.widget.Toast
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.mvp.presenter.BasePresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.view.ILockView
import com.tuya.smart.bizubundle.panel.demo.videolock.view.PhotoCountdownDialog
import com.tuya.smart.bizubundle.panel.demo.videolock.view.RealTimeVideoDialog
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.IIPCManager
import com.tuya.tuyalock.videolock.api.ILockReportListener
import com.tuya.tuyalock.videolock.api.IVideoLockManager
import com.tuya.tuyalock.videolock.bean.FileBucketInfoBean
import com.tuya.tuyalock.videolock.bean.RemoteMediaBean
import com.tuya.tuyalock.videolock.constants.DPCodes
import com.tuya.tuyalock.videolock.enums.FileTypeEnum

/**
 * Create by blitzfeng on 5/10/22
 */
class LockPresenter(var mContext: Context, var mDevId: String?, val iLockView: ILockView?) :
    BasePresenter(),
    ILockReportListener {
    private val TAG = "LockPresenter"
    private var iTuyaVideoLockManager: IVideoLockManager? = null
    var iIPCManager: IIPCManager? = null
    private var realTimeDialog: RealTimeVideoDialog? = null
    private var countdownDialog: PhotoCountdownDialog? = null
    private var tempDpCode: String? = null

    init {
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        iTuyaVideoLockManager = iTuyaLockManager.newVideoLockManagerInstance(mDevId)
        iTuyaVideoLockManager?.registerLockReportListener(this)
        iIPCManager = iTuyaVideoLockManager?.ipcManager
        L.i(TAG, "init LockPresenter")
    }


    fun remoteUnLock(isOpen: Boolean, confirm: Boolean) {
        iTuyaVideoLockManager?.remoteLock(isOpen, confirm, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(mContext, "publish success", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun requestLatestPicture() {
        iTuyaVideoLockManager?.getLatestPicture(FileTypeEnum.REMOTE_UNLOCK,
            object : ITuyaResultCallback<FileBucketInfoBean> {
                override fun onSuccess(result: FileBucketInfoBean?) {
                    L.i(TAG, "result filePath:${result?.toString()}")
                    result?.let {
                        requestPictureAndVideo(it.bucket, it.filePath, it.fileKey)
                    }

                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun requestPictureAndVideo(
        bucket: String?,
        filePath: String?,
        fileKey: String?
    ) {
        iTuyaVideoLockManager?.getPictureAndVideo(
            bucket,
            filePath,
            object : ITuyaResultCallback<RemoteMediaBean> {
                override fun onSuccess(result: RemoteMediaBean?) {
                    L.i(TAG, result?.toString())
                    if (fileKey != null) {
                        iLockView?.updateImage(result?.fileUrl, fileKey)
                    }

                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                }

            })
    }


    /**
     * dpId 212
     */
    override fun onLockMessageArrived(
        devId: String?,
        dpCode: String?,
        fileBucketInfoBean: FileBucketInfoBean?
    ) {
        L.i(TAG, "onLockMessageArrived devId:$devId   dpCode:${dpCode.toString()}")
        fileBucketInfoBean?.let {
            iTuyaVideoLockManager?.getPictureAndVideo(
                it.bucket,
                it.filePath,
                object : ITuyaResultCallback<RemoteMediaBean> {
                    override fun onSuccess(result: RemoteMediaBean?) {
                        L.i(TAG, "fileBucketInfoBean:${result.toString()}")
                        result?.let { its ->
                            if (tempDpCode == DPCodes.DP_VIDEO_REQUEST_REALTIME) {
                                realTimeDialog?.updateImage(its.fileUrl, it.fileKey, its.angle)
                                return@let
                            }
                            if (tempDpCode == DPCodes.DP_ALARM_REQUEST_COUNTDOWN ||
                                tempDpCode == DPCodes.DP_UNLOCK_REQUEST_COUNTDOWN
                            ) {
                                countdownDialog?.updateImage(its.fileUrl, it.fileKey, its.angle)
                                return@let
                            }
                        }
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()

                    }

                })

        }

    }

    /**
     * dpId 63
     */
    override fun onVideoRequestRealtime(devId: String?, dpCode: String?, dpValue: String?) {
        L.i(TAG, "onVideoRequestRealtime devId:$devId   dpCode:${dpCode.toString()}  value:$dpCode")
        tempDpCode = dpCode
        showVideoRequestDialog()
    }

    /**
     * dpId 9
     */
    override fun unlockRequestCountdown(devId: String?, dpCode: String?, dpValue: Any?) {
        L.i(TAG, "unlockRequestCountdown devId:$devId   dpCode:${dpCode.toString()}  value:$dpCode")
        tempDpCode = dpCode
        showTakePhotoCountdownDialog(dpValue)
    }

    /**
     * dpId 46
     */
    override fun alarmRequestCountdown(devId: String?, dpCode: String?, dpValue: Any?) {
        L.i(TAG, "alarmRequestCountdown devId:$devId   dpCode:${dpCode.toString()}  value:$dpCode")
        tempDpCode = dpCode
        showTakePhotoCountdownDialog(dpValue)

    }

    private fun showTakePhotoCountdownDialog(dpValue: Any?) {
        dpValue?.let {
            if (countdownDialog == null)
                countdownDialog =
                    mDevId?.let { it1 -> PhotoCountdownDialog(mContext, it1, it as Int) }
            if (!countdownDialog!!.isShowing)
                countdownDialog!!.show()
            countdownDialog!!.setOnDismissListener {
                //Reset state for all the views in the dialog
                countdownDialog = null
            }
        }
    }

    /**
     * dpId
     */
    override fun onRemoteUnlockReport(devId: String?, dpCode: String?, dpValue: Any?) {
        L.i(TAG, "onRemoteUnlockReport devId:$devId   dpCode:${dpCode.toString()}  value:$dpCode")
    }

    /**
     * dpId
     */
    override fun onForceLockUpReport(devId: String?, dpCode: String?, dpValue: Any?) {
        L.i(TAG, "onForceLockUpReport devId:$devId   dpCode:${dpCode.toString()}  value:$dpCode")
    }

    override fun onLockDpUpdate(devId: String?, dpCodes: MutableMap<String, Any>?) {
        L.i(TAG, "onLockDpUpdate devId:$devId   dpCode:${dpCodes.toString()} ")
    }


    private fun showVideoRequestDialog() {
        if (realTimeDialog == null)
            realTimeDialog = mDevId?.let { RealTimeVideoDialog(mContext, it) }
        if (!realTimeDialog!!.isShowing)
            realTimeDialog!!.show()

        realTimeDialog!!.setOnDismissListener {
            //Reset state for all the views in the dialog
            realTimeDialog = null
        }
    }

    fun forceAntiLock() {
        iTuyaVideoLockManager?.forceLock(true, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                L.e(TAG, "code $code error:$error")
                ToastUtil.shortToast(mContext, error)
            }

            override fun onSuccess() {
                L.i(TAG, "force anti-lock success")
                ToastUtil.shortToast(mContext, "force anti-lock success")
            }

        })
    }

    override fun onDestroy(){
        iTuyaVideoLockManager?.ipcManager?.onDestroy()
        iTuyaVideoLockManager?.onDestroy()
    }
}