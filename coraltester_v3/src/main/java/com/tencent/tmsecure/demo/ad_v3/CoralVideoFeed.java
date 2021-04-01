package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralVideoFeedListener;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;

/**
 * 内容联盟
 * Created by wanghl on 2020/7/7
 */
public final class CoralVideoFeed extends CoralBase {
    private int mContainerId;
    private Fragment mFeedFragment;

    public CoralVideoFeed(Context context, int taskType) {
        super(context, taskType);
    }

    public CoralVideoFeed container(int containerId) {
        mContainerId = containerId;
        return this;
    }

    @Override
    public void pull() {
        super.pull();

        new ADLoader(mContext)
                .get(ADType.VIDEO_FEED)
                .from(ADSource.CORAL)
//                .reward(true)
//                .with(new HashMap<String, Object>() {
//                    {
//                        put(CoralAD.Key.TASK_TYPE, mTaskType);
//                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
//                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
//                    }
//                })
                .load(new CoralVideoFeedListener() {
                    @Override
                    public void onAdLoaded(@Nullable Fragment fragment) {
                        mFeedFragment = fragment;
                        whenAdLoaded(null);
                    }

                    @Override
                    public void onAdFailed(ADError adError) {
                        whenAdError(adError.toString());
                        super.onAdFailed(adError);
                    }

                    @Override
                    public boolean onAdShow(@Nullable CoralAD ad) {
                        whenAdShow();
                        return super.onAdShow(ad);
                    }

                    @Override
                    public boolean onAppDownloading(@Nullable CoralAD ad, @Nullable String downloadUrl) {
                        whenAppDownloadEvent(ADEvent.Download_Start, "内容联盟");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "内容联盟");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "内容联盟");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "内容联盟");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }

                    @Override
                    public void onPageEnter(com.tz.sdk.coral.ad.type.CoralVideoFeed.Item item) {
                        super.onPageEnter(item);
                    }

                });
    }

    @Override
    protected void showAd(CoralAD ad) {
        if (mContext != null
                && mContext instanceof AppCompatActivity
                && mContainerId != 0
                && mFeedFragment != null) {
            ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .replace(mContainerId, mFeedFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}
