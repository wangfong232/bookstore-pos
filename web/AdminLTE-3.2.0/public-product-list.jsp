<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bookstore - Sản phẩm</title>

    <!-- Google Font -->
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <!-- FontAwesome -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
    <!-- AdminLTE -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">

    <style>
        * { box-sizing: border-box; }

        body {
            font-family: 'Poppins', sans-serif;
            background: #f0f2f5;
            color: #333;
        }

        /* ── Top Navbar ── */
        .shop-navbar {
            background: linear-gradient(135deg, #302b63, #24243e);
            padding: 0 30px;
            height: 64px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 1000;
            box-shadow: 0 2px 15px rgba(0,0,0,0.2);
        }
        .shop-navbar .brand {
            color: #fff;
            font-size: 1.5rem;
            font-weight: 700;
            text-decoration: none;
            display: flex; align-items: center; gap: 10px;
        }
        .shop-navbar .brand:hover { color: #c4b5fd; text-decoration: none; }
        .shop-navbar .brand i {
            background: linear-gradient(135deg, #667eea, #764ba2);
            padding: 8px 10px;
            border-radius: 10px;
        }
        .nav-links { display: flex; gap: 15px; align-items: center; }
        .nav-links a {
            color: rgba(255,255,255,0.8);
            text-decoration: none;
            font-size: 0.9rem;
            font-weight: 500;
            padding: 8px 18px;
            border-radius: 25px;
            transition: all 0.3s;
        }
        .nav-links a:hover {
            background: rgba(255,255,255,0.15);
            color: #fff;
            text-decoration: none;
        }
        .nav-links .btn-login-nav {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
        }
        .nav-links .btn-login-nav:hover {
            box-shadow: 0 4px 15px rgba(102,126,234,0.5);
        }

        /* ── Layout ── */
        .shop-layout {
            display: flex;
            max-width: 1400px;
            margin: 0 auto;
            padding: 25px 20px;
            gap: 25px;
        }

        /* ── Sidebar Filter ── */
        .filter-sidebar {
            width: 280px;
            flex-shrink: 0;
        }
        .filter-card {
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 2px 20px rgba(0,0,0,0.06);
            padding: 24px;
            margin-bottom: 20px;
            border: 1px solid rgba(0,0,0,0.04);
        }
        .filter-card h5 {
            font-size: 0.95rem;
            font-weight: 700;
            color: #302b63;
            margin-bottom: 16px;
            padding-bottom: 10px;
            border-bottom: 2px solid #f0f0f0;
            display: flex; align-items: center; gap: 8px;
        }
        .filter-card h5 i {
            color: #764ba2;
        }

        .search-box {
            position: relative;
        }
        .search-box input {
            width: 100%;
            padding: 10px 14px 10px 40px;
            border: 2px solid #e8e8e8;
            border-radius: 12px;
            font-size: 0.9rem;
            font-family: 'Poppins', sans-serif;
            transition: border-color 0.3s;
            outline: none;
        }
        .search-box input:focus {
            border-color: #764ba2;
        }
        .search-box .search-icon {
            position: absolute;
            left: 14px;
            top: 50%;
            transform: translateY(-50%);
            color: #aaa;
        }

        .filter-list {
            list-style: none;
            padding: 0;
            margin: 0;
            max-height: 280px;
            overflow-y: auto;
        }
        .filter-list::-webkit-scrollbar { width: 4px; }
        .filter-list::-webkit-scrollbar-thumb { background: #ddd; border-radius: 4px; }

        .filter-list li {
            margin-bottom: 2px;
        }
        .filter-list li a {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 9px 14px;
            color: #555;
            text-decoration: none;
            border-radius: 10px;
            font-size: 0.88rem;
            transition: all 0.2s;
        }
        .filter-list li a:hover {
            background: #f5f3ff;
            color: #764ba2;
            text-decoration: none;
        }
        .filter-list li a.active-filter {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            font-weight: 600;
        }
        .filter-count {
            background: rgba(0,0,0,0.06);
            padding: 2px 8px;
            border-radius: 20px;
            font-size: 0.75rem;
        }
        .active-filter .filter-count {
            background: rgba(255,255,255,0.25);
        }

        .btn-clear-filter {
            display: inline-block;
            width: 100%;
            padding: 10px;
            background: #f8f8f8;
            color: #888;
            border: 2px dashed #ddd;
            border-radius: 12px;
            text-align: center;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 500;
            transition: all 0.3s;
            font-family: 'Poppins', sans-serif;
            cursor: pointer;
            margin-top: 10px;
        }
        .btn-clear-filter:hover {
            background: #fff0f0;
            border-color: #ff6b6b;
            color: #ff6b6b;
            text-decoration: none;
        }

        /* ── Product Grid ── */
        .product-main {
            flex: 1;
            min-width: 0;
        }

        .results-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 10px;
        }
        .results-count {
            font-size: 0.9rem;
            color: #666;
        }
        .results-count strong { color: #302b63; }

        .active-filters-tags {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
        .filter-tag {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: #f5f3ff;
            color: #764ba2;
            padding: 4px 14px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        .filter-tag a {
            color: #764ba2;
            text-decoration: none;
            font-weight: 700;
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            gap: 22px;
        }

        .product-card {
            background: #fff;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 15px rgba(0,0,0,0.05);
            transition: transform 0.3s, box-shadow 0.3s;
            border: 1px solid rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
        }
        .product-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 12px 35px rgba(0,0,0,0.12);
        }

        .product-img-wrap {
            position: relative;
            width: 100%;
            padding-top: 100%;
            background: #f8f8f8;
            overflow: hidden;
        }
        .product-img-wrap img {
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 100%;
            object-fit: cover;
            transition: transform 0.4s;
        }
        .product-card:hover .product-img-wrap img {
            transform: scale(1.08);
        }
        .product-img-placeholder {
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ccc;
            font-size: 3rem;
        }

        .sale-badge {
            position: absolute;
            top: 12px; left: 12px;
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: #fff;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 700;
        }

        .stock-overlay {
            position: absolute;
            bottom: 0; left: 0; right: 0;
            padding: 8px 14px;
            background: linear-gradient(transparent, rgba(0,0,0,0.6));
            display: flex;
            justify-content: flex-end;
        }
        .stock-tag {
            padding: 3px 10px;
            border-radius: 15px;
            font-size: 0.72rem;
            font-weight: 600;
            backdrop-filter: blur(4px);
        }
        .stock-in    { background: rgba(40,167,69,0.85); color: #fff; }
        .stock-low   { background: rgba(255,193,7,0.9); color: #333; }
        .stock-out   { background: rgba(220,53,69,0.85); color: #fff; }

        .product-info {
            padding: 16px 18px 20px;
            display: flex;
            flex-direction: column;
            flex: 1;
        }
        .product-name {
            font-size: 0.95rem;
            font-weight: 600;
            color: #2d2d2d;
            margin-bottom: 8px;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .product-price-row {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: auto;
        }
        .product-price {
            font-size: 1.1rem;
            font-weight: 700;
            color: #764ba2;
        }
        .product-old-price {
            font-size: 0.85rem;
            color: #aaa;
            text-decoration: line-through;
        }
        .product-stock-text {
            font-size: 0.78rem;
            color: #999;
            margin-top: 6px;
        }

        /* ── Pagination ── */
        .pagination-wrap {
            display: flex;
            justify-content: center;
            margin-top: 35px;
        }
        .pagination-wrap .pagination {
            gap: 4px;
        }
        .pagination-wrap .page-link {
            border: none;
            border-radius: 10px !important;
            padding: 8px 16px;
            font-size: 0.9rem;
            color: #555;
            font-weight: 500;
            font-family: 'Poppins', sans-serif;
            transition: all 0.2s;
        }
        .pagination-wrap .page-link:hover {
            background: #f5f3ff;
            color: #764ba2;
        }
        .pagination-wrap .page-item.active .page-link {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            box-shadow: 0 4px 12px rgba(102,126,234,0.35);
        }
        .pagination-wrap .page-item.disabled .page-link {
            color: #ccc;
        }

        /* ── Empty State ── */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #aaa;
        }
        .empty-state i { font-size: 4rem; margin-bottom: 20px; color: #ddd; }
        .empty-state p { font-size: 1.1rem; }

        /* ── Responsive ── */
        @media (max-width: 992px) {
            .shop-layout { flex-direction: column; }
            .filter-sidebar { width: 100%; }
            .filter-card { margin-bottom: 15px; }
            .product-grid { grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 15px; }
        }
        @media (max-width: 576px) {
            .shop-navbar { padding: 0 15px; }
            .product-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
            .product-info { padding: 12px; }
            .product-name { font-size: 0.85rem; }
            .product-price { font-size: 0.95rem; }
        }
    </style>
</head>
<body>

<!-- ═══════ TOP NAVBAR ═══════ -->
<nav class="shop-navbar">
    <a href="${pageContext.request.contextPath}/home" class="brand">
        <i class="fas fa-book-open"></i> Bookstore
    </a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/products">
            <i class="fas fa-shopping-bag"></i> Sản phẩm
        </a>
        <a href="${pageContext.request.contextPath}/login" class="btn-login-nav">
            <i class="fas fa-sign-in-alt"></i> Đăng nhập
        </a>
    </div>
</nav>

<!-- ═══════ MAIN LAYOUT ═══════ -->
<div class="shop-layout">

    <!-- ─── Sidebar Filter ─── -->
    <aside class="filter-sidebar">

        <!-- Search -->
        <div class="filter-card">
            <h5><i class="fas fa-search"></i> Tìm kiếm</h5>
            <form method="get" action="${pageContext.request.contextPath}/products" id="searchForm">
                <input type="hidden" name="categoryId" value="${categoryId}"/>
                <input type="hidden" name="brandId" value="${brandId}"/>
                <input type="hidden" name="stockStatus" value="${stockStatus}"/>
                <div class="search-box">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" name="search" value="${search}" placeholder="Tìm tên sản phẩm..."
                           onkeydown="if(event.key==='Enter') this.form.submit();">
                </div>
            </form>
        </div>

        <!-- Category Filter -->
        <div class="filter-card">
            <h5><i class="fas fa-th-list"></i> Danh mục</h5>
            <ul class="filter-list">
                <li>
                    <a href="${pageContext.request.contextPath}/products?search=${search}&brandId=${brandId}&stockStatus=${stockStatus}"
                       class="${empty categoryId ? 'active-filter' : ''}">
                        <span>Tất cả danh mục</span>
                    </a>
                </li>
                <c:forEach var="cat" items="${categories}">
                    <li>
                        <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${cat.categoryID}&brandId=${brandId}&stockStatus=${stockStatus}"
                           class="${categoryId == cat.categoryID ? 'active-filter' : ''}">
                            <span>
                                <c:if test="${not empty cat.icon}"><i class="${cat.icon}"></i> </c:if>
                                ${cat.categoryName}
                            </span>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- Brand Filter -->
        <div class="filter-card">
            <h5><i class="fas fa-bookmark"></i> Thương hiệu</h5>
            <ul class="filter-list">
                <li>
                    <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&stockStatus=${stockStatus}"
                       class="${empty brandId ? 'active-filter' : ''}">
                        <span>Tất cả thương hiệu</span>
                    </a>
                </li>
                <c:forEach var="brand" items="${brands}">
                    <li>
                        <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&brandId=${brand.brandID}&stockStatus=${stockStatus}"
                           class="${brandId == brand.brandID ? 'active-filter' : ''}">
                            <span>${brand.brandName}</span>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- Stock Status Filter -->
        <div class="filter-card">
            <h5><i class="fas fa-boxes"></i> Tình trạng kho</h5>
            <ul class="filter-list">
                <li>
                    <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&brandId=${brandId}"
                       class="${empty stockStatus ? 'active-filter' : ''}">
                        <span>Tất cả</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=in_stock"
                       class="${stockStatus == 'in_stock' ? 'active-filter' : ''}">
                        <span><i class="fas fa-check-circle text-success"></i> Còn hàng</span>
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=out_of_stock"
                       class="${stockStatus == 'out_of_stock' ? 'active-filter' : ''}">
                        <span><i class="fas fa-times-circle text-danger"></i> Hết hàng</span>
                    </a>
                </li>
            </ul>
        </div>

        <!-- Clear filters -->
        <a href="${pageContext.request.contextPath}/products" class="btn-clear-filter">
            <i class="fas fa-times"></i> Xoá tất cả bộ lọc
        </a>
    </aside>

    <!-- ─── Product Main ─── -->
    <main class="product-main">

        <!-- Results bar -->
        <div class="results-bar">
            <div class="results-count">
                Tìm thấy <strong>${totalRecords}</strong> sản phẩm
                <c:if test="${currentPage > 1}">
                    — Trang <strong>${currentPage}</strong>/<strong>${totalPages}</strong>
                </c:if>
            </div>
            <div class="active-filters-tags">
                <c:if test="${not empty search}">
                    <span class="filter-tag">
                        <i class="fas fa-search"></i> "${search}"
                        <a href="${pageContext.request.contextPath}/products?categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}">×</a>
                    </span>
                </c:if>
                <c:if test="${not empty categoryId}">
                    <c:forEach var="cat" items="${categories}">
                        <c:if test="${categoryId == cat.categoryID}">
                            <span class="filter-tag">
                                <i class="fas fa-th-list"></i> ${cat.categoryName}
                                <a href="${pageContext.request.contextPath}/products?search=${search}&brandId=${brandId}&stockStatus=${stockStatus}">×</a>
                            </span>
                        </c:if>
                    </c:forEach>
                </c:if>
                <c:if test="${not empty brandId}">
                    <c:forEach var="brand" items="${brands}">
                        <c:if test="${brandId == brand.brandID}">
                            <span class="filter-tag">
                                <i class="fas fa-bookmark"></i> ${brand.brandName}
                                <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&stockStatus=${stockStatus}">×</a>
                            </span>
                        </c:if>
                    </c:forEach>
                </c:if>
                <c:if test="${not empty stockStatus}">
                    <span class="filter-tag">
                        <i class="fas fa-boxes"></i> ${stockStatus == 'in_stock' ? 'Còn hàng' : 'Hết hàng'}
                        <a href="${pageContext.request.contextPath}/products?search=${search}&categoryId=${categoryId}&brandId=${brandId}">×</a>
                    </span>
                </c:if>
            </div>
        </div>

        <!-- Product Grid -->
        <c:choose>
            <c:when test="${not empty products}">
                <div class="product-grid">
                    <c:forEach var="product" items="${products}">
                        <div class="product-card">
                            <!-- Image -->
                            <div class="product-img-wrap">
                                <c:choose>
                                    <c:when test="${not empty product.imageURL}">
                                        <img src="${product.imageURL}" alt="${product.productName}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="product-img-placeholder">
                                            <i class="fas fa-book"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <!-- Sale badge -->
                                <c:if test="${product.compareAtPrice != null && product.compareAtPrice > product.sellingPrice}">
                                    <div class="sale-badge">
                                        <fmt:formatNumber value="${(product.compareAtPrice - product.sellingPrice) / product.compareAtPrice * 100}" 
                                                          maxFractionDigits="0"/>% GIẢM
                                    </div>
                                </c:if>

                                <!-- Stock indicator -->
                                <div class="stock-overlay">
                                    <c:choose>
                                        <c:when test="${product.stock <= 0}">
                                            <span class="stock-tag stock-out">Hết hàng</span>
                                        </c:when>
                                        <c:when test="${product.stock <= product.reorderLevel}">
                                            <span class="stock-tag stock-low">Còn ${product.stock}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stock-tag stock-in">Còn ${product.stock}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Info -->
                            <div class="product-info">
                                <div class="product-name">${product.productName}</div>
                                <div class="product-price-row">
                                    <span class="product-price">
                                        <fmt:formatNumber value="${product.sellingPrice}" type="number" groupingUsed="true"/>₫
                                    </span>
                                    <c:if test="${product.compareAtPrice != null && product.compareAtPrice > product.sellingPrice}">
                                        <span class="product-old-price">
                                            <fmt:formatNumber value="${product.compareAtPrice}" type="number" groupingUsed="true"/>₫
                                        </span>
                                    </c:if>
                                </div>
                                <div class="product-stock-text">
                                    <i class="fas fa-box"></i> Kho: ${product.stock} sản phẩm
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- ─── Pagination ─── -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination-wrap">
                        <ul class="pagination">
                            <!-- Previous -->
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/products?page=${currentPage - 1}&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>

                            <c:choose>
                                <c:when test="${totalPages <= 7}">
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/products?page=${i}&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">${i}</a>
                                        </li>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${currentPage > 3}">
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/products?page=1&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">1</a>
                                        </li>
                                        <c:if test="${currentPage > 4}">
                                            <li class="page-item disabled"><span class="page-link">...</span></li>
                                        </c:if>
                                    </c:if>

                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link"
                                                   href="${pageContext.request.contextPath}/products?page=${i}&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">${i}</a>
                                            </li>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages - 2}">
                                        <c:if test="${currentPage < totalPages - 3}">
                                            <li class="page-item disabled"><span class="page-link">...</span></li>
                                        </c:if>
                                        <li class="page-item">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/products?page=${totalPages}&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">${totalPages}</a>
                                        </li>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>

                            <!-- Next -->
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link"
                                   href="${pageContext.request.contextPath}/products?page=${currentPage + 1}&search=${search}&categoryId=${categoryId}&brandId=${brandId}&stockStatus=${stockStatus}&pageSize=${pageSize}">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <i class="fas fa-search"></i>
                    <p>Không tìm thấy sản phẩm nào phù hợp.</p>
                    <a href="${pageContext.request.contextPath}/products" style="color: #764ba2; font-weight: 600;">
                        <i class="fas fa-arrow-left"></i> Xem tất cả sản phẩm
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<!-- jQuery -->
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
<!-- Bootstrap 4 -->
<script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>

</body>
</html>
