/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.MaterialIn;
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
import java.util.ArrayList;
import java.util.List;
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
public class MaterialInController {

    private static final Logger logger = Logger.getLogger(MaterialInController.class);

    public int importData(String fileName, String carrierName) {
        InputStream inputStream = null;
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            ArrayList<MaterialIn> imports = new ArrayList<>();
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
//                logger.info("count row num:" + sheet.getPhysicalNumberOfRows() + ",the row num is:" + i);
                HSSFRow row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }

                int cellNum = row.getPhysicalNumberOfCells();
//                logger.info("count cell num is:" + cellNum);
                if (cellNum >= 5) {
                    MaterialIn info = new MaterialIn();

                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    info.setPinMing(row.getCell(0).getStringCellValue());
                    row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    info.setJianHao(row.getCell(1).getStringCellValue());
                    row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                    info.setCarModel(row.getCell(2).getStringCellValue());
                    row.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                    info.setCarCount((int) row.getCell(3).getNumericCellValue());
                    row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                    info.setRemark(row.getCell(4).getStringCellValue());

                    imports.add(info);
                }
            }
            return batchAddMaterial(carrierName, imports);

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

    public int batchAddMaterial(String carrierName, List<MaterialIn> imports) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        opt = new DatabaseOpt();
        try {
            conn = opt.getConnect();
            conn.setAutoCommit(false);
            statement = conn.prepareCall("insert into tbOrderMaterialIn(materialInID, PinMing, JianHao, CarModel, CarCount, AddTime, Remark, carrierName)"
                    + "values(MATERIALINID.NEXTVAL, ?, ?, ?, ?, TO_DATE(TO_CHAR(SYSDATE, 'yyyy-mm-dd hh24:mi:ss'), 'yyyy-mm-dd hh24:mi:ss'), ?, ?)");
            for (MaterialIn infoImport : imports) {
                statement.setString(1, infoImport.getPinMing());
                statement.setString(2, infoImport.getJianHao());
                statement.setString(3, infoImport.getCarModel());
                statement.setInt(4, infoImport.getCarCount());
                statement.setString(5, infoImport.getRemark());
                statement.setString(6, carrierName);
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
     * 添加原材料入库
     *
     * @param userName
     * @param jianHaoName
     * @param countNum
     * @param remark
     * @return
     */
    public int addMaterial(String userName, String jianHaoName, int countNum, String remark) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        int result = -1;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("{call tbOrderMaterialInAdd(?, ?, ?, ?)}");
            statement.setString(1, jianHaoName);
            statement.setInt(2, countNum);
            statement.setString(3, remark);
            statement.setString(4, userName);
            result = statement.executeUpdate();
            if (result == 1) {
                return 0;
            }
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
        return result;
    }

    /**
     * 获取原材料库存
     *
     * @param jianHaoName
     * @param carModel
     * @param carrierName
     * @param pageSize
     * @param pageIndex
     * @return
     */
    public ArrayList<MaterialIn> getData(String jianHaoName, String carModel, String carrierName, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<MaterialIn> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(jianHaoName)) {
                if (caseCount == 0) {
                    whereCase += ("PinMing like '%" + jianHaoName + "%'");
                } else {
                    whereCase += (" and PinMing like '%" + jianHaoName + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(carModel)) {
                if (caseCount == 0) {
                    whereCase += ("CarModel like '%" + carModel + "%'");
                } else {
                    whereCase += (" and CarModel like '%" + carModel + "%'");
                }
                caseCount++;
            }
            if (!Units.strIsEmpty(carrierName)) {
                if (caseCount == 0) {
                    whereCase += ("carrierName like '%" + carrierName + "%'");
                } else {
                    whereCase += (" and carrierName like '%" + carrierName + "%'");
                }
                caseCount++;
            }
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbOrderMaterialIn");
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
                MaterialIn info = new MaterialIn();
                info.setMaterialInId(set.getInt("materialInID"));
                info.setPinMing(set.getString("PinMing"));
                info.setJianHao(set.getString("JianHao"));
                info.setCarModel(set.getString("CarModel"));
                info.setCarCount(set.getInt("CarCount"));
                info.setAddTime(set.getString("AddTime"));
                info.setRemark(set.getString("Remark"));
                result.add(info);
            }
            MaterialIn.setRecordCount(statement.getInt("myrows"));
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
