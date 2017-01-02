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
public class ProductReportExport {
    private int planCount;
    private String jianHaoName;
    private String jianHao;
    private int initStock;
    private int productInToday;
    private int productInCount;
    private int productOutToday;
    private int productOutCount;
    private int productStock;

    public int getPlanCount() {
        return planCount;
    }

    public void setPlanCount(int planCount) {
        this.planCount = planCount;
    }

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

    public int getProductInToday() {
        return productInToday;
    }

    public void setProductInToday(int productInToday) {
        this.productInToday = productInToday;
    }

    public int getProductInCount() {
        return productInCount;
    }

    public void setProductInCount(int productInCount) {
        this.productInCount = productInCount;
    }

    public int getProductOutToday() {
        return productOutToday;
    }

    public void setProductOutToday(int productOutToday) {
        this.productOutToday = productOutToday;
    }

    public int getProductOutCount() {
        return productOutCount;
    }

    public void setProductOutCount(int productOutCount) {
        this.productOutCount = productOutCount;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }
    
}
