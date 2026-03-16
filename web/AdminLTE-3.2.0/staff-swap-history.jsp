<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Lịch Sử Đổi Ca</title>
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

                <section class="content-header">
                    <div class="container-fluid">
                        <h1><i class="fas fa-history"></i> Lịch Sử Đổi Ca Của Tôi</h1>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <div class="card card-outline card-primary">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h3 class="card-title">Danh sách đơn đổi ca</h3>
                                <a href="${pageContext.request.contextPath}/staff/swap"
                                   class="btn btn-primary btn-sm">
                                    <i class="fas fa-plus"></i> Gửi đơn mới
                                </a>
                            </div>

                            <div class="card-body table-responsive p-0">
                                <table class="table table-hover text-nowrap">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Ca của tôi</th>
                                            <th>Ngày</th>
                                            <th>Đổi với</th>
                                            <th>Ca đổi</th>
                                            <th>Ngày đổi</th>
                                            <th>Lý do</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                        <c:forEach var="r" items="${myRequests}">
                                            <tr>
                                                <td>#${r.swapRequestID}</td>
                                                <td>${r.shiftName}</td>
                                                <td>${r.workDate}</td>
                                                <td>${r.toFullName}</td>
                                                <td>${r.toShiftName}</td>
                                                <td>${r.toWorkDate}</td>
                                                <td>${r.reason}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${r.status == 'PENDING'}">
                                                            <span class="badge badge-warning">
                                                                <i class="fas fa-clock"></i> Chờ duyệt
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${r.status == 'APPROVED'}">
                                                            <span class="badge badge-success">
                                                                <i class="fas fa-check"></i> Đã chấp nhận
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-danger">
                                                                <i class="fas fa-times"></i> Bị từ chối
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>

                                        <c:if test="${empty myRequests}">
                                            <tr>
                                                <td colspan="8" class="text-center text-muted py-4">
                                                    <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                                    Bạn chưa có đơn đổi ca nào.
                                                </td>
                                            </tr>
                                        </c:if>

                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>
                </section>

            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>
    </body>
</html>
