<%-- 
    Document   : material_init_import
    Created on : 2016-11-19, 1:11:29
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>原材料期初导入</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" href="../style/detail_style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="../style/style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="../style/bootstrap.min.css" type="text/css" />

        <script type="text/javascript" src="../javascript/jquery-1.11.1.min.js"></script>
        <script type="text/javascript" src="../javascript/jquery.placeholder.min.js"></script>
        <script type="text/javascript" src="../javascript/JSON-js-master/json2.js"></script>
        <script type="text/javascript" src="../javascript/bootstrap.min.js"></script>
        <script type="text/javascript" src="../javascript/script.js"></script>
        <script type="text/javascript" src="../javascript/projectScript.js"></script>
        <script type="text/javascript">
            window.onload = function () {
                $("[name=carrierName]").val('${sessionScope.user.userName}');
                $("#importData").bind("click", function () {
                    uploadExcel();
                });
                $("iframe").bind("load", function () {
                    var responseText = document.getElementById("hiddenFrame").contentWindow.document.body.innerText;
                    var obj = JSON.parse(responseText);
                    if (obj.status === 0) {
                        var sendBody = new Object();
                        sendBody.fileName = obj.data[0].fileName;
                        sendBody.carrierName = '${sessionScope.user.userName}';
                        //var sendBody = '{"fileName":"' + obj.data[0].fileName + '","productLineId":"' + $("#productLineId").val() + '"}';
                        getDataInterface("materialInitImport.do", JSON.stringify(sendBody), function (result) {
                            var importRes = JSON.parse(result);
                            alert(importRes.message);
                            if (importRes.status === 0) {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);
                            }
                        });
                    } else {
                        alert(obj.message);
                    }
                });
            };
            function uploadExcel() {
                var filePath = $('#fileInput').val();
                if (filePath !== "") {
                    //对文件格式进行验证(简单验证)
                    var d1 = /\.[^\.]+$/.exec(filePath);
                    if (d1 == ".xls") {
                        $("#import_form").submit();
                    } else {
                        alert("请选择xls文件");
                    }
                } else {
                    alert("请选择文件");
                }
            }
        </script>
    </head>
    <body>
        <div class="layer_content">
            <form target="hiddenFrame" class="form-horizontal" id="import_form" enctype="multipart/form-data" method="POST" action="../UploadFile">
                <div class="form-group-sm form-group">
                    <label for="fileInput" class="col-sm-2 control-label">上传文件</label>
                    <div class="col-sm-9">
                        <input type="file" class="form-control" name="fileInput" id="fileInput" />
                    </div>
                </div>
                <div class="form-group-sm form-group">
                    <div class="col-sm-offset-2 col-sm-9 function">
                        <a class="btn btn-default" id="importData">导入</a>
                        <a class="btn btn-default" id="loadTemplate" href="原材料期初导入模板.xls" target="_blank">下载模板</a>
                    </div>
                </div>
            </form>
            <iframe name="hiddenFrame" id="hiddenFrame" style="display: none;"></iframe>
        </div>

    </body>
</html>
