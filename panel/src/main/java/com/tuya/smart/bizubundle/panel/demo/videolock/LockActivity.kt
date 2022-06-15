package com.tuya.smart.bizubundle.panel.demo.videolock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.tuya.drawee.view.DecryptImageView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.LockPresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.view.ILockView

class LockActivity : AppCompatActivity(), View.OnClickListener,ILockView {
    private val TAG = "LockActivity"

    companion object{
        const val REQUEST_PERMISSION_STORAGE_CODE = 10
        const val REQUEST_PERMISSION_RECORDER_CODE = 20
    }

    private lateinit var remoteUnlockTv: TextView
    private lateinit var requestLatestPictureTv: TextView
    private lateinit var lockStateTv: TextView
    private lateinit var forceAntiLockTv:TextView
    private lateinit var decrptImageDiv:DecryptImageView

    private var mDevId: String? = null
    private lateinit var mPresenter: LockPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        initView()
        initData()
        initPresenter()

    }

    private fun initData() {
        mDevId = intent.getStringExtra("devId");


    }

    private fun initPresenter() {
        mPresenter = LockPresenter(this, mDevId,this)

    }


    private fun initView() {
        remoteUnlockTv = findViewById(R.id.tv_remote_unlock)
        lockStateTv = findViewById(R.id.tv_lock_state)
        requestLatestPictureTv = findViewById(R.id.tv_request_latest_picture)
        remoteUnlockTv.setOnClickListener(this)
        requestLatestPictureTv.setOnClickListener(this)
        forceAntiLockTv = findViewById(R.id.tv_force_anti_lock)
        forceAntiLockTv.setOnClickListener(this)
        decrptImageDiv = findViewById(R.id.div_picture)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_remote_unlock -> {
                mPresenter.remoteUnLock(true,true)

            }
            R.id.tv_request_latest_picture -> {
                mPresenter.requestLatestPicture()
            }
            R.id.tv_force_anti_lock -> {
                mPresenter.forceAntiLock()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun updateImage(url: String?,fileKey:String) {
        url?.let {
            decrptImageDiv.setImageURI(it,fileKey.toByteArray())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

}