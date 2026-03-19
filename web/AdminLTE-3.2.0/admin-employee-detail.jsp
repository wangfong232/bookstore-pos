<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>${employee != null ? 'Chỉnh sửa' : 'Thêm mới'} Nhân viên - Admin</title>

        <!-- Google Font -->
        <link rel="stylesheet"
              href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <!-- Font Awesome -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
        <!-- AdminLTE -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>

    <body class="hold-transition sidebar-mini">
        <div class="wrapper">

            <!-- Navbar -->
            <jsp:include page="include/admin-header.jsp"/>

            <!-- Sidebar -->
            <jsp:include page="include/admin-sidebar.jsp"/>

            <!-- Content Wrapper -->
            <div class="content-wrapper">

                <!-- Content Header -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>
                                    <i class="fas fa-user"></i>
                                    ${employee != null ? 'Chỉnh sửa Nhân viên' : 'Thêm Nhân viên Mới'}
                                </h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item">
                                        <a href="<%= request.getContextPath() %>/AdminLTE-3.2.0/index.jsp">Home</a>
                                    </li>
                                    <li class="breadcrumb-item">
                                        <a href="<%= request.getContextPath() %>/admin/employees">Nhân viên</a>
                                    </li>
                                    <li class="breadcrumb-item active">
                                        ${employee != null ? 'Chỉnh sửa' : 'Thêm mới'}
                                    </li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Main content -->
                <section class="content">
                    <div class="container-fluid">

                        <!-- Error -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas fa-ban"></i> ${error}
                            </div>
                        </c:if>

                        <div class="row">
                            <!-- Form -->
                            <div class="col-md-8">
                                <div class="card card-primary">
                                    <div class="card-header">
                                        <h3 class="card-title">Thông tin Nhân viên</h3>
                                    </div>

                                    <form method="post"
                                          action="<%= request.getContextPath() %>/admin/employees"
                                          id="employeeForm">

                                        <input type="hidden" name="action"
                                               value="${employee != null ? 'update' : 'insert'}">

                                        <c:if test="${employee != null}">
                                            <input type="hidden" name="employeeId"
                                                   value="${employee.employeeId}">
                                        </c:if>

                                        <div class="card-body">

                                            <!-- Full Name -->
                                            <div class="form-group">
                                                <label>Họ và tên <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control"
                                                       name="fullName"
                                                       value="${employee != null ? employee.fullName : ''}"
                                                       placeholder="Nhập họ và tên" 
                                                       pattern="^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ\s]+$"
                                                       title="Họ và tên chỉ được chứa chữ cái và khoảng trắng"
                                                       required>
                                            </div>

                                            <!-- Email -->
                                            <div class="form-group">
                                                <label>Email <span class="text-danger">*</span></label>
                                                <input type="email" class="form-control"
                                                       name="email"
                                                       value="${employee.email}"
                                                       id="emailInput"
                                                       placeholder="example@email.com" required>
                                            </div>

                                            <!-- Phone -->
                                            <div class="form-group">
                                                <label>Số điện thoại</label>
                                                <input type="text" class="form-control"
                                                       name="phone"
                                                       value="${employee.phone}"
                                                       placeholder="0123456789">
                                            </div>

                                            <!-- Role -->
                                            <div class="form-group">
                                                <label>Vai trò <span class="text-danger">*</span></label>
                                                <select name="roleId" class="form-control" required>
                                                    <option value="">-- Chọn vai trò --</option>
                                                    <c:forEach items="${roles}" var="r">
                                                        <option value="${r.roleId}"
                                                                ${employee != null && employee.role.roleId == r.roleId ? 'selected' : ''}>
                                                            ${r.roleName}
                                                        </option>
                                                    </c:forEach>
                                                </select>

                                            </div>


                                            <!-- Hire Date -->
                                            <div class="form-group">
                                                <label>Ngày vào làm</label>
                                                <input type="date" class="form-control"
                                                       name="hireDate"
                                                       value="${employee.hireDate}">
                                            </div>

                                            <!-- Status -->
                                            <div class="form-group">
                                                <div class="custom-control custom-switch">
                                                    <input type="checkbox"
                                                           class="custom-control-input"
                                                           id="status"
                                                           name="status"
                                                           value="ACTIVE"
                                                           ${employee == null || employee.status == 'ACTIVE' ? 'checked' : ''}>
                                                    <label class="custom-control-label" for="status">
                                                        Kích hoạt nhân viên
                                                    </label>
                                                </div>
                                                <small class="form-text text-muted">
                                                    Tắt để vô hiệu hóa tài khoản nhân viên
                                                </small>
                                            </div>

                                        </div>

                                        <div class="card-footer">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-save"></i>
                                                ${employee != null ? 'Cập nhật' : 'Thêm mới'}
                                            </button>
                                            <a href="<%= request.getContextPath() %>/admin/employees"
                                               class="btn btn-default">
                                                <i class="fas fa-times"></i> Hủy
                                            </a>
                                        </div>
                                    </form>
                                </div>
                            </div>

                            <!-- Info -->
                            <div class="col-md-4">
                                <div class="card card-info">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-info-circle"></i> Hướng dẫn
                                        </h3>
                                    </div>
                                    <div class="card-body">
                                        <ul class="pl-3">
                                            <li>Email phải <strong>duy nhất</strong></li>
                                            <li>Vai trò quyết định quyền truy cập</li>
                                            <li>Nhân viên inactive sẽ không đăng nhập được</li>
                                        </ul>
                                    </div>
                                </div>

                                <c:if test="${employee != null}">
                                    <div class="card card-secondary">
                                        <div class="card-header">
                                            <h3 class="card-title">
                                                <i class="fas fa-database"></i> Thông tin
                                            </h3>
                                        </div>
                                        <div class="card-body">
                                            <p><strong>ID:</strong> #${employee.employeeId}</p>
                                            <p><strong>Trạng thái:</strong>
                                                <span class="badge ${employee.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                                    ${employee.status}
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                        </div>
                    </div>
                </section>
            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>
        
        <script>
            // Ngăn chặn khoảng trắng trong email
            const emailInput = document.getElementById('emailInput');
            if (emailInput) {
                emailInput.addEventListener('keydown', function (e) {
                    if (e.which === 32)
                        e.preventDefault();
                });

                emailInput.addEventListener('input', function (e) {
                    this.value = this.value.replace(/\s/g, '');
                });
            }
        </script>
    </body>
</html>
