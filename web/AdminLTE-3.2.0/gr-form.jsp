<%-- 
    Document   : gr-form
    Created on : Mar 4, 2026, 6:23:43 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="roleName" value="${sessionScope.roleName}" />
<c:set var="isManagerOrAdmin" value="${roleName == 'Manager' || roleName == 'Store Manager' || roleName == 'Admin'}" />

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>
            <c:choose>
                <c:when test="${mode == 'create'}">Tạo phiếu nhập kho</c:when>
                <c:when test="${mode == 'view'}">Xem phiếu nhập kho</c:when>
                <c:otherwise>Phiếu nhập kho đang xử lý</c:otherwise>
            </c:choose>
        </title>
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
                                <h1>
                                    <c:choose>
                                        <c:when test="${mode == 'create'}">
                                            <i class="fas fa-plus-circle"></i> Tạo phiếu nhập kho
                                        </c:when>
                                        <c:when test="${mode == 'view'}">
                                            <i class="fas fa-eye"></i> Xem phiếu nhập kho
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-warehouse"></i> Phiếu nhập kho — Đang xử lý
                                        </c:otherwise>
                                    </c:choose>
                                </h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Home</a></li>
                                    <li class="breadcrumb-item">
                                        <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list">Phiếu nhập kho</a>
                                    </li>
                                    <li class="breadcrumb-item active">
                                        <c:choose>
                                            <c:when test="${mode == 'create'}">Tạo mới</c:when>
                                            <c:when test="${mode == 'view'}">Chi tiết</c:when>
                                            <c:otherwise>Đang xử lý</c:otherwise>
                                        </c:choose>
                                    </li>
                                </ol>
                            </div>
                        </div></div></section>

                <section class="content">
                    <div class="container-fluid">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas fa-ban"></i> ${error}
                            </div>
                        </c:if>
                        <c:if test="${not empty msg && msg.contains('success')}">
                            <div class="alert alert-success alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas fa-check"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_complete'}">Phiếu nhập kho đã được hoàn tất! Tồn kho đã được cập nhật.</c:when>
                                    <c:when test="${msg == 'success_create'}">Tạo phiếu nhập kho thành công!</c:when>
                                    <c:otherwise>Thao tác thành công!</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                        <c:if test="${msg == 'access_denied'}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas fa-ban"></i> Bạn không có quyền thực hiện thao tác này!
                            </div>
                        </c:if>

                        <c:if test="${mode=='create'}">
                            <form method="post" action="${pageContext.request.contextPath}/admin/goodsreceipt" id="grForm">
                                <input type="hidden" name="action" value="save">

                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-info-circle"></i> Thông tin phiếu nhập
                                        </h3>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <div class="col-md-4 form-group">
                                                <label><strong>Mã phiếu nhập:</strong></label>
                                                <div class="input-group">
                                                    <input type="text" class="form-control" value="${grNumber}" readonly
                                                           style="background-color:#f8f9fa;">
                                                    <div class="input-group-append">
                                                        <span class="input-group-text text-muted">(tự động)</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="col-md-5 form-group">
                                                <label><strong>Chọn đơn đặt hàng: <span class="text-danger">*</span></strong></label>
                                                <select name="poId" id="poSelect" class="form-control" required>
                                                    <option value="">-- Chọn ĐĐH đã duyệt --</option>
                                                    <c:forEach var="po" items="${poList}">
                                                        <option value="${po.id}" data-supplier="${po.supplierName}" 
                                                                ${selectedPoId == po.id ? 'selected': ''}>${po.poNumber} - ${po.supplierName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>

                                            <div class="col-md-3 form-group">
                                                <label><strong>Ngày nhập: <span class="text-danger">*</span></strong></label>
                                                <input type="datetime-local" name="receiptDate" 
                                                       class="form-control" required value="${empty receiptDate ? '' : receiptDate}">
                                            </div>     
                                        </div><div class="row">
                                            <div class="col-md-4 form-group">
                                                <label><strong>Nhà cung cấp:</strong></label>
                                                <input type="text" class="form-control" id="supplierDisplay" readonly
                                                       value="${selectedPo.supplierName}" placeholder="(tự động từ ĐĐH)">
                                            </div>   
                                            <div class="col-md-4 form-group">
                                                <label><strong>Người nhận:</strong></label>
                                                <div class="input-group">
                                                    <input type="text" class="form-control" value="${currentUserName}" readonly>
                                                    <div class="input-group-append">
                                                        <span class="input-group-text text-muted">(current user)</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div><div class="card" id="itemsCard">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-list"></i> Chi tiết nhập kho</h3>
                                    </div>
                                    <div class="card-body">
                                        <div class="alert alert-info mb-2" id="noItemsAlert" style="${not empty grItems ? 'display:none':''}">
                                            <i class="fas fa-arrow-up"></i>
                                            Vui lòng chọn đơn đặt hàng để tải danh sách sản phẩm.
                                        </div>

                                        <table class="table table-hover table-bordered table-sm" id="itemsTable" 
                                               style="${empty grItems ? 'display:none' : ''}">
                                            <thead>
                                                <tr>
                                                    <th style="width:40px">#</th>
                                                    <th>Sản phẩm</th>
                                                    <th>SL đặt</th>
                                                    <th>SL nhận</th>
                                                    <th>Đơn giá <small class="text-muted">(từ ĐĐH)</small></th>
                                                    <th>Thành tiền</th>
                                                </tr>  
                                            </thead>
                                            <tbody id="itemsBody">
                                                <c:forEach var="item" items="${grItems}" varStatus="var">
                                                    <tr>
                                                        <td>${var.index + 1}</td>
                                                        <td>
                                                            <input type="hidden" name="poLineItemId" value="${item.poLineItemId}">
                                                            <input type="hidden" name="productId" value="${item.productId}">
                                                            <input type="hidden" name="maxQty" value="${item.quantityReceived}">
                                                            ${item.productName}
                                                        </td>
                                                        <td>${item.quantityOrdered}</td>
                                                        <td>
                                                            <input type="number" name="quantityReceived"   class="form-control form-control-sm qty-received"
                                                                   value="${item.quantityReceived}"  min="0" max="${item.quantityReceived}"
                                                                   data-ordered="${item.quantityReceived}">
                                                        </td>
                                                        <td>
                                                            <input type="hidden" name="unitCost" class="unit-cost-val" value="${item.unitCost}">
                                                            <fmt:formatNumber value="${item.unitCost}" type="currency" currencySymbol="đ"/>
                                                        </td>
                                                        <td class="line-total text-right">-</td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>

                                        <div id="itemsSummary" class="row mt-2"
                                             style="${empty grItems ? 'display:none' : ''}">
                                            <div class="col-md-6">
                                                <small class="text-muted">Tổng SL nhận: <strong id="totalReceived">-</strong></small>
                                            </div>
                                            <div class="col-md-6 text-right">
                                                <h5 class="mb-0">Tổng tiền: <strong id="grandTotal">-</strong></h5>
                                            </div>
                                        </div>
                                    </div><div class="card-footer">
                                        <div class="form-group mb-0">
                                            <label><strong>Ghi chú:</strong></label>
                                            <textarea name="notes" class="form-control" rows="2"
                                                      placeholder="Ghi chú về tình trạng hàng, thiếu hàng...">${notes}</textarea>
                                        </div>
                                    </div>

                                </div>

                                <div class="card">
                                    <div class="card-body">
                                        <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list"
                                           class="btn btn-secondary mr-2">
                                            <i class="fas fa-arrow-left"></i> Quay lại
                                        </a>
                                        <button type="submit" class="btn btn-primary" id="saveBtn">
                                            <i class="fas fa-save"></i> Lưu phiếu (Đang nhập)
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </c:if>


                        <c:if test="${mode=='pending'|| mode=='view'}">
                            <div class="card card-primary card-outline">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin phiếu nhập</h3>
                                    <div class="card-tools">
                                        <c:if test="${mode=='pending'}">
                                            <span class="badge badge-warning p-2">
                                                <i class="fas fa-circle"></i> Đang nhập
                                            </span>
                                        </c:if>
                                        <c:if test="${mode == 'view'}">
                                            <span class="badge badge-success p-2">
                                                <i class="fas fa-check-square"></i> Hoàn tất
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4 form-group">
                                            <label class="text-muted">Mã phiếu nhập:</label>
                                            <div class="readonly-field"><strong>${gr.receiptNumber}</strong></div>
                                        </div>

                                        <div class="col-md-4 form-group">
                                            <label class="text-muted">Đơn đặt hàng:</label>
                                            <div class="readonly-field">
                                                <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=view&poNumber=${gr.poNumber}">
                                                    ${gr.poNumber}
                                                </a>
                                            </div>
                                        </div>

                                        <div class="col-md-4 form-group">
                                            <label class="text-muted">Ngày nhập:</label>
                                            <div class="readonly-field">
                                                ${gr.receiptDate != null ? gr.receiptDateFormatted : '-'}
                                            </div>
                                        </div>        

                                    </div><div class="row">
                                        <div class="col-md-4 form-group">
                                            <label class="text-muted">Nhà cung cấp:</label>
                                            <div class="readonly-field">${gr.supplierName}</div>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label class="text-muted">Người nhận:</label>
                                            <div class="readonly-field">${gr.receivedByName}</div>
                                        </div>
                                        <c:if test="${mode == 'view' && not empty gr.completedAt}">
                                            <div class="col-md-4 form-group">
                                                <label class="text-muted">Hoàn tất lúc:</label>
                                                <div class="readonly-field">${gr.completedAtFormatted}</div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div><div class="card">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-list"></i> Chi tiết nhập kho</h3>
                                </div>
                                <div class="card-body p-0">
                                    <c:choose>
                                        <c:when test="${not empty items}">
                                            <table class="table table-hover table-bordered mb-0">
                                                <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>Sản phẩm</th>
                                                        <th>SL đặt</th>
                                                        <th>SL nhận</th>
                                                        <th>Đơn giá</th>
                                                        <th >Thành tiền</th>
                                                            <c:if test="${mode == 'view'}">
                                                            <th class="text-right">Giá vốn TB mới</th>
                                                            </c:if> 
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="item" items="${items}" varStatus="var">
                                                        <tr>
                                                            <td>${var.index +1}</td>
                                                            <td>${item.productName}</td>
                                                            <td>${item.quantityOrdered}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${item.quantityOrdered!=null && item.quantityReceived <item.quantityOrdered}">
                                                                        <span class="discrepancy">${item.quantityReceived}</span>
                                                                        <small class="text-danger ml-1">
                                                                            (thiếu ${item.quantityOrdered - item.quantityReceived})
                                                                        </small>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-success font-weight-bold">${item.quantityReceived}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <fmt:formatNumber value="${item.unitCost}" type="currency" currencySymbol="đ" />
                                                            </td>
                                                            <td>
                                                                <fmt:formatNumber value="${item.lineTotal}" type="currency" currencySymbol="đ" />
                                                            </td>
                                                            <c:if test="${mode == 'view'}">
                                                                <td class="text-right">
                                                                    <c:if test="${item.newAvgCost != null}">
                                                                        <fmt:formatNumber value="${item.newAvgCost}"  type="currency" currencySymbol="đ" />
                                                                    </c:if>
                                                                </td>
                                                            </c:if>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                                <tfoot>
                                                    <tr>
                                                        <td colspan="3" class="text-right"><strong>Tổng cộng:</strong></td>
                                                        <td> <strong>${gr.totalQuantity}</strong></td>
                                                        <td></td>
                                                        <td>
                                                            <strong>
                                                                <fmt:formatNumber value="${gr.totalAmount}"  type="currency" currencySymbol="đ" />
                                                            </strong>
                                                        </td>
                                                        <c:if test="${mode == 'view'}"><td></td></c:if>
                                                        </tr>
                                                    </tfoot>
                                                </table>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-info m-3 text-center">Không có chi tiết nhập kho.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div><div class="card-footer">
                                    <div class="row">
                                        <div class="col-md-8">
                                            <label class="text-muted">Ghi chú:</label>
                                            <div class="readonly-field"
                                                 style="min-height:60px; align-items:flex-start; padding:8px 12px;">
                                                <c:choose>
                                                    <c:when test="${not empty gr.notes}">${gr.notes}</c:when>
                                                    <c:otherwise><em class="text-muted">Không có ghi chú</em></c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="col-md-4 d-flex align-items-center justify-content-end">
                                            <div>
                                                <span class="text-muted">Trạng thái: </span>
                                                <c:if test="${mode == 'pending'}">
                                                    <span class="badge badge-warning p-2 ml-1">
                                                        <i class="fas fa-circle"></i> Đang nhập
                                                    </span>
                                                </c:if>
                                                <c:if test="${mode == 'view'}">
                                                    <span class="badge badge-success p-2 ml-1">
                                                        <i class="fas fa-check-square"></i> Hoàn tất
                                                    </span>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div></div></div><div class="card">
                                <div class="card-body d-flex flex-wrap gap-2">
                                    <c:if test="${mode == 'pending'}">
                                        <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list"
                                           class="btn btn-secondary mr-2">
                                            <i class="fas fa-arrow-left"></i> Quay lại
                                        </a>

                                            <form method="post" action="${pageContext.request.contextPath}/admin/goodsreceipt"
                                                  style="display:inline"
                                                  onsubmit="return confirm('Xác nhận HỦY phiếu ${gr.receiptNumber}? Phiếu sẽ bị xóa vĩnh viễn và không thể khôi phục.')">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="receiptNumber" value="${gr.receiptNumber}">
                                                <button type="submit" class="btn btn-danger mr-2">
                                                    <i class="fas fa-trash-alt"></i> Hủy phiếu
                                                </button>
                                            </form>

                                            <form method="post" action="${pageContext.request.contextPath}/admin/goodsreceipt" style="display:inline"
                                                  onsubmit="return confirm('Xác nhận hoàn tất nhập kho? Tồn kho sẽ được cập nhật ngay lập tức và không thể hoàn tác.');">
                                                <input type="hidden" name="action" value="complete">
                                                <input type="hidden" name="receiptNumber" value="${gr.receiptNumber}">
                                                <button type="submit" class="btn btn-primary">
                                                    <i class="fas fa-check"></i> Hoàn tất nhập kho
                                                </button>                                                
                                            </form>
                                    </c:if>

                                    <c:if test="${mode == 'view'}">
                                        <a href="${pageContext.request.contextPath}/admin/goodsreceipt?action=list"
                                           class="btn btn-secondary">
                                            <i class="fas fa-arrow-left"></i> Quay lại
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </section>
            </div>
            <jsp:include page="include/admin-footer.jsp" />
        </div><script>
            $(document).ready(function () {

                // AJAX
                $('#poSelect').on('change', function () {
                    var poId = $(this).val();
                    var supplierName = $(this).find(':selected').data('supplier') || '';
                    $('#supplierDisplay').val(supplierName);

                    if (!poId) {
                        $('#itemsBody').empty();
                        $('#itemsTable').hide();
                        $('#itemsSummary').hide();
                        $('#noItemsAlert').show();
                        return;
                    }


                    $.getJSON('${pageContext.request.contextPath}/admin/goodsreceipt?action=getPoItems&poId=' + poId, function (items) {
                        if (!items || items.length === 0) {
                            $('#itemsBody').empty();
                            $('#itemsTable').hide();
                            $('#itemsSummary').hide();
                            $('#noItemsAlert').show();
                            return;
                        }

                        var tbody = $('#itemsBody');
                        tbody.empty();

                        $.each(items, function (idx, item) {
                            var remaining = item.remaining; // ord- alreadyReceived

                            var row =
                                    '<tr>' +
                                    '<td>' + (idx + 1) + '</td>' +
                                    '<td>' +
                                    '<input type="hidden" name="poLineItemId" value="' + item.poLineItemId + '">' +
                                    '<input type="hidden" name="productId" value="' + item.productId + '">' +
                                    '<input type="hidden" name="maxQty" value="' + remaining + '">' +
                                    item.productName +
                                    '</td>' +
                                    '<td>' + item.quantityOrdered + '</td>' +
                                    '<td><input type="number" name="quantityReceived" ' +
                                    'class="form-control form-control-sm qty-received" ' +
                                    'value="' + remaining + '" min="0" max="' + remaining + '" ' +
                                    'data-ordered="' + remaining + '"></td>' +
                                    '<td>' +
                                    '<input type="hidden" name="unitCost" class="unit-cost-val" value="' + item.unitCost + '">' +
                                    Number(item.unitCost).toLocaleString('vi-VN') + 'đ' +
                                    '</td>' +
                                    '<td class="line-total text-right">-</td>' +
                                    '</tr>';

                            tbody.append(row);
                        });

                        $('#noItemsAlert').hide();
                        $('#itemsTable').show();
                        $('#itemsSummary').show();
                        recalcTotals();

                    }).fail(function () {
                        alert('Không thể tải danh sách sản phẩm. Vui lòng thử lại.');
                    });
                });

                //tính lại thành tiền và tổng 
                function recalcTotals() {
                    var totalReceived = 0;
                    var grandTotal = 0;

                    $('.qty-received').each(function () {
                        var qty = parseInt($(this).val()) || 0;
                        var unitCost = parseFloat($(this).closest('tr').find('.unit-cost-val').val()) || 0;
                        var lineTotal = qty * unitCost;

                        $(this).closest('tr').find('.line-total')
                                .text(lineTotal.toLocaleString('vi-VN') + 'đ');

                        totalReceived += qty;
                        grandTotal += lineTotal;
                    });

                    $('#totalReceived').text(totalReceived.toLocaleString('vi-VN'));
                    $('#grandTotal').text(grandTotal.toLocaleString('vi-VN') + 'đ');
                }

                recalcTotals();

                // Validate khi thay đổi SL nhận
                $(document).on('input', '.qty-received', function () {
                    var max = parseInt($(this).data('ordered')) || 0;
                    var val = parseInt($(this).val()) || 0;

                    if (val < 0) {
                        $(this).val(0);
                    } else if (val > max) {
                        $(this).val(max);
                        $(this).addClass('is-invalid');
                    } else {
                        $(this).removeClass('is-invalid');
                    }

                    recalcTotals();
                });

                // Validate trước khi submit form
                $('#grForm').on('submit', function (e) {
                    if (!$('#poSelect').val()) {
                        alert('Vui lòng chọn đơn đặt hàng.');
                        e.preventDefault();
                        return;
                    }

                    var hasQty = false;
                    $('.qty-received').each(function () {
                        if (parseInt($(this).val()) > 0)
                            hasQty = true;
                    });

                    if (!hasQty) {
                        alert('Vui lòng nhập số lượng nhận cho ít nhất 1 sản phẩm.');
                        e.preventDefault();
                    }
                });

            });
        </script>

    </body>
</html>