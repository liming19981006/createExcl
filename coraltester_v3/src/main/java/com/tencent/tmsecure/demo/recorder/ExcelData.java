package com.tencent.tmsecure.demo.recorder;

import android.os.Environment;

import com.tz.sdk.core.ad.ADEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据格式
 * Created by wanghl on 2020/6/11
 */
public final class ExcelData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String environment;
    private String guid;
    private String imei;
    private String sdk;
    private String startTime;
    private String endTime;
    private ArrayList<ADType> adTypeList;
    private HashMap<ADType, HashMap<ADEvent, Integer>> events;
    private String filePath;

    public ExcelData() {
        adTypeList = new ArrayList<>();
        events = new HashMap<>();
        filePath = Environment.getDataDirectory() + File.separator + "excel.data";
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ArrayList<ADType> getAdTypeList() {
        return adTypeList;
    }

    public void setAdTypeList(ArrayList<ADType> adTypeList) {
        this.adTypeList = adTypeList;
    }

    public HashMap<ADType, HashMap<ADEvent, Integer>> getEvents() {
        return events;
    }

    public void setEvents(HashMap<ADType, HashMap<ADEvent, Integer>> events) {
        this.events = events;
    }

    public void writeToFile() {
        synchronized (this) {
            ObjectOutputStream oos = null;
            try {
                File file = new File(filePath);
                oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(this);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void readFromFile() {
        synchronized (this) {
            ObjectInputStream ois = null;
            try {
                File file = new File(filePath);
                ois = new ObjectInputStream(new FileInputStream(file));
                ExcelData excelData = (ExcelData) ois.readObject();
                clone(excelData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void clone(ExcelData excelData) {
        if (excelData != null) {
            environment = excelData.environment;
            guid = excelData.guid;
            imei = excelData.imei;
            sdk = excelData.sdk;
            startTime = excelData.startTime;
            endTime = excelData.endTime;
            adTypeList = excelData.adTypeList;
            events = excelData.events;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExcelData{");
        sb.append("environment='").append(environment).append('\'');
        sb.append(", guid='").append(guid).append('\'');
        sb.append(", imei='").append(imei).append('\'');
        sb.append(", sdk='").append(sdk).append('\'');
        sb.append(", startTime='").append(startTime).append('\'');
        sb.append(", endTime='").append(endTime).append('\'');
        sb.append(", adTypeList=").append(adTypeList);
        sb.append(", events=").append(events);
        sb.append('}');
        return sb.toString();
    }
}
