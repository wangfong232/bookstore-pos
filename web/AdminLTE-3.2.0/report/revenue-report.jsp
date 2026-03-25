<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <title>Báo cáo Doanh thu - Bookstore POS</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    <style>
        .chart-card { border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,.08); }
        .chart-card .card-header { border-radius: 12px 12px 0 0; font-weight: 600; }
        .small-box { border-radius: 12px; overflow: hidden; }
        .donut-chart { height: 280px; }
    </style>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="../include/admin-header.jsp"/>
    <jsp:include page="../include/admin-sidebar.jsp"/>

    <div class="content-wrapper">
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><i class="fas fa-chart-line text-primary"></i> Báo cáo Doanh thu</h1>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">

                <!-- Filter -->
                <div class="card card-outline card-primary chart-card">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/report/revenue" class="row align-items-end">
                            <div class="col-md-3">
                                <label>Từ ngày</label>
                                <input type="date" name="fromDate" value="${fromDate}" class="form-control">
                            </div>
                            <div class="col-md-3">
                                <label>Đến ngày</label>
                                <input type="date" name="toDate" value="${toDate}" class="form-control">
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary btn-block">
                                    <i class="fas fa-search"></i> Xem báo cáo
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- 1. THÔNG SỐ TỔNG QUAN -->
                <div class="row">
                    <div class="col-lg-3 col-6">
                        <div class="small-box bg-info">
                            <div class="inner">
                                <h3><fmt:formatNumber value="${overview.totalRevenue}" type="number" maxFractionDigits="0"/>đ</h3>
                                <p>Tổng doanh thu</p>
                            </div>
                            <div class="icon"><i class="fas fa-coins"></i></div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-6">
                        <div class="small-box bg-warning">
                            <div class="inner">
                                <h3><fmt:formatNumber value="${overview.totalDiscount}" type="number" maxFractionDigits="0"/>đ</h3>
                                <p>Tổng chiết khấu</p>
                            </div>
                            <div class="icon"><i class="fas fa-tags"></i></div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-6">
                        <div class="small-box bg-success">
                            <div class="inner">
                                <h3>${overview.orderCount}</h3>
                                <p>Số đơn hàng</p>
                            </div>
                            <div class="icon"><i class="fas fa-shopping-cart"></i></div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-6">
                        <div class="small-box bg-danger">
                            <div class="inner">
                                <h3><fmt:formatNumber value="${overview.avgOrder}" type="number" maxFractionDigits="0"/>đ</h3>
                                <p>Đơn hàng trung bình</p>
                            </div>
                            <div class="icon"><i class="fas fa-chart-bar"></i></div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <!-- 4a. DOANH THU THEO NHÂN VIÊN - Bar chart -->
                    <div class="col-lg-6">
                        <div class="card chart-card">
                            <div class="card-header bg-success text-white">
                                <h3 class="card-title"><i class="fas fa-user-tie mr-1"></i> Doanh thu theo nhân viên</h3>
                            </div>
                            <div class="card-body">
                                <div class="position-relative donut-chart">
                                    <canvas id="revenue-by-staff-chart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 4b. DOANH THU THEO CA LÀM - Donut chart -->
                    <div class="col-lg-6">
                        <div class="card chart-card">
                            <div class="card-header bg-info text-white">
                                <h3 class="card-title"><i class="fas fa-clock mr-1"></i> Doanh thu theo ca làm</h3>
                            </div>
                            <div class="card-body">
                                <div class="position-relative donut-chart">
                                    <canvas id="revenue-by-shift-chart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 6. DOANH THU THEO DANH MỤC - Donut chart -->
                <div class="row">
                    <div class="col-12">
                        <div class="card chart-card">
                            <div class="card-header bg-warning">
                                <h3 class="card-title"><i class="fas fa-book mr-1"></i> Doanh thu theo danh mục sản phẩm</h3>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="position-relative" style="height: 300px;">
                                            <canvas id="revenue-by-category-chart"></canvas>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <table class="table table-sm table-hover">
                                            <thead><tr><th>Danh mục</th><th class="text-right">Doanh thu</th></tr></thead>
                                            <tbody>
                                                <c:forEach items="${revenueByCategory}" var="cat">
                                                    <tr>
                                                        <td>${cat.categoryName}</td>
                                                        <td class="text-right"><fmt:formatNumber value="${cat.revenue}" type="number" maxFractionDigits="0"/>đ</td>
                                                    </tr>
                                                </c:forEach>
                                                <c:if test="${empty revenueByCategory}">
                                                    <tr><td colspan="2" class="text-muted text-center">Chưa có dữ liệu</td></tr>
                                                </c:if>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </section>
    </div>

    <jsp:include page="../include/admin-footer.jsp"/>
</div>

<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/chart.js/Chart.min.js"></script>

<script>
(function() {
    const ctx = document.getElementById('revenue-by-staff-chart');
    if (!ctx) return;

    const labels = [<c:forEach items="${revenueByStaff}" var="s" varStatus="st">'${fn:replace(s.staffName, "'", "\\'")}'<c:if test="${!st.last}">,</c:if></c:forEach>];
    const data = [<c:forEach items="${revenueByStaff}" var="s" varStatus="st">${s.revenue}<c:if test="${!st.last}">,</c:if></c:forEach>];
    const colors = ['#007bff','#28a745','#ffc107','#dc3545','#17a2b8','#6f42c1'];
    if (labels.length === 0) return;

    new Chart(ctx, {
        type: 'horizontalBar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (đ)',
                data: data,
                backgroundColor: data.map(function(_, i) { return colors[i % colors.length]; }),
                borderColor: data.map(function(_, i) { return colors[i % colors.length]; }),
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            legend: { display: false },
            scales: {
                xAxes: [{
                    beginAtZero: true,
                    ticks: {
                        callback: function(v) { return v.toLocaleString('vi-VN') + ' đ'; }
                    }
                }],
                yAxes: [{}]
            }
        }
    });
})();

(function() {
    const ctx = document.getElementById('revenue-by-shift-chart');
    if (!ctx) return;

    const labels = [<c:forEach items="${revenueByShift}" var="s" varStatus="st">'${fn:replace(s.shiftName, "'", "\\'")}'<c:if test="${!st.last}">,</c:if></c:forEach>];
    const data = [<c:forEach items="${revenueByShift}" var="s" varStatus="st">${s.revenue}<c:if test="${!st.last}">,</c:if></c:forEach>];
    const colors = ['#007bff','#28a745','#ffc107','#dc3545'];
    if (labels.length === 0) return;

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors.slice(0, data.length),
                borderWidth: 2,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            legend: { position: 'bottom' }
        }
    });
})();

(function() {
    const ctx = document.getElementById('revenue-by-category-chart');
    if (!ctx) return;

    const labels = [<c:forEach items="${revenueByCategory}" var="c" varStatus="st">'${fn:replace(c.categoryName, "'", "\\'")}'<c:if test="${!st.last}">,</c:if></c:forEach>];
    const data = [<c:forEach items="${revenueByCategory}" var="c" varStatus="st">${c.revenue}<c:if test="${!st.last}">,</c:if></c:forEach>];
    const colors = ['#007bff','#28a745','#ffc107','#dc3545','#17a2b8','#6f42c1','#e83e8c','#fd7e14'];
    if (labels.length === 0) return;

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors.slice(0, data.length),
                borderWidth: 2,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            legend: { position: 'right' }
        }
    });
})();
</script>
</body>
</html>
