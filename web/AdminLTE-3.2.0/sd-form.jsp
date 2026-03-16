<%-- 
    Document   : sd-form
    Created on : Mar 11, 2026, 11:28:02 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo phiếu xuất hủy</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
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
                                <h1><i class="fas fa-trash-alt"></i> Tạo phiếu xuất hủy hàng</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/stockdisposal?action=list">Xuất hủy hàng</a></li>
                                    <li class="breadcrumb-item active">Tạo mới</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">
                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success')?'alert-success':'alert-danger'} alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert">x</button>
                                ${msg}
                            </div>
                        </c:if>


                        <form action="${pageContext.request.contextPath}/admin/stockdisposal" method="post" id="disposalForm">
                            <input type="hidden" name="action" value="save">
                            <input type="hidden" name="sdNumber" value="${sdNumber}">

                            <!-- header info -->
                            <div class="card card-danger card-outline">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin phiếu xuất hủy</h3>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4 form-group">
                                            <label>Mã phiếu <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" value="${sdNumber}" readonly>
                                            <small class="text-muted">(tự động)</small>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Ngày tạo</label>
                                            <input type="text" class="form-control" value="${today}" readonly>
                                            <small class="text-muted">(auto)</small>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Lý do chung <span class="text-danger">*</span></label>
                                            <select name="disposalReason" class="form-control" required>
                                                <option value="">-- Chọn lý do -- </option>
                                                <option value="DAMAGED">Hỏng hóc</option>
                                                <option value="EXPIRED">Hết hạn sử dụng</option>
                                                <option value="DEFECTIVE">Lỗi sản phẩm</option>
                                                <option value="OTHER">Khác</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- product detail -->
                            <div class="card">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-boxes"></i> Chi tiết sản phẩm xuất hủy</h3>
                                </div>

                                <div class="card-body">
                                    <div class="row mb-4">
                                        <div class="col-md-6" style="position: relative;">
                                            <label>Tìm và thêm sản phẩm:</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control" placeholder="Gõ tên hoặc SKU sản phẩm..." id="productSearchInput" >
                                                <div class="input-group-append">
                                                    <button type="button" class="btn btn-primary" id="btnAddProduct"><i class="fas fa-plus mr-1"></i>Thêm</button>
                                                </div>
                                            </div>
                                            <div id="productDropdown" class="list-group shadow mt-1"
                                                 style="position:absolute; z-index:9999; width:100%; display:none;">
                                            </div>
                                        </div>

                                        <table class="table table-bordered product-table table-hover mt-1" id="detailTable">
                                            <thead class="thead-light">
                                                <tr>
                                                    <th style="width:50px" class="text-center">STT</th>
                                                    <th>Sản phẩm</th>
                                                    <th style="width:120px" class="text-center">Khả dụng / Tồn</th>
                                                    <th style="width:150px" class="text-center">SL xuất hủy</th>
                                                    <th>Lý do cụ thể</th>
                                                    <th style="width:60px" class="text-center">Xóa</th>
                                                </tr>
                                            </thead>
                                            <tbody id="detailBody">
                                                <tr id="emptyRow">
                                                    <td colspan="6" class="text-center text-muted py-4">
                                                        <i class="fas fa-box-open mr-1"></i> Chưa có sản phẩm. Dùng ô tìm kiếm để thêm sản phẩm.
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <div class="mt-2">
                                            <strong>Tổng số lượng xuất hủy: </strong>
                                            <span id="totalQtyDisplay"  class="text-danger">0</span>
                                        </div>

                                    </div>
                                </div>

                                <div class="card">
                                    <div class="card-body">
                                        <div class="form-group">
                                            <label>Ghi chú:</label>
                                            <textarea name="notes" class="form-control" rows="3"
                                                      placeholder="Ghi chú thêm về đợt xuất hủy này..."></textarea>
                                        </div>
                                        <div class="d-flex justify-content-end">
                                            <a href="${pageContext.request.contextPath}/admin/stockdisposal?action=list"
                                               class="btn btn-default mr-2">
                                                <i class="fas fa-times"></i> Hủy
                                            </a>
                                            <button type="submit" class="btn btn-danger" id="btnSubmit">
                                                <i class="fas fa-paper-plane"></i> Lưu và gửi duyệt
                                            </button>
                                        </div>
                                    </div>
                                </div>
                        </form>
                    </div>
                </section>
            </div>
            <jsp:include page="include/admin-footer.jsp"/>

        </div>

        <script>
            var products = [
            <c:forEach var="p" items="${productList}" varStatus="s">
            {id: ${p.id},
                    name: "<c:out value='${p.productName}'/>",
                    sku: "<c:out value='${p.sku}'/>",
                    stock: ${p.stock},
                    reservedStock: ${p.reservedStock},
                    availableStock: ${p.stock - p.reservedStock}
            }<c:if test="${!s.last}">,</c:if>
            </c:forEach>
            ];
            $(function () {

                var $search = $("#productSearchInput");
                var $dropdown = $("#productDropdown");
                var $table = $("#detailBody");
                var $total = $("#totalQtyDisplay");
                var $btnAdd = $("#btnAddProduct");
                var lastFiltered = [];

                $search.on("input", function () {

                    var query = $(this).val().toLowerCase().trim();
                    $dropdown.empty().hide();

                    if (query.length < 1) {
                        lastFiltered = [];
                        return;
                    }
                    var filtered = products.filter(function (p) {
                        return p.name.toLowerCase().includes(query) ||
                                p.sku.toLowerCase().includes(query);
                    }).slice(0, 8);
                    lastFiltered = filtered;

                    if (filtered.length === 0) {
                        $dropdown.html(
                                '<div class="list-group-item text-muted">Không tìm thấy sản phẩm</div>'
                                ).show();
                        return;
                    }

                    var html = "";

                    filtered.forEach(function (p) {

                        html += '<a href="#" class="list-group-item list-group-item-action product-item"' +
                                ' data-id="' + p.id + '"' +
                                ' data-name="' + p.name + '"' +
                                ' data-stock="' + p.stock + '"' +
                                ' data-available="' + p.availableStock + '">' +
                                '<strong>' + p.name + '</strong>' +
                                '<div class="small text-muted">Khả dụng: ' + p.availableStock + ' / Tồn: ' + p.stock + '</div>' +
                                '</a>';
                    });

                    $dropdown.html(html).show();

                });

                function addTopMatchedProduct() {
                    if (!lastFiltered.length) {
                        return false;
                    }
                    var p = lastFiltered[0];
                    addProduct(p.id, p.name, p.stock, p.availableStock);
                    $search.val("").focus();
//                    $search.focus();
                    $dropdown.hide();
                    lastFiltered = [];
                    return true;
                }

                $dropdown.on("click", ".product-item", function (e) {

                    e.preventDefault();

                    var id = $(this).data("id");
                    var name = $(this).data("name");
                    var stock = $(this).data("stock");
                    var available = $(this).data("available");

                    addProduct(id, name, stock, available);

                    $search.val("").focus();
                    $dropdown.hide();
                    lastFiltered = [];
                });

                $search.on("keydown", function (e) {
                    if (e.key != "Enter") {
                        return;
                    }
                    e.preventDefault();

                    if (!addTopMatchedProduct()) {
                        $search.trigger("input");
                        addTopMatchedProduct();
                    }
                });

                $btnAdd.on("click", function () {
                    if (!addTopMatchedProduct()) {
                        $search.trigger("input");
                        if (!addTopMatchedProduct()) {
                            $search.focus();
                        }
                    }
                });

                function addProduct(id, name, stock, available) {
                    var maxAvailable = parseInt(available, 10) || 0;
                    if (maxAvailable <= 0) {
                        alert("Sản phẩm này không còn số lượng khả dụng để xuất hủy.");
                        return;
                    }
                    var found = false;

                    $table.find("tr").each(function () {

                        var pid = $(this).find("input[name='pid[]']").val();

                        if (pid == id) {
                            var $qty = $(this).find("input[name='dispQty[]']");
                            var currentQty = parseInt($qty.val(), 10) || 0;
                            var maxQty = parseInt($qty.attr("max"), 10) || 0;
                            if (currentQty < maxQty) {
                                $qty.val(currentQty + 1);
                                highlightRow($(this));
                            } else {
                                alert("Sản phẩm này đã đạt số lượng tối đa có thể xuất hủy.");
                            }
                            found = true;
                            return false;
                        }
                    });

                    if (found) {
                        updateTotal();
                        return;
                    }

                    $("#emptyRow").remove();

                    var index = $table.find("tr").length + 1;

                    var row = '<tr>' +
                            '<td class="text-center">' + index + '</td>' +
                            '<td><strong>' + name + '</strong>' +
                            '<input type="hidden" name="pid[]" value="' + id + '"></td>' +
                            '<td class="text-center">' + available + ' / ' + stock + '</td>' +
                            '<td><input type="number" class="form-control qty-input" name="dispQty[]" value="1" min="1" max="' + maxAvailable + '"></td>' +
                            '<td><input type="text" class="form-control" name="specificReason[]" placeholder="Lý do..."></td>' +
                            '<td class="text-center"><button type="button" class="btn btn-danger btn-sm btn-remove"><i class="fas fa-trash"></i></button></td>' +
                            '</tr>';

                    var $row = $(row).appendTo($table);
                    highlightRow($row);
                    updateTotal();
                }

                $table.on("click", ".btn-remove", function () {
                    $(this).closest("tr").remove();
                    updateRowNumbers();
                    updateTotal();

                });

                function updateRowNumbers() {

                    $table.find("tr").each(function (i) {
                        $(this).find("td:first").text(i + 1);

                    });

                }


                $table.on("input", ".qty-input", function () {
                    var maxQty = parseInt($(this).attr("max"), 10) || 0;
                    var val = parseInt($(this).val(), 10);
                    if (isNaN(val) || val < 1) {
                        $(this).val(1);
                        updateTotal();
                        return;
                    }
                    if (val > maxQty) {
                        $(this).val(maxQty);
                    }
                    var $btnAdd = $("#btnAddProduct");
                    var lastFiltered = [];
                    updateTotal();
                });

                function updateTotal() {

                    var total = 0;

                    $table.find("input[name='dispQty[]']").each(function () {

                        total += parseInt($(this).val()) || 0;

                    });

                    $total.text(total);
                }

                function highlightRow($row) {

                    $row.addClass("highlight-row");

                    setTimeout(function () {
                        $row.removeClass("highlight-row");
                    }, 700);

                    $row[0].scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });

                }

                $(document).click(function (e) {

                    if (!$(e.target).closest("#productSearchInput,#productDropdown").length) {

                        $dropdown.hide();

                    }

                });

                $("#disposalForm").on("keydown", function (e) {
                    if (e.target && e.target.id === "productSearchInput") {
                        return;
                    }
                    if (e.key === "Enter") {
                        e.preventDefault();
                    }

                });

            });
        </script>

    </body>
</html>