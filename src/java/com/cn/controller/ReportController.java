/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.MaterialReport;
import com.cn.bean.ProductReport;
import com.cn.util.DatabaseOpt;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class ReportController {
    private static final Logger logger = Logger.getLogger(ReportController.class);
    
    public ArrayList<ProductReport> getProductReport(String beginTime, String endTime, String carrierName) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<ProductReport> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String sql = "select p.JIANHAO, p.PINMING, sum(p.CARCOUNT) as planCount";
            sql += ",nvl((select sum(pi.INITNUM) from tbProductInit pi where pi.JIANHAO = p.JIANHAO), 0) as initStock";
//            sql += ",(select b.PINMING from tbBaseInfo b where b.JIANHAO = p.PLANJIANHAO) as jianHaoName";
//            sql += ",nvl((select sum(m.MATERIALCARCOUNT) from tbOrderMaterial m where m.MATERIALJIANHAO = p.PLANJIANHAO and m.MATERIALADDTIME between TO_DATE('" + endTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)as materialInToday";
            sql += ",nvl((select sum(i.CARCOUNT) from tbOrderWarehouseIn i where i.JIANHAO = p.JIANHAO and i.ADDTIME between TO_DATE('" + endTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as productInToday";
//            sql += ",nvl((select sum(m.MATERIALCARCOUNT) from tbOrderMaterial m where m.MATERIALJIANHAO = p.PLANJIANHAO and m.MATERIALADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as materialInCount";
            sql += ",nvl((select sum(i.CARCOUNT) from tbOrderWarehouseIn i where i.JIANHAO = p.JIANHAO and i.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as productInCount";
            sql += ",nvl((select sum(o.CARCOUNT) from tbOrderWarehouseOut o where o.JIANHAO = p.JIANHAO and o.ADDTIME between TO_DATE('" + endTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as productOutToday";
            sql += ",nvl((select sum(o.CARCOUNT) from tbOrderWarehouseOut o where o.JIANHAO = p.JIANHAO and o.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as productOutCount";
//            sql += ",(nvl((select sum(m.MATERIALCARCOUNT) from tbOrderMaterial m where m.MATERIALJIANHAO = p.PLANJIANHAO and m.MATERIALADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) - nvl((select sum(i.WAREHOUSEINCARCOUNT) from tbOrderWarehouseIn i where i.WAREHOUSEINJIANHAO = p.PLANJIANHAO and i.WAREHOUSEINADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)) as materialStock";
            sql += ",(nvl((select sum(i.CARCOUNT) from tbOrderWarehouseIn i where i.JIANHAO = p.JIANHAO and i.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) - nvl((select sum(o.CARCOUNT) from tbOrderWarehouseOut o where o.JIANHAO = p.JIANHAO and o.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)) as productStock";
            sql += " from tbOrderPlan p group by p.JIANHAO, p.PINMING";
            statement = conn.prepareCall(sql);
            ResultSet set = statement.executeQuery();
            result = new ArrayList<>();
            while (set.next()) {
                ProductReport report = new ProductReport();
                report.setPlanCount(set.getInt("planCount"));
                report.setJianHaoName(set.getString("PinMing"));
                report.setJianHao(set.getString("JianHao"));
                report.setInitStock(set.getInt("initStock"));
//                report.setMaterialInToday(set.getInt("materialInToday"));
                report.setProductInToday(set.getInt("productInToday"));
//                report.setMaterialInCount(set.getInt("materialInCount"));
                report.setProductInCount(set.getInt("productInCount"));
                report.setProductOutToday(set.getInt("productOutToday"));
                report.setProductOutCount(set.getInt("productOutCount"));
//                report.setMaterialStock(set.getInt("materialStock"));
                report.setProductStock(set.getInt("productStock"));
                
                result.add(report);
            }
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
    
    /**
     * 
     * @param beginTime
     * @param endTime
     * @param carrierName
     * @param jianHao
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<ProductReport> getProductReport(String beginTime, String endTime,
            String carrierName, String jianHao, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        OracleCallableStatement statement = null;
        ArrayList<ProductReport> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = (OracleCallableStatement) conn.prepareCall("{call REPORTPACKAGE.TBGETPRODUCTREPORT(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, carrierName);
            statement.setString(2, jianHao);
            statement.setString(3, beginTime);
            statement.setString(4, endTime);
            statement.setInt(5, pageSize);
            statement.setInt(6, pageIndex);
            statement.registerOutParameter(7, OracleTypes.INTEGER);
            statement.registerOutParameter(8, OracleTypes.INTEGER);
            statement.registerOutParameter(9, OracleTypes.ARRAY, "REPORT_DATA");
            statement.execute();
            
            Datum[] datum =  statement.getARRAY(9).getOracleArray();
            result = new ArrayList<>();
            for (Datum item : datum) {
                STRUCT struct = (STRUCT) item;
                Object obj[] = struct.getAttributes();
                ProductReport report = new ProductReport();
                report.setJianHaoName(obj[0].toString());
                report.setJianHao(obj[1].toString());
                report.setPlanCount(Integer.valueOf(obj[2].toString()));
                report.setInitStock(Integer.valueOf(obj[3].toString()));
                report.setProductInToday(Integer.valueOf(obj[4].toString()));
                report.setProductInCount(Integer.valueOf(obj[5].toString()));
                report.setProductOutToday(Integer.valueOf(obj[6].toString()));
                report.setProductOutCount(Integer.valueOf(obj[7].toString()));
                report.setProductStock(Integer.valueOf(obj[8].toString()));
                //System.out.println("0:" + obj[0] + ",1:" + obj[1] + ",2:" + obj[2] + ",3:" + obj[3] + ",4:" + obj[4] + ",5:" + obj[5] + ",6:" + obj[6]);
                result.add(report);
            }
            ProductReport.setRecordCount(statement.getInt(7));
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
    
    public ArrayList<MaterialReport> getMaterialReport(String beginTime, String endTime, String carrierName) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<MaterialReport> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String sql = "select b.JIANHAO, b.PINMING, b.CARRIERNAME";
            sql += ",nvl((select sum(mi.INITNUM) from tbMaterialInit mi where mi.JIANHAO = b.JIANHAO), 0) as initStock";
            sql += ",nvl((select sum(i.CARCOUNT) from tbOrderMaterialIn i where i.JIANHAO = b.JIANHAO and i.ADDTIME between TO_DATE('" + endTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)as materialInToday";
//            sql += ",nvl((select sum(m.MATERIALCARCOUNT) from tbOrderMaterial m where m.MATERIALJIANHAO = p.PLANJIANHAO and m.MATERIALADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) as materialInCount";
            sql += ",nvl((select sum(i.CARCOUNT) from tbOrderMaterialIn i where i.JIANHAO = b.JIANHAO and i.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)as materialInCount";
            sql += ",nvl((select sum(o.CARCOUNT) from tbOrderMaterialOut o where o.JIANHAO = b.JIANHAO and o.ADDTIME between TO_DATE('" + endTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)as materialOutToday";
            sql += ",nvl((select sum(o.CARCOUNT) from tbOrderMaterialOut o where o.JIANHAO = b.JIANHAO and o.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)as materialOutCount";
//            sql += ",(nvl((select sum(m.MATERIALCARCOUNT) from tbOrderMaterial m where m.MATERIALJIANHAO = p.PLANJIANHAO and m.MATERIALADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) - nvl((select sum(i.WAREHOUSEINCARCOUNT) from tbOrderWarehouseIn i where i.WAREHOUSEINJIANHAO = p.PLANJIANHAO and i.WAREHOUSEINADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)) as materialStock";
            sql += ",(nvl((select sum(i.CARCOUNT) from tbOrderMaterialIn i where i.JIANHAO = b.JIANHAO and i.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0) - nvl((select sum(o.CARCOUNT) from tbOrderMaterialOut o where o.JIANHAO = b.JIANHAO and o.ADDTIME between TO_DATE('" + beginTime + "' || ' 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and TO_DATE('" + endTime + "' || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')), 0)) as materialStock";
            sql += " from TBMATERIALBASEINFO b";
            statement = conn.prepareCall(sql);
            ResultSet set = statement.executeQuery();
            result = new ArrayList<>();
            while (set.next()) {
                MaterialReport report = new MaterialReport();
                report.setJianHaoName(set.getString("jianHao"));
                report.setJianHao(set.getString("pinMing"));
                report.setInitStock(set.getInt("initStock"));
                report.setMaterialInToday(set.getInt("materialInToday"));
                report.setMaterialInCount(set.getInt("materialInCount"));
                report.setMaterialOutToday(set.getInt("materialOutToday"));
                report.setMaterialOutCount(set.getInt("materialOutCount"));
                report.setMaterialStock(set.getInt("materialStock"));
                result.add(report);
            }
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
    
    /**
     * 
     * @param beginTime
     * @param endTime
     * @param carrierName
     * @param jianHao
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<MaterialReport> getMaterialReport(String beginTime, String endTime,
            String carrierName, String jianHao, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        OracleCallableStatement statement = null;
        ArrayList<MaterialReport> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = (OracleCallableStatement) conn.prepareCall("{call REPORTPACKAGE.TBGETMATERIALREPORT(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, carrierName);
            statement.setString(2, jianHao);
            statement.setString(3, beginTime);
            statement.setString(4, endTime);
            statement.setInt(5, pageSize);
            statement.setInt(6, pageIndex);
            statement.registerOutParameter(7, OracleTypes.INTEGER);
            statement.registerOutParameter(8, OracleTypes.INTEGER);
            statement.registerOutParameter(9, OracleTypes.ARRAY, "REPORT_DATA");
            statement.execute();
            
            Datum[] datum =  statement.getARRAY(9).getOracleArray();
            result = new ArrayList<>();
            for (Datum item : datum) {
                STRUCT struct = (STRUCT) item;
                Object obj[] = struct.getAttributes();
                MaterialReport report = new MaterialReport();
                report.setJianHaoName(obj[1].toString());
                report.setJianHao(obj[0].toString());
                report.setInitStock(Integer.valueOf(obj[3].toString()));
                report.setMaterialInToday(Integer.valueOf(obj[4].toString()));
                report.setMaterialInCount(Integer.valueOf(obj[5].toString()));
                report.setMaterialOutToday(Integer.valueOf(obj[6].toString()));
                report.setMaterialOutCount(Integer.valueOf(obj[7].toString()));
                report.setMaterialStock(Integer.valueOf(obj[8].toString()));
                //System.out.println("0:" + obj[0] + ",1:" + obj[1] + ",2:" + obj[2] + ",3:" + obj[3] + ",4:" + obj[4] + ",5:" + obj[5] + ",6:" + obj[6]);
                result.add(report);
            }
            MaterialReport.setRecordCount(statement.getInt(7));
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
