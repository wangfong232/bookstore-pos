<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${product != null ? 'Chỉnh sửa' : 'Thêm mới'} Sản phẩm - Admin</title>
    
    <!-- Google Font: Source Sans Pro -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    
    <style>
        .image-preview-container {
            position: relative;
            max-width: 300px;
            margin: 15px 0;
        }
        .image-preview {
            max-width: 100%;
            max-height: 300px;
            border: 2px dashed #dee2e6;
            border-radius: 8px;
            padding: 10px;
            display: none;
        }
        .image-preview.show {
            display: block;
        }
        .upload-placeholder {
            border: 2px dashed #dee2e6;
            border-radius: 8px;
            padding: 40px;
            text-align: center;
            background: #f8f9fa;
            cursor: pointer;
            transition: all 0.3s;
        }
        .upload-placeholder:hover {
            border-color: #007bff;
            background: #e7f3ff;
        }
        .upload-placeholder i {
            font-size: 3rem;
            color: #6c757d;
        }
        .remove-image-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 10;
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
                        <h1><i class="fas fa-box"></i> ${product != null ? 'Chỉnh sửa Sản phẩm' : 'Thêm Sản phẩm Mới'}</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/AdminLTE-3.2.0/index.jsp">Home</a></li>
                            <li class="breadcrumb-item"><a href="<%= request.getContextPath() %>/admin/products">Sản phẩm</a></li>
                            <li class="breadcrumb-item active">${product != null ? 'Chỉnh sửa' : 'Thêm mới'}</li>
                        </ol>
                    </div>
                </div>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="container-fluid">
                
                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <i class="icon fas fa-ban"></i> ${error}
                    </div>
                </c:if>
                
                <form method="post" action="<%= request.getContextPath() %>/admin/products" id="productForm" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="${product != null ? 'edit' : 'add'}">
                    <c:if test="${product != null}">
                        <input type="hidden" name="productId" value="${product.id}">
                    </c:if>
                    
                    <div class="row">
                        <!-- Left Column - Main Info -->
                        <div class="col-md-8">
                            <!-- Basic Information -->
                            <div class="card card-primary">
                                <div class="card-header">
                                    <h3 class="card-title">Thông tin cơ bản</h3>
                                </div>
                                
                                <div class="card-body">
                                    <!-- Product Name -->
                                    <div class="form-group">
                                        <label for="productName">Tên Sản phẩm <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="productName" name="productName" 
                                               value="${product != null ? product.productName : ''}" 
                                               placeholder="Nhập tên sản phẩm..." required>
                                    </div>

                                    <!-- SKU -->
                                    <div class="form-group">
                                        <label for="sku">Mã SKU <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="sku" name="sku" 
                                               value="${product != null ? product.sku : ''}" 
                                               placeholder="VD: PROD-001" required>
                                        <small class="form-text text-muted">Mã SKU phải duy nhất</small>
                                    </div>

                                    <!-- Category, Brand, Supplier -->
                                    <div class="row">
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="categoryId">Danh mục <span class="text-danger">*</span></label>
                                                <select class="form-control" id="categoryId" name="categoryId" required>
                                                    <option value="">-- Chọn danh mục --</option>
                                                    <c:forEach var="cat" items="${categories}">
                                                        <option value="${cat.categoryID}" 
                                                                ${product != null && product.categoryId == cat.categoryID ? 'selected' : ''}>
                                                            ${cat.categoryName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="brandId">Thương hiệu</label>
                                                <select class="form-control" id="brandId" name="brandId">
                                                    <option value="">-- Chọn thương hiệu --</option>
                                                    <c:forEach var="brand" items="${brands}">
                                                        <option value="${brand.brandID}" 
                                                                ${product != null && product.brandId == brand.brandID ? 'selected' : ''}>
                                                            ${brand.brandName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="form-group">
                                                <label for="supplierId">Nhà cung cấp</label>
                                                <select class="form-control" id="supplierId" name="supplierId">
                                                    <option value="">-- Chọn NCC --</option>
                                                    <c:forEach var="supplier" items="${suppliers}">
                                                        <option value="${supplier.id}" 
                                                                ${product != null && product.supplierId == supplier.id ? 'selected' : ''}>
                                                            ${supplier.supplierName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Description -->
                                    <div class="form-group">
                                        <label for="description">Mô tả</label>
                                        <textarea class="form-control" id="description" name="description" rows="3" 
                                                  placeholder="Nhập mô tả sản phẩm...">${product != null ? product.description : ''}</textarea>
                                    </div>

                                    <!-- Specifications -->
                                    <div class="form-group">
                                        <label for="specifications">Thông số kỹ thuật</label>
                                        <textarea class="form-control" id="specifications" name="specifications" rows="3" 
                                                  placeholder="Nhập thông số kỹ thuật...">${product != null ? product.specifications : ''}</textarea>
                                    </div>
                                </div>
                                
                                <div class="card-footer">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save"></i> ${product != null ? 'Cập nhật' : 'Thêm mới'}
                                    </button>
                                    <a href="<%= request.getContextPath() %>/admin/products" class="btn btn-default">
                                        <i class="fas fa-times"></i> Hủy
                                    </a>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column - Image & Status -->
                        <div class="col-md-4">
                            <!-- Product Image -->
                            <div class="card card-info">
                                <div class="card-header">
                                    <h3 class="card-title">Hình ảnh sản phẩm</h3>
                                </div>
                                
                                <div class="card-body">
                                    <!-- Upload Placeholder -->
                                    <div class="upload-placeholder" id="uploadPlaceholder" onclick="document.getElementById('imageFile').click()">
                                        <i class="fas fa-cloud-upload-alt"></i>
                                        <p class="mt-2 mb-0">Click để chọn ảnh</p>
                                        <small class="text-muted">JPG, PNG, GIF (Max 10MB)</small>
                                    </div>
                                    
                                    <!-- Hidden File Input -->
                                    <input type="file" id="imageFile" name="imageFile" accept="image/*" style="display: none;" onchange="previewImage(this)">
                                    
                                    <!-- Image Preview -->
                                    <div class="image-preview-container">
                                        <img id="imagePreview" 
                                             class="image-preview ${product != null && product.imageURL != null ? 'show' : ''}" 
                                             src="${product != null ? product.imageURL : ''}" 
                                             alt="Preview">
                                        <button type="button" class="btn btn-danger btn-sm remove-image-btn" 
                                                id="removeImageBtn" onclick="removeImage()" 
                                                style="display: ${product != null && product.imageURL != null ? 'block' : 'none'};">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                    
                                    <small class="form-text text-muted">
                                        <i class="fas fa-info-circle"></i> Tải lên ảnh từ thiết bị của bạn
                                    </small>
                                </div>
                            </div>

                            <!-- Status -->
                            <div class="card card-secondary">
                                <div class="card-header">
                                    <h3 class="card-title">Trạng thái</h3>
                                </div>
                                
                                <div class="card-body">
                                    <div class="form-group">
                                        <div class="custom-control custom-switch">
                                            <input type="checkbox" class="custom-control-input" id="isActive" name="isActive" 
                                                   ${product == null || product.isActive ? 'checked' : ''}>
                                            <label class="custom-control-label" for="isActive">Kích hoạt sản phẩm</label>
                                        </div>
                                        <small class="form-text text-muted">Bật để hiển thị sản phẩm trên website</small>
                                    </div>
                                </div>
                            </div>

                            <!-- Product Info (if editing) -->
                            <c:if test="${product != null}">
                                <div class="card card-default">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-database"></i> Thông tin</h3>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>ID:</strong> #${product.id}</p>
                                        <p><strong>Trạng thái:</strong> 
                                            <span class="badge ${product.isActive ? 'badge-success' : 'badge-danger'}">
                                                ${product.isActive ? 'Active' : 'Inactive'}
                                            </span>
                                        </p>
                                        <c:if test="${product.createdDate != null}">
                                            <p><strong>Ngày tạo:</strong><br>
                                                <small>${product.createdDate}</small>
                                            </p>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    </div>
    
    <script>
    // Preview image when selected
    function previewImage(input) {
        if (input.files && input.files[0]) {
            const file = input.files[0];
            
            // Validate file size (10MB)
            if (file.size > 10 * 1024 * 1024) {
                alert('Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 10MB.');
                input.value = '';
                return;
            }
            
            // Validate file type
            if (!file.type.match('image.*')) {
                alert('Vui lòng chọn file ảnh (JPG, PNG, GIF)!');
                input.value = '';
                return;
            }
            
            const reader = new FileReader();
            
            reader.onload = function(e) {
                document.getElementById('imagePreview').src = e.target.result;
                document.getElementById('imagePreview').classList.add('show');
                document.getElementById('uploadPlaceholder').style.display = 'none';
                document.getElementById('removeImageBtn').style.display = 'block';
            }
            
            reader.readAsDataURL(file);
        }
    }

    // Remove image
    function removeImage() {
        document.getElementById('imageFile').value = '';
        document.getElementById('imagePreview').classList.remove('show');
        document.getElementById('imagePreview').src = '';
        document.getElementById('uploadPlaceholder').style.display = 'block';
        document.getElementById('removeImageBtn').style.display = 'none';
    }

    // Form validation
    document.getElementById('productForm').addEventListener('submit', function(e) {
        const productName = document.getElementById('productName').value.trim();
        const sku = document.getElementById('sku').value.trim();
        const categoryId = document.getElementById('categoryId').value;
        
        if (!productName) {
            e.preventDefault();
            alert('Vui lòng nhập tên sản phẩm!');
            document.getElementById('productName').focus();
            return false;
        }
        
        if (!sku) {
            e.preventDefault();
            alert('Vui lòng nhập mã SKU!');
            document.getElementById('sku').focus();
            return false;
        }
        
        if (!categoryId) {
            e.preventDefault();
            alert('Vui lòng chọn danh mục!');
            document.getElementById('categoryId').focus();
            return false;
        }
        
        return true;
    });

    // Load preview on page load if editing
    window.addEventListener('DOMContentLoaded', function() {
        const existingImage = document.getElementById('imagePreview').src;
        if (existingImage && existingImage !== '' && existingImage !== window.location.href) {
            document.getElementById('uploadPlaceholder').style.display = 'none';
            document.getElementById('removeImageBtn').style.display = 'block';
        }
    });
    </script>

    <!-- Footer -->
    <jsp:include page="include/admin-footer.jsp" />
