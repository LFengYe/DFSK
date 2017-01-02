<%-- 
    Document   : report_material
    Created on : 2016-11-19, 4:16:31
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>原材料时间段报表</title>
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
                $("[name=carrierName]").val('${sessionScope.user.userName}');
                $("#beginTime").bind("click", function () {
                    new WdatePicker({dateFmt: 'yyyy-MM-dd', maxDate: getMaxDate()});
                });
                $("#beginTime").val(getNowDateShort());
                $("#endTime").bind("click", function () {
                    new WdatePicker({dateFmt: 'yyyy-MM-dd'});
                });
                $("#endTime").val(getNowDateShort());
                $("#search").bind("click", function () {
                    getList("search_form");
                });
                $("#reset").bind("click", function () {
                    document.getElementById("search_form").reset();
                });
                $("#export").bind("click", function () {
                    var sendBody = serializeForm("search_form");
                    getDataInterface('exportMaterialReport.do', sendBody, function (result) {
                        var obj = JSON.parse(result);
                        if (obj.status === 0) {
                            location.href = encodeURI(obj.data);
                        }
                        alert(obj.message);
                    });
                });
                getList("search_form");
            };

            function getList(formId) {
                var sendBody = serializeForm(formId);
                getDataInterface('getMaterialReportWithTime.do', sendBody, function(result) {
                    var obj = JSON.parse(result);
                    if (obj.status === 0) {
                        var htmlStr = "<table class='detail_data'><tr class='report_table_header'><td>产品名称</td><td>件号</td><td>期初成品库存</td><td>当日入库</td><td>累计入库</td><td>当日出库</td><td>累计出库</td><td>库存</td></tr>";
                        for (var i = 0; i < obj.data.length; i++) {
                            htmlStr += "<tr class='report_table_content'>";
//                            htmlStr += "<td>" + obj.data[i].planCount + "</td>";
                            htmlStr += "<td>" + obj.data[i].jianHaoName + "</td>";
                            htmlStr += "<td>" + obj.data[i].jianHao + "</td>";
                            htmlStr += "<td>" + obj.data[i].initStock + "</td>";
                            htmlStr += "<td>" + obj.data[i].materialInToday + "</td>";
                            htmlStr += "<td>" + obj.data[i].materialInCount + "</td>";
                            htmlStr += "<td>" + obj.data[i].materialOutToday + "</td>";
                            htmlStr += "<td>" + obj.data[i].materialOutCount + "</td>";
                            htmlStr += "<td>" + obj.data[i].materialStock + "</td>";
                            htmlStr += "</tr>";
                        }
                        htmlStr += "</table>";
                        $("#message_list").html(htmlStr);

                        var recordCount = obj.count;
                        $("#exportPageSize").val(recordCount);
                        var pageSize = $("#pageSize").val();
                        var pageCount = parseInt((recordCount % pageSize === 0) ? (recordCount / pageSize) : (recordCount / pageSize + 1));
                        layerPage(pageCount);

                    } else {
                        var htmlStr = "<table class='detail_data'><tr class='report_table_header'><td>产品名称</td><td>件号</td><td>期初成品库存</td><td>当日入库</td><td>累计入库</td><td>当日出库</td><td>累计出库</td><td>库存</td></tr>";
                        htmlStr += "<td colspan='8'><h3>没有查询到相关数据！</h3></td>";
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
                <a href="javascript:void(0)" id="export">导出</a>
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
                                <label for="jianHao">件号：</label>
                                <input type="text" name="jianHao" id="jianHao"/>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="beginTime">开始时间：</label>
                                <input type="text"  name="beginTime" id="beginTime"/>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="endTime">结束时间:：</label>
                                <input type="text" name="endTime" id="endTime"/>
                            </div>
                        </td>
                    </tr>
                </table>
                <input type="hidden" name="carrierName" id="carrierName" />
                <input type="hidden" name="pageIndex" id="pageIndex" value="1" />
                <input type="hidden" name="pageSize" id="pageSize" value="15" />
                <input type="hidden" name="exportPageIndex" id="exportPageIndex" value="1" />
                <input type="hidden" name="exportPageSize" id="exportPageSize" value="999" />
            </form>
        </div>

        <div>
            <div id="message_list">
                <table class="report_table">
                    <tr class='report_table_header'>
                        <td>产品名称</td><td>期初成品库存</td><td>当日入库</td><td>累计入库</td><td>当日出库</td><td>累计出库</td><td>库存</td>
                    </tr>
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

