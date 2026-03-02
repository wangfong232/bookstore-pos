<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Tạo đơn đặt hàng</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@ttskch/select2-bootstrap4-theme@1.5.2/dist/select2-bootstrap4.min.css">

        <style>
            /* Highlight animation */
            @keyframes flashHighlight {
                0% {
                    background-color: rgba(76, 175, 80, 0.4);
                }
                100% {
                    background-color: transparent;
                }
            }
            .highlight-row {
                animation: flashHighlight 1s ease-out;
            }

            /* Quick search styling */
            .quick-search-container {
                background: #f8f9fa;
                padding: 12px 15px;
                border-bottom: 2px solid #dee2e6;
                margin: -1px -1px 0 -1px;
            }

            /* Zebra striping */
            #productTableBody tr:nth-child(even) {
                background-color: rgba(0,0,0,.02);
            }
        </style>
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">
            <!-- Navbar & Sidebar -->
            <jsp:include page="include/admin-header.jsp" />
            <jsp:include page="include/admin-sidebar.jsp" />


            <div class="content-wrapper">
                <!-- Content Header (Page header) -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>
                                    <c:if test="${mode=='add'}">Tạo đơn đặt hàng mới</c:if>
                                    <c:if test="${mode=='edit'}">Sửa đơn đặt hàng</c:if>
                                    </h1>
                                </div>
                                <div class="col-sm-6">
                                    <ol class="breadcrumb float-sm-right">
                                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/purchaseorder?action=list">Đơn đặt hàng</a></li>
                                    <li class="breadcrumb-item active">
                                        <c:if test="${mode=='add'}">Thêm mới</c:if>
                                        <c:if test="${mode=='edit'}">Chỉnh sửa</c:if>
                                        </li>
                                    </ol>
                                </div>
                            </div>
                        </div><!-- /.container-fluid -->
                    </section>

                    <!-- Main content -->
                    <section class="content">
                        <div class="container-fluid">
                            <div class="row">
                                <!-- left column -->
                                <div class="col-12">     
                                    <!-- general form elements -->
                                    <div class="card card-primary">
                                        <div class="card-header">
                                            <h3 class="card-title">Thông tin cơ bản</h3>
                                        </div>
                                        <!-- /.card-header -->

                                        <!-- Display error message if exists -->
                                    <c:if test="${not empty error}">
                                        <div class="alert alert-danger alert-dismissible" style="margin: 15px 15px 0 15px;">
                                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                                            <i class="icon fas fa-ban"></i> ${error}
                                        </div>
                                    </c:if>

                                    <!-- form start -->
                                    <form action="${pageContext.request.contextPath}/purchaseorder" method="post">
                                        <div class="card-body">
                                            <div class="form-group">
                                                <label for="poNumber">Mã ĐĐH: <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="poNumber" name="poNumber" value="${poNumber}" placeholder="Tự động" readonly>
                                                <small class="form-text text-muted">Mã đơn đặt hàng được tự động tạo</small>
                                            </div>

                                            <div class="col-md-6">
                                                <div class="form-group">
                                                    <label for="supplierId">Nhà cung cấp: <span class="text-danger">*</span></label>
                                                    <select class="form-control select2" id="supplierId" name="supplierId" required>
                                                        <option value="">-- Chọn nhà cung cấp --</option>
                                                        <c:forEach var="item" items="${supList}">
                                                            <option value="${item.id}" ${item.id == po.supplierId ? 'selected' : ''}>
                                                                ${item.supplierName}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="form-group">
                                                        <label for="orderDate">Ngày tạo:</label>
                                                        <div class="input-group">
                                                            <div class="input-group-prepend">
                                                                <span class="input-group-text"><i class="far fa-calendar-alt"></i></span>
                                                            </div>
                                                            <input type="date" class="form-control" id="orderDate" name="orderDate" value="${orderDate}">
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="col-md-6">
                                                    <div class="form-group">
                                                        <label for="expectedDate">Ngày giao dự kiến:</label>
                                                        <div class="input-group">
                                                            <div class="input-group-prepend">
                                                                <span class="input-group-text"><i class="far fa-calendar-alt"></i></span>
                                                            </div>
                                                            <input type="date" class="form-control" id="expectedDate" name="expectedDate" value="${expectedDate}">
                                                        </div>
                                                        <small class="form-text text-muted">Có thể cập nhật sau khi trao đổi với nhà cung cấp</small>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- chi tiet san pham  add items -->
                                            <div class="card card-outline card-info mt-3">
                                                <div class="card-header">
                                                    <h3 class="card-title"><i class="fas fa-boxes"></i> Chi tiết sản phẩm</h3>
                                                    <div class="card-tools">
                                                        <button type="button" class="btn btn-info btn-sm" data-toggle="modal"
                                                                data-target="#addProductModal">
                                                            <i class="fas fa-list-ul"></i> Thêm chi tiết
                                                        </button>
                                                    </div>
                                                </div>

                                                <!-- Quick Search Bar -->
                                                <div class="quick-search-container">
                                                    <div class="input-group">
                                                        <div class="input-group-prepend">
                                                            <span class="input-group-text bg-success text-white"><i class="fas fa-bolt"></i></span>
                                                        </div>
                                                        <!--                                                        <select class="form-control" id="quickProductSearch" style="width: 100%;">
                                                                                                                    <option value=""></option>
                                                                                                                    <option value="1" data-name="Sách Lập trình Java" data-price="150000">Sách Lập trình Java - 150,000đ</option>
                                                                                                                    <option value="2" data-name="Sách Python cơ bản" data-price="120000">Sách Python cơ bản - 120,000đ</option>
                                                                                                                    <option value="3" data-name="Sách JavaScript nâng cao" data-price="180000">Sách JavaScript nâng cao - 180,000đ</option>
                                                                                                                    <option value="4" data-name="Sách SQL Server" data-price="200000">Sách SQL Server - 200,000đ</option>
                                                                                                                    <option value="5" data-name="Sách HTML5 & CSS3" data-price="130000">Sách HTML5 & CSS3 - 130,000đ</option>
                                                                                                                    <option value="6" data-name="Sách Design Patterns" data-price="170000">Sách Design Patterns - 170,000đ</option>
                                                                                                                    <option value="7" data-name="Sách Clean Code" data-price="190000">Sách Clean Code - 190,000đ</option>
                                                                                                                </select>-->
                                                        <div class="position-relative">
                                                            <input type="text" id="productSearchInput" class="form-control" placeholder="Gõ tên sản phẩm để tìm..." autocomplete="off">

                                                            <div id="searchResults" class="list-group position-absolute w-100 shadow" style="z-index: 1000; max-height: 300px; overflow-y: auto; display: none;">
                                                            </div>
                                                        </div>
                                                        <div class="input-group-append">
                                                            <span class="input-group-text text-muted"><i class="fas fa-plus-circle"></i></span>
                                                        </div>
                                                    </div>
                                                    <small class="text-muted">
                                                        <table class="table table-bordered table-striped table hover mb-0">
                                                            <thead>
                                                                <tr>
                                                                    <th style="width: 5%">STT</th>
                                                                    <th style="width: 22%">Sản phẩm</th>
                                                                    <th style="width: 8%">Số lượng</th>
                                                                    <th style="width: 13%">Đơn giá</th>
                                                                    <th style="width: 18%">Giảm giá</th>
                                                                    <th style="width: 13%">Thành tiền</th>
                                                                    <th style="width: 16%">Ghi chú</th>
                                                                    <th style="width: 5%">Xóa</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody id="productTableBody">
                                                                <c:choose>
                                                                    <c:when test="${not empty orderItems}">
                                                                        <c:forEach var="item" items="${orderItems}" varStatus="status">
                                                                            <tr>
                                                                                <td class="text-center">${status.index + 1}</td>

                                                                                <td> ${item.productName}
                                                                                    <input type="hidden" name="productId" value="${item.productId}">
                                                                                </td>

                                                                                <td>
                                                                                    <input type="number"  name="quantity" class="form-control form-control-sm qty-input" 
                                                                                           value="${item.quantityOrdered}" min="1" onchange="calculateRowTotal(this)">
                                                                                </td>
                                                                                <td>
                                                                                    <input type="number" name="unitPrice" class="form-control form-control-sm price-input" 
                                                                                           value="${item.unitPrice}" min="0" step="1000" onchange="calculateRowTotal(this)">
                                                                                </td>
                                                                                <td>
                                                                                    <div class="input-group input-group-sm">
                                                                                        <input type="number"  name="discountValue" class="form-control discount-input" 
                                                                                               value="${item.discountValue}" min="0" onchange="calculateRowTotal(this)">
                                                                                        <select name="discountType" class="form-control discount-type-select" style="max-width: 80px" onchange="updateDiscountConstraints(this); calculateRowTotal(this)">
                                                                                            <option value="PERCENT" ${item.discountType == 'PERCENT' ? 'selected' : ''}>%</option>
                                                                                            <option value="AMOUNT" ${item.discountType == 'AMOUNT' ? 'selected' : ''}>VND</option>
                                                                                        </select>
                                                                                    </div>
                                                                                </td>
                                                                                <td class="text-right">
                                                                                    <strong class="text-success">${item.lineTotal}đ</strong>
                                                                                </td>
                                                                                <td>
                                                                                    <input type="text" name="itemNote" class="form-control form-control-sm" value="${item.notes}" placeholder="Ghi chú...">
                                                                                </td>
                                                                                <td class="text-center">
                                                                                    <button type="button" class="btn btn-danger btn-sm" onclick="removeProduct(this)">
                                                                                        <i class="fas fa-trash"></i>
                                                                                    </button>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </c:when>

                                                                </c:choose>
                                                            </tbody>
                                                        </table>
                                                </div>
                                            </div>

                                            <div class="card-footer">
                                                <div class="row">
                                                    <div class="col-md-6">
                                                        <h5><i class="fas fa-calculator"></i> Tổng số lượng: <span class="badge badge-primary badge-lg" id="totalQuantity">0</span></h5>
                                                    </div>
                                                    <div class="col-md-6 text-right">
                                                        <h4><strong>Tổng tiền:</strong> <span class="text-danger" id="totalAmount">0đ</span></h4>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Notes -->
                                        <div class="form-group mt-3">
                                            <label for="notes"><i class="fas fa-sticky-note"></i> Ghi chú:</label>
                                            <textarea class="form-control" id="notes" name="notes" rows="3" placeholder="Nhập ghi chú cho đơn đặt hàng...">${notes}</textarea>
                                        </div><!-- ./them chi tiet san pham -->

                                </div><!-- ./card-body -->
                                <div class="card-footer">
                                    <a href="${pageContext.request.contextPath}/purchaseorder?action=list" class="btn btn-default">
                                        <i class="fas fa-times"></i> Hủy
                                    </a>
                                    <button type="submit" name="action" value="save" class="btn btn-primary float-right">
                                        <i class="fas fa-save"></i> Gửi duyệt
                                    </button>
                                </div>
                                </form><!-- ./form -->
                            </div><!-- /.card -->
                        </div><!-- ./col-12 -->
                    </div><!-- ./row -->
            </div><!-- ./container-fluid -->
        </section><!-- ./content -->

    </div><!-- ./content-wrapper -->

    <!-- Footer -->
    <jsp:include page="include/admin-footer.jsp" />
