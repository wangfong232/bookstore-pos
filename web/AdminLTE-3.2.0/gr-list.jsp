<%-- 
    Document   : gr-list
    Created on : Mar 4, 2026, 12:28:50 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Quản lý phiếu nhập kho</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">
            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>

            <div class="content-wrapper">
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1><i class="fas fa-warehouse"></i> Quản lý phiếu nhập kho</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Home</a></li>
                                    <li class="breadcrumb-item active">Phiếu nhập kho</li>
                                </ol>
                            </div>
                        </div></div></section><section class="content">
                    <div class="container-fluid">
                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.contains('success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${msg.contains('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_create'}">Tạo phiếu nhập kho thành công!</c:when>
                                    <c:when test="${msg == 'success_complete'}">Hoàn tất phiếu nhập kho thành công! Kho đã được cập nhật.</c:when>
                                    <c:when test="${msg == 'success_cancel'}">Hủy phiếu nhập kho thành công!</c:when>
                                    <c:when test="${msg == 'fail_notfound'}">Không tìm thấy phiếu nhập kho.</c:when>
                                    <c:when test="${msg == 'access_denied'}">Bạn không có quyền thực hiện thao tác này!</c:when>
                                    <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại!</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas fa-ban"></i> ${error}
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">
                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-search"></i> Tìm kiếm &amp; Lọc</h3>
                                        <div class="card-tools">
                                            <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=create"
                                               class="btn btn-success btn-sm">
                                                <i class="fas fa-plus"></i> Tạo phiếu nhập từ ĐĐH
                                            </a>
                                        </div>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/goodsreceipt" method="get">
                                            <input type="hidden" name="action" value="list">
                                            <div class="row">
                                                <div class="col-md-3 form-group">
                                                    <label>Tìm kiếm:</label>
                                                    <input type="text" name="key" class="form-control"
                                                           value="${param.key}"
                                                           placeholder="Mã phiếu, mã ĐĐH, nhà cung cấp...">
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Trạng thái:</label>
                                                    <select name="status" class="form-control">
                                                        <option value="" ${empty param.status ? 'selected' : ''}>Tất cả</option>
                                                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Đang nhập</option>
                                                        <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Từ ngày:</label>
                                                    <input type="date" name="from" class="form-control" value="${param.from}">
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Đến ngày:</label>
                                                    <input type="date" name="to" class="form-control" value="${param.to}">
                                                </div>
                                                <div class="col-md-3 form-group d-flex align-items-end">
                                                    <button type="submit" class="btn btn-primary mr-2">
                                                        <i class="fas fa-search"></i> Tìm kiếm
                                                    </button>
                                                    <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list"
                                                       class="btn btn-default">
                                                        <i class="fas fa-redo"></i> Đặt lại
                                                    </a>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div><div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-list"></i> Danh sách phiếu nhập kho
                                            <c:if test="${totalRecords > 0}">
                                                <span class="badge badge-secondary ml-2">${totalRecords} phiếu</span>
                                            </c:if>
                                        </h3>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:choose>
                                            <c:when test="${not empty lists}">
                                                <table class="table table-bordered table-striped table-hover mb-0">
                                                    <thead class="thead-light">
                                                        <tr>
                                                            <th>Mã phiếu</th>
                                                            <th>Ngày nhập</th>
                                                            <th>Mã ĐĐH</th>
                                                            <th>Nhà CC</th>
                                                            <th>Tổng tiền</th>
                                                            <th>Trạng thái</th>
                                                            <th>Thao tác</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${lists}">
                                                            <tr>
                                                                <td><strong>${item.receiptNumber}</strong></td>
                                                                <td>
                                                                    <c:if test="${not empty item.receiptDate}">
                                                                        ${item.receiptDateFormatted}
                                                                    </c:if>
                                                                </td>
                                                                <td>${item.poNumber}</td>
                                                                <td>${item.supplierName}</td>
                                                                <td><c:if test="${item.totalAmount != null}"><fmt:formatNumber value="${item.totalAmount}" type="currency" currencySymbol="đ"/></c:if></td>
                                                                    <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.status== 'PENDING'}">
                                                                            <span class="badge badge-warning">
                                                                                <i class="fas fa-circle" style="color:#f39c12"></i> Đang nhập
                                                                            </span>
                                                                        </c:when>
                                                                        <c:when test="${item.status == 'COMPLETED'}">
                                                                            <span class="badge badge-success">Hoàn tất</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge badge-secondary">${item.status}</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=view&receiptNumber=${item.receiptNumber}"
                                                                       class="btn btn-sm btn-outline-primary">
                                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="alert alert-info m-3 text-center">
                                                    <i class="fas fa-info-circle"></i> Không có phiếu nhập kho nào.
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div><c:if test="${not empty lists && totalPages > 1}">
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
                                                           href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list&page=1&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list&page=${currentPage - 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link"
                                                               href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list&page=${i}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list&page=${currentPage + 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list&page=${totalPages}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">Last</a>
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