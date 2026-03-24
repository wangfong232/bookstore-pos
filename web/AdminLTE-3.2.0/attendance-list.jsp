<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Quản lý Chấm công - Admin</title>

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
                                <h1><i class="fas fa-clock"></i> Quản lý Chấm công</h1>
                            </div>
                            <div class="col-sm-6 text-right">
                                <a href="${pageContext.request.contextPath}/admin/attendance?action=stats"
                                   class="btn btn-success">
                                    <i class="fas fa-chart-bar"></i> Xem thống kê tháng
                                </a>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <!-- FILTER THEO NGÀY -->
                        <div class="card card-outline card-primary">
                            <div class="card-body">

                                <form method="get"
                                      action="${pageContext.request.contextPath}/admin/attendance">

                                    <input type="hidden" name="action" value="list"/>

                                    <div class="row">

                                        <div class="col-md-4">
                                            <label>Chọn ngày</label>
                                            <input type="date"
                                                   name="workDate"
                                                   value="${workDate}"
                                                   class="form-control">
                                        </div>

                                        <div class="col-md-4 d-flex align-items-end">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-search"></i> Xem
                                            </button>

                                            <a href="${pageContext.request.contextPath}/admin/attendance?action=list"
                                               class="btn btn-secondary ml-2">
                                                Reset
                                            </a>
                                        </div>

                                    </div>
                                </form>

                            </div>
                        </div>

                        <div class="row">

                            <div class="col-lg-4 col-6">
                                <div class="small-box bg-info">
                                    <div class="inner">
                                        <h3>${stats.total}</h3>
                                        <p>Tổng nhân viên hôm nay</p>
                                    </div>
                                    <div class="icon">
                                        <i class="fas fa-users"></i>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-4 col-6">
                                <div class="small-box bg-success">
                                    <div class="inner">
                                        <h3>${stats.checkedIn}</h3>
                                        <p>Đã check-in</p>
                                    </div>
                                    <div class="icon">
                                        <i class="fas fa-check"></i>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-4 col-6">
                                <div class="small-box bg-danger">
                                    <div class="inner">
                                        <h3>${stats.notCheckedIn}</h3>
                                        <p>Chưa check-in</p>
                                    </div>
                                    <div class="icon">
                                        <i class="fas fa-times"></i>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <!-- TABLE ATTENDANCE -->
                        <div class="card">
                            <div class="card-header">
                                <h3 class="card-title">
                                    Danh sách chấm công ngày ${workDate}
                                </h3>
                            </div>

                            <div class="card-body table-responsive p-0">
                                <table class="table table-hover text-nowrap">

                                    <thead>
                                        <tr>
                                            <th>Nhân viên</th>
                                            <th>Ca</th>
                                            <th>Giờ vào</th>
                                            <th>Giờ ra</th>
                                            <th>Trạng thái</th>
                                            <th>Đi trễ</th>
                                        </tr>
                                    </thead>

                                    <tbody>

                                        <c:forEach items="${attendanceList}" var="a">

                                            <tr>

                                                <td>${a.fullName}</td>

                                                <td>
                                                    ${a.shiftName}
                                                    <br/>
                                                    <small class="text-muted">
                                                        ${a.startTime} - ${a.endTime}
                                                    </small>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${a.checkIn != null}">
                                                            ${a.checkIn}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-danger">--</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${a.checkOut != null}">
                                                            ${a.checkOut}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-warning">--</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <!-- STATUS -->
                                                <td>
                                                    <c:choose>

                                                        <c:when test="${a.checkIn == null}">
                                                            <span class="badge badge-danger">
                                                                ABSENT
                                                            </span>
                                                        </c:when>

                                                        <c:when test="${a.checkIn != null && a.checkOut == null}">
                                                            <span class="badge badge-warning">
                                                                WORKING
                                                            </span>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <span class="badge badge-success">
                                                                COMPLETED
                                                            </span>
                                                        </c:otherwise>

                                                    </c:choose>
                                                </td>

                                                <!-- LATE -->
                                                <td>
                                                    <c:if test="${a.checkIn != null && a.checkIn > a.startTime}">
                                                        <span class="badge badge-danger">
                                                            LATE
                                                        </span>
                                                    </c:if>
                                                </td>

                                            </tr>

                                        </c:forEach>

                                        <c:if test="${empty attendanceList}">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted">
                                                    Không có dữ liệu chấm công cho ngày này.
                                                </td>
                                            </tr>
                                        </c:if>

                                    </tbody>

                                </table>
                                <div class="card-footer clearfix">
                                    <ul class="pagination pagination-sm m-0 float-right">

                                        <c:forEach begin="1" end="${totalPages}" var="i">

                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link"
                                                   href="?action=list&workDate=${workDate}&page=${i}">
                                                    ${i}
                                                </a>
                                            </li>

                                        </c:forEach>

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