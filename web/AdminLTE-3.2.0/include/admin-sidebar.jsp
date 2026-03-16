<%-- 
    Document   : admin-sidebar
    Created on : Feb 2, 2026, 9:55:25 AM
    Author     : xuand
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
                <a href="${pageContext.request.contextPath}/profile" class="d-block">${sessionScope.fullName != null ? sessionScope.fullName : 'User'}</a>
                <small class="text-muted">${sessionScope.roleName != null ? sessionScope.roleName : ''}</small>
            </div>
        </div>

        <!-- Sidebar Search Form -->
        <div class="form-inline">
            <div class="input-group" data-widget="sidebar-search">
                <input class="form-control form-control-sidebar" type="search" placeholder="Tìm kiếm..." aria-label="Search">
                <div class="input-group-append">
                    <button class="btn btn-sidebar">
                        <i class="fas fa-search fa-fw"></i>
                    </button>
                </div>
            </div>
        </div>

        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">

                <!-- Dashboard - Tất cả role -->
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                        <i class="nav-icon fas fa-tachometer-alt"></i>
                        <p>Dashboard</p>
                    </a>
                </li>

                <!-- POS - Bán hàng (Saler, Staff, Store Manager, Manager) -->
                <c:if test="${sessionScope.roleId == 5 || sessionScope.roleId == 3 || sessionScope.roleId == 2 || sessionScope.roleId == 1}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/pos" class="nav-link">
                            <i class="nav-icon fas fa-cash-register"></i>
                            <p>Bán hàng (POS)</p>
                        </a>
                    </li>
                </c:if>

                <!-- Chấm công - Cho nhân viên (Staff, Saler, Store Manager) -->
                <c:if test="${sessionScope.roleId == 3 || sessionScope.roleId == 5 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/staff/attendance-checkin" class="nav-link">
                            <i class="nav-icon fas fa-fingerprint"></i>
                            <p>Chấm công</p>
                        </a>
                    </li>
                </c:if>

                <!-- ======================== MANAGER & STORE MANAGER MENU ======================== -->
                <!-- Quản lý sản phẩm -->
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
                                <i class="far fa-circle nav-icon"></i>
                                <p>Danh sách sản phẩm</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="categories" class="nav-link">
                                <i class="far fa-dot-circle nav-icon"></i>
                                <p>Danh mục</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="brands" class="nav-link">
                                <i class="far fa-dot-circle nav-icon"></i>
                                <p>Thương hiệu</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Quản lý kho -->
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
                            <a href="${pageContext.request.contextPath}/admin/employees" class="nav-link">
                                <i class="far fa-circle nav-icon"></i>
                                <p>Tồn kho</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/purchaseorder" class="nav-link">
                                <i class="far fa-dot-circle nav-icon"></i>
                                <p>Nhập hàng</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/stocktake" class="nav-link">
                                <i class="fa-solid fa-list-check nav-icon"></i>
                                <p>Kiểm kho</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="#" class="nav-link">
                                <i class="far fa-dot-circle nav-icon"></i>
                                <p>Xuất hủy</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/inventorytransaction" class="nav-link">
                                <i class="fas fa-history nav-icon"></i>
                                <p>Lịch sử giao dịch</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Quản lý sản phẩm (Manager, Store Manager) -->
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
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách sản phẩm</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/categories" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh mục</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/brands" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Thương hiệu</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <!-- Quản lý kho (Manager, Store Manager) -->
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
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
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Tồn kho</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/purchaseorder" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Nhập hàng</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Kiểm kho</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Xuất hủy</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <!-- Đơn mua hàng (Manager, Store Manager) -->
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

                <!-- Hóa đơn bán hàng (Manager, Store Manager) -->
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

                <!-- Khách hàng (Manager, Store Manager, Saler) -->
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
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách khách hàng</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Điểm tích lũy</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/customer-tiers" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Hạng thành viên</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <!-- Khuyến mãi (Manager, Store Manager) -->
                <c:if test="${sessionScope.roleId == 1 || sessionScope.roleId == 2}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-tags"></i>
                            <p>
                                Khuyến mãi
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="promotions" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Danh sách khuyến mãi</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Điều kiện áp dụng</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <!-- Ca làm việc (Manager, Store Manager, Staff, Saler) - view only for Staff/Saler -->
                <c:if test="${sessionScope.roleId == 3 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin/shift-management" class="nav-link">
                            <i class="far fa-clock nav-icon"></i>
                            <p>Ca làm việc</p>
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.roleId == 3 || sessionScope.roleId == 5}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/staff/swap"
                           class="nav-link ${pageContext.request.requestURI.contains('/staff/swap') ? 'active' : ''}">
                            <i class="far fa-file-alt nav-icon"></i>
                            <p>Đơn đổi ca</p>
                        </a>
                    </li>
                </c:if>


                <!-- Nhân viên (Manager, Store Manager) -->
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
                                    <p>Chấm công</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/admin/shift-management" class="nav-link">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Ca làm việc</p>
                                </a>
                            </li>

                            <li class="nav-item">
                                <a href="${pageContext.request.contextPath}/staff/swap"
                                   class="nav-link ${pageContext.request.requestURI.contains('/staff/swap') ? 'active' : ''}">
                                    <i class="far fa-dot-circle nav-icon"></i>
                                    <p>Đơn đổi ca</p>
                                </a>
                            </li>
                        </ul>
                    </li>
                </c:if>

                <!-- Báo cáo (Manager) -->
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
                </c:if>

                <!-- Thông báo (Tất cả) -->
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas fa-bell"></i>
                        <p>
                            Thông báo
                            <span class="right badge badge-warning">5</span>
                        </p>
                    </a>
                </li>

                <!-- Cài đặt (Manager) -->
                <c:if test="${sessionScope.roleId == 1}">
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-cog"></i>
                            <p>Cài đặt hệ thống</p>
                        </a>
                    </li>
                </c:if>

                <!-- Hồ sơ cá nhân (Tất cả) -->
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                        <i class="nav-icon fas fa-user-circle"></i>
                        <p>Hồ sơ cá nhân</p>
                    </a>
                </li>

                <!-- Đăng xuất (Tất cả) -->
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
