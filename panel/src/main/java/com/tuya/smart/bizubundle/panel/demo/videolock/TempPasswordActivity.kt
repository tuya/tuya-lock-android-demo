package com.tuya.smart.bizubundle.panel.demo.videolock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.PhotoLockTempPwdPresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.TempPasswordPresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.VideoLockTempPwdPresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.utils.ToastUtil

class TempPasswordActivity : AppCompatActivity() ,View.OnClickListener{
    private var mDevId:String? = null
    private var mPresenter:TempPasswordPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_password)
        initView()
        initData()
    }


    private fun initView(){
        findViewById<Button>(R.id.btn_create_online_pwd).setOnClickListener(this)
        findViewById<Button>(R.id.btn_set_online_name).setOnClickListener(this)
        findViewById<Button>(R.id.btn_get_online_list).setOnClickListener(this)
        findViewById<Button>(R.id.btn_delete_online_pwd).setOnClickListener(this)
        findViewById<Button>(R.id.btn_offline_pwd).setOnClickListener(this)
        findViewById<Button>(R.id.btn_set_offline_name).setOnClickListener(this)
        findViewById<Button>(R.id.btn_get_offline_list).setOnClickListener(this)
        findViewById<Button>(R.id.btn_clear_code).setOnClickListener(this)
        findViewById<Button>(R.id.btn_get_no_limit_pwd).setOnClickListener(this)
        findViewById<Button>(R.id.btn_set_online_pwd).setOnClickListener(this)
    }

    private fun initData(){
        mDevId = intent.getStringExtra("devId")
        val iDataManager = TuyaHomeSdk.getDataInstance()
        val deviceBean = iDataManager.getDeviceBean(mDevId)
        if(deviceBean?.categoryCode == "wf_jtmspro"){ //photo lock
            mPresenter = PhotoLockTempPwdPresenter(this,mDevId)
        }else if(deviceBean?.categoryCode == "videolock_1w_1"){//video lock
            mPresenter = VideoLockTempPwdPresenter(this,mDevId)
        }else{
            ToastUtil.shortToast(this,"This page not support")
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_create_online_pwd -> {
                mPresenter?.createOnlineTempPwd()
            }
            R.id.btn_set_online_name -> {
                mPresenter?.updateOnlinePwdName()
            }
            R.id.btn_get_online_list -> {
                mPresenter?.getOnlinePwdList()
            }
            R.id.btn_delete_online_pwd -> {
                mPresenter?.deleteOnlinePwd()
            }
            R.id.btn_offline_pwd -> {
                mPresenter?.getOfflineTempPwd()
            }
            R.id.btn_set_offline_name -> {
                mPresenter?.updateOfflineTempPwd()
            }
            R.id.btn_get_offline_list -> {
                mPresenter?.getOfflineTempPwdList()
            }
            R.id.btn_clear_code -> {
                mPresenter?.getOfflineClearCode()
            }
            R.id.btn_get_no_limit_pwd -> {
                mPresenter?.getNoLimitOfflinePwd()
            }
            R.id.btn_set_online_pwd -> {
                mPresenter?.updateOnlinePwd()
            }
        }
    }
}