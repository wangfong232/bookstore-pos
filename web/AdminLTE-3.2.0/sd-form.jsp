<%-- 
    Document   : sd-form
    Created on : Mar 11, 2026, 11:28:02 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo phiếu xuất hủy</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">
            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>

            <div class="content-wrapper">
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1><i class="fas fa-trash-alt"></i> Tạo phiếu xuất hủy hàng</h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/stockdisposal?action=list">Xuất hủy hàng</a></li>
                                    <li class="breadcrumb-item active">Tạo mới</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="content">
                    <div class="container-fluid">
                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success')?'alert-success':'alert-danger'} alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert">x</button>
                                ${msg}
                            </div>
                        </c:if>


                        <form action="${pageContext.request.contextPath}/stockdisposal" method="post">
                            <input type="hidden" name="action" value="save">
                            <input type="hidden" name="sdNumber" value="${sdNumber}">

                            <!-- header info -->
                            <div class="card card-danger card-outline">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin phiếu xuất hủy</h3>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-4 form-group">
                                            <label>Mã phiếu <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" value="${sdNumber}" readonly>
                                            <small class="text-muted">(tự động)</small>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Ngày tạo</label>
                                            <input type="text" class="form-control" value="${today}" readonly>
                                            <small class="text-muted">(auto)</small>
                                        </div>
                                        <div class="col-md-4 form-group">
                                            <label>Lý do chung <span class="text-danger">*</span></label>
                                            <select name="disposalReason" class="form-control" required>
                                                <option value="">-- Chọn lý do -- </option>
                                                <option value="DAMAGED">Hỏng hóc</option>
                                                <option value="EXPIRED">Hết hạn sử dụng</option>
                                                <option value="DEFECTIVE">Lỗi sản phẩm</option>
                                                <option value="OTHER">Khác</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- product detail -->
                            <div class="card">
                                <div class="card-header">
                                    <h3 class="card-title"><i class="fas fa-boxes"></i> Chi tiết sản phẩm xuất hủy</h3>
                                </div>

                                <div class="card-body">
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label>Tìm và thêm sản phẩm:</label>
                                            <div class="input-group">
                                                <input type="text" class="form-control" placeholder="Gõ tên hoặc SKU sản phẩm...">
                                                <div class="input-group-append">
                                                    <button type="button" class="btn btn-primary"><i class="fas fa-plus"></i>Thêm</button>
                                                </div>
                                            </div>
                                            <div  class="list-group shadow">
                                                
                                            </div>
                                        </div>
                                    </div>

                                    <table class="table table-borderd product-table">
                                        <thead class="thead-light">
                                            <tr>
                                                <th style="width:50px" class="text-center">STT</th>
                                                <th>Sản phẩm</th>
                                                <th style="width:120px" class="text-center">Khả dụng / Tồn</th>
                                                <th style="width:150px" class="text-center">SL xuất hủy</th>
                                                <th>Lý do cụ thể</th>
                                                <th style="width:60px" class="text-center">Xóa</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td colspan="6" class="text-center text-muted py-4">
                                                    <i class="fas fa-box-open mr-1"></i> Chưa có sản phẩm. Dùng ô tìm kiếm để thêm sản phẩm.
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <div class="mt-2">
                                        <strong>Tổng số lượng xuất hủy: </strong>
                                        <span class="text-danger">0</span>
                                    </div>

                                </div>
                            </div>

                            <div class="card">
                                <div class="card-body">
                                    <div class="form-group">
                                        <label>Ghi chú:</label>
                                        <textarea name="notes" class="form-control" rows="3"
                                                  placeholder="Ghi chú thêm về đợt xuất hủy này..."></textarea>
                                    </div>
                                    <div class="d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/stockdisposal?action=list"
                                           class="btn btn-default mr-2">
                                            <i class="fas fa-times"></i> Hủy
                                        </a>
                                        <button type="submit" class="btn btn-danger">
                                            <i class="fas fa-paper-plane"></i> Lưu và gửi duyệt
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </section>
            </div>

            <jsp:include page="include/admin-footer.jsp"/>
        </div>
          
    </body>
</html>
