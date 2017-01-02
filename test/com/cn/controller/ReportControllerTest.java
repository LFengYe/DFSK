/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.MaterialReport;
import com.cn.bean.ProductReport;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LFeng
 */
public class ReportControllerTest {
    
    public ReportControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getProductReport method, of class ReportController.
     */
    @Test
    public void testGetProductReport() {
        System.out.println("getProductReport");
        String beginTime = "";
        String endTime = "";
        String carrierName = "";
        ReportController instance = new ReportController();
        ArrayList<ProductReport> expResult = null;
        ArrayList<ProductReport> result = instance.getProductReport(beginTime, endTime, carrierName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaterialReport method, of class ReportController.
     */
    @Test
    public void testGetMaterialData_3args() {
        System.out.println("getMaterialData");
        String beginTime = "";
        String endTime = "";
        String carrierName = "";
        ReportController instance = new ReportController();
        ArrayList<MaterialReport> expResult = null;
        ArrayList<MaterialReport> result = instance.getMaterialReport(beginTime, endTime, carrierName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaterialReport method, of class ReportController.
     */
    @Test
    public void testGetMaterialData_6args() {
        System.out.println("getMaterialData");
        String beginTime = "2016-12-01";
        String endTime = "2016-12-26";
        String carrierName = "";
        String jianHao = "";
        int pageSize = 11;
        int pageIndex = 1;
        ReportController instance = new ReportController();
        ArrayList<MaterialReport> expResult = null;
        ArrayList<MaterialReport> result = instance.getMaterialReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
    }

    /**
     * Test of getProductReport method, of class ReportController.
     */
    @Test
    public void testGetProductReport_3args() {
        System.out.println("getProductReport");
        String beginTime = "";
        String endTime = "";
        String carrierName = "";
        ReportController instance = new ReportController();
        ArrayList<ProductReport> expResult = null;
        ArrayList<ProductReport> result = instance.getProductReport(beginTime, endTime, carrierName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProductReport method, of class ReportController.
     */
    @Test
    public void testGetProductReport_6args() {
        System.out.println("getProductReport");
        String beginTime = "2016-12-01";
        String endTime = "2016-12-26";
        String carrierName = "";
        String jianHao = "";
        int pageSize = 11;
        int pageIndex = 1;
        ReportController instance = new ReportController();
        ArrayList<ProductReport> expResult = null;
        ArrayList<ProductReport> result = instance.getProductReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
    }

    /**
     * Test of getMaterialReport method, of class ReportController.
     */
    @Test
    public void testGetMaterialReport_3args() {
        System.out.println("getMaterialReport");
        String beginTime = "";
        String endTime = "";
        String carrierName = "";
        ReportController instance = new ReportController();
        ArrayList<MaterialReport> expResult = null;
        ArrayList<MaterialReport> result = instance.getMaterialReport(beginTime, endTime, carrierName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaterialReport method, of class ReportController.
     */
    @Test
    public void testGetMaterialReport_6args() {
        System.out.println("getMaterialReport");
        String beginTime = "";
        String endTime = "";
        String carrierName = "";
        String jianHao = "";
        int pageSize = 0;
        int pageIndex = 0;
        ReportController instance = new ReportController();
        ArrayList<MaterialReport> expResult = null;
        ArrayList<MaterialReport> result = instance.getMaterialReport(beginTime, endTime, carrierName, jianHao, pageSize, pageIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
