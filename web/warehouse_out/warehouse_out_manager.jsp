<%-- 
    Document   : warehouse_out_manager
    Created on : 2016-11-11, 6:12:41
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>成品出库</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
        <meta http-equiv="X-UA-Compatible" content="IE=9" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <link rel="stylesheet" href="../style/detail_style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="../style/style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="../style/bootstrap.min.css" type="text/css" media="all" />

        <script type="text/javascript" src="../javascript/jquery-1.11.1.min.js"></script>
        <script type="text/javascript" src="../javascript/bootstrap.min.js"></script>
        <script type="text/javascript" src="../javascript/laypage/laypage.js"></script>
        <script type="text/javascript" src="../javascript/My97DatePicker/WdatePicker.js"></script>
        <script type="text/javascript" src="../javascript/script.js"></script>
        <script type="text/javascript" src="../javascript/projectScript.js"></script>
        <script type="text/javascript">
            window.onload = function () {
                $("#beginTime").bind("click", function () {
                    new WdatePicker({dateFmt: 'yyyy-MM-dd', maxDate: getMaxDate()});
                });
                $("#endTime").bind("click", function () {
                    new WdatePicker({dateFmt: 'yyyy-MM-dd', maxDate: getMaxDate()});
                });
                $("#add").bind("click", function () {
                    parent.displayLayer("./warehouse_out/warehouse_out_add.jsp", "成品出库添加", function() {getList("search_form");});
                });
                $("#importData").bind("click", function () {
                    parent.displayLayer("./warehouse_out/warehouse_out_import.html", "成品出库导入", function() {getList("search_form");});
                });
                $("#search").bind("click", function () {
                    getList("search_form");
                });
                $("#reset").bind("click", function () {
                    document.getElementById("search_form").reset();
                });
                $("#carrierName").val('${sessionScope.user.userName}');
                getList("search_form");
            };

            function getList(formId) {
                var sendBody = serializeForm(formId);
                getDataInterface('warehouseOutGet.do', sendBody, function(result) {
                    var obj = JSON.parse(result);
                    if (obj.status === 0) {
                        var htmlStr = "<table class='detail_data'><tr class='table_header'><td>序号</td><td>品名</td><td>件号</td><td>入库时间</td><td>套数</td><td>操作</td></tr>";
                        for (var i = 0; i < obj.data.length; i++) {
                            htmlStr += "<tr class='table_content'>";
                            htmlStr += "<td>" + (obj.data[i].warehouseOutId ? obj.data[i].warehouseOutId : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].pinMing ? obj.data[i].pinMing : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].jianHao ? obj.data[i].jianHao : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].addTime ? obj.data[i].addTime : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].carCount ? obj.data[i].carCount : "") + "</td>";
                            htmlStr += "<td>";
                            htmlStr += "<a href=''>修改</a><span>|</span>";
                            htmlStr += "<a href='javascript:deleteOrder(" + obj.data[i].jianHao + ")'>删除</a>";
                            htmlStr += "</td>";
                            htmlStr += "</tr>";
                        }
                        htmlStr += "</table>";
                        $("#message_list").html(htmlStr);

                        var recordCount = obj.count;
                        var pageSize = $("#pageSize").val();
                        var pageCount = parseInt((recordCount % pageSize === 0) ? (recordCount / pageSize) : (recordCount / pageSize + 1));
                        layerPage(pageCount);

                    } else {
                        var htmlStr = "<table class='detail_data'><tr class='table_header'><td>序号</td><td>品名</td><td>件号</td><td>入库时间</td><td>套数</td><td>操作</td></tr>";
                        htmlStr += "<td colspan='6'><h3>没有查询到相关数据！</h3></td>";
                        htmlStr + "<tr></tr></table>";
                        $("#message_list").html(htmlStr);
                        layerPage(0);
                    }
                });
            }
            function layerPage(pageCount) {
                document.getElementById("page_info").innerHTML = '目前正在第' + $("#pageIndex").val() + '页，一共有' + pageCount + '页';
                laypage({
                    cont: "page",
                    pages: pageCount,
                    curr: $("#pageIndex").val(),
                    skip: true,
                    jump: function (obj, first) {
                        if (!first) {
                            $("#pageIndex").val(obj.curr);
                            getList("search_form");
                        }
                    }
                });
            }
        </script>
    </head>
    <body>
        <div class="function">
            <span class="left">
                <a href="javascript:void(0)" id="add">添加</a>
            </span>
            <span class="right">
                <a href="javascript:void(0)" id="search">搜索</a>
                <a href="javascript:void(0)" id="reset">重置</a>
            </span>
        </div>
        <div class="search">
            <form id="search_form" class="form-horizontal" role="form">
                <table>
                    <tr>
                        <td>
                            <div class="form-group">
                                <label for="jianHaoName" class="col-sm-4 control-label">件号名称：</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" name="jianHaoName" id="jianHaoName"/>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="jianHao" class="col-sm-4 control-label">件号：</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" name="jianHao" id="jianHao"/>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="beginTime" class="col-sm-4 control-label">开始时间：</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" name="beginTime" id="beginTime"/>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="endTime" class="col-sm-4 control-label">结束时间:：</label>
                                <div class="col-sm-8">
                                    <input type="text" class="form-control" name="endTime" id="endTime"/>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
                <input type="hidden" name="pageIndex" id="pageIndex" value="1" />
                <input type="hidden" name="pageSize" id="pageSize" value="15" />
                <input type="hidden"  name="carrierName" id="carrierName" />
            </form>
        </div>

        <div>
            <div id="message_list">
                <table class="detail_data">
                    <tr class='table_header'><td>序号</td><td>品名</td><td>件号</td><td>入库时间</td><td>套数</td><td>操作</td></tr>
                </table>
            </div>
            <div id="page_div" class="page_div">
                <div id="page"></div>
                <div id="page_info"></div>
            </div>
            <div style="clear: both;float: none;"></div>
        </div>
    </body>
</html>
