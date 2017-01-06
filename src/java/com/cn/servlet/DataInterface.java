/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.BaseInfo;
import com.cn.bean.MaterialIn;
import com.cn.bean.MaterialBaseInfo;
import com.cn.bean.MaterialExportTemplate;
import com.cn.bean.MaterialInit;
import com.cn.bean.MaterialOut;
import com.cn.bean.MaterialReport;
import com.cn.bean.MaterialReportExport;
import com.cn.bean.OrderPlan;
import com.cn.bean.ProductInit;
import com.cn.bean.ProductReport;
import com.cn.bean.ProductReportExport;
import com.cn.bean.UserInfo;
import com.cn.bean.ProductWarehouseIn;
import com.cn.bean.ProductWarehouseOut;
import com.cn.controller.BaseInfoController;
import com.cn.controller.MaterialInController;
import com.cn.controller.MaterialBaseInfoController;
import com.cn.controller.MaterialInitController;
import com.cn.controller.MaterialOutController;
import com.cn.controller.OrderPlanController;
import com.cn.controller.ProductInitController;
import com.cn.controller.ReportController;
import com.cn.controller.UserInfoController;
import com.cn.controller.WarehouseInController;
import com.cn.controller.WarehouseOutController;
import com.cn.util.ExportExcel;
import com.cn.util.Units;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class DataInterface extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DataInterface.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param params
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String params)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String subUri = uri.substring(uri.lastIndexOf("/") + 1,
                uri.lastIndexOf("."));
        String json = null;
        try {
            /*
            String os = SystemTool.getOSName();
            System.out.println("OS Name:" + os);
            String MacAddress;
            if (os.equals("windows 7")) {
                MacAddress = SystemTool.getMACAddress();
            } else if (os.startsWith("windows")) {
                MacAddress = SystemTool.getWindowsMACAddress();
            } else {
                MacAddress = SystemTool.getUnixMACAddress();
            }
            System.out.println("MAC:" + MacAddress);
            */
            System.out.println("params:" + params);
            JSONObject paramsJson = JSONObject.parseObject(params);
            HttpSession session = request.getSession();
            /*验证是否登陆*/
            if (!"userLogin".equals(subUri) && session.getAttribute("user") == null) {
                session.invalidate();
                json = Units.objectToJson(-99, "未登陆", null);
                PrintWriter out = response.getWriter();
                try {
                    response.setContentType("text/html;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-store");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                    out.print(json);
                } finally {
                    out.close();
                }
                return;
            }

            switch (subUri) {
                //<editor-fold desc="基础信息相关接口" defaultstate="collapsed">
                case "baseInfoImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    BaseInfoController controller = new BaseInfoController();
                    int result = controller.importData(loadpath + fileName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "baseInfoGet": {
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String jianHao = paramsJson.getString("jianHao");
                    String carModel = paramsJson.getString("carModel");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    BaseInfoController controller = new BaseInfoController();
                    ArrayList<BaseInfo> result = controller.getData(jianHaoName, jianHao, carModel, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, BaseInfo.recordCount);
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="原材料基础信息" defaultstate="collapsed">
                case "materialInfoImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    String carrierName = paramsJson.getString("carrierName");
                    MaterialBaseInfoController controller = new MaterialBaseInfoController();
                    int result = controller.importData(loadpath + fileName, carrierName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "materialInfoGet": {
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String jianHao = paramsJson.getString("jianHao");
                    String carModel = paramsJson.getString("carModel");
                    String carrierName = paramsJson.getString("carrierName");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    MaterialBaseInfoController controller = new MaterialBaseInfoController();
                    ArrayList<MaterialBaseInfo> result = controller.getData(jianHaoName, jianHao, carrierName, carModel, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, MaterialBaseInfo.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                case "exportMaterialTemplate": {
                    String carrierName = paramsJson.getString("carrierName");
                    MaterialBaseInfoController controller = new MaterialBaseInfoController();
                    ArrayList<MaterialBaseInfo> result = controller.getData("", "", carrierName, "", Integer.MAX_VALUE, 1);
                    ArrayList<MaterialExportTemplate> exportData = new ArrayList<>();
                    for (MaterialBaseInfo info : result) {
                        MaterialExportTemplate template = new MaterialExportTemplate();
                        template.setPinMing(info.getPinMing());
                        template.setJianHao(info.getJianHao());
                        template.setCarModel(info.getCarModel());
                        template.setProductJianHao(info.getProductJianHao());
                        exportData.add(template);
                    }
                    
                    ExportExcel<MaterialExportTemplate> exportExcel = new ExportExcel<>();
                    //response.setHeader("Content-disposition", "attachment;filename=" + Units.getNowTime() + ".xls");
                    String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
                    String fileName = "原材料录入模板_" + Units.getNowDate() + ".xls";
                    File file = Units.createNewFile(filePath, fileName);
                    OutputStream stream = new FileOutputStream(file);
                    //exportExcel.exportExcel(result, stream);
                    String[] headers = new String[]{"件号名称", "件号", "车型", "成品件号", "数量", "备注"};
                    exportExcel.exportExcel("原材料", headers, exportData, stream, "yyyy-MM-dd HH:mm:ss");
                    json = Units.objectToJson(0, "导出成功!", getServletContext().getContextPath() + "/exportFile/" + fileName);
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="原材料期初" defaultstate="collapsed">
                case "materialInitImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    String carrierName = paramsJson.getString("carrierName");
                    MaterialInitController controller = new MaterialInitController();
                    int result = controller.importData(loadpath + fileName, carrierName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "materialInitGet": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String carModel = paramsJson.getString("carModel");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    MaterialInitController controller = new MaterialInitController();
                    ArrayList<MaterialInit> result = controller.getData(carrierName, jianHaoName, carModel, beginTime, endTime, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, BaseInfo.recordCount);
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="成品期初" defaultstate="collapsed">
                case "productInitImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    String carrierName = paramsJson.getString("carrierName");
                    ProductInitController controller = new ProductInitController();
                    int result = controller.importData(loadpath + fileName, carrierName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "productInitGet": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String carModel = paramsJson.getString("carModel");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    ProductInitController controller = new ProductInitController();
                    ArrayList<ProductInit> result = controller.getData(carrierName, jianHaoName, carModel, beginTime, endTime, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, BaseInfo.recordCount);
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="计划相关接口" defaultstate="collapsed">
                case "orderPlanImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    int planType = paramsJson.getIntValue("planType");
                    OrderPlanController controller = new OrderPlanController();
                    int result = controller.importData(loadpath + fileName, planType);
                    if (result == 0) {
                        json = Units.objectToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        if (result == -3) {
                            json = Units.objectToJson(result, "导入的数据格式错误!", null);
                        } else if (result == -2) {
                            json = Units.objectToJson(result, "导入的文件格式错误!", null);
                        } else {
                            json = Units.objectToJson(result, "导入失败!", null);
                        }
                    }
                    break;
                }
                case "orderPlanGet": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String jianHao = paramsJson.getString("jianHao");
                    String carrierCode = paramsJson.getString("carrierCode");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    String searchField = paramsJson.getString("searchField");
                    String searchValue = paramsJson.getString("searchValue");
                    String orderField = paramsJson.getString("orderField");
                    int orderFlag = paramsJson.getIntValue("orderFlag");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    OrderPlanController controller = new OrderPlanController();
                    ArrayList<OrderPlan> result = controller.getData(carrierName, jianHaoName, jianHao, carrierCode, beginTime, endTime,
                            searchField, searchValue, orderField, orderFlag, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, OrderPlan.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="用户信息接口" defaultstate="collapsed">
                case "userInfoAdd": {
                    String username = paramsJson.getString("username");
                    String password = paramsJson.getString("password");
                    String carrierCode = paramsJson.getString("carrierCode");
                    int userType = paramsJson.getIntValue("userType");
                    String contact = paramsJson.getString("contact");
                    String remark = paramsJson.getString("remark");
                    UserInfoController controller = new UserInfoController();
                    int result = controller.addUserInfo(username, password, carrierCode, contact, userType, remark);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "用户名已存在!", null);
                    }
                    break;
                }
                case "userInfoGet": {
                    String username = paramsJson.getString("userName");
                    String carrierCode = paramsJson.getString("carrierCode");
                    int userType = paramsJson.getIntValue("userType");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    UserInfoController controller = new UserInfoController();
                    ArrayList<UserInfo> result = controller.getData(username, carrierCode, userType, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, UserInfo.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                case "userLogin": {
                    String username = paramsJson.getString("username");
                    String carrierCode = paramsJson.getString("username");
                    String password = paramsJson.getString("password");
                    UserInfoController controller = new UserInfoController();
                    ArrayList<UserInfo> result = controller.userLogin(username, carrierCode, -1, 11, 1);
                    if (result != null && result.size() > 0) {
                        UserInfo user = result.get(0);
                        if (password.compareTo(user.getPassword()) == 0) {
                            json = Units.objectToJson(0, "登陆成功", user);
                            session.setAttribute("user", user);
                        } else {
                            json = Units.objectToJson(-1, "用户名或密码输入不正确", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "用户名不存在", null);
                    }
                    break;
                }
                case "modifyPass": {
                    String userName = paramsJson.getString("userName");
                    String oldPassword = paramsJson.getString("old_password");
                    String newPassword = paramsJson.getString("new_password");
                    UserInfoController controller = new UserInfoController();
                    ArrayList<UserInfo> result = controller.getData(userName, null, -1, 1, 1);
                    if (result != null && result.size() > 0) {
                        UserInfo info = result.get(0);
                        if (oldPassword.compareTo(info.getPassword()) == 0) {
                            int modifyRes = controller.modifyPass(userName, newPassword);
                            if (modifyRes == 1) {
                                json = Units.objectToJson(0, "修改成功, 下次请使用新密码登陆!", null);
                            } else {
                                json = Units.objectToJson(-1, "修改出错!", null);
                            }
                        } else {
                            json = Units.objectToJson(-1, "修改出错!(旧密码不正确)", null);
                        }
                    } else {
                        json = Units.objectToJson(-1, "修改出错!(用户信息错误)", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="原材料入库接口" defaultstate="collapsed">
                case "materialInImport": {
                    String userName = paramsJson.getString("carrierName");
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    MaterialInController controller = new MaterialInController();
                    int result = controller.importData(loadpath + fileName, userName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "materialInAdd": {
                    String userName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    int countNum = paramsJson.getIntValue("countNum");
                    String remark = paramsJson.getString("remark");
                    MaterialInController controller = new MaterialInController();
                    int result = controller.addMaterial(userName, jianHaoName, countNum, remark);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "materialInAddBatch": {
                    String userName = paramsJson.getString("carrierName");
                    List<MaterialIn> data = JSON.parseArray(paramsJson.getString("data"), MaterialIn.class);
                    MaterialInController controller = new MaterialInController();
                    int result = controller.batchAddMaterial(userName, data);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "materialInGet": {
                    String userName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String carModel = paramsJson.getString("carModel");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    MaterialInController controller = new MaterialInController();
                    ArrayList<MaterialIn> result = controller.getData(jianHaoName, carModel, userName, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, MaterialIn.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="原材料出库接口" defaultstate="collapsed">
                case "materialOutImport": {
                    String userName = paramsJson.getString("carrierName");
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    MaterialOutController controller = new MaterialOutController();
                    int result = controller.importData(loadpath + fileName, userName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "materialOutAddBatch": {
                    String userName = paramsJson.getString("carrierName");
                    List<MaterialOut> data = JSON.parseArray(paramsJson.getString("data"), MaterialOut.class);
                    MaterialOutController controller = new MaterialOutController();
                    int result = controller.batchAddMaterial(userName, data);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "materialOutGet": {
                    String userName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String carModel = paramsJson.getString("carModel");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    MaterialOutController controller = new MaterialOutController();
                    ArrayList<MaterialOut> result = controller.getData(jianHaoName, carModel, userName, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, MaterialOut.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="成品入库接口" defaultstate="collapsed">
                case "warehouseInImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    BaseInfoController controller = new BaseInfoController();
                    int result = controller.importData(loadpath + fileName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "warehouseInAdd": {
                    String userName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    int countNum = paramsJson.getIntValue("countNum");
                    String remark = paramsJson.getString("remark");
                    WarehouseInController controller = new WarehouseInController();
                    int result = controller.addWarehouseIn( userName,jianHaoName, countNum, remark);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "warehouseInAddBatch": {
                    String userName = paramsJson.getString("carrierName");
                    List<ProductWarehouseIn> data = JSON.parseArray(paramsJson.getString("data"), ProductWarehouseIn.class);
                    WarehouseInController controller = new WarehouseInController();
                    int result = controller.batchAddWarehouseIn(userName, data);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "warehouseInGet": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    WarehouseInController controller = new WarehouseInController();
                    ArrayList<ProductWarehouseIn> result = controller.getData(jianHaoName, jianHao, beginTime, endTime, carrierName, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, ProductWarehouseIn.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="成品出库接口" defaultstate="collapsed">
                case "warehouseOutImport": {
                    String loadpath = getServletContext().getRealPath("/").replace("\\", "/") + "excelFile/"; //上传文件存放目录
                    String fileName = paramsJson.getString("fileName");
                    BaseInfoController controller = new BaseInfoController();
                    int result = controller.importData(loadpath + fileName);
                    if (result == 0) {
                        json = Units.jsonStrToJson(0, "导入成功!", null);
                    } else {
                        File file = new File(loadpath + fileName);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                        json = Units.objectToJson(-1, "存在重复记录!", null);
                    }
                    break;
                }
                case "warehouseOutAdd": {
                    String userName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    int countNum = paramsJson.getIntValue("countNum");
                    String remark = paramsJson.getString("remark");
                    WarehouseOutController controller = new WarehouseOutController();
                    int result = controller.addWarehouseOut( userName,jianHaoName, countNum, remark);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "warehouseOutAddBatch": {
                    String userName = paramsJson.getString("carrierName");
                    List<ProductWarehouseOut> data = JSON.parseArray(paramsJson.getString("data"), ProductWarehouseOut.class);
                    WarehouseOutController controller = new WarehouseOutController();
                    int result = controller.batchAddWarehouseOut(userName, data);
                    if (result == 0) {
                        json = Units.objectToJson(result, "添加成功!", null);
                    } else {
                        json = Units.objectToJson(result, "添加失败!", null);
                    }
                    break;
                }
                case "warehouseOutGet": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHaoName = paramsJson.getString("jianHaoName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    WarehouseOutController controller = new WarehouseOutController();
                    ArrayList<ProductWarehouseOut> result = controller.getData(jianHaoName, jianHao, beginTime, endTime, carrierName, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, ProductWarehouseOut.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                //</editor-fold>
                
                //<editor-fold desc="报表接口" defaultstate="collapsed">
                case "reportWithTime": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    ReportController controller = new ReportController();
                    ArrayList<ProductReport> result = controller.getProductReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, ProductReport.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                case "exportProductReport": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("exportPageSize");
                    int pageIndex = paramsJson.getIntValue("exportPageIndex");
                    ReportController controller = new ReportController();
                    ArrayList<ProductReport> result = controller.getProductReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
                    ArrayList<ProductReportExport> exportData = new ArrayList<>();
                    for (ProductReport item : result) {
                        ProductReportExport reportExport = new ProductReportExport();
                        reportExport.setInitStock(item.getInitStock());
                        reportExport.setPlanCount(item.getPlanCount());
                        reportExport.setJianHao(item.getJianHao());
                        reportExport.setJianHaoName(item.getJianHaoName());
                        reportExport.setProductInCount(item.getProductInCount());
                        reportExport.setProductInToday(item.getProductInToday());
                        reportExport.setProductOutCount(item.getProductOutCount());
                        reportExport.setProductOutToday(item.getProductOutToday());
                        reportExport.setProductStock(item.getProductStock());
                        exportData.add(reportExport);
                    }
                    ExportExcel<ProductReportExport> exportExcel = new ExportExcel<>();
                    //response.setHeader("Content-disposition", "attachment;filename=" + Units.getNowTime() + ".xls");
                    String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
                    String fileName = "成品报表_" + Units.getNowDate() + ".xls";
                    File file = Units.createNewFile(filePath, fileName);
                    OutputStream stream = new FileOutputStream(file);
                    //exportExcel.exportExcel(result, stream);
                    String[] headers = new String[]{"计划数量", "件号名称", "件号", "期初库存", "今日入库", "累计入库", "今日出库", "累计出库", "库存"};
                    exportExcel.exportExcel("时间段报表", headers, exportData, stream, "yyyy-MM-dd HH:mm:ss");
                    json = Units.objectToJson(0, "导出成功!", getServletContext().getContextPath() + "/exportFile/" + fileName);
                    break;
                }
                case "getMaterialReportWithTime": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("pageSize");
                    int pageIndex = paramsJson.getIntValue("pageIndex");
                    ReportController controller = new ReportController();
                    ArrayList<MaterialReport> result = controller.getMaterialReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
                    if (result != null) {
                        json = Units.listToJson(result, MaterialReport.getRecordCount());
                    } else {
                        json = Units.objectToJson(-1, "记录为空", null);
                    }
                    break;
                }
                case "exportMaterialReport": {
                    String carrierName = paramsJson.getString("carrierName");
                    String jianHao = paramsJson.getString("jianHao");
                    String beginTime = paramsJson.getString("beginTime");
                    String endTime = paramsJson.getString("endTime");
                    int pageSize = paramsJson.getIntValue("exportPageSize");
                    int pageIndex = paramsJson.getIntValue("exportPageIndex");
                    ReportController controller = new ReportController();
                    ArrayList<MaterialReport> result = controller.getMaterialReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
                    ArrayList<MaterialReportExport> exportData = new ArrayList<>();
                    for (MaterialReport item : result) {
                        MaterialReportExport reportExport = new MaterialReportExport();
                        reportExport.setInitStock(item.getInitStock());
                        reportExport.setJianHao(item.getJianHao());
                        reportExport.setJianHaoName(item.getJianHaoName());
                        reportExport.setMaterialInCount(item.getMaterialInCount());
                        reportExport.setMaterialInToday(item.getMaterialInToday());
                        reportExport.setMaterialOutCount(item.getMaterialOutCount());
                        reportExport.setMaterialOutToday(item.getMaterialOutToday());
                        reportExport.setMaterialStock(item.getMaterialStock());
                        exportData.add(reportExport);
                    }
                    ExportExcel<MaterialReportExport> exportExcel = new ExportExcel<>();
                    String filePath = getServletContext().getRealPath("/").replace("\\", "/") + "exportFile/";
                    String fileName = "原材料报表_" + Units.getNowDate() + ".xls";
                    File file = Units.createNewFile(filePath, fileName);
                    OutputStream stream = new FileOutputStream(file);
                    //exportExcel.exportExcel(result, stream);
                    String[] headers = new String[]{"计划数量", "件号名称", "件号", "期初库存", "今日入库", "累计入库", "今日出库", "累计出库", "库存"};
                    exportExcel.exportExcel("时间段报表", headers, exportData, stream, "yyyy-MM-dd HH:mm:ss");
                    json = Units.objectToJson(0, "导出成功!", getServletContext().getContextPath() + "/exportFile/" + fileName);
                    break;
                }
                //</editor-fold>
            }
        } catch (Exception e) {
            logger.error("数据库执行错误", e);
        }

        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            out.print(json);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = request.getParameter("params");
        processRequest(request, response, params);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String params = getRequestPostStr(request);
        processRequest(request, response, params);
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * <pre>
     * 举例：
     * </pre>
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * 根据request获取Post参数
     *
     * @param request
     * @return
     * @throws IOException (最大只能读取1133个字符, 原因还未找到)
     */
    private String getPostParameter(HttpServletRequest request) throws IOException {
        BufferedInputStream buf = null;
        int iContentLen = request.getContentLength();
        if (iContentLen > 0) {
            byte sContent[] = new byte[iContentLen];
            String sContent2 = null;
            try {
                buf = new BufferedInputStream(request.getInputStream(), iContentLen);
                buf.read(sContent, 0, sContent.length);
                sContent2 = new String(sContent, 0, iContentLen, "UTF-8");
//                System.out.println("content:" + sContent2 + ",len:" + sContent2.length());
            } catch (IOException e) {
                throw new IOException("Parse data error!", e);
            } finally {
                if (null != buf) {
                    buf.close();
                }
            }
            return sContent2;
        }
        return null;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
