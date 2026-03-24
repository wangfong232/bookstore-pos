<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Chỉnh Sửa Chiến Dịch Khuyến Mãi</title>

                <!-- Google Font: Source Sans Pro -->
                <link rel="stylesheet"
                    href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
                <!-- Font Awesome -->
                <link rel="stylesheet"
                    href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
                <!-- Theme style -->
                <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">
                <!-- Tempusdominus Bootstrap 4 (Date picker) -->
                <link rel="stylesheet"
                    href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css">

                <style>
                    /* ===== Modal-like card wrapper ===== */
                    .promo-form-wrapper {
                        max-width: 780px;
                        margin: 0 auto;
                    }

                    .promo-card {
                        border: 1px solid #ccc;
                        border-radius: 4px;
                        background: #fff;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
                    }

                    .promo-card-header {
                        background: #f2f2f2;
                        border-bottom: 1px solid #ccc;
                        padding: 12px 18px;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        border-radius: 4px 4px 0 0;
                    }

                    .promo-card-header h5 {
                        margin: 0;
                        font-size: 15px;
                        font-weight: 600;
                        color: #333;
                    }

                    .promo-card-header .close-btn {
                        background: #555;
                        color: #fff;
                        border: none;
                        border-radius: 50%;
                        width: 26px;
                        height: 26px;
                        font-size: 14px;
                        cursor: pointer;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        line-height: 1;
                        text-decoration: none;
                    }

                    .promo-card-header .close-btn:hover {
                        background: #333;
                        color: #fff;
                    }

                    .promo-card-body {
                        background: #e8e8e8;
                        padding: 24px 24px 12px 24px;
                    }

                    .promo-card-footer {
                        background: #e8e8e8;
                        border-top: 1px solid #ccc;
                        padding: 14px 24px;
                        display: flex;
                        justify-content: flex-end;
                        gap: 10px;
                        border-radius: 0 0 4px 4px;
                    }

                    /* ===== Inner white form area ===== */
                    .form-section-title {
                        font-size: 13px;
                        font-weight: 700;
                        color: #333;
                        margin-bottom: 8px;
                    }

                    .inner-form {
                        background: #e8e8e8;
                    }

                    .form-label-bold {
                        font-weight: 700;
                        font-size: 13px;
                        margin-bottom: 4px;
                        color: #222;
                        display: block;
                    }

                    /* ===== Section separators ===== */
                    .section-block {
                        margin-bottom: 16px;
                    }

                    /* ===== Condition row ===== */
                    .condition-row {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        margin-bottom: 8px;
                        flex-wrap: wrap;
                    }

                    .condition-row label.check-label {
                        font-size: 13px;
                        margin-bottom: 0;
                        font-weight: 500;
                        color: #333;
                        display: flex;
                        align-items: center;
                        gap: 5px;
                        cursor: pointer;
                        min-width: 120px;
                    }

                    .condition-row .field-label {
                        font-size: 13px;
                        font-weight: 700;
                        color: #333;
                        margin-bottom: 0;
                    }

                    /* ===== Rule setting row ===== */
                    .rule-row {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        flex-wrap: wrap;
                        margin-top: 6px;
                    }

                    .rule-row .rule-label {
                        font-size: 13px;
                        font-weight: 600;
                        color: #333;
                    }

                    .rule-row .rule-num-input {
                        width: 70px;
                        text-align: center;
                    }

                    /* ===== Date picker row ===== */
                    .date-row {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        flex-wrap: wrap;
                        margin-top: 6px;
                    }

                    .date-row .date-label {
                        font-size: 13px;
                        font-weight: 600;
                        color: #333;
                    }

                    .date-row .input-group {
                        max-width: 200px;
                    }

                    /* ===== Buttons ===== */
                    .btn-cancel-promo {
                        background: #545b62;
                        color: #fff;
                        border: none;
                        padding: 8px 28px;
                        font-size: 13px;
                        font-weight: 600;
                        border-radius: 4px;
                    }

                    .btn-cancel-promo:hover {
                        background: #3d4349;
                        color: #fff;
                    }

                    .btn-save-promo {
                        background: #28a745;
                        color: #fff;
                        border: none;
                        padding: 8px 28px;
                        font-size: 13px;
                        font-weight: 700;
                        border-radius: 4px;
                        letter-spacing: 0.5px;
                    }

                    .btn-save-promo:hover {
                        background: #1e7e34;
                        color: #fff;
                    }

                    /* ===== Dynamic rule sections ===== */
                    .rule-section-dynamic {
                        display: none;
                    }

                    /* ===== Discount / Fixed amount input ===== */
                    .rule-value-input {
                        width: 100px;
                    }

                    /* ===== Select & input unified sizing ===== */
                    .promo-select {
                        height: 34px;
                        font-size: 13px;
                        padding: 4px 8px;
                    }

                    .promo-input {
                        height: 34px;
                        font-size: 13px;
                        padding: 4px 10px;
                    }
                </style>
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
                                        <h1>
                                            <i class="fas fa-bullhorn"></i>
                                            Chỉnh Sửa Chiến Dịch Khuyến Mãi
                                        </h1>
                                    </div>
                                    <div class="col-sm-6">
                                        <ol class="breadcrumb float-sm-right">
                                            <li class="breadcrumb-item"><a href="#">Home</a></li>
                                            <li class="breadcrumb-item">
                                                <a href="<%= request.getContextPath() %>/promotions">Chiến Dịch
                                                    Khuyến Mãi</a>
                                            </li>
                                            <li class="breadcrumb-item active">Chỉnh Sửa</li>
                                        </ol>
                                    </div>
                                </div>
                            </div>
                        </section>

                        <!-- Main content -->
                        <section class="content">
                            <div class="container-fluid">
                                <div class="promo-form-wrapper">

                                    <!-- Alert messages -->
                                    <c:if test="${not empty errorMessage}">
                                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                            <i class="fas fa-exclamation-circle mr-1"></i>
                                            ${errorMessage}
                                            <button type="button" class="close" data-dismiss="alert">
                                                <span>&times;</span>
                                            </button>
                                        </div>
                                    </c:if>

                                    <!-- Promotion Form Card (modal-like style) -->
                                    <div class="promo-card">

                                        <!-- Card Header -->
                                        <div class="promo-card-header">
                                            <h5>Chỉnh Sửa Chiến Dịch Khuyến Mãi</h5>
                                            <a href="<%= request.getContextPath() %>/promotions" class="close-btn"
                                                title="Close">
                                                <i class="fas fa-times" style="font-size:12px;"></i>
                                            </a>
                                        </div>

                                        <!-- Card Body -->
                                        <div class="promo-card-body">
                                            <form action="<%= request.getContextPath() %>/promotions" method="post"
                                                id="promotionForm" novalidate>

                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="promotionID"
                                                    value="${promotion.promotionID}">

                                                <!-- Section title -->
                                                <div class="section-block">

                                                    <!-- Promotion Code -->
                                                    <div class="section-block">
                                                        <label class="form-label-bold" for="promotionCode">Mã Chiến
                                                            Dịch:</label>
                                                        <input type="text"
                                                            class="form-control promo-input ${not empty error_promotionCode ? 'is-invalid' : ''}"
                                                            id="promotionCode" name="promotionCode"
                                                            value="${promotion.promotionCode}" required>
                                                        <c:if test="${not empty error_promotionCode}">
                                                            <div class="text-danger small mt-1">${error_promotionCode}
                                                            </div>
                                                        </c:if>
                                                        <div class="invalid-feedback">Vui lòng nhập mã chiến dịch.</div>
                                                    </div>

                                                    <!-- Promotion Name -->
                                                    <div class="section-block">
                                                        <label class="form-label-bold" for="promotionName">Tên Chiến
                                                            Dịch:</label>
                                                        <input type="text" class="form-control promo-input"
                                                            id="promotionName" name="promotionName"
                                                            value="${promotion.promotionName}" required>
                                                        <div class="invalid-feedback">Vui lòng nhập tên chiến dịch.
                                                        </div>
                                                    </div>

                                                    <!-- Promotion Type -->
                                                    <div class="section-block">
                                                        <label class="form-label-bold" for="promotionType">Loại Khuyến
                                                            Mãi:</label>
                                                        <select class="form-control promo-select" id="promotionType"
                                                            name="promotionType" onchange="onTypeChange(this.value)"
                                                            required>
                                                            <option value="PERCENT" ${promotion.promotionType
                                                                eq 'PERCENT' ? 'selected' : '' }>Giảm % Giá</option>
                                                            <option value="FIXED" ${promotion.promotionType eq 'FIXED'
                                                                ? 'selected' : '' }>Giảm Số Tiền Cố Định</option>
                                                        </select>
                                                    </div>

                                                    <!-- Status -->
                                                    <div class="section-block">
                                                        <label class="form-label-bold">Trạng thái:</label>
                                                        <div class="custom-control custom-switch mt-1">
                                                            <input type="checkbox" class="custom-control-input"
                                                                id="promoStatus" name="status" value="ACTIVE"
                                                                ${promotion.status eq 'ACTIVE' ? 'checked' : '' }>
                                                            <label class="custom-control-label font-weight-normal"
                                                                for="promoStatus">Kích hoạt chương trình</label>
                                                        </div>
                                                    </div>

                                                </div>

                                                <!-- CONDITION -->
                                                <div class="section-block">
                                                    <div class="form-section-title">ĐIỀU KIỆN:</div>

                                                    <!-- Apply for: Book Category -->
                                                    <div class="condition-row">
                                                        <label class="check-label" for="chkApplyCategory">
                                                            <input type="checkbox" id="chkApplyCategory"
                                                                name="applyCategory" value="true" ${not empty
                                                                promotion.applicableCategories ? 'checked' : '' }
                                                                onchange="toggleSelect('categorySelect', this.checked)">
                                                            Áp dụng cho:
                                                        </label>
                                                        <span class="field-label">Loại Sách:</span>
                                                        <select class="form-control promo-select" id="categorySelect"
                                                            name="bookCategory" style="width: 160px;" ${not empty
                                                            promotion.applicableCategories ? '' : 'disabled' }>
                                                            <option value="ALL">Tất Cả Loại Sách</option>
                                                            <c:set var="selectedCat"
                                                                value="${not empty promotion.applicableCategories ? promotion.applicableCategories[0].categoryID : ''}" />
                                                            <c:forEach items="${categories}" var="category">
                                                                <option value="${category.categoryID}" ${selectedCat eq
                                                                    category.categoryID ? 'selected' : '' }>
                                                                    ${category.categoryName}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>

                                                    <!-- Min Order Value -->
                                                    <div class="condition-row">
                                                        <label class="check-label">
                                                            <i class="fas fa-shopping-cart text-muted mr-1"></i> Đơn
                                                            hàng tối thiểu:
                                                        </label>
                                                        <div class="input-group" style="width: 160px;">
                                                            <input type="number" class="form-control promo-input"
                                                                id="minOrderValue" name="minOrderValue" placeholder="0"
                                                                value="${not empty promotion.conditions ? promotion.conditions[0].conditionValue : '0'}">
                                                            <div class="input-group-append">
                                                                <span class="input-group-text">đ</span>
                                                            </div>
                                                        </div>
                                                        <small id="minOrderValueError" class="text-danger mt-1"
                                                            style="display: none;"></small>
                                                    </div>

                                                    <!-- Customer Tier -->
                                                    <div class="condition-row">
                                                        <label class="check-label" for="chkCustomerTier">
                                                            <input type="checkbox" id="chkCustomerTier" name="applyTier"
                                                                value="true" ${not empty
                                                                promotion.applicableCustomerTiers ? 'checked' : '' }
                                                                onchange="toggleSelect('tierSelect', this.checked)">
                                                            Bậc Khách Hàng:
                                                        </label>
                                                        <select class="form-control promo-select" id="tierSelect"
                                                            name="customerTier" style="width: 140px;" ${not empty
                                                            promotion.applicableCustomerTiers ? '' : 'disabled' }>
                                                            <option value="">Chọn Bậc</option>
                                                            <c:set var="selectedTier"
                                                                value="${not empty promotion.applicableCustomerTiers ? promotion.applicableCustomerTiers[0].tierID : ''}" />
                                                            <c:forEach items="${customerTiers}" var="tier">
                                                                <option value="${tier.tierID}" ${selectedTier eq
                                                                    tier.tierID ? 'selected' : '' }>
                                                                    ${tier.tierName}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>

                                                <!-- RULE SETTING -->
                                                <div class="section-block">
                                                    <div class="form-section-title">QUY TẮC ÁP DỤNG:</div>

                                                    <!-- % Discount -->
                                                    <div class="rule-row rule-section-dynamic" id="rule-PERCENT">
                                                        <span class="rule-label">Giảm giá:</span>
                                                        <div class="input-group" style="width:160px;">
                                                            <input type="number"
                                                                class="form-control promo-input ${not empty error_discountPercent ? 'is-invalid' : ''}"
                                                                id="discountPercent" name="discountPercent" min="0"
                                                                max="100"
                                                                value="${(promotion.discount.discountType eq 'PERCENT' || promotion.discount.discountType eq 'PERCENTAGE') ? promotion.discount.discountValue : ''}"
                                                                placeholder="VD: 20">
                                                            <div class="input-group-append">
                                                                <span class="input-group-text">%</span>
                                                            </div>
                                                        </div>
                                                        <small id="discountPercentError" class="text-danger mt-1"
                                                            style="display: none;"></small>
                                                        <c:if test="${not empty error_discountPercent}">
                                                            <div class="text-danger small mt-1">${error_discountPercent}
                                                            </div>
                                                        </c:if>
                                                        <span class="rule-label">giảm</span>
                                                    </div>

                                                    <!-- Fixed Amount -->
                                                    <div class="rule-row rule-section-dynamic" id="rule-FIXED">
                                                        <span class="rule-label">Số tiền giảm:</span>
                                                        <div class="input-group" style="width:180px;">
                                                            <input type="number"
                                                                class="form-control promo-input ${not empty error_fixedAmount ? 'is-invalid' : ''}"
                                                                id="fixedAmount" name="fixedAmount" min="0"
                                                                value="${(promotion.discount.discountType eq 'FIXED' || promotion.discount.discountType eq 'FIXED_AMOUNT') ? promotion.discount.discountValue : ''}"
                                                                placeholder="VD: 50000">
                                                            <div class="input-group-append">
                                                                <span class="input-group-text">VNĐ</span>
                                                            </div>
                                                        </div>
                                                        <small id="fixedAmountError" class="text-danger mt-1"
                                                            style="display: none;"></small>
                                                        <c:if test="${not empty error_fixedAmount}">
                                                            <div class="text-danger small mt-1">${error_fixedAmount}
                                                            </div>
                                                        </c:if>
                                                    </div>

                                                </div>

                                                <!-- TIME -->
                                                <div class="section-block">
                                                    <div class="form-section-title">THỜI GIAN:</div>
                                                    <div class="date-row">
                                                        <span class="date-label">Từ:</span>
                                                        <div class="input-group date" id="fromDatePicker"
                                                            data-target-input="nearest">
                                                            <input type="text"
                                                                class="form-control promo-input datetimepicker-input"
                                                                name="startDate" id="startDate"
                                                                data-target="#fromDatePicker" placeholder="mm/dd/yyyy"
                                                                value="${promotion.startDateForm}" required>
                                                            <div class="input-group-append"
                                                                data-target="#fromDatePicker"
                                                                data-toggle="datetimepicker">
                                                                <div class="input-group-text">
                                                                    <i class="far fa-calendar-alt"></i>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <span class="date-label">Đến:</span>
                                                        <div class="input-group date" id="toDatePicker"
                                                            data-target-input="nearest">
                                                            <input type="text"
                                                                class="form-control promo-input datetimepicker-input"
                                                                name="endDate" id="endDate" data-target="#toDatePicker"
                                                                placeholder="mm/dd/yyyy"
                                                                value="${promotion.endDateForm}" required>
                                                            <div class="input-group-append" data-target="#toDatePicker"
                                                                data-toggle="datetimepicker">
                                                                <div class="input-group-text">
                                                                    <i class="far fa-calendar-alt"></i>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </form>
                                        </div><!-- /.promo-card-body -->

                                        <!-- Card Footer (buttons) -->
                                        <div class="promo-card-footer">
                                            <a href="<%= request.getContextPath() %>/promotions"
                                                class="btn btn-cancel-promo">Hủy</a>
                                            <button type="submit" form="promotionForm" class="btn btn-save-promo"
                                                onclick="return validateForm()">CẬP NHẬT</button>
                                        </div>

                                    </div><!-- /.promo-card -->

                                </div><!-- /.promo-form-wrapper -->
                            </div>
                        </section>
                    </div><!-- /.content-wrapper -->

                    <!-- Footer -->
                    <jsp:include page="include/admin-footer.jsp" />
                </div><!-- /.wrapper -->

                <!-- jQuery -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
                <!-- Moment.js -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/moment/moment.min.js"></script>
                <!-- Bootstrap 4 -->
                <script
                    src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
                <!-- Tempusdominus Bootstrap 4 -->
                <script
                    src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/tempusdominus-bootstrap-4/js/tempusdominus-bootstrap-4.min.js"></script>
                <!-- AdminLTE App -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

                <script>
                    $(function () {
                        // Init date pickers
                        $('#fromDatePicker').datetimepicker({
                            format: 'MM/DD/YYYY',
                            icons: {
                                time: 'far fa-clock',
                                date: 'far fa-calendar-alt',
                                up: 'fas fa-arrow-up',
                                down: 'fas fa-arrow-down',
                                previous: 'fas fa-chevron-left',
                                next: 'fas fa-chevron-right',
                                today: 'fas fa-calendar-check',
                                clear: 'far fa-trash-alt',
                                close: 'fas fa-times'
                            }
                        });

                        $('#toDatePicker').datetimepicker({
                            format: 'MM/DD/YYYY',
                            useCurrent: false,
                            icons: {
                                time: 'far fa-clock',
                                date: 'far fa-calendar-alt',
                                up: 'fas fa-arrow-up',
                                down: 'fas fa-arrow-down',
                                previous: 'fas fa-chevron-left',
                                next: 'fas fa-chevron-right',
                                today: 'fas fa-calendar-check',
                                clear: 'far fa-trash-alt',
                                close: 'fas fa-times'
                            }
                        });

                        // Link from/to pickers
                        $('#fromDatePicker').on('change.datetimepicker', function (e) {
                            $('#toDatePicker').datetimepicker('minDate', e.date);
                        });
                        $('#toDatePicker').on('change.datetimepicker', function (e) {
                            $('#fromDatePicker').datetimepicker('maxDate', e.date);
                        });

                        // Init rule sections based on current type
                        var initialType = $('#promotionType').val();
                        showRuleSection(initialType);

                        // Real-time validation for numeric fields
                        function validateNumeric(input, errorElement, fieldName) {
                            const val = input.val();
                            if (val && !/^\d*\.?\d*$/.test(val)) {
                                errorElement.text('Vui lòng chỉ nhập số cho ' + fieldName + '.').show();
                                input.val(val.replace(/[^\d.]/g, ''));
                            } else {
                                errorElement.hide();
                            }
                        }

                        $('#minOrderValue').on('input', function () {
                            validateNumeric($(this), $('#minOrderValueError'), 'đơn hàng tối thiểu');
                        });
                        $('#discountPercent').on('input', function () {
                            validateNumeric($(this), $('#discountPercentError'), 'tỷ lệ giảm giá');
                        });
                        $('#fixedAmount').on('input', function () {
                            validateNumeric($(this), $('#fixedAmountError'), 'số tiền giảm');
                        });
                    });

                    /**
                     * Show correct rule section based on promotion type
                     */
                    function onTypeChange(type) {
                        showRuleSection(type);
                    }

                    function showRuleSection(type) {
                        // Hide all dynamic rule sections
                        $('.rule-section-dynamic').hide();
                        // Show the matching one
                        var $target = $('#rule-' + type);
                        if ($target.length) {
                            $target.show();
                        }
                    }

                    /**
                     * Enable/disable a select based on checkbox state
                     */
                    function toggleSelect(selectId, enabled) {
                        var $sel = $('#' + selectId);
                        if (enabled) {
                            $sel.prop('disabled', false);
                        } else {
                            $sel.prop('disabled', true);
                        }
                    }

                    /**
                     * Validate form before submit
                     */
                    function validateForm() {
                        var valid = true;

                        var code = $.trim($('#promotionCode').val());
                        if (!code) {
                            $('#promotionCode').addClass('is-invalid');
                            valid = false;
                        } else {
                            $('#promotionCode').removeClass('is-invalid');
                        }

                        var name = $.trim($('#promotionName').val());
                        if (!name) {
                            $('#promotionName').addClass('is-invalid');
                            valid = false;
                        } else {
                            $('#promotionName').removeClass('is-invalid');
                        }

                        var startDate = $.trim($('#startDate').val());
                        var endDate = $.trim($('#endDate').val());
                        if (!startDate) {
                            $('#startDate').addClass('is-invalid');
                            valid = false;
                        } else {
                            $('#startDate').removeClass('is-invalid');
                        }
                        if (!endDate) {
                            $('#endDate').addClass('is-invalid');
                            valid = false;
                        } else {
                            $('#endDate').removeClass('is-invalid');
                        }

                        if (!valid) {
                            alert("Vui lòng kiểm tra lại các thông tin nhập vào.");
                            return false;
                        }

                        // Check values
                        var type = $('#promotionType').val();
                        var minOrder = parseFloat($('#minOrderValue').val());
                        if (isNaN(minOrder) || minOrder < 0) {
                            alert("Đơn hàng tối thiểu phải là số lớn hơn 0.");
                            $('#minOrderValue').addClass('is-invalid');
                            return false;
                        } else {
                            $('#minOrderValue').removeClass('is-invalid');
                        }

                        if (type === 'PERCENT') {
                            var pct = parseFloat($('#discountPercent').val());
                            if (isNaN(pct) || pct < 0 || pct > 100) {
                                alert("Tỉ lệ giảm giá phải là số từ 0 đến 100.");
                                $('#discountPercent').addClass('is-invalid');
                                return false;
                            }
                        } else if (type === 'FIXED') {
                            var fixed = parseFloat($('#fixedAmount').val());
                            if (isNaN(fixed) || fixed < 0) {
                                alert("Số tiền giảm phải là số lớn hơn 0.");
                                $('#fixedAmount').addClass('is-invalid');
                                return false;
                            }
                        }

                        // Re-enable disabled selects before submit so values are sent
                        $('#categorySelect').prop('disabled', false);
                        $('#tierSelect').prop('disabled', false);
                        return true;
                    }
                </script>
            </body>

            </html>