package com.tuya.lock.demo.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.UserMemberAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.scene.api.IResultCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.MemberInfoBean;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * 门锁用户成员管理
 */
public class UserMemberActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private UserMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_user_member);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        findViewById(R.id.user_add).setOnClickListener(v -> {
            UserAddActivity.startActivity(v.getContext(), null, mDevId, 0);
        });

        RecyclerView rvList = findViewById(R.id.user_list);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new UserMemberAdapter();
        adapter.setDevId(mDevId);
        adapter.deleteUser(new IResultCallback<MemberInfoBean>() {
            @Override
            public void onSuccess(MemberInfoBean infoBean) {
                deleteLockUser(infoBean.getUserId());
            }

            @Override
            public void onError(@Nullable String s, @Nullable String s1) {

            }
        });
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLockUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    /**
     * 获取门锁成员
     */
    private void getLockUser() {
        String dps = "12,13,14,15,16,19";
        tuyaLockDevice.getProLockMemberList(dps, new ITuyaResultCallback<ArrayList<MemberInfoBean>>() {
            @Override
            public void onSuccess(ArrayList<MemberInfoBean> result) {
                Log.i(Constant.TAG, "get lock users success: lockUserBean = " + JSONArray.toJSONString(result));
                adapter.setData(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "get lock users failed: code = " + code + "  message = " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                //DEMO 没用户的情况创建个用户
//                ArrayList<MemberInfoBean> users = new ArrayList<>();
//                MemberInfoBean bleLockUser = new MemberInfoBean();
//                bleLockUser.setAvatarUrl("https://images.tuyacn.com/app/default_avatar/avatar1.png");
//                bleLockUser.setLockUserId(1);
//                bleLockUser.setNickName("懂王在世");
//                bleLockUser.setUserId("01010");
//                bleLockUser.getTimeScheduleInfo().setPermanent(false);
//                bleLockUser.getTimeScheduleInfo().setEffectiveTime(1670377959000L);
//                bleLockUser.getTimeScheduleInfo().setExpiredTime(1701913959000L);
//                users.add(bleLockUser);
//
//                MemberInfoBean bleLockUser2 = new MemberInfoBean();
//                bleLockUser2.setAvatarUrl("https://images.tuyacn.com/app/default_avatar/avatar2.png");
//                bleLockUser2.setLockUserId(2);
//                bleLockUser2.setNickName("周伯通");
//                bleLockUser2.setUserId("01011");
//                bleLockUser2.getTimeScheduleInfo().setPermanent(true);
//                users.add(bleLockUser2);
//
//                adapter.setData(users);
//                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 删除成员
     */
    private void deleteLockUser(String userId) {
        tuyaLockDevice.removeProLockMember(userId, new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.i(Constant.TAG, "delete lock user success:" + result);
                Toast.makeText(getApplicationContext(), "delete lock user success: " + result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String code, String error) {
                Log.e(Constant.TAG, "delete lock user failed: code = " + code + "  message = " + error);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
