package com.tuya.smart.bizubundle.panel.demo.videolock.view

import android.app.Dialog
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.tuya.drawee.view.DecryptImageView
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.VideoDialogPresenter
import com.tuya.smart.camera.middleware.widget.AbsVideoViewCallback
import com.tuya.smart.camera.middleware.widget.TuyaCameraView
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.enums.RotateModeEnum

/**
 * Remote unlock request dialog
 * Create by blitzfeng on 5/12/22
 */
class RealTimeVideoDialog(override var mContext: Context,override var mDevId: String) : BaseRemoteDialog(mContext,  mDevId),
    View.OnClickListener {
    private val TAG = "RealTimeVideoDialog"

    private var rotateMode: RotateModeEnum? = null
    var cameraView: TuyaCameraView? = null
    lateinit var mPreviewIv: DecryptImageView
    lateinit var mPlayBtn: Button
    lateinit var mTalkBtn: Button
    lateinit var mSnapshotBtn: Button
    lateinit var mRecorderBtn: Button
    lateinit var mMuteBtn: Button

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_UPDATE_INIT -> {
                    Toast.makeText(mContext, "P2P connect success", Toast.LENGTH_SHORT).show()
                    mPreviewIv.visibility = View.GONE
                    mPlayBtn.visibility = View.GONE
                    mPresenter.startPlay()
                }
                MSG_START_PREVIEW -> {
                    if (mPresenter.isSupportTalk())
                        mTalkBtn.visibility = View.VISIBLE
                    mMuteBtn.visibility = View.VISIBLE
                    mSnapshotBtn.visibility = View.VISIBLE
                    mRecorderBtn.visibility = View.VISIBLE

                }
            }
        }
    }

    init {

        initCamera()
        initView()

    }

    private fun initCamera() {
        mPresenter.initCamera()
    }


     override fun initView() {
        super.initView()

        view.findViewById<LinearLayout>(R.id.ll_realtime_request).visibility = View.VISIBLE
        cameraView = view.findViewById(R.id.camera_video_view)
        cameraView!!.setViewCallback(object : AbsVideoViewCallback() {
            override fun onCreated(p0: Any?) {
                super.onCreated(p0)
                L.e(TAG, "cameraView onCreated $p0")
                mPresenter.generateCameraView(p0)

            }
        })
        cameraView!!.createVideoView(mDevId)

        val windowManager = mContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val width = windowManager.defaultDisplay.width
        val height = width * ASPECT_RATIO_WIDTH / ASPECT_RATIO_HEIGHT
        val layoutParams = FrameLayout.LayoutParams(width, height)
        cameraView!!.layoutParams = layoutParams

        view.findViewById<Button>(R.id.btn_unlock).setOnClickListener(this)
        view.findViewById<Button>(R.id.btn_refuse_unlock).setOnClickListener(this)
        view.findViewById<Button>(R.id.btn_retake_photo).setOnClickListener(this)
        mPlayBtn = view.findViewById(R.id.btn_play)
        mPlayBtn.setOnClickListener(this)
        mPreviewIv = view.findViewById(R.id.iv_preview)
        mTalkBtn = view.findViewById(R.id.btn_talk)
        mTalkBtn.setOnClickListener(this)
        mSnapshotBtn = view.findViewById(R.id.btn_snapshot)
        mSnapshotBtn.setOnClickListener(this)
        mRecorderBtn = view.findViewById(R.id.btn_recorder)
        mRecorderBtn.setOnClickListener(this)
        mMuteBtn = view.findViewById(R.id.btn_mute)
        mMuteBtn.setOnClickListener(this)

    }


    fun updateImage(imagePath: String?, fileKey: String, angle: Int?) {
        imagePath?.let {
            mPreviewIv.setImageURI(imagePath, fileKey.toByteArray())
            if (mPresenter.isPlaying()) {
                mPlayBtn.visibility = View.VISIBLE
                mPreviewIv.visibility = View.VISIBLE
                mPresenter.stopPreview()
            }
        }
        angle?.let {
            cameraView?.setRotateAngle(it.toFloat())

            when (it) {
                0 -> rotateMode = RotateModeEnum.NORMAL
                1 -> rotateMode = RotateModeEnum.ANGEL_90
                2 -> rotateMode = RotateModeEnum.ANGEL_180
                3 -> rotateMode = RotateModeEnum.ANGEL_270
            }
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_unlock -> {
                mPresenter.remoteUnLock(true, true)
            }
            R.id.btn_refuse_unlock -> {
                mPresenter.remoteUnLock(true, false)
            }
            R.id.btn_play -> {
                if (mPresenter.isConnected())
                    mPresenter.startPlay()
                else
                    mPresenter.connect()
            }
            R.id.btn_mute -> {
                if (mPresenter.isMuting())
                    mPresenter.setMute(false)
                else
                    mPresenter.setMute(true)
            }
            R.id.btn_recorder -> {
                if (mPresenter.isRecorder())
                    mPresenter.stopRecorder()
                else
                    mPresenter.recorder(rotateMode)
            }
            R.id.btn_snapshot -> {
                mPresenter.snapshot(rotateMode)

            }
            R.id.btn_talk -> {
                if (mPresenter.isTalking())
                    mPresenter.stopTalk()
                else
                    mPresenter.startTalk()
            }
            R.id.btn_retake_photo -> {
                mPresenter.retakePhoto()
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        mPresenter.onDestroy()
    }

    override fun onSuccess(msgWhat: Int) {
        mHandler.sendEmptyMessage(msgWhat)
    }

    override fun onError(error: String?) {
        ToastUtil.shortToast(mContext, error)
    }

    override fun show() {

        super.show()
    }



}