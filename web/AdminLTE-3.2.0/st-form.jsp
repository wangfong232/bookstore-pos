<%-- 
    Document   : st-form
    Created on : Mar 7, 2026, 12:19:20 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo phiếu kiểm kê kho</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
        <style>
            #productTable {
                table-layout: fixed;
            }
            .step-indicator{
                display: flex;
                align-items: center;
                margin-bottom: 20px;
            }
            .step-box {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 36px;
                height: 36px;
                border-radius: 50%;
                font-weight: bold;
                font-size: 15px;
            }
            .step-box.active  {
                background: #007bff;
                color: #fff;
            }
            .step-box.done    {
                background: #28a745;
                color: #fff;
            }
            .step-box.pending {
                background: #dee2e6;
                color: #6c757d;
            }
            .step-line {
                flex: 1;
                height: 3px;
                background: #dee2e6;
                margin: 0 8px;
            }
            .step-line.done  {
                background: #28a745;
            }
            .variance-surplus {
                color: #28a745;
                font-weight: bold;
            }
            .variance-shortage {
                color: #dc3545;
                font-weight: bold;
            }
            #step1-panel, #step2-panel {
                display: none;
            }
            .product-row-selected {
                background-color: #e8f4fd !important;
            }
        </style>
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>
            <div class="content-wrapper">
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1><i class="fas fa-clipboard-check"></i> Tạo phiếu kiểm kê kho</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/stocktake?action=list">Kiểm kê kho</a></li>
                                    <li class="breadcrumb-item active">Tạo mới</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert">x</button>
                                ${error}
                            </div>
                        </c:if>

                        <!-- step -->
                        <div class="step-indicator">
                            <div class="step-box" id="step1-box">1</div>
                            <div class="step-line" id="line-1-2"></div>
                            <div class="step-box pending" id="step2-box">2</div>
                        </div>
                        <div class="d-flex mb-3" style="gap:60px; font-size:13px; color:#555; margin-left:4px;">
                            <span id="step1-label"><strong>Bước 1: </strong> Chọn sản phẩm kiểm kê</span>
                            <span id="step2-label" style="opacity:.5"><strong>Bước 2:</strong> Nhập số lượng thực tế</span>
                        </div>

                        <!-- step 1-->
                        <div id="step1-panel">
                            <div class="card card-primary card-outline">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin phiếu kiểm kê</h3>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4 form-group">
                                            <label>Mã phiếu kiểm kê <span class="text-danger">*</span></label>
                                            <input type="text" id="stNumber" class="form-control" readonly value="${stNumber}">
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Ngày kiểm kê</label>
                                            <c:set var="displayDate" value="${not empty st.stockTakeDate ? st.stockTakeDate : today}" />

                                            <input type="hidden" id="stockTakeDate" value="${displayDate}">
                                            <div class="form-control-plaintext font-weight-bold">
                                                <i class="fas fa-calendar-day text-primary mr-1"></i>${displayDate}
                                            </div>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Phạm vi kiểm kê</label>
                                            <select id="scopeType" class="form-control">
                                                <option value="ALL">Toàn bộ sản phẩm</option>
                                                <option value="CATEGORY">Theo danh mục</option>
                                                <option value="SELECTED">Chọn sản phẩm cụ thể</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-12 form-group">
                                            <label>Ghi chú</label>
                                            <textarea id="st1Notes" class="form-control" rows="2" placeholder="Ghi chú phiếu kiểm kê... "></textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card" id="category-select-card" style="display:none">
                                <div class="card-body">
                                    <div class="form-group mb-1">
                                        <label>Chọn danh mục <span class="text-danger">*</span></label>
                                        <select id="categorySelect" class="form-control">
                                            <option value="">
                                                -- Chọn danh mục --
                                            </option>
                                            <c:forEach var="cat" items="${categoryList}">
                                                <c:if test="${cat.isActive}">
                                                    <option value="${cat.categoryID}">${cat.categoryName}</option>
                                                </c:if>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <small id="categoryProductCount" class="text-muted"></small>
                                </div>
                            </div>

                            <!-- product selection (only for selected scope) -->
                            <div class="card" id="product-select-card" style="display:none">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-search"></i> Tìm và chọn sản phẩm cần kiểm</h3>   

                                    <div class="card-tools">
                                        <input type="text" class="form-control form-control-sm" id="productSearch" placeholder="Tìm theo tên hoặc SKU..." style="width:250px">
                                    </div>
                                </div>

                                <div class="card-body p-0">
                                    <table class="table table-bordered table-sm mb-0" id="productTable">
                                        <thead class="thead-light">
                                            <tr>
                                                <th style="width:40px"><input type="checkbox" id="checkAll"></th>
                                                <th>SKU</th>
                                                <th>Tên sản phẩm</th>
                                                <th style="width:120px">Tồn hệ thống</th>
                                                <th style="width:130px">Giá vốn</th>   
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${productList}">
                                                <tr class="product-row" data-name="${p.productName}" data-sku="${p.sku}">
                                                    <td class="text-center">
                                                        <input type="checkbox" class="product-check" value="${p.id}" data-name="${p.productName}"
                                                               data-sku="${p.sku}" data-stock="${p.stock}" data-cost="${p.costPrice}">
                                                    </td>
                                                    <td>${p.sku}</td>
                                                    <td>${p.productName}</td>
                                                    <td class="text-center">${p.stock}</td>
                                                    <td class="text-right">
                                                        <c:if test="${p.costPrice!=null}">
                                                            <fmt:formatNumber value="${p.costPrice}" type="number" maxFractionDigits="0"/>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="card-footer">
                                    <span id="selectedCount" class="text-muted">Chưa chọn sản phẩm nào</span>
                                </div>
                            </div>

                            <div class="card" id="all-scope-info" style="display:none">
                                <div class="card-body text-info">
                                    <i class="fas fa-info-circle"></i>
                                    Phiếu kiểm kê sẽ bao gồm tất cả <strong>${fn:length(productList)}</strong> sản phẩm đang hoạt động
                                </div>
                            </div>

                            <div class="text-right mt-3">
                                <a href="${pageContext.request.contextPath}/admin/stocktake?action=list" class="btn btn-default mr-2">
                                    <i class="fas fa-times"></i> Hủy
                                </a>
                                <button type="button" class="btn btn-primary" id="btnToStep2">
                                    <i class="fas fa-arrow-right"></i> Tiếp theo: Nhập số lượng
                                </button>
                            </div>

                        </div><!-- /step 1 -->

                        <!-- step 2 -->
                        <div id="step2-panel">
                            <form action="${pageContext.request.contextPath}/admin/stocktake" method="post" id="st-form">
                                <input type="hidden" name="action" value="save">
                                <input type="hidden" name="stNumber" id="f_stNumber">
                                <input type="hidden" name="stockTakeDate" id="f_stockTakeDate">
                                <input type="hidden" name="scopeType" id="f_scopeType">
                                <input type="hidden" name="scopeValue" id="f_scopeValue">
                                <input type="hidden" name="notes" id="f_notes">

                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-edit"></i> Nhập số lượng thực tế
                                        </h3>
                                        <div class="card-tools text-muted small">
                                            Phiếu: <strong id="hdr_stNumber"></strong>
                                            |  Ngày: <strong id="hdr_date"></strong>
                                        </div>
                                    </div>
                                    <div class="card-body p-0">
                                        <div class="p-2 border-bottom">
                                            <input type="text" id="countSearch" class="form-control form-control-sm"
                                                   placeholder="Tìm theo tên hoặc SKU..." style="max-width:280px">
                                        </div>
                                        <table class="table table-bordered table-sm mb-0" id="countTable">
                                            <thead class="thead-light">
                                                <tr>
                                                    <th>#</th>
                                                    <th>SKU</th>
                                                    <th>Tên sản phẩm</th>
                                                    <th style="width:120px">Tồn HT</th>
                                                    <th style="width:130px">Thực tế <span class="text-danger">*</span></th>
                                                    <th style="width:100px">Chênh lệch</th>
                                                    <th style="width:150px">Lý do</th>
                                                    <th>Ghi chú</th>
                                                    <th style="display:none"></th>   
                                                </tr>
                                            </thead>
                                            <tbody id="countTableBody">

                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                <div class="text-right mt-3">
                                    <button type="button" class="btn btn-default mr-2" id="btnBack">
                                        <i class="fas fa-arrow-left"></i> Quay lại
                                    </button>
                                    <button type="submit" class="btn btn-success">
                                        <i class="fas fa-save"></i> Lưu phiếu kiểm kê
                                    </button>
                                </div>

                            </form> 
                        </div>

                    </div><!-- ./container-f -->
                </section><!-- /content -->

            </div><!-- /container-wrapper -->
            <jsp:include page="include/admin-footer.jsp"/>
        </div><!-- /wrapper -->
        <script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>        
        <script>
            //data from server
            var ALL_PRODUCTS = [];
            <c:forEach var="p" items="${productList}">
            ALL_PRODUCTS.push(
                    {id: ${p.id},
                        name: '${p.productName.replace("'","\\'")}',
                        sku: '${p.sku}',
                        stock: ${p.stock},
                        cost: ${p.costPrice!=null ? p.costPrice : 0},
                        categoryId: ${p.categoryId}
                    }
            );
            </c:forEach>

            var REASON_OPTIONS = ['', 'LOSS', 'DAMAGE', 'THEFT', 'ERROR', 'OTHER'];
            var REASON_LABELS = ['-- Lý do --', 'Mất hàng', 'Hư hỏng', 'Trộm cắp', 'Lỗi nhập liệu', 'Khác'];
            var savedActQtys = {};
            var savedReasons = {};
            var savedNotes = {};
            var ALL_CATEGORIES = [];
            <c:forEach var="cat" items="${categoryList}">
                <c:if test="${cat.isActive}">
            ALL_CATEGORIES.push({
                id:${cat.categoryID},
                name: '${cat.categoryName.replace("'","\\'")}'
            });
                </c:if>
            </c:forEach>

            function showStep(n) {
                $('#step1-panel').toggle(n === 1);
                $('#step2-panel').toggle(n === 2);
                if (n === 1) {
                    $('#step1-box').removeClass('done pending').addClass('active');
                    $('#step2-box').removeClass('active done').addClass('pending');
                    $('#step2-label').css('opacity', '.5');
                    $('#line-1-2').removeClass('done');
                } else {
                    $('#step1-box').removeClass('active pending').addClass('done');
                    $('#step2-box').removeClass('pending done').addClass('active');
                    $('#step2-label').css('opacity', '1');
                    $('#line-1-2').addClass('done');
                }
            }

            $('#scopeType').on('change', function () {
                var sel = $(this).val();
                if (sel === 'SELECTED') {
                    $('#product-select-card').show();
                    $('#all-scope-info').hide();
                    $('#category-select-card').hide();
                } else if (sel === 'CATEGORY') {
                    $('#product-select-card').hide();
                    $('#all-scope-info').hide();
                    $('#category-select-card').show();
                    updateCategoryProductCount();
                } else {
                    $('#product-select-card').hide();
                    $('#all-scope-info').show();
                    $('#category-select-card').hide();
                }
            });
            $('#categorySelect').on('change', function () {
                updateCategoryProductCount();
            });
            function updateCategoryProductCount() {
                var catId = parseInt($('#categorySelect').val()) || 0;
                if (!catId) {
                    $('#categoryProductCount').text('');
                    return;
                }
                var count = ALL_PRODUCTS.filter(function (p) {
                    return p.categoryId === catId;
                }).length;
                $('#categoryProductCount').text('Danh mục này có ' + count + ' sản phẩm đang hoạt động');
            }

            function sortProductTable(kw) {
                kw = (kw || '').toLowerCase().trim();
                var tbody = $('#productTable tbody');
                var checkedMatched = [], uncheckedMatched = [], unmatched = [];
                tbody.find('tr.product-row').each(function () {
                    var isChecked = $(this).find('.product-check').prop('checked');
                    var sku = String($(this).data('sku') || '').toLowerCase();
                    var name = String($(this).data('name') || '').toLowerCase();
                    var matches = !kw || sku.includes(kw) || name.includes(kw);
                    if (isChecked && matches) {
                        checkedMatched.push(this);
                    } else if (!isChecked && matches) {
                        uncheckedMatched.push(this);
                    } else {
                        unmatched.push(this);
                    }
                });
                checkedMatched.forEach(function (row) {
                    tbody.append(row);
                    $(row).show();
                });
                uncheckedMatched.forEach(function (row) {
                    tbody.append(row);
                    $(row).show();
                });
                unmatched.forEach(function (row) {
                    tbody.append(row);
                    $(row).hide();
                });
            }

            $('#productSearch').on('input', function () {
                sortProductTable($(this).val());
            });
            $('#productSearch').on('keydown', function (e) {
                if (e.key !== 'Enter')
                    return;
                e.preventDefault();
                var firstVisible = $('#productTable tbody tr.product-row:visible').first();
                if (!firstVisible.length)
                    return;
                var cb = firstVisible.find('.product-check');
                var nowChecked = !cb.prop('checked');
                cb.prop('checked', nowChecked);
                firstVisible.toggleClass('product-row-selected', nowChecked);
                updateSelectedCount();
                $(this).val('').trigger('input');
            });
            //all
            $('#checkAll').on('change', function () {
                $('.product-check:visible').prop('checked', this.checked);
                updateSelectedCount();
                updateRowHighlight();
            });
            $(document).on('change', '.product-check', function () {
                updateSelectedCount();
                updateRowHighlight();
                sortProductTable($('#productSearch').val());
            });
            function updateSelectedCount() {
                var n = $('.product-check:checked').length;
                $('#selectedCount').text(n === 0 ? 'Chưa chọn sản phẩm nào' : 'Đã chọn ' + n + ' sản phẩm');
            }

            function updateRowHighlight() {
                $('.product-check').each(function () {
                    $(this).closest('tr').toggleClass('product-row-selected', this.checked);
                });
            }

            //next -> step 2
            $('#btnToStep2').on('click', function () {
                var date = $('#stockTakeDate').val();
                var scope = $('#scopeType').val();
                var todayVal = '${not empty st.stockTakeDate ? st.stockTakeDate : today}';
                if (!date) {
                    alert('Vui lòng chọn ngày kiểm kê.');
                    return;
                }
                if (date !== todayVal) {
                    alert('Ngày kiểm kê phải là ngày hôm nay (' + todayVal + '). Không được chọn ngày quá khứ hoặc tương lai.');
                    return;
                }
                var products = [];
                if (scope === 'ALL') {
                    products = ALL_PRODUCTS;
                } else if (scope === 'CATEGORY') {
                    var catId = parseInt($('#categorySelect').val()) || 0;
                    if (!catId) {
                        alert('Vui lòng chọn danh mục');
                        return;
                    }
                    products = ALL_PRODUCTS.filter(function (p) {
                        return p.categoryId === catId;
                    });
                    if (products.length === 0) {
                        alert('Danh mục này không có sản phẩm nào');
                        return;
                    }
                    $('#f_scopeValue').val(catId);
                } else {
                    $('.product-check:checked').each(function () {
                        var pid = parseInt(($(this)).val());
                        var found = ALL_PRODUCTS.find(function (p) {
                            return p.id === pid;
                        });
                        if (found)
                            products.push(found);
                    });
                    if (products.length === 0) {
                        alert('Vui lòng chọn ít nhất một sản phẩm');
                        return;
                    }
                }

                $('#f_stNumber').val($('#stNumber').val());
                $('#f_stockTakeDate').val(date);
                $('#f_scopeType').val(scope);
                $('#f_notes').val($('#st1Notes').val());
                $('#hdr_stNumber').text($('#stNumber').val());
                $('#hdr_date').text(date);
                buildCountTable(products);
                showStep(2);
            });
            function buildCountTable(products) {
                var tbody = $('#countTableBody');
                tbody.empty();
                products.forEach(function (p, idx) {


                    var actVal = (savedActQtys[p.id] !== undefined) ? savedActQtys[p.id] : p.stock;
                    var savedReason = savedReasons[p.id] || '';
                    var savedNote = savedNotes[p.id] || '';
                    var diff = actVal - p.stock;
                    var varianceHtml = diff === 0 ? '<span class="text-muted">0</span>' : diff > 0
                            ? '<span class="variance-surplus">+' + diff + '</span>'
                            : '<span class="variance-shortage">' + diff + '</span>';
                    var reasonOptsSel = REASON_OPTIONS.map(function (v, i) {
                        return '<option value="' + v + '"' + (v === savedReason ? ' selected' : '') + '>' + REASON_LABELS[i] + '</option>';
                    }).join('');
                    var row =
                            '<tr id="crow_' + idx + '">' +
                            '<td>' + (idx + 1) + '</td>' +
                            '<td>' + escHtml(p.sku) + '</td>' +
                            '<td>' + escHtml(p.name) + '</td>' +
                            '<td class="text-center"><strong>' + p.stock + '</strong></td>' +
                            '<td>' +
                            '<input type="number" class="form-control form-control-sm actual-qty" min="0"' +
                            ' value="' + actVal + '" data-sys="' + p.stock + '" data-idx="' + idx + '" data-pid="' + p.id + '">' +
                            '</td>' +
                            '<td class="text-center variance-cell" id="var_' + idx + '">' + varianceHtml + '</td>' +
                            '<td>' +
                            '<select class="form-control form-control-sm reason-select" id="reason_' + idx + '" data-pid="' + p.id + '"' + (diff === 0 ? ' disabled' : '') + '>' + reasonOptsSel +
                            '</select>' +
                            '</td>' +
                            '<td>' +
                            '<input type="text" class="form-control form-control-sm note-input" id="dnote_' + idx + '" data-pid="' + p.id + '" placeholder="Ghi chú..." value="' + escHtml(savedNote) + '">' +
                            '</td>' +
                            '<td style="display:none">' +
                            '<input type="hidden" name="pid[]" value="' + p.id + '">' +
                            '<input type="hidden" class="act-hidden" name="actQty[]" value="' + actVal + '">' +
                            '<input type="hidden" class="reason-hidden" name="reason[]" value="">' +
                            '<input type="hidden" class="note-hidden" name="detailNotes[]" value="">' +
                            '</td>' +
                            '</tr>';
                    tbody.append(row);
                });
                //bind
                tbody.off('input change').on('input', '.actual-qty', function () {
                    var idx = $(this).data('idx');
                    var pid = $(this).data('pid');
                    var sysQty = parseInt($(this).data('sys'));
                    var actQty = parseInt($(this).val()) || 0;
                    var diff = actQty - sysQty;
                    var cell = $('#var_' + idx);
                    if (diff === 0) {
                        cell.html('<span class="text-muted">0</span>');
                        $('#reason_' + idx).prop('disabled', true).val('');
                        if (pid)
                            savedReasons[pid] = '';
                    } else if (diff > 0) {
                        cell.html('<span class="variance-surplus">+' + diff + '</span>');
                        $('#reason_' + idx).prop('disabled', false);
                    } else {
                        cell.html('<span class="variance-shortage">' + diff + '</span>');
                        $('#reason_' + idx).prop('disabled', false);
                    }
                    $(this).closest('tr').find('.act-hidden').val(actQty);
                    if (pid)
                        savedActQtys[pid] = actQty;
                }).on('change', '.reason-select', function () {
                    var pid = $(this).data('pid');
                    if (pid)
                        savedReasons[pid] = $(this).val();
                }).on('input', '.note-input', function () {
                    var pid = $(this).data('pid');
                    if (pid)
                        savedNotes[pid] = $(this).val();
                });
                $('#st-form').off('submit').on('submit', function (e) {
                    var errors = [];
                    $('#countTableBody tr').each(function () {
                        var $row = $(this);
                        var idx = $row.find('.actual-qty').data('idx');
                        var actQty = parseInt($row.find('.actual-qty').val());
                        var sysQty = parseInt($row.find('.actual-qty').data('sys'));
                        var name = $row.find('td:eq(2)').text().trim();
                        if (isNaN(actQty) || actQty < 0) {
                            errors.push('Số lượng thực tế của "' + name + '" phải >= 0.');
                        }
                        var diff = actQty - sysQty;
                        if (!isNaN(diff) && diff !== 0 && !$('#reason_' + idx).val()) {
                            errors.push('Vui lòng chọn lý do cho sản phẩm có chênh lệch: "' + name + '".');
                        }
                        $row.find('.reason-hidden').val($('#reason_' + idx).val());
                        $row.find('.note-hidden').val($('#dnote_' + idx).val());
                    });
                    if (errors.length > 0) {
                        e.preventDefault();
                        alert(errors.join('\n'));
                    }
                });
            }

            $('#btnBack').on('click', function () {
                showStep(1);
            });
            // Step 2 live search
            $(document).on('input', '#countSearch', function () {
                var kw = $(this).val().toLowerCase().trim();
                var tbody = $('#countTableBody');
                var matched = [], unmatched = [];
                tbody.find('tr').each(function () {
                    var sku = $(this).find('td:eq(1)').text().toLowerCase();
                    var name = $(this).find('td:eq(2)').text().toLowerCase();
                    if (!kw || sku.includes(kw) || name.includes(kw)) {
                        matched.push(this);
                    } else {
                        unmatched.push(this);
                    }
                });
                matched.forEach(function (row) {
                    tbody.append(row);
                    $(row).show();
                });
                unmatched.forEach(function (row) {
                    tbody.append(row);
                    $(row).hide();
                });
            });
            function escHtml(s) {
                return $('<div>').text(s).html();
            }

            $(function () {
            <c:if test="${not empty st && st.status == 'IN_PROGRESS'}">
                $('#scopeType').val('${st.scopeType}').trigger('change');
                <c:if test="${st.scopeType == 'CATEGORY'}">
                $('#categorySelect').val('${st.scopeValue}').trigger('change');
                </c:if>
                $('#st1Notes').val('${st.notes != null ? st.notes : ""}');

                <c:forEach var="detail" items="${st.details}">
                savedActQtys[${detail.productId}] = ${detail.actualQuantity};
                savedReasons[${detail.productId}] = '${detail.varianceReason != null ? detail.varianceReason : ""}';
                savedNotes[${detail.productId}] = '${detail.notes != null ? detail.notes : ""}';
                </c:forEach>

                var editProducts = [];
                <c:forEach var="detail" items="${st.details}">
                editProducts.push({
                    id: ${detail.productId},
                    name: '${detail.productName.replace("'", "\\'")}',
                    sku: '${detail.productSku}',
                    stock: ${detail.systemQuantity}
                });
                </c:forEach>

                $('#f_stNumber').val('${st.stockTakeNumber}');
                $('#f_stockTakeDate').val('${st.stockTakeDate}');
                $('#f_scopeType').val('${st.scopeType}');
                $('#f_notes').val('${st.notes != null ? st.notes : ""}');
                $('#hdr_stNumber').text('${st.stockTakeNumber}');
                $('#hdr_date').text('${st.stockTakeDate}');

                buildCountTable(editProducts);
                showStep(2);
            </c:if>
            });
            $(function () {
            <c:if test="${empty st}">
                showStep(1);
                var initialScope = $('#scopeType').val();
                if (initialScope === 'ALL') {
                    $('#all-scope-info').show();
                }
            </c:if>
            });
        </script>
    </body>
</html>
