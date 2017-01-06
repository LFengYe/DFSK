/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.ProductWarehouseIn;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class WarehouseInController {
    private static final Logger logger = Logger.getLogger(WarehouseInController.class);
    
    public int batchAddWarehouseIn(String userName, List<ProductWarehouseIn> imports) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        opt = new DatabaseOpt();
        try {
            conn = opt.getConnect();
            conn.setAutoCommit(false);
            statement = conn.prepareCall("insert into tbOrderWareHouseIn(warehouseInId, PinMing, JianHao, CarCount, AddTime, Remark, carrierName)"
                    + "values(WAREHOUSEINID.NEXTVAL, ?, ?, ?, TO_DATE(TO_CHAR(SYSDATE, 'yyyy-mm-dd hh24:mi:ss'), 'yyyy-mm-dd hh24:mi:ss'), ?, ?)");
            for (ProductWarehouseIn infoImport : imports) {
                statement.setString(1, infoImport.getPinMing());
                statement.setString(2, infoImport.getJianHao());
                statement.setInt(3, infoImport.getCarCount());
                statement.setString(4, infoImport.getRemark());
                statement.setString(5, userName);
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
     * 添加成品入库
     * @param userName
     * @param jianHaoName
     * @param countNum
     * @param remark
     * @return 
     */
    public int addWarehouseIn(String userName, String jianHaoName, int countNum, String remark) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        int result = -1;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("{call tbOrderWarehouseInAdd(?, ?, ?, ?)}");
            statement.setString(1, jianHaoName);
            statement.setInt(2, countNum);
            statement.setString(3, remark);
            statement.setString(4, userName);
            result = statement.executeUpdate();
            if (result == 1)
                return 0;
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
     * 获取成品入库
     * @param jianHaoName
     * @param jianHao
     * @param beginTime
     * @param endTime
     * @param carrierName
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<ProductWarehouseIn> getData(String jianHaoName, String jianHao, String beginTime, String endTime,
            String carrierName, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<ProductWarehouseIn> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(jianHaoName)) {
                if (caseCount == 0)
                    whereCase += ("PinMing like '%" + jianHaoName + "%'");
                else
                    whereCase += (" and PinMing like '%" + jianHaoName + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(jianHao)) {
                if (caseCount == 0)
                    whereCase += ("JianHao like '%" + jianHao + "%'");
                else
                    whereCase += (" and JianHao like '%" + jianHao + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(carrierName)) {
                if (caseCount == 0)
                    whereCase += ("carrierName like '%" + carrierName + "%'");
                else
                    whereCase += (" and carrierName like '%" + carrierName + "%'");
                caseCount++;
            }
            if (!Units.strIsEmpty(beginTime) && !Units.strIsEmpty(endTime)) {
                if (caseCount == 0)
                    whereCase += ("AddTime between TO_DATE('" + beginTime + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
                else
                    whereCase += ("and AddTime between TO_DATE('" + beginTime + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
                caseCount++;
            }
            
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbOrderWarehouseIn");
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
                ProductWarehouseIn info = new ProductWarehouseIn();
                info.setWarehouseInId(set.getInt("warehouseInId"));
                info.setPinMing(set.getString("PinMing"));
                info.setJianHao(set.getString("JianHao"));
//                info.setCountNum(set.getInt("CountNum"));
                info.setCarCount(set.getInt("CarCount"));
                info.setAddTime(set.getString("AddTime"));
                info.setRemark(set.getString("Remark"));
                result.add(info);
            }
            ProductWarehouseIn.setRecordCount(statement.getInt("myrows"));
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
