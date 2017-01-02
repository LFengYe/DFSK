<%-- 
    Document   : index
    Created on : 2016-11-11, 4:33:26
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>东风小康</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <link rel="stylesheet" href="style/bootstrap.min.css" type="text/css"  media="all" />
        <link rel="stylesheet" href="style/bootstrap-treeview.min.css" type="text/css" media="all"  />
        <link rel="stylesheet" href="style/main_style.css" type="text/css" media="all"  />
        <link rel="stylesheet" href="style/detail_style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="style/style.css" type="text/css"  media="all" />
        <!--[if lt IE 9]>
        <script type="text/javascript" src="javascript/respond.min.js"></script>
        <script type="text/javascript" src="javascript/html5shiv.min.js"></script>
        <![endif]-->
        <script type="text/javascript" src="javascript/jquery-1.11.1.min.js"></script>
        <script type="text/javascript" src="javascript/bootstrap.min.js"></script>
        <script type="text/javascript" src="javascript/bootstrap-treeview.js"></script>
        <script type="text/javascript" src="javascript/script.js"></script>
        <script type="text/javascript" src="javascript/layer/layer.js"></script>
        <script type="text/javascript">
            var adminTree = [
                {text: "计划导入", href: "javascript:hrefToIframe('order/order_manager.html')"}
                , {text: "原材料入库", href: "javascript:hrefToIframe('material_in/material_in_manager.html')"}
                , {text: "原材料出库", href: "javascript:hrefToIframe('material_out/material_out_manager.html')"}
                , {text: "成品入库", href: "javascript:hrefToIframe('warehouse_in/warehouse_in_manager.html')"}
                , {text: "成品出库", href: "javascript:hrefToIframe('warehouse_out/warehouse_out_manager.html')"}
                , {text: "报表查看", nodes: [
                        {text: "原材料报表", href: "javascript:hrefToIframe('report/report_material.html')"}
                        , {text: "成品报表", href: "javascript:hrefToIframe('report/report_product.html')"}]
                }
                , {text: "系统管理", nodes: [
                        {text: "用户管理", href: "javascript:hrefToIframe('system/user_manager.html')"}
                        , {text: "基础信息", href: "javascript:hrefToIframe('system/base_manager.html')"}
                        , {text: "原材料信息", href: "javascript:hrefToIframe('base/meterial_manager.html')"}
                        , {text: "原材料期初", href: "javascript:hrefToIframe('base/meterial_init_manager.html')"}
                        , {text: "成品期初", href: "javascript:hrefToIframe('base/product_init_manager.html')"}]
                }
            ];
            var orderTree = [
                {text: "计划导入", href: "javascript:hrefToIframe('order/order_manager.html')"}
                , {text: "报表查看", nodes: [
                        {text: "原材料报表", href: "javascript:hrefToIframe('report/report_material.html')"}
                        , {text: "成品报表", href: "javascript:hrefToIframe('report/report_product.html')"}]
                }
            ];

            var carrierTree = [
                {text: "计划查看", href: "javascript:hrefToIframe('order/order_query.jsp')"}
                , {text: "原材料入库", href: "javascript:hrefToIframe('material_in/material_in_manager.jsp')"}
                , {text: "原材料出库", href: "javascript:hrefToIframe('material_out/material_out_manager.jsp')"}
                , {text: "成品入库", href: "javascript:hrefToIframe('warehouse_in/warehouse_in_manager.jsp')"}
                , {text: "成品出库", href: "javascript:hrefToIframe('warehouse_out/warehouse_out_manager.jsp')"}
                , {text: "报表查看", nodes: [
                        {text: "原材料报表", href: "javascript:hrefToIframe('report/report_material.jsp')"}
                        , {text: "成品报表", href: "javascript:hrefToIframe('report/report_product.jsp')"}]
                }
                , {text: "系统管理", nodes: [
                        {text: "原材料信息", href: "javascript:hrefToIframe('base/meterial_manager.jsp')"}
                        , {text: "原材料期初", href: "javascript:hrefToIframe('base/meterial_init_manager.jsp')"}
                        , {text: "成品期初", href: "javascript:hrefToIframe('base/product_init_manager.jsp')"}]
                }
            ];

            window.onload = function () {
                findDimensions();
                createTreeMenu();
                $("#password").bind('click', function () {
                    displayLayer('modify_pass.html?userName=${sessionScope.user.userName}', '修改密码', null, -1);
                });
                var htmlStr = "<h4>Hi," + '${sessionScope.user.userName}' + "</h4>";
                htmlStr += "<h4>" + getMaxDate() + "</h4>";
                $("#sysInfo").html(htmlStr);
            };
            function createTreeMenu() {
                var userType = ${sessionScope.user.userType};
                $("iframe").attr("src", "${sessionScope.user.roleHomePage}");
                var tree = ${sessionScope.user.rolePermission};
                
                $('#tree_menu').treeview({
                    data: tree,
                    enableLinks: true,
                    color: '#ffffff',
                    backColor: '#428bca',
                    onhoverColor: '#428bca',
                    selectedColor: '#1d2f33',
                    selectedBackColor: '#ffffff'
                });
            }
            function hrefToIframe(url) {
                window.parent.content.location.href = url;
            }

            function displayLayer(url, title, callback) {
                layer.open({
                    type: 2,
                    title: title,
                    shadeClose: true,
                    shade: 0.3,
                    maxmin: true, //开启最大化最小化按钮
                    area: ['1000px', '600px'],
                    content: url,
                    end: function () {
                        if (callback) {
                            callback();
                        }
                    }
                });
            }

            function exitSystem() {
                if (confirm("确定登出系统？")) {
                    location.href = "exit.jsp";
                }
            }
        </script>
    </head>
    <body>
        <div id="top">
            <div id="logo">
                <h1>东风小康供应商协同平台</h1>
            </div>
            <div class="navbar_info">
                <div class="username">
                    <div><a href="javascript:void(0)" id="password" title="修改密码"><img alt="" src="images/nav_user.png" /></a></div>
                    <div id='sysInfo'><h4>Hi,test</h4><h4>2016-10-27 10:10</h4></div>
                </div>
                <div class="loginOut">
                    <a href="javascript:exitSystem()" title="退出登录" target="_top"><img src="images/nav_exit.png" alt="login out"/></a>
                </div>
            </div>
        </div>
        <div id="left">
            <div id="tree_menu"></div>
        </div>
        <div id="right">
            <iframe name="content" src="" frameborder="0" style="width: 100%;height: 100%;"></iframe>
        </div>
    </body>
</html>
