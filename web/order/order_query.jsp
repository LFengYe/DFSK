<%-- 
    Document   : order_query
    Created on : 2016-11-11, 5:50:01
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>计划查询</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
        <meta http-equiv="X-UA-Compatible" content="IE=9" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <link rel="stylesheet" href="../style/detail_style.css" type="text/css"/>
        <link rel="stylesheet" href="../style/style.css" type="text/css"/>
        <link rel="stylesheet" href="../style/bootstrap.min.css" type="text/css"/>

        <!--[if lt IE 9]>
        <script type="text/javascript" src="javascript/respond.min.js"></script>
        <script type="text/javascript" src="javascript/html5shiv.min.js"></script>
        <![endif]-->
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
                    new WdatePicker({dateFmt: 'yyyy-MM-dd'});
                });
                $("#importData").bind("click", function () {
                    parent.displayLayer("./order/order_import.html", "计划导入", function() {getList("search_form");});
                });
                $("#search").bind("click", function () {
                    getList("search_form");
                });
                $("#reset").bind("click", function () {
                    document.getElementById("search_form").reset();
                });
                
                $("#carrierCode").val('${sessionScope.user.carrierCode}');
                $("#searchFieldSelect").bind("change", function(e) {
                    var obj = $(e.target);
                    $("#searchField").val(obj.val());
                });
                getList("search_form");
            };
            function getList(formId) {
                var sendBody = serializeForm(formId);
                getDataInterface('orderPlanGet.do', sendBody, function(result) {
                    var obj = JSON.parse(result);
                    if (obj.status === 0) {
                        var htmlStr = "<table class='detail_data'><tr class='table_header'>";
                        htmlStr += "<td><a name='planId'>序号</a></td>";
                        htmlStr += "<td><a name='pinMing'>件号名称</a></td>";
                        htmlStr += "<td><a name='jianHao'>件号</a></td>";
                        htmlStr += "<td><a name='finishTime'>计划完成时间</a></td>";
                        htmlStr += "<td><a name='carCount'>数量</a></td>";
                        htmlStr += "<td><a name='carrierName'>供应商</a></td>";
                        htmlStr += "<td><a name='carrierCode'>供应商代码</a></td>";
                        htmlStr += "<td><a name='unit'>单位</a></td>";
                        htmlStr += "</tr>";
                        for (var i = 0; i < obj.data.length; i++) {
                            htmlStr += "<tr class='table_content'>";
                            htmlStr += "<td>" + (obj.data[i].planId ? obj.data[i].planId : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].pinMing ? obj.data[i].pinMing : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].jianHao ? obj.data[i].jianHao : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].finishTime ? obj.data[i].finishTime : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].carCount ? obj.data[i].carCount : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].carrierName ? obj.data[i].carrierName : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].carrierCode ? obj.data[i].carrierCode : "") + "</td>";
                            htmlStr += "<td>" + (obj.data[i].unit ? obj.data[i].unit : "") + "</td>";
//                            htmlStr += "<td>" + (obj.data[i].planType ? obj.data[i].planType : "") + "</td>";
                            htmlStr += "</tr>";
                        }
                        htmlStr += "</table>";
                        $("#message_list").html(htmlStr);
                        $(".table_header a").bind("click", function (e) {
                            var name = $(e.target).attr("name");
                            var orderField = $("#orderField").val();
                            if (name === orderField) {
                                $("#orderFlag").val(($("#orderFlag").val() == 0) ? 1 : 0);
                            } else {
                                $("#orderField").val(name);
                            }
                            getList('search_form');
                        });

                        var recordCount = obj.count;
                        var pageSize = $("#pageSize").val();
                        var pageCount = parseInt((recordCount % pageSize === 0) ? (recordCount / pageSize) : (recordCount / pageSize + 1));
                        layerPage(recordCount, pageCount);

                    } else {
                        var htmlStr = "<table class='detail_data'><tr class='table_header'><td>序号</td><td>件号名称</td><td>件号</td><td>计划完成时间</td><td>套数</td><td>供应商</td><td>供应商代码</td><td>单位</td></tr>";
                        htmlStr += "<td colspan='8'><h3>没有查询到相关数据！</h3></td>";
                        htmlStr + "<tr></tr></table>";
                        $("#message_list").html(htmlStr);
                        layerPage(0, 0);
                    }
                });
            }
            function layerPage(recordCount, pageCount) {
                document.getElementById("page_info").innerHTML = '目前正在第' + $("#pageIndex").val() + '页，一共有' + pageCount + '页,' + recordCount + "条记录";
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

            function deleteOrder(orderId) {
                if (confirm("确定删除该计划？")) {
                }
            }
        </script>
    </head>
    <body>
        <div class="function">
            <span class="left">
                
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
                                <div>
                                    <select name="searchFieldSelect" id="searchFieldSelect">
                                        <option value='all'>通用</option>
<!--                                        <option value='carrierCode'>供应商代码</option>-->
                                        <option value='jianHao'>件号</option>
                                        <option value='pinMing'>件号名称</option>
                                    </select>
                                </div>
                                <input type="hidden" name="searchField" id="searchField" value="all" />
                                <input type="text" name="searchValue" id="searchValue" />
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="beginTime">开始时间：</label>
                                <input type="text" name="beginTime" id="beginTime"/>
                            </div>
                        </td>
                        <td>
                            <div class="form-group">
                                <label for="endTime">结束时间：</label>
                                <input type="text" name="endTime" id="endTime"/>
                            </div>
                        </td>
                    </tr>
                </table>
                <input type="hidden" name="carrierName" id="carrierName"/>
                <input type="hidden" name="carrierCode" id="carrierCode"/>
                <input type="hidden" name="orderField" id="orderField" value="" />
                <input type="hidden" name="orderFlag" id="orderFlag" value="0" />
                <input type="hidden" name="pageIndex" id="pageIndex" value="1" />
                <input type="hidden" name="pageSize" id="pageSize" value="15" />
            </form>
        </div>

        <div>
            <div id="message_list">
                <table class="detail_data">
                    <tr class='table_header'><td>序号</td><td>件号名称</td><td>件号</td><td>计划完成时间</td><td>套数</td><td>供应商</td><td>班次</td><td>车型</td><td>部品</td><td>操作</td></tr>
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
