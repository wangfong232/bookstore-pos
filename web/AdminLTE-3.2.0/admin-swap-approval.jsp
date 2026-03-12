<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Duyệt Đơn Đổi Ca</title>

        <!-- Google Font -->
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
                        <h1><i class="fas fa-exchange-alt"></i> Duyệt Đơn Đổi Ca</h1>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <div class="card card-outline card-primary">

                            <div class="card-header">
                                <h3 class="card-title">Danh sách yêu cầu</h3>
                            </div>

                            <div class="card-body table-responsive p-0">

                                <table class="table table-hover text-nowrap">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Nhân viên</th>
                                            <th>Ca làm</th>
                                            <th>Người muốn đổi</th>
                                            <th>Ca của người muốn đổi</th>
                                            <th>Lí do</th>
                                            <th>Trạng thái</th>
                                            <th>Hành động</th>
                                        </tr>
                                    </thead>

                                    <tbody>

                                        <c:forEach var="s" items="${requests}">
                                            <tr>
                                                <td>#${s.swapRequestID}</td>

                                                <td>${s.fullName}</td>
                                                <td>${s.shiftName} - ${s.workDate}</td>

                                                <td>${s.toFullName}</td>
                                                <td>${s.toShiftName} - ${s.toWorkDate}</td>

                                                <td>${s.reason}</td>

                                                <td>
                                                    <c:choose>
                                                        <c:when test="${s.status == 'PENDING'}">
                                                            <span class="badge badge-warning">PENDING</span>
                                                        </c:when>
                                                        <c:when test="${s.status == 'APPROVED'}">
                                                            <span class="badge badge-success">APPROVED</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-danger">REJECTED</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                                <td>

                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/admin/swap-approval"
                                                          style="display:inline">

                                                        <input type="hidden"
                                                               name="requestID"
                                                               value="${s.swapRequestID}"/>

                                                        <button name="action"
                                                                value="approve"
                                                                class="btn btn-sm btn-success"
                                                                onclick="return confirm('Chấp nhận?')">
                                                            <i class="fas fa-check"></i>
                                                        </button>

                                                        <button name="action"
                                                                value="reject"
                                                                class="btn btn-sm btn-danger"
                                                                onclick="return confirm('Từ chối?')">
                                                            <i class="fas fa-times"></i>
                                                        </button>

                                                    </form>

                                                </td>

                                            </tr>
                                        </c:forEach>

                                        <c:if test="${empty requests}">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted">
                                                    Không có yêu cầu nào.
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