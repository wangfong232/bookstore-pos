<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý Combo - Admin</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
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
                        <h1><i class="fas fa-boxes"></i> Quản lý Combo</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/AdminLTE-3.2.0/index.jsp">Home</a></li>
                            <li class="breadcrumb-item active">Combo</li>
                        </ol>
                    </div>
                </div>
            </div>
        </section>

        <section class="content">
            <div class="container-fluid">

                <!-- Messages -->
                <c:if test="${param.msg == 'add_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Tạo combo thành công!
                    </div>
                </c:if>
                <c:if test="${param.msg == 'delete_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Xoá combo thành công! Stock sản phẩm con đã được trả lại.
                    </div>
                </c:if>
                <c:if test="${param.msg == 'increase_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Tăng số lượng combo thành công!
                    </div>
                </c:if>
                <c:if test="${param.msg == 'decrease_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Giảm số lượng combo thành công!
                    </div>
                </c:if>
                <c:if test="${param.msg == 'adjust_error'}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-ban"></i> ${param.error != null ? param.error : 'Không thể điều chỉnh số lượng combo!'}
                    </div>
                </c:if>
                <c:if test="${param.msg == 'update_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Cập nhật combo thành công!
                    </div>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Danh sách Combo</h3>
                        <div class="card-tools">
                            <a href="<%= request.getContextPath() %>/admin/combos?action=add" class="btn btn-primary btn-sm">
                                <i class="fas fa-plus"></i> Tạo Combo Mới
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th style="width: 60px;">ID</th>
                                        <th>Tên Combo</th>
                                        <th style="width: 120px;">Mã SKU</th>
                                        <th style="width: 130px;">Giá bán</th>
                                        <th style="width: 100px;">Số lượng</th>
                                        <th>Sản phẩm trong combo</th>
                                        <th style="width: 200px;" class="text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="combo" items="${combos}">
                                        <tr>
                                            <td><strong>#${combo.comboID}</strong></td>
                                            <td>
                                                <strong>${combo.productName}</strong>
                                                <span class="badge badge-info ml-1">COMBO</span>
                                            </td>
                                            <td><code>${combo.productSku}</code></td>
                                            <td>
                                                <strong class="text-success">
                                                    <fmt:formatNumber value="${combo.sellingPrice}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${combo.comboQuantity <= 0}">
                                                        <span class="badge badge-danger"><i class="fas fa-times-circle"></i> 0</span>
                                                    </c:when>
                                                    <c:when test="${combo.comboQuantity <= 5}">
                                                        <span class="badge badge-warning"><i class="fas fa-exclamation-triangle"></i> ${combo.comboQuantity}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-success"><i class="fas fa-check-circle"></i> ${combo.comboQuantity}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:forEach var="item" items="${combo.comboItems}" varStatus="status">
                                                    <small>
                                                        <i class="fas fa-cube text-muted"></i>
                                                        ${item.childProductName} <strong>×${item.quantity}</strong>
                                                    </small>
                                                    <c:if test="${!status.last}"><br></c:if>
                                                </c:forEach>
                                            </td>
                                            <td class="text-center">
                                                <!-- Edit button -->
                                                <a href="<%= request.getContextPath() %>/admin/combos?action=edit&id=${combo.comboID}"
                                                   class="btn btn-warning btn-sm" title="Chỉnh sửa combo">
                                                    <i class="fas fa-edit"></i>
                                                </a>

                                                <!-- Adjust buttons -->
                                                <form method="post" action="<%= request.getContextPath() %>/admin/combos" style="display: inline;">
                                                    <input type="hidden" name="action" value="adjust">
                                                    <input type="hidden" name="comboID" value="${combo.comboID}">
                                                    <input type="hidden" name="delta" value="-1">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm" title="Giảm 1"
                                                            onclick="return confirm('Giảm 1 combo? Stock SP con sẽ được trả lại.')">
                                                        <i class="fas fa-minus"></i>
                                                    </button>
                                                </form>

                                                <button type="button" class="btn btn-outline-primary btn-sm"
                                                        onclick="showAdjustModal(${combo.comboID}, '${combo.productName}', ${combo.comboQuantity})"
                                                        title="Điều chỉnh số lượng">
                                                    <i class="fas fa-sliders-h"></i>
                                                </button>

                                                <form method="post" action="<%= request.getContextPath() %>/admin/combos" style="display: inline;">
                                                    <input type="hidden" name="action" value="adjust">
                                                    <input type="hidden" name="comboID" value="${combo.comboID}">
                                                    <input type="hidden" name="delta" value="1">
                                                    <button type="submit" class="btn btn-outline-success btn-sm" title="Tăng 1"
                                                            onclick="return confirm('Tăng 1 combo? Stock SP con sẽ bị trừ.')">
                                                        <i class="fas fa-plus"></i>
                                                    </button>
                                                </form>

                                                <a href="<%= request.getContextPath() %>/admin/combos?action=delete&id=${combo.comboID}"
                                                   class="btn btn-danger btn-sm ml-1"
                                                   onclick="return confirm('Xoá combo này? Tất cả stock sẽ được trả lại cho SP con.')"
                                                   title="Xoá combo">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty combos}">
                                        <tr>
                                            <td colspan="7" class="text-center py-4">
                                                <i class="fas fa-boxes fa-3x text-muted mb-3"></i>
                                                <p class="text-muted">Chưa có combo nào. <a href="<%= request.getContextPath() %>/admin/combos?action=add">Tạo combo mới</a></p>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <jsp:include page="include/admin-footer.jsp" />
</div>

<!-- Adjust Quantity Modal -->
<div class="modal fade" id="adjustModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary">
                <h4 class="modal-title"><i class="fas fa-sliders-h"></i> Điều chỉnh số lượng Combo</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <form method="post" action="<%= request.getContextPath() %>/admin/combos">
                <input type="hidden" name="action" value="adjust">
                <input type="hidden" name="comboID" id="adjustComboID">
                <div class="modal-body">
                    <p>Combo: <strong id="adjustComboName"></strong></p>
                    <p>Số lượng hiện tại: <span class="badge badge-info" id="adjustCurrentQty"></span></p>
                    <div class="form-group">
                        <label for="adjustDelta">Thay đổi (+ tăng, - giảm):</label>
                        <input type="number" class="form-control" id="adjustDelta" name="delta" value="1" required>
                        <small class="form-text text-muted">
                            <i class="fas fa-info-circle"></i> Số dương = tăng combo (trừ stock SP con). Số âm = giảm combo (trả stock SP con).
                        </small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-check"></i> Xác nhận</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

<script>
function showAdjustModal(comboID, comboName, currentQty) {
    $('#adjustComboID').val(comboID);
    $('#adjustComboName').text(comboName);
    $('#adjustCurrentQty').text(currentQty);
    $('#adjustDelta').val(1);
    $('#adjustModal').modal('show');
}

setTimeout(function() {
    $('.alert').fadeOut('slow');
}, 3000);
</script>

</body>
</html>
