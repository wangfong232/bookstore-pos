<%-- 
    Document   : po-list
    Created on : Feb 9, 2026, 3:43:51 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Danh sách đơn đặt hàng</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">

        <div class="wrapper">
            <!--sidebar-->
            <jsp:include page="include/admin-sidebar.jsp" />
            <jsp:include page="include/admin-header.jsp" />

            <!-- Content Wrapper -->
            <div class="content-wrapper">
                <!--content header (page header) -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>Quản lý đơn đặt hàng</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/dashboard">Home</a>
                                    </li>
                                    <li class="breadcrumb-item active">Đơn đặt hàng</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>


                <!-- main content -->
                <section class="content">
                    <div class="container-fluid">
                        <!-- Success/Error Messages -->
                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.contains('success') ? 'alert-success' : 'alert-danger'} alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                                <i class="icon fas ${msg.contains('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_add'}">Thêm đơn hàng thành công!</c:when>
                                    <c:when test="${msg == 'success_edit'}">Cập nhật đơn hàng thành công!</c:when>
                                    <c:when test="${msg == 'success_delete'}">Xóa đơn hàng thành công!</c:when>
                                    <c:when test="${msg == 'success_approve'}">Duyệt đơn hàng thành công!</c:when>
                                    <c:when test="${msg == 'success_reject'}">Từ chối đơn hàng thành công!</c:when>
                                    <c:when test="${msg == 'success_cancel'}">Hủy đơn hàng thành công!</c:when>
                                    <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại!</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">
                                <!-- Search & Filter Card -->
                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-search"></i> Tìm kiếm & Lọc
                                        </h3>
                                        <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=add"
                                           class="btn btn-success mb-2" style="margin-left: 80%;">
                                            <i class="fas fa-plus"></i> Tạo đơn đặt hàng mới
                                        </a>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/purchaseorder" method="get"
                                              class="form-inline">
                                            <input type="hidden" name="action" value="list">
                                            <div class="form-group mr-3 mb-2">
                                                <label for="key" class="mr-2">Tìm kiếm:</label>
                                                <input type="text" class="form-control" id="key" name="key" value="${param.key}"
                                                       placeholder="Nhập từ khóa...">
                                            </div>

                                            <div class="form-group mr-3 mb-2">
                                                <label for="status" class="mr-2">Trạng thái:</label>
                                                <select name="status" id="status" class="form-control">
                                                    <option value="" ${empty param.status ? 'selected' : ''}>-- Tất cả --</option>
                                                    <option value="PENDING_APPROVAL" ${param.status == 'PENDING_APPROVAL' ? 'selected' : ''}>Chờ duyệt</option>
                                                    <option value="APPROVED" ${param.status == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                                                    <option value="REJECTED" ${param.status == 'REJECTED' ? 'selected' : ''}>Đã từ chối</option>
                                                    <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                                                    <option value="PARTIAL_RECEIVED" ${param.status == 'PARTIAL_RECEIVED' ? 'selected' : ''}>Nhận một phần</option>
                                                    <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
                                                </select>
                                            </div>
                                            <div class="form-group mr-3 mb-2">
                                                <label for="from" class="mr-2">Từ ngày:</label>
                                                <input type="date" class="form-control" id="from" name="from" value="${param.from}">
                                            </div>

                                            <div class="form-group mr-3 mb-2">
                                                <label for="to" class="mr-2">Đến ngày: </label>
                                                <input type="date" class="form-control" id="to" name="to" value="${param.to}">
                                            </div>

                                            <button type="submit" class="btn btn-primary mb-2 mr-2">
                                                <i class="fas fa-search"></i> Tìm kiếm
                                            </button>
                                            <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=list"
                                               class="btn btn-default mb-2 mr-2">
                                                <i class="fas fa-redo"></i> Đặt lại
                                            </a>

                                        </form>
                                    </div>
                                </div><!-- search box-->

                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-list"></i> Danh sách đơn đặt hàng
                                        </h3>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${not empty lists}">
                                            <table class="table table-bordered table-striped table-hover">
                                                <thead>
                                                    <tr>
                                                        <th style="width: 10%">Mã</th>
                                                        <th style="width: 15%">Ngày tạo</th>
                                                        <th style="width: 20%">Nhà cung cấp</th>
                                                        <th style="width: 15%">Tổng tiền</th>
                                                        <th style="width: 15%">Trạng thái</th>
                                                        <th style="width: 15%">Người tạo</th>
                                                        <th style="width: 10%" class="text-center">Thao tác</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="item" items="${lists}">
                                                        <tr class="clickable-row" data-href="${pageContext.request.contextPath}/admin/purchaseorder?action=detail&poNumber=${item.poNumber}"
                                                            style="cursor: pointer">
                                                            <td> <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=detail&poNumber=${item.poNumber}">${item.poNumber}</a></td>
                                                            <td>${item.createdAt}</td>
                                                            <td>${item.supplierName}</td>
                                                            <td>${item.totalAmount}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${item.status == 'PENDING_APPROVAL'}">
                                                                        <span class="badge badge-warning">Chờ duyệt</span>
                                                                    </c:when>
                                                                    <c:when test="${item.status == 'APPROVED'}">
                                                                        <span class="badge badge-success">Đã duyệt</span>
                                                                    </c:when>
                                                                    <c:when test="${item.status == 'REJECTED'}">
                                                                        <span class="badge badge-danger">Từ chối</span>
                                                                    </c:when>
                                                                    <c:when test="${item.status == 'CANCELLED'}">
                                                                        <span class="badge badge-dark">Đã hủy</span>
                                                                    </c:when>
                                                                    <c:when test="${item.status == 'PARTIAL_RECEIVED'}">
                                                                        <span class="badge badge-info">Nhận một phần</span>
                                                                    </c:when>
                                                                    <c:when test="${item.status == 'COMPLETED'}">
                                                                        <span class="badge badge-primary">Hoàn thành</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge badge-secondary">${item.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${item.createdByName}</td>
                                                            <td class="text-center">
                                                                <div class="btn-group">
                                                                    <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=detail&poNumber=${item.poNumber}" 
                                                                       class="btn btn-sm btn-info" title="Xem chi tiết">
                                                                        <i class="fas fa-eye"></i>
                                                                    </a>
                                                                    <c:if test="${item.status == 'PENDING_APPROVAL'}">
                                                                        <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=edit&poNumber=${item.poNumber}" 
                                                                           class="btn btn-sm btn-warning" title="Chỉnh sửa">
                                                                            <i class="fas fa-edit"></i>
                                                                        </a>
                                                                    </c:if>
                                                                </div>
                                                            </td>
                                                        </tr>

                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:if>

                                        <c:if test="${empty lists}">
                                            <div class="alert alert-info text-center">
                                                <i class="fas fa-info-circle"></i>
                                                Không có đơn đặt hàng nào được thêm, hãy thêm đơn đặt hàng mới.
                                            </div>
                                        </c:if>
                                    </div><!-- /.card-body -->

                                    <!-- Pagination -->
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
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/purchaseorder?action=list&page=1&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/purchaseorder?action=list&page=${currentPage - 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link"
                                                               href="${pageContext.request.contextPath}/admin/purchaseorder?action=list&page=${i}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/purchaseorder?action=list&page=${currentPage + 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/purchaseorder?action=list&page=${totalPages}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">Last</a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </c:if>
                                </div><!-- /.card -->

                            </div><!-- ./ col-12 -->
                        </div><!-- ./ row -->
                    </div><!-- ./ container-fluid -->
                </section> <!--./ content -->
            </div><!-- ./ content-wrapper -->


            <!-- Footer -->
            <jsp:include page="include/admin-footer.jsp" />
        </div>
        <!-- ./wrapper -->

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                var rows = document.querySelectorAll(".clickable-row");

                rows.forEach(function (row) {
                    row.addEventListener("click", function () {
                        if (e.target.closest('.btn') || e.target.tagName === 'A') {
                            return;
                        }

                        var url = this.getAttribute("data-href");
                        if (url) {
                            window.location.href = url;
                        }
                    });
                });
            });
        </script>
    </body>
</html>
