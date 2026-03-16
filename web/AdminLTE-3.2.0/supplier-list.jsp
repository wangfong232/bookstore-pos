<%-- 
    Document   : supplier-list
    Created on : Jan 31, 2026, 12:21:55 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Quản lý nhà cung cấp</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp" />

            <div class="content-wrapper">
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>Quản lý nhà cung cấp</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item active">Nhà cung cấp</li>
                                </ol>
                            </div>
                        </div>
                    </div></section>

                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${msg.startsWith('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_add'}">Thêm nhà cung cấp mới thành công!</c:when>
                                    <c:when test="${msg == 'success_edit'}">Cập nhật thông tin thành công!</c:when>
                                    <c:when test="${msg == 'success_delete'}">Xóa nhà cung cấp thành công!</c:when>
                                    <c:when test="${msg == 'success_active'}">Kích hoạt nhà cung cấp thành công!</c:when>
                                    <c:when test="${msg == 'success_deactive'}">Hủy kích hoạt nhà cung cấp thành công!</c:when>
                                    <c:when test="${msg == 'fail_delete_has_orders'}">Không thể xóa NCC này vì đã có đơn đặt hàng. Hãy sử dụng chức năng Hủy kích hoạt thay vì Xóa.</c:when>
                                    <c:when test="${msg == 'fail_lock_active_orders'}">Không thể khóa NCC này vì còn ${blockingCount} đơn đặt hàng đang xử lý. Hãy hoàn tất các đơn này trước.</c:when>
                                    <c:when test="${msg == 'access_denied'}">Bạn không có quyền thực hiện thao tác này!</c:when>
                                    <c:otherwise>Có lỗi xảy ra, vui lòng thử lại!</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">
                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-search"></i> Tìm kiếm & Lọc
                                        </h3>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/supplier" method="get" class="form-inline">
                                            <input type="hidden" name="action" value="search">
                                            <div class="form-group mr-3 mb-2">
                                                <label for="key" class="mr-2">Tìm kiếm:</label>
                                                <input type="text" class="form-control" id="key" name="key" value="${param.key}" placeholder="Nhập từ khóa...">
                                            </div>

                                            <div class="form-group mr-3 mb-2">
                                                <label for="status" class="mr-2">Trạng thái:</label>
                                                <select name="status" id="status" class="form-control">
                                                    <option value="">Tất cả</option>
                                                    <option value="true" ${param.status=='true' ? 'selected': ''}>Hoạt động</option>
                                                    <option value="false" ${param.status=='false' ? 'selected': ''}>Ngừng hoạt động</option>
                                                </select>
                                            </div>

                                            <button type="submit" class="btn btn-primary mb-2 mr-2">
                                                <i class="fas fa-search"></i> Tìm kiếm
                                            </button>
                                            <a href="${pageContext.request.contextPath}/admin/supplier?action=list" class="btn btn-default mb-2 mr-2">
                                                <i class="fas fa-redo"></i> Đặt lại
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/supplier?action=add" class="btn btn-success mb-2">
                                                <i class="fas fa-plus"></i> Tạo NCC mới
                                            </a>
                                        </form>
                                    </div>
                                </div>

                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-list"></i> Danh sách nhà cung cấp
                                        </h3>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${not empty lists}">
                                            <table class="table table-bordered table-striped table-hover">
                                                <thead class="thead-light">
                                                    <tr>
                                                        <th style="width: 10%">Mã</th>
                                                        <th style="width: 30%">Tên NCC</th>
                                                        <th style="width: 25%">Người liên hệ</th>
                                                        <th style="width: 20%">Điện thoại</th>
                                                        <th style="width: 15%">Trạng thái</th>
                                                        <th style="width: 10%">Thao tác</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="item" items="${lists}">
                                                        <tr>
                                                            <td>${item.supplierCode}</td>
                                                            <td>${item.supplierName}</td>
                                                            <td>${item.contactPerson}</td>
                                                            <td>${item.phone}</td>
                                                            <td>
                                                                <c:if test="${item.isActive}">
                                                                    <span class="badge badge-success">Hoạt động</span>
                                                                </c:if>
                                                                <c:if test="${!item.isActive}">
                                                                    <span class="badge badge-danger">Không hoạt động</span>
                                                                </c:if>
                                                            </td>
                                                            <td>
                                                                <div class="d-flex justify-content-center gap-2">
                                                                    <a href="${pageContext.request.contextPath}/admin/supplier?action=edit&code=${item.supplierCode}" class="btn btn-warning btn-sm mr-1" title="Sửa">
                                                                        <i class="fas fa-edit"></i>
                                                                    </a>

                                                                    <c:if test="${item.isActive}">
                                                                        <form action="${pageContext.request.contextPath}/admin/supplier" method="post" style="display: inline-block;">
                                                                            <input type="hidden" name="code" value="${item.supplierCode}">
                                                                            <button type="submit" name="action" value="deactive" class="btn btn-secondary btn-sm mr-1" onclick="return confirm('Bạn chắc chắn muốn hủy kích hoạt?');" title="Hủy kích hoạt">
                                                                                <i class="fas fa-ban"></i>
                                                                            </button>
                                                                        </form>
                                                                    </c:if>
                                                                    <c:if test="${!item.isActive}">
                                                                        <form action="${pageContext.request.contextPath}/admin/supplier" method="post" style="display: inline-block;">
                                                                            <input type="hidden" name="code" value="${item.supplierCode}">
                                                                            <button type="submit" name="action" value="active" class="btn btn-success btn-sm mr-1" title="Kích hoạt">
                                                                                <i class="fas fa-check"></i>
                                                                            </button>
                                                                        </form>
                                                                    </c:if>

                                                                    <form action="${pageContext.request.contextPath}/admin/supplier" method="post" style="display: inline-block;">
                                                                        <input type="hidden" name="code" value="${item.supplierCode}">
                                                                        <button type="submit" name="action" value="delete" class="btn btn-danger btn-sm" onclick="return confirm('Bạn chắc chắn muốn xóa vĩnh viễn nhà cung cấp này?');" title="Xóa">
                                                                            <i class="fas fa-trash"></i>
                                                                        </button>
                                                                    </form>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:if>

                                        <c:if test="${empty lists}">
                                            <div class="text-center p-4 text-muted">
                                                <i class="fas fa-inbox fa-3x mb-3"></i>
                                                <p>Không tìm thấy nhà cung cấp nào.</p>
                                            </div>
                                        </c:if>
                                    </div>
                                    <c:if test="${not empty lists && totalPages > 1}">
                                        <div class="card-footer clearfix">
                                            <div class="float-left">
                                                <p class="text-muted">
                                                    Hiển thị trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
                                                </p>
                                            </div>
                                            <ul class="pagination pagination-sm m-0 float-right">
                                                <c:if test="${currentPage > 1}">
                                                    <li class="page-item">
                                                        <a class="page-link" href="supplier?action=list&page=1&key=${param.key}&status=${param.status}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link" href="supplier?action=list&page=${currentPage - 1}&key=${param.key}&status=${param.status}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link" href="supplier?action=list&page=${i}&key=${param.key}&status=${param.status}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link" href="supplier?action=list&page=${currentPage + 1}&key=${param.key}&status=${param.status}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link" href="supplier?action=list&page=${totalPages}&key=${param.key}&status=${param.status}">Last</a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </c:if>
                                </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            <jsp:include page="include/admin-footer.jsp" />
        </div>
     
    </body>
</html>