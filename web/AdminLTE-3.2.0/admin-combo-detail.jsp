<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${combo != null ? 'Chỉnh sửa' : 'Tạo'} Combo - Admin</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/select2/css/select2.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="include/admin-header.jsp" />
    <jsp:include page="include/admin-sidebar.jsp" />

    <div class="content-wrapper">
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><i class="fas fa-boxes"></i> ${combo != null ? 'Chỉnh sửa Combo' : 'Tạo Combo Mới'}</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/AdminLTE-3.2.0/index.jsp">Home</a></li>
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/admin/combos">Combo</a></li>
                            <li class="breadcrumb-item active">${combo != null ? 'Chỉnh sửa' : 'Tạo mới'}</li>
                        </ol>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">

                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-ban"></i> ${error}
                    </div>
                </c:if>

                <form method="post" action="<%= request.getContextPath() %>/admin/combos" id="comboForm">
                    <input type="hidden" name="action" value="${combo != null ? 'edit' : 'add'}">
                    <c:if test="${combo != null}">
                        <input type="hidden" name="comboID" value="${combo.comboID}">
                    </c:if>

                    <div class="row">
                        <!-- Left: Combo Info -->
                        <div class="col-md-6">
                            <div class="card card-primary">
                                <div class="card-header">
                                    <h3 class="card-title">Thông tin Combo</h3>
                                </div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="comboName">Tên Combo <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="comboName" name="comboName"
                                               value="${combo != null ? combo.productName : ''}"
                                               placeholder="VD: Combo Sách Lập Trình Java" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="comboSku">Mã SKU (Scancode) <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="comboSku" name="comboSku"
                                               value="${combo != null ? combo.productSku : ''}"
                                               placeholder="VD: COMBO-001" required>
                                        <small class="form-text text-muted">Mã dùng để quét tại POS</small>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="sellingPrice">Giá bán combo (VNĐ) <span class="text-danger">*</span></label>
                                                <input type="number" class="form-control" id="sellingPrice" name="sellingPrice"
                                                       value="${combo != null ? combo.sellingPrice : ''}"
                                                       min="0" step="1000" placeholder="0" required>
                                            </div>
                                        </div>
                                        <c:if test="${combo == null}">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="comboQuantity">Số lượng combo <span class="text-danger">*</span></label>
                                                    <input type="number" class="form-control" id="comboQuantity" name="comboQuantity"
                                                           min="1" value="1" required>
                                                    <small class="form-text text-muted">Stock SP con sẽ bị trừ tương ứng</small>
                                                </div>
                                            </div>
                                        </c:if>
                                        <c:if test="${combo != null}">
                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label>Số lượng combo hiện tại</label>
                                                    <p class="form-control-plaintext">
                                                        <span class="badge badge-info" style="font-size: 1rem;">${combo.comboQuantity}</span>
                                                        <small class="text-muted ml-2">(điều chỉnh ± ở trang danh sách)</small>
                                                    </p>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="form-group">
                                        <label for="description">Mô tả</label>
                                        <textarea class="form-control" id="description" name="description" rows="2"
                                                  placeholder="Mô tả combo...">${comboProduct != null ? comboProduct.description : ''}</textarea>
                                    </div>
                                    <c:if test="${combo == null}">
                                        <input type="hidden" name="categoryId" value="1">
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Right: Child Products -->
                        <div class="col-md-6">
                            <div class="card card-info">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-cubes"></i> Sản phẩm trong Combo</h3>
                                    <div class="card-tools">
                                        <button type="button" class="btn btn-tool" onclick="addComboRow()">
                                            <i class="fas fa-plus"></i> Thêm SP
                                        </button>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <table class="table table-sm" id="comboItemsTable">
                                        <thead>
                                            <tr>
                                                <th>Sản phẩm</th>
                                                <th style="width: 100px;">Số lượng</th>
                                                <th style="width: 80px;">Stock</th>
                                                <th style="width: 50px;">Xoá</th>
                                            </tr>
                                        </thead>
                                        <tbody id="comboItemsBody">
                                            <!-- Dynamic rows or pre-filled from combo data -->
                                        </tbody>
                                    </table>
                                    <div class="text-center mt-2">
                                        <button type="button" class="btn btn-outline-primary btn-sm" onclick="addComboRow()">
                                            <i class="fas fa-plus-circle"></i> Thêm sản phẩm vào combo
                                        </button>
                                    </div>

                                    <c:if test="${combo == null}">
                                        <div class="callout callout-info mt-3">
                                            <h5><i class="fas fa-info-circle"></i> Lưu ý</h5>
                                            <p class="mb-0">Khi tạo combo, stock sản phẩm con sẽ bị trừ:<br>
                                            <strong>Stock trừ = SL trong combo × SL combo tạo</strong></p>
                                        </div>
                                    </c:if>
                                    <c:if test="${combo != null}">
                                        <div class="callout callout-warning mt-3">
                                            <h5><i class="fas fa-exclamation-triangle"></i> Lưu ý khi chỉnh sửa</h5>
                                            <p class="mb-0">Hệ thống sẽ tự động <strong>trả stock</strong> SP con cũ và <strong>trừ stock</strong> SP con mới
                                            dựa trên số lượng combo hiện tại (<strong>${combo.comboQuantity}</strong>).</p>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="fas fa-save"></i> ${combo != null ? 'Cập nhật Combo' : 'Tạo Combo'}
                            </button>
                            <a href="<%= request.getContextPath() %>/admin/combos" class="btn btn-default btn-lg ml-2">
                                <i class="fas fa-times"></i> Hủy
                            </a>
                        </div>
                    </div>
                </form>

            </div>
        </section>
    </div>

    <jsp:include page="include/admin-footer.jsp" />
