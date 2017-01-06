/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.OrderPlan;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author LFeng
 */
public class OrderPlanController {

    private static final Logger logger = Logger.getLogger(OrderPlanController.class);
    private static final int[][] templateDataIndex = {
        {0, 1, 13, 14, 16, 17, 21, 22},
        {0, 1, 2, 3, 4, 5, 6, 7},
        {23, 8, 4, 32, 30, 14, 21, 27}
    };

    /**
     * 导入计划数据
     *
     * @param fileName
     * @param planType
     * @return -1 -- 导入数据失败, 0 -- 导入数据成功, -2 -- 导入的文件格式错误, -3 -- 导入的数据格式错误
     */
    public int importData(String fileName, int planType) {
        InputStream inputStream = null;
        int templateType;//模板类型: 0 -- 小康物流系统导出的模板 | 1 -- 系统提供下载的模板 | 2 -- 小康SAP系统模板
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            Sheet sheet;
            if (fileName.endsWith(".xls")) {
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                sheet = workbook.getSheetAt(0);
            } else if (fileName.endsWith(".xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheetAt(0);
            } else {
                logger.info("导入的文件不是Excel文件!");
                return -2;
            }

            Row headRow = sheet.getRow(0);
            if (headRow.getPhysicalNumberOfCells() == 38) {
                templateType = 0;
            } else if (headRow.getPhysicalNumberOfCells() == 8) {
                templateType = 1;
            } else if (headRow.getPhysicalNumberOfCells() == 34) {
                templateType = 2;
            } else {
                logger.info("导入的文件数据格式不正确!");
                return -3;
            }

            ArrayList<OrderPlan> imports = new ArrayList<>();
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                int cellNum = row.getPhysicalNumberOfCells();
                //logger.info("count row num:" + sheet.getPhysicalNumberOfRows() + ",the row num is:" + i + ",count cell num is:" + cellNum);
                OrderPlan info = new OrderPlan();
                //需求日期
                if (row.getCell(templateDataIndex[templateType][0]).getCellType() == Cell.CELL_TYPE_STRING) {
                    info.setFinishTime(row.getCell(templateDataIndex[templateType][0]).getStringCellValue());
                } else {
                    if (DateUtil.isCellDateFormatted(row.getCell(templateDataIndex[templateType][0]))) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = DateUtil.getJavaDate(row.getCell(templateDataIndex[templateType][0]).getNumericCellValue());
                        info.setFinishTime(dateFormat.format(date));
                    } else {
                        DecimalFormat df = new DecimalFormat("0");
                        info.setFinishTime(df.format(row.getCell(templateDataIndex[templateType][0]).getNumericCellValue()));
                    }
                }
                //下达时间
                if (row.getCell(templateDataIndex[templateType][1]).getCellType() == Cell.CELL_TYPE_STRING) {
                    info.setSendTime(row.getCell(templateDataIndex[templateType][1]).getStringCellValue());
                } else {
                    if (DateUtil.isCellDateFormatted(row.getCell(templateDataIndex[templateType][1]))) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = DateUtil.getJavaDate(row.getCell(templateDataIndex[templateType][1]).getNumericCellValue());
                        info.setSendTime(dateFormat.format(date));
                    } else {
                        DecimalFormat df = new DecimalFormat("0");
                        info.setSendTime(df.format(row.getCell(templateDataIndex[templateType][1]).getNumericCellValue()));
                    }
                }
                //供货客户代码
                row.getCell(templateDataIndex[templateType][2]).setCellType(Cell.CELL_TYPE_STRING);
                info.setCarrierCode(row.getCell(templateDataIndex[templateType][2]).getStringCellValue());
                //供货客户名称
                row.getCell(templateDataIndex[templateType][3]).setCellType(Cell.CELL_TYPE_STRING);
                info.setCarrierName(row.getCell(templateDataIndex[templateType][3]).getStringCellValue());
                //部品名称
                row.getCell(templateDataIndex[templateType][4]).setCellType(Cell.CELL_TYPE_STRING);
                info.setPinMing(row.getCell(templateDataIndex[templateType][4]).getStringCellValue());
                //件号
                row.getCell(templateDataIndex[templateType][5]).setCellType(Cell.CELL_TYPE_STRING);
                info.setJianHao(row.getCell(templateDataIndex[templateType][5]).getStringCellValue());
                //计量单位
                row.getCell(templateDataIndex[templateType][6]).setCellType(Cell.CELL_TYPE_STRING);
                info.setUnit(row.getCell(templateDataIndex[templateType][6]).getStringCellValue());
                //数量
                row.getCell(templateDataIndex[templateType][7]).setCellType(Cell.CELL_TYPE_NUMERIC);
                info.setCarCount((int) row.getCell(templateDataIndex[templateType][7]).getNumericCellValue());

