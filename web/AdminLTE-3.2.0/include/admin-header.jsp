<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    (function () {
        if (localStorage.getItem('adminlte_sidebar') === 'collapsed') {
            document.body.classList.add('sidebar-collapse');
        }
    })();
</script>

<!-- Navbar -->
<nav class="main-header navbar navbar-expand navbar-white navbar-light">
    <!-- Left navbar links -->
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
        </li>
        <li class="nav-item d-none d-sm-inline-block">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Home</a>
        </li>
    </ul>

    <!-- Right navbar links -->
    <ul class="navbar-nav ml-auto">
        <!-- Notifications Dropdown Menu -->
        <li class="nav-item dropdown">
            <a class="nav-link" data-toggle="dropdown" href="#">
                <i class="far fa-bell"></i>
                <span class="badge badge-warning navbar-badge">5</span>
            </a>
            <div class="dropdown-menu dropdown-menu-lg dropdown-menu-right">
                <span class="dropdown-item dropdown-header">5 Thông báo</span>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item">
                    <i class="fas fa-box-open mr-2"></i> Sản phẩm sắp hết hàng
                    <span class="float-right text-muted text-sm">3 phút</span>
                </a>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item dropdown-footer">Xem tất cả thông báo</a>
            </div>
        </li>
        <!-- Fullscreen -->
        <li class="nav-item">
            <a class="nav-link" data-widget="fullscreen" href="#" role="button">
                <i class="fas fa-expand-arrows-alt"></i>
            </a>
        </li>
    </ul>
</nav>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        var btn = document.querySelector('[data-widget="pushmenu"]');
        if (btn) {
            btn.addEventListener('click', function () {
                setTimeout(function () {
                    localStorage.setItem('adminlte_sidebar',
                            document.body.classList.contains('sidebar-collapse') ? 'collapsed' : 'open');
                }, 350);
            });
        }
    });
</script>
