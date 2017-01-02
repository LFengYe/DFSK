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
public class BaseInfo {
    public static int recordCount;
    
    private int baseId;
    private String pinMing;
    private String jianHao;
    private String carModel;
    private int carNum;

    public int getBaseId() {
        return baseId;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
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

    public int getCarNum() {
        return carNum;
    }

    public void setCarNum(int carNum) {
        this.carNum = carNum;
    }

    public String getJianHao() {
        return jianHao;
    }

    public void setJianHao(String jianHao) {
        this.jianHao = jianHao;
    }
    
}
