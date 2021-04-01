package com.tencent.tmsecure.demo;

import android.app.Application;
import android.util.Log;

import com.moji.mjweather.BuildConfig;
import com.tencent.ep.shanhuad.adpublic.ShanHuAD;
import com.tencent.tmsecure.demo.callback.H5Browser;
import com.tmsdk.module.coin.AbsTMSConfig;
import com.tmsdk.module.coin.TMSDKContext;
import com.tz.sdk.core.engine.ADEngine;
import com.tz.sdk.core.engine.ADEngineConfig;
import com.tz.sdk.core.engine.IADEngineState;

/**
 * 自定义App
 * Created by wanghl on 2020/6/9
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        iniADEngine();
    }

    private void iniADEngine() {
        ADEngine.getInstance(this)
                .start(new ADEngineConfig.Builder(this)
                                .log(true)
                                .verbose(true)
                                .forTest(true)
                                .build(),
                        new IADEngineState() {
                            @Override
                            public void onIdle() {

                            }

                            @Override
                            public void onStarting() {

                            }

                            @Override
                            public void onStarted() {
                                if (BuildConfig.DEBUG) {
                                    Log.d("MyApplication", "ADEngine started");
                                }
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                if (BuildConfig.DEBUG) {
                                    Log.e("MyApplication", "ADEngine failed: code=" + code + ",msg=" + msg);
                                }
                            }
                        });
    }

}
