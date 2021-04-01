package com.tencent.tmsecure.demo.recorder;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.moji.mjweather.R;
import com.tencent.tmsecure.demo.util.CommonUtil;
import com.tz.sdk.core.ad.ADEvent;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel处理
 * Created by wanghl on 2020/6/11
 */
final class ExcelManager {
    private static ExcelManager mInstance;
    private Context mContext;
    private ExcelData mExcelData;

    private ExcelManager(Context context) {
        mContext = context;
        mExcelData = new ExcelData();
//        mExcelData.readFromFile();
    }

    public static ExcelManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ExcelManager.class) {
                if (mInstance == null) {
                    mInstance = new ExcelManager(context);
                }
            }
        }
        return mInstance;
    }

    public ExcelManager environment(String environment) {
        mExcelData.setEnvironment(environment);
        return this;
    }

    public ExcelManager guid(String guid) {
        mExcelData.setGuid(guid);
        return this;
    }

    public ExcelManager imei(String imei) {
        mExcelData.setImei(imei);
        return this;
    }

    public ExcelManager sdk(String sdk) {
        mExcelData.setSdk(sdk);
        return this;
    }

    public ExcelManager startTime(String startTime) {
        mExcelData.setStartTime(startTime);
        return this;
    }

    public ExcelManager endTime(String endTime) {
        mExcelData.setEndTime(endTime);
        return this;
    }

    public ExcelManager clearData() {
        if (mExcelData != null) {
            synchronized (mExcelData) {
                mExcelData.getAdTypeList().clear();
                mExcelData.getEvents().clear();
            }
        }
        return this;
    }

    public ExcelData getExcelData() {
        return mExcelData;
    }

    public boolean hasGuid() {
        if (mExcelData != null) {
            synchronized (mExcelData) {
                return !TextUtils.isEmpty(mExcelData.getGuid());
            }
        }
        return false;
    }

    public boolean hasData() {
        if (mExcelData != null) {
            synchronized (mExcelData) {
                return mExcelData.getAdTypeList().size() > 0;
            }
        }
        return false;
    }

    public void onEvent(ADType adType, final ADEvent adEvent) {
        if (mExcelData != null) {
            synchronized (mExcelData) {
                List<ADType> adTypeList = mExcelData.getAdTypeList();
                if (!adTypeList.contains(adType)) {
                    adTypeList.add(adType);
                }
                HashMap<ADType, HashMap<ADEvent, Integer>> adTypeMap = mExcelData.getEvents();
                if (!adTypeMap.containsKey(adType)) {
                    HashMap<ADEvent, Integer> eventMap = new HashMap<>();
                    eventMap.put(adEvent, 1);
                    adTypeMap.put(adType, eventMap);
                } else {
                    HashMap<ADEvent, Integer> eventMap = adTypeMap.get(adType);
                    if (!eventMap.containsKey(adEvent)) {
                        eventMap.put(adEvent, 1);
                    } else {
                        eventMap.put(adEvent, eventMap.get(adEvent) + 1);
                    }
                }
            }
        }
    }

    public void save() {
        mExcelData.writeToFile();
    }

    public String export() {
        if (mExcelData != null) {
            synchronized (mExcelData) {
                CommonUtil.showLoading(mContext);

                HSSFWorkbook mExcelWorkbook = new HSSFWorkbook();
                HSSFSheet sheet = mExcelWorkbook.createSheet();
                mExcelWorkbook.setSheetName(0, mContext.getResources().getString(R.string.app_name));
                sheet.setDefaultColumnWidth((short) 15);

                //居中样式
                HSSFCellStyle centerStyle = mExcelWorkbook.createCellStyle();
                centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                centerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
                centerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
                centerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
                centerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

                HSSFCellStyle centerVerticalStyle = mExcelWorkbook.createCellStyle();
                centerVerticalStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                centerVerticalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
                centerVerticalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
                centerVerticalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
                centerVerticalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

                // 加粗字体
                HSSFFont boldFont = mExcelWorkbook.createFont();
                boldFont.setFontName("黑体");
                boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                /*环境+sdk*/
                HSSFRow row1 = sheet.createRow(0);
                row1.setHeightInPoints(30);

                //环境
                HSSFCellStyle centerBoldStyle = mExcelWorkbook.createCellStyle();
                centerBoldStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                centerBoldStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                centerBoldStyle.setFont(boldFont);
                centerBoldStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
                centerBoldStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
                centerBoldStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
                centerBoldStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

                HSSFCell cell;
                cell = row1.createCell((short) 0, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("环境"));
                cell = row1.createCell((short) 1, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 2, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 3, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getEnvironment()));
                cell = row1.createCell((short) 4, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 5, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 2));
                sheet.addMergedRegion(new Region(0, (short) 3, 0, (short) 5));
                //sdk
                cell = row1.createCell((short) 6, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("SDK"));
                cell = row1.createCell((short) 7, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 8, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 9, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getSdk()));
                cell = row1.createCell((short) 10, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row1.createCell((short) 11, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(0, (short) 6, 0, (short) 8));
                sheet.addMergedRegion(new Region(0, (short) 9, 0, (short) 11));
                /*guid+imei*/
                HSSFRow row2 = sheet.createRow(1);
                row2.setHeightInPoints(30);
                //guid
                cell = row2.createCell((short) 0, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("guid"));
                cell = row2.createCell((short) 1, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 2, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 3, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getGuid()));
                cell = row2.createCell((short) 4, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 5, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(1, (short) 0, 1, (short) 2));
                sheet.addMergedRegion(new Region(1, (short) 3, 1, (short) 5));
                //imei
                cell = row2.createCell((short) 6, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("imei"));
                cell = row2.createCell((short) 7, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 8, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 9, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getImei()));
                cell = row2.createCell((short) 10, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row2.createCell((short) 11, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(1, (short) 6, 1, (short) 8));
                sheet.addMergedRegion(new Region(1, (short) 9, 1, (short) 11));
                /*开始时间+结束时间*/
                HSSFRow row3 = sheet.createRow(2);
                row3.setHeightInPoints(30);
                //开始时间
                cell = row3.createCell((short) 0, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("开始时间"));
                cell = row3.createCell((short) 1, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 2, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 3, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getStartTime()));
                cell = row3.createCell((short) 4, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 5, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(2, (short) 0, 2, (short) 2));
                sheet.addMergedRegion(new Region(2, (short) 3, 2, (short) 5));
                //结束时间
                cell = row3.createCell((short) 6, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerBoldStyle);
                cell.setCellValue(new HSSFRichTextString("结束时间"));
                cell = row3.createCell((short) 7, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 8, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 9, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerVerticalStyle);
                cell.setCellValue(new HSSFRichTextString(mExcelData.getEndTime()));
                cell = row3.createCell((short) 10, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                cell = row3.createCell((short) 11, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(centerStyle);
                sheet.addMergedRegion(new Region(2, (short) 6, 2, (short) 8));
                sheet.addMergedRegion(new Region(2, (short) 9, 2, (short) 11));

                /*标题*/
                HSSFRow row4 = sheet.createRow(3);
                row4.setHeightInPoints(30);
                //字体颜色加粗及单元格背景色
                HSSFCellStyle colorStyle = mExcelWorkbook.createCellStyle();
                colorStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                colorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                colorStyle.setFillForegroundColor(HSSFColor.BLACK.index);
                colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                HSSFFont boldWhiteFont = mExcelWorkbook.createFont();
                boldWhiteFont.setFontName("黑体");
                boldWhiteFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                boldWhiteFont.setColor(HSSFColor.WHITE.index);
                colorStyle.setFont(boldWhiteFont);
                colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
                colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
                colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
                colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

                cell = row4.createCell((short) 0, HSSFCell.CELL_TYPE_STRING);
                cell.setCellStyle(colorStyle);
                cell.setCellValue(new HSSFRichTextString("事件"));
                for (int i = 0; i < ADType.values().length; i++) {
                    cell = row4.createCell((short) (i + 1), HSSFCell.CELL_TYPE_STRING);
                    cell.setCellStyle(colorStyle);
                    cell.setCellValue(new HSSFRichTextString(ADType.values()[i].getCombinedName()));
                }

                /*广告类型*/
                for (int i = 4; i < ADEvent.values().length + 4; i++) {
                    HSSFRow row = sheet.createRow(i);
                    row.setHeightInPoints(30);
                    cell = row.createCell((short) 0, HSSFCell.CELL_TYPE_STRING);
                    cell.setCellStyle(centerStyle);
                    cell.setCellValue(new HSSFRichTextString(ADEvent.values()[i - 4].getName()));
                    for (short j = 1; j < 12; j++) {
                        cell = row.createCell(j, HSSFCell.CELL_TYPE_STRING);
                        cell.setCellStyle(centerStyle);
                        cell.setCellValue(new HSSFRichTextString(""));
                    }
                }

                /*操作次数*/
                for (ADType adType : mExcelData.getAdTypeList()) {
                    for (Map.Entry<ADEvent, Integer> entries : mExcelData.getEvents().get(adType).entrySet()) {
                        int rowIndex = entries.getKey().getId() + 3;
                        int columnIndex = adType.getColumnIndex();
                        int cellValue = entries.getValue();
                        sheet.getRow(rowIndex)
                                .getCell((short) columnIndex)
                                .setCellValue(new HSSFRichTextString(String.valueOf(cellValue)));
                    }
                }

                /*写文件*/
                String filePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .append(File.separator).append(Environment.DIRECTORY_DOWNLOADS).append(File.separator)
                        .append("Coral_test_").append(System.currentTimeMillis()).append(".xls").toString();
                try {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    OutputStream os = new FileOutputStream(new File(filePath));
                    mExcelWorkbook.write(os);
                    os.flush();
                    os.close();
                    return filePath;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    CommonUtil.hideLoading();
                }
            }
        }
        return null;
    }
}
