<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <style>
            .calendar-grid {
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                border-top: 1px solid #dee2e6;
                border-left: 1px solid #dee2e6;
            }

            .calendar-header {
                background: #f8f9fa;
                text-align: center;
                font-weight: bold;
                padding: 10px;
                border-right: 1px solid #dee2e6;
                border-bottom: 1px solid #dee2e6;
            }

            .calendar-cell {
                min-height: 120px;
                padding: 5px;
                border-right: 1px solid #dee2e6;
                border-bottom: 1px solid #dee2e6;
                position: relative;
            }

            .calendar-date {
                font-weight: bold;
                margin-bottom: 5px;
            }

            .calendar-event {
                background: #ffeeba;
                padding: 4px 6px;
                border-radius: 6px;
                font-size: 16px;
                margin-bottom: 4px;
            }

            .calendar-cell.empty {
                background: #f8f9fa;
            }
        </style>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Quản lí ca làm việc</title>

        <!-- Google Font -->
        <link rel="stylesheet"
              href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">

        <!-- FontAwesome -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">

        <!-- AdminLTE -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>

    <body class="hold-transition sidebar-mini">
        <div class="wrapper">

            <!-- HEADER -->
            <jsp:include page="include/admin-header.jsp"/>

            <!-- SIDEBAR -->
            <jsp:include page="include/admin-sidebar.jsp"/>

            <!-- Content Wrapper -->
            <div class="content-wrapper">

                <!-- Content Header -->
                <section class="content-header">
                    <div class="container-fluid">
                        <h1><i class="fas fa-calendar-alt"></i> Quản lí ca làm việc</h1>
                    </div>
                </section>

                <!-- Main content -->
                <section class="content">
                    <div class="container-fluid">

                        <!-- View-only alert for Staff & Saler -->
                        <c:if test="${requestScope.isViewOnly}">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            
                        </c:if>

                        <!-- Assign Card - Only show for Manager & Store Manager -->
                        <c:if test="${not requestScope.isViewOnly}">
                            <div class="card card-primary">
                                <div class="card-header">
                                    <h3 class="card-title">Phân công nhân viên vào ca làm việc</h3>
                                </div>

                                <form method="post" action="shift-management">
                                    <div class="card-body row">

                                        <div class="form-group col-md-3">
                                            <div class="form-group row-md-3">
                                                <label>Loại thời gian</label>
                                                <select id="dateType" class="form-control">
                                                    <option value="day">Theo ngày</option>
                                                    <option value="week">Theo tuần</option>
                                                    <option value="month">Theo tháng</option>
                                                </select>
                                            </div>

                                            <div class="form-group row-md-3">
                                                <label>Chọn thời gian</label>

                                                <!-- Theo ngày -->
                                                <input type="date" name="workDate"
                                                       id="dateInput"
                                                       class="form-control">

                                                <!-- Theo tuần -->
                                                <input type="week"
                                                       name="weekInput"
                                                       id="weekInput"
                                                       class="form-control d-none">

                                                <!-- Theo tháng -->
                                                <input type="month"
                                                       name="monthInput"
                                                       id="monthInput"
                                                       class="form-control d-none">
                                            </div>
                                        </div>

                                        <div class="form-group col-md-3">
                                            <label>Ca</label>
                                            <select name="shiftID" class="form-control">
                                                <c:forEach var="s" items="${shifts}">
                                                    <option value="${s.shiftID}">
                                                        ${s.shiftName}
                                                        (${s.startTime} - ${s.endTime})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="form-group col-md-4">
                                            <label>Nhân viên</label>

                                            <button type="button"
                                                    class="btn btn-outline-primary btn-block"
                                                    data-toggle="modal"
                                                    data-target="#employeeModal">
                                                Chọn nhân viên
                                            </button>

                                            <!-- Hidden input để submit -->
                                            <input type="hidden" name="employeeIDs" id="selectedEmployeeIDs" required>

                                            <!-- Hiển thị tên đã chọn -->
                                            <div id="selectedEmployeeName"
                                                 class="mt-2 text-muted small">
                                            </div>
                                        </div>

                                        <div class="form-group col-md-2">
                                            <label style="visibility:hidden;">Action</label>
                                            <button class="btn btn-primary btn-block">
                                                Phân công
                                            </button>
                                        </div>

                                    </div>
                                </form>
                            </div>
                        </c:if>

                        <!-- DAILY SHIFT OVERVIEW -->
                        <div class="card card-info">

                            <!-- HEADER + DATE FILTER -->
                            <div class="card-header">

                                <div class="row align-items-center">

                                    <!-- LEFT TITLE -->
                                    <div class="col-md-5">
                                        <h3 class="card-title mb-0">
                                            Tổng quan về ca làm việc hàng ngày
                                        </h3>
                                    </div>

                                    <!-- RIGHT ACTIONS (dịch vào giữa) -->
                                    <div class="col-md-7">

                                        <div class="d-flex align-items-center">

                                            <!-- DATE FILTER -->
                                            <form method="get"
                                                  action="shift-management"
                                                  class="form-inline mb-0">

                                                <input type="date"
                                                       style="font-size:16px; padding: 3px 10px"
                                                       name="viewDate"
                                                       value="${viewDate}"
                                                       class="form-control form-control-sm mr-2"/>

                                                <button class="btn btn-sm btn-primary mr-2" style="font-size:16px; padding: 3px 10px">
                                                    Xem
                                                </button>

                                                <!-- TOGGLE CALENDAR -->
                                                <c:choose>
                                                    <c:when test="${viewMode == 'calendar'}">
                                                        <a href="shift-management"
                                                           style="font-size:16px; padding: 3px 10px"
                                                           class="btn btn-sm btn-danger mr-2">
                                                            <i class="fas fa-times"></i> Ẩn lịch
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="shift-management?view=calendar"
                                                           style="font-size:16px; padding: 3px 10px"
                                                           class="btn btn-sm btn-success mr-2">
                                                            <i class="fas fa-calendar-alt"></i> Xem lịch
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>

                                            </form>

                                            <!-- DROPDOWN -->
                                            <c:if test="${not requestScope.isViewOnly}">
                                                <div class="btn-group ml-2">
                                                    <button type="button"
                                                            class="btn btn-sm btn-warning dropdown-toggle"
                                                            data-toggle="dropdown"
                                                            style="font-size:16px; padding: 3px 10px">
                                                        <i class="fas fa-file-alt mr-1"></i>
                                                        Xem đơn
                                                    </button>

                                                    <div class="dropdown-menu bg-white">
                                                        <a class="dropdown-item" href="swap-approval">
                                                            <i class="fas fa-exchange-alt mr-2 text-primary"></i>
                                                            Xem đơn đổi ca
                                                        </a>
                                                        <a class="dropdown-item" href="ot-approval">
                                                            <i class="fas fa-clock mr-2 text-success"></i>
                                                            Xem đơn OT
                                                        </a>
                                                    </div>

                                                </div>
                                            </c:if>
                                        </div>
                                    </div>

                                </div>

                            </div><!-- HEADER + DATE FILTER -->

                            <div class="card-body">
                                <div class="row">

                                    <c:forEach var="shift" items="${shifts}">
                                        <div class="col-md-4 mb-4">
                                            <div class="card shadow-sm h-100">

                                                <!-- SHIFT HEADER -->
                                                <div class="card-header bg-primary text-white">
                                                    <b>${shift.shiftName}</b>
                                                    <span class="float-right">
                                                        ${shift.startTime} - ${shift.endTime}
                                                    </span>
                                                </div>

                                                <div class="card-body">

                                                    <c:set var="hasEmployee" value="false"/>

                                                    <!-- LOOP EMPLOYEE IN THIS SHIFT -->
                                                    <c:forEach var="a" items="${assignments}">
                                                        <c:if test="${a.shiftID == shift.shiftID}">
                                                            <c:set var="hasEmployee" value="true"/>

                                                            <div class="border rounded p-3 mb-3 bg-light">

                                                                <div class="d-flex justify-content-between">
                                                                    <div>
                                                                        <strong>${a.fullName}</strong>
                                                                        <div class="text-muted small">
                                                                            ${a.role}
                                                                        </div>
                                                                    </div>

                                                                    <div>
                                                                        <c:choose>
                                                                            <c:when test="${a.status == 'SWAP'}">
                                                                                <span class="badge badge-warning">
                                                                                    Đổi ca
                                                                                </span>
                                                                            </c:when>
                                                                            <c:when test="${a.status == 'LEAVE'}">
                                                                                <span class="badge badge-danger">
                                                                                    Nghỉ
                                                                                </span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="badge badge-success">
                                                                                    Đã phân công
                                                                                </span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>

                                                                <hr class="my-2">

                                                                <div class="small text-muted">
                                                                    Date: ${a.workDate}
                                                                </div>

                                                            </div>

                                                        </c:if>
                                                    </c:forEach>

                                                    <!-- IF NO EMPLOYEE -->
                                                    <c:if test="${!hasEmployee}">
                                                        <div class="text-center text-muted pt-4">
                                                            <i class="fas fa-user-slash fa-2x mb-2"></i>
                                                            <div>Chưa có nhân viên nào được phân công</div>
                                                        </div>
                                                    </c:if>

                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>

                                </div>
                            </div>
                        </div> <!-- DAILY SHIFT OVERVIEW -->


                        <!-- View calendar -->
                        <c:if test="${viewMode == 'calendar'}">

                            <div class="card">

                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h3 class="card-title">
                                        Tháng ${month}/${year}
                                    </h3>

                                    <form method="get" action="shift-management" class="form-inline">
                                        <input type="hidden" name="view" value="calendar"/>

                                        <input type="month"
                                               name="monthPicker"
                                               value="${year}-${month < 10 ? '0' : ''}${month}"
                                               class="form-control mr-2"/>

                                        <button class="btn btn-primary btn-sm">Xem</button>
                                    </form>
                                </div>

                                <div class="card-body p-0">

                                    <!-- Calendar Grid -->
                                    <div class="calendar-grid">

                                        <!-- HEADER -->
                                        <div class="calendar-header">Mon</div>
                                        <div class="calendar-header">Tue</div>
                                        <div class="calendar-header">Wed</div>
                                        <div class="calendar-header">Thu</div>
                                        <div class="calendar-header">Fri</div>
                                        <div class="calendar-header">Sat</div>
                                        <div class="calendar-header">Sun</div>

                                        <!-- EMPTY CELLS BEFORE FIRST DAY -->
                                        <c:forEach begin="1" end="${firstDayOfWeek - 1}">
                                            <div class="calendar-cell empty"></div>
                                        </c:forEach>

                                        <!-- DAYS -->
                                        <c:forEach begin="1" end="${daysInMonth}" var="day">

                                            <div class="calendar-cell">

                                                <div class="calendar-date">
                                                    ${day}
                                                </div>

                                                <!-- SHIFT INSIDE DAY -->
                                                <c:forEach var="a" items="${calendarAssignments}">
                                                    <c:if test="${a.workDate.toLocalDate().getDayOfMonth() == day}">
                                                        <div class="calendar-event">
                                                            ${a.shiftName} <br/>
                                                            <small>${a.fullName}</small>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>

                                            </div>

                                        </c:forEach>

                                    </div>
                                </div>

                            </div>

                        </c:if>



                    </div> <!-- container-fluid -->
                    <!-- EMPLOYEE SELECT MODAL -->
                    <div class="modal fade" id="employeeModal">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">

                                <div class="modal-header bg-primary text-white">
                                    <h5 class="modal-title">Chọn nhân viên</h5>
                                    <button type="button" class="close text-white"
                                            data-dismiss="modal">&times;</button>
                                </div>

                                <div class="modal-body">

                                    <!-- Search box -->
                                    <input type="text"
                                           id="searchEmployee"
                                           class="form-control mb-3"
                                           placeholder="Tìm theo tên...">

                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th></th>
                                                <th>Họ tên</th>
                                                <th>Vai trò</th>
                                                <th>Trạng thái</th>
                                            </tr>
                                        </thead>

                                        <tbody id="employeeTable">
                                            <c:forEach var="e" items="${allEmployees}">
                                                <tr>
                                                    <td>
                                                        <input type="checkbox"
                                                               name="empCheckbox"
                                                               value="${e.employeeId}"
                                                               data-name="${e.fullName}">
                                                    </td>
                                                    <td>${e.fullName}</td>
                                                    <td>${e.role.roleName}</td>
                                                    <td>
                                                        <span class="badge ${e.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                                            ${e.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>

                                </div>

                                <div class="modal-footer">
                                    <button type="button"
                                            class="btn btn-primary"
                                            onclick="selectEmployee()">
                                        Xác nhận
                                    </button>
                                </div>

                            </div>
                        </div>
                    </div>
                </section>
            </div>

            <!-- FOOTER -->
            <jsp:include page="include/admin-footer.jsp"/>

        </div>


        <script>
            function selectEmployee() {

                let checked = document.querySelectorAll('input[name="empCheckbox"]:checked');

                if (checked.length === 0) {
                    alert("Vui lòng chọn ít nhất 1 nhân viên");
                    return;
                }

                let ids = [];
                let names = [];

                checked.forEach(function (cb) {
                    ids.push(cb.value);
                    names.push(cb.dataset.name);
                });

                // Gán vào hidden input (cách nhau bởi dấu phẩy)
                document.getElementById("selectedEmployeeIDs").value = ids.join(",");

                // Hiển thị tên
                document.getElementById("selectedEmployeeName").innerHTML =
                        "<b>Đã chọn:</b> " + names.join(", ");

                $('#employeeModal').modal('hide');
            }
        </script>


        <script>
            //js cho phần chọn ngày cho nhân viên làm
            document.getElementById("dateType").addEventListener("change", function () {

                document.getElementById("dateInput").classList.add("d-none");
                document.getElementById("weekInput").classList.add("d-none");
                document.getElementById("monthInput").classList.add("d-none");

                if (this.value === "day") {
                    document.getElementById("dateInput").classList.remove("d-none");
                } else if (this.value === "week") {
                    document.getElementById("weekInput").classList.remove("d-none");
                } else if (this.value === "month") {
                    document.getElementById("monthInput").classList.remove("d-none");
                }
            }
            );
        </script>
    </body>
</html>