package com.tuya.smart.bizubundle.panel.demo.videolock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.tuya.smart.bizubundle.panel.demo.R

class LockFunctionActivity : AppCompatActivity(),View.OnClickListener {
//    private lateinit var mLogBtn:Button
//    private lateinit var mTempPwdBtn:Button
//    private lateinit var mMemberBtn:Button
//    private lateinit var mOthersBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_ability)

        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.btn_log).setOnClickListener(this)
        findViewById<Button>(R.id.btn_temp_pwd).setOnClickListener(this)
        findViewById<Button>(R.id.btn_member).setOnClickListener(this)
        findViewById<Button>(R.id.btn_other).setOnClickListener(this)
        findViewById<Button>(R.id.btn_video).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_log -> {
                startActivity(Intent(this,LogsActivity::class.java)
                    .putExtra("devId",intent.getStringExtra("devId")))

            }
            R.id.btn_video -> {
                startActivity(Intent(this,LockActivity::class.java)
                    .putExtra("devId",intent.getStringExtra("devId")))
            }
            R.id.btn_member -> {
                startActivity(Intent(this,MemberManagementActivity::class.java)
                    .putExtra("devId",intent.getStringExtra("devId")))
            }
            R.id.btn_temp_pwd -> {
                startActivity(Intent(this,TempPasswordActivity::class.java)
                    .putExtra("devId",intent.getStringExtra("devId")))
            }
        }
    }
}