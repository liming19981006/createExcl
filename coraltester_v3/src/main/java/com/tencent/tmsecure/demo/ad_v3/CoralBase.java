package com.tencent.tmsecure.demo.ad_v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatDialog;

import com.bumptech.glide.Glide;
import com.moji.mjweather.R;
import com.tencent.tmsecure.demo.recorder.ADType;
import com.tencent.tmsecure.demo.recorder.Recorder;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tz.sdk.coral.ad.CoralAD;
import com.tz.sdk.core.ad.ADEvent;
import com.tz.sdk.core.ui.ADContainer;

/**
 * 广告基类
 * Created by wanghl on 2020/6/16
 */
public abstract class CoralBase {
    protected Context mContext;
    protected int mTaskType;
    protected Handler mMainHandler;
    private AppCompatDialog mAdContentDialog;
    protected String mAccountId = "asdadadas9x02910";
    protected String mLoginKey = "asdada90cjasfjiisfioad0";

    public CoralBase(Context context, int taskType) {
        mContext = context;
        mTaskType = taskType;
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取广告类型
     *
     * @return
     */
    public ADType getADType() {
        return null;
    }

    /**
     * 获取dialog布局资源
     *
     * @return
     */
    public int getAdContentLayoutId() {
        return 0;
    }

    /**
     * 拉取任务和广告
     */
    @CallSuper
    public void pull() {
        CommonUtil.showLoading(mContext);
    }

    /**
     * 显示广告
     *
     * @param ad 广告
     */
    protected void showAd(CoralAD ad) {
        if (mContext != null && getAdContentLayoutId() != 0) {
            mAdContentDialog = new AppCompatDialog(mContext, R.style.PopUpDialog);
            mAdContentDialog.setContentView(getAdContentLayoutId());
            mAdContentDialog.setCancelable(false);
            mAdContentDialog.setCanceledOnTouchOutside(false);
            mAdContentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mAdContentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mAdContentDialog.show();

            ImageView ivIcon = mAdContentDialog.findViewById(R.id.iv_icon);
            Glide.with(mContext).load(ad.getIcon()).into(ivIcon);
            ((TextView) mAdContentDialog.findViewById(R.id.tv_title)).setText(ad.getTitle());
            ((TextView) mAdContentDialog.findViewById(R.id.tv_desc)).setText(ad.getDescription());
            ((TextView) mAdContentDialog.findViewById(R.id.tv_cta)).setText(TextUtils.isEmpty(ad.getCta()) ? "立即领取" : ad.getCta());
            if (getADType() != null) {
                ((TextView) mAdContentDialog.findViewById(R.id.tv_type))
                        .setText(getADType().getName() + (mTaskType == 0 ? "" : String.valueOf(mTaskType)));
            }
            mAdContentDialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdContentDialog.dismiss();
                }
            });
            ADContainer container = mAdContentDialog.findViewById(R.id.ad_container);
            container.setAdModel(ad);
        }
    }

    /**
     * 广告加载成功
     *
     * @param ad 广告
     */
    protected void whenAdLoaded(CoralAD ad) {
        CommonUtil.hideLoading();
        showAd(ad);
    }

    /**
     * 广告加载失败
     *
     * @param msg 错误信息
     */
    protected void whenAdError(String msg) {
        CommonUtil.hideLoading();
        if (mContext != null) {
            CommonUtil.showToast(mContext, msg);
        }
    }

    /**
     * 广告显示
     */
    protected void whenAdShow() {
        CommonUtil.hideLoading();
        Recorder.getInstance(mContext).task(mTaskType).record(ADEvent.Show);
        if (mContext != null) {
            CommonUtil.showToast(mContext, "广告已显示");
        }
    }

    /**
     * 广告点击
     */
    protected void whenAdClicked() {
        Recorder.getInstance(mContext).task(mTaskType).record(ADEvent.Click);
        if (mContext != null) {
            CommonUtil.showToast(mContext, "广告已点击");
        }
    }

    /**
     * APP开始下载
     *
     * @param event  事件类型
     * @param source 安装来源
     */
    @SuppressLint("DefaultLocale")
    protected void whenAppDownloadEvent(ADEvent event, String source) {
        if (mContext != null) {
            Recorder.getInstance(mContext).task(mTaskType).record(event);
            CommonUtil.showToast(mContext, String.format("%s(%d)%s", source, mTaskType, event.getName()));
        }
    }
}
