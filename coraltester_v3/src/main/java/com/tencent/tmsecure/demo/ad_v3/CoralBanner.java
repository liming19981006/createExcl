package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tencent.ep.common.adapt.BuildConfig;
import com.tencent.ep.shanhuad.adpublic.adbuilder.ADBanner;
import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralBannerListener;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;

/**
 * 横幅
 * Created by wanghl on 2020/6/18
 */
public final class CoralBanner extends CoralBase {
    private ADBanner mADBanner;
    private IBannerViewLoadListener mIBannerViewLoadListener;
    private View mBannerView;
    private ViewGroup mContainer;

    public CoralBanner(Context context, int taskType) {
        super(context, taskType);
    }

    public CoralBanner container(ViewGroup container) {
        mContainer = container;
        return this;
    }

    /**
     * 设置view加载监听
     *
     * @param bannerViewLoaded
     * @return
     */
    public CoralBase onLoad(IBannerViewLoadListener bannerViewLoaded) {
        mIBannerViewLoadListener = bannerViewLoaded;
        return this;
    }

    @Override
    public void pull() {
        super.pull();
        new ADLoader(mContext)
                .get(ADType.BANNER)
                .from(ADSource.CORAL)
                .count(1)
                .reward(true)
                .with(new HashMap<String, Object>() {
                    {
                        put(CoralAD.Key.TASK_TYPE, mTaskType);
                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
                        put(CoralAD.Key.BANNER_CONTAINER_VIEW_GROUP, mContainer);
                        put(CoralAD.Key.BANNER_REFRESH_TIME, 30);
                    }
                })
                .load(new CoralBannerListener() {
                    @Override
                    public void onAdLoaded(View view) {
                        mBannerView = view;
                        whenAdLoaded(null);
                    }

                    @Override
                    public void onAdFailed(ADError adError) {
                        super.onAdFailed(adError);
                        if (BuildConfig.DEBUG) {
                            Log.e("CoralBanner", adError.toString());
                        }
                        whenAdError(adError.toString());
                    }

                    @Override
                    public boolean onAdShow(@Nullable CoralAD ad) {
                        whenAdShow();
                        return super.onAdShow(ad);
                    }

                    @Override
                    public boolean onAdClicked(@Nullable CoralAD ad) {
                        whenAdClicked();
                        return super.onAdClicked(ad);
                    }

                    @Override
                    public boolean onAppDownloading(@Nullable CoralAD ad, @Nullable String downloadUrl) {
                        whenAppDownloadEvent(ADEvent.Download_Start, "横幅");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "横幅");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "横幅");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "横幅");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }
                });
    }

    @Override
    protected void showAd(CoralAD ad) {
        if (mIBannerViewLoadListener != null) {
            mIBannerViewLoadListener.onBannerLoaded(mBannerView);
        }
    }

    /**
     * view加载完成
     */
    public interface IBannerViewLoadListener {
        void onBannerLoaded(View bannerView);
    }
}
