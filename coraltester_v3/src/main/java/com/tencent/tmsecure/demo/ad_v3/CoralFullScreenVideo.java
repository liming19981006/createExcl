package com.tencent.tmsecure.demo.ad_v3;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralVideoListener;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 全屏视频
 * Created by wanghl on 2020/7/7
 */
public final class CoralFullScreenVideo extends CoralBase {
    public CoralFullScreenVideo(Context context, int taskType) {
        super(context, taskType);
    }

    @Override
    public void pull() {
        super.pull();

        new ADLoader(mContext)
                .get(ADType.FULL_SCREEN_VIDEO)
                .from(ADSource.CORAL)
                .reward(true)
                .with(new HashMap<String, Object>() {
                    {
                        put(CoralAD.Key.TASK_TYPE, mTaskType);
                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
                    }
                })
                .load(new CoralVideoListener() {
                    @Override
                    public void onAdLoaded(List<CoralAD> adList) {
                        if (adList != null && adList.size() > 0) {
                            whenAdLoaded(adList.get(0));
                        } else {
                            whenAdError("视频类(" + mTaskType + ")广告返回数量0");
                        }
                    }

                    @Override
                    public void onAdFailed(ADError adError) {
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
                        whenAppDownloadEvent(ADEvent.Download_Start, "全屏视频");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "全屏视频");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "全屏视频");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "全屏视频");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onVideoFinished(@Nullable CoralAD coralAD, @Nullable String s) {
                        return super.onVideoFinished(coralAD, s);
                    }

                    @Override
                    public boolean onVideoClosed(@Nullable CoralAD coralAD, @Nullable String s) {
                        return super.onVideoClosed(coralAD, s);
                    }

                    @Override
                    public void onVideoSkipped(@Nullable CoralAD ad, @Nullable String videoUrl) {
                        super.onVideoSkipped(ad, videoUrl);
                    }
                });
    }

    @Override
    protected void showAd(CoralAD ad) {
//        ad.playVideo((Activity) mContext);
    }
}
