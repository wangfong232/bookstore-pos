<%--
    Document   : it-list
    Created on : Mar 16, 2026
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="util.DateUtil"%> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Lịch sử giao dịch kho</title>
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
                                <h1><i class="fas fa-history"></i> Lịch sử giao dịch kho</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item active">Lịch sử giao dịch kho</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty msg}">
                            <div class="alert ${fn:startsWith(msg,'success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${fn:startsWith(msg,'success') ? 'fa-check' : 'fa-ban'}"></i>
                                ${msg}
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-12">

                                <div class="card card-info card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-search"></i> Tìm kiếm & Lọc</h3>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/inventorytransaction" method="get">
                                            <input type="hidden" name="action" value="list">
                                            <div class="row">
                                                <div class="col-md-4 form-group">
                                                    <label>Tìm kiếm:</label>
                                                    <input type="text" name="key" class="form-control"
                                                           value="${param.key}"
                                                           placeholder="Mã tham chiếu, tên sản phẩm, SKU, ghi chú...">
                                                </div>
                                                <div class="col-md-2 form-group">
                                                    <label>Loại giao dịch:</label>
                                                    <select name="txType" class="form-control">
                                                        <option value="" ${empty param.txType ? 'selected' : ''}>Tất cả</option>
                                                        <option value="IN"         ${param.txType == 'IN'         ? 'selected' : ''}>IN — Nhập kho</option>
                                                        <option value="OUT"        ${param.txType == 'OUT'        ? 'selected' : ''}>OUT — Xuất kho</option>
                                                        <option value="ADJUSTMENT" ${param.txType == 'ADJUSTMENT' ? 'selected' : ''}>ADJUSTMENT — Điều chỉnh</option>
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
                                                    <a href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list"
                                                       class="btn btn-default">
                                                        <i class="fas fa-redo"></i> Đặt lại
                                                    </a>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div><!-- /search card -->

                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-list"></i> Danh sách giao dịch
                                            <c:if test="${totalRecords > 0}">
                                                <span class="badge badge-secondary ml-2">${totalRecords} giao dịch</span>
                                            </c:if>
                                        </h3>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:choose>
                                            <c:when test="${not empty lists}">
                                                <table class="table table-bordered table-striped table-hover">
                                                    <thead class="thead-light">
                                                        <tr>
                                                            <th style="width:140px">Ngày giờ</th>
                                                            <th style="width:110px" class="text-center">Loại</th>
                                                            <th style="width:130px">Loại tham chiếu</th>
                                                            <th style="width:160px">Mã tham chiếu</th>
                                                            <th>Sản phẩm</th>
                                                            <th style="width:80px" class="text-center">SL</th>
                                                            <th style="width:80px" class="text-center">Tồn trước</th>
                                                            <th style="width:80px" class="text-center">Tồn sau</th>
                                                            <th style="width:140px">Người thực hiện</th>
                                                            <th>Ghi chú</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${lists}">
                                                            <tr>
                                                                <td>${DateUtil.format(item.transactionDate)}</td>                                                                <td class="text-center">
                                                                    <c:choose>
                                                                        <c:when test="${item.transactionType == 'IN'}">
                                                                            <span class="badge badge-success">IN</span>
                                                                        </c:when>
                                                                        <c:when test="${item.transactionType == 'OUT'}">
                                                                            <span class="badge badge-danger">OUT</span>
                                                                        </c:when>
                                                                        <c:when test="${item.transactionType == 'ADJUSTMENT'}">
                                                                            <span class="badge badge-warning">ADJ</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge badge-secondary">${item.transactionType}</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.referenceType == 'GOODS_RECEIPT'}">Nhập hàng</c:when>
                                                                        <c:when test="${item.referenceType == 'SALE'}">Bán hàng</c:when>
                                                                        <c:when test="${item.referenceType == 'DISPOSAL'}">Xuất hủy</c:when>
                                                                        <c:when test="${item.referenceType == 'STOCK_TAKE'}">Kiểm kê</c:when>
                                                                        <c:when test="${item.referenceType == 'MANUAL'}">Thủ công</c:when>
                                                                        <c:otherwise>${item.referenceType}</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${not empty item.referenceUrl}">
                                                                            <a href="${item.referenceUrl}">${item.referenceCode}</a>
                                                                        </c:when>
                                                                        <c:otherwise>${item.referenceCode}</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <strong>${item.productSku}</strong>
                                                                    <c:if test="${not empty item.productName}">
                                                                        <br><small class="text-muted">${item.productName}</small>
                                                                    </c:if>
                                                                </td>
                                                                <td class="text-center font-weight-bold">
                                                                    <c:choose>
                                                                        <c:when test="${item.quantityChange > 0}">
                                                                            <span class="text-success">+${item.quantityChange}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="text-danger">${item.quantityChange}</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="text-center">${item.stockBefore}</td>
                                                                <td class="text-center">${item.stockAfter}</td>
                                                                <td>${item.createdByName}</td>
                                                                <td><small>${item.notes}</small></td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center py-5 text-muted">
                                                    <i class="fas fa-inbox fa-3x mb-3"></i>
                                                    <p>Không có giao dịch nào phù hợp với điều kiện tìm kiếm.</p>
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
                                                           href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list&page=1&key=${param.key}&txType=${param.txType}&from=${param.from}&to=${param.to}">First</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list&page=${currentPage - 1}&key=${param.key}&txType=${param.txType}&from=${param.from}&to=${param.to}">«</a>
                                                    </li>
                                                </c:if>

                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                    <c:if test="${i == currentPage || i == currentPage - 1 || i == currentPage + 1}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link"
                                                               href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list&page=${i}&key=${param.key}&txType=${param.txType}&from=${param.from}&to=${param.to}">${i}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${currentPage < totalPages}">
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list&page=${currentPage + 1}&key=${param.key}&txType=${param.txType}&from=${param.from}&to=${param.to}">»</a>
                                                    </li>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/admin/inventorytransaction?action=list&page=${totalPages}&key=${param.key}&txType=${param.txType}&from=${param.from}&to=${param.to}">Last</a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </c:if>
                                </div><!-- /list card -->

                            </div><!-- ./col-12 -->
                        </div><!-- /row -->

                    </div>
                </section><!-- ./content -->
            </div><!-- ./content-wrapper -->

            <jsp:include page="include/admin-footer.jsp"/>
        </div><!-- ./wrapper -->
    </body>
</html>
