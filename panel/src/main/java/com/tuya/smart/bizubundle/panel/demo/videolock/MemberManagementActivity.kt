package com.tuya.smart.bizubundle.panel.demo.videolock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.bizubundle.panel.demo.R
import com.tuya.smart.bizubundle.panel.demo.videolock.adapter.MemberAdapter
import com.tuya.smart.bizubundle.panel.demo.videolock.presenter.MemberPresenter
import com.tuya.smart.bizubundle.panel.demo.videolock.view.IMemberView
import com.tuya.tuyalock.videolock.bean.WifiLockUserBean

class MemberManagementActivity : AppCompatActivity() ,View.OnClickListener,IMemberView{

    private lateinit var mAddMemberBtn:Button
    private lateinit var mUpdateMemberBtn:Button
    private lateinit var mDeleteMemberBtn:Button
    private lateinit var mMemberListBtn:Button
    private lateinit var mMemberListRv:RecyclerView

    private lateinit var mPresenter:MemberPresenter
    private lateinit var mAdapter:MemberAdapter

    private var mDevId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_management)
        initView()
        initData()
    }

    private fun initView(){
        mAddMemberBtn = findViewById(R.id.btn_add_member)
        mUpdateMemberBtn = findViewById(R.id.btn_update_member)
        mDeleteMemberBtn = findViewById(R.id.btn_delete_member)
        mMemberListBtn = findViewById(R.id.btn_member_list)
        mMemberListRv = findViewById(R.id.rv_member_list)
        mAddMemberBtn.setOnClickListener(this)
        mUpdateMemberBtn.setOnClickListener(this)
        mDeleteMemberBtn.setOnClickListener(this)
        mMemberListBtn.setOnClickListener(this)

        mAdapter = MemberAdapter()
        mMemberListRv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mMemberListRv.adapter = mAdapter
        mAdapter.onItemClickListener = object :MemberAdapter.OnItemClickListener{
            override fun onClick(bean: WifiLockUserBean) {
                mPresenter.getMemberDetail(bean.userId)
            }

        }
    }

    private fun initData(){
        mDevId = intent.getStringExtra("devId");
        mPresenter = MemberPresenter(this,mDevId,this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_add_member -> {
                mPresenter.addMember()
            }
            R.id.btn_update_member -> {
                mPresenter.updateMember()
            }
            R.id.btn_delete_member -> {
                mPresenter.deleteMember()
            }
            R.id.btn_member_list -> {
                mPresenter.getMemberList()
            }
        }
    }

    override fun onGetList(list: ArrayList<WifiLockUserBean>?) {
        list?.let {
            mAdapter.list.clear()
            mAdapter.list.addAll(it)
            mAdapter.notifyDataSetChanged()

        }
    }
}