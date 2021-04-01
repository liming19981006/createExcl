package com.tencent.tmsecure.demo.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moji.mjweather.R;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tz.sdk.core.ad.ADEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 操作录制器
 * Created by wanghl on 2020/6/11
 */
public final class Recorder {
    private static Recorder mInstance;
    private Context mContext;
    private View mControllerView;
    private boolean mRecording;
    private int mTaskType;
    private TextView mTvStart;
    private TextView mTvEnd;
    private View mTvTips;
    private View mIvRecord;
    private View mIvExcel;
    private SimpleDateFormat mDateFormat;
    private ObjectAnimator mAnimator;
    private IExcelListener mIExcelListener;
    private String mExcelFile;

    private Recorder(@NonNull Context context) {
        mContext = context;
        mControllerView = LayoutInflater.from(context).inflate(R.layout.layout_record, null);
        initController();
    }

    public static Recorder getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Recorder.class) {
                if (mInstance == null) {
                    mInstance = new Recorder(context);
                }
            }
        }
        return mInstance;
    }

    private void initController() {
        mTvStart = mControllerView.findViewById(R.id.tv_start);
        mTvEnd = mControllerView.findViewById(R.id.tv_end);
        mTvTips = mControllerView.findViewById(R.id.tv_tips);
        mIvRecord = mControllerView.findViewById(R.id.iv_record);
        mIvExcel = mControllerView.findViewById(R.id.iv_excel);

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mAnimator = ObjectAnimator.ofFloat(mTvTips, "alpha", 1f, 0f, 1f);
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mTvEnd.setText(mDateFormat.format(new Date()));
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mTvEnd.setText(mDateFormat.format(new Date()));
            }
        });
        mIvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRecording) {
                    if (ExcelManager.getInstance(mContext).hasData()) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("开始录制")
                                .setMessage("重新录制将覆盖之前的测试数据，确定继续吗？")
                                .setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startRecord();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();

                    } else {
                        startRecord();
                    }
                } else {
                    endRecord();
                }
            }
        });
        mIvExcel.setOnClickListener(v -> {
            if (mExcelFile == null) {
                if (!ExcelManager.getInstance(mContext).hasData()) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Excel导出")
                            .setMessage("对不起，您还没有拉到任何广告，请重新录制")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return;
                } else {
                    mExcelFile = ExcelManager.getInstance(mContext).export();
                    if (mExcelFile == null) {
                        CommonUtil.showToast(mContext, "生成Excel报表出错，请重试");
                        return;
                    }
                }
            }
            CommonUtil.showToast(mContext, "Excel报表已生成");
            if (mIExcelListener != null) {
                mIExcelListener.onExcelExported(mExcelFile, ExcelManager.getInstance(mContext).getExcelData());
            }
        });
    }

    private void startRecord() {
        if (!ExcelManager.getInstance(mContext).hasGuid()) {
            CommonUtil.showToast(mContext, "请先获取guid");
            return;
        }
        mRecording = true;
        ExcelManager.getInstance(mContext).clearData();
        mExcelFile = null;
        mIvRecord.setActivated(true);
        mTvStart.setText(mDateFormat.format(new Date()));
        ((ViewGroup) mTvStart.getParent()).setVisibility(View.VISIBLE);
        ((ViewGroup) mTvEnd.getParent()).setVisibility(View.VISIBLE);
        mTvTips.setVisibility(View.VISIBLE);
        mIvExcel.setVisibility(View.INVISIBLE);
        mAnimator.start();
    }

    private void endRecord() {
        new AlertDialog.Builder(mContext)
                .setTitle("停止录制")
                .setMessage("确定要停止录制吗？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExcelManager.getInstance(mContext)
                                .startTime(mTvStart.getText().toString())
                                .endTime(mTvEnd.getText().toString());

                        mRecording = false;
                        mIvRecord.setActivated(false);
                        mIvExcel.setVisibility(View.VISIBLE);
                        mTvTips.setVisibility(View.INVISIBLE);
                        mAnimator.end();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
    }

    public View getControllerView() {
        return mControllerView;
    }

    public Recorder environment(String environment) {
        ExcelManager.getInstance(mContext).environment(environment);
        return this;
    }

    public Recorder guid(String guid) {
        ExcelManager.getInstance(mContext).guid(guid);
        return this;
    }

    public Recorder imei(String imei) {
        ExcelManager.getInstance(mContext).imei(imei);
        return this;
    }

    public Recorder sdk(String sdk) {
        ExcelManager.getInstance(mContext).sdk(sdk);
        return this;
    }

    public Recorder task(int taskType) {
        mTaskType = taskType;
        return this;
    }

    public Recorder excel(IExcelListener iExcelListener) {
        mIExcelListener = iExcelListener;
        return this;
    }

    public synchronized boolean isRecording() {
        return mRecording;
    }

    public void record(ADEvent adEvent) {
        if (mRecording) {
            ExcelManager.getInstance(mContext).onEvent(ADType.of(mTaskType), adEvent);
        }
    }

    public interface IExcelListener {
        void onExcelExported(String file, ExcelData excelData);
    }
}
