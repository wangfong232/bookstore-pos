<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách hóa đơn bán - Bookstore POS</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    <style>
        .invoice-filters-card { border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,.06); }
        .invoice-filters-card .card-header { border-radius: 12px 12px 0 0; }
        .invoice-status-badge { padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 600; }
        .invoice-status-paid { background: rgba(34, 197, 94, 0.1); color: #16a34a; }
        .invoice-status-pending { background: rgba(234, 179, 8, 0.1); color: #eab308; }
        .invoice-status-cancelled { background: rgba(248, 113, 113, 0.12); color: #ef4444; }
        .table-invoice-list th { white-space: nowrap; }
        .table-invoice-list td { vertical-align: middle; }
        .invoice-empty-state { padding: 40px 10px; text-align: center; color: #6b7280; }
        .invoice-empty-icon { font-size: 32px; margin-bottom: 8px; color: #9ca3af; }
    </style>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="include/admin-header.jsp"/>
    <jsp:include page="include/admin-sidebar.jsp"/>

    <div class="content-wrapper">
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <i class="fas fa-file-invoice-dollar text-primary"></i>
                            Danh sách hóa đơn bán
                        </h1>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">

                <div class="card card-outline card-primary invoice-filters-card mb-3">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-filter mr-1"></i>
                            Bộ lọc hóa đơn
                        </h3>
                    </div>
                    <div class="card-body">
                        <form method="get"
                              action="${pageContext.request.contextPath}/sales-invoices"
                              class="row align-items-end">
                            <div class="col-lg-6 col-md-12 mb-2">
                                <label class="mb-1">Từ khóa</label>
                                <input type="text"
                                       name="q"
                                       value="${keyword}"
                                       class="form-control"
                                       placeholder="Nhập mã hóa đơn, tên khách hàng hoặc nhân viên rồi nhấn Enter để tìm">
                            </div>
                            <div class="col-lg-6 col-md-12 mb-2">
                                <div class="d-flex flex-wrap justify-content-lg-end">
                                    <div class="mr-2 mb-2" style="min-width: 150px;">
                                        <label class="mb-1">Từ ngày</label>
                                        <input type="date"
                                               name="fromDate"
                                               value="${fromDate}"
                                               class="form-control">
                                    </div>
                                    <div class="mr-2 mb-2" style="min-width: 150px;">
                                        <label class="mb-1">Đến ngày</label>
                                        <input type="date"
                                               name="toDate"
                                               value="${toDate}"
                                               class="form-control">
                                    </div>
                                    <div class="mb-2">
                                        <label class="mb-1 d-block">&nbsp;</label>
                                        <button type="submit" class="btn btn-primary mr-2">
                                            <i class="fas fa-search mr-1"></i>
                                            Lọc
                                        </button>
                                        <a href="${pageContext.request.contextPath}/sales-invoices"
                                           class="btn btn-outline-secondary">
                                            Gỡ bộ lọc
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-list mr-1"></i>
                            Danh sách hóa đơn gần đây
                        </h3>
                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <c:if test="${not empty invoices}">
                            <div class="table-responsive">
                                <table class="table table-hover table-striped mb-0 table-invoice-list">
                                    <thead class="thead-light">
                                    <tr>
                                        <th style="width: 40px;">#</th>
                                        <th>Mã hóa đơn</th>
                                        <th>Ngày tạo</th>
                                        <th>Khách hàng</th>
                                        <th>Nhân viên</th>
                                        <th class="text-right">Tổng tiền</th>
                                        <th class="text-right">Chiết khấu</th>
                                        <th class="text-right">Thanh toán</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="inv" items="${invoices}" varStatus="st">
                                        <tr>
                                            <td>${st.index + 1}</td>
                                            <td class="font-weight-bold">${inv.invoiceCode}</td>
                                            <td>
                                                <fmt:formatDate value="${inv.paidAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty inv.customerName}">
                                                        ${inv.customerName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Khách vãng lai</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${inv.staffName}</td>
                                            <td class="text-right">
                                                <fmt:formatNumber value="${inv.totalAmount}" type="number" maxFractionDigits="0"/> đ
                                            </td>
                                            <td class="text-right">
                                                <fmt:formatNumber value="${inv.discountAmount}" type="number" maxFractionDigits="0"/> đ
                                            </td>
                                            <td class="text-right font-weight-bold">
                                                <fmt:formatNumber value="${inv.finalAmount}" type="number" maxFractionDigits="0"/> đ
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${inv.paymentStatus == 'PAID'}">
                                                        <span class="invoice-status-badge invoice-status-paid">Đã thanh toán</span>
                                                    </c:when>
                                                    <c:when test="${inv.paymentStatus == 'PENDING'}">
                                                        <span class="invoice-status-badge invoice-status-pending">Chờ thanh toán</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="invoice-status-badge invoice-status-cancelled">Đã hủy</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>

                        <c:if test="${empty invoices}">
                            <div class="invoice-empty-state">
                                <div class="invoice-empty-icon">
                                    <i class="fas fa-receipt"></i>
                                </div>
                                <p class="mb-1">Chưa có dữ liệu hóa đơn để hiển thị.</p>
                                <p class="text-muted mb-0">Hãy thực hiện giao dịch tại màn hình POS, sau đó quay lại đây để xem lịch sử hóa đơn.</p>
                            </div>
                        </c:if>
                    </div>
                </div>

            </div>
        </section>
    </div>

    <jsp:include page="include/admin-footer.jsp"/>
</div>
</body>
</html>

