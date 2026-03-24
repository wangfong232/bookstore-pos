<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Cài Đặt Cấp Bậc Khách Hàng</title>

                <!-- Google Font: Source Sans Pro -->
                <link rel="stylesheet"
                    href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
                <!-- Font Awesome -->
                <link rel="stylesheet"
                    href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
                <!-- Theme style -->
                <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">
            </head>

            <body class="hold-transition sidebar-mini">
                <div class="wrapper">

                    <!-- Navbar -->
                    <jsp:include page="include/admin-header.jsp" />

                    <!-- Sidebar -->
                    <jsp:include page="include/admin-sidebar.jsp" />

                    <!-- Content Wrapper -->
                    <div class="content-wrapper">
                        <!-- Content Header -->
                        <section class="content-header">
                            <div class="container-fluid">
                                <div class="row mb-2">
                                    <div class="col-sm-6">
                                        <h1><i class="fas fa-trophy"></i> CẤP BẬC KHÁCH HÀNG</h1>
                                    </div>
                                </div>
                            </div>
                        </section>

                        <!-- Main content -->
                        <section class="content">
                            <div class="container-fluid">
                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible">
                                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                                        <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
                                    </div>
                                </c:if>
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible">
                                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                                        <i class="fas fa-check-circle"></i> ${successMessage}
                                    </div>
                                </c:if>

                                <!-- Tier Classification Rules Card -->
                                <div class="card">
                                    <div class="card-header bg-light">
                                        <h3 class="card-title text-bold text-uppercase" style="margin-top: 5px;">Quy Tắc
                                            Phân Loại Cấp Bậc</h3>
                                        <div class="card-tools">
                                            <a href="<%= request.getContextPath() %>/customer-tiers"
                                                class="btn btn-success btn-sm">
                                                <i class="fas fa-plus"></i> Thêm Cấp Bậc
                                            </a>
                                        </div>
                                    </div>

                                    <div class="card-body p-0">
                                        <table class="table table-bordered table-hover">
                                            <thead class="bg-light">
                                                <tr>
                                                    <th>Tên Cấp Bậc</th>
                                                    <th>Điểm Tối Thiểu</th>
                                                    <th>Giảm Giá %</th>
                                                    <th>Thao Tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:choose>
                                                    <c:when test="${empty tiers}">
                                                        <tr>
                                                            <td colspan="5" class="text-center text-muted py-4">
                                                                Chưa có cấp bậc nào. Hãy thêm cấp bậc mới.
                                                            </td>
                                                        </tr>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <c:forEach items="${tiers}" var="tier">
                                                            <tr
                                                                class="${tier.tierID == selectedTier.tierID ? 'table-active' : ''}">
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${tier.tierName eq 'Bronze'}">
                                                                            <span class="badge"
                                                                                style="background-color: #D35400; color: white;">${tier.tierName}</span>
                                                                        </c:when>
                                                                        <c:when test="${tier.tierName eq 'Silver'}">
                                                                            <span class="badge"
                                                                                style="background-color: #A6ACAF; color: white;">${tier.tierName}</span>
                                                                        </c:when>
                                                                        <c:when test="${tier.tierName eq 'Gold'}">
                                                                            <span class="badge"
                                                                                style="background-color: #F1C40F; color: black;">${tier.tierName}</span>
                                                                        </c:when>
                                                                        <c:when test="${tier.tierName eq 'Diamond'}">
                                                                            <span class="badge"
                                                                                style="background-color: #3498DB; color: white;">${tier.tierName}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span
                                                                                class="badge bg-info">${tier.tierName}</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <fmt:formatNumber value="${tier.minPoint}"
                                                                        type="number" groupingUsed="true" />
                                                                </td>
                                                                <td>${tier.discountRate}%</td>
                                                                <td>
                                                                    <a
                                                                        href="<%= request.getContextPath() %>/customer-tiers?action=edit&id=${tier.tierID}">Sửa</a>
                                                                    <span class="text-muted mx-1">|</span>
                                                                    <a href="<%= request.getContextPath() %>/customer-tiers?action=delete&id=${tier.tierID}"
                                                                        class="text-danger"
                                                                        onclick="return confirm('Bạn có chắc muốn xoá cấp bậc này?');">Xoá</a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                <!-- Selected Tier Details Section -->
                                <div class="card card-light" id="tierDetails">
                                    <div class="card-header border-0">
                                        <h3 class="card-title text-bold text-uppercase">
                                            <c:choose>
                                                <c:when test="${not empty selectedTier}">
                                                    Chỉnh Sửa Hạng: <span class="text-warning text-bold"
                                                        style="color: #F1C40F !important;">${selectedTier.tierName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    Thêm Hạng Mới
                                                </c:otherwise>
                                            </c:choose>
                                        </h3>
                                    </div>
                                    <form action="<%= request.getContextPath() %>/customer-tiers" method="post">
                                        <input type="hidden" name="action"
                                            value="${not empty selectedTier ? 'update' : 'add'}">
                                        <c:if test="${not empty selectedTier}">
                                            <input type="hidden" name="tierID" value="${selectedTier.tierID}">
                                        </c:if>

                                        <div class="card-body">
                                            <!-- Tier Name -->
                                            <div class="form-group row">
                                                <label for="tierName" class="col-sm-2 col-form-label">Tên Bậc:</label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" id="tierName"
                                                        name="tierName" value="${selectedTier.tierName}" required>
                                                </div>
                                            </div>

                                            <!-- Requirement -->
                                            <div class="form-group row">
                                                <label for="spendingRequirement" class="col-sm-2 col-form-label">Điều
                                                    Kiện:</label>
                                                <div class="col-sm-10 d-flex align-items-center">
                                                    <span class="mr-2">Tối thiểu</span>
                                                    <c:if test="${not empty selectedTier}">
                                                        <fmt:formatNumber value="${selectedTier.minPoint}" type="number"
                                                            groupingUsed="false" var="formattedMinPoint" />
                                                    </c:if>
                                                    <input type="text" class="form-control" id="spendingRequirement"
                                                        name="minPoint" value="${formattedMinPoint}"
                                                        style="width: 150px;" required>
                                                    <span class="ml-2">điểm</span>
                                                    <small id="minPointError" class="text-danger ml-3"
                                                        style="display: none;"></small>
                                                </div>
                                            </div>


                                            <!-- Discount Rate -->
                                            <div class="form-group row">
                                                <label for="discountRate" class="col-sm-2 col-form-label">Tỷ Lệ Giảm
                                                    Giá:</label>
                                                <div class="col-sm-10">
                                                    <div class="input-group" style="width: 150px;">
                                                        <input type="text" class="form-control" id="discountRate"
                                                            name="discountRate"
                                                            value="${selectedTier.discountRate != null ? selectedTier.discountRate : 0}"
                                                            required>
                                                        <div class="input-group-append">
                                                            <span class="input-group-text">%</span>
                                                        </div>
                                                    </div>
                                                    <small id="discountRateError" class="text-danger mt-1"
                                                        style="display: none;"></small>
                                                </div>
                                            </div>


                                        </div>

                                        <div class="card-footer bg-transparent border-top-0 d-flex justify-content-end">
                                            <a href="<%= request.getContextPath() %>/customer-tiers"
                                                class="btn btn-secondary mr-2"
                                                style="background-color: #6c757d; border-color: #6c757d; color: white;">Hủy</a>
                                            <button type="submit" class="btn btn-primary"
                                                style="background-color: #2b6bd8; border-color: #2b6bd8;">LƯU THAY
                                                ĐỔI</button>
                                        </div>
                                    </form>
                                </div>

                            </div>
                        </section>
                    </div>

                    <!-- Footer -->
                    <jsp:include page="include/admin-footer.jsp" />

                </div>

                <!-- jQuery -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
                <!-- Bootstrap 4 -->
                <script
                    src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
                <!-- AdminLTE App -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>
                <script>
                    $(document).ready(function () {
                        const minPointInput = $('#spendingRequirement');
                        const discRateInput = $('#discountRate');
                        const minPointError = $('#minPointError');
                        const discRateError = $('#discountRateError');

                        function validateNumeric(input, errorElement, fieldName) {
                            const val = input.val();
                            // Regex check for numeric (integer or decimal)
                            if (val && !/^\d*\.?\d*$/.test(val)) {
                                errorElement.text('Vui lòng chỉ nhập số cho ' + fieldName + '.').show();
                                // Optional: Remove non-numeric characters automatically
                                input.val(val.replace(/[^\d.]/g, ''));
                            } else {
                                errorElement.hide();
                            }
                        }

                        minPointInput.on('input', function () {
                            validateNumeric($(this), minPointError, 'điểm tối thiểu');
                        });

                        discRateInput.on('input', function () {
                            validateNumeric($(this), discRateError, 'tỷ lệ giảm giá');
                        });

                        $('form[action*="customer-tiers"]').on('submit', function (e) {
                            const tierName = $('#tierName').val().trim();
                            if (tierName === '') {
                                alert('Tên bậc không được để trống.');
                                e.preventDefault();
                                return;
                            }

                            const minValStr = minPointInput.val().replace(/,/g, '');
                            const minVal = parseFloat(minValStr);
                            if (isNaN(minVal) || minVal < 0 || !/^\d+\.?\d*$/.test(minValStr)) {
                                alert('Điểm tối thiểu phải là số lớn hơn 0.');
                                e.preventDefault();
                                return;
                            }

                            const discValStr = discRateInput.val().trim();
                            const discVal = parseFloat(discValStr);
                            if (isNaN(discVal) || discVal < 0 || discVal > 100 || !/^\d+\.?\d*$/.test(discValStr)) {
                                alert('Tỷ lệ giảm giá phải là số từ 0 đến 100.');
                                e.preventDefault();
                                return;
                            }
                        });
                    });
                </script>
            </body>

            </html>