                imports.add(info);
                /*
                 if (templateType == 0) {
                 OrderPlan info = new OrderPlan();
                 //需求日期
                 if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setFinishTime(row.getCell(0).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(0))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(0).getNumericCellValue());
                 info.setFinishTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setFinishTime(df.format(row.getCell(0).getNumericCellValue()));
                 }
                 }
                 //下达时间
                 if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setSendTime(row.getCell(1).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(1))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(1).getNumericCellValue());
                 info.setSendTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setSendTime(df.format(row.getCell(1).getNumericCellValue()));
                 }
                 }
                 //供货客户代码
                 row.getCell(13).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierCode(row.getCell(13).getStringCellValue());
                 //供货客户名称
                 row.getCell(14).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierName(row.getCell(14).getStringCellValue());
                 //部品名称
                 row.getCell(16).setCellType(Cell.CELL_TYPE_STRING);
                 info.setPinMing(row.getCell(16).getStringCellValue());
                 //件号
                 row.getCell(17).setCellType(Cell.CELL_TYPE_STRING);
                 info.setJianHao(row.getCell(17).getStringCellValue());
                 //计量单位
                 row.getCell(21).setCellType(Cell.CELL_TYPE_STRING);
                 info.setUnit(row.getCell(21).getStringCellValue());
                 //数量
                 row.getCell(22).setCellType(Cell.CELL_TYPE_NUMERIC);
                 info.setCarCount((int) row.getCell(22).getNumericCellValue());
                    
                 imports.add(info);
                 } else if (templateType == 1) {
                 OrderPlan info = new OrderPlan();
                 //需求日期
                 if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setFinishTime(row.getCell(0).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(0))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(0).getNumericCellValue());
                 info.setFinishTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setFinishTime(df.format(row.getCell(0).getNumericCellValue()));
                 }
                 }
                 //下达时间
                 if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setSendTime(row.getCell(1).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(1))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(1).getNumericCellValue());
                 info.setSendTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setSendTime(df.format(row.getCell(1).getNumericCellValue()));
                 }
                 }
                 //供货客户代码
                 row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierCode(row.getCell(2).getStringCellValue());
                 //供货客户名称
                 row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierName(row.getCell(3).getStringCellValue());
                 //部品名称
                 row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                 info.setPinMing(row.getCell(4).getStringCellValue());
                 //件号
                 row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                 info.setJianHao(row.getCell(5).getStringCellValue());
                 //计量单位
                 row.getCell(6).setCellType(Cell.CELL_TYPE_STRING);
                 info.setUnit(row.getCell(6).getStringCellValue());
                 //数量
                 row.getCell(7).setCellType(Cell.CELL_TYPE_NUMERIC);
                 info.setCarCount((int) row.getCell(7).getNumericCellValue());
                    
                 imports.add(info);
                 } else if (templateType == 2) {
                 OrderPlan info = new OrderPlan();
                 //需求日期
                 if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setFinishTime(row.getCell(0).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(0))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(0).getNumericCellValue());
                 info.setFinishTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setFinishTime(df.format(row.getCell(0).getNumericCellValue()));
                 }
                 }
                 //下达时间
                 if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                 info.setSendTime(row.getCell(1).getStringCellValue());
                 } else {
                 if (DateUtil.isCellDateFormatted(row.getCell(1))) {
                 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                 Date date = DateUtil.getJavaDate(row.getCell(1).getNumericCellValue());
                 info.setSendTime(dateFormat.format(date));
                 } else {
                 DecimalFormat df = new DecimalFormat("0");
                 info.setSendTime(df.format(row.getCell(1).getNumericCellValue()));
                 }
                 }
                 //供货客户代码
                 row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierCode(row.getCell(2).getStringCellValue());
                 //供货客户名称
                 row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                 info.setCarrierName(row.getCell(3).getStringCellValue());
                 //部品名称
                 row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                 info.setPinMing(row.getCell(4).getStringCellValue());
                 //件号
                 row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                 info.setJianHao(row.getCell(5).getStringCellValue());
                 //计量单位
                 row.getCell(6).setCellType(Cell.CELL_TYPE_STRING);
                 info.setUnit(row.getCell(6).getStringCellValue());
                 //数量
                 row.getCell(7).setCellType(Cell.CELL_TYPE_NUMERIC);
                 info.setCarCount((int) row.getCell(7).getNumericCellValue());
                    
                 imports.add(info);
                 }
                 */
            }
            return batchAddData(planType, imports);
        } catch (FileNotFoundException ex) {
            logger.error("未找到文件", ex);
        } catch (IOException ex) {
            logger.error("IO异常", ex);
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                logger.error("关闭输入流异常", ex);
            }
        }
        return -1;
    }

    public int batchAddData(int planType, List<OrderPlan> imports) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        opt = new DatabaseOpt();
        try {
            conn = opt.getConnect();
            conn.setAutoCommit(false);
            statement = conn.prepareCall("insert into tbOrderPlan(planId, PinMing, JianHao, FinishTime, CarCount, CarrierCode, CarrierName, planType, Unit, SendTime)"
                    + "values(PLANID.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (OrderPlan infoImport : imports) {
                statement.setString(1, infoImport.getPinMing());
                statement.setString(2, infoImport.getJianHao());
                statement.setString(3, infoImport.getFinishTime());
                statement.setInt(4, infoImport.getCarCount());
                statement.setString(5, infoImport.getCarrierCode());
                statement.setString(6, infoImport.getCarrierName());
                statement.setInt(7, planType);
                statement.setString(8, infoImport.getUnit());
                statement.setString(9, infoImport.getSendTime());
                statement.addBatch();
            }
            statement.executeBatch();
            conn.commit();
            return 0;
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                logger.error("数据库回滚错误", ex1);
            }
            logger.error("数据库执行错误", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return -1;
    }

    /**
     * 获取计划列表
     *
     * @param carrierName
     * @param jianHaoName
     * @param jianHao
     * @param carrierCode
     * @param beginTime
     * @param endTime
     * @param searchField
     * @param searchValue
     * @param orderField
     * @param orderFlag
     * @param pageSize
     * @param pageIndex
     * @return
     */
    public ArrayList<OrderPlan> getData(String carrierName, String jianHaoName, String jianHao,
            String carrierCode, String beginTime, String endTime, String searchField, String searchValue,
            String orderField, int orderFlag, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<OrderPlan> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(searchValue)) {
                if (searchField.compareToIgnoreCase("all") == 0) {
                    whereCase += ("CarrierName like '%" + searchValue + "%'");
                    whereCase += (" or carrierCode like '%" + searchValue + "%'");
                    whereCase += (" or JianHao like '%" + searchValue + "%'");
                    whereCase += (" or PinMing like '%" + searchValue + "%'");
                } else {
                    whereCase += (searchField + " like '%" + searchValue + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(carrierName)) {
                if (caseCount == 0) {
                    whereCase += ("CarrierName like '%" + carrierName + "%'");
                } else {
                    whereCase += (" and CarrierName like '%" + carrierName + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(carrierCode)) {
                if (caseCount == 0) {
                    whereCase += ("carrierCode like '%" + carrierCode + "%'");
                } else {
                    whereCase += (" and carrierCode like '%" + carrierCode + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(jianHaoName)) {
                if (caseCount == 0) {
                    whereCase += ("PinMing like '%" + jianHaoName + "%'");
                } else {
                    whereCase += (" and PinMing like '%" + jianHaoName + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(jianHao)) {
                if (caseCount == 0) {
                    whereCase += ("JianHao like '%" + jianHao + "%'");
                } else {
                    whereCase += (" and JianHao like '%" + jianHao + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(beginTime) && !Units.strIsEmpty(endTime)) {
                if (caseCount == 0) {
                    whereCase += ("FinishTime >= '" + beginTime + "' and FinishTime <= '" + endTime + "'");
                } else {
                    whereCase += ("and FinishTime >= '" + beginTime + "' and FinishTime <= '" + endTime + "'");
                }
                caseCount++;
            }

            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbOrderPlan");
            statement.setString("fields", "*");
            statement.setString("wherecase", whereCase);
            statement.setInt("pageSize", pageSize);
            statement.setInt("pageNow", pageIndex);
            statement.setString("orderField", orderField);
            statement.setInt("orderFlag", orderFlag);
            statement.registerOutParameter("myrows", OracleTypes.NUMBER);
            statement.registerOutParameter("myPageCount", OracleTypes.NUMBER);
            statement.registerOutParameter("p_cursor", OracleTypes.CURSOR);
            statement.execute();
            ResultSet set = (ResultSet) statement.getObject("p_cursor");
            result = new ArrayList<>();
            while (set.next()) {
                OrderPlan info = new OrderPlan();
                info.setPlanId(set.getInt("planId"));
                info.setPinMing(set.getString("PinMing"));
                info.setJianHao(set.getString("JianHao"));
                info.setFinishTime(set.getString("FinishTime"));
                info.setCarCount(set.getInt("CarCount"));
                info.setCarrierCode(set.getString("CarrierCode"));
                info.setCarrierName(set.getString("CarrierName"));
                info.setUnit(set.getString("Unit"));
                info.setPlanType(set.getInt("planType"));
                result.add(info);
            }
            OrderPlan.setRecordCount(statement.getInt("myrows"));
            return result;
        } catch (SQLException ex) {
            logger.error("数据库执行错误", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return null;
    }
}
