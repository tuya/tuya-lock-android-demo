package com.tuya.smart.bizubundle.panel.demo.videolock.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.tuya.drawee.view.DecryptImageView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.camera.middleware.widget.TuyaCameraView
import com.tuya.tuyalock.videolock.enums.RotateModeEnum
import java.util.*

/**
 * Create by blitzfeng on 5/25/22
 */
class PhotoCountdownDialog(mContext: Context, mDevId: String,val value: Int) : BaseRemoteDialog(mContext, mDevId),View.OnClickListener {

    companion object{
        const val MSG_UPDATE_TIME = 1
    }

    private lateinit var mForceAntiLockBtn:Button
    private lateinit var mIgnoreTv:TextView
    lateinit var mPreviewIv: DecryptImageView
    private var timer: Timer? = null
    private var timerTask:TimerTask? = null
    private var isCountdown = true

    private val mHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                MSG_UPDATE_TIME -> {
                    val countdown = msg.arg1
                    mIgnoreTv.text = mContext.getString(R.string.lock_ignore)+"("+countdown.toString()+")"
                    if(countdown == 0) {
                        isCountdown = false
                        dismiss()
                    }
                }
            }
        }
    }

    init {
        initView()
    }

    override fun initView() {
        super.initView()

        view.findViewById<LinearLayout>(R.id.ll_countdown).visibility = View.VISIBLE
        view.findViewById<Button>(R.id.btn_play).visibility = View.GONE
        view.findViewById<TuyaCameraView>(R.id.camera_video_view).visibility = View.GONE
        mForceAntiLockBtn = view.findViewById(R.id.btn_force_anti_lock)
        mForceAntiLockBtn.setOnClickListener(this)
        mPreviewIv = view.findViewById(R.id.iv_preview)
        mIgnoreTv = view.findViewById(R.id.tv_ignore)
        mIgnoreTv.setOnClickListener(this)

        setCountdown(value)
    }

    fun updateImage(imagePath: String?, fileKey: String, angle: Int?) {
        imagePath?.let {
            mPreviewIv.setImageURI(imagePath, fileKey.toByteArray())
        }
        angle?.let {

        }


    }

    fun setCountdown(v:Int){
        var value = v
        if(timer == null){
            timer = Timer()

            timerTask =  object:TimerTask() {
                override fun run() {
                    val msg = Message.obtain()
                    msg.what = MSG_UPDATE_TIME
                    msg.arg1 = value
                    mHandler.sendMessage(msg)

                    if(value == 0 || !isCountdown)
                        return
                    value --
                }

            }
            timer!!.schedule(timerTask,0L,1000L)
        }
    }

    override fun onSuccess(msgWhat: Int) {
    }

    override fun onError(error: String?) {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_force_anti_lock -> {
                mPresenter.forceAntiLock()
            }
            R.id.tv_ignore -> {
                dismiss()
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        timer?.cancel()
        timerTask?.cancel()
        mPresenter.onDestroy()
    }
}