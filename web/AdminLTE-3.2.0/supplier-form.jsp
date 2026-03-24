<%-- Document : supplier-form Created on : Jan 30, 2026, 11:52:16 PM Author : qp --%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>
            <c:if test="${mode=='add'}">Thêm nhà cung cấp</c:if>
            <c:if test="${mode=='edit'}">Sửa nhà cung cấp</c:if>
            </title>

            <!-- Google Font: Source Sans Pro -->
            <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
            <!-- Font Awesome -->
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
            <!-- Theme style -->
            <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>

    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <!--             Navbar 
                        <nav class="main-header navbar navbar-expand navbar-white navbar-light">
                             Left navbar links 
                            <ul class="navbar-nav">
                                <li class="nav-item">
                                    <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
                                </li>
                            </ul>
                        </nav>-->
            <!-- /.navbar -->

            <!-- Sidebar -->
            <jsp:include page="include/admin-sidebar.jsp" />

            <!-- Content Wrapper. Contains page content -->
            <div class="content-wrapper">
                <!-- Content Header (Page header) -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>
                                    <c:if test="${mode=='add'}">Thêm nhà cung cấp</c:if>
                                    <c:if test="${mode=='edit'}">Sửa nhà cung cấp</c:if>
                                    </h1>
                                </div>
                                <div class="col-sm-6">
                                    <ol class="breadcrumb float-sm-right">
                                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/supplier?action=list">Nhà cung cấp</a></li>
                                    <li class="breadcrumb-item active">
                                        <c:if test="${mode=='add'}">Thêm mới</c:if>
                                        <c:if test="${mode=='edit'}">Chỉnh sửa</c:if>
                                        </li>
                                    </ol>
                                </div>
                            </div>
                        </div><!-- /.container-fluid -->
                    </section>

                    <!-- Main content -->
                    <section class="content">
                        <div class="container-fluid">
                            <div class="row">
                                <!-- left column -->
                                <div class="col-md-12">
                                    <!-- general form elements -->
                                    <div class="card card-primary">
                                        <div class="card-header">
                                            <h3 class="card-title">Thông tin cơ bản</h3>
                                        </div>
                                        <!-- /.card-header -->

                                        <!-- Display error message if exists -->
                                    <c:if test="${not empty error}">
                                        <div class="alert alert-danger alert-dismissible" style="margin: 15px 15px 0 15px;">
                                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                                            <i class="icon fas fa-ban"></i> ${error}
                                        </div>
                                    </c:if>

                                    <!-- form start -->
                                    <form action="${pageContext.request.contextPath}/admin/supplier" method="post">
                                        <div class="card-body">
                                            <div class="form-group">
                                                <label for="supplierCode">Mã NCC:</label>
                                                <input type="text" class="form-control" id="supplierCode" name="code" value="${code}" placeholder="Tự động" readonly>
                                                <small class="form-text text-muted">Mã nhà cung cấp được tự động tạo</small>
                                            </div>

                                            <div class="form-group">
                                                <label for="supplierName">Tên NCC: <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="supplierName" name="name" value="${supplier.supplierName}" placeholder="Nhập tên nhà cung cấp" required>
                                            </div>

                                            <div class="form-group">
                                                <label for="contactPerson">Người liên hệ:</label>
                                                <input type="text" class="form-control" id="contactPerson" name="contactPerson" value="${supplier.contactPerson}" placeholder="Nhập tên người liên hệ">
                                            </div>

                                            <div class="form-group">
                                                <label for="phone">Điện thoại:</label>
                                                <div class="input-group">
                                                    <div class="input-group-prepend">
                                                        <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                                    </div>
                                                    <input type="text" class="form-control" id="phone" name="phone" value="${supplier.phone}" placeholder="Nhập số điện thoại">
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="email">Email:</label>
                                                <div class="input-group">
                                                    <div class="input-group-prepend">
                                                        <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                                    </div>
                                                    <input type="email" class="form-control" id="email" name="email" value="${supplier.email}" placeholder="Nhập email">
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="address">Địa chỉ:</label>
                                                <textarea class="form-control" id="address" name="address" rows="3" placeholder="Nhập địa chỉ">${supplier.address}</textarea>
                                            </div>
                                        </div>
                                        <!-- /.card-body -->

                                        <div class="card-footer">
                                            <a href="${pageContext.request.contextPath}/admin/supplier?action=list" class="btn btn-default">
                                                <i class="fas fa-times"></i> Hủy
                                            </a>
                                            <button type="submit" name="action" value="save" class="btn btn-primary float-right">
                                                <i class="fas fa-save"></i> Lưu
                                            </button>
                                        </div>
                                    </form>
                                </div>
                                <!-- /.card -->
                            </div>
                        </div>
                    </div><!-- /.container-fluid -->
                </section>
                <!-- /.content -->
            </div>
            <!-- /.content-wrapper -->

            <!-- Footer -->
            <jsp:include page="include/admin-footer.jsp"/>
        </div>
        <!-- ./wrapper -->
    </body>

</html>