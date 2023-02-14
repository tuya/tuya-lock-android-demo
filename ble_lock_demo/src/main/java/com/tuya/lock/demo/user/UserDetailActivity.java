package com.tuya.lock.demo.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.utils.Utils;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.optimus.lock.bean.ble.MemberInfoBean;


/**
 * 添加成员
 */
public class UserDetailActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private MemberInfoBean userBean;
    private int mFrom;

    public static void startActivity(Context context, MemberInfoBean memberInfoBean,
                                     String devId, int from) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        //设备id
        intent.putExtra(Constant.DEVICE_ID, devId);
        //创建还是编辑
        intent.putExtra(Constant.FROM, from);
        //编辑的密码数据
        intent.putExtra(Constant.USER_DATA, JSONObject.toJSONString(memberInfoBean));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_user_edit);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String userData = getIntent().getStringExtra(Constant.USER_DATA);
        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        mFrom = getIntent().getIntExtra(Constant.FROM, 0);
        try {
            userBean = JSONObject.parseObject(userData, MemberInfoBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (null == userBean) {
            userBean = new MemberInfoBean();
        }

        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(userBean.getNickName());

        TextView lockUserIdView = findViewById(R.id.lockUserIdView);
        lockUserIdView.setText(String.valueOf(userBean.getLockUserId()));

        TextView userTypeView = findViewById(R.id.userTypeView);
        userTypeView.setText(String.valueOf(userBean.getUserType()));

        TextView userContactView = findViewById(R.id.userContactView);
        userContactView.setText(userBean.getUserContact());

        TextView userIdView = findViewById(R.id.userIdView);
        userIdView.setText(String.valueOf(userBean.getUserId()));

        TextView unlockDetailView = findViewById(R.id.unlockDetailView);
        unlockDetailView.setText(JSONArray.toJSONString(userBean.getUnlockDetail()));


        RadioButton user_unlock_permanent_yes = findViewById(R.id.user_unlock_permanent_yes);
        RadioButton user_unlock_permanent_no = findViewById(R.id.user_unlock_permanent_no);
        if (userBean.getTimeScheduleInfo().isPermanent()) {
            user_unlock_permanent_yes.setChecked(true);
        } else {
            user_unlock_permanent_no.setChecked(true);
        }

        RadioGroup user_unlock_permanent = findViewById(R.id.user_unlock_permanent);
        user_unlock_permanent.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.user_unlock_permanent_yes) {
                userBean.getTimeScheduleInfo().setPermanent(true);
            } else if (checkedId == R.id.user_unlock_permanent_no) {
                userBean.getTimeScheduleInfo().setPermanent(false);
            }
            showTimeMain(userBean.getTimeScheduleInfo().isPermanent());
        });
        showTimeMain(userBean.getTimeScheduleInfo().isPermanent());

        EditText user_effective_timestamp_content = findViewById(R.id.user_effective_timestamp_content);
        user_effective_timestamp_content.setText(Utils.getDateDay(userBean.getTimeScheduleInfo().getEffectiveTime(), "yyyy-MM-dd HH:mm:ss"));
        user_effective_timestamp_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    userBean.getTimeScheduleInfo().setEffectiveTime(Utils.getStampTime(s.toString(), "yyyy-MM-dd HH:mm:ss"));
                    Log.i(Constant.TAG, "effectiveTimestamp select:" + userBean.getTimeScheduleInfo().getEffectiveTime());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText user_invalid_timestamp_content = findViewById(R.id.user_invalid_timestamp_content);
        user_invalid_timestamp_content.setText(Utils.getDateDay(userBean.getTimeScheduleInfo().getExpiredTime(), "yyyy-MM-dd HH:mm:ss"));
        user_invalid_timestamp_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    userBean.getTimeScheduleInfo().setExpiredTime(Utils.getStampTime(s.toString(), "yyyy-MM-dd HH:mm:ss"));
                    Log.i(Constant.TAG, "invalidTimestamp select:" + userBean.getTimeScheduleInfo().getExpiredTime());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView user_avatar = findViewById(R.id.user_avatar_file_content);
        if (!TextUtils.isEmpty(userBean.getAvatarUrl())) {
            Utils.showImageUrl(userBean.getAvatarUrl(), user_avatar);
        }

        Button submitBtn = findViewById(R.id.edit_user_submit);
        submitBtn.setOnClickListener(v -> {
            if (mFrom == 1) {
                updateLockUser();
            } else {
                Toast.makeText(getApplicationContext(), "暂不支持", Toast.LENGTH_SHORT).show();
            }
        });
        if (mFrom == 1) {
            submitBtn.setText("更新时效");
        } else {
            submitBtn.setText("添加用户");
        }
    }

    private void showTimeMain(boolean hide) {
        View user_time_main = findViewById(R.id.user_time_main);
        if (hide) {
            user_time_main.setVisibility(View.GONE);
        } else {
            user_time_main.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    /**
     * 用户更新信息
     */
    private void updateLockUser() {
        tuyaLockDevice.updateProLockMemberTime(userBean.getUserId(), userBean.getTimeScheduleInfo(), new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.i(Constant.TAG, "update lock user time success");
                Toast.makeText(getApplicationContext(), "add lock user success", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "update lock user time failed: code = " + code + "  message = " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
