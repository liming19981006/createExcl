package com.tencent.tmsecure.demo.util;

import com.google.gson.Gson;
import com.tencent.ep.shanhuad.adpublic.ShanHuAD;
import com.tencent.ep.shanhuad.adpublic.models.AdMetaInfo;
import com.tencent.qqpim.discovery.AdDisplayModel;
import com.tmsdk.module.coin.AppRetainModel;
import com.tmsdk.module.coin.AppRetainSpUtil;
import com.tmsdk.module.coin.AppRetainUtil;


public class DownloadReportProxy {
    public static Gson gson = new Gson();
    public static void reportStartDownload(AdMetaInfo model, AdDisplayModel adDisplayModel){
        ShanHuAD.reportStartDownload(adDisplayModel);
    }

    public static void reportDownloadFinish(AdMetaInfo model, AdDisplayModel adDisplayModel){
        ShanHuAD.reportDownloadFinish(adDisplayModel);
    }

    public static void reportInstalled(AdMetaInfo model, AdDisplayModel adDisplayModel){
        ShanHuAD.reportInstalled(adDisplayModel);
        AppRetainUtil.putInstalledAppToSp(adDisplayModel.packageName,new AppRetainModel(System.currentTimeMillis(),adDisplayModel.packageName,
                -1,adDisplayModel.uniqueKey,"", AppRetainModel.installed,model.title,model.cta,model.icon));
    }

    public static void reportActive(AdMetaInfo model, AdDisplayModel adDisplayModel){
        ShanHuAD.reportActive(adDisplayModel);
        AppRetainModel appRetainModel = gson.fromJson((String) AppRetainSpUtil.getISharePreferenceImp().getString(adDisplayModel.packageName,""), AppRetainModel.class);
        if(model != null){
            appRetainModel.clickNum=+1;
        }
        AppRetainUtil.putInstalledAppToSp(adDisplayModel.packageName,appRetainModel);
    }

    public static void reportDownloadFinish(AdMetaInfo mStyleAdEntity, AdDisplayModel adDisplayModel, String apkFilePath) {
        ShanHuAD.reportDownloadFinish(adDisplayModel);
        AppRetainUtil.putDownloadAppToSp(adDisplayModel.packageName,new AppRetainModel(System.currentTimeMillis(),adDisplayModel.packageName,
                -1,adDisplayModel.uniqueKey,apkFilePath, AppRetainModel.downloadComplete,mStyleAdEntity.title,mStyleAdEntity.cta,mStyleAdEntity.icon));
    }
}
