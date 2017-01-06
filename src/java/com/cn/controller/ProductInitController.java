/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.ProductInit;
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
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author LFeng
 */
public class ProductInitController {
    private static final Logger logger = Logger.getLogger(ProductInitController.class);
    
    /**
     * 导入原材料期初数据
     *
     * @param fileName
     * @param carrierName
     * @return
     */
    public int importData(String fileName, String carrierName) {
        InputStream inputStream = null;
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            ArrayList<ProductInit> imports = new ArrayList<>();
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
//                logger.info("count row num:" + sheet.getPhysicalNumberOfRows() + ",the row num is:" + i);
                HSSFRow row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }

                int cellNum = row.getPhysicalNumberOfCells();
//                logger.info("count cell num is:" + cellNum);
                if (cellNum >= 4) {
                    ProductInit info = new ProductInit();
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    info.setJianHao(row.getCell(0).getStringCellValue());
                    if (Units.strIsEmpty(info.getJianHao()))
                        continue;
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    info.setPinMing(row.getCell(1).getStringCellValue());
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    info.setCarModel(row.getCell(2).getStringCellValue());
                    row.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                    info.setInitNum((int) row.getCell(3).getNumericCellValue());
                    
                    if (row.getCell(4).getCellType() == Cell.CELL_TYPE_STRING) {
                        info.setInitDate(row.getCell(4).getStringCellValue());
                    } else {
                        if (HSSFDateUtil.isCellDateFormatted(row.getCell(4))) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = HSSFDateUtil.getJavaDate(row.getCell(4).getNumericCellValue());
                            info.setInitDate(dateFormat.format(date));
                        } else {
                            DecimalFormat df = new DecimalFormat("0");
                            info.setInitDate(df.format(row.getCell(4).getNumericCellValue()));
                        }
                    }
                    info.setCarrierName(carrierName);
                    imports.add(info);
                }
            }
            DatabaseOpt opt;
            Connection conn = null;
            CallableStatement statement = null;
            opt = new DatabaseOpt();
            try {
                conn = opt.getConnect();
                conn.setAutoCommit(false);
                statement = conn.prepareCall("insert into tbProductInit(productInitId, pinMing, jianHao, carModel, initNum, initDate, carrierName)"
                        + "values(productInitId.NEXTVAL, ?, ?, ?, ?, ?, ?)");
                for (ProductInit infoImport : imports) {
                    statement.setString(1, infoImport.getPinMing());
                    statement.setString(2, infoImport.getJianHao());
                    statement.setString(3, infoImport.getCarModel());
                    statement.setInt(4, infoImport.getInitNum());
                    statement.setString(5, infoImport.getInitDate());
                    statement.setString(6, infoImport.getCarrierName());
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
    
    /**
     * 查询原材料期初
     * @param carrierName
     * @param jianHaoName
     * @param carModel
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<ProductInit> getData(String carrierName, String jianHaoName, String carModel,
            String beginTime, String endTime, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<ProductInit> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(carrierName)) {
                if (caseCount == 0)
                    whereCase += ("carrierName like '%" + carrierName + "%'");
                else
                    whereCase += (" and carrierName like '%" + carrierName + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(jianHaoName)) {
                if (caseCount == 0)
                    whereCase += ("pinMing like '%" + jianHaoName + "%'");
                else
                    whereCase += (" and pinMing like '%" + jianHaoName + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(carModel)) {
                if (caseCount == 0)
                    whereCase += ("planCarModel like '%" + carModel + "%'");
                else
                    whereCase += (" and planCarModel like '%" + carModel + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(beginTime) && !Units.strIsEmpty(endTime)) {
                if (caseCount == 0)
                    whereCase += ("initDate >= '" + beginTime + "' and initDate <= '" + endTime + "'");
                else
                    whereCase += ("and initDate >= '" + beginTime + "' and initDate <= '" + endTime + "'");
                caseCount++;
            }
            
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbProductInit");
            statement.setString("fields", "*");
            statement.setString("wherecase", whereCase);
            statement.setInt("pageSize", pageSize);
            statement.setInt("pageNow", pageIndex);
            statement.setString("orderField", null);
            statement.setInt("orderFlag", 0);
            statement.registerOutParameter("myrows", OracleTypes.NUMBER);
            statement.registerOutParameter("myPageCount", OracleTypes.NUMBER);
            statement.registerOutParameter("p_cursor", OracleTypes.CURSOR);
            statement.execute();
            ResultSet set = (ResultSet) statement.getObject("p_cursor");
            result = new ArrayList<>();
            while (set.next()) {
                ProductInit info = new ProductInit();
                info.setProductInitId(set.getInt("productInitId"));
                info.setPinMing(set.getString("pinMing"));
                info.setJianHao(set.getString("jianHao"));
                info.setCarModel(set.getString("carModel"));
                info.setInitNum(set.getInt("initNum"));
                info.setInitDate(set.getString("initDate"));
                info.setCarrierName(set.getString("carrierName"));
                result.add(info);
            }
            ProductInit.setRecordCount(statement.getInt("myrows"));
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
