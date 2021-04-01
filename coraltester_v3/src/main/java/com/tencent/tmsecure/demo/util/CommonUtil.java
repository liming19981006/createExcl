package com.tencent.tmsecure.demo.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.moji.mjweather.R;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
    /**
     * 日志标签
     */
    private static final String TAG = "CommonUtil";

    /**
     * 获取字符串的MD5编码
     *
     * @param source
     * @return
     */
    public static String getMD5(String source) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(source.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "getMD5 (NoSuchAlgorithmException)", e);
        }
        //将加密后的数据转换为16进制数字
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }


    /**
     * 应用是否已安装
     *
     * @param pkg 包名
     * @return
     */
    public static boolean isPkgInstalled(Context mContext, String pkg) {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(pkg, 0);//flag 0 不会返回多余的数据
        } catch (Throwable e) {
            Log.e(TAG, "IsPkgInstalled (Throwable)", e);
        }
        return (info != null);
    }

    /**
     * 外部应用安装器安装apk（原生接口）
     *
     * @param path apk的路径
     * @return
     */
    public static boolean installApkByPath(Context mContext, String path) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mContext.getApplicationContext(), mContext.getPackageName() + ".coralfileprovider", new File(path));
            } else {
                uri = Uri.fromFile(new File(path));
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mContext.startActivity(intent);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "installApkByPath (Throwable)", e);
        }
        return false;
    }

    public static void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static AlertDialog mLoadingDialog;

    public synchronized static void showLoading(@NonNull final Context context) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new AlertDialog.Builder(context)
                            .setView(R.layout.dialog_loading)
                            .setCancelable(false)
                            .create();
                    mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                mLoadingDialog.show();
            }
        });
    }

    public synchronized static void hideLoading() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    private static long clickTime;

    public static boolean doubleClickToQuit(Context context) {
        long now = System.currentTimeMillis();
        if (now - clickTime > 2000) {
            clickTime = now;
            showToast(context, "再次点击退出程序");
            return false;
        }
        return true;
    }

    public static void setOutline(ImageView imageView) {
        if (imageView != null) {
            imageView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
        }
    }
}
