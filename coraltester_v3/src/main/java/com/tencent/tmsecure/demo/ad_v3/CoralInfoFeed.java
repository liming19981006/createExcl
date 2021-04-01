package com.tencent.tmsecure.demo.ad_v3;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import com.moji.mjweather.R;
import com.tencent.tmsecure.demo.util.DeviceUtil;
import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.coral.callback.CoralInfoFeedListener;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ad.ADSource;
import com.tz.sdk.core.ad.ADType;
import com.tz.sdk.core.loader.ADLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 信息流
 * Created by wanghl on 2020/7/7
 */
public final class CoralInfoFeed extends CoralBase {
    private List<View> mFeedViewList;

    public CoralInfoFeed(Context context, int taskType) {
        super(context, taskType);
    }

    @Override
    public int getAdContentLayoutId() {
        return R.layout.dialog_info_feed;
    }

    @Override
    public void pull() {
        super.pull();
        new ADLoader(mContext)
                .get(ADType.INFO_FEED)
                .from(ADSource.CORAL)
                .count(5)
                .reward(true)
                .with(new HashMap<String, Object>() {
                    {
                        put(CoralAD.Key.TASK_TYPE, mTaskType);
                        put(CoralAD.Key.ACCOUNT_ID, mAccountId);
                        put(CoralAD.Key.LOGIN_KEY, mLoginKey);
                    }
                })
                .load(new CoralInfoFeedListener() {
                    @Override
                    public void onAdViewLoaded(List<View> viewList) {
                        mFeedViewList = viewList;
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
                        return super.onAdClicked(ad);
                    }

                    @Override
                    public boolean onAppDownloading(@Nullable CoralAD ad, @Nullable String downloadUrl) {
                        whenAppDownloadEvent(ADEvent.Download_Start, "信息流");
                        return super.onAppDownloading(ad, downloadUrl);
                    }

                    @Override
                    public boolean onAppDownloaded(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Download_Success, "信息流");
                        return super.onAppDownloaded(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppInstalled(@Nullable CoralAD ad, @Nullable String downloadUrl, @Nullable String localFile) {
                        whenAppDownloadEvent(ADEvent.Install_Success, "信息流");
                        return super.onAppInstalled(ad, downloadUrl, localFile);
                    }

                    @Override
                    public boolean onAppActivated(CoralAD ad, String downloadUrl, String localFile) {
                        whenAppDownloadEvent(ADEvent.Activated, "信息流");
                        return super.onAppActivated(ad, downloadUrl, localFile);
                    }

                    @Override
                    public void onDislikeClicked() {
                        super.onDislikeClicked();
                    }
                });
    }

    @Override
    protected void showAd(CoralAD ad) {
        if (mContext != null) {
            AppCompatDialog dialog = new AppCompatDialog(mContext, R.style.PopUpDialog);
            dialog.setContentView(getAdContentLayoutId());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();

            ListView lvFeed = dialog.findViewById(R.id.lv_feed);
            lvFeed.setAdapter(new FeedAdapter(mFeedViewList));

            dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    class FeedAdapter extends BaseAdapter {
        private List<View> mList;

        public FeedAdapter(List<View> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new FrameLayout(mContext);
                convertView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                convertView.setPadding(0, 0, 0, DeviceUtil.dip2px(mContext, 5f));
            }
            View view = mList.get(position);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            if (((ViewGroup) convertView).getChildAt(0) != null) {
                ((ViewGroup) convertView).removeAllViews();
            }
            ((ViewGroup) convertView).addView(view,
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            return convertView;
        }
    }
}
