package com.tencent.tmsecure.demo.recorder;

public enum ADType {
    CoralCard(102, "卡券", 1),
    CoralDownload(103, "下载", 2),
    CoralDownloadGdt(134, "下载", 3),
    CoralRewardVideo(104, "视频", 4),
    CoralRewardVideoGdt(131, "视频", 5),
    CoralRewardVideoKs(132, "视频", 6),
    CoralSplashImage(125, "开屏", 7),
    CoralBanner(130, "横幅", 8),
    CoralInfoFeed(128, "信息流", 9),
    CoralFullScreenVideo(137, "全屏", 10),
    CoralVideoFeed(138, "联盟", 11);

    private int mTaskType;
    private String mName;
    private int mColumnIndex;

    ADType(int taskType, String name, int columnIndex) {
        mTaskType = taskType;
        mName = name;
        mColumnIndex = columnIndex;
    }

    static ADType of(int taskType) {
        for (ADType adType : values()) {
            if (adType.mTaskType == taskType) {
                return adType;
            }
        }
        return null;
    }

    public int getTaskType() {
        return mTaskType;
    }

    public String getName() {
        return mName;
    }

    public int getColumnIndex() {
        return mColumnIndex;
    }

    public String getCombinedName() {
        return mName + (mTaskType != 0 ? mTaskType : "");
    }
}