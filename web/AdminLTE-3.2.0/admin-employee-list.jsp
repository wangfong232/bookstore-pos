<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Quản lý Nhân viên - Admin</title>

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
                                <h1><i class="fas fa-users"></i> Quản lý Nhân viên</h1>
                            </div>
                            <div class="col-sm-6 text-right">
                                <a href="${pageContext.request.contextPath}/admin/employees?action=add"
                                   class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Chỉnh sửa thông tin cá nhân
                                </a>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <!-- Toast -->
                        <c:if test="${param.success == 'insert'}">
                            <div class="alert alert-success alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                Thêm nhân viên thành công!
                            </div>
                        </c:if>

                        <!-- SEARCH + FILTER -->
                        <div class="card card-outline card-primary">
                            <div class="card-body">
                                <form method="get"
                                      action="${pageContext.request.contextPath}/admin/employees">

                                    <input type="hidden" name="action" value="list"/>

                                    <div class="row">

                                        <!-- Search by name -->
                                        <div class="col-md-4">
                                            <input type="text"
                                                   name="key"
                                                   value="${param.key}"
                                                   class="form-control"
                                                   placeholder="Tìm theo tên nhân viên...">
                                        </div>

                                        <!-- Filter status -->
                                        <div class="col-md-3">
                                            <select name="status" class="form-control">
                                                <option value="">-- Tất cả trạng thái --</option>
                                                <option value="ACTIVE"
                                                        ${param.status == 'ACTIVE' ? 'selected' : ''}>
                                                    ACTIVE
                                                </option>
                                                <option value="INACTIVE"
                                                        ${param.status == 'INACTIVE' ? 'selected' : ''}>
                                                    INACTIVE
                                                </option>
                                            </select>
                                        </div>

                                        <!-- Filter role -->
                                        <div class="col-md-3">
                                            <select name="roleId" class="form-control">
                                                <option value="">-- Tất cả vai trò --</option>

                                                <c:forEach items="${roleList}" var="r">
                                                    <option value="${r.roleId}"
                                                            ${param.roleId == r.roleId ? 'selected' : ''}>
                                                        ${r.roleName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <!-- Buttons -->
                                        <div class="col-md-3">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-search"></i> Tìm kiếm
                                            </button>

                                            <a href="${pageContext.request.contextPath}/admin/employees?action=list"
                                               class="btn btn-secondary">
                                                Reset
                                            </a>
                                        </div>

                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- TABLE -->
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title">Danh sách Nhân viên</h3>
                            </div>

                            <div class="card-body table-responsive p-0">
                                <table class="table table-hover text-nowrap">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Họ tên</th>
                                            <th>Email</th>
                                            <th>Vai trò</th>
                                            <th>Trạng thái</th>
                                            <th width="150">Thao tác</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        <c:forEach items="${lists}" var="e">
                                            <tr>
                                                <td>#${e.employeeId}</td>
                                                <td>${e.fullName}</td>
                                                <td>${e.email}</td>
                                                <td>${e.role.roleName}</td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${e.status == 'ACTIVE'}">
                                                            <span class="badge badge-success">ACTIVE</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-danger">INACTIVE</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <a href="${pageContext.request.contextPath}/admin/employees?action=edit&id=${e.employeeId}"
                                                       class="btn btn-sm btn-info">
                                                        <i class="fas fa-edit"></i>
                                                    </a>

                                                    <a href="${pageContext.request.contextPath}/admin/employees?action=toggle&id=${e.employeeId}"
                                                       class="btn btn-sm ${e.status == 'ACTIVE' ? 'btn-warning' : 'btn-success'}"
                                                       onclick="return confirm('Bạn có chắc muốn thay đổi trạng thái?')">
                                                        <i class="fas ${e.status == 'ACTIVE' ? 'fa-ban' : 'fa-check'}"></i>
                                                    </a>

                                                    <!-- Delete -->
                                                    <a href="${pageContext.request.contextPath}/admin/employees?action=delete&id=${e.employeeId}"
                                                       class="btn btn-sm btn-danger"
                                                       onclick="return confirm('Bạn có chắc muốn xóa nhân viên này?')">
                                                        <i class="fas fa-trash"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>

                                        <c:if test="${empty lists}">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted">
                                                    Không tìm thấy nhân viên nào.
                                                </td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>

                            <!-- PAGINATION giữ filter -->
                            <div class="card-footer clearfix">
                                <ul class="pagination pagination-sm m-0 float-right">
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/admin/employees?action=list&page=${i}&key=${param.key}&status=${param.status}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>

                        </div>

                    </div>
                </section>
            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>

        
    </body>
</html>