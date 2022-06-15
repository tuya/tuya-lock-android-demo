package com.tuya.smart.bizubundle.panel.demo.videolock.view

/**
 * Create by blitzfeng on 5/23/22
 */
interface IVideoDialogView {
    fun onSuccess(msgWhat:Int)

    fun onError(error:String?)

    fun dismiss()
}