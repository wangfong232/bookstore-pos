<%-- 
    Document   : po-detail
    Created on : Feb 24, 2026, 10:55:58 PM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Chi tiết đơn đặt hàng</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">

    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">
            <!-- Navbar & Sidebar -->
            <jsp:include page="include/admin-header.jsp" />
            <jsp:include page="include/admin-sidebar.jsp" />

            <div class="content-wrapper">
                <!-- Content Header (Page header) -->
                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>
                                    Chi tiết đơn đặt hàng
                                </h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/purchaseorder?action=list">Đơn đặt hàng</a></li>
                                    <li class="breadcrumb-item active">
                                        Chi tiết
                                    </li>
                                </ol>
                            </div>
                        </div>
                    </div><!-- /.container-fluid -->
                </section>

                <!-- Main content -->
                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                                <i class="icon fas fa-ban"></i> ${error}
                            </div>
                        </c:if>

                        <div class="card card-default">
                            <div class="card-header">
                                <h3 class="card-title"><strong>Thông tin đơn hàng</strong></h3>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-4">
                                        <p><strong>Mã ĐĐH:</strong> <span class="text-primary">${po.poNumber}</span></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p><strong>Nhà cung cấp:</strong> ${not empty po.supplierName ? po.supplierName : po.supplierId}</p>
                                        <p><strong>Ngày tạo:</strong> ${po.createdAt}</p>
                                    </div>
                                    <div class="col-md-4">
                                        <p><strong>Ngày giao dự kiến:</strong> 
                                            <c:choose>
                                                <c:when test="${not empty po.expectedDate}">
                                                    ${po.expectedDate}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">(Chưa xác định)</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p><strong>Trạng thái: </strong> 
                                            <c:choose>
                                                <c:when test="${po.status == 'PENDING_APPROVAL'}"><span class="badge badge-warning">Chờ duyệt</span></c:when>
                                                <c:when test="${po.status == 'APPROVED'}"><span class="badge badge-success">Đã duyệt</span></c:when>
                                                <c:when test="${po.status == 'CANCELLED'}"><span class="badge badge-danger">Đã hủy</span></c:when>
                                                <c:when test="${po.status == 'REJECTED'}"><span class="badge badge-danger">Từ chối</span></c:when>
                                                <c:otherwise><span class="badge badge-secondary">${po.status}</span></c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                                <hr>

                                <h5><strong>Chi tiết sản phẩm</strong></h5>
                                <div class="table-responsive">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr>
                                                <th style="width: 50px">STT</th>
                                                <th>Sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Đơn giá</th>
                                                <th>Giảm giá</th> 
                                                <th>Thành tiền</th>
                                                <th>Ghi chú</th>
                                            </tr> 
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${items}" varStatus="status">
                                                <tr>
                                                    <td>${status.index + 1}</td>
                                                    <td>${item.productName}</td>
                                                    <td>${item.quantityOrdered}</td>
                                                    <td><c:if test="${item.unitPrice != null}"><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="đ"/></c:if></td>
                                                    <td><c:if test="${item.discountValue != null}"><fmt:formatNumber value="${item.discountValue}" type="currency" currencySymbol="đ"/></c:if></td>
                                                    <td><strong class="text-success"><c:if test="${item.lineTotal != null}"><fmt:formatNumber value="${item.lineTotal}" type="currency" currencySymbol="đ"/></c:if></strong></td>
                                                    <td>${item.notes}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="row mt-3">
                                    <div class="col-md-6">
                                        <p><strong>Ghi chú:</strong> <br> 
                                            <em>${not empty po.notes ? po.notes : 'Không có ghi chú'}</em>
                                        </p>
                                    </div>
                                    <div class="col-md-6 text-right">
                                        <p>Tổng phụ: <strong><c:if test="${po.subtotal != null}"><fmt:formatNumber value="${po.subtotal}" type="currency" currencySymbol="đ"/></c:if></strong></p>
                                        <p>Tổng giảm giá: <strong><c:if test="${po.totalDiscount != null}"><fmt:formatNumber value="${po.totalDiscount}" type="currency" currencySymbol="đ"/></c:if></strong></p>
                                        <h4>Tổng tiền: <strong class="text-danger"><c:if test="${po.totalAmount != null}"><fmt:formatNumber value="${po.totalAmount}" type="currency" currencySymbol="đ"/></c:if></strong></h4>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5><strong>Thông tin kiểm soát</strong></h5>
                                    <div class="row text-muted" style="font-size: 0.9rem;">
                                        <div class="col-md-6">
                                            Người tạo: <strong>${not empty po.createdByName ? po.createdByName : po.createdBy}</strong> - ${po.createdAt}
                                    </div>
                                    <div class="col-md-6">
                                        Người duyệt:
                                        <c:choose>
                                            <c:when test="${not empty po.approvedBy}">
                                                <strong>${not empty po.approvedByName ? po.approvedByName : po.approvedBy}</strong> - ${po.approvedAt}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">(Chưa duyệt)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="mt-4 pt-3 border-top">
                                    <!-- Actions for Manager/Admin when status is PENDING_APPROVAL -->
                                    <c:if test="${po.status == 'PENDING_APPROVAL' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <!-- Approve Form -->
                                                <form method="post" action="${pageContext.request.contextPath}/admin/purchaseorder"
                                                      onsubmit="return confirm('Bạn có chắc muốn duyệt đơn hàng này?');">
                                                    <input type="hidden" name="action" value="approve" />
                                                    <input type="hidden" name="poNumber" value="${po.poNumber}" />
                                                    <input type="hidden" name="by" value="${sessionScope.employeeId}" />

                                                    <button type="submit" class="btn btn-success btn-lg">
                                                        <i class="fas fa-check"></i> Duyệt đơn
                                                    </button>
                                                </form>
                                            </div>
                                            <div class="col-md-6">
                                                <!-- Reject Form -->
                                                <form method="post" action="${pageContext.request.contextPath}/admin/purchaseorder"
                                                      onsubmit="return validateRejectForm(this);">
                                                    <input type="hidden" name="action" value="reject" />
                                                    <input type="hidden" name="poNumber" value="${po.poNumber}" />
                                                    <input type="hidden" name="by" value="${sessionScope.employeeId}" />

                                                    <div class="form-group">
                                                        <label for="rejectReason">Lý do từ chối: <span class="text-danger">*</span></label>
                                                        <textarea class="form-control" id="rejectReason" name="reason" rows="3"
                                                                  placeholder="Nhập lý do từ chối..." required></textarea>
                                                    </div>

                                                    <button type="submit" class="btn btn-danger btn-lg">
                                                        <i class="fas fa-times"></i> Từ chối
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                        <hr class="my-3">
                                    </c:if>

                                    <!-- Actions for Manager/Admin when status is APPROVED -->
                                    <c:if test="${po.status == 'APPROVED' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager'  || sessionScope.roleName == 'Admin')}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/purchaseorder"
                                              onsubmit="return validateCancelForm(this);">
                                            <input type="hidden" name="action" value="cancel" />
                                            <input type="hidden" name="poNumber" value="${po.poNumber}" />
                                            <input type="hidden" name="by" value="${sessionScope.employeeId}" />

                                            <div class="form-group">
                                                <label for="cancelReason">Lý do hủy: <span class="text-danger">*</span></label>
                                                <textarea class="form-control" id="cancelReason" name="reason" rows="3"
                                                          placeholder="Nhập lý do hủy đơn..." required></textarea>
                                            </div>

                                            <button type="submit" class="btn btn-danger btn-lg">
                                                <i class="fas fa-ban"></i> Hủy đơn
                                            </button>
                                        </form>
                                        <hr class="my-3">
                                    </c:if>

                                    <!-- Actions for Staff (Creator) when status is PENDING or REJECTED -->
                                    <c:if test="${(po.status == 'PENDING_APPROVAL' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin' || sessionScope.employeeId == po.createdBy)) 
                                                  || (po.status == 'REJECTED' && sessionScope.employeeId == po.createdBy)}">
                                          <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=edit&poNumber=${po.poNumber}" class="btn btn-primary btn-lg">
                                              <i class="fas fa-edit"></i> Sửa đơn
                                          </a>
                                    </c:if>

                                    <a href="${pageContext.request.contextPath}/admin/purchaseorder?action=list"
                                       class="btn btn-secondary btn-lg">
                                        <i class="fas fa-arrow-left"></i> Quay lại
                                    </a>
                                </div><!-- ./mt-4 -->
                            </div><!-- ./card-body -->
                        </div><!-- ./card -->

                    </div><!-- ./container-fluid -->
                </section><!-- ./content -->

            </div><!-- ./content-wrapper -->

            <!-- Main Footer -->
            <jsp:include page="include/admin-footer.jsp"/>
        </div><!-- ./wrapper -->

        <script>
            function validateRejectForm(form) {
                var reason = form.querySelector('[name="reason"]').value.trim();
                if (reason === '') {
                    alert('Vui lòng nhập lý do từ chối!');
                    return false;
                }
                return confirm('Bạn có chắc muốn từ chối đơn hàng này?');
            }

            function validateCancelForm(form) {
                var reason = form.querySelector('[name="reason"]').value.trim();
                if (reason === '') {
                    alert('Vui lòng nhập lý do hủy đơn!');
                    return false;
                }
                return confirm('Bạn có chắc muốn hủy đơn hàng này?');
            }
        </script>
    </body>
</html>
