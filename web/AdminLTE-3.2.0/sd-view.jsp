<%-- 
    Document   : sd-view
    Created on : Mar 11, 2026, 10:14:05 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chi tiết phiếu xuất hủy - ${sd.disposalNumber}</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
        <style>
            .audit-block {
                background: #f8f9fa;
                border-left: 4px solid #007bff;
                padding: 12px 16px;
                border-radius: 4px;
            }
            .audit-block.approved {
                border-color: #28a745;
            }
            .audit-block.rejected {
                border-color: #dc3545;
            }
            .audit-block.completed {
                border-color: #6f42c1;
            }
        </style>
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
                                <h1><i class="fas fa-trash-alt"></i>Phiếu xuất hủy: <strong>${sd.disposalNumber}</strong></h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/stockdisposal?action=list">Xuất hủy hàng</a></li>
                                    <li class="breadcrumb-item active">${sd.disposalNumber}</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section><section>
                    <div class="container-fluid">

                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success') ? 'alert-success' : 'alert-danger'} alert-dismissible">
                                <button type="button" class="close" data-dismiss="alert">x;</button>
                                <i class="icon fas ${msg.startsWith('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_approve'}">Duyệt phiếu xuất hủy thành công!</c:when>
                                    <c:when test="${msg == 'success_reject'}">Đã từ chối phiếu xuất hủy.</c:when>
                                    <c:when test="${msg == 'success_complete'}">Hoàn tất xuất hủy! Tồn kho đã được giảm tương ứng.</c:when>
                                    <c:when test="${msg == 'fail_self_approve'}">Người tạo phiếu không được tự duyệt!</c:when>
                                    <c:when test="${msg == 'fail_approve'}">Duyệt thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_reject'}">Từ chối thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_reject_noreason'}">Vui lòng nhập lý do từ chối.</c:when>
                                    <c:when test="${msg == 'fail_complete'}">Hoàn tất thất bại. Tồn kho có thể không đủ.</c:when>
                                    <c:when test="${msg == 'fail_no_physical_confirm'}">Vui lòng xác nhận đã hủy vật lý thực tế trước khi hoàn tất.</c:when>
                                    <c:otherwise>${msg}</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="row">
                            <div class="col-md-4">
                                <div class="card card-danger card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-info-circle"></i>Thông tin phiếu</h3>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Mã phiếu: 
                                            </div>
                                            <div class="col-7">
                                                <strong>${sd.disposalNumber}</strong>
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Ngày tạo
                                            </div>
                                            <div class="col-7">
                                                ${sd.disposalDate !=null ? sd.disposalDateFormatted : ''}
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Lý do chung: 
                                            </div>
                                            <div class="col-7">
                                                <c:choose>
                                                    <c:when test="${sd.disposalReason == 'DAMAGED'}">
                                                        <span class="badge badge-warning">Hỏng hóc</span>
                                                    </c:when>
                                                    <c:when test="${sd.disposalReason == 'EXPIRED'}">
                                                        <span class="badge badge-warning">Hết hạn</span>     
                                                    </c:when>
                                                    <c:when test="${sd.disposalReason == 'DEFECTIVE'}">
                                                        <span class="badge badge-warning">Lỗi sản phẩm</span>
                                                    </c:when>
                                                    <c:otherwise>                                                      
                                                        <span class="badge badge-secondary">Khác</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Trạng thái:
                                            </div>
                                            <div class="col-7">
                                                <c:choose>
                                                    <c:when test="${sd.status == 'PENDING_APPROVAL'}">
                                                        <span class="badge badge-warning">Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${sd.status == 'APPROVED'}">
                                                        <span class="badge badge-info">Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${sd.status == 'REJECTED'}">
                                                        <span class="badge badge-danger">Từ chối</span>
                                                    </c:when>
                                                    <c:when test="${sd.status == 'COMPLETED'}">
                                                        <span class="badge badge-success">Hoàn tất</span>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Tổng SP: 
                                            </div>
                                            <div class="col-7">
                                                ${sd.totalQuantity} sản phẩm
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-5 font-weight-bold">
                                                Tổng gía trị:
                                            </div>
                                            <div class="col-7">
                                                <fmt:formatNumber value= "${sd.totalValue}" type="number" maxFractionDigits="0"/>đ
                                            </div>
                                        </div>
                                        <c:if test="${not empty sd.notes}">
                                            <div class="row mb-2">
                                                <div class="col-5 font-weight-bold">Ghi chú:</div>
                                                <div class="col-7 text-muted">${sd.notes}</div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="card">
                                    <div class="card-body">
                                        <div class="audit-block mb-3">
                                            <div class="font-weight-bold mb-1">Người tạo:</div>
                                            <div>${not empty sd.createdByName ? sd.createdByName : sd.createdBy}</div>
                                            <small class="text-muted">${sd.createdAt != null ? sd.createdAtFormatted : ''}</small>
                                        </div>

                                        <c:if test="${not empty sd.approvedByName or sd.approvedBy > 0}">
                                            <div class="audit-block mb-3 ${sd.status == 'REJECTED' ? 'rejected' : 'approved' }">
                                                <div class="font-weight-bold mb-1">
                                                    ${sd.status == 'REJECTED' ? 'Người từ chối' : 'Người duyệt'}
                                                </div>
                                                <div>
                                                    <div>${not empty sd.approvedByName ? sd.approvedByName :  sd.approvedBy }</div>
                                                    <small class="text-muted">${sd.approvedAt != null ? sd.approvedAtFormatted : ''}</small>
                                                </div>
                                                <c:if test="${not empty sd.rejectionReason}">
                                                    <div class="mt-1"><strong>Lý do:</strong> ${sd.rejectionReason}</div>
                                                </c:if>
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty sd.disposedByName or sd.disposedBy > 0}">
                                            <div class="audit-block completed">
                                                <div class="font-weight-bold mb-1 text-purple">Người xử lý hủy</div>
                                                <div>${not empty sd.disposedByName ? sd.disposedByName : sd.disposedBy }</div>
                                                <small class="text-muted">${sd.disposedAt != null ? sd.disposedAtFormatted : ''}</small>
                                            </div>

                                        </c:if>
                                    </div>
                                </div>

                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-tasks"></i> Thao tác</h3>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${sd.status == 'PENDING_APPROVAL' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">

                                            <c:choose>
                                                <c:when test="${sessionScope.employeeId == sd.createdBy && sessionScope.roleName != 'Admin'}">
                                                    <div class="alert alert-info text-center">
                                                        <i class="fas fa-info-circle"></i> Phiếu xuất hủy đang chờ một Quản lý khác duyệt. Bạn không thể tự duyệt phiếu do mình tạo.
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/admin/stockdisposal" method="post" class="mb-3">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="number" value="${sd.disposalNumber}">
                                                        <button type="submit" class="btn btn-success btn-block" 
                                                                onclick="return confirm('Xác nhận duyệt phiếu xuất hủy này?')">
                                                            <i class="fas fa-check"></i> Duyệt phiếu
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/admin/stockdisposal" method="post" class="mb-3" id="rejectForm">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="number" value="${sd.disposalNumber}">
                                                        <input type="hidden" name="rejectionReason" id="hiddenRejectReason">
                                                        <button type="button" class="btn btn-danger btn-block" onclick="confirmReject()">
                                                            <i class="fas fa-times"></i> Từ chối
                                                        </button>
                                                    </form>

                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>

                                        <c:if test="${sd.status == 'APPROVED' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">
                                            <form action="${pageContext.request.contextPath}/admin/stockdisposal" method="post" id="completeForm" class="mb-3">
                                                <input type="hidden" name="action" value="complete">
                                                <input type="hidden" name="number" value="${sd.disposalNumber}">
                                                <input type="hidden" name="physicalConfirmed" id="physicalConfirmedInput" value="false">
                                                <div class="form-check mb-3">
                                                    <input class="form-check-input" type="checkbox" id="chkPhysical">
                                                    <label class="form-check-label font-weight-bold" for="chkPhysical">
                                                        Xác nhận đã hủy vật lý thực tế
                                                    </label>
                                                </div>
                                                <button type="submit" class="btn btn-dark btn-block" id="btnComplete" disabled>
                                                    <i class="fas fa-check-double"></i> Hoàn tất xuất hủy
                                                </button>
                                            </form>
                                        </c:if>

                                        <a href="${pageContext.request.contextPath}/admin/stockdisposal?action=list" class="btn btn-default btn-block">
                                            <i class="fas fa-arrow-left"></i> Quay lại danh sách
                                        </a>
                                    </div>
                                </div>
                            </div>


                            <div class="col-md-8">
                                <div class="card-header">
                                    <h3 class="card-title">
                                        <i class="fas fa-list-ul"></i> Chi tiết sản phẩm xuất hủy
                                        <span class="badge badge-secondary ml-2">${sd.totalQuantity} sản phẩm</span>
                                    </h3>
                                </div>
                                <div class="card-body p-0">
                                    <c:choose>
                                        <c:when test="${not empty sd.details}">
                                            <table class="table table-bordered table-striped mb-0">
                                                <thead class="thead-light">
                                                    <tr>
                                                        <th class="text-center" style="width:50px">STT</th>
                                                        <th>SKU</th>
                                                        <th>Tên sản phẩm</th>
                                                        <th class="text-center" style="width:100px">Tồn trước</th>
                                                        <th class="text-center" style="width:100px">SL xuất hủy</th>
                                                        <th class="text-right" style="width:120px">Giá trị</th>
                                                        <th>Ghi chú SP</th> </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="d" items="${sd.details}" varStatus="status">
                                                        <tr>
                                                            <td class="text-center">${status.index + 1}</td>
                                                            <td>${d.productSku}</td>
                                                            <td>${d.productName}</td>
                                                            <td class="text-center">${d.currentStock}</td>
                                                            <td class="text-center text-danger font-weight-bold">${d.quantity}</td>
                                                            <td class="text-right">
                                                                <fmt:formatNumber value="${d.lineTotal}" type="number" maxFractionDigits="0"/>đ
                                                            </td>
                                                            <td class="text-muted">${not empty d.specificReason ? d.specificReason : '-'}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                                <tfoot>
                                                    <tr class="font-weight-bold">
                                                        <td colspan="4" class="text-right">Tổng cộng:</td>
                                                        <td class="text-center text-danger">${sd.totalQuantity}</td>
                                                        <td class="text-right">
                                                            <fmt:formatNumber value="${sd.totalValue}" type="number" maxFractionDigits="0"/>đ
                                                        </td>
                                                        <td></td>
                                                    </tr>
                                                </tfoot>
                                            </table>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-center py-4 text-muted">Không có chi tiết sản phẩm.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div></div>
                </section>

            </div><jsp:include page="include/admin-footer.jsp"/>
        </div><script>
            var chk = document.getElementById('chkPhysical');
            var btn = document.getElementById('btnComplete');
            var hdnConfirmed = document.getElementById('physicalConfirmedInput');

            if (chk) {
                chk.addEventListener('change', function () {
                    btn.disabled = !chk.checked;
                    if (hdnConfirmed) {
                        hdnConfirmed.value = chk.checked ? 'true' : 'false';
                    }
                });
            }

            function confirmReject() {
                var reason = prompt("Vui lòng nhập lý do từ chối phiếu xuất hủy này:");
                if (reason != null && reason.trim() !== "") {
                    document.getElementById("hiddenRejectReason").value = reason;
                    document.getElementById("rejectForm").submit();
                } else if (reason != null) {
                    alert("Lý do từ chối không được để trống!");
                }
            }
        </script>
    </body>
</html>