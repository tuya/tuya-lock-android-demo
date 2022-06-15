package com.tuya.smart.bizubundle.panel.demo.videolock.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.tuya.drawee.view.DecryptImageView
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.VideoDialogPresenter
import com.tuya.smart.camera.middleware.widget.TuyaCameraView

/**
 * Create by blitzfeng on 5/25/22
 */
 abstract class BaseRemoteDialog : Dialog ,IVideoDialogView{
    private val TAG = "BaseRemoteDialog"

    open val mContext:Context
    open val mDevId:String


    companion object {
        const val MSG_UPDATE_INIT = 1
        const val MSG_START_PREVIEW = 2
        const val ASPECT_RATIO_WIDTH = 9
        const val ASPECT_RATIO_HEIGHT = 16
    }



    lateinit var view: View
    val mPresenter: VideoDialogPresenter

    constructor(mContext: Context,mDevId:String) : super(mContext){
        this.mContext = mContext
        this.mDevId = mDevId

        mPresenter = VideoDialogPresenter(mContext, mDevId, this)
    }

    open fun initView(){
        getWindow()?.requestFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(mContext).inflate(R.layout.dialog_realtime_video, null)

        setContentView(view)
        getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE));
        getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )



    }

}