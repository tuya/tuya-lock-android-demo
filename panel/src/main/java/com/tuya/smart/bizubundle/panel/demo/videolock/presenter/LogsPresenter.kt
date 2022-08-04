package com.tuya.smart.bizubundle.panel.demo.videolock.presenter

import android.content.Context
import com.tuya.smart.android.mvp.presenter.BasePresenter
import com.tuya.smart.api.service.MicroServiceManager
import com.tuya.smart.bizubundle.panel.demo.videolock.view.ILogView
import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import com.tuya.smart.family.base.api.anntation.MemberRole
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.MemberBean
import com.tuya.smart.home.sdk.callback.ITuyaGetMemberListCallback
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.optimus.lock.api.ITuyaLockManager
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.tuya.tuyalock.videolock.api.ILockBaseAbilityManager
import com.tuya.tuyalock.videolock.bean.LatestLogBean
import com.tuya.tuyalock.videolock.bean.LogsListBean

/**
 * Create by blitzfeng on 7/5/22
 */
class LogsPresenter(mDevId: String?, val context: Context?, val iView: ILogView?) :
    BasePresenter(context) {
    private val iLockBaseAbilityManager: ILockBaseAbilityManager?
    var ownerMemberInfo: MemberBean? = null

    init {
        val iTuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager::class.java)
        val iVideoLockManager = iTuyaLockManager?.newVideoLockManagerInstance(mDevId)
        iLockBaseAbilityManager = iVideoLockManager?.baseAbilityManager
        getHomeMemberInfo()
    }

    fun getLatestLog(isHomeMember: Boolean) {

        if (isHomeMember) {
            if (ownerMemberInfo == null) {
                ToastUtil.shortToast(context, "home info is null")
                return
            }
            iLockBaseAbilityManager?.getLatestLog(
                0,
                ownerMemberInfo!!.memberId.toString(),
                object : ITuyaResultCallback<LatestLogBean> {
                    override fun onSuccess(result: LatestLogBean?) {
                        result?.let {
                            iView?.onGetLog(it)

                        }
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        ToastUtil.shortToast(context, errorMessage)
                    }

                })
        } else {

        }


    }

    private fun getHomeMemberInfo() {
        val familyService =
            MicroServiceManager.getInstance().findServiceByInterface<AbsBizBundleFamilyService>(
                AbsBizBundleFamilyService::class.java.name
            )
        familyService?.let {
            TuyaHomeSdk.getMemberInstance()
                .queryMemberList(it.currentHomeId, object : ITuyaGetMemberListCallback {
                    override fun onSuccess(memberBeans: MutableList<MemberBean>?) {
                        memberBeans?.forEach {

                            if (it.uid == TuyaHomeSdk.getUserInstance().user?.uid) {
                                ownerMemberInfo = it
                                return@forEach
                            }
                        }

                    }

                    override fun onError(errorCode: String?, error: String?) {
                        ToastUtil.shortToast(context, error)
                    }

                })
        }
    }

    fun getLogsList(isHomeMember: Boolean) {

        if (isHomeMember) {
            if (ownerMemberInfo == null) {
                ToastUtil.shortToast(context, "home info is null")
                return
            }
            iLockBaseAbilityManager?.getLogList("",
                null,
                false,
                null,
                System.currentTimeMillis(),
                "",
                20,
                0,
                ownerMemberInfo!!.memberId.toString(),
                object : ITuyaResultCallback<LogsListBean> {
                    override fun onSuccess(result: LogsListBean?) {
                        result?.let {
                            iView?.onGetLogList(it.records)
                        }
                    }

                    override fun onError(errorCode: String?, errorMessage: String?) {
                        ToastUtil.shortToast(context, errorMessage)
                    }

                })
        } else {

        }

    }

    fun bindToUser(isHomeMember: Boolean, logsInfoBean: LogsListBean.LogsInfoBean?) {
        if (logsInfoBean == null)
            return
        if (isHomeMember) {
            if (ownerMemberInfo == null) {
                ToastUtil.shortToast(context, "home info is null")
                return
            }
            //bind to current user
            val array = arrayOf("${logsInfoBean.dpId}-${logsInfoBean.data}")
            iLockBaseAbilityManager?.bindUnlockWaysToUser(ownerMemberInfo!!.memberId.toString(),
                array, object : IResultCallback {
                    override fun onError(code: String?, error: String?) {
                        ToastUtil.shortToast(context, error)
                    }

                    override fun onSuccess() {
                        getLogsList(true)
                    }

                })
        } else {

        }

    }
}