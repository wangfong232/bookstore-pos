<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="entity.Role" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <style>
            body.register-page {
                background-image: url("AdminLTE-3.2.0/images/book.png");
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                height: 100vh;
            }
            .register-logo a{
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
        <title>Bookstore | Registration Page</title>

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
    <body class="hold-transition register-page">
        <div class="register-box">
            <div class="register-logo">
                <a><b>Bookstore</b> Local POS</a>
            </div>

            <div class="card">
                <div class="card-body register-card-body">
                    <p class="login-box-msg">Đăng ký thành viên mới</p>
                    <c:if test="${sessionScope.roleName != null && fn:contains(fn:toLowerCase(sessionScope.roleName),'manager')}">
                        <p class="text-info small">Bạn là quản lý, có thể gán vai trò và ngày vào làm.</p>
                    </c:if>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/register" method="post">
                        <div class="input-group mb-3">
                            <input type="text" name="fullName" class="form-control" placeholder="Họ và tên" 
                                   pattern="^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ\s]+$"
                                   title="Họ và tên chỉ được chứa chữ cái và khoảng trắng"
                                   required>
                            <div class="input-group-append">
                                <div class="input-group-text">
                                    <span class="fas fa-user"></span>
                                </div>
                            </div>
                        </div>
                        <div class="input-group mb-3">
                            <input type="email" name="email" class="form-control" placeholder="Email" required>
                            <div class="input-group-append">
                                <div class="input-group-text">
                                    <span class="fas fa-envelope"></span>
                                </div>
                            </div>
                        </div>
                        <div class="input-group mb-3">
                            <input type="text" name="phone" class="form-control" placeholder="SĐT">
                            <div class="input-group-append">
                                <div class="input-group-text">
                                    <span class="fas fa-phone"></span>
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
                        <div class="input-group mb-3">
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Xác nhận mật khẩu" required>

                            <div class="input-group-append">
                                <div class="input-group-text" onclick="togglePassword('confirmPassword', this)" style="cursor:pointer">
                                    <span class="fas fa-eye"></span>
                                </div>
                            </div>
                        </div>

                        <c:if test="${sessionScope.roleName != null && fn:contains(fn:toLowerCase(sessionScope.roleName),'manager')}">
                            <div class="input-group mb-3">
                                <select name="roleId" class="form-control">
                                    <option value="">Select Role</option>
                                    <%
                                      List<Role> roles = (List<Role>) request.getAttribute("roles");
                                      if (roles != null) {
                                        for (Role role : roles) {
                                    %>
                                    <option value="<%= role.getRoleId() %>"><%= role.getRoleName() %></option>
                                    <%
                                        }
                                      }
                                    %>
                                </select>
                                <div class="input-group-append">
                                    <div class="input-group-text">
                                        <span class="fas fa-user-tag"></span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${sessionScope.roleName != null && fn:contains(fn:toLowerCase(sessionScope.roleName),'manager')}">
                            <div class="input-group mb-3">
                                <input type="date" name="hireDate" class="form-control" placeholder="Ngày vào làm">
                                <div class="input-group-append">
                                    <div class="input-group-text">
                                        <span class="fas fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <div>
                            <div>
                                <div class="icheck-primary">
                                    <input type="checkbox" id="agreeTerms" name="terms" value="agree" required>
                                    <label for="agreeTerms">
                                        Tôi chấp thuận với <a href="#">Điều khoản</a>
                                    </label>
                                </div>
                            </div>

                        </div>


                        <button type="submit" class="btn btn-primary btn-block" style="margin-bottom: 10px">Đăng ký</button>


                    </form>

                    <a href="${pageContext.request.contextPath}/login" class="text-center">Tôi đã có tài khoản</a>
                </div>
                <!-- /.form-box -->
            </div><!-- /.card -->
        </div>
        <!-- /.register-box -->

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
                                            eye.classList.add("fa-eye");
                                        }
                                    }

                                    // Ngăn chặn khoảng trắng trong email
                                    document.querySelector('input[name="email"]').addEventListener('keydown', function (e) {
                                        if (e.which === 32)
                                            e.preventDefault();
                                    });

                                    document.querySelector('input[name="email"]').addEventListener('input', function (e) {
                                        this.value = this.value.replace(/\s/g, '');
                                    });
    </script>
</html>
