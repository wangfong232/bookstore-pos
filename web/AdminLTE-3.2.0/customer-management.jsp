<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Quản Lý Khách Hàng</title>

                <!-- Google Font: Source Sans Pro -->
                <link rel="stylesheet"
                    href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
                <!-- Font Awesome -->
                <link rel="stylesheet"
                    href="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
                <!-- Theme style -->
                <link rel="stylesheet" href="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/css/adminlte.min.css">

                <style>
                    /* Tier badge colors */
                    .badge-gold {
                        background-color: #F1C40F;
                        color: #000;
                    }

                    .badge-silver {
                        background-color: #A6ACAF;
                        color: #fff;
                    }

                    .badge-bronze {
                        background-color: #D35400;
                        color: #fff;
                    }

                    .badge-diamond {
                        background-color: #3498DB;
                        color: #fff;
                    }

                    /* Customer list row hover & selected */
                    .customer-row {
                        cursor: pointer;
                    }

                    .customer-row:hover {
                        background-color: #f4f6f9;
                    }

                    .customer-row.selected {
                        background-color: #e9ecef;
                    }

                    /* Tab styling */
                    .detail-tab {
                        display: inline-block;
                        padding: 8px 20px;
                        border: 1px solid #dee2e6;
                        border-radius: 4px;
                        cursor: pointer;
                        margin-right: 8px;
                        background: #fff;
                        color: #495057;
                        font-weight: 400;
                    }

                    .detail-tab.active {
                        background: #fff;
                        border-bottom: 2px solid #007bff;
                        font-weight: 600;
                    }

                    /* Tab content */
                    .tab-panel {
                        display: none;
                    }

                    .tab-panel.active {
                        display: block;
                    }

                    /* Pagination */
                    .pagination-custom {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        padding: 10px 0;
                        gap: 2px;
                    }

                    .pagination-custom a,
                    .pagination-custom span {
                        display: inline-block;
                        padding: 4px 10px;
                        border: 1px solid #dee2e6;
                        color: #495057;
                        text-decoration: none;
                        font-size: 14px;
                    }

                    .pagination-custom span.active-page {
                        font-weight: bold;
                        background-color: #e9ecef;
                    }

                    .pagination-custom a:hover {
                        background-color: #f4f6f9;
                    }

                    /* Customer details card */
                    .customer-details-card {
                        min-height: 480px;
                    }

                    /* Avatar icon */
                    .customer-avatar {
                        width: 40px;
                        height: 40px;
                        border-radius: 50%;
                        background-color: #6c757d;
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        color: #fff;
                        font-size: 18px;
                        margin-right: 10px;
                    }

                    /* Order history table */
                    .order-table th {
                        background-color: #f8f9fa;
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
                                        <h1><i class="fas fa-users"></i> QUẢN LÝ KHÁCH HÀNG</h1>
                                    </div>
                                </div>
                            </div>
                        </section>

                        <!-- Main content -->
                        <section class="content">
                            <div class="container-fluid">

                                <!-- Alert messages -->
                                <c:if test="${not empty param.msg}">
                                    <c:choose>
                                        <c:when test="${param.msg eq 'update_success'}">
                                            <div class="alert alert-success alert-dismissible fade show">
                                                <button type="button" class="close"
                                                    data-dismiss="alert">&times;</button>
                                                <i class="fas fa-check-circle"></i> Cập nhật khách hàng thành công!
                                            </div>
                                        </c:when>
                                        <c:when test="${param.msg eq 'add_success'}">
                                            <div class="alert alert-success alert-dismissible fade show">
                                                <button type="button" class="close"
                                                    data-dismiss="alert">&times;</button>
                                                <i class="fas fa-check-circle"></i> Thêm khách hàng thành công!
                                            </div>
                                        </c:when>
                                        <c:when test="${param.msg eq 'delete_success'}">
                                            <div class="alert alert-warning alert-dismissible fade show">
                                                <button type="button" class="close"
                                                    data-dismiss="alert">&times;</button>
                                                <i class="fas fa-trash"></i> Đã xóa khách hàng!
                                            </div>
                                        </c:when>
                                        <c:when test="${param.msg eq 'not_found'}">
                                            <div class="alert alert-danger alert-dismissible fade show">
                                                <button type="button" class="close"
                                                    data-dismiss="alert">&times;</button>
                                                <i class="fas fa-exclamation-circle"></i> Không tìm thấy khách hàng!
                                            </div>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <!-- Search bar -->
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <form action="<%= request.getContextPath() %>/customers" method="get"
                                            class="form-inline" id="searchForm">
                                            <input type="hidden" name="action" value="search">

                                            <select name="tier" class="form-control mr-2" onchange="this.form.submit()">
                                                <option value="all" ${empty param.tier or param.tier eq 'all'
                                                    ? 'selected' : '' }>Tất cả Hạng</option>
                                                <c:forEach items="${tiers}" var="tierObj">
                                                    <option value="${tierObj.tierName}" ${param.tier eq tierObj.tierName
                                                        ? 'selected' : '' }>${tierObj.tierName}</option>
                                                </c:forEach>
                                            </select>

                                            <input type="text" class="form-control mr-2" id="searchInput" name="keyword"
                                                placeholder="Tìm theo Tên, Số điện thoại..." value="${param.keyword}"
                                                style="width: 250px;">
                                            <button type="submit" class="btn btn-success">
                                                <i class="fas fa-search"></i> Tìm kiếm
                                            </button>
                                        </form>
                                    </div>
                                </div>

                                <!-- Main Layout: Customer List + Customer Details -->
                                <div class="row">
                                    <!-- LEFT: Customer List -->
                                    <div class="col-md-6">
                                        <div class="card">
                                            <div class="card-header bg-light">
                                                <h3 class="card-title text-bold text-uppercase"
                                                    style="margin-top: 5px;">
                                                    Danh Sách Khách Hàng
                                                </h3>
                                            </div>
                                            <div class="card-body p-0">
                                                <table class="table table-bordered table-hover mb-0" id="customerTable">
                                                    <thead class="bg-light">
                                                        <tr>
                                                            <th>Họ Tên</th>
                                                            <th style="width: 140px;">Số Điện Thoại</th>
                                                            <th style="width: 90px;">Hạng</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:if test="${empty customers}">
                                                            <tr>
                                                                <td colspan="3" class="text-center">Chưa có dữ liệu
                                                                    khách hàng.</td>
                                                            </tr>
                                                        </c:if>
                                                        <c:if test="${not empty customers}">
                                                            <c:forEach items="${customers}" var="cust"
                                                                varStatus="status">
                                                                <tr class="customer-row ${status.index == 0 && empty selectedCustomer ? 'selected' : ''} ${cust.customerID == selectedCustomer.customerID ? 'selected' : ''}"
                                                                    data-id="${cust.customerID}"
                                                                    data-name="${cust.customerName}"
                                                                    data-phone="${cust.phone}"
                                                                    data-email="${cust.email}"
                                                                    data-tier="${cust.tierName}"
                                                                    data-points="${cust.points}">
                                                                    <td>${cust.customerName}</td>
                                                                    <td>${cust.phone}</td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when test="${cust.tierName eq 'Gold'}">
                                                                                <span
                                                                                    class="badge badge-gold">${cust.tierName}</span>
                                                                            </c:when>
                                                                            <c:when test="${cust.tierName eq 'Silver'}">
                                                                                <span
                                                                                    class="badge badge-silver">${cust.tierName}</span>
                                                                            </c:when>
                                                                            <c:when test="${cust.tierName eq 'Bronze'}">
                                                                                <span
                                                                                    class="badge badge-bronze">${cust.tierName}</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${cust.tierName eq 'Diamond'}">
                                                                                <span
                                                                                    class="badge badge-diamond">${cust.tierName}</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span
                                                                                    class="badge bg-info">${cust.tierName}</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </c:if>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <!-- Pagination -->
                                            <div class="card-footer clearfix">
                                                <div class="pagination-custom" id="paginationControls">
                                                    <!-- Pages will be rendered here via JS -->
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- RIGHT: Customer Details -->
                                    <div class="col-md-6">
                                        <div class="card customer-details-card">
                                            <div class="card-header bg-light d-flex align-items-center">
                                                <h3 class="card-title text-bold text-uppercase mb-0" style="flex:1;">
                                                    Chi Tiết Khách Hàng
                                                </h3>
                                                <div class="card-tools">
                                                    <button type="button" id="btnEdit"
                                                        class="btn btn-sm btn-outline-primary" onclick="enterEditMode()"
                                                        style="display:none;" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i> Chỉnh sửa
                                                    </button>
                                                </div>
                                            </div>
                                            <div class="card-body">
                                                <!-- Customer Avatar & Name -->
                                                <div class="d-flex align-items-center mb-3">
                                                    <div class="customer-avatar">
                                                        <i class="fas fa-user"></i>
                                                    </div>
                                                    <h5 class="mb-0" id="detailName"
                                                        style="color:#6c757d; margin-right: 10px;">-- Chưa chọn
                                                        khách hàng --</h5>
                                                    <span id="detailBadge" class="badge bg-secondary"
                                                        style="display:none; font-size: 14px;"></span>
                                                </div>

                                                <!-- Personal Info -->


                                                <div class="tab-panel active" id="panelPersonalInfo">

                                                    <!-- VIEW MODE -->
                                                    <div id="viewMode">
                                                        <div class="form-group row">
                                                            <label class="col-sm-4 col-form-label text-muted">Họ
                                                                tên:</label>
                                                            <div class="col-sm-8">
                                                                <p class="form-control-plaintext font-weight-bold"
                                                                    id="viewName">--</p>
                                                            </div>
                                                        </div>
                                                        <div class="form-group row">
                                                            <label class="col-sm-4 col-form-label text-muted">Số điện
                                                                thoại:</label>
                                                            <div class="col-sm-8">
                                                                <p class="form-control-plaintext" id="viewPhone">--</p>
                                                            </div>
                                                        </div>
                                                        <div class="form-group row">
                                                            <label
                                                                class="col-sm-4 col-form-label text-muted">Email:</label>
                                                            <div class="col-sm-8">
                                                                <p class="form-control-plaintext" id="viewEmail">--</p>
                                                            </div>
                                                        </div>

                                                        <div class="form-group row">
                                                            <label class="col-sm-4 col-form-label text-muted">Điểm tích
                                                                lũy:</label>
                                                            <div class="col-sm-8">
                                                                <p class="form-control-plaintext" id="viewPoints">--</p>
                                                            </div>
                                                        </div>

                                                    </div>

                                                    <!-- EDIT MODE -->
                                                    <div id="editMode" style="display:none;">
                                                        <form id="customerDetailForm"
                                                            action="<%= request.getContextPath() %>/customers"
                                                            method="post">
                                                            <input type="hidden" name="action" value="update">
                                                            <input type="hidden" name="customerID" id="detailID"
                                                                value="">

                                                            <div class="form-group row">
                                                                <label class="col-sm-4 col-form-label">Họ tên:</label>
                                                                <div class="col-sm-8">
                                                                    <input type="text" class="form-control"
                                                                        id="detailCustomerName" name="customerName"
                                                                        value="" placeholder="Họ tên">
                                                                </div>
                                                            </div>
                                                            <div class="form-group row">
                                                                <label class="col-sm-4 col-form-label">Số điện
                                                                    thoại:</label>
                                                                <div class="col-sm-8">
                                                                    <input type="text" class="form-control bg-light"
                                                                        id="detailPhone" value="" readonly
                                                                        title="Không thể thay đổi số điện thoại">
                                                                    <small class="text-muted"><i
                                                                            class="fas fa-lock"></i> Không thể thay
                                                                        đổi</small>
                                                                </div>
                                                            </div>
                                                            <div class="form-group row">
                                                                <label class="col-sm-4 col-form-label">Email:</label>
                                                                <div class="col-sm-8">
                                                                    <input type="text" class="form-control"
                                                                        id="detailEmail" name="email" value=""
                                                                        placeholder="Email">
                                                                </div>
                                                            </div>

                                                            <div class="form-group row">
                                                                <label class="col-sm-4 col-form-label">Điểm tích
                                                                    lũy:</label>
                                                                <div class="col-sm-8">
                                                                    <span id="detailPoints"
                                                                        class="form-control-plaintext"></span>
                                                                </div>
                                                            </div>
                                                        </form>
                                                        <!-- Edit mode buttons -->
                                                        <div class="d-flex justify-content-end mt-3 gap-2">
                                                            <button type="button" class="btn btn-secondary btn-sm mr-2"
                                                                onclick="cancelEdit()">
                                                                <i class="fas fa-times"></i> Hủy
                                                            </button>

                                                            <button type="submit" class="btn btn-primary btn-sm"
                                                                form="customerDetailForm">
                                                                <i class="fas fa-save"></i> Lưu Thay Đổi
                                                            </button>
                                                        </div>
                                                    </div>

                                                </div>



                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.row -->

                            </div>
                        </section>
                    </div>

                    <!-- Footer -->
                    <jsp:include page="include/admin-footer.jsp" />

                </div>

                <!-- jQuery -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
                <!-- Bootstrap 4 -->
                <script
                    src="<%= request.getContextPath() %>/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
                <!-- AdminLTE App -->
                <script src="<%= request.getContextPath() %>/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>

                <script>
                    // Lưu dữ liệu khách hàng đang được chọn
                    var currentCustomer = null;

                    $(document).ready(function () {
                        // --- Customer row click: update details panel ---
                        $('.customer-row').on('click', function () {
                            $('.customer-row').removeClass('selected');
                            $(this).addClass('selected');

                            var id = $(this).data('id');
                            var name = $(this).data('name');
                            var phone = $(this).data('phone');
                            var email = $(this).data('email');
                            var tier = $(this).data('tier');
                            var points = $(this).data('points');

                            // Lưu lại để dùng khi cancel
                            currentCustomer = { id, name, phone, email, tier, points };

                            // Cập nhật header tên
                            $('#detailName').text(name).css('color', '');

                            // Cập nhật badge hạng trên header
                            if (tier) {
                                $('#detailBadge').text(tier).show();
                                $('#detailBadge').removeClass('badge-gold badge-silver badge-bronze badge-diamond bg-info bg-secondary');
                                if (tier === 'Gold') $('#detailBadge').addClass('badge-gold');
                                else if (tier === 'Silver') $('#detailBadge').addClass('badge-silver');
                                else if (tier === 'Bronze') $('#detailBadge').addClass('badge-bronze');
                                else if (tier === 'Diamond') $('#detailBadge').addClass('badge-diamond');
                                else $('#detailBadge').addClass('bg-info');
                            } else {
                                $('#detailBadge').hide();
                            }

                            // === VIEW MODE ===
                            $('#viewName').text(name || '--');
                            $('#viewPhone').text(phone || '--');
                            $('#viewEmail').text(email || '--');
                            $('#viewTier').text(tier || '--');
                            $('#viewPoints').text(points ? Number(points).toLocaleString() + ' pts' : '--');


                            // === EDIT MODE fields (prefill) ===
                            $('#detailID').val(id);
                            $('#detailCustomerName').val(name);
                            $('#detailPhone').val(phone);
                            $('#detailEmail').val(email);
                            $('#detailTier').val(tier);
                            $('#detailPoints').text(points ? Number(points).toLocaleString() + ' pts' : '--');

                            // Về view mode, hiện nút Edit
                            showViewMode();
                        });


                    });

                    function showViewMode() {
                        $('#viewMode').show();
                        $('#editMode').hide();
                        $('#btnEdit').show();
                    }

                    function enterEditMode() {
                        $('#viewMode').hide();
                        $('#editMode').show();
                        $('#btnEdit').hide();
                    }

                    function cancelEdit() {
                        showViewMode();
                    }



                    // --- Pagination Logic (10 items per page) ---
                    var itemsPerPage = 10;
                    var currentPage = 1;
                    var totalItems = $('.customer-row').length;
                    var totalPages = Math.ceil(totalItems / itemsPerPage);

                    function renderPagination() {
                        var paginationHtml = '';

                        // Nút Previous
                        if (currentPage > 1) {
                            paginationHtml += '<a href="#" onclick="changePage(-1); return false;">&lt;</a>';
                        } else {
                            paginationHtml += '<span class="text-muted" style="border-color:transparent;">&lt;</span>';
                        }

                        // Các số trang
                        for (var i = 1; i <= totalPages; i++) {
                            if (i === currentPage) {
                                paginationHtml += '<span class="active-page" onclick="goToPage(' + i + ')">' + i + '</span>';
                            } else {
                                paginationHtml += '<a href="#" onclick="goToPage(' + i + '); return false;">' + i + '</a>';
                            }
                        }

                        // Nút Next
                        if (currentPage < totalPages && totalPages > 0) {
                            paginationHtml += '<a href="#" onclick="changePage(1); return false;">&gt;</a>';
                        } else {
                            paginationHtml += '<span class="text-muted" style="border-color:transparent;">&gt;</span>';
                        }

                        $('#paginationControls').html(paginationHtml);
                    }

                    function goToPage(page) {
                        if (page < 1 || page > totalPages) return;
                        currentPage = page;

                        // Ẩn tất cả khách hàng
                        $('.customer-row').hide();

                        // Hiển thị khách hàng của trang hiện tại
                        var startIndex = (currentPage - 1) * itemsPerPage;
                        var endIndex = startIndex + itemsPerPage;
                        $('.customer-row').slice(startIndex, endIndex).show();

                        renderPagination();
                    }

                    function changePage(direction) {
                        goToPage(currentPage + direction);
                    }

                    // Khởi tạo phân trang khi trang vừa load xong
                    if (totalPages > 0) {
                        goToPage(1);
                    } else {
                        $('#paginationControls').html('<span>Không có dữ liệu</span>');
                    }




                </script>
            </body>

            </html>