<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>

        <style>
            body.login-page {
                background-image: url("AdminLTE-3.2.0/images/book.png");
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                height: 100vh;
            }
            .login-logo a{
                color:white !important;
                font-size:32px;
                font-weight:700;
                letter-spacing:1px;
                text-shadow:0 4px 20px rgba(0,0,0,0.6);
            }

            .card-body {
                border-radius: 30px;
            }

        </style>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Bookstore | Log in</title>

        <!-- Google Font -->
        <link rel="stylesheet"
              href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">

        <!-- FontAwesome -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">

        <!-- AdminLTE -->
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition login-page">
        <div class="login-box">
            <div class="login-logo" style="color: #ffffff">
                <a><b>Bookstore</b> Local POS</a>
            </div>
            <!-- /.login-logo -->
            <div class="card">
                <div class="card-body login-card-body">
                    <p class="login-box-msg">Đăng nhập để bắt đầu phiên làm việc của bạn</p>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                    <% } %>
                    <% if (request.getParameter("success") != null && request.getParameter("success").equals("register")) { %>
                    <div class="alert alert-success">Đăng ký thành công! Hãy đăng nhập.</div>
                    <% } %>
                    <% if (request.getParameter("success") != null && request.getParameter("success").equals("register_pending")) { %>
                    <div class="alert alert-warning">
                        <strong>Đã đăng ký thành công!</strong><br/>
                        Đã đăng ký thành công, Tài khoản của bạn đang chờ manager phân quyền và kích hoạt. Vui lòng đợi.
                    </div>
                    <% } %>
                    <% if (request.getParameter("success") != null && request.getParameter("success").equals("logout")) { %>
                    <div class="alert alert-info">Bạn đã đăng nhập thành công.</div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <div class="input-group mb-3">
                            <input type="email" name="email" class="form-control" placeholder="Email" required>
                            <div class="input-group-append">
                                <div class="input-group-text">
                                    <span class="fas fa-envelope"></span>
                                </div>
                            </div>
                        </div>
                        <div class="input-group mb-3">
                            <input type="password" id="password" name="password" class="form-control" placeholder="Mật khẩu" required>

                            <div class="input-group-append">
                                <div class="input-group-text" onclick="togglePassword('password', this)" style="cursor:pointer">
                                    <span class="fas fa-eye"></span>
                                </div>
                            </div>
                        </div>

                        <div class="icheck-primary">
                            <input type="checkbox" id="remember">
                            <label for="remember">
                                Remember Me
                            </label>
                        </div>

                        <button type="submit" class="btn btn-primary btn-block" style="margin-bottom: 10px">Đăng nhập</button>
                    </form>

                    <p class="mb-1">
                        <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu</a>
                    </p>
                    <p class="mb-0">
                        <a href="${pageContext.request.contextPath}/register" class="text-center">Đăng ký tài khoản mới</a>
                    </p>
                </div>
                <!-- /.login-card-body -->
            </div>
        </div>
        <!-- /.login-box -->

        <!-- jQuery -->
        <script src="plugins/jquery/jquery.min.js"></script>
        <!-- Bootstrap 4 -->
        <script src="plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
        <!-- AdminLTE App -->
        <script src="dist/js/adminlte.min.js"></script>
    </body>
    <script>
                                    function togglePassword(id, icon) {
                                        const input = document.getElementById(id);
                                        const eye = icon.querySelector("span");

                                        if (input.type === "password") {
                                            input.type = "text";
                                            eye.classList.remove("fa-eye");
                                            eye.classList.add("fa-eye-slash");
                                        } else {
                                            input.type = "password";
                                            eye.classList.remove("fa-eye-slash");
                                            eye.classList.add("fa-eye");
                                        }
                                    }
    </script>
</html>

