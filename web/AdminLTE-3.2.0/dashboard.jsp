<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <title>Dashboard - Bookstore Management</title>

  <link rel="stylesheet"
        href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
</head>

<body class="hold-transition sidebar-mini">
  <div class="wrapper">

    <jsp:include page="include/admin-header.jsp"/>
    <jsp:include page="include/admin-sidebar.jsp"/>

    <div class="content-wrapper">

      <!-- Content Header -->
      <section class="content-header">
        <div class="container-fluid">
          <div class="row mb-2">
            <div class="col-sm-6">
              <h1><i class="fas fa-tachometer-alt"></i> Dashboard</h1>
            </div>
            <div class="col-sm-6 text-right">
              <ol class="breadcrumb float-sm-right">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                <li class="breadcrumb-item active">Dashboard</li>
              </ol>
            </div>
          </div>
        </div>
      </section>

      <!-- Main Content -->
      <section class="content">
        <div class="container-fluid">

          <!-- Welcome Card -->
          <div class="row">
            <div class="col-md-12">
              <div class="card card-primary card-outline">
                <div class="card-header">
                  <h3 class="card-title">
                    <i class="fas fa-smile"></i> Xin chào, <strong>${fullName != null ? fullName : 'User'}</strong>
                  </h3>
                </div>
                <div class="card-body">
                  <p>Bạn đang đăng nhập với vai trò: <strong><span class="badge badge-info">${roleName != null ? roleName : 'User'}</span></strong></p>
                  <p>Chào mừng đến với Hệ thống Quản lý Bán Sách. Bạn có thể sử dụng menu bên trái để truy cập các tính năng khác nhau.</p>
                </div>
              </div>
            </div>
          </div>

          <!-- PHẦN DÀNH CHO ADMIN & MANAGER (Role 1, 2) -->
          <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
          <!-- Stats Row -->
          <div class="row">
            <div class="col-lg-3 col-6">
              <div class="small-box bg-info">
                <div class="inner">
                  <h3>${totalEmployees != null ? totalEmployees : 0}</h3>
                  <p>Nhân viên</p>
                </div>
                <div class="small-box-footer">
                  <a href="${pageContext.request.contextPath}/admin/employees?action=list" class="small-box-footer">
                    Xem chi tiết <i class="fas fa-arrow-circle-right"></i>
                  </a>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-6">
              <div class="small-box bg-success">
                <div class="inner">
                  <h3>${totalProducts != null ? totalProducts : 0}</h3>
                  <p>Sản phẩm</p>
                </div>
                <div class="small-box-footer">
                  <a href="#" class="small-box-footer">
                    Xem chi tiết <i class="fas fa-arrow-circle-right"></i>
                  </a>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-6">
              <div class="small-box bg-warning">
                <div class="inner">
                  <h3>${totalPOs != null ? totalPOs : 0}</h3>
                  <p>Đơn mua hàng</p>
                </div>
                <div class="small-box-footer">
                  <a href="#" class="small-box-footer">
                    Xem chi tiết <i class="fas fa-arrow-circle-right"></i>
                  </a>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-6">
              <div class="small-box bg-danger">
                <div class="inner">
                  <h3>${totalSuppliers != null ? totalSuppliers : 0}</h3>
                  <p>Nhà cung cấp</p>
                </div>
                <div class="small-box-footer">
                  <a href="${pageContext.request.contextPath}/admin/suppliers" class="small-box-footer">
                    Xem chi tiết <i class="fas fa-arrow-circle-right"></i>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Quick Links -->
          <div class="row mt-4">
            <div class="col-md-6">
              <div class="card card-outline card-primary">
                <div class="card-header">
                  <h3 class="card-title"><i class="fas fa-link"></i> Liên kết nhanh</h3>
                </div>
                <div class="card-body">
                  <ul class="list-unstyled">
                    <li class="mb-2">
                      <a href="${pageContext.request.contextPath}/admin/employees?action=list" class="btn btn-sm btn-primary">
                        <i class="fas fa-users"></i> Quản lý Nhân viên
                      </a>
                    </li>
                    <li class="mb-2">
                      <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-sm btn-success">
                        <i class="fas fa-list"></i> Quản lý Danh mục
                      </a>
                    </li>
                    <li class="mb-2">
                      <a href="${pageContext.request.contextPath}/admin/brands" class="btn btn-sm btn-warning">
                        <i class="fas fa-copyright"></i> Quản lý Thương hiệu
                      </a>
                    </li>
                    <li class="mb-2">
                      <a href="${pageContext.request.contextPath}/admin/suppliers" class="btn btn-sm btn-danger">
                        <i class="fas fa-truck"></i> Quản lý Nhà cung cấp
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </c:if>

          <!-- PHẦN DÀNH CHO STAFF & SALER (Role 3, 4, 5) -->
          <c:if test="${sessionScope.roleId == 3 || sessionScope.roleId == 4 || sessionScope.roleId == 5}">
          <div class="row">
            <div class="col-lg-4 col-6">
              <div class="small-box bg-success">
                <div class="inner">
                  <h3>Bán Hàng</h3>
                  <p>Màn hình POS</p>
                </div>
                <div class="icon">
                  <i class="fas fa-cash-register"></i>
                </div>
                <a href="${pageContext.request.contextPath}/pos" class="small-box-footer">
                  Tới thu ngân <i class="fas fa-arrow-circle-right"></i>
                </a>
              </div>
            </div>

            <div class="col-lg-4 col-6">
              <div class="small-box bg-info">
                <div class="inner">
                  <h3>Chấm Công</h3>
                  <p>Quản lý giờ làm</p>
                </div>
                <div class="icon">
                  <i class="fas fa-clock"></i>
                </div>
                <a href="${pageContext.request.contextPath}/staff/attendance-checkin" class="small-box-footer">
                  Ra vào ca <i class="fas fa-arrow-circle-right"></i>
                </a>
              </div>
            </div>

            <div class="col-lg-4 col-12">
              <div class="small-box bg-warning">
                <div class="inner">
                  <h3>Đổi Ca</h3>
                  <p>Xin đổi lịch làm việc</p>
                </div>
                <div class="icon">
                  <i class="fas fa-exchange-alt"></i>
                </div>
                <a href="${pageContext.request.contextPath}/staff/swap" class="small-box-footer">
                  Thực hiện ngay <i class="fas fa-arrow-circle-right"></i>
                </a>
              </div>
            </div>
          </div>
          </c:if>

            <div class="col-md-6">
              <div class="card card-outline card-secondary">
                <div class="card-header">
                  <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin hệ thống</h3>
                </div>
                <div class="card-body">
                  <p><strong>Tên ứng dụng:</strong> Bookstore Management System</p>
                  <p><strong>Phiên bản:</strong> 1.0.0</p>
                  <p><strong>Năm:</strong> 2026</p>
                  <p><strong>Email hỗ trợ:</strong> support@bookstore.com</p>
                </div>
              </div>
            </div>
          </div>

        </div>
      </section>

    </div>

    <jsp:include page="include/admin-footer.jsp"/>

  </div>

</body>
</html>
