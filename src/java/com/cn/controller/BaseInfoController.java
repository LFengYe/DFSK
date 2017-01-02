/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.BaseInfo;
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
import java.sql.Types;
import java.util.ArrayList;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author LFeng
 */
public class BaseInfoController {
    private static final Logger logger = Logger.getLogger(BaseInfoController.class);

    /**
     * 导入基础数据
     *
     * @param fileName
     * @return
     */
    public int[] importData(String fileName) {
        InputStream inputStream = null;
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            ArrayList<BaseInfo> imports = new ArrayList<>();
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
//                logger.info("count row num:" + sheet.getPhysicalNumberOfRows() + ",the row num is:" + i);
                HSSFRow row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }

                int cellNum = row.getPhysicalNumberOfCells();
//                logger.info("count cell num is:" + cellNum);
                if (cellNum >= 4) {
                    BaseInfo info = new BaseInfo();
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    info.setPinMing(row.getCell(0).getStringCellValue());
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    info.setJianHao(row.getCell(1).getStringCellValue());
                    if (Units.strIsEmpty(info.getJianHao()))
                        continue;
                    row.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                    info.setCarNum((int) row.getCell(3).getNumericCellValue());
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    info.setCarModel(row.getCell(2).getStringCellValue());
                    
                    imports.add(info);
                }
            }
            DatabaseOpt opt;
            Connection conn = null;
            CallableStatement statement = null;
            opt = new DatabaseOpt();
            try {
                conn = opt.getConnect();
                statement = conn.prepareCall("insert into tbBaseInfo(baseId, pinMing, jianHao, carModel, carNum)"
                        + "values(BASEID.NEXTVAL, ?, ?, ?, ?)");
                for (BaseInfo infoImport : imports) {
                    statement.setString(1, infoImport.getPinMing());
                    statement.setString(2, infoImport.getJianHao());
                    statement.setString(3, infoImport.getCarModel());
                    statement.setInt(4, infoImport.getCarNum());
                    statement.addBatch();
                }
                return statement.executeBatch();
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
        return null;
    }

    /**
     * 
     * @param jianHaoName
     * @param jianHao
     * @param carModel
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<BaseInfo> getData(String jianHaoName, String jianHao, String carModel, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<BaseInfo> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(jianHaoName)) {
                if (caseCount == 0)
                    whereCase += ("pinMing like '%" + jianHaoName + "%'");
                else
                    whereCase += (" and pinMing like '%" + jianHaoName + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(jianHao)) {
                if (caseCount == 0)
                    whereCase += ("jianHao like '%" + jianHao + "%'");
                else
                    whereCase += (" and jianHao like '%" + jianHao + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(carModel)) {
                if (caseCount == 0)
                    whereCase += ("carModel like '%" + carModel + "%'");
                else
                    whereCase += (" and carModel like '%" + carModel + "%'");
                caseCount++;
            }
            
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbBaseInfo");
            statement.setString("fields", "*");
            statement.setString("wherecase", whereCase);
            statement.setInt("pageSize", pageSize);
            statement.setInt("pageNow", pageIndex);
            statement.setString("orderField", "baseId");
            statement.setInt("orderFlag", 0);
            statement.registerOutParameter("myrows", OracleTypes.NUMBER);
            statement.registerOutParameter("myPageCount", OracleTypes.NUMBER);
            statement.registerOutParameter("p_cursor", OracleTypes.CURSOR);
            statement.execute();
            ResultSet set = (ResultSet) statement.getObject("p_cursor");
            result = new ArrayList<>();
            while (set.next()) {
                BaseInfo info = new BaseInfo();
                info.setBaseId(set.getInt("baseId"));
                info.setPinMing(set.getString("pinMing"));
                info.setJianHao(set.getString("jianHao"));
                info.setCarModel(set.getString("carModel"));
                info.setCarNum(set.getInt("carNum"));
                result.add(info);
            }
            BaseInfo.recordCount = statement.getInt("myrows");
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
