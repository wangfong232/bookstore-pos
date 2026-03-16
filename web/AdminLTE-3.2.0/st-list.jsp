<%-- 
    Document   : st-list
    Created on : Mar 6, 2026, 11:21:25 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Danh sách phiếu kiểm kho</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">

    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>
            <div class="content-wrapper">
                <!-- Content Header -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1><i class="fas fa-clipboard-check"></i> Quản lý kiểm kê kho</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item active">Kiểm kê kho</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- main content -->            
                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${msg.startsWith('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_save'}">Tạo phiếu kiểm kê thành công!</c:when>
                                    <c:when test="${msg == 'success_submit'}">Gửi duyệt thành công!</c:when>
                                    <c:when test="${msg == 'success_approve'}">Duyệt phiếu kiểm kê thành công!</c:when>
                                    <c:when test="${msg == 'success_recount'}">Yêu cầu kiểm kê lại thành công!</c:when>
                                    <c:when test="${msg == 'success_cancel'}">Hủy phiếu kiểm kê thành công!</c:when>
                                    <c:when test="${msg == 'fail_notfound'}">Không tìm thấy phiếu kiểm kê.</c:when>
                                    <c:when test="${msg == 'fail_self_approve'}">Người tạo không được tự duyệt!</c:when>
                                    <c:when test="${msg == 'fail_date'}">Ngày kiểm kê không hợp lệ. Chỉ được tạo phiếu vào ngày hôm nay.</c:when>
                                    <c:when test="${msg == 'fail_invalid_qty'}">Số lượng thực tế không hợp lệ (phải >= 0).</c:when>
                                    <c:when test="${msg == 'fail_save'}">Lưu phiếu kiểm kê thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_cancel'}">Hủy phiếu kiểm kê thất bại. Vui lòng thử lại.</c:when>
                                    <c:otherwise>${msg}</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">

                                <!-- Search Card -->
                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-search"></i> Tìm kiếm &amp; Lọc</h3>
                                        <div class="card-tools">
                                            <a href="${pageContext.request.contextPath}/admin/stocktake?action=create"
                                               class="btn btn-success btn-sm">
                                                <i class="fas fa-plus"></i> Tạo phiếu kiểm kê mới
                                            </a>
                                        </div>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/stocktake" method="get">
                                            <input type="hidden" name="action" value="list">
                                            <div class="row">
                                                <div class="col-md-3 form-group">
                                                    <label>Tìm kiếm:</label>
                                                    <input type="text" name="key" class="form-control"
                                                           value="${param.key}"
                                                           placeholder="Mã phiếu kiểm kê...">
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Trạng thái:</label>
                                                    <select name="status" class="form-control">
                                                        <option value="" ${empty param.status ? 'selected' : ''}>Tất cả</option>
                                                        <option value="IN_PROGRESS"       ${param.status == 'IN_PROGRESS'       ? 'selected' : ''}>Đang thực hiện</option>
                                                        <option value="PENDING_APPROVAL"  ${param.status == 'PENDING_APPROVAL'  ? 'selected' : ''}>Chờ duyệt</option>
                                                        <option value="COMPLETED"         ${param.status == 'COMPLETED'         ? 'selected' : ''}>Đã hoàn thành</option>
                                                        <option value="CANCELLED"         ${param.status == 'CANCELLED'         ? 'selected' : ''}>Đã hủy</option>
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
                                                    <a href="${pageContext.request.contextPath}/admin/stocktake?action=list"
                                                       class="btn btn-default">
                                                        <i class="fas fa-redo"></i> Đặt lại
                                                    </a>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div><!-- /search card -->

                                <!-- List card -->
                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-list"></i> Danh sách phiếu kiểm kê
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
                                                            <th style="width:150px">Mã phiếu</th>
                                                            <th>Ngày kiểm</th>
                                                            <th>Người kiểm</th>
                                                            <th style="width:80px">Số SP</th>
                                                            <th style="width:120px">Chênh lệch SL</th>
                                                            <th>Trạng thái</th>
                                                            <th style="width:100px">Thao tác</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${lists}">
                                                            <tr>
                                                                <td><strong>${item.stockTakeNumber}</strong></td>
                                                                <td>${item.stockTakeDate}</td>
                                                                <td>${item.createdByName}</td>
                                                                <td>${item.totalItems}</td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.totalVarianceQty < 0}">
                                                                            <span class="text-danger font-weight-bold">${item.totalVarianceQty}</span>
                                                                        </c:when>
                                                                        <c:when test="${item.totalVarianceQty > 0}">
                                                                            <span class="text-success font-weight-bold">+${item.totalVarianceQty}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="text-muted">0</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.status=='IN_PROGRESS'}">
                                                                            <span class="badge badge-warning">Đang thực hiện</span>
                                                                        </c:when>

                                                                        <c:when test="${item.status=='PENDING_APPROVAL'}">
                                                                            <span class="badge badge-info">Chờ duyệt</span>
                                                                        </c:when>

                                                                        <c:when test="${item.status=='CANCELLED'}">
                                                                            <span class="badge badge-danger">Đã hủy</span>
                                                                        </c:when>

                                                                        <c:when test="${item.status=='COMPLETED'}">
                                                                            <span class="badge badge-success">Đã hoàn thành</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge badge-secondary">${item.status}</span>
                                                                        </c:otherwise>  
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <a href="${pageContext.request.contextPath}/admin/stocktake?action=view&number=${item.stockTakeNumber}"
                                                                       class="btn btn-info btn-sm">
                                                                        <i class="fas fa-eye"></i> Xem
                                                                    </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center p-4 text-muted">
                                                    <i class="fas fa-inbox fa-3x mb-3"></i>
                                                    <p>Không tìm thấy phiếu kiểm kê nào.</p>
                                                </div>
                                            </c:otherwise>         
                                        </c:choose>
                                    </div><!-- ./card- -->

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
                                                           href="${pageContext.request.contextPath}/admin/stocktake?action=list&page=1&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stocktake?action=list&page=${currentPage - 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link"
                                                               href="${pageContext.request.contextPath}/admin/stocktake?action=list&page=${i}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stocktake?action=list&page=${currentPage + 1}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/stocktake?action=list&page=${totalPages}&key=${param.key}&status=${param.status}&from=${param.from}&to=${param.to}">Last</a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </c:if>

                                </div><!-- /card -->
                            </div><!-- /col -->
                        </div><!-- ./row container-fluid -->
                </section><!-- ./content -->
            </div><!-- ./content-wrapper -->
            <jsp:include page="include/admin-footer.jsp"/>
        </div><!-- ./wrapper -->
    </body>
</html>
