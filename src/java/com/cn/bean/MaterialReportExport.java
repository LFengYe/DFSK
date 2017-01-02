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
public class MaterialReportExport {
    private String jianHaoName;
    private String jianHao;
    private int initStock;
    private int materialInToday;
    private int materialInCount;
    private int materialOutToday;
    private int materialOutCount;
    private int materialStock;

    public String getJianHaoName() {
        return jianHaoName;
    }

    public void setJianHaoName(String jianHaoName) {
        this.jianHaoName = jianHaoName;
    }

    public String getJianHao() {
        return jianHao;
    }

    public void setJianHao(String jianHao) {
        this.jianHao = jianHao;
    }

    public int getInitStock() {
        return initStock;
    }

    public void setInitStock(int initStock) {
        this.initStock = initStock;
    }

    public int getMaterialInToday() {
        return materialInToday;
    }

    public void setMaterialInToday(int materialInToday) {
        this.materialInToday = materialInToday;
    }

    public int getMaterialInCount() {
        return materialInCount;
    }

    public void setMaterialInCount(int materialInCount) {
        this.materialInCount = materialInCount;
    }

    public int getMaterialOutToday() {
        return materialOutToday;
    }

    public void setMaterialOutToday(int materialOutToday) {
        this.materialOutToday = materialOutToday;
    }

    public int getMaterialOutCount() {
        return materialOutCount;
    }

    public void setMaterialOutCount(int materialOutCount) {
        this.materialOutCount = materialOutCount;
    }

    public int getMaterialStock() {
        return materialStock;
    }

    public void setMaterialStock(int materialStock) {
        this.materialStock = materialStock;
    }
}
