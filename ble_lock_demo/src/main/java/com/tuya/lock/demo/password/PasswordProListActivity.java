package com.tuya.lock.demo.password;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.lock.demo.R;
import com.tuya.lock.demo.adapter.PasswordProOfflineListAdapter;
import com.tuya.lock.demo.constant.Constant;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.optimus.lock.api.ITuyaBleLockV2;
import com.tuya.smart.optimus.lock.api.ITuyaLockManager;
import com.tuya.smart.optimus.lock.api.bean.ProTempPasswordItem;
import com.tuya.smart.optimus.lock.api.enums.ProPasswordListTypeEnum;
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk;
import com.tuya.smart.scene.api.IResultCallback;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PasswordProListActivity extends AppCompatActivity {

    private ITuyaBleLockV2 tuyaLockDevice;
    private PasswordProOfflineListAdapter adapter;
    private final List<ProPasswordListTypeEnum> authTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_pro_offline_list);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String mDevId = getIntent().getStringExtra(Constant.DEVICE_ID);
        ITuyaLockManager tuyaLockManager = TuyaOptimusSdk.getManager(ITuyaLockManager.class);
        tuyaLockDevice = tuyaLockManager.getBleLockV2(mDevId);

        RecyclerView rvList = findViewById(R.id.password_list);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        authTypes.add(ProPasswordListTypeEnum.LOCK_BLUE_PASSWORD);
        authTypes.add(ProPasswordListTypeEnum.LOCK_OFFLINE_TEMP_PWD);
        authTypes.add(ProPasswordListTypeEnum.LOCK_TEMP_PWD);

        RadioGroup list_type_main = findViewById(R.id.list_type_main);
        list_type_main.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.list_type_all) {
                authTypes.clear();
                authTypes.add(ProPasswordListTypeEnum.LOCK_BLUE_PASSWORD);
                authTypes.add(ProPasswordListTypeEnum.LOCK_OFFLINE_TEMP_PWD);
                authTypes.add(ProPasswordListTypeEnum.LOCK_TEMP_PWD);
            } else if (checkedId == R.id.list_type_online) {
                authTypes.clear();
                authTypes.add(ProPasswordListTypeEnum.LOCK_TEMP_PWD);
            } else if (checkedId == R.id.list_type_offline) {
                authTypes.clear();
                authTypes.add(ProPasswordListTypeEnum.LOCK_OFFLINE_TEMP_PWD);
            } else if (checkedId == R.id.list_type_ble) {
                authTypes.clear();
                authTypes.add(ProPasswordListTypeEnum.LOCK_BLUE_PASSWORD);
            }
            getOfflineTempPasswordList();
        });

        adapter = new PasswordProOfflineListAdapter();
        adapter.setDevId(mDevId);
        adapter.delete(new IResultCallback<ProTempPasswordItem>() {
            @Override
            public void onSuccess(ProTempPasswordItem passwordItem) {
                tuyaLockDevice.deleteProOnlinePassword(passwordItem.getUnlockBindingId(), passwordItem.getSn(), new ITuyaResultCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.i(Constant.TAG, "deleteProOnlineTempPassword success: " + result);
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        Log.e(Constant.TAG, "deleteProOnlineTempPassword failed: code = " + errorCode + "  message = " + errorMessage);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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
        getOfflineTempPasswordList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tuyaLockDevice.onDestroy();
    }

    /**
     * 获取门锁成员
     */
    private void getOfflineTempPasswordList() {
        tuyaLockDevice.getProPasswordList(authTypes, new ITuyaResultCallback<ArrayList<ProTempPasswordItem>>() {
            @Override
            public void onSuccess(ArrayList<ProTempPasswordItem> result) {
                adapter.setData(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.e(Constant.TAG, "getProTempPasswordList failed: code = " + errorCode + "  message = " + errorMessage);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
