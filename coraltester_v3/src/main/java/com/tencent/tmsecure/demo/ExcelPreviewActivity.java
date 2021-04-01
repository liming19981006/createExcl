package com.tencent.tmsecure.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.FileProvider;

import com.moji.mjweather.R;
import com.tencent.tmsecure.demo.recorder.ADType;
import com.tencent.tmsecure.demo.recorder.ExcelData;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tencent.tmsecure.demo.util.DeviceUtil;
import com.tz.sdk.core.ad.ADEvent;

import java.io.File;
import java.util.HashMap;

/**
 * Excel数据模拟显示
 * Created by wanghl on 2020/6/13
 */
public class ExcelPreviewActivity extends AppCompatActivity implements View.OnClickListener {
    private String mFile;
    private ExcelData mData;
    private int mCellWidth;
    private AppCompatDialog mShareDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    private void init() {
        mFile = getIntent().getStringExtra("file");
        mData = (ExcelData) getIntent().getSerializableExtra("data");
        ((TextView) findViewById(R.id.tv_file)).setText(mFile == null ? "" : mFile.substring(mFile.lastIndexOf('/') + 1));
        findViewById(R.id.iv_share).setOnClickListener(this);

        final GridLayout grid = findViewById(R.id.gl_excel);
        grid.setColumnCount(12);
        grid.setRowCount(10);
        grid.post(new Runnable() {
            @Override
            public void run() {
                mCellWidth = grid.getWidth() / 12;
                //环境
                grid.addView(addCell(3, "环境", true, true, false));
                grid.addView(addCell(3, mData.getEnvironment(), false, false, false));
                //sdk
                grid.addView(addCell(3, "SDK", true, true, false));
                grid.addView(addCell(3, mData.getSdk(), false, false, false));
                //guid
                grid.addView(addCell(3, "guid", true, true, false));
                grid.addView(addCell(3, mData.getGuid(), false, false, false));
                //sdk
                grid.addView(addCell(3, "imei", true, true, false));
                grid.addView(addCell(3, mData.getImei(), false, false, false));
                //开始时间
                grid.addView(addCell(3, "开始时间", true, true, false));
                grid.addView(addCell(3, mData.getStartTime(), false, false, false));
                //结束时间
                grid.addView(addCell(3, "结束时间", true, true, false));
                grid.addView(addCell(3, mData.getEndTime(), false, false, false));
                //广告类型
                grid.addView(addCell(1, "事件", true, true, true));
                for (int i = 0; i < ADType.values().length; i++) {
                    grid.addView(addCell(1, ADType.values()[i].getCombinedName(), true, true, true));
                }
                //操作次数
                for (ADEvent adEvent : ADEvent.values()) {
                    grid.addView(addCell(1, adEvent.getName(), true, false, false));
                    for (ADType adType : ADType.values()) {
                        HashMap<ADEvent, Integer> eventMap = mData.getEvents().get(adType);
                        if (eventMap != null) {
                            if (eventMap.containsKey(adEvent)) {
                                grid.addView(addCell(1, String.valueOf(eventMap.get(adEvent)), true, false, false));
                                continue;
                            }
                        }
                        grid.addView(addCell(1, "", true, false, false));
                    }
                }
            }
        });
    }

    private View addCell(int columnSpan, String value, boolean center, boolean bold, boolean colorBg) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED),
                GridLayout.spec(GridLayout.UNDEFINED, columnSpan));
        lp.width = mCellWidth * columnSpan;
        lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
        TextView tvCell = new TextView(this);
        tvCell.setLayoutParams(lp);
        tvCell.setBackgroundResource(R.drawable.cell_bg);
        tvCell.setSingleLine(true);
        if (bold) {
            tvCell.getPaint().setFakeBoldText(true);
        }
        if (colorBg) {
            tvCell.setTextColor(getResources().getColor(android.R.color.white));
            tvCell.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            tvCell.setTextColor(getResources().getColor(android.R.color.black));
        }
        tvCell.setEllipsize(TextUtils.TruncateAt.END);
        tvCell.setGravity(center ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
        int padding = DeviceUtil.dip2px(this, 5f);
        tvCell.setPadding(0, padding, 0, padding);
        tvCell.setTextSize(14f);
        tvCell.setText(value);
        return tvCell;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share://分享
                mShareDialog = new AppCompatDialog(ExcelPreviewActivity.this, R.style.BottomPopUpDialog);
                View view = getLayoutInflater().inflate(R.layout.dialog_share, null);
                mShareDialog.setContentView(view);
                mShareDialog.getWindow().setGravity(Gravity.BOTTOM);
                mShareDialog.findViewById(R.id.ll_wechat).setOnClickListener(this);
                mShareDialog.findViewById(R.id.ll_qq).setOnClickListener(this);
                mShareDialog.show();
                break;
            case R.id.ll_wechat://微信
            case R.id.ll_qq://QQ
                share(v.getId());
                break;
        }
    }

    private void share(int id) {
        if (mFile == null || !new File(mFile).exists()) {
            CommonUtil.showToast(this, "文件不存在，请重新录制");
            return;
        }
        final Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this.getApplicationContext(),
                    getPackageName() + ".tzprovider", new File(mFile));
        } else {
            uri = Uri.fromFile(new File(mFile));
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage(id == R.id.ll_wechat ? "com.tencent.mm" : "com.tencent.mobileqq");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            mShareDialog.dismiss();
        } else {
            CommonUtil.showToast(this, "未安装" + (id == R.id.ll_wechat ? "微信" : "QQ"));
        }
    }
}
