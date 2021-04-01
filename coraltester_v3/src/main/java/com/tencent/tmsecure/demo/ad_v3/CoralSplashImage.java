package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralSplashImageListener;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 开屏（图片）
 * Created by wanghl on 2020/6/18
 */
public final class CoralSplashImage extends CoralBase {
    private ViewGroup mContainer;
    private View mSkipView;
    private ISplashImageListener mISplashImageListener;

    public CoralSplashImage(Context context, int taskType) {
        super(context, taskType);
    }

    public CoralSplashImage container(ViewGroup container) {
        mContainer = container;
        return this;
    }

    public CoralSplashImage skip(View skipView) {
        mSkipView = skipView;
        return this;
    }

    public CoralSplashImage listen(ISplashImageListener splashImageListener) {
        mISplashImageListener = splashImageListener;
        return this;
    }

    @Override
    public void pull() {
        super.pull();
        new ADLoader(mContext)
                .get(ADType.SPLASH_IMAGE)
                .from(ADSource.CORAL)
                .count(1)
                .reward(true)
                .with(new HashMap<String, Object>() {
                    {
                        put(CoralAD.Key.TASK_TYPE, mTaskType);
                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
                        put(CoralAD.Key.SPLASH_IMAGE_CONTAINER_VIEW_GROUP, mContainer);
                        put(CoralAD.Key.SPLASH_IMAGE_SKIP_VIEW, mSkipView);
                    }
                })
                .load(new CoralSplashImageListener() {
                    @Override
                    public void onAdFailed(ADError adError) {
                        super.onAdFailed(adError);
                        whenAdError(adError.toString());
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mISplashImageListener != null) {
                                    mISplashImageListener.onAdError();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdLoaded(@Nullable List<CoralAD> adList) {
                        super.onAdLoaded(adList);
                        whenAdLoaded(null);
                    }

                    @Override
                    public boolean onAdShow(@Nullable CoralAD ad) {
                        whenAdShow();
                        return super.onAdShow(ad);
                    }

                    @Override
                    public boolean onAdClicked(@Nullable CoralAD ad) {
                        whenAdClicked();
//                        return super.onAdClicked(ad);
                        return true;
                    }

                    @Override
                    public boolean onAppDownloading(@Nullable CoralAD ad, @Nullable String downloadUrl) {
                        whenAppDownloadEvent(ADEvent.Download_Start, "开屏");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "开屏");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "开屏");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "开屏");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }

                    @Override
                    public void onAdTimeTick(final long millisecondLeft) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mISplashImageListener != null) {
                                    mISplashImageListener.onTimeTick(millisecondLeft);
                                }
                            }
                        });
                    }

                    @Override
                    public boolean onAdTimesUp() {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mISplashImageListener != null) {
                                    mISplashImageListener.onTimesUp();
                                }
                            }
                        });
                        return super.onAdTimesUp();
                    }
                });
    }

    @Override
    protected void showAd(CoralAD ad) {
        if (mISplashImageListener != null) {
            mISplashImageListener.onAdShow();
        }
    }

    public interface ISplashImageListener {
        /**
         * 广告加载失败
         */
        void onAdError();

        /**
         * 广告显示
         */
        void onAdShow();

        /**
         * 倒计时
         *
         * @param msLeft
         */
        void onTimeTick(long msLeft);

        /**
         * 倒计时结束
         */
        void onTimesUp();
    }
}
