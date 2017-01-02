<%-- 
    Document   : warehouse_out_add
    Created on : 2016-11-11, 6:11:39
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>添加成品出库</title>
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
            var obj;
            var inputObj = [];
            var selectedIndex;
            window.onload = function () {
                $("[name=carrierCode]").val('${sessionScope.user.carrierCode}');
                $("#jianHaoName_input").blur(function() {
                    $("#jianHaoName").val($("#jianHaoName_input").val());
                    getList("search_form");
                });
                $("#jianHao_input").blur(function() {
                    $("#jianHao").val($("#jianHao_input").val());
                    getList("search_form");
                });
                $("#addData").bind("click", function () {
                    if (checkInput()) {
                        $("#carCount" + selectedIndex).html($("#countNum").val());
                        var tmp = new Object();
                        tmp.pinMing = obj.data[selectedIndex].pinMing;
                        tmp.jianHao = obj.data[selectedIndex].jianHao;
                        tmp.carCount = $("#countNum").val();
                        tmp.remark = $("#remark").val();
                        inputObj.push(tmp);
                        document.getElementById("add_form").reset();
                    }
                });
                $("#saveData").bind("click", function () {
                    var obj = new Object();
                    obj.carrierName = '${sessionScope.user.userName}';
                    obj.data = inputObj;
                    var sendBody = JSON.stringify(obj);
                    getDataInterface("warehouseOutAddBatch.do", sendBody, function (result) {
                        var obj = JSON.parse(result);
                        alert(obj.message);
                        if (obj.status === 0) {
                            var index = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(index);
                        }
                    });
                });
                getList("search_form");
            };
            
            function checkInput() {
                if ($.trim($("#jianHaoName_input").val()) === "") {
                    $("#jianHaoName_input").focus();
                    alert("件号名称不能为空！");
                    return false;
                }
                if ($.trim($("#jianHao_input").val()) === "") {
                    $("#jianHao_input").focus();
                    alert("件号不能为空！");
                    return false;
                }
                if ($.trim($("#countNum").val()) === "") {
                    $("#countNum").focus();
                    alert("数量不能为空！");
                    return false;
                }
                return true;
            }
            
            function getList(formId) {
                var sendBody = serializeForm(formId);
                getDataInterface('orderPlanGet.do', sendBody, function(result) {
                    obj = JSON.parse(result);
                    if (obj.status === 0) {
                        var htmlStr = "<table class='detail_data'><tr class='table_header'><td>序号</td><td>件号名称</td><td>件号</td><td>计划完成时间</td><td>计划数</td><td>入库数</td><td>操作</td></tr>";
                        for (var i = 0; i < obj.data.length; i++) {
                            htmlStr += "<tr class='table_content'>";
                            htmlStr += "<td>" + (obj.data[i].planId ? obj.data[i].planId : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].pinMing ? obj.data[i].pinMing : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].jianHao ? obj.data[i].jianHao : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].finishTime ? obj.data[i].finishTime : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].carCount ? obj.data[i].carCount : "") + "</td>";
                            htmlStr += "<td>" + ("<div id=carCount" + i + "></div>") + "</td>";
                            htmlStr += "<td>";
                            htmlStr += "<a href='javascript:addWarehouseOut(" + i + ")'>添加</a>";
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
                        var htmlStr = "<table class='detail_data'><tr class='table_header'><td>序号</td><td>件号名称</td><td>件号</td><td>计划完成时间</td><td>计划数</td><td>入库数</td><td>操作</td></tr>";
                        htmlStr += "<td colspan='7'><h3>没有查询到相关数据！</h3></td>";
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
            
            function addWarehouseOut(index) {
                selectedIndex = index;
                $("#jianHao_input").val(obj.data[index].jianHao);
                $("#jianHaoName_input").val(obj.data[index].pinMing);
            }
        </script>
    </head>
    <body>
        <div>
            <form id="add_form"  class="form-horizontal" role="form">
                <input type="hidden"  name="carrierCode" />
                <table>
                    <tr>
                        <td>
                            <div class="form-group-sm form-group">
                                <label for="jianHao_input" class="col-sm-4 control-label">件号</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="jianHaoName_input" id="jianHao_input" value="" />
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group-sm form-group">
                                <label for="jianHaoName_input" class="col-sm-4 control-label">件号名称</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="jianHaoName_input" id="jianHaoName_input" value="" />
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group-sm form-group">
                                <label for="countNum" class="col-sm-4 control-label">总数</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="countNum" id="countNum" placeholder="请输入总数" />
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group-sm form-group">
                                <label for="remark" class="col-sm-4 control-label">备注</label>
                                <div class="col-sm-6">
                                    <input type="text" class="form-control" name="remark" id="remark" placeholder="请输入备注" />
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-group-sm form-group">
                                <div class="col-sm-offset-2 col-sm-9 function">
                                    <a class="btn btn-default" id="addData">添加</a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <div class="search">
            <form id="search_form" class="form-horizontal" role="form">
                <input type="hidden" name="jianHaoName" id="jianHaoName" value=""/>
                <input type="hidden" name="jianHao" id="jianHao" value=""/>
                <input type="hidden" name="pageIndex" id="pageIndex" value="1" />
                <input type="hidden" name="pageSize" id="pageSize" value="10" />
                <input type="hidden"  name="carrierCode"/>
            </form>
        </div>

        <div>
            <div id="message_list">
                <table class="detail_data">
                    <tr class='table_header'><td>序号</td><td>件号名称</td><td>件号</td><td>计划完成时间</td><td>套数</td><td>总数</td><td>备注</td><td>操作</td></tr>
                </table>
            </div>
            <div id="page_div" class="page_div">
                <div id="page"></div>
                <div id="page_info"></div>
            </div>
            <div class="function btn_center">
                <a class="btn btn-default" id="saveData">保存</a>
            </div>
            <div style="clear: both;float: none;"></div>
        </div>
    </body>
</html>
