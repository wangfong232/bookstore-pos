<%-- Main POS screen --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                <!DOCTYPE html>
                <html>

                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Bán hàng (POS)</title>
                    <link rel="stylesheet"
                        href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
                    <link rel="stylesheet"
                        href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
                    <style>
                        body.dark-pos {
                            background-color: #020617;
                            color: black;
                        }

                        .dark-pos .content-wrapper {
                            background: radial-gradient(circle at top left, #fcfcfc, #f6f2f7);
                            color: black;
                        }

                        .pos-no-sidebar .main-sidebar {
                            position: fixed;
                            top: 0;
                            left: 0;
                            height: 100vh;
                            transform: translateX(-100%);
                            transition: transform 0.3s ease;
                            z-index: 1050;
                        }

                        .pos-sidebar-open .main-sidebar {
                            transform: translateX(0);
                        }

                        .pos-wrapper-full .content-wrapper {
                            margin-left: 0 !important;
                        }

                        .pos-offcanvas-backdrop {
                            position: fixed;
                            inset: 0;
                            background: rgba(15, 23, 42, 0.6);
                            z-index: 1040;
                            opacity: 0;
                            visibility: hidden;
                            transition: opacity 0.3s ease, visibility 0.3s ease;
                        }

                        .pos-sidebar-open .pos-offcanvas-backdrop {
                            opacity: 1;
                            visibility: visible;
                        }

                        .dark-pos .card,
                        .dark-pos .card-header,
                        .dark-pos .card-body,
                        .dark-pos .card-footer {
                            background-color: transparent;
                            border: none;
                        }

                        .pos-search-bar {
                            background: white;
                            border-radius: 999px;
                            padding: 6px 16px;
                            display: flex;
                            align-items: center;
                            box-shadow: 0 0 0 1px #1e293b;
                        }

                        .pos-search-bar i {
                            color: #64748b;
                            margin-right: 8px;
                        }

                        .pos-search-bar input {
                            background: transparent;
                            border: none;
                            color: black;
                            width: 100%;
                            outline: none;
                        }

                        .pos-search-bar input::placeholder {
                            color: black;
                        }

                        .pos-tabs {
                            margin-top: 16px;
                            margin-bottom: 16px;
                            display: flex;
                            flex-wrap: wrap;
                            gap: 8px;
                        }

                        .pos-tab {
                            padding: 6px 14px;
                            border-radius: 999px;
                            background: #020617;
                            color: #ff0000;
                            font-size: 13px;
                            border: 1px solid #1e293b;
                            cursor: pointer;
                            transition: all 0.15s ease;
                            display: inline-block;
                            text-decoration: none;
                        }

                        .pos-tab:hover {
                            color: #111827;
                        }

                        .pos-tab.active,
                        .pos-tab:hover {
                            background: #fbbf24;
                            border-color: #f59e0b;
                            color: #111827;
                        }

                        .pos-products-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
                            gap: 14px;
                        }

                        .pos-product-card {
                            background: wheat;
                            border-radius: 16px;
                            padding: 14px 12px;
                            border: 1px solid #1f2937;
                            position: relative;
                            cursor: pointer;
                            transition: transform 0.12s ease, box-shadow 0.12s ease, border-color 0.12s ease;
                            display: flex;
                            flex-direction: column;
                            height: 100%;
                            min-height: 210px;
                        }

                        .pos-product-card:hover {
                            transform: translateY(-2px);
                            border-color: #fbbf24;
                            box-shadow: 0 14px 30px rgba(15, 23, 42, 0.85);
                        }

                        .pos-product-icon {
                            width: 36px;
                            height: 36px;
                            border-radius: 12px;
                            background: #dea600;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 18px;
                            margin-bottom: 10px;
                        }

                        .pos-product-name {
                            font-size: 20px;
                            font-weight: 500;
                            color: black;
                            margin-bottom: 4px;
                            min-height: 36px;
                        }

                        .pos-product-sku {
                            font-size: 15px;
                            color: black;
                            margin-bottom: 10px;
                        }

                        .pos-product-price-row {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-top: auto;
                        }

                        .pos-product-price {
                            font-weight: 600;
                            color: green;
                            font-size: 15px;
                        }

                        .pos-product-stock {
                            font-size: 11px;
                            color: white;
                        }

                        .pos-product-add-btn {
                            background: #f4dbff;
                            border-radius: 999px;
                            border: none;
                            color: #0b1120;
                            padding: 4px 10px;
                            font-size: 11px;
                            font-weight: 600;
                            display: inline-flex;
                            align-items: center;
                            gap: 4px;
                        }

                        .pos-product-add-btn i {
                            font-size: 10px;
                        }

                        .pos-order-panel {
                            background: white;
                            border-radius: 20px;
                            border: 1px solid #1f2937;
                            padding: 16px 16px 10px;
                            box-shadow: 0 8px 20px rgba(15, 23, 42, 0.6);
                        }

                        .pos-order-header {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-bottom: 10px;
                        }

                        .pos-order-title {
                            font-size: 15px;
                            font-weight: 600;
                        }

                        .pos-order-tag {
                            font-size: 11px;
                            padding: 4px 10px;
                            background: rgba(34, 197, 94, 0.15);
                            color: #4ade80;
                            border-radius: 999px;
                            border: 1px solid rgba(34, 197, 94, 0.4);
                        }

                        .pos-order-items {
                            max-height: 280px;
                            overflow-y: auto;
                            padding-right: 4px;
                            margin-bottom: 8px;
                        }

                        .pos-order-item {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            padding: 8px 0;
                            border-bottom: 1px solid #111827;
                        }

                        .pos-order-item:last-child {
                            border-bottom: none;
                        }

                        .pos-order-item-name {
                            font-size: 13px;
                            font-weight: 500;
                            color: #e5e7eb;
                        }

                        .pos-order-item-meta {
                            font-size: 11px;
                            color: #6b7280;
                        }

                        .pos-order-qty-input {
                            width: 60px;
                            background: #020617;
                            border-radius: 999px;
                            border: 1px solid #1f2937;
                            color: #e5e7eb;
                            padding: 2px 8px;
                            font-size: 12px;
                        }

                        .pos-chip-remove {
                            background: transparent;
                            border: none;
                            color: #f87171;
                            font-size: 12px;
                        }

                        .pos-order-summary-row {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            font-size: 12px;
                            color: black;
                            margin-bottom: 4px;
                        }

                        .pos-order-summary-row strong {
                            color: #e5e7eb;
                        }

                        .pos-total-amount {
                            font-size: 20px;
                            font-weight: 700;
                            color: #fbbf24;
                        }

                        .pos-pay-btn {
                            width: 100%;
                            border-radius: 999px;
                            padding: 10px 0;
                            font-weight: 600;
                            font-size: 14px;
                        }

                        .pos-cancel-btn {
                            border-radius: 999px;
                            padding: 8px 0;
                            font-size: 13px;
                            width: 100%;
                        }

                        .pos-note {
                            background: white;
                            border-radius: 12px;
                            border: 1px solid #1f2937;
                            color: black;
                            font-size: 12px;
                        }

                        .pos-empty-state {
                            text-align: center;
                            padding: 40px 10px;
                            color: #6b7280;
                            font-size: 13px;
                        }

                        .pos-empty-icon {
                            font-size: 30px;
                            margin-bottom: 8px;
                            color: #4b5563;
                        }

                        .pos-select {
                            background: white !important;
                            border: 1px solid #1e293b;
                            color: black;
                            border-radius: 8px;
                        }

                        .pos-payment-option {
                            flex: 1;
                            padding: 10px;
                            border-radius: 12px;
                            border: 2px solid #1e293b;
                            background: #e9ecf5;
                            color: #9ca3af;
                            cursor: pointer;
                            text-align: center;
                            transition: all 0.15s ease;
                        }

                        .pos-payment-option:hover {
                            border-color: #475569;
                            color: #e5e7eb;
                        }

                        .pos-payment-option.active {
                            border-color: #fbbf24;
                            background: rgba(251, 191, 36, 0.15);
                            color: #fbbf24;
                        }

                        .pos-payment-option input {
                            display: none;
                        }

                        @media (max-width: 991.98px) {
                            .pos-products-grid {
                                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                            }
                        }
                    </style>
                </head>

                <body class="hold-transition dark-pos pos-no-sidebar">
                    <div class="wrapper pos-wrapper-full">
                        <jsp:include page="include/admin-sidebar.jsp" />
                        <div class="pos-offcanvas-backdrop"></div>
                        <!-- Content Wrapper (full width, no sidebar) -->
                        <div class="content-wrapper">
                            <section class="content-header">
                                <div class="container-fluid">
                                    <div class="row mb-2">
                                        <div class="col-sm-6 d-flex align-items-center">
                                            <button type="button" id="pos-sidebar-toggle"
                                                class="btn btn-sm btn-outline-secondary mr-2">
                                                <i class="fas fa-bars"></i>
                                            </button>
                                            <h1 class="mb-0">Bán hàng (POS)</h1>
                                        </div>
                                        <div class="col-sm-6">
                                            <ol class="breadcrumb float-sm-right">
                                                <li class="breadcrumb-item"><a
                                                        href="${pageContext.request.contextPath}/dashboard">Home</a>
                                                </li>
                                                <li class="breadcrumb-item active">POS</li>
                                            </ol>
                                        </div>
                                    </div>
                                </div><!-- /.container-fluid -->
                            </section>

                            <!-- Main content -->
                            <section class="content">
                                <div class="container-fluid">
                                    <div class="row">
                                        <!-- Left column: search & product list -->
                                        <div class="col-lg-8 mb-3">
                                            <!-- Messages -->
                                            <c:if test="${not empty msg}">
                                                <div class="alert alert-success alert-dismissible fade show">
                                                    <i class="fas fa-check-circle"></i> ${msg}
                                                    <button type="button" class="close" data-dismiss="alert"
                                                        aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                            </c:if>
                                            <c:if test="${not empty error}">
                                                <div class="alert alert-danger alert-dismissible fade show">
                                                    <i class="fas fa-exclamation-triangle"></i> ${error}
                                                    <button type="button" class="close" data-dismiss="alert"
                                                        aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                            </c:if>

                                            <!-- Search bar -->
                                            <form action="<c:url value='/pos'/>" method="get" class="mb-3">
                                                <div class="pos-search-bar">
                                                    <i class="fas fa-search"></i>
                                                    <input type="text" name="key" value="${searchKey}"
                                                        placeholder="Tìm sản phẩm theo tên, SKU..." autocomplete="off">
                                                </div>
                                            </form>

                                            <!-- Category Filter -->
                                            <c:if test="${not empty categories}">
                                                <div class="mb-3">

                                                    <form id="pos-category-form" action="<c:url value='/pos'/>"
                                                        method="get" class="d-inline">

                                                        <input type="hidden" name="key" value="${searchKey}">

                                                        <label for="pos-category" class="mr-2 text-muted small">
                                                            Danh mục:
                                                        </label>

                                                        <select id="pos-category" name="categoryId"
                                                            class="pos-select form-control form-control-sm d-inline-block"
                                                            style="width: auto; min-width: 180px;">

                                                            <option value="">Tất cả</option>

                                                            <c:forEach var="cat" items="${categories}">
                                                                <option value="${cat.categoryID}" ${selectedCategoryId
                                                                    !=null && selectedCategoryId==cat.categoryID
                                                                    ? 'selected' : '' }>
                                                                    ${cat.categoryName}
                                                                </option>
                                                            </c:forEach>

                                                        </select>

                                                    </form>

                                                </div>
                                            </c:if>


                                            <!-- Quick add by SKU -->
                                            <div class="mb-3">

                                                <form action="<c:url value='/pos'/>" method="post" class="form-inline">

                                                    <input type="hidden" name="action" value="addItem">

                                                    <div class="form-group mr-3">

                                                        <label for="sku" class="mr-2 text-sm">
                                                            SKU:
                                                        </label>

                                                        <input type="text" class="form-control form-control-sm" id="sku"
                                                            name="sku" placeholder="Quét hoặc nhập SKU" autofocus>
                                                    </div>

                                                    <div class="form-group mr-3">

                                                        <label for="quantity" class="mr-2 text-sm">
                                                            SL:
                                                        </label>

                                                        <input type="number" min="1"
                                                            class="form-control form-control-sm" name="quantity"
                                                            value="1" style="width:80px;">

                                                    </div>

                                                    <button type="submit" class="btn btn-sm btn-outline-info">

                                                        <i class="fas fa-plus"></i>
                                                        Thêm

                                                    </button>

                                                </form>

                                            </div>


                                            <!-- Product Grid -->
                                            <c:if test="${not empty products}">

                                                <div class="pos-products-grid">

                                                    <c:forEach var="p" items="${products}">

                                                        <form action="<c:url value='/pos'/>" method="post">

                                                            <input type="hidden" name="action" value="addItem">
                                                            <input type="hidden" name="sku" value="${p.sku}">
                                                            <input type="hidden" name="quantity" value="1">

                                                            <button type="submit" class="pos-product-card"
                                                                style="width:100%; text-align:left;">

                                                                <div
                                                                    class="d-flex justify-content-between align-items-start">

                                                                    <div class="pos-product-icon">
                                                                        <i class="fas fa-book"></i>
                                                                    </div>

                                                                    <span
                                                                        class="badge ${p.stock > 10 ? 'badge-success' : 'badge-warning'}"
                                                                        style="border-radius:999px;font-size:10px; border:1px solid #1f2937;">

                                                                        <i class="fas fa-box-open"></i>
                                                                        Tồn: ${p.stock}

                                                                    </span>

                                                                </div>

                                                                <div class="pos-product-name">
                                                                    ${p.productName}
                                                                </div>

                                                                <div class="pos-product-sku">
                                                                    SKU: ${p.sku}
                                                                </div>

                                                                <div class="pos-product-price-row">

                                                                    <div class="pos-product-price">
                                                                        <fmt:formatNumber value="${p.sellingPrice}"
                                                                            type="number" maxFractionDigits="0" /> đ
                                                                    </div>

                                                                    <span class="pos-product-add-btn">
                                                                        <i class="fas fa-plus"></i>
                                                                        Thêm
                                                                    </span>

                                                                </div>

                                                            </button>

                                                        </form>

                                                    </c:forEach>

                                                </div>

                                            </c:if>

                                            <!-- Pagination -->
                                            <c:if test="${totalPages > 1}">
                                                <nav aria-label="POS product pagination" class="mt-3">
                                                    <ul class="pagination pagination-sm justify-content-center mb-0">
                                                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                                            <a class="page-link"
                                                                href="${pageContext.request.contextPath}/pos?page=${currentPage - 1}&key=${searchKey}&categoryId=${selectedCategoryId}">
                                                                Trước
                                                            </a>
                                                        </li>
                                                        <c:forEach begin="1" end="${totalPages}" var="pNo">
                                                            <li class="page-item ${pNo == currentPage ? 'active' : ''}">
                                                                <a class="page-link"
                                                                    href="${pageContext.request.contextPath}/pos?page=${pNo}&key=${searchKey}&categoryId=${selectedCategoryId}">
                                                                    ${pNo}
                                                                </a>
                                                            </li>
                                                        </c:forEach>
                                                        <li
                                                            class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                                            <a class="page-link"
                                                                href="${pageContext.request.contextPath}/pos?page=${currentPage + 1}&key=${searchKey}&categoryId=${selectedCategoryId}">
                                                                Sau
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </nav>
                                            </c:if>


                                            <!-- Empty State -->
                                            <c:if test="${empty products}">
                                                <div class="pos-empty-state">

                                                    <div class="pos-empty-icon">
                                                        <i class="fas fa-book-open"></i>
                                                    </div>

                                                    Nhập từ khóa hoặc SKU để tìm và thêm sản phẩm vào đơn hàng.

                                                </div>
                                            </c:if>

                                        </div>
                                        <!-- Right column: cart & payment -->
                                        <div class="col-lg-4">
                                            <div class="pos-order-panel">
                                                <div class="pos-order-header">
                                                    <div>
                                                        <div class="pos-order-title">
                                                            Đơn hàng
                                                        </div>
                                                        <small class="text-muted">Nhân viên: Cashier</small>
                                                    </div>
                                                    <div class="pos-order-tag">
                                                        Ca đang mở
                                                    </div>
                                                </div>
                                                <div class="card-body p-0">
                                                    <c:if test="${not empty cart}">
                                                        <div class="table-responsive" style="max-height: 360px;">
                                                            <table class="table table-striped table-hover mb-0">
                                                                <thead>
                                                                    <tr>
                                                                        <th style="font-size: 11px;">Sản phẩm</th>
                                                                        <th class="text-center"
                                                                            style="width: 70px; font-size: 11px;">SL
                                                                        </th>
                                                                        <th class="text-right"
                                                                            style="width: 90px; font-size: 11px;">Thành
                                                                            tiền</th>
                                                                        <th class="text-center" style="width: 40px;">
                                                                        </th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <c:forEach var="item" items="${cart}">
                                                                        <tr>
                                                                            <td>
                                                                                <div class="text-truncate"
                                                                                    style="max-width: 120px; font-size: 12px;"
                                                                                    title="${item.product.productName}">
                                                                                    <strong>${item.product.productName}</strong>
                                                                                </div>
                                                                                <small class="text-muted">SKU:
                                                                                    ${item.product.sku}</small>
                                                                            </td>
                                                                            <td class="text-center">
                                                                                <form action="<c:url value='/pos'/>"
                                                                                    method="post">
                                                                                    <input type="hidden" name="action"
                                                                                        value="updateQty">
                                                                                    <input type="hidden"
                                                                                        name="productId"
                                                                                        value="${item.product.productID}">
                                                                                    <input type="number" name="quantity"
                                                                                        value="${item.quantity}" min="1"
                                                                                        class="form-control form-control-sm text-center"
                                                                                        style="width: 50px; font-size: 11px;"
                                                                                        onchange="this.form.submit()">
                                                                                </form>
                                                                            </td>
                                                                            <td class="text-right"
                                                                                style="font-size: 12px;">
                                                                                <fmt:formatNumber
                                                                                    value="${item.lineTotal}"
                                                                                    type="number"
                                                                                    maxFractionDigits="0" /> đ
                                                                            </td>
                                                                            <td class="text-center">
                                                                                <form action="<c:url value='/pos'/>"
                                                                                    method="post">
                                                                                    <input type="hidden" name="action"
                                                                                        value="removeItem">
                                                                                    <input type="hidden"
                                                                                        name="productId"
                                                                                        value="${item.product.productID}">
                                                                                    <button type="submit"
                                                                                        class="btn btn-link text-danger p-0">
                                                                                        <i class="fas fa-trash"></i>
                                                                                    </button>
                                                                                </form>
                                                                            </td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </c:if>

                                                    <c:if test="${empty cart}">
                                                        <div class="pos-empty-state">
                                                            <div class="pos-empty-icon">
                                                                <i class="fas fa-shopping-basket"></i>
                                                            </div>
                                                            Chọn sản phẩm để thêm vào đơn hàng.
                                                        </div>
                                                    </c:if>
                                                </div>
                                                <div class="card-footer px-0 mt-3">
                                                    <div class="mb-3 bg-light p-2 rounded">
                                                        <div class="pos-order-summary-row">
                                                            <span>Tạm tính</span>
                                                            <span id="pos-subtotal" class="font-weight-bold">
                                                                <fmt:formatNumber value="${totalAmount}" type="number"
                                                                    maxFractionDigits="0" /> đ
                                                            </span>
                                                        </div>
                                                        <div class="pos-order-summary-row text-success">
                                                            <span>Khuyến mãi (Tự động)</span>
                                                            <span id="pos-auto-discount" class="font-weight-bold">-
                                                                <fmt:formatNumber value="${autoPromoDiscount}"
                                                                    type="number" maxFractionDigits="0" /> đ
                                                            </span>
                                                        </div>
                                                        <div class="pos-order-summary-row">
                                                            <span>Giảm thêm (<span
                                                                    id="pos-discount-pct">0</span>%)</span>
                                                            <span id="pos-discount-amount" class="font-weight-bold">- 0
                                                                đ</span>
                                                        </div>
                                                        <div class="pos-order-summary-row border-top pt-1 mt-1">
                                                            <span>Thuế VAT</span>
                                                            <span class="font-weight-bold">
                                                                <fmt:formatNumber value="${vatAmount}" type="number"
                                                                    maxFractionDigits="0" /> đ
                                                            </span>
                                                        </div>
                                                        <hr style="margin: 6px 0;">
                                                        <div class="d-flex justify-content-between align-items-center">
                                                            <span class="text-sm font-weight-bold text-muted">TỔNG
                                                                CỘNG</span>
                                                            <span class="pos-total-amount" id="pos-final-amount">
                                                                <fmt:formatNumber
                                                                    value="${totalAmount - autoPromoDiscount + vatAmount}"
                                                                    type="number" maxFractionDigits="0" /> đ
                                                            </span>
                                                        </div>
                                                    </div>

                                                    <form id="checkout-form" action="<c:url value='/pos'/>"
                                                        method="post">
                                                        <input type="hidden" name="action" value="checkout">

                                                        <div class="form-group mb-2">
                                                            <label for="pos-discount"
                                                                class="mb-1 small font-weight-bold text-uppercase text-muted">Giảm
                                                                thêm (%)</label>
                                                            <input type="number" id="pos-discount"
                                                                name="discountPercent" min="0" max="100" step="0.5"
                                                                value="0" class="form-control form-control-sm"
                                                                placeholder="Nhập số %">
                                                        </div>

                                                        <div class="form-group mb-2">
                                                            <label
                                                                class="mb-1 small font-weight-bold text-uppercase text-muted">Phương
                                                                thức thanh toán</label>
                                                            <div class="d-flex" style="gap: 8px;">
                                                                <label
                                                                    class="pos-payment-option active border flex-fill">
                                                                    <input type="radio" name="paymentMethod"
                                                                        value="CASH" checked
                                                                        onchange="togglePayment(this)">
                                                                    <i class="fas fa-money-bill-wave d-block mb-1"></i>
                                                                    <span style="font-size: 11px;">Tiền mặt</span>
                                                                </label>
                                                                <label class="pos-payment-option border flex-fill">
                                                                    <input type="radio" name="paymentMethod"
                                                                        value="TRANSFER" onchange="togglePayment(this)">
                                                                    <i class="fas fa-credit-card d-block mb-1"></i>
                                                                    <span style="font-size: 11px;">Chuyển khoản</span>
                                                                </label>
                                                            </div>
                                                        </div>

                                                        <div id="cash-section">
                                                            <div class="form-group mb-2">
                                                                <label for="pos-cash"
                                                                    class="mb-1 small font-weight-bold text-uppercase text-muted">Tiền
                                                                    khách đưa</label>
                                                                <input type="number" id="pos-cash" name="cashReceived"
                                                                    min="0" step="1000"
                                                                    class="form-control form-control-sm font-weight-bold text-primary"
                                                                    placeholder="Nhập số tiền mặt">
                                                            </div>
                                                            <div
                                                                class="d-flex justify-content-between align-items-center mb-2 px-2 py-1 bg-light rounded">
                                                                <span class="text-xs text-muted">Tiền thừa trả
                                                                    khách:</span>
                                                                <span id="pos-change-amount"
                                                                    class="font-weight-bold text-success">0 đ</span>
                                                            </div>
                                                        </div>

                                                        <div class="form-group mb-2">
                                                            <label
                                                                class="mb-1 small font-weight-bold text-uppercase text-muted">SĐT
                                                                Khách hàng</label>
                                                            <input type="text" name="customerId"
                                                                class="form-control form-control-sm"
                                                                placeholder="09xxxxxxx">
                                                        </div>

                                                        <div class="form-group mb-2">
                                                            <label
                                                                class="mb-1 small font-weight-bold text-uppercase text-muted">Tên
                                                                (Khách mới)</label>
                                                            <input type="text" name="customerName"
                                                                class="form-control form-control-sm"
                                                                placeholder="Nguyễn Văn A">
                                                        </div>

                                                        <div class="form-group mb-3">
                                                            <label
                                                                class="mb-1 small font-weight-bold text-uppercase text-muted">Ghi
                                                                chú</label>
                                                            <textarea name="note" rows="2"
                                                                class="form-control form-control-sm"
                                                                placeholder="Ghi chú nếu có..."></textarea>
                                                        </div>

                                                        <div class="row no-gutters">
                                                            <div class="col-4 pr-1">
                                                                <button type="button"
                                                                    class="btn btn-outline-secondary btn-block py-2"
                                                                    onclick="if(confirm('Hủy giỏ hàng?')) location.href='?action=clearCart'"
                                                                    <c:if test="${empty cart}">disabled</c:if>
                                                                    >Hủy</button>
                                                            </div>
                                                            <div class="col-8">
                                                                <button type="submit"
                                                                    class="btn btn-warning pos-pay-btn" <c:if
                                                                    test="${empty cart}">disabled</c:if>>
                                                                    <i class="fas fa-check-circle mr-1"></i> THANH TOÁN
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                        <!-- /.content-wrapper -->
                    </div>

                    <script
                        src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
                    <script
                        src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
                    <script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

                    <input type="hidden" id="raw-subtotal" value="${totalAmount}">
                    <input type="hidden" id="raw-auto-discount" value="${autoPromoDiscount}">
                    <input type="hidden" id="raw-vat" value="${vatAmount}">

                    <script>
                        function updateSummary() {
                            var subtotal = parseFloat($('#raw-subtotal').val()) || 0;
                            var autoDiscount = parseFloat($('#raw-auto-discount').val()) || 0;
                            var vat = parseFloat($('#raw-vat').val()) || 0;

                            var manualPct = parseFloat($('#pos-discount').val()) || 0;
                            if (manualPct < 0) manualPct = 0;
                            if (manualPct > 100) manualPct = 100;

                            var manualDiscount = subtotal * manualPct / 100;
                            var finalAmount = subtotal - autoDiscount - manualDiscount + vat;
                            if (finalAmount < 0) finalAmount = 0;

                            $('#pos-discount-pct').text(manualPct);
                            $('#pos-discount-amount').text('- ' + manualDiscount.toLocaleString('vi-VN', { maximumFractionDigits: 0 }) + ' đ');
                            $('#pos-final-amount').text(finalAmount.toLocaleString('vi-VN', { maximumFractionDigits: 0 }) + ' đ');

                            var cash = parseFloat($('#pos-cash').val()) || 0;
                            var change = cash - finalAmount;
                            if (change < 0) change = 0;
                            $('#pos-change-amount').text(change.toLocaleString('vi-VN', { maximumFractionDigits: 0 }) + ' đ');
                        }

                        function togglePayment(radio) {
                            $('.pos-payment-option').removeClass('active');
                            $(radio).closest('.pos-payment-option').addClass('active');
                            if (radio.value === 'TRANSFER') {
                                $('#cash-section').slideUp();
                            } else {
                                $('#cash-section').slideDown();
                            }
                        }

                        $(document).ready(function () {
                            $('#pos-discount, #pos-cash').on('input change', updateSummary);
                            updateSummary();

                            function focusSKU() {
                                var $sku = $('#sku');
                                if ($sku.length) {
                                    $sku.focus();
                                    $sku.select();
                                }
                            }
                            focusSKU();
                            $(document).on('click', '.pos-product-card', function () {
                                setTimeout(focusSKU, 50);
                            });
                            $(document).on('submit', 'form', function () {
                                var action = $(this).find('input[name="action"]').val();
                                if (action === 'addItem') {
                                    setTimeout(focusSKU, 100);
                                }
                            });
                            $(document).on('change', 'input[name="quantity"]', function () {
                                $(this).closest('form').submit();
                            });

                            $('#pos-sidebar-toggle').on('click', function () {
                                $('body').toggleClass('pos-sidebar-open');
                            });
                            $('.pos-offcanvas-backdrop').on('click', function () {
                                $('body').removeClass('pos-sidebar-open');
                            });

                            $('#pos-category').on('change', function () {
                                $('#pos-category-form').submit();
                            });
                        });
                    </script>
                </body>

                </html>