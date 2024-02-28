package com.thing.smart.sweeper;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thingclips.smart.optimus.sdk.ThingOptimusSdk;
import com.thingclips.smart.optimus.sweeper.api.IThingSweeperKitSdk;
import com.thingclips.smart.optimus.sweeper.api.IThingSweeperP2P;
import com.thingclips.smart.sweepe.p2p.bean.SweeperP2PBean;
import com.thingclips.smart.sweepe.p2p.callback.SweeperP2PCallback;
import com.thingclips.smart.sweepe.p2p.callback.SweeperP2PDataCallback;
import com.thingclips.smart.sweepe.p2p.manager.DownloadType;


/**
 * Created by HuiYao on 2023/12/13
 */
public class KTP2pConnectActivity extends AppCompatActivity {

    private IThingSweeperP2P mSweeperP2P;

    private SpannableStringBuilder stringBuilder;

    private TextView tvP2pConnectShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kt_p2p_connect);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvP2pConnectShow = findViewById(R.id.tvP2pConnectShow);

        //get device id
        String devId = getIntent().getStringExtra("deviceId");

        showText("设备ID: " + devId);

        //初始化
        IThingSweeperKitSdk iThingSweeperKitSdk = ThingOptimusSdk.getManager(IThingSweeperKitSdk.class);
        if (null != iThingSweeperKitSdk) {
            mSweeperP2P = iThingSweeperKitSdk.getSweeperP2PInstance(devId);
            mSweeperP2P.stopReconnect(true);
            showText("初始化");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mSweeperP2P) {
            if (!mSweeperP2P.checkP2PActive()) {
                showText("未链接状态，开始链接");
                connectDevice();
            } else {
                showText("已链接状态，保持不变");
            }
        }
    }

    private void connectDevice() {
        long startConnectTime = System.currentTimeMillis();
        mSweeperP2P.connectDeviceByP2P(new SweeperP2PCallback() {
            @Override
            public void onSuccess() {
                long endConnectTime = System.currentTimeMillis() - startConnectTime;
                showText("P2P通道已链接，链接时间:" + endConnectTime);
                startObserverSweeperDataByP2P();
            }

            @Override
            public void onFailure(int i) {
                showText("链接失败 错误码：" + i);
                if (i == -3) {
                    long startConnectTime = System.currentTimeMillis();
                    showText("P2P通道开始执行二次链接");
                    mSweeperP2P.connectDeviceByP2P(new SweeperP2PCallback() {
                        @Override
                        public void onSuccess() {
                            long endConnectTime = System.currentTimeMillis() - startConnectTime;
                            showText("P2P通道重连成功，链接时间:" + endConnectTime);
                            startObserverSweeperDataByP2P();
                        }

                        @Override
                        public void onFailure(int i) {
                            showText("二次链接失败 错误码：" + i);
                        }
                    });
                }
            }
        });
    }

    private void startObserverSweeperDataByP2P() {
        mSweeperP2P.startObserverSweeperDataByP2P(DownloadType.P2PDownloadTypeStill,
                new SweeperP2PCallback() {
                    @Override
                    public void onSuccess() {
                        showText("p2p数据下载状态开启成功");
                    }

                    @Override
                    public void onFailure(int i) {
                        showText("p2p数据下载状态开启失败，错误码：" + i);
                    }
                }, new SweeperP2PDataCallback() {
                    @Override
                    public void receiveData(int i, SweeperP2PBean sweeperP2PBean) {
                        if (null != sweeperP2PBean && null != sweeperP2PBean.getData()) {
                            //get Data
                            showText("接收扫地机数据 index:" + i + ", dataSize:" + sweeperP2PBean.getData().length());
                        }
                    }

                    @Override
                    public void onFailure(int i) {
                        showText("扫地机数据获取失败 错误码：" + i);
                    }
                });
    }

    private void showText(String tips) {
        if (null == stringBuilder) {
            stringBuilder = new SpannableStringBuilder();
        }
        stringBuilder.append(tips);
        stringBuilder.append("\n");
        tvP2pConnectShow.post(() -> tvP2pConnectShow.setText(stringBuilder));
    }

    @Override
    protected void onDestroy() {
        //p2p stop
        if (null != mSweeperP2P) {
            mSweeperP2P.stopObserverSweeperDataByP2P(new SweeperP2PCallback() {
                @Override
                public void onSuccess() {
                    mSweeperP2P.onDestroyP2P();
                }

                @Override
                public void onFailure(int i) {
                    mSweeperP2P.onDestroyP2P();
                }
            });
        }
        super.onDestroy();
    }

}
