package com.tuya.lock.demo.user;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;
import com.tuya.lock.demo.R;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.lock.demo.utils.Utils;
import com.tuya.smart.home.sdk.bean.MemberBean;
import com.tuya.smart.home.sdk.bean.MemberWrapperBean;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.sdk.api.ITuyaDataCallback;
import com.tuya.smart.sdk.optimus.lock.bean.ble.MemberInfoBean;

import java.io.File;


/**
 * 添加成员
 */
public class UserAddActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private MemberInfoBean userBean;
    private int mFrom;

    private ImageView user_avatar;
    private EditText nameView;
    private EditText lockUserIdView;
    private EditText account_View;
    private EditText countryCode_View;
    private EditText invitationCode_View;
    private EditText role_View;

    private MemberWrapperBean.Builder memberWrapperBean;

    public static void startActivity(Context context, MemberInfoBean memberInfoBean,
                                     String devId, int from) {
        Intent intent = new Intent(context, UserAddActivity.class);
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
        setContentView(R.layout.activity_ble_user_add);

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

        memberWrapperBean = new MemberWrapperBean.Builder();


        /* *
         * 用户昵称
         */
        nameView = findViewById(R.id.nameView);
        nameView.setText(userBean.getNickName());

        /**
         * 头像
         */
        user_avatar = findViewById(R.id.user_avatar_file_content);
        if (!TextUtils.isEmpty(userBean.getAvatarUrl())) {
            Utils.showImageUrl(userBean.getAvatarUrl(), user_avatar);
        }
        user_avatar.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0x01);
        });

        /* *
         * 是否为家庭管理员
         */
        memberWrapperBean.setAdmin(false);
        RadioGroup user_type_wrap = findViewById(R.id.user_type_wrap);
        user_type_wrap.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.user_type_admin) {
                memberWrapperBean.setAdmin(true);
            } else {
                memberWrapperBean.setAdmin(false);
            }
        });

        /**
         * 家庭成员id
         */
        lockUserIdView = findViewById(R.id.user_id_view);
        lockUserIdView.setText(String.valueOf(userBean.getLockUserId()));

        /**
         * 受邀账号
         */
        account_View = findViewById(R.id.account_View);

        /**
         * 国家码
         */
        countryCode_View = findViewById(R.id.countryCode_View);

        /**
         * 邀请码
         */
        invitationCode_View = findViewById(R.id.invitationCode_View);

        /**
         * 账号类型
         */
        role_View = findViewById(R.id.role_View);

        /**
         * 是否需要受邀请者同意接受加入家庭邀请
         */
        memberWrapperBean.setAutoAccept(true);
        RadioGroup autoAccept_wrap = findViewById(R.id.autoAccept_wrap);
        autoAccept_wrap.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.autoAccept_yes) {
                memberWrapperBean.setAutoAccept(true);
            } else {
                memberWrapperBean.setAutoAccept(false);
            }
        });

        /**
         * 提交创建或更新
         */
        Button submitBtn = findViewById(R.id.edit_user_submit);
        submitBtn.setOnClickListener(v -> {
            if (mFrom == 1) {
                addLockUser();
            } else {
                Toast.makeText(getApplicationContext(), "暂不支持", Toast.LENGTH_SHORT).show();
            }
        });
        if (mFrom == 1) {
            submitBtn.setText("更新信息");
        } else {
            submitBtn.setText("添加用户");
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
    private void addLockUser() {
        memberWrapperBean.setNickName(nameView.getText().toString());
        memberWrapperBean.setNickName(lockUserIdView.getText().toString());
        memberWrapperBean.setAccount(account_View.getText().toString());
        memberWrapperBean.setCountryCode(countryCode_View.getText().toString());
        memberWrapperBean.setInvitationCode(invitationCode_View.getText().toString());
        memberWrapperBean.setRole(Integer.parseInt(role_View.getText().toString()));

        tuyaLockDevice.createProLockMember(memberWrapperBean.build(), new ITuyaDataCallback<MemberBean>() {
            @Override
            public void onSuccess(MemberBean result) {
                Log.i(Constant.TAG, "add lock user success");
                Toast.makeText(getApplicationContext(), "add lock user success", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String code, String message) {
                Log.e(Constant.TAG, "add lock user failed: code = " + code + "  message = " + message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && null != data) {
            // 选择到图片的uri
            Uri uri = data.getData();

            // 第一种方式，使用文件路径创建图片
            // 文件路径的列
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // 获取图片的游标
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            // 获取列的指针
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // 根据指针获取图片路径
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            File avatarFile = new File(picturePath);


            memberWrapperBean.setHeadPic(picturePath);

            // 使用地址获取图片
            Bitmap bitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
            user_avatar.post(() -> user_avatar.setImageBitmap(bitmap));


        }
    }
}
