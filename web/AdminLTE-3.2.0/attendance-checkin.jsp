<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Chấm công - Nhân viên</title>
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
                                <h1><i class="fas fa-fingerprint"></i> Chấm công</h1>
                            </div>
                            <div class="col-sm-6">
                                <small class="text-muted float-right pt-2">
                                    <i class="fas fa-calendar-day"></i>
                                    Hôm nay: <strong><%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) %></strong>
                                </small>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <!-- Thông báo lỗi/thành công -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <i class="fas fa-exclamation-circle"></i> ${error}
                                <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show">
                                <i class="fas fa-check-circle"></i> ${success}
                                <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${not empty shifts}">

                                <c:forEach items="${shifts}" var="shift" varStatus="loop">
                                    <div class="row mb-4">

                                        <!-- Thông tin ca làm việc -->
                                        <div class="col-md-6">
                                            <div class="card card-primary">
                                                <div class="card-header">
                                                    <h3 class="card-title">
                                                        <i class="fas fa-calendar-alt"></i>
                                                        Ca làm việc<c:if test="${fn:length(shifts) > 1}"> #${loop.index + 1}</c:if>
                                                    </h3>
                                                </div>
                                                <div class="card-body">
                                                    <div class="form-group">
                                                        <label>Tên ca:</label>
                                                        <div class="alert alert-info mb-2 py-2">
                                                            <strong>${shift.shiftName}</strong>
                                                        </div>
                                                    </div>

                                                    <div class="row">
                                                        <div class="col-6">
                                                            <label>Giờ vào:</label>
                                                            <div class="alert alert-light mb-0 py-1">
                                                                <i class="fas fa-sign-in-alt text-success"></i> ${shift.startTime}
                                                            </div>
                                                        </div>
                                                        <div class="col-6">
                                                            <label>Giờ ra:</label>
                                                            <div class="alert alert-light mb-0 py-1">
                                                                <i class="fas fa-sign-out-alt text-danger"></i> ${shift.endTime}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Trạng thái và nút check-in/out -->
                                        <div class="col-md-6">
                                            <div class="card ${shift.shiftStatus == 'EXPIRED' ? 'card-danger' :
                                                               shift.shiftStatus == 'NOT_YET' ? 'card-secondary' :
                                                               shift.shiftStatus == 'COMPLETED' ? 'card-success' : 'card-warning'}">
                                                <div class="card-header">
                                                    <h3 class="card-title">
                                                        <i class="fas fa-hourglass-start"></i> Trạng thái
                                                    </h3>
                                                </div>
                                                <div class="card-body text-center">

                                                    <%-- ===== TRẠNG THÁI CHƯA ĐẾN GIỜ ===== --%>
                                                    <c:if test="${shift.shiftStatus == 'NOT_YET'}">
                                                        <div class="py-3">
                                                            <i class="fas fa-clock fa-2x text-secondary mb-2"></i>
                                                            <p class="text-secondary font-weight-bold mb-1">Chưa đến giờ chấm công</p>
                                                            <small class="text-muted">Ca bắt đầu lúc <strong>${shift.startTime}</strong></small>
                                                        </div>
                                                    </c:if>

                                                    <%-- ===== TRẠNG THÁI QUÁ HẠN ===== --%>
                                                    <c:if test="${shift.shiftStatus == 'EXPIRED' && empty shift.checkIn}">
                                                        <div class="py-3">
                                                            <i class="fas fa-times-circle fa-2x text-danger mb-2"></i>
                                                            <p class="text-danger font-weight-bold mb-1">Quá hạn chấm công</p>
                                                            <small class="text-muted">Ca đã kết thúc lúc <strong>${shift.endTime}</strong></small>
                                                        </div>
                                                    </c:if>

                                                    <%-- ===== ĐÃ CHECK-IN ===== --%>
                                                    <c:if test="${not empty shift.checkIn}">
                                                        <div class="mb-2">
                                                            <span class="badge badge-success p-2">
                                                                <i class="fas fa-check"></i> Đã check-in
                                                            </span>
                                                            <p class="mt-1 mb-0">
                                                                <strong>Giờ check-in:</strong>
                                                                <span class="text-success">${shift.checkIn}</span>
                                                            </p>
                                                        </div>
                                                    </c:if>

                                                    <%-- ===== ĐÃ CHECK-OUT ===== --%>
                                                    <c:if test="${not empty shift.checkOut}">
                                                        <div class="mb-2">
                                                            <span class="badge badge-info p-2">
                                                                <i class="fas fa-sign-out-alt"></i> Đã check-out
                                                            </span>
                                                            <p class="mt-1 mb-0">
                                                                <strong>Giờ check-out:</strong>
                                                                <span class="text-info">${shift.checkOut}</span>
                                                            </p>
                                                        </div>
                                                    </c:if>

                                                    <%-- ===== NÚT CHECK-IN (chỉ ACTIVE & chưa check-in) ===== --%>
                                                    <c:if test="${shift.shiftStatus == 'ACTIVE' && empty shift.checkIn}">
                                                        <form method="post" class="mb-2">
                                                            <input type="hidden" name="action" value="checkin">
                                                            <input type="hidden" name="assignmentId" value="${shift.attendanceId}">
                                                            <button type="submit" class="btn btn-success btn-lg">
                                                                <i class="fas fa-sign-in-alt"></i> Check-in
                                                            </button>
                                                        </form>
                                                    </c:if>

                                                    <%-- ===== NÚT CHECK-OUT (đã check-in, chưa check-out) ===== --%>
                                                    <c:if test="${not empty shift.checkIn && empty shift.checkOut}">
                                                        <form method="post" class="mb-2">
                                                            <input type="hidden" name="action" value="checkout">
                                                            <input type="hidden" name="assignmentId" value="${shift.attendanceId}">
                                                            <button type="submit" class="btn btn-danger btn-lg">
                                                                <i class="fas fa-sign-out-alt"></i> Check-out
                                                            </button>
                                                        </form>
                                                    </c:if>

                                                    <%-- ===== HOÀN TẤT ===== --%>
                                                    <c:if test="${shift.shiftStatus == 'COMPLETED'}">
                                                        <p class="text-success mt-2 mb-0">
                                                            <i class="fas fa-check-double"></i>
                                                            <strong>Hoàn tất ca làm việc!</strong>
                                                        </p>
                                                    </c:if>

                                                </div>
                                            </div>
                                        </div>

                                    </div>
                                </c:forEach>
                            </c:when>

                            <c:otherwise>
                                <!-- Nếu không có ca làm việc -->
                                <div class="card card-secondary">
                                    <div class="card-body text-center py-5">
                                        <i class="fas fa-inbox fa-3x text-muted mb-3 d-block"></i>
                                        <h4>Không có ca làm việc hôm nay</h4>
                                        <p class="text-muted">Vui lòng liên hệ quản lý để được xếp lịch.</p>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </section>
            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>

        
    </body>
</html>
