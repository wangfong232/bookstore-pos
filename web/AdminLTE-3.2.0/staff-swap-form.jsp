<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <title>Gửi Đơn Đổi Ca</title>

        <!-- Google Font -->
        <link rel="stylesheet"
              href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
        <style>
            #loadingIndicator { display: none; }
        </style>
    </head>

    <body class="hold-transition sidebar-mini">
        <div class="wrapper">

            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>

            <div class="content-wrapper">

                <section class="content-header">
                    <div class="container-fluid d-flex justify-content-between align-items-center">
                        <h1><i class="fas fa-exchange-alt"></i> Gửi Đơn Đổi Ca</h1>
                        <a href="${pageContext.request.contextPath}/staff/my-swaps"
                           class="btn btn-outline-secondary btn-sm">
                            <i class="fas fa-history"></i> Xem đơn của tôi
                        </a>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <!-- SUCCESS -->
                        <c:if test="${param.success == 'true'}">
                            <div class="alert alert-success alert-dismissible fade show">
                                <i class="fas fa-check-circle"></i>
                                Gửi đơn đổi ca thành công! Vui lòng chờ manager xét duyệt.
                                <button type="button" class="close" data-dismiss="alert">
                                    <span>&times;</span>
                                </button>
                            </div>
                        </c:if>

                        <c:if test="${param.error == 'true'}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <i class="fas fa-exclamation-circle"></i>
                                Có lỗi xảy ra. Vui lòng thử lại.
                                <button type="button" class="close" data-dismiss="alert">
                                    <span>&times;</span>
                                </button>
                            </div>
                        </c:if>

                        <!-- DUPLICATE ERROR -->
                        <c:if test="${param.error == 'duplicate'}">
                            <div class="alert alert-warning alert-dismissible fade show">
                                <i class="fas fa-exclamation-triangle"></i>
                                Đơn đổi ca với nội dung này đang chờ xử lý. Vui lòng không gửi trùng lặp.
                                <button type="button" class="close" data-dismiss="alert">
                                    <span>&times;</span>
                                </button>
                            </div>
                        </c:if>

                        <div class="card card-primary">
                            <div class="card-header">
                                <h3 class="card-title">Thông tin đổi ca</h3>
                            </div>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/staff/swap"
                                  id="swapForm">

                                <div class="card-body">

                                    <!-- CHỌN NGÀY -->
                                    <div class="form-group">
                                        <label><i class="fas fa-calendar-day mr-1"></i>Chọn ngày làm việc</label>
                                        <input type="date"
                                               id="workDate"
                                               name="workDate"
                                               class="form-control"
                                               required>
                                        <small class="text-muted">Chọn ngày để lọc danh sách ca theo ngày đó</small>
                                        <span id="loadingIndicator" class="ml-2 text-primary">
                                            <i class="fas fa-spinner fa-spin"></i> Đang tải...
                                        </span>
                                    </div>

                                    <!-- CA CỦA MÌNH -->
                                    <div class="form-group">
                                        <label><i class="fas fa-user-clock mr-1"></i>Ca của bạn</label>
                                        <select id="fromAssignment"
                                                name="fromAssignmentID"
                                                class="form-control"
                                                required>
                                            <option value="">-- Chọn ngày để xem ca của bạn --</option>
                                            <c:forEach items="${myAssignments}" var="a">
                                                <option value="${a.assignmentID}"
                                                        data-employee="${a.employeeId}"
                                                        data-shiftname="${a.shiftName}"
                                                        data-workdate="${a.workDate}">
                                                    ${a.shiftName} — ${a.workDate}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- THÔNG TIN CA CỦA MÌNH (display only) -->
                                    <div class="form-row mb-3" id="fromInfoRow" style="display:none!important;">
                                        <div class="col-md-6">
                                            <label class="small text-muted">Ca</label>
                                            <input type="text" id="fromShiftName_display" class="form-control form-control-sm" disabled>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="small text-muted">Ngày</label>
                                            <input type="text" id="fromWorkDate_display" class="form-control form-control-sm" disabled>
                                        </div>
                                    </div>

                                    <!-- CA MUỐN ĐỔI -->
                                    <div class="form-group">
                                        <label><i class="fas fa-users mr-1"></i>Ca muốn đổi (người khác)</label>
                                        <select id="toAssignment"
                                                name="toAssignmentID"
                                                class="form-control"
                                                required>
                                            <option value="">-- Chọn ngày để xem ca người khác --</option>
                                            <c:forEach items="${otherAssignments}" var="a">
                                                <option value="${a.assignmentID}"
                                                        data-employee="${a.employeeId}"
                                                        data-shiftname="${a.shiftName}"
                                                        data-workdate="${a.workDate}"
                                                        data-fullname="${a.fullName}">
                                                    ${a.shiftName} — ${a.workDate} (${a.fullName})
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- THÔNG TIN CA NGƯỜI KIA (display only) -->
                                    <div class="form-row mb-3" id="toInfoRow" style="display:none!important;">
                                        <div class="col-md-4">
                                            <label class="small text-muted">Nhân viên</label>
                                            <input type="text" id="toFullName_display" class="form-control form-control-sm" disabled>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="small text-muted">Ca</label>
                                            <input type="text" id="toShiftName_display" class="form-control form-control-sm" disabled>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="small text-muted">Ngày</label>
                                            <input type="text" id="toWorkDate_display" class="form-control form-control-sm" disabled>
                                        </div>
                                    </div>

                                    <!-- LÝ DO -->
                                    <div class="form-group">
                                        <label><i class="fas fa-comment mr-1"></i>Lý do đổi ca</label>
                                        <textarea name="reason"
                                                  class="form-control"
                                                  rows="4"
                                                  placeholder="Nhập lý do bạn muốn đổi ca..."
                                                  required></textarea>
                                    </div>

                                </div>

                                <div class="card-footer">
                                    <button type="submit" class="btn btn-primary" id="submitBtn">
                                        <i class="fas fa-paper-plane"></i> Gửi đơn
                                    </button>
                                    <a href="${pageContext.request.contextPath}/staff/my-swaps"
                                       class="btn btn-secondary ml-2">
                                        <i class="fas fa-history"></i> Xem đơn của tôi
                                    </a>
                                </div>

                            </form>
                        </div>

                    </div>
                </section>

            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>

        <script>
            (function () {
                var contextPath = '${pageContext.request.contextPath}';
                var dateInput = document.getElementById('workDate');
                var fromSel = document.getElementById('fromAssignment');
                var toSel = document.getElementById('toAssignment');
                var fromInfoRow = document.getElementById('fromInfoRow');
                var toInfoRow = document.getElementById('toInfoRow');
                var loading = document.getElementById('loadingIndicator');

                // Update displayed info when dropdown changes
                fromSel.addEventListener('change', function () {
                    var opt = fromSel.options[fromSel.selectedIndex];
                    if (opt && opt.value) {
                        document.getElementById('fromShiftName_display').value = opt.getAttribute('data-shiftname') || '';
                        document.getElementById('fromWorkDate_display').value = opt.getAttribute('data-workdate') || '';
                        fromInfoRow.style.setProperty('display', 'flex', 'important');
                    } else {
                        fromInfoRow.style.setProperty('display', 'none', 'important');
                    }
                });

                toSel.addEventListener('change', function () {
                    var opt = toSel.options[toSel.selectedIndex];
                    if (opt && opt.value) {
                        document.getElementById('toFullName_display').value = opt.getAttribute('data-fullname') || '';
                        document.getElementById('toShiftName_display').value = opt.getAttribute('data-shiftname') || '';
                        document.getElementById('toWorkDate_display').value = opt.getAttribute('data-workdate') || '';
                        toInfoRow.style.setProperty('display', 'flex', 'important');
                    } else {
                        toInfoRow.style.setProperty('display', 'none', 'important');
                    }
                });

                // AJAX when date changes
                dateInput.addEventListener('change', function () {
                    var date = dateInput.value;
                    if (!date) return;

                    loading.style.display = 'inline';

                    fetch(contextPath + '/staff/swap?ajax=true&workDate=' + date)
                        .then(function (res) { return res.json(); })
                        .then(function (data) {
                            // Rebuild fromAssignment dropdown
                            fromSel.innerHTML = '<option value="">-- Chọn ca của bạn --</option>';
                            data.my.forEach(function (a) {
                                var opt = new Option(
                                    a.shiftName + ' \u2014 ' + a.workDate,
                                    a.id
                                );
                                opt.setAttribute('data-employee', a.employeeId);
                                opt.setAttribute('data-shiftname', a.shiftName);
                                opt.setAttribute('data-workdate', a.workDate);
                                fromSel.appendChild(opt);
                            });
                            if (data.my.length === 0) {
                                fromSel.innerHTML = '<option value="">Bạn không có ca nào trong ngày này</option>';
                            }

                            // Rebuild toAssignment dropdown
                            toSel.innerHTML = '<option value="">-- Chọn ca muốn đổi --</option>';
                            data.other.forEach(function (a) {
                                var opt = new Option(
                                    a.shiftName + ' \u2014 ' + a.workDate + ' (' + (a.fullName || 'NV#' + a.employeeId) + ')',
                                    a.id
                                );
                                opt.setAttribute('data-employee', a.employeeId);
                                opt.setAttribute('data-shiftname', a.shiftName);
                                opt.setAttribute('data-workdate', a.workDate);
                                opt.setAttribute('data-fullname', a.fullName || '');
                                toSel.appendChild(opt);
                            });
                            if (data.other.length === 0) {
                                toSel.innerHTML = '<option value="">Không có nhân viên khác làm ngày này</option>';
                            }

                            // Reset info rows
                            fromInfoRow.style.setProperty('display', 'none', 'important');
                            toInfoRow.style.setProperty('display', 'none', 'important');
                        })
                        .catch(function (err) {
                            console.error('AJAX error:', err);
                            alert('Không thể tải dữ liệu ca. Vui lòng thử lại.');
                        })
                        .finally(function () {
                            loading.style.display = 'none';
                        });
                });

                // Trigger initial display if JSTL pre-populated dropdowns
                document.addEventListener('DOMContentLoaded', function () {
                    if (fromSel.options.length > 1) fromSel.dispatchEvent(new Event('change'));
                    if (toSel.options.length > 1) toSel.dispatchEvent(new Event('change'));
                });

            })();
        </script>
    </body>
</html>