</div>

<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/select2/js/select2.full.min.js"></script>
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

<script>
// Product data from server
var availableProducts = [
    <c:forEach var="p" items="${products}" varStatus="status">
    { id: ${p.id}, name: '${p.productName}', sku: '${p.sku}', stock: ${p.stock} }<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

// Existing combo items (for edit mode)
var existingItems = [
    <c:if test="${combo != null}">
        <c:forEach var="item" items="${combo.comboItems}" varStatus="status">
        { childProductID: ${item.childProductID}, quantity: ${item.quantity}, name: '${item.childProductName}', sku: '${item.childProductSku}', stock: ${item.childProductStock} }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    </c:if>
];

var rowCounter = 0;

function buildOptions(selectedId) {
    var options = '<option value="">-- Ch\u1ECDn SP --</option>';
    for (var i = 0; i < availableProducts.length; i++) {
        var p = availableProducts[i];
        var selected = (p.id == selectedId) ? ' selected' : '';
        options += '<option value="' + p.id + '" data-stock="' + p.stock + '"' + selected + '>'
                + p.name + ' (' + p.sku + ')</option>';
    }
    // If selectedId is not found in availableProducts (e.g., the product is the one being edited),
    // check existingItems for it
    if (selectedId) {
        var found = false;
        for (var i = 0; i < availableProducts.length; i++) {
            if (availableProducts[i].id == selectedId) { found = true; break; }
        }
        if (!found) {
            for (var i = 0; i < existingItems.length; i++) {
                if (existingItems[i].childProductID == selectedId) {
                    options += '<option value="' + selectedId + '" data-stock="' + existingItems[i].stock + '" selected>'
                            + existingItems[i].name + ' (' + existingItems[i].sku + ')</option>';
                    break;
                }
            }
        }
    }
    return options;
}

function addComboRow(selectedId, quantity) {
    var tbody = document.getElementById('comboItemsBody');
    var row = document.createElement('tr');
    row.id = 'comboRow_' + rowCounter;

    var options = buildOptions(selectedId || '');
    var qty = quantity || 1;

    // Determine initial stock display
    var stockText = '-';
    var stockClass = 'text-muted';
    if (selectedId) {
        // Find stock for this product
        for (var i = 0; i < availableProducts.length; i++) {
            if (availableProducts[i].id == selectedId) {
                stockText = availableProducts[i].stock;
                stockClass = parseInt(stockText) > 0 ? 'text-success' : 'text-danger';
                break;
            }
        }
        if (stockText === '-') {
            for (var i = 0; i < existingItems.length; i++) {
                if (existingItems[i].childProductID == selectedId) {
                    stockText = existingItems[i].stock;
                    stockClass = parseInt(stockText) > 0 ? 'text-success' : 'text-danger';
                    break;
                }
            }
        }
    }

    var currentRowId = rowCounter;
    row.innerHTML =
        '<td>' +
            '<select name="childProductID" id="productSelect_' + currentRowId + '" class="form-control product-select" required ' +
                'onchange="updateStock(this, ' + currentRowId + ')">' +
                options +
            '</select>' +
        '</td>' +
        '<td><input type="number" name="childQuantity" class="form-control form-control-sm" value="' + qty + '" min="1" required></td>' +
        '<td><span id="stockInfo_' + currentRowId + '" class="' + stockClass + '">' + stockText + '</span></td>' +
        '<td><button type="button" class="btn btn-danger btn-xs" onclick="removeComboRow(\'' + currentRowId + '\')">' +
            '<i class="fas fa-times"></i></button></td>';

    tbody.appendChild(row);

    // Initialize Select2 on the new dropdown
    $('#productSelect_' + currentRowId).select2({
        theme: 'bootstrap4',
        placeholder: '-- T\u00ECm v\u00E0 ch\u1ECDn s\u1EA3n ph\u1EA9m --',
        allowClear: true,
        width: '100%'
    }).on('change', function() {
        updateStock(this, currentRowId);
    });

    rowCounter++;
}

function removeComboRow(id) {
    var row = document.getElementById('comboRow_' + id);
    if (row) {
        // Destroy Select2 before removing
        $('#productSelect_' + id).select2('destroy');
        row.remove();
    }
}

function updateStock(selectEl, rowId) {
    // Handle both native select and Select2 change events
    var $sel = $(selectEl).is('select') ? $(selectEl) : $('#productSelect_' + rowId);
    var selectedVal = $sel.val();
    var stockSpan = document.getElementById('stockInfo_' + rowId);

    if (selectedVal) {
        var option = $sel.find('option:selected');
        var stock = option.attr('data-stock');
        if (stock !== undefined && stock !== '') {
            stockSpan.textContent = stock;
            stockSpan.className = parseInt(stock) > 0 ? 'text-success' : 'text-danger';
        } else {
            stockSpan.textContent = '-';
            stockSpan.className = 'text-muted';
        }
    } else {
        stockSpan.textContent = '-';
        stockSpan.className = 'text-muted';
    }
}

// Validate form
document.getElementById('comboForm').addEventListener('submit', function(e) {
    var rows = document.querySelectorAll('#comboItemsBody tr');
    if (rows.length === 0) {
        e.preventDefault();
        alert('Vui l\u00F2ng th\u00EAm \u00EDt nh\u1EA5t 1 s\u1EA3n ph\u1EA9m v\u00E0o combo!');
        return false;
    }
    var selects = document.querySelectorAll('#comboItemsBody select[name="childProductID"]');
    for (var i = 0; i < selects.length; i++) {
        if (!selects[i].value) {
            e.preventDefault();
            alert('Vui l\u00F2ng ch\u1ECDn s\u1EA3n ph\u1EA9m cho t\u1EA5t c\u1EA3 c\u00E1c d\u00F2ng!');
            selects[i].focus();
            return false;
        }
    }
    return true;
});

// Initialize on page load
window.addEventListener('DOMContentLoaded', function() {
    if (existingItems.length > 0) {
        // Edit mode: pre-fill existing combo items
        for (var i = 0; i < existingItems.length; i++) {
            addComboRow(existingItems[i].childProductID, existingItems[i].quantity);
        }
    } else {
        // Add mode: start with one empty row
        addComboRow();
    }
});
</script>

</body>
</html>
