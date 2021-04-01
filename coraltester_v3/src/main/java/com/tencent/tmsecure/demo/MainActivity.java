package com.tencent.tmsecure.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.moji.mjweather.R;
import com.tencent.ep.common.adapt.BuildConfig;
import com.tencent.tmsecure.demo.ad_v3.CoralBanner;
import com.tencent.tmsecure.demo.ad_v3.CoralCard;
import com.tencent.tmsecure.demo.ad_v3.CoralDownload;
import com.tencent.tmsecure.demo.ad_v3.CoralFullScreenVideo;
import com.tencent.tmsecure.demo.ad_v3.CoralInfoFeed;
import com.tencent.tmsecure.demo.ad_v3.CoralRewardVideo;
import com.tencent.tmsecure.demo.ad_v3.CoralSplashImage;
import com.tencent.tmsecure.demo.ad_v3.CoralVideoFeed;
import com.tencent.tmsecure.demo.recorder.ExcelData;
import com.tencent.tmsecure.demo.recorder.Recorder;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tencent.tmsecure.demo.util.DeviceUtil;
import com.tmsdk.module.coin.TMSDKContext;
import com.tz.sdk.core.engine.ADEngine;
import com.tz.sdk.core.engine.IADEngineState;

import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String guid, imei, sdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ADEngine.getInstance(MainActivity.this).getState() == IADEngineState.ENGINE_FAILED) {
            CommonUtil.showToast(this, "广告引擎初始化失败，请确认程序配置");
            finish();
            return;
        }
        CommonUtil.checkPermission(this);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        CommonUtil.showLoading(this);

        setTitle(String.format("%s v%s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
        findViewById(R.id.ll_102).setOnClickListener(this);
        findViewById(R.id.ll_103).setOnClickListener(this);
        findViewById(R.id.ll_134).setOnClickListener(this);
        findViewById(R.id.ll_104).setOnClickListener(this);
        findViewById(R.id.ll_131).setOnClickListener(this);
        findViewById(R.id.ll_132).setOnClickListener(this);
        findViewById(R.id.ll_125).setOnClickListener(this);
        findViewById(R.id.ll_130).setOnClickListener(this);
        findViewById(R.id.ll_128).setOnClickListener(this);
        findViewById(R.id.ll_137).setOnClickListener(this);
        findViewById(R.id.ll_138).setOnClickListener(this);

        ViewGroup recordContainer = findViewById(R.id.fl_record);
        if (recordContainer.getChildAt(0) != null) {
            recordContainer.removeAllViews();
        }
        View controllerView = Recorder.getInstance(this).getControllerView();
        if (controllerView.getParent() != null) {
            ((ViewGroup) controllerView.getParent()).removeView(controllerView);
        }
        recordContainer.addView(controllerView);
        if (!TextUtils.isEmpty(TMSDKContext.getGUID())) {
            getDeviceInfo();
        } else {
            findViewById(R.id.guid).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDeviceInfo();
                }
            }, 2000);
        }
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        guid = TMSDKContext.getGUID();
        imei = DeviceUtil.getDeviceId(MainActivity.this);
        sdk = TMSDKContext.getSDKVersionInfo();
        ((TextView) findViewById(R.id.guid)).setText("guid:    " + guid);
        ((TextView) findViewById(R.id.imei)).setText("imei:    " + imei);
        ((TextView) findViewById(R.id.sdk)).setText(" sdk:    " + sdk);
        View btnRetry = findViewById(R.id.tv_retry);
        if (TextUtils.isEmpty(guid)) {
            btnRetry.setVisibility(View.VISIBLE);
            btnRetry.setOnClickListener(this);
        }
        Recorder.getInstance(this)
                .environment("测试")
                .guid(guid)
                .imei(imei)
                .sdk(sdk)
                .excel(new Recorder.IExcelListener() {
                    @Override
                    public void onExcelExported(String file, ExcelData excelData) {
                        MainActivity.this.startActivity(new Intent(MainActivity.this,
                                ExcelPreviewActivity.class).putExtra("file", file)
                                .putExtra("data", excelData));
                    }
                });
        CommonUtil.hideLoading();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_retry://重新获取guid
                CommonUtil.showLoading(this);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtil.hideLoading();
                        guid = TMSDKContext.getGUID();
                        if (!TextUtils.isEmpty(guid)) {
                            ((TextView) findViewById(R.id.guid)).setText("guid:    " + guid);
                            Recorder.getInstance(v.getContext()).guid(guid);
                            v.setVisibility(View.GONE);
                        } else {
                            CommonUtil.showToast(v.getContext(), "获取guid失败，请稍后重试");
                        }
                    }
                }, 1000);
                break;
            case R.id.ll_102://卡券
                new CoralCard(this, 102).pull();
                break;
            case R.id.ll_103://下载
            case R.id.ll_134://下载（广点通)
                new CoralDownload(this, v.getId() == R.id.ll_103 ? 103 : 134).pull();
                break;
            case R.id.ll_104://视频
            case R.id.ll_131://视频（广点通)
            case R.id.ll_132://视频（快手)
                new CoralRewardVideo(this, v.getId() == R.id.ll_104 ?
                        104 : v.getId() == R.id.ll_131 ?
                        131 : 132).pull();
                break;
            case R.id.ll_125://开屏
                final ViewGroup splashPage = findViewById(R.id.fl_splash_container);
                splashPage.setVisibility(View.VISIBLE);
                final ViewGroup container = findViewById(R.id.fl_splash_content);
                final TextView skipView = findViewById(R.id.tv_skip);
                new CoralSplashImage(this, 125)
                        .container(container)
                        .skip(skipView)
                        .listen(new CoralSplashImage.ISplashImageListener() {
                            @Override
                            public void onAdError() {
                                splashPage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdShow() {
//                                skipView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onTimeTick(long msLeft) {
                                skipView.setText("跳过:" + msLeft / 1000);

                            }

                            @Override
                            public void onTimesUp() {
                                if (container.getChildAt(0) != null) {
                                    container.removeAllViews();
                                }
                                skipView.setText("跳过:");
                                splashPage.setVisibility(View.GONE);
                            }
                        }).pull();
                break;
            case R.id.ll_130://banner
                new CoralBanner(this, 130)
                        .container(findViewById(R.id.fl_banner))
                        .onLoad(new CoralBanner.IBannerViewLoadListener() {
                            @Override
                            public void onBannerLoaded(View bannerView) {
//                                ViewGroup bannerContainer = findViewById(R.id.fl_banner);
//                                if (bannerContainer.getChildAt(0) != null) {
//                                    bannerContainer.removeAllViews();
//                                }
//                                if (bannerView != null)
//                                    bannerContainer.addView(bannerView);
                            }
                        }).pull();
                break;
            case R.id.ll_128://信息流
                new CoralInfoFeed(this, 128).pull();
                break;
            case R.id.ll_137://全屏视频
                new CoralFullScreenVideo(this, 137).pull();
                break;
            case R.id.ll_138://内容联盟
                new CoralVideoFeed(this, 138).container(R.id.fl_video_feed).pull();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }
        if (findViewById(R.id.fl_splash_container).getVisibility() == View.VISIBLE) return;
        if (CommonUtil.doubleClickToQuit(this)) {
            if (Recorder.getInstance(this).isRecording()) {
                new AlertDialog.Builder(this)
                        .setTitle("退出")
                        .setMessage("正在录制，确定继续退出？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                initView();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("授权")
                        .setMessage("请通过授权，保证程序正常使用")
                        .setPositiveButton("确定", (dialog, which) -> CommonUtil.checkPermission(MainActivity.this))
                        .show();
            }
        }
    }
}