</div><!-- ./wrapper -->

<!-- Add product modal -->
<div class="modal fade" id="addProductModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header bg-success">
                <h5 class="modal-tittle"><i class="fas fa-plus-circle"></i> Thêm sản phẩm</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>

            <div class="modal-body">
                <div class="form-group">
                    <label for="productSelect">Chọn sản phẩm: <span class="text-danger">*</span></label>
                    <select class="form-control" id="productSelect">
                        <option value="">-- Chọn sản phẩm --</option>
                        <c:forEach var="item" items="${productList}">
                            <option value="${item.id}" 
                                    data-name="${item.productName}" 
                                    data-price="${item.sellingPrice}">
                                ${item.productName} - ${item.sellingPrice}đ 
                            </option>
                        </c:forEach>
                    </select>                         
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label for="productQty">Số lượng: <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="productQty" value="1" min="1">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label for="productPrice">Đơn giá:</label>
                            <input type="number" class="form-control" id="productPrice" value="0" min="0" step="1000" >
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label for="productDiscount">Giảm giá (%):</label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="productDiscount" value="0" min="0">
                                <select class="form-control" id="productDiscountType" style="max-width: 100px;">
                                    <option value="PERCENT">%</option>
                                    <option value="AMOUNT">VND</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="productNotes">Ghi chú:</label>
                    <input type="text" class="form-control" id="productNotes" placeholder="Nhập ghi chú...">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-success" onclick="addProductToTable()"><i class="fas fa-check"></i> Thêm</button>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

