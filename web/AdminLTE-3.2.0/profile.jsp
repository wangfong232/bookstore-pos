<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Hồ sơ cá nhân - Bookstore</title>

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

        <!-- Header -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><i class="fas fa-user-circle"></i> Hồ sơ cá nhân</h1>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">

                <!-- Success/Error Messages -->
                <c:if test="${param.success == 'update'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> Cập nhật thông tin thành công!
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'password'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> Đổi mật khẩu thành công!
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'update'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> Cập nhật thất bại. Vui lòng thử lại.
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'old_password_incorrect'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> Mật khẩu cũ không chính xác.
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'password_mismatch'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> Mật khẩu mới không khớp. Vui lòng kiểm tra lại.
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'password_length'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle"></i> Mật khẩu phải có ít nhất 6 ký tự.
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>

                <div class="row">
                    <div class="col-md-8">

                        <!-- Thông tin cá nhân -->
                        <div class="card card-primary">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <i class="fas fa-info-circle"></i> Thông tin cá nhân
                                </h3>
                                <div class="card-tools">
                                    <button type="button" class="btn btn-tool btn-sm" onclick="toggleEditMode('info-form')">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                </div>
                            </div>

                            <form id="info-form" action="${pageContext.request.contextPath}/profile" method="post">
                                <input type="hidden" name="action" value="update">
                                
                                <div class="card-body">
                                    <div class="form-group">
                                        <label>Họ và tên</label>
                                        <input type="text" name="fullName" class="form-control" 
                                               value="${employee.fullName}" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label>Email</label>
                                        <input type="email" name="email" class="form-control" 
                                               value="${employee.email}" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label>Điện thoại</label>
                                        <input type="text" name="phone" class="form-control" 
                                               value="${employee.phone}" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label>Vai trò</label>
                                        <input type="text" class="form-control" 
                                               value="${sessionScope.roleName}" readonly>
                                    </div>

                                    <div class="form-group" id="info-buttons" style="display:none;">
                                        <button type="submit" class="btn btn-success">
                                            <i class="fas fa-save"></i> Lưu
                                        </button>
                                        <button type="button" class="btn btn-secondary" 
                                                onclick="toggleEditMode('info-form')">
                                            <i class="fas fa-times"></i> Hủy
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <!-- Đổi mật khẩu -->
                        <div class="card card-warning">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <i class="fas fa-lock"></i> Đổi mật khẩu
                                </h3>
                            </div>

                            <form action="${pageContext.request.contextPath}/profile" method="post">
                                <input type="hidden" name="action" value="change_password">
                                
                                <div class="card-body">
                                    <div class="form-group">
                                        <label>Mật khẩu cũ</label>
                                        <input type="password" name="oldPassword" class="form-control" 
                                               placeholder="Nhập mật khẩu cũ" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Mật khẩu mới</label>
                                        <input type="password" name="newPassword" class="form-control" 
                                               placeholder="Nhập mật khẩu mới (ít nhất 6 ký tự)" required>
                                    </div>

                                    <div class="form-group">
                                        <label>Nhập lại mật khẩu mới</label>
                                        <input type="password" name="confirmPassword" class="form-control" 
                                               placeholder="Nhập lại mật khẩu mới" required>
                                    </div>
                                </div>

                                <div class="card-footer">
                                    <button type="submit" class="btn btn-warning">
                                        <i class="fas fa-key"></i> Đổi mật khẩu
                                    </button>
                                </div>
                            </form>
                        </div>

                    </div>

                    <div class="col-md-4">
                        <!-- Thông tin khác -->
                        <div class="card card-info">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <i class="fas fa-address-card"></i> Thông tin tài khoản
                                </h3>
                            </div>

                            <div class="card-body text-center">
                                <img src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/img/user2-160x160.jpg" 
                                     class="img-circle elevation-2 mb-3" alt="User Image" 
                                     style="width: 150px; height: 150px;">
                                
                                <h5>${employee.fullName}</h5>
                                <p class="text-muted">${sessionScope.roleName}</p>
                                <hr>
                                
                                <div class="text-left">
                                    <p><strong>ID:</strong> ${employee.employeeId}</p>
                                    <p><strong>Email:</strong> ${employee.email}</p>
                                    <p><strong>Điện thoại:</strong> ${employee.phone}</p>
                                    <p><strong>Trạng thái:</strong> 
                                        <c:if test="${employee.status == 'ACTIVE'}">
                                            <span class="badge badge-success">Hoạt động</span>
                                        </c:if>
                                        <c:if test="${employee.status == 'PENDING'}">
                                            <span class="badge badge-warning">Chờ kích hoạt</span>
                                        </c:if>
                                        <c:if test="${employee.status == 'INACTIVE'}">
                                            <span class="badge badge-danger">Vô hiệu hóa</span>
                                        </c:if>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        </section>
    </div>

    <jsp:include page="include/admin-footer.jsp"/>
</div>

<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

<script>
function toggleEditMode(formId) {
    const form = document.getElementById(formId);
    const inputs = form.querySelectorAll('input[name*="fullName"], input[name*="email"], input[name*="phone"]');
    const buttonsDiv = document.getElementById('info-buttons');
    const isReadonly = inputs[0].readOnly;
    
    inputs.forEach(input => {
        input.readOnly = !isReadonly;
        input.classList.toggle('bg-light');
    });
    
    if (isReadonly) {
        buttonsDiv.style.display = 'block';
    } else {
        buttonsDiv.style.display = 'none';
    }
}
</script>

</body>
</html>
