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
public class MaterialBaseInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    private int materialBaseId;
    private String pinMing;
    private String jianHao;
    private String carModel;
    private int carNum;
    private String carrierName;
    private String productJianHao;

    public int getMaterialBaseId() {
        return materialBaseId;
    }

    public void setMaterialBaseId(int materialBaseId) {
        this.materialBaseId = materialBaseId;
    }

    public String getPinMing() {
        return pinMing;
    }

    public void setPinMing(String pinMing) {
        this.pinMing = pinMing;
    }

    public String getJianHao() {
        return jianHao;
    }

    public void setJianHao(String jianHao) {
        this.jianHao = jianHao;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarNum() {
        return carNum;
    }

    public void setCarNum(int carNum) {
        this.carNum = carNum;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getProductJianHao() {
        return productJianHao;
    }

    public void setProductJianHao(String productJianHao) {
        this.productJianHao = productJianHao;
    }
}