<script>
                    // Danh sách sản phẩm
//                    const products = [
//                        {id: '1', name: 'Sách Lập trình Java', price: 150000},
//                        {id: '2', name: 'Sách Python cơ bản', price: 120000},
//                        {id: '3', name: 'Sách JavaScript nâng cao', price: 180000},
//                        {id: '4', name: 'Sách SQL Server', price: 200000},
//                        {id: '5', name: 'Sách HTML5 & CSS3', price: 130000},
//                        {id: '6', name: 'Sách Design Patterns', price: 170000},
//                        {id: '7', name: 'Sách Clean Code', price: 190000}
//                    ];

                    const products = [
    <c:forEach var="item" items="${productList}" varStatus="status">
                    {id: '${item.id}', name: '${item.productName}', price: ${item.sellingPrice}}
        <c:if test="${!status.last}">,</c:if>
    </c:forEach>
                    ];

                    $(document).ready(function () {
                        // Khởi tạo Select2
                        $('#quickProductSearch').select2({
                            theme: 'bootstrap4',
                            placeholder: "Gõ tên sản phẩm để thêm ngay ..."
                        });

                        // Sự kiện chọn Quick Search
                        $('#quickProductSearch').on('select2:select', function (e) {
                            var element = $(e.params.data.element);
                            var pid = element.val();
                            var name = element.data('name');
                            var price = parseInt(element.data('price')) || 0;

                            createRow(pid, name, price, 1);

                            $(this).val(null).trigger('change');
                        });

                        // Modal: Tự động điền giá khi chọn sản phẩm
                        $('#productSelect').change(function () {
                            var selected = $(this).find('option:selected');
                            var price = selected.data('price') || 0;
                            document.getElementById('productPrice').value = price;
                        });
                    });


                    document.addEventListener('DOMContentLoaded', function () {
                        const searchInput = document.getElementById('productSearchInput');
                        const searchResults = document.getElementById('searchResults');

                        if (searchInput && searchResults) {
                            // Tìm kiếm khi gõ
                            searchInput.addEventListener('input', function () {
                                const query = this.value.trim().toLowerCase();

                                if (query.length < 1) {
                                    searchResults.style.display = 'none';
                                    return;
                                }

                                // Lọc sản phẩm
                                const filtered = products.filter(p => p.name.toLowerCase().includes(query));

                                if (filtered.length === 0) {
                                    searchResults.innerHTML = '<div class="list-group-item text-muted">Không tìm thấy sản phẩm</div>';
                                    searchResults.style.display = 'block';
                                    return;
                                }

                                // Hiển thị kết quả
                                let html = '';
                                filtered.forEach(p => {
                                    html += '<a href="#" class="list-group-item list-group-item-action search-item" ';
                                    html += 'data-id="' + p.id + '" data-name="' + p.name + '" data-price="' + p.price + '">';
                                    html += '<strong>' + p.name + '</strong> - <span class="text-success">' + formatCurrency(p.price) + '</span>';
                                    html += '</a>';
                                });
                                searchResults.innerHTML = html;
                                searchResults.style.display = 'block';
                            });

                            // Chọn sản phẩm từ dropdown
                            searchResults.addEventListener('click', function (e) {
                                e.preventDefault();
                                const item = e.target.closest('.search-item');
                                if (!item)
                                    return;

                                const pid = item.dataset.id;
                                const pname = item.dataset.name;
                                const price = parseInt(item.dataset.price);

                                createRow(pid, pname, price, 1);

                                // Reset search
                                searchInput.value = '';
                                searchResults.style.display = 'none';
                            });

                            // Ẩn dropdown khi click ra ngoài
                            document.addEventListener('click', function (e) {
                                if (!searchInput.contains(e.target) && !searchResults.contains(e.target)) {
                                    searchResults.style.display = 'none';
                                }
                            });
                        }
                        const tableBody = document.getElementById('productTableBody');


                        tableBody.addEventListener('input', function (e) {
                            if (e.target.matches('.qty-input, .price-input, .discount-input')) {
                                calculateRowTotal(e.target);
                            }
                        });

                        document.querySelectorAll('.discount-type-select').forEach(function (select) {
                            updateDiscountConstraints(select);
                        });

                        calculateGrandTotal();
                    });


                    function createRow(pid, pname, price, qtyToAdd) {
                        if (!pid)
                            return;

                        var tbody = document.getElementById('productTableBody');
                        var rows = tbody.querySelectorAll('tr');
                        var found = false;

//tìm dòng trùng 
                        rows.forEach(function (row) {
                            if (row.id === 'emptyRow')
                                return;

                            var pidInput = row.querySelector('input[name="productId"]');
                            if (pidInput && pidInput.value == pid) {
                                var qtyInput = row.querySelector('.qty-input');
                                var currentQty = parseInt(qtyInput.value) || 0;

                                qtyInput.value = currentQty + parseInt(qtyToAdd);
                                calculateRowTotal(qtyInput);

                                row.classList.add('highlight-row');
                                setTimeout(function () {
                                    row.classList.remove('highlight-row');
                                }, 1000);
                                row.scrollIntoView({behavior: 'smooth', block: 'center'});

                                found = true;
                            }
                        });

                        if (!found) {
                            var emptyRow = document.getElementById('emptyRow');
                            if (emptyRow)
                                emptyRow.remove();

                            var rowCount = tbody.querySelectorAll('tr').length + 1;
                            var total = price * qtyToAdd;
                            var tr = document.createElement('tr');

                            var html = '';
                            html += '<td class="text-center align-middle">' + rowCount + '</td>';
                            html += '<td class="align-middle">' + pname + '<input type="hidden" name="productId" value="' + pid + '"></td>';
                            html += '<td><input type="number" name="quantity" class="form-control form-control-sm qty-input" value="' + qtyToAdd + '" min="1"></td>';
                            html += '<td><input type="number" name="unitPrice" class="form-control form-control-sm price-input" value="' + price + '" min="0" step="1000"></td>';
                            html += '<td>';
                            html += '  <div class="input-group input-group-sm">';
                            html += '    <input type="number" name="discountValue" class="form-control discount-input" value="0" min="0" onchange="calculateRowTotal(this)">';
                            html += '    <select name="discountType" class="form-control discount-type-select" style="max-width: 80px;" onchange="updateDiscountConstraints(this); calculateRowTotal(this);">';
                            html += '      <option value="PERCENT" selected>%</option>';
                            html += '      <option value="AMOUNT">VND</option>';
                            html += '    </select>';
                            html += '  </div>';
                            html += '</td>';
                            html += '<td class="text-right align-middle row-total"><strong class="text-success">' + formatCurrency(total) + '</strong></td>';
                            html += '<td><input type="text" name="itemNote" class="form-control form-control-sm" placeholder="Ghi chú..."></td>';
                            html += '<td class="text-center align-middle">';
                            html += '  <button type="button" class="btn btn-danger btn-sm" onclick="removeProduct(this)"><i class="fas fa-trash"></i></button>';
                            html += '</td>';

                            tr.innerHTML = html;
                            tbody.appendChild(tr);

                            tr.classList.add('highlight-row');
                            setTimeout(function () {
                                tr.classList.remove('highlight-row');
                            }, 1000);
                        }

                        calculateGrandTotal();
                    }

                    function addProductToTable() {
                        var select = document.getElementById('productSelect');
                        var productId = select.value;
                        var selectedOption = select.options[select.selectedIndex];

                        if (!productId) {
                            alert('Vui lòng chọn sản phẩm!');
                            return;
                        }

                        var productName = selectedOption.getAttribute('data-name');
                        var quantity = parseInt(document.getElementById('productQty').value) || 1;
                        var price = parseInt(document.getElementById('productPrice').value) || 0;
                        var discount = parseFloat(document.getElementById('productDiscount').value) || 0;
                        var discountType = document.getElementById('productDiscountType').value;

                        var notes = document.getElementById('productNotes').value;

                        createRow(productId, productName, price, quantity);

                        //cập nhật thông tin phụ 
                        var rows = document.querySelectorAll('#productTableBody tr');
                        var targetRow = null;

                        rows.forEach(function (r) {
                            var pidInput = r.querySelector('input[name="productId"]');
                            if (pidInput && pidInput.value == productId)
                                targetRow = r;
                        });

                        if (targetRow) {
                            targetRow.querySelector('.discount-input').value = discount;
                            targetRow.querySelector('.discount-type-select').value = discountType;
                            targetRow.querySelector('input[name="itemNote"]').value = notes;
                            updateDiscountConstraints(targetRow.querySelector('.discount-type-select'));
                            calculateRowTotal(targetRow.querySelector('.qty-input'));
                        }

                        // Reset form
                        $('#productSelect').val('').trigger('change');
                        document.getElementById('productQty').value = 1;
                        document.getElementById('productPrice').value = 0;
                        document.getElementById('productDiscount').value = 0;
                        document.getElementById('productDiscountType').value = 'PERCENT';
                        document.getElementById('productNotes').value = '';


                    }

                    function calculateRowTotal(element) {
                        var row = element.closest('tr');
                        var quantity = parseInt(row.querySelector('.qty-input').value) || 0;
                        var price = parseInt(row.querySelector('.price-input').value) || 0;
                        var discountValue = parseFloat(row.querySelector('.discount-input').value) || 0;
                        var discountType = row.querySelector('.discount-type-select').value;

                        var subtotal = quantity * price;
                        var discountAmount = 0;

                        if (discountType === 'PERCENT') {
                            discountAmount = subtotal * discountValue / 100;
                        } else {
                            discountAmount = discountValue;
                        }

                        var total = subtotal - discountAmount;
                        if (total < 0)
                            total = 0;

                        row.querySelector('.row-total strong').textContent = formatCurrency(total);
                        calculateGrandTotal();
                    }

                    function removeProduct(button) {
                        if (!confirm('Bạn có chắc muốn xóa sản phẩm này?'))
                            return;

                        var row = button.closest('tr');
                        row.remove();

                        var tbody = document.getElementById('productTableBody');

                        if (tbody.querySelectorAll('tr').length === 0) {
                            var emptyRowHtml = '<tr id="emptyRow">';
                            emptyRowHtml += '<td colspan="8" class="text-center text-muted py-4">';
                            emptyRowHtml += '<i class="fas fa-inbox fa-3x mb-3 d-block"></i>';
                            emptyRowHtml += '<p class="mb-0">Chưa có sản phẩm nào. Click "Thêm sản phẩm" để bắt đầu.</p>';
                            emptyRowHtml += '</td></tr>';

                            tbody.innerHTML = emptyRowHtml;
                        } else {
                            updateRowNumbers();
                        }
                        calculateGrandTotal();
                    }

                    function updateRowNumbers() {
                        var rows = document.querySelectorAll('#productTableBody tr');
                        var count = 0;
                        rows.forEach(function (row) {
                            if (row.id !== 'emptyRow') {
                                count++;
                                row.cells[0].textContent = count;
                            }
                        });
                    }

                    function calculateGrandTotal() {
                        var totalQty = 0;
                        var totalAmount = 0;

                        var rows = document.querySelectorAll('#productTableBody tr');

                        rows.forEach(function (row) {
                            if (row.id === 'emptyRow')
                                return;

                            var qInput = row.querySelector('.qty-input');
                            var pInput = row.querySelector('.price-input');
                            var dInput = row.querySelector('.discount-input');
                            var dTypeSelect = row.querySelector('.discount-type-select');

                            if (qInput && pInput && dInput && dTypeSelect) {
                                var qty = parseInt(qInput.value) || 0;
                                var price = parseInt(pInput.value) || 0;
                                var discountValue = parseFloat(dInput.value) || 0;
                                var discountType = dTypeSelect.value;

                                totalQty += qty;
                                var subtotal = qty * price;
                                var discountAmount = 0;

                                if (discountType === 'PERCENT') {
                                    discountAmount = subtotal * discountValue / 100;
                                } else {
                                    discountAmount = discountValue;
                                }

                                totalAmount += subtotal - discountAmount;
                            }
                        });

                        document.getElementById('totalQuantity').textContent = totalQty;
                        document.getElementById('totalAmount').textContent = formatCurrency(totalAmount);
                    }

                    function updateDiscountConstraints(selectElement) {
                        var row = selectElement.closest('tr');
                        if (!row)
                            return;

                        var discountInput = row.querySelector('.discount-input');
                        var discountType = selectElement.value;

                        if (discountType === 'PERCENT') {
                            discountInput.setAttribute('max', '100');
                            discountInput.setAttribute('step', '0.01');
                            if (parseFloat(discountInput.value) > 100) {
                                discountInput.value = '100';
                            }
                        } else {
                            discountInput.removeAttribute('max');
                            discountInput.setAttribute('step', '1000');
                        }
                    }

                    function formatCurrency(value) {
                        return value.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'}).replace('₫', 'đ');
                    }
</script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!--<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>-->
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script>
                    $(document).ready(function () {
                        $('#supplierId').select2({
                            theme: 'bootstrap4',
                            placeholder: '-- Chọn nhà cung cấp --'
                        });
                        $('#productSelect').select2({
                            theme: 'bootstrap4',
                            placeholder: '-- Chọn sản phẩm --',
                            dropdownParent: $('#addProductModal')
                        });

                        $('#addProductModal').on('hidden.bs.modal', function () {
                            if ($('.modal-backdrop').length > 0) {
                                $('.modal-backdrop').remove();
                                $('body').removeClass('modal-open').css('padding-right', '');
                            }
                        });

                        //handle discount type change in modal
                        $('#productDiscountType').on('change', function () {
                            var discountInput = $('#productDiscount');
                            var discountType = $(this).val();

                            if (discountType === 'PERCENT') {
                                discountInput.attr('max', '100');
                                discountInput.attr('step', '0.01');
                                if (parseFloat(discountInput.val()) > 100) {
                                    discountInput.val('100');
                                }
                            } else {
                                discountInput.removeAttr('max');
                                discountInput.attr('step', '1000');
                            }
                        });

                    });

</script>

</body>
</html>