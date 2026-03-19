<%-- ok
    Document   : admin-sidebar
    Author     : Refactored with role-based access control
    Roles:
        1 = Manager       – quản lý sản phẩm, nhân viên, báo cáo toàn hệ thống
        2 = Store Manager – quản lý kho, nhà cung cấp, phê duyệt báo cáo
        3 = Staff         – xử lý đơn hàng, xem thông tin KH
        5 = Saler         – POS, khách hàng, khuyến mãi, thông báo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Main Sidebar Container -->
<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <!-- Brand Logo -->
    <a href="${pageContext.request.contextPath}/dashboard" class="brand-link">
        <img src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/img/AdminLTELogo.png"
             alt="Logo" class="brand-image img-dot-circle elevation-3" style="opacity: .8">
        <span class="brand-text font-weight-light">Bookstore POS</span>
    </a>

    <!-- Sidebar -->
    <div class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel mt-3 pb-3 mb-3 d-flex">
            <div class="image">
                <img src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/img/user2-160x160.jpg"
                     class="img-dot-circle elevation-2" alt="User Image">
            </div>
            <div class="info">
                <a href="${pageContext.request.contextPath}/profile" class="d-block">
                    ${sessionScope.fullName != null ? sessionScope.fullName : 'User'}
                </a>
                <small class="text-muted">${sessionScope.roleName != null ? sessionScope.roleName : ''}</small>
            </div>
        </div>

        <!-- Sidebar Search Form -->
        <div class="form-inline">
            <div class="input-group" data-widget="sidebar-search">
                <input class="form-control form-control-sidebar" type="search"
                       placeholder="Tìm kiếm..." aria-label="Search">
                <div class="input-group-append">
                    <button class="btn btn-sidebar">
                        <i class="fas fa-search fa-fw"></i>
                    </button>
                </div>
            </div>
        </div>

        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column"
                data-widget="treeview" role="menu" data-accordion="false">

                <%-- ===== Dashboard – Tất cả roles ===== --%>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                        <i class="nav-icon fas fa-tachometer-alt"></i>
                        <p>Dashboard</p>
                    </a>
                </li>

                <%-- ===== BÁN HÀNG (POS) – Store Manager, Staff, Saler ===== --%>
                <c:if test="${sessionScope.roleId == 2 || sessionScope.roleId == 3 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/pos" class="nav-link">
                            <i class="nav-icon fas fa-cash-register"></i>
                            <p>Bán hàng (POS)</p>
                        </a>
                    </li>
                </c:if>

                <%-- ===== CHẤM CÔNG (tự check-in) – Store Manager, Staff, Saler ===== --%>
                <c:if test="${sessionScope.roleId == 2 || sessionScope.roleId == 3 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/staff/attendance-checkin" class="nav-link">
                            <i class="nav-icon fas fa-fingerprint"></i>
                            <p>Chấm công</p>
                        </a>
                    </li>
                </c:if>

                <%-- ===== CA LÀM VIỆC – Staff & Saler (xem lịch cá nhân + đổi ca) ===== --%>
                <c:if test="${sessionScope.roleId == 3 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/shift-management" class="nav-link">
                            <i class="nav-icon far fa-clock"></i>
                            <p>Ca làm việc</p>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/staff/swap"
                           class="nav-link ${pageContext.request.requestURI.contains('/staff/swap') ? 'active' : ''}">
                            <i class="nav-icon far fa-file-alt"></i>
                            <p>Đơn đổi ca</p>
                        </a>
                    </li>
                </c:if>

                <%-- ===== QUẢN LÝ SẢN PHẨM – Manager & Store Manager ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-book"></i>
                            <p>
                                Quản lý sản phẩm
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/products" class="nav-link">
                                    <i class="nav-icon fas fa-boxes"></i>
                                    <p>Danh sách sản phẩm</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/categories" class="nav-link">
                                    <i class="nav-icon fas fa-th-list"></i>
                                    <p>Danh mục</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/brands" class="nav-link">
                                    <i class="nav-icon fas fa-tag"></i>
                                    <p>Thương hiệu</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <%-- ===== QUẢN LÝ KHO – Tất cả roles ===== --%>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas fa-warehouse"></i>
                        <p>
                            Quản lý kho
                            <i class="right fas fa-angle-left"></i>
                        </p>
                    </a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/purchaseorder" class="nav-link">
                                <i class="fas fa-file-import nav-icon"></i>
                                <p>Nhập hàng</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/goodsreceipt" class="nav-link">
                                <i class="fas fa-truck-loading nav-icon"></i>
                                <p>Nhận hàng</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/stocktake" class="nav-link">
                                <i class="fas fa-list-check nav-icon"></i>
                                <p>Kiểm kho</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/stockdisposal" class="nav-link">
                                <i class="fas fa-trash-can nav-icon"></i>
                                <p>Xuất hủy</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/inventorytransaction" class="nav-link">
                                <i class="fas fa-history nav-icon"></i>
                                <p>Lịch sử giao dịch</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/supplier" class="nav-link">
                                <i class="fas fa-truck nav-icon"></i>
                                <p>Nhà cung cấp</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <%-- ===== ĐƠN MUA HÀNG – Manager & Store Manager ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-shopping-cart"></i>
                            <p>
                                Đơn mua hàng
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách đơn hàng</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/supplier" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Nhà cung cấp</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <%-- ===== HÓA ĐƠN BÁN HÀNG – Manager & Store Manager ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-file-invoice"></i>
                            <p>
                                Hóa đơn bán
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách hóa đơn</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Thanh toán</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <%-- ===== KHÁCH HÀNG – Manager, Store Manager, Saler ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-users"></i>
                            <p>
                                Khách hàng
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/customers" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách khách hàng</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/customer-tiers" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Cấp bậc khách hàng</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <%-- ===== KHUYẾN MÃI – Manager, Store Manager, Saler ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/promotions" class="nav-link">
                            <i class="nav-icon fas fa-tags"></i>
                            <p>Khuyến mãi</p>
                        </a>
                    </li>
                </c:if>

                <%-- ===== NHÂN VIÊN – Manager & Store Manager ===== --%>
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-user-tie"></i>
                            <p>
                                Nhân viên
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/employees"
                                   class="nav-link ${pageContext.request.requestURI.contains('/admin/employees') ? 'active' : ''}">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách nhân viên</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/attendance?action=list"
                                   class="nav-link ${pageContext.request.requestURI.contains('/admin/attendance') ? 'active' : ''}">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Quản lý chấm công</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/shift-management" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Ca làm việc</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/swap-approval"
                                   class="nav-link ${pageContext.request.requestURI.contains('/admin/swap-approval') ? 'active' : ''}">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Phê duyệt đổi ca</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <%-- ===== BÁO CÁO – Manager only ===== --%>
                <c:if test="${sessionScope.roleId == 1}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-chart-line"></i>
                            <p>
                                Báo cáo
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Doanh thu</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Tồn kho</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Sản phẩm bán chạy</p>
                                </a>
                            </li>
                        </ul>
                    </li>

                    <%-- Cài đặt hệ thống --%>
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-cog"></i>
                            <p>Cài đặt hệ thống</p>
                        </a>
                    </li>
                </c:if>

                <%-- ===== THÔNG BÁO – Tất cả roles ===== --%>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas fa-bell"></i>
                        <p>
                            Thông báo
                            <span class="right badge badge-warning">5</span>
                        </p>
                    </a>
                </li>

                <%-- ===== HỒ SƠ CÁ NHÂN – Tất cả roles ===== --%>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                        <i class="nav-icon fas fa-user-circle"></i>
                        <p>Hồ sơ cá nhân</p>
                    </a>
                </li>

                <%-- ===== ĐĂNG XUẤT – Tất cả roles ===== --%>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link">
                        <i class="nav-icon fas fa-sign-out-alt"></i>
                        <p>Đăng xuất</p>
                    </a>
                </li>

            </ul>
        </nav>
        <!-- /.sidebar-menu -->
    </div>
    <!-- /.sidebar -->

</aside>
