package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.app.Activity
import android.content.Context
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tuya.smart.android.camera.sdk.bean.IPCSnapshotConfig
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.mvp.presenter.BasePresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.LockActivity
import com.tuya.smart.bizubundle.panel.demo.videolock.utils.CheckPermissionUtils
import com.tuya.smart.bizubundle.panel.demo.videolock.utils.IPCCameraUtils
import com.tuya.smart.bizubundle.panel.demo.videolock.view.BaseRemoteDialog
import com.tuya.smart.bizubundle.panel.demo.videolock.view.IVideoDialogView
import com.tuya.smart.bizubundle.panel.demo.videolock.view.RealTimeVideoDialog
import com.tuya.smart.bizubundle.panel.demo.videolock.view.BaseRemoteDialog.Companion.MSG_START_PREVIEW
import com.tuya.smart.camera.camerasdk.bean.TuyaVideoFrameInfo
import com.tuya.smart.camera.camerasdk.typlayer.callback.AbsP2pCameraListener
import com.tuya.smart.camera.ipccamerasdk.p2p.ICameraP2P
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.IIPCManager
import com.tuya.tuyalock.videolock.api.ILockReportListener
import com.tuya.tuyalock.videolock.api.IVideoLockManager
import com.tuya.tuyalock.videolock.bean.FileBucketInfoBean
import com.tuya.tuyalock.videolock.enums.RotateModeEnum
import java.io.File
import java.nio.ByteBuffer

/**
 * Create by blitzfeng on 5/20/22
 */
