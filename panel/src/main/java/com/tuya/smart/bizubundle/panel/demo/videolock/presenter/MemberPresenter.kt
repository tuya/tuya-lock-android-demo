package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import com.tuya.smart.bizubundle.panel.demo.videolock.view.IMemberView
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.uispecs.component.util.FamilyDialogUtils
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.ILockBaseAbilityManager
import com.tuya.tuyalock.videolock.bean.WifiLockUserBean
import com.tuya.tuyalock.videolock.bean.WifiLockUserDetail
import org.json.JSONObject

/**
 * Create by blitzfeng on 7/26/22
 */
class MemberPresenter {
    private val mDevId:String?
    private val mContext:Context
    private val iLockBaseAbilityManager: ILockBaseAbilityManager?
    private var numb:Int = 0
    private var userList:MutableList<String> = ArrayList()
    private var iMemberView:IMemberView? = null

    constructor(context: Context, devId:String?,iView:IMemberView?){
        mDevId = devId
        mContext = context
        iMemberView = iView
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        val iVideoLockManager = iTuyaLockManager?.newVideoLockManagerInstance(mDevId)
        iLockBaseAbilityManager = iVideoLockManager?.baseAbilityManager
    }


    fun addMember(){
        iLockBaseAbilityManager?.addUser("User${numb++}",null,"0",System.currentTimeMillis(),
        180,80,"CM",object :ITuyaResultCallback<String>{
                override fun onSuccess(result: String?) {
                    // add userId to list
                    result?.let {
                        val json = JSONObject(it)
                        val id = json.getString("userId")
                        userList.add(id)
                    }
                    ToastUtil.shortToast(mContext,"Add Success")
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    ToastUtil.shortToast(mContext,errorMessage)
                }

            })
    }

    fun updateMember(){
        iLockBaseAbilityManager?.updateUser(userList.get(0),"User--${numb}",null,
        object :IResultCallback{
            override fun onError(code: String?, error: String?) {
                ToastUtil.shortToast(mContext,error)
            }

            override fun onSuccess() {
                ToastUtil.shortToast(mContext,"Modify Success")
            }

        })
    }
    fun deleteMember(){
        iLockBaseAbilityManager?.deleteUser(userList.get(0),object :IResultCallback{
            override fun onError(code: String?, error: String?) {
                ToastUtil.shortToast(mContext,error)
            }

            override fun onSuccess() {
                ToastUtil.shortToast(mContext,"Delete Success")
                userList.removeAt(0)
            }

        })
    }

    fun getMemberList(){
        iLockBaseAbilityManager?.getUsersList(object :ITuyaResultCallback<ArrayList<WifiLockUserBean>>{
            override fun onSuccess(result: ArrayList<WifiLockUserBean>?) {
                iMemberView?.onGetList(result)
                userList.clear()
                result?.let {
                    for (wifiLockUser in it){
                        userList.add(wifiLockUser.userId)
                    }
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext,errorMessage)
            }

        })
    }

    fun getMemberDetail(userId:String){
        iLockBaseAbilityManager?.getUserDetail(userId,object :ITuyaResultCallback<WifiLockUserDetail>{
            override fun onSuccess(result: WifiLockUserDetail?) {
                result?.let {
                    showDetail(it)
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.shortToast(mContext,errorMessage)
            }

        })
    }

    fun showDetail(wifiLockUserDetail: WifiLockUserDetail){
        FamilyDialogUtils.showConfirmAndCancelDialog(mContext,"",wifiLockUserDetail.toString())
    }
}