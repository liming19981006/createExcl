package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.moji.mjweather.R;
import com.tencent.ep.shanhuad.BuildConfig;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralADListener;
import com.tz.sdk.coral.callback.h5.DownloadProcess;
import com.tz.sdk.coral.task.RewardTask;
import com.tz.sdk.coral.task.TaskResult;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 下载
 * Created by wanghl on 2020/6/18
 */
public final class CoralDownload extends CoralBase {

    public CoralDownload(Context context, int taskType) {
        super(context, taskType);
    }
    public boolean i =true;
    /**
    i用于判断如果已经在下载了，那就在download回调里return false，不进行重复的下载
     */

    @Override
    public com.tencent.tmsecure.demo.recorder.ADType getADType() {
        return com.tencent.tmsecure.demo.recorder.ADType.CoralDownload;
    }

    @Override
    public int getAdContentLayoutId() {
        return R.layout.dialog_download;
    }

    @Override
    public void pull() {
        super.pull();
        new ADLoader(mContext)
                .get(ADType.APP_DOWNLOAD)
                .from(ADSource.CORAL)
                .count(1)
                .reward(true)
                .with(new HashMap<String, Object>() {
                    {
                        put(CoralAD.Key.TASK_TYPE, mTaskType);
                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
                    }
                })
                .load(new CoralADListener() {
                    @Override
                    public boolean onTaskAvailable(RewardTask rewardTask) {
                        return super.onTaskAvailable(rewardTask);
                    }

                    @Override
                    public boolean onTaskNotAvailable(int taskType, ADError adError) {
                        CommonUtil.hideLoading();
                        return super.onTaskNotAvailable(taskType, adError);
                    }

                    @Override
                    public boolean openH5(CoralAD ad, String h5Url) {
                        return super.openH5(ad, h5Url);
                    }

                    @Override
                    public boolean download(DownloadProcess downloadProcess) {
                        if(!i){
                            return false;
                        }
                        return super.download(downloadProcess);
                    }

                    @Override
                    public void onAdFailed(ADError adError) {
                        super.onAdFailed(adError);
                        if (BuildConfig.DEBUG) {
                            Log.e("CoralDownload", adError.toString());
                        }
                        whenAdError(adError.toString());
                    }

                    @Override
                    public void onAdLoaded(List<CoralAD> adList) {
                        if (adList != null && adList.size() > 0) {
                            whenAdLoaded(adList.get(0));
                        } else {
                            whenAdError("下载类(" + mTaskType + ")广告返回数量0");
                        }
                    }

                    @Override
                    public boolean onAdShow(@Nullable CoralAD ad) {
                        whenAdShow();
                        return false;
                    }

                    @Override
                    public boolean onAdClicked(@Nullable CoralAD ad) {
                        whenAdClicked();
                        return false;
                    }

                    @Override
                    public boolean onAppDownloading(@Nullable CoralAD ad, @Nullable String downloadUrl) {
                        whenAppDownloadEvent(ADEvent.Download_Start, "下载");
                        i = false;
                        return false;
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "下载");
                        i =true;
                        return false;
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "下载");
                        return false;
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "下载");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }
                });
    }
}