class VideoDialogPresenter(
    val mContext: Context,
    var mDevId: String,
    var iVideoDialogView: IVideoDialogView?
) : BasePresenter(), ILockReportListener {
    private val TAG = "VideoDialogPresenter"
    private val MSG_TAKING_PHOTO = 1

    private var iTuyaVideoLockManager: IVideoLockManager? = null
    private var iIpcManager: IIPCManager? = null
    private var isTakingPhoto = false

    init {
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        iTuyaVideoLockManager = iTuyaLockManager.newVideoLockManagerInstance(mDevId)
        iTuyaVideoLockManager?.registerLockReportListener(this)
        iIpcManager = iTuyaVideoLockManager?.ipcManager
    }

    fun initCamera() {
        iIpcManager?.registerP2PCameraListener(object : AbsP2pCameraListener() {
            override fun onReceiveAudioBufferData(
                nSampleRate: Int,
                nChannelNum: Int,
                nBitWidth: Int,
                nTimeStamp: Long,
                progress: Long,
                duration: Long
            ) {
                super.onReceiveAudioBufferData(
                    nSampleRate,
                    nChannelNum,
                    nBitWidth,
                    nTimeStamp,
                    progress,
                    duration
                )
            }

            override fun onReceiveFrameYUVData(
                sessionId: Int,
                y: ByteBuffer?,
                u: ByteBuffer?,
                v: ByteBuffer?,
                videoFrameInfo: TuyaVideoFrameInfo?,
                camera: Any?
            ) {
                super.onReceiveFrameYUVData(sessionId, y, u, v, videoFrameInfo, camera)
            }

            override fun onReceiveFrameYUVData(
                sessionId: Int,
                y: ByteBuffer?,
                u: ByteBuffer?,
                v: ByteBuffer?,
                width: Int,
                height: Int,
                nFrameRate: Int,
                nIsKeyFrame: Int,
                timestamp: Long,
                nProgress: Long,
                nDuration: Long,
                camera: Any?
            ) {
                super.onReceiveFrameYUVData(
                    sessionId,
                    y,
                    u,
                    v,
                    width,
                    height,
                    nFrameRate,
                    nIsKeyFrame,
                    timestamp,
                    nProgress,
                    nDuration,
                    camera
                )
            }

            override fun onReceiveSpeakerEchoData(pcm: ByteBuffer?, sampleRate: Int) {
                super.onReceiveSpeakerEchoData(pcm, sampleRate)
            }

            override fun onSessionStatusChanged(camera: Any?, sessionId: Int, sessionStatus: Int) {
                super.onSessionStatusChanged(camera, sessionId, sessionStatus)
            }


        })


    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_TAKING_PHOTO -> {
                isTakingPhoto = false
            }
        }
        return true
    }

    fun generateCameraView(p0: Any?) {
        iIpcManager?.generateCameraView(p0)
    }

    fun startPlay() {
        if (iIpcManager?.isConnected == true)
            iIpcManager?.startPreview(2, object : IResultCallback {
                override fun onError(code: String?, error: String?) {
//                    Toast.makeText(mContext, "error$error", Toast.LENGTH_SHORT).show()
                    L.e(TAG, "code:$code  msg:$error")
                    iVideoDialogView?.onError(error)
                }

                override fun onSuccess() {
//                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show()
                    L.i(TAG, "preview success")
                    iVideoDialogView?.onSuccess(MSG_START_PREVIEW)
                }

            })

    }

    fun connect() {

        iIpcManager?.connect(object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                Toast.makeText(
                    mContext,
                    "P2P connect failed :error$error",
                    Toast.LENGTH_SHORT
                )
                    .show()
                L.e(TAG, "p2p connect failed $error")
            }

            override fun onSuccess() {
                iVideoDialogView?.onSuccess(BaseRemoteDialog.MSG_UPDATE_INIT)
                L.i(TAG, "p2p connect success")
            }

        })
    }

    fun startTalk() {
        val permissions = arrayOf("android.permission.RECORD_AUDIO")
        if (checkAndRequestPermission(
                permissions,
                LockActivity.REQUEST_PERMISSION_RECORDER_CODE
            )
        )
            iIpcManager?.startTalk(object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    iVideoDialogView?.onError(error)
                }

                override fun onSuccess() {
                    L.i(TAG, "startTalk success")
                }

            })
    }

    fun stopTalk() {
        iIpcManager?.stopTalk(object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                iVideoDialogView?.onError(error)
            }

            override fun onSuccess() {

            }

        })
    }

    fun isSupportTalk(): Boolean {
        return iIpcManager?.isSupportTalk == true
    }

    fun isTalking(): Boolean {
        return iIpcManager?.isTalkBacking == true
    }

    fun snapshot(rotateMode: RotateModeEnum?) {
        val permissions = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )
        if (checkAndRequestPermission(
                permissions,
                LockActivity.REQUEST_PERMISSION_STORAGE_CODE
            )
        ) {
            var path = ""
            if (Build.VERSION.SDK_INT >= 30) {
                path = IPCCameraUtils.RECORD_PATH_Q
                val file = File(path)
                if (!file.exists())
                    file.mkdirs()

            } else {
                path = IPCCameraUtils.SNAPSHOT_PATH
            }
            iIpcManager?.snapshot(
                path,
                mContext,
                ICameraP2P.PLAYMODE.LIVE,
                rotateMode ?: RotateModeEnum.NORMAL,
                object : ITuyaResultCallback<String> {
                    override fun onSuccess(result: String?) {
                        L.i(TAG, "snapshot success :$result")
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        iVideoDialogView?.onError(errorMessage)
                    }

                })

        }
    }

    fun recorder(rotateMode: RotateModeEnum?) {
        val permissions = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
        )
        if (checkAndRequestPermission(
                permissions,
                LockActivity.REQUEST_PERMISSION_STORAGE_CODE
            )
        ) {
            var path = ""
            if (Build.VERSION.SDK_INT >= 30) {
                path = IPCCameraUtils.recordPathSupportQ(mDevId)
                val file = File(path)
                if (!file.exists())
                    file.mkdirs()

            } else {
                path = IPCCameraUtils.SNAPSHOT_PATH
            }
            iIpcManager?.startRecord(
                path,
                mContext,
                rotateMode ?: RotateModeEnum.NORMAL,
                object : IResultCallback {
                    override fun onError(code: String?, error: String?) {
                        iVideoDialogView?.onError(error)
                    }

                    override fun onSuccess() {
                        L.i(TAG, "recorder success")
                    }

                })
        }
    }

    fun stopRecorder() {
        iIpcManager?.stopRecord(object : ITuyaResultCallback<Map<String, String>> {
            override fun onSuccess(result: Map<String, String>?) {
                result?.let {
                    val videoPath = it.get("video")
                    val coverImagePath = it.get("coverImage")
                    L.i(TAG, "videoPath:$videoPath  coverImage:$coverImagePath")
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                iVideoDialogView?.onError(errorMessage)
            }

        })
    }

    fun isRecorder(): Boolean {
        return iIpcManager?.isRecording == true
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

    fun setMute(mute: Boolean) {
        iIpcManager?.enableMute(mute, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                iVideoDialogView?.onError(error)
            }

            override fun onSuccess() {
                L.i(TAG, "set mute $mute success ")
            }

        })
    }

    fun isMuting(): Boolean {
        return iIpcManager?.isMuting == true
    }

    override fun onDestroy() {
        iIpcManager?.stopPreview(null)
        iIpcManager?.disconnect(null)
        iIpcManager?.onDestroy()
    }

    fun checkAndRequestPermission(permission: Array<String>, requestCode: Int): Boolean {
        var isGranted = true
        for (p in permission)
            if (!CheckPermissionUtils.checkSinglePermission(p, mContext as Activity)) {
                isGranted = false
            }
        if (!isGranted) {
            CheckPermissionUtils.requestPermission(mContext as Activity, permission, requestCode)
            return false
        }
        return true

    }

    /**
     * Only publish one time in 15s because of the device
     * Publish retake photo dp, the Device will take photo and report by the callback "onLockMessageArrived"
     */
    fun retakePhoto() {
        if (isTakingPhoto) {
            Toast.makeText(mContext, "Is taking photo", Toast.LENGTH_SHORT).show()
            return
        }

        iTuyaVideoLockManager?.reTakePhoto(true, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                L.e(TAG, "RetakePhoto error:$error")
            }

            override fun onSuccess() {
                L.i(TAG, "Retake photo publish success")
                isTakingPhoto = true
                mHandler.sendEmptyMessageDelayed(MSG_TAKING_PHOTO, 15 * 1000L)
            }

        })
    }

    fun isPlaying(): Boolean {
        return iIpcManager?.isPreviewOn == true
    }

    fun stopPreview() {
        iIpcManager?.stopPreview(object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                L.e(TAG, "StopPreview error:$error")
            }

            override fun onSuccess() {
                L.i(TAG, "Stop preview success")
            }

        })
    }

    fun isConnected(): Boolean {
        return iIpcManager?.isConnected == true
    }

    fun remoteUnLock(isOpen: Boolean, confirm: Boolean) {
        iTuyaVideoLockManager?.remoteLock(isOpen, confirm, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(mContext, "publish success", Toast.LENGTH_SHORT).show()
                if (!confirm)
                    iVideoDialogView?.dismiss()
            }

        })
    }

    override fun onLockMessageArrived(
        devId: String?,
        dpCode: String?,
        fileBucketInfoBean: FileBucketInfoBean?
    ) {
        isTakingPhoto = false
    }

    override fun onVideoRequestRealtime(devId: String?, dpCode: String?, dpValue: String?) {
    }

    override fun unlockRequestCountdown(devId: String?, dpCode: String?, dpValue: Any?) {
    }

    override fun alarmRequestCountdown(devId: String?, dpCode: String?, dpValue: Any?) {
    }

    override fun onRemoteUnlockReport(devId: String?, dpCode: String?, dpValue: Any?) {
        Toast.makeText(mContext, "Unlock Success", Toast.LENGTH_SHORT).show()
        iVideoDialogView?.dismiss()
    }

    override fun onForceLockUpReport(devId: String?, dpCode: String?, dpValue: Any?) {
    }

    override fun onLockDpUpdate(devId: String?, dpCode: MutableMap<String, Any>?) {
    }

}