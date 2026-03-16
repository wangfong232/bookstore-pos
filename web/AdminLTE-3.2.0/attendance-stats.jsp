<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Thống kê giờ làm việc - Admin</title>

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
                                <h1><i class="fas fa-chart-bar"></i> Thống kê giờ làm theo tháng</h1>
                            </div>
                            <div class="col-sm-6 text-right">
                                <a href="${pageContext.request.contextPath}/admin/attendance?action=list"
                                   class="btn btn-secondary">
                                    <i class="fas fa-arrow-left"></i> Quay lại chấm công
                                </a>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <!-- FILTER THEO THÁNG / NĂM -->
                        <div class="card card-outline card-primary">
                            <div class="card-body">
                                <form method="get"
                                      action="${pageContext.request.contextPath}/admin/attendance">

                                    <input type="hidden" name="action" value="stats"/>

                                    <div class="row">

                                        <div class="col-md-3">
                                            <label>Tháng</label>
                                            <select name="month" class="form-control">
                                                <c:forEach begin="1" end="12" var="m">
                                                    <option value="${m}" ${m == month ? 'selected' : ''}>
                                                        Tháng ${m}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label>Năm</label>
                                            <select name="year" class="form-control">
                                                <c:forEach begin="2023" end="2030" var="y">
                                                    <option value="${y}" ${y == year ? 'selected' : ''}>
                                                        ${y}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-md-3 d-flex align-items-end">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-search"></i> Xem thống kê
                                            </button>
                                        </div>

                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- BẢNG THỐNG KÊ -->
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <i class="fas fa-clock mr-1"></i>
                                    Tổng giờ làm việc – Tháng ${month}/${year}
                                </h3>
                            </div>

                            <div class="card-body table-responsive p-0">
                                <table class="table table-hover table-striped text-nowrap">
                                    <thead class="bg-light">
                                        <tr>
                                            <th style="width: 50px;">#</th>
                                            <th>Nhân viên</th>
                                            <th class="text-center">Số ngày có mặt</th>
                                            <th class="text-center">Tổng giờ làm việc</th>
                                        </tr>
                                    </thead>

                                    <tbody>

                                        <c:choose>
                                            <c:when test="${not empty statsList}">
                                                <c:forEach items="${statsList}" var="s" varStatus="loop">
                                                    <tr>
                                                        <td>
                                                            ${(currentPage - 1) * 10 + loop.index + 1}
                                                        </td>

                                                        <td>
                                                            <i class="fas fa-user-circle text-muted mr-1"></i>
                                                            ${s.fullName}
                                                        </td>

                                                        <td class="text-center">
                                                            <span class="badge badge-info">
                                                                ${s.workDays} ngày
                                                            </span>
                                                        </td>

                                                        <td class="text-center">
                                                            <strong>${s.totalHoursFormatted}</strong>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="4" class="text-center text-muted py-4">
                                                        <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                                        Không có dữ liệu chấm công cho tháng ${month}/${year}.
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>

                                    </tbody>
                                </table>

                                <!-- PHÂN TRANG -->
                                <div class="card-footer clearfix">
                                    <small class="text-muted float-left mt-1">
                                        Trang ${currentPage} / ${totalPages}
                                    </small>
                                    <ul class="pagination pagination-sm m-0 float-right">

                                        <%-- Nút Previous --%>
                                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="?action=stats&month=${month}&year=${year}&page=${currentPage - 1}">
                                                &laquo;
                                            </a>
                                        </li>

                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link"
                                                   href="?action=stats&month=${month}&year=${year}&page=${i}">
                                                    ${i}
                                                </a>
                                            </li>
                                        </c:forEach>

                                        <%-- Nút Next --%>
                                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="?action=stats&month=${month}&year=${year}&page=${currentPage + 1}">
                                                &raquo;
                                            </a>
                                        </li>

                                    </ul>
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
