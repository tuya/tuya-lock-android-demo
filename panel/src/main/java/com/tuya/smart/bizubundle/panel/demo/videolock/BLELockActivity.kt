package com.tuya.smart.bizubundle.panel.demo.videolock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.tuya.smart.android.common.utils.AESUtil
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.IBLEExtendManager
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.utils.ToastUtil
import java.lang.Exception

class BLELockActivity : AppCompatActivity(), View.OnClickListener {
    private var mDevId: String? = null
    private var iBLEExtendManager: IBLEExtendManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blelock)

        init()
    }

    fun init() {
        findViewById<TextView>(R.id.tv_query_setting).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_set_pwd).setOnClickListener(this)

        mDevId = intent.getStringExtra("devId");
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        val iTuyaBleLock = iTuyaLockManager?.getBleLock(mDevId);
        iBLEExtendManager = iTuyaBleLock?.extendAbilityManager

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_query_setting -> {
                querySetting()
            }
            R.id.tv_set_pwd -> {
                setDefaultPassword()
            }
        }
    }

    private fun setDefaultPassword() {
        iBLEExtendManager?.setAudioUnlockPassword(true,"111222",object :ITuyaResultCallback<Boolean>{
            override fun onSuccess(result: Boolean?) {
                ToastUtil.shortToast(this@BLELockActivity,"result:$result")
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(this@BLELockActivity,errorMessage)
            }

        })
    }

    private fun querySetting() {
        iBLEExtendManager?.hasSettingAudioUnlockPassword(object :ITuyaResultCallback<Boolean>{
            override fun onSuccess(result: Boolean?) {
                ToastUtil.shortToast(this@BLELockActivity,"result:$result")
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(this@BLELockActivity,errorMessage)
            }

        })
    }
}