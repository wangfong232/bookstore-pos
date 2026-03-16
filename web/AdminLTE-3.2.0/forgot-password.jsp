<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <style>
            body.login-page {
                background-image: url("AdminLTE-3.2.0/images/book.png");
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                height: 100vh;
            }
            .login-logo a {
                color: white !important;
                font-size: 32px;
                font-weight: 700;
                letter-spacing: 1px;
                text-shadow: 0 4px 20px rgba(0,0,0,0.6);
            }
            .card-body {
                border-radius: 30px;
            }
            .step-indicator {
                display: flex;
                justify-content: center;
                gap: 8px;
                margin-bottom: 18px;
            }
            .step-dot {
                width: 10px; height: 10px;
                border-radius: 50%;
                background: #dee2e6;
                transition: background .3s;
            }
            .step-dot.active { background: #007bff; }
        </style>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Bookstore | Quên mật khẩu</title>

        <link rel="stylesheet"
              href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/fontawesome-free/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition login-page">
        <div class="login-box">
            <div class="login-logo" style="color:#ffffff">
                <a><b>Bookstore</b> Local POS</a>
            </div>

            <div class="card">
                <div class="card-body login-card-body">

                    <%-- Step indicator dots --%>
                    <div class="step-indicator">
                        <div class="step-dot ${(step == null || step.equals('1')) ? 'active' : ''}"></div>
                        <div class="step-dot ${(step != null && step.equals('2')) ? 'active' : ''}"></div>
                    </div>

                    <%-- ======= BƯỚC 1: Nhập email ======= --%>
                    <% if (request.getAttribute("step") == null || request.getAttribute("step").equals("1")) { %>

                        <p class="login-box-msg">
                            <i class="fas fa-lock mr-1"></i> Quên mật khẩu
                        </p>
                        <p class="text-center text-muted" style="font-size:13px; margin-top:-10px; margin-bottom:16px;">
                            Nhập email đã đăng ký để đặt lại mật khẩu
                        </p>

                        <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle mr-1"></i>
                            <%= request.getAttribute("error") %>
                        </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                            <input type="hidden" name="step" value="1"/>

                            <div class="input-group mb-3">
                                <input type="email" name="email"
                                       class="form-control" placeholder="Nhập email của bạn"
                                       value="<%= request.getAttribute("emailValue") != null ? request.getAttribute("emailValue") : "" %>"
                                       required autofocus>
                                <div class="input-group-append">
                                    <div class="input-group-text">
                                        <span class="fas fa-envelope"></span>
                                    </div>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary btn-block" style="margin-bottom:10px">
                                <i class="fas fa-arrow-right mr-1"></i> Tiếp tục
                            </button>
                        </form>

                        <p class="mb-0 text-center">
                            <a href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-arrow-left mr-1"></i> Quay lại đăng nhập
                            </a>
                        </p>

                    <%-- ======= BƯỚC 2: Nhập mật khẩu mới ======= --%>
                    <% } else { %>

                        <p class="login-box-msg">
                            <i class="fas fa-key mr-1"></i> Đặt lại mật khẩu
                        </p>
                        <p class="text-center text-muted" style="font-size:13px; margin-top:-10px; margin-bottom:16px;">
                            Tạo mật khẩu mới cho tài khoản
                            <strong><%= request.getAttribute("emailValue") %></strong>
                        </p>

                        <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle mr-1"></i>
                            <%= request.getAttribute("error") %>
                        </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                            <input type="hidden" name="step" value="2"/>
                            <input type="hidden" name="email" value="<%= request.getAttribute("emailValue") %>"/>

                            <div class="input-group mb-3">
                                <input type="password" id="newPassword" name="newPassword"
                                       class="form-control" placeholder="Mật khẩu mới" required>
                                <div class="input-group-append">
                                    <div class="input-group-text"
                                         onclick="togglePassword('newPassword', this)" style="cursor:pointer">
                                        <span class="fas fa-eye"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="input-group mb-3">
                                <input type="password" id="confirmPassword" name="confirmPassword"
                                       class="form-control" placeholder="Xác nhận mật khẩu" required>
                                <div class="input-group-append">
                                    <div class="input-group-text"
                                         onclick="togglePassword('confirmPassword', this)" style="cursor:pointer">
                                        <span class="fas fa-eye"></span>
                                    </div>
                                </div>
                            </div>

                            <small class="text-muted d-block mb-3">
                                <i class="fas fa-info-circle mr-1"></i>
                                Mật khẩu tối thiểu 6 ký tự
                            </small>

                            <button type="submit" class="btn btn-success btn-block" style="margin-bottom:10px">
                                <i class="fas fa-save mr-1"></i> Đặt lại mật khẩu
                            </button>
                        </form>

                        <p class="mb-0 text-center">
                            <a href="${pageContext.request.contextPath}/forgot-password">
                                <i class="fas fa-arrow-left mr-1"></i> Nhập lại email
                            </a>
                        </p>

                    <% } %>
                </div>
                <!-- /.login-card-body -->
            </div>
        </div>
        <!-- /.login-box -->

        <script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/jquery/jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/js/adminlte.min.js"></script>
        <script>
            function togglePassword(id, icon) {
                const input = document.getElementById(id);
                const eye = icon.querySelector("span");
                if (input.type === "password") {
                    input.type = "text";
                    eye.classList.replace("fa-eye", "fa-eye-slash");
                } else {
                    input.type = "password";
                    eye.classList.replace("fa-eye-slash", "fa-eye");
                }
            }

            // Client-side: kiểm tra 2 password khớp nhau trước khi submit
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.querySelector("form[action*='forgot-password']");
                if (!form) return;
                const confirmField = document.getElementById("confirmPassword");
                if (!confirmField) return;

                form.addEventListener("submit", function (e) {
                    const p1 = document.getElementById("newPassword").value;
                    const p2 = confirmField.value;
                    if (p1.length < 6) {
                        e.preventDefault();
                        alert("Mật khẩu phải có ít nhất 6 ký tự!");
                        return;
                    }
                    if (p1 !== p2) {
                        e.preventDefault();
                        alert("Mật khẩu xác nhận không khớp!");
                    }
                });
            });
        </script>
    </body>
</html>
