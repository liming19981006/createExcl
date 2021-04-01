package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.moji.mjweather.R;
import com.tencent.ep.common.adapt.BuildConfig;
import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralADListener;
import com.tz.sdk.coral.task.RewardTask;
import com.tz.sdk.core.ad.ADError;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 卡券
 * Created by wanghl on 2020/6/16
 */
public final class CoralCard extends CoralBase {

    public CoralCard(Context context, int taskType) {
        super(context, taskType);
    }

    @Override
    public com.tencent.tmsecure.demo.recorder.ADType getADType() {
        return com.tencent.tmsecure.demo.recorder.ADType.CoralCard;
    }

    @Override
    public int getAdContentLayoutId() {
        return R.layout.dialog_card;
    }

    @Override
    public void pull() {
        super.pull();
        new ADLoader(mContext)
                .get(ADType.CARD)
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
                    public boolean onTaskNotAvailable(int taskType, ADError adError) {
                        if (BuildConfig.DEBUG) {
                            Log.e("CoralCard", adError.toString());
                        }
                        return super.onTaskNotAvailable(taskType, adError);
                    }

                    @Override
                    public boolean onTaskAvailable(RewardTask rewardTask) {
                        if (BuildConfig.DEBUG) {
                            Log.d("CoralCard", rewardTask.toString());
                        }
                        return super.onTaskAvailable(rewardTask);
                    }

                    @Override
                    public void onAdFailed(ADError adError) {
                        super.onAdFailed(adError);
                        if (BuildConfig.DEBUG) {
                            Log.e("CoralCard", adError.toString());
                        }
                        whenAdError(adError.toString());
                    }

                    @Override
                    public void onAdLoaded(List<CoralAD> adList) {
                        if (adList != null && adList.size() > 0) {
                            whenAdLoaded(adList.get(0));
                        } else {
                            whenAdError("卡券类(" + mTaskType + ")广告返回数量0");
                        }
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
                        whenAppDownloadEvent(ADEvent.Download_Start, "卡券");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "卡券");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "卡券");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "卡券");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }
                });
    }
}
