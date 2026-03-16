<%-- 
    Document   : sd-list
    Created on : Mar 11, 2026, 12:45:10 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Danh sách phiếu xuất hủy</title>
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
                                <h1><i class="fas fa-trash-alt"></i> Quản lý xuất hủy hàng</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item active">Xuất hủy hàng</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">
                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${msg.startsWith('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_save'}">Tạo phiếu xuất hủy thành công!</c:when>
                                    <c:when test="${msg == 'success_approve'}">Duyệt phiếu xuất hủy thành công!</c:when>
                                    <c:when test="${msg == 'success_reject'}">Đã từ chối phiếu xuất hủy.</c:when>
                                    <c:when test="${msg == 'success_complete'}">Hoàn tất xuất hủy! Tồn kho đã được cập nhật.</c:when>
                                    <c:when test="${msg == 'fail_notfound'}">Không tìm thấy phiếu xuất hủy.</c:when>
                                    <c:when test="${msg == 'fail_save'}">Tạo phiếu thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_noproduct'}">Vui lòng thêm ít nhất một sản phẩm.</c:when>
                                    <c:when test="${msg == 'fail_noreason'}">Vui lòng chọn lý do xuất hủy.</c:when>
                                    <c:when test="${msg == 'fail_invalid_qty'}">Số lượng xuất hủy không hợp lệ (phải &gt; 0).</c:when>
                                    <c:when test="${fn:startsWith(msg, 'fail_exceed_stock:')}">Số lượng xuất hủy vượt quá tồn kho: ${fn:substringAfter(msg, 'fail_exceed_stock:')}</c:when>
                                    <c:otherwise>${msg}</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">
                                <div class="card card-danger card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-search"></i> Tìm kiếm & Lọc</h3>
                                        <div class="card-tools">
                                            <a href="${pageContext.request.contextPath}/admin/stockdisposal?action=create"
                                               class="btn btn-success btn-sm">
                                                <i class="fas fa-plus"></i> Tạo phiếu xuất hủy mới
                                            </a>
                                        </div>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/stockdisposal" method="get">
                                            <input type="hidden" name="action" value="list">
                                            <div class="row">
                                                <div class="col-md-3 form-group">
                                                    <label>Tìm kiếm:</label>
                                                    <input type="text" name="key" class="form-control"
                                                           value="${param.key}"
                                                           placeholder="Mã phiếu, ghi chú...">
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Trạng thái:</label>
                                                    <select name="status" class="form-control">
                                                        <option value="" ${empty param.status ? 'selected' : ''}>Tất cả</option>
                                                        <option value="PENDING_APPROVAL" ${param.status == 'PENDING_APPROVAL' ? 'selected' : ''}>Chờ duyệt</option>
                                                        <option value="APPROVED" ${param.status == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
                                                        <option value="REJECTED" ${param.status == 'REJECTED' ? 'selected' : ''}>Từ chối</option>
                                                        <option value="COMPLETED"${param.status == 'COMPLETED'? 'selected' : ''}>Đã hoàn tất</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Lý do:</label>
                                                    <select name="reason" class="form-control">
                                                        <option value="" ${empty param.reason ? 'selected' : ''}>Tất cả</option>
                                                        <option value="DAMAGED" ${param.reason == 'DAMAGED'   ? 'selected' : ''}>Hỏng hóc</option>
                                                        <option value="EXPIRED" ${param.reason == 'EXPIRED'   ? 'selected' : ''}>Hết hạn</option>
                                                        <option value="DEFECTIVE" ${param.reason == 'DEFECTIVE' ? 'selected' : ''}>Lỗi sản phẩm</option>
                                                        <option value="OTHER" ${param.reason == 'OTHER'     ? 'selected' : ''}>Khác</option>
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
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <button type="submit" class="btn btn-primary mr-2">
                                                        <i class="fas fa-search"></i> Tìm kiếm
                                                    </button>
                                                    <a href="${pageContext.request.contextPath}/admin/stockdisposal?action=list"
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
                                            <i class="fas fa-list"></i> Danh sách phiếu xuất hủy
                                            <c:if test="${totalRecords > 0}">
                                                <span class="badge badge-secondary ml-2">${totalRecords} phiếu</span>
                                            </c:if>
                                        </h3>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:choose>
                                            <c:when test="${not empty lists}">
                                                <table class="table table-bordered table-striped table-hover">
                                                    <thead class="thead-light">
                                                        <tr>
                                                            <th style="width:140px">Mã phiếu</th>
                                                            <th style="width:140px">Ngày tạo</th>
                                                            <th>Lý do chung</th>
                                                            <th style="width:80px" class="text-center">Tổng SP</th>
                                                            <th style="width:120px" class="text-right">Giá trị</th>
                                                            <th style="width:130px">Trạng thái</th>
                                                            <th>Người tạo</th>
                                                            <th style="width:160px" class="text-center">Thao tác</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${lists}" >
                                                            <tr>
                                                                <td><strong>${item.disposalNumber}</strong></td>
                                                                <td>
                                                                    ${item.disposalDateFormatted}
                                                                </td>  
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.disposalReason == 'DAMAGED'}">Hỏng hóc</c:when>
                                                                        <c:when test="${item.disposalReason == 'EXPIRED'}">Hết hạn</c:when>
                                                                        <c:when test="${item.disposalReason == 'DEFECTIVE'}">Lỗi sản phẩm</c:when>
                                                                        <c:otherwise>Khác</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="text-center">${item.totalQuantity}</td>
                                                                <td class="text-right">
                                                                    <fmt:formatNumber value="${item.totalValue}" type="number" maxFractionDigits="0"/>đ
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.status== 'PENDING_APPROVAL'}">
                                                                            <span class="badge badge-warning"><i class="fas fa-clock mr-1"></i>Chờ duyệt</span>
                                                                        </c:when>
                                                                        <c:when test="${item.status== 'APPROVED'}">
                                                                            <span class="badge badge-info"><i class="fas fa-check-circle mr-1"></i>Đã duyệt</span>
                                                                        </c:when>
                                                                        <c:when test="${item.status== 'REJECTED'}">
                                                                            <span class="badge badge-danger"><i class="fas fa-times-circle mr-1"></i>Từ chối</span>
                                                                        </c:when>
                                                                        <c:when test="${item.status== 'COMPLETED'}">
                                                                            <span class="badge badge-success"><i class="fas fa-check-double mr-1"></i>Hoàn thành</span>
                                                                        </c:when>
                                                                    </c:choose>
                                                                </td>
                                                                <td>${item.createdByName}</td>
                                                                <td class="text-center">
                                                                    <a href="${pageContext.request.contextPath}/admin/stockdisposal?action=view&number=${item.disposalNumber}"
                                                                       class="btn btn-info btn-sm">
                                                                        <i class="fas fa-eye"></i> Xem chi tiết
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center py-5 text-muted">
                                                    <i class="fas fa-inbox fa-3x mb-3"></i>
                                                    <p>Không có phiếu xuất hủy nào.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
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
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stockdisposal?action=list&page=1&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stockdisposal?action=list&page=${currentPage - 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link"
                                                               href="${pageContext.request.contextPath}/admin/stockdisposal?action=list&page=${i}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stockdisposal?action=list&page=${currentPage + 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stockdisposal?action=list&page=${totalPages}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">Last</a>
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
            <jsp:include page="include/admin-footer.jsp"/>
        </div>
    </body>
</html>