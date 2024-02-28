package com.thing.smart.sweeper;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
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
 * create by nielev on 2023/2/22
 */
public class P2pConnectActivity extends AppCompatActivity {

    private IThingSweeperP2P mSweeperP2P;

    private SpannableStringBuilder stringBuilder;

    private TextView tvP2pConnectShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_connect);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        stringBuilder = new SpannableStringBuilder();
        tvP2pConnectShow = findViewById(R.id.tvP2pConnectShow);
        findViewById(R.id.btnStopP2PData).setVisibility(View.GONE);

        //get device id
        String devId = getIntent().getStringExtra("deviceId");

        //get sweeperp2p
        IThingSweeperKitSdk iThingSweeperKitSdk = ThingOptimusSdk.getManager(IThingSweeperKitSdk.class);
        if (null != iThingSweeperKitSdk) {
            mSweeperP2P = iThingSweeperKitSdk.getSweeperP2PInstance(devId);

            //p2p connect
            findViewById(R.id.btnStartConnectP2pStep).setOnClickListener(v -> {
                        long startConnectTime = System.currentTimeMillis();
                        showText("onClick connectDeviceByP2P");
                        mSweeperP2P.connectDeviceByP2P(new SweeperP2PCallback() {
                            @Override
                            public void onSuccess() {
                                long endConnectTime = System.currentTimeMillis() - startConnectTime;
                                showText(getString(R.string.p2p_connect_status) + " true, time:" + endConnectTime);
                                //p2p connect suc, start get Sweeper data
                                mSweeperP2P.startObserverSweeperDataByP2P(DownloadType.P2PDownloadTypeStill, new SweeperP2PCallback() {
                                    @Override
                                    public void onSuccess() {
                                        //start suc
                                        showText(getString(R.string.p2p_download_data_status) + " true");

                                        findViewById(R.id.btnStopP2PData).setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onFailure(int i) {
                                        //start failure
                                        showText(getString(R.string.p2p_download_data_status) + " false");
                                    }
                                }, new SweeperP2PDataCallback() {
                                    @Override
                                    public void receiveData(int i, @Nullable SweeperP2PBean sweeperP2PBean) {
                                        if (null != sweeperP2PBean && null != sweeperP2PBean.getData()) {
                                            //get Data
                                            showText("receiveData index:" + i + ", dataSize:" + sweeperP2PBean.getData().length());
                                        }
                                    }

                                    @Override
                                    public void onFailure(int i) {
                                        showText("onFailure code:" + i);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i) {
                                long endConnectTime = System.currentTimeMillis() - startConnectTime;
                                showText(getString(R.string.p2p_connect_status) + " onFailure code:" + i + ", time:" + endConnectTime);
                            }
                        });
                    }
            );

            findViewById(R.id.btnStopP2PData).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long stopTime = System.currentTimeMillis();
                    showText("onClick stopObserverSweeperDataByP2P");
                    mSweeperP2P.stopObserverSweeperDataByP2P(new SweeperP2PCallback() {
                        @Override
                        public void onSuccess() {
                            long endTime = System.currentTimeMillis() - stopTime;
                            //stop suc
                            showText("stopDataByP2P onSuccess time:" + endTime);
                        }

                        @Override
                        public void onFailure(int i) {
                            long endTime = System.currentTimeMillis() - stopTime;
                            //stop suc
                            showText("stopDataByP2P onFailure code:" + i + ", time:" + endTime);
                        }
                    });
                }
            });
        }

    }

    private void showText(String tips) {
        stringBuilder.append(tips);
        stringBuilder.append("\n");
        tvP2pConnectShow.post(new Runnable() {
            @Override
            public void run() {
                tvP2pConnectShow.setText(stringBuilder);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //p2p stop
        if (null != mSweeperP2P) mSweeperP2P.onDestroyP2P();
    }


}