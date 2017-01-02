/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
public class MaterialOut {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private int materialOutId;
    private String pinMing;
    private String jianHao;
    private String carModel;
//    private int carNum;//单车用量
//    private int countNum;//总数
    private int carCount;//车辆份数
    private String addTime;
    private String remark;

    public int getMaterialOutId() {
        return materialOutId;
    }

    public void setMaterialOutId(int materialOutId) {
        this.materialOutId = materialOutId;
    }

    public String getPinMing() {
        return pinMing;
    }

    public void setPinMing(String pinMing) {
        this.pinMing = pinMing;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarCount() {
        return carCount;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getJianHao() {
        return jianHao;
    }

    public void setJianHao(String jianHao) {
        this.jianHao = jianHao;
    }
    
}
