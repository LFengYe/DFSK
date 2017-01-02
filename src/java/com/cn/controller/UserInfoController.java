/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.UserInfo;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import oracle.jdbc.OracleTypes;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class UserInfoController {
    private static final Logger logger = Logger.getLogger(UserInfoController.class);
    
    /**
     * 添加用户信息
     * @param userName
     * @param password
     * @param carrierCode
     * @param contact
     * @param userType
     * @param remark
     * @return 
     */
    public int addUserInfo(String userName, String password, String carrierCode, String contact, int userType, String remark) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        int result = -1;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("insert into tbUserInfo(userId, userName, password, userType, carrierCode, userContact, userRemark)"
                    + " values(USERID.NEXTVAL, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, userName);
            statement.setString(2, password);
            statement.setInt(3, userType);
            statement.setString(4, carrierCode);
            statement.setString(5, contact);
            statement.setString(6, remark);
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
    
    public int modifyPass(String userName, String newPassword) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        int result = -1;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("update tbUserInfo set password = ? where userName = ?");
            statement.setString(1, newPassword);
            statement.setString(2, userName);
            result = statement.executeUpdate();
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
        return result;
    }
    
    /**
     * 获取用户信息
     * @param userName
     * @param carrierCode
     * @param userType
     * @param pageSize
     * @param pageIndex
     * @return 
     */
    public ArrayList<UserInfo> getData(String userName, String carrierCode, int userType, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<UserInfo> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "";
            int caseCount = 0;
            if (!Units.strIsEmpty(userName)) {
                if (caseCount == 0)
                    whereCase += ("userName = '" + userName + "'");
                else
                    whereCase += (" and userName = '" + userName + "'");
                caseCount++;
            }
            if (!Units.strIsEmpty(carrierCode)) {
                if (caseCount == 0)
                    whereCase += ("carrierCode = '" + carrierCode + "'");
                else
                    whereCase += (" and carrierCode = '" + carrierCode + "'");
                caseCount++;
            }
            if (userType != -1) {
                if (caseCount == 0)
                    whereCase += ("userType = " + userType);
                else
                    whereCase += (" and userType = " + userType);
                caseCount++;
            }
            
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbUserInfo u left join tbRoleInfo r on u.userType = r.roleId");
            statement.setString("fields", "u.*, r.*");
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
                UserInfo info = new UserInfo();
                info.setUserId(set.getInt("userId"));
                info.setUserName(set.getString("userName"));
                info.setPassword(set.getString("password"));
                info.setUserType(set.getInt("userType"));
                info.setUserContact(set.getString("userContact"));
                info.setUserRemark(set.getString("userRemark"));
                info.setCarrierCode(set.getString("carrierCode"));
                
                info.setRoleId(set.getInt("roleId"));
                info.setRoleName(set.getString("roleName"));
                info.setRoleHomePage(set.getString("roleHomePage"));
                info.setRolePermission(set.getString("rolePermission"));
                result.add(info);
            }
            UserInfo.setRecordCount(statement.getInt("myrows"));
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
    
    public ArrayList<UserInfo> userLogin(String userName, String carrierCode, int userType, int pageSize, int pageIndex) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        ArrayList<UserInfo> result;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            String whereCase = "userName = '" + userName + "'";
            whereCase += " or carrierCode = '" + carrierCode + "'";
            
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("tableName", "tbUserInfo u left join tbRoleInfo r on u.userType = r.roleId");
            statement.setString("fields", "u.*, r.*");
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
                UserInfo info = new UserInfo();
                info.setUserId(set.getInt("userId"));
                info.setUserName(set.getString("userName"));
                info.setPassword(set.getString("password"));
                info.setUserType(set.getInt("userType"));
                info.setUserContact(set.getString("userContact"));
                info.setUserRemark(set.getString("userRemark"));
                info.setCarrierCode(set.getString("carrierCode"));
                
                info.setRoleId(set.getInt("roleId"));
                info.setRoleName(set.getString("roleName"));
                info.setRoleHomePage(set.getString("roleHomePage"));
                info.setRolePermission(set.getString("rolePermission"));
                result.add(info);
            }
            UserInfo.setRecordCount(statement.getInt("myrows"));
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
