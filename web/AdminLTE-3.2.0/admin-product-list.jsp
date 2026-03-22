<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý Sản phẩm - Admin</title>
    
    <!-- Google Font: Source Sans Pro -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    
    <style>
        .product-image {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
        }
        .stock-badge {
            font-size: 0.85rem;
        }
        .price-info {
            font-size: 0.9rem;
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
                        <h1><i class="fas fa-box"></i> Quản lý Sản phẩm</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/AdminLTE-3.2.0/index.jsp">Home</a></li>
                            <li class="breadcrumb-item active">Sản phẩm</li>
                        </ol>
                    </div>
                </div>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="container-fluid">
                
                <!-- Success/Error Messages -->
                <c:if test="${param.msg == 'add_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Thêm sản phẩm thành công!
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'update_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Cập nhật sản phẩm thành công!
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'delete_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Ẩn sản phẩm thành công!
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'activate_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Kích hoạt sản phẩm thành công!
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'deactivate_success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-check"></i> Ẩn sản phẩm thành công!
                    </div>
                </c:if>

                <!-- Main Card -->
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">Danh sách Sản phẩm</h3>
                        <div class="card-tools">
                            <a href="<%= request.getContextPath() %>/admin/products?action=add" class="btn btn-primary btn-sm">
                                <i class="fas fa-plus"></i> Thêm Sản phẩm Mới
                            </a>
                        </div>
                    </div>
                    
                    <!-- Card Body -->
                    <div class="card-body">
                        <!-- Filter Form -->
                        <form method="get" action="<%= request.getContextPath() %>/admin/products" class="mb-3">
                            <div class="row">
                                <div class="col-md-3">
                                    <input type="text" name="search" value="${search}" class="form-control" 
                                           placeholder="Tìm theo tên, SKU...">
                                </div>
                                <div class="col-md-2">
                                    <select name="categoryId" class="form-control">
                                        <option value="">-- Danh mục --</option>
                                        <c:forEach var="cat" items="${categories}">
                                            <option value="${cat.categoryID}" ${categoryId == cat.categoryID ? 'selected' : ''}>
                                                ${cat.categoryName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <select name="brandId" class="form-control">
                                        <option value="">-- Thương hiệu --</option>
                                        <c:forEach var="brand" items="${brands}">
                                            <option value="${brand.brandID}" ${brandId == brand.brandID ? 'selected' : ''}>
                                                ${brand.brandName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <select name="status" class="form-control">
                                        <option value="">-- Trạng thái --</option>
                                        <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                                        <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                    </select>
                                </div>
                                <div class="col-md-1">
                                    <select name="pageSize" class="form-control" onchange="this.form.submit()">
                                        <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                        <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                        <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <button type="submit" class="btn btn-primary btn-block">
                                        <i class="fas fa-search"></i> Tìm
                                    </button>
                                </div>
                            </div>
                        </form>

                        <!-- Table -->
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead>
                                    <tr>
                                        <th style="width: 60px;">ID</th>
                                        <th style="width: 100px;">Ảnh</th>
                                        <th>Tên Sản phẩm</th>
                                        <th style="width: 100px;">SKU</th>
                                        <th style="width: 120px;">Giá bán</th>
                                        <th style="width: 80px;">Tồn kho</th>
                                        <th style="width: 100px;">Trạng thái</th>
                                        <th style="width: 150px;" class="text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td><strong>#${product.id}</strong></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty product.imageURL}">
                                                        <img src="${product.imageURL}" alt="${product.productName}" 
                                                             class="product-image">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="text-center text-muted">
                                                            <i class="fas fa-image fa-3x"></i>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <strong>${product.productName}</strong>
                                                <c:if test="${product.isCombo}">
                                                    <span class="badge badge-info ml-1">COMBO</span>
                                                </c:if>
                                                <c:if test="${not empty product.description}">
                                                    <br><small class="text-muted">${product.description.length() > 50 ? product.description.substring(0, 50).concat('...') : product.description}</small>
                                                </c:if>
                                            </td>
                                            <td><code>${product.sku}</code></td>
                                            <td class="price-info">
                                                <strong class="text-success">
                                                    <fmt:formatNumber value="${product.sellingPrice}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                </strong>
                                                <c:if test="${product.compareAtPrice != null && product.compareAtPrice > product.sellingPrice}">
                                                    <br><small class="text-muted"><del><fmt:formatNumber value="${product.compareAtPrice}" type="currency" currencySymbol="₫" groupingUsed="true"/></del></small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.stock <= 0}">
                                                        <span class="badge badge-danger stock-badge">
                                                            <i class="fas fa-times-circle"></i> Hết hàng
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${product.stock <= product.reorderLevel}">
                                                        <span class="badge badge-warning stock-badge">
                                                            <i class="fas fa-exclamation-triangle"></i> ${product.stock}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-success stock-badge">
                                                            <i class="fas fa-check-circle"></i> ${product.stock}
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${product.isActive}">
                                                        <span class="badge badge-success">
                                                            <i class="fas fa-check-circle"></i> Active
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-danger">
                                                            <i class="fas fa-times-circle"></i> Inactive
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <a href="<%= request.getContextPath() %>/admin/products?action=edit&id=${product.id}" 
                                                   class="btn btn-warning btn-sm" title="Chỉnh sửa">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                
                                                <c:choose>
                                                    <c:when test="${product.isActive}">
                                                        <button type="button" class="btn btn-danger btn-sm" 
                                                                onclick="confirmToggle(${product.id}, '${product.productName}', false)" 
                                                                title="Ẩn">
                                                            <i class="fas fa-eye-slash"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="btn btn-success btn-sm" 
                                                                onclick="confirmToggle(${product.id}, '${product.productName}', true)" 
                                                                title="Kích hoạt">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    
                                    <c:if test="${empty products}">
                                        <tr>
                                            <td colspan="8" class="text-center">
                                                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                <p class="text-muted">Không có sản phẩm nào.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalRecords > 0}">
                            <div class="row mt-3">
                                <div class="col-sm-12 col-md-5">
                                    <div class="dataTables_info" role="status" aria-live="polite">
                                        Hiển thị <strong>${(currentPage-1)*pageSize + 1}</strong> đến 
                                        <strong>${currentPage*pageSize > totalRecords ? totalRecords : currentPage*pageSize}</strong> 
                                        của <strong>${totalRecords}</strong> bản ghi
                                    </div>
                                </div>
                                <div class="col-sm-12 col-md-7">
                                    <div class="dataTables_paginate paging_simple_numbers float-right">
                                        <ul class="pagination">
                                            <li class="paginate_button page-item previous ${currentPage == 1 ? 'disabled' : ''}">
                                                <a href="?page=${currentPage - 1}&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                   class="page-link">Trước</a>
                                            </li>
                                            
                                            <c:choose>
                                                <c:when test="${totalPages <= 7}">
                                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                                        <li class="paginate_button page-item ${currentPage == i ? 'active' : ''}">
                                                            <a href="?page=${i}&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                               class="page-link">${i}</a>
                                                        </li>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${currentPage > 3}">
                                                        <li class="paginate_button page-item">
                                                            <a href="?page=1&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                               class="page-link">1</a>
                                                        </li>
                                                        <c:if test="${currentPage > 4}">
                                                            <li class="paginate_button page-item disabled">
                                                                <span class="page-link">...</span>
                                                            </li>
                                                        </c:if>
                                                    </c:if>
                                                    
                                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                                        <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                                                            <li class="paginate_button page-item ${currentPage == i ? 'active' : ''}">
                                                                <a href="?page=${i}&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                                   class="page-link">${i}</a>
                                                            </li>
                                                        </c:if>
                                                    </c:forEach>
                                                    
                                                    <c:if test="${currentPage < totalPages - 2}">
                                                        <c:if test="${currentPage < totalPages - 3}">
                                                            <li class="paginate_button page-item disabled">
                                                                <span class="page-link">...</span>
                                                            </li>
                                                        </c:if>
                                                        <li class="paginate_button page-item">
                                                            <a href="?page=${totalPages}&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                               class="page-link">${totalPages}</a>
                                                        </li>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <li class="paginate_button page-item next ${currentPage >= totalPages ? 'disabled' : ''}">
                                                <a href="?page=${currentPage + 1}&search=${search}&status=${status}&categoryId=${categoryId}&brandId=${brandId}&pageSize=${pageSize}" 
                                                   class="page-link">Sau</a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>
    </div>
    
    <!-- Footer -->
    <jsp:include page="include/admin-footer.jsp" />
    
</div>

<!-- Toggle Status Confirmation Modal -->
<div class="modal fade" id="toggleModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header" id="modalHeader">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <p id="modalMessage"></p>
                <p class="text-warning"><i class="fas fa-info-circle"></i> Bạn có thể thay đổi lại sau!</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                <a href="#" id="confirmToggleBtn" class="btn"></a>
            </div>
        </div>
    </div>
</div>

<!-- jQuery -->
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<!-- Bootstrap 4 -->
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- AdminLTE App -->
<script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

<script>
function confirmToggle(id, name, activate) {
    if (activate) {
        $('#modalHeader').removeClass('bg-danger').addClass('bg-success');
        $('#modalTitle').html('<i class="fas fa-eye"></i> Xác nhận kích hoạt');
        $('#modalMessage').html('Bạn có chắc chắn muốn <strong>kích hoạt</strong> sản phẩm <strong>' + name + '</strong>?');
        $('#confirmToggleBtn').removeClass('btn-danger').addClass('btn-success').html('<i class="fas fa-eye"></i> Kích hoạt');
    } else {
        $('#modalHeader').removeClass('bg-success').addClass('bg-danger');
        $('#modalTitle').html('<i class="fas fa-eye-slash"></i> Xác nhận ẩn');
        $('#modalMessage').html('Bạn có chắc chắn muốn <strong>ẩn</strong> sản phẩm <strong>' + name + '</strong>?');
        $('#confirmToggleBtn').removeClass('btn-success').addClass('btn-danger').html('<i class="fas fa-eye-slash"></i> Ẩn');
    }
    
    $('#confirmToggleBtn').attr('href', '<%= request.getContextPath() %>/admin/products?action=toggle&id=' + id);
    $('#toggleModal').modal('show');
}

// Auto hide alerts after 3 seconds
setTimeout(function() {
    $('.alert').fadeOut('slow');
}, 3000);
</script>

</body>
</html>
