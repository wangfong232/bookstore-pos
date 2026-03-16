<%-- 
    Document   : st-view
    Created on : Mar 7, 2026, 12:19:30 AM
    Author     : qp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chi tiết phiếu kiểm kê - ${st.stockTakeNumber}</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/AdminLTE-3.2.0/dist/css/adminlte.min.css">
        <style>
            .variance-loss    {
                color: #dc3545;
                font-weight: 600;
            }
            .variance-surplus {
                color: #28a745;
                font-weight: 600;
            }
            .variance-zero    {
                color: #6c757d;
            }
            .audit-block {
                background: #f8f9fa;
                border-left: 4px solid #007bff;
                padding: 12px 16px;
                border-radius: 4px;
            }
            .audit-block.recount {
                border-color: #ffc107;
            }

        </style>
    </head>
    <body class="hold-transition sidebar-mini layout-fixed">
        <div class="wrapper">

            <jsp:include page="include/admin-header.jsp"/>
            <jsp:include page="include/admin-sidebar.jsp"/>

            <!-- Content Wrapper -->
            <div class="content-wrapper">

                <section class="content-header">
                    <div class="container-fluid">
                        <div class="row mb-2">
                            <div class="col-sm-6">
                                <h1>
                                    <i class="fas fa-clipboard-check"></i>
                                    Phiếu kiểm kê: <strong>${st.stockTakeNumber}</strong>
                                </h1>
                            </div>
                            <div class="col-sm-6">
                                <ol class="breadcrumb float-sm-right">
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/dashboard">Home</a></li>
                                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/stocktake?action=list">Kiểm kê kho</a></li>
                                    <li class="breadcrumb-item active">${st.stockTakeNumber}</li>
                                </ol>
                            </div>
                        </div>
                    </div>
                </section><!-- /content-header -->

                <section class="content">
                    <div class="container-fluid">

                        <c:if test="${not empty msg}">
                            <div class="alert ${msg.startsWith('success') ? 'alert-success' : 'alert-danger'} alert-dismissible fade show">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <i class="icon fas ${msg.startsWith('success') ? 'fa-check' : 'fa-ban'}"></i>
                                <c:choose>
                                    <c:when test="${msg == 'success_save'}">Tạo phiếu kiểm kê thành công!</c:when>
                                    <c:when test="${msg == 'success_submit'}">Đã gửi phiếu lên chờ duyệt!</c:when>
                                    <c:when test="${msg == 'success_approve'}">Duyệt phiếu kiểm kê thành công!</c:when>
                                    <c:when test="${msg == 'success_recount'}">Yêu cầu kiểm kê lại đã được gửi!</c:when>
                                    <c:when test="${msg == 'fail_self_approve'}">Người tạo phiếu không được tự duyệt!</c:when>
                                    <c:when test="${msg == 'fail_submit'}">Gửi duyệt thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_approve'}">Duyệt thất bại. Vui lòng thử lại.</c:when>
                                    <c:when test="${msg == 'fail_recount'}">Yêu cầu kiểm kê lại thất bại.</c:when>
                                    <c:when test="${msg == 'fail_recount_noreason'}">Vui lòng nhập lý do yêu cầu kiểm kê lại.</c:when>
                                    <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại!</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>


                        <div class="row">

                            <div class="col-md-4">

                                <div class="card card-primary card-outline">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-info-circle"></i> Thông tin phiếu</h3>
                                    </div>

                                    <div class="card-body">
                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Mã phiếu:</div>
                                            <div class="col-sm-7"><strong>${st.stockTakeNumber}</strong></div>
                                        </div>  
                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Ngày kiểm:</div>
                                            <div class="col-sm-7">${st.stockTakeDate}</div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Phạm vi:</div>
                                            <div class="col-sm-7">
                                                <c:choose>
                                                    <c:when test="${st.scopeType == 'ALL'}">Toàn bộ</c:when>
                                                    <c:when test="${st.scopeType=='CATEGORY'}">Danh mục: ${scopeValue}</c:when>
                                                    <c:otherwise>Chọn lọc</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Trạng thái:</div>
                                            <div class="col-sm-7">
                                                <c:choose>
                                                    <c:when test="${st.status == 'IN_PROGRESS'}">
                                                        <span class="badge badge-warning">Đang thực hiện</span>
                                                    </c:when>

                                                    <c:when test="${st.status == 'PENDING_APPROVAL'}">
                                                        <span class="badge badge-info">Chờ duyệt</span>
                                                    </c:when>

                                                    <c:when test="${st.status == 'COMPLETED'}">
                                                        <span class="badge badge-success">Đã hoàn thành</span>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Số sản phẩm:</div>
                                            <div class="col-sm-7">${st.totalItems}</div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Tổng chênh lệch SL:</div>
                                            <div class="col-sm-7">

                                                <c:choose>

                                                    <c:when test="${st.totalVarianceQty < 0}">
                                                        <span class="variance-loss">${st.totalVarianceQty}</span>
                                                    </c:when>

                                                    <c:when test="${st.totalVarianceQty > 0}">
                                                        <span class="variance-surplus">+${st.totalVarianceQty}</span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="variance-zero">0</span>
                                                    </c:otherwise>

                                                </c:choose>

                                            </div>
                                        </div>

                                        <div class="row mb-2">
                                            <div class="col-sm-5 font-weight-bold">Tổng tiền CL:</div>
                                            <div class="col-sm-7">
                                                <c:choose>
                                                    <c:when test="${st.totalVarianceValue < 0}">
                                                        <span class="variance-loss"><fmt:formatNumber value="${st.totalVarianceValue}" type="currency" currencySymbol="đ"/> </span>
                                                    </c:when>
                                                    <c:when test="${st.totalVarianceValue > 0}">
                                                        <span class="variance-surplus">+<fmt:formatNumber value="${st.totalVarianceValue}" type="currency" currencySymbol="đ"/> </span>
                                                    </c:when>
                                                    <c:otherwise><span class="variance-zero">0 đ</span></c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <c:if test="${not empty st.notes}">
                                            <div class="row mb-2">
                                                <div class="col-sm-5 font-weight-bold">Ghi chú:</div>
                                                <div class="col-sm-7">${st.notes}</div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div><!-- /info -->

                                <!--<!-- audit -->
                                <div class="card card-outline card-secondary">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-history"></i> Thông tin kiểm toán</h3>
                                    </div>

                                    <div class="card-body">
                                        <div class="audit-block mb-3">
                                            <div class="small text-muted mb-1">Người kiểm kê</div>
                                            <strong>${st.createdByName}</strong>
                                            <c:if test="${not empty st.createdAt}">
                                                <div class="small text-muted">Tạo lúc: ${st.createdAt}</div>
                                            </c:if>
                                            <c:if test="${not empty st.submittedAt}">
                                                <div class="small text-muted">Gửi duyệt: ${st.submittedAt}</div>
                                            </c:if>
                                        </div>

                                        <c:if test="${not empty st.approvedByName}">
                                            <div class="audit-block" style="border-color:#28a745">
                                                <div class="small text-muted mb-1">Người duyệt</div>
                                                <strong>${st.approvedByName}</strong>
                                                <c:if test="${not empty st.approvedAt}">
                                                    <div class="small text-muted">Duyệt lúc: ${st.approvedAt}</div>
                                                </c:if>
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty st.recountReason}">
                                            <div class="audit-block recount mt-3">
                                                <div class="small text-muted mb-1">
                                                    <i class="fas fa-redo"></i> Yêu cầu kiểm kê lại
                                                </div>
                                                <strong>${st.recountByName}</strong>
                                                <c:if test="${not empty st.recountRequestedAt}">
                                                    <div class="small text-muted">Lúc: ${st.recountRequestedAt}</div>
                                                </c:if>
                                                <div class="mt-1 text-warning"><em>${st.recountReason}</em></div>
                                            </div>
                                        </c:if>
                                    </div>

                                </div><!-- /audit -->

                                <!-- Action -->
                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title"><i class="fas fa-cogs"></i> Thao tác</h3>
                                    </div>

                                    <div class="card-body">
                                        <c:if test="${st.status == 'IN_PROGRESS' && (sessionScope.employeeId == st.createdBy || sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">                                           
                                            <form action="${pageContext.request.contextPath}/admin/stocktake" method="post">
                                                <input type="hidden" name="action" value="submit">
                                                <input type="hidden" name="number" value="${st.stockTakeNumber}">
                                                <button type="submit" class="btn btn-primary btn-block mb-2"
                                                        onclick="return confirm('Gửi phiếu kiểm kê lên chờ duyệt?')">
                                                    <i class="fas fa-paper-plane"></i> Gửi duyệt
                                                </button>
                                            </form>
                                                
                                            <a href="${pageContext.request.contextPath}/admin/stocktake?action=step2&stNumber=${st.stockTakeNumber}" 
                                               class="btn btn-info btn-block mb-2">
                                                <i class="fas fa-edit"></i> Thực hiện kiểm kê lại
                                            </a>
                                        </c:if> 

                                        <c:if test="${st.status == 'PENDING_APPROVAL' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">
                                            <form action="${pageContext.request.contextPath}/admin/stocktake" method="post">
                                                <input type="hidden" name="action" value="approve">
                                                <input type="hidden" name="number" value="${st.stockTakeNumber}">
                                                <button type="submit" class="btn btn-success btn-block mb-2"
                                                        onclick="return confirm('Duyệt phiếu kiểm kê này?')">
                                                    <i class="fas fa-check-circle"></i> Duyệt
                                                </button>
                                            </form>

                                            <c:if test="${st.status == 'PENDING_APPROVAL' && (sessionScope.roleName == 'Manager' || sessionScope.roleName == 'Store Manager' || sessionScope.roleName == 'Admin')}">
                                                <button type="button" class="btn btn-warning btn-block mb-2"
                                                        data-toggle="modal" data-target="#recountModal">
                                                    <i class="fas fa-redo"></i> Yêu cầu kiểm lại
                                                </button>
                                            </c:if>
                                        </c:if>

                                        <a href="${pageContext.request.contextPath}/admin/stocktake?action=list"
                                           class="btn btn-default btn-block">
                                            <i class="fas fa-arrow-left"></i> Quay lại danh sách
                                        </a>

                                    </div>
                                </div>

                            </div><!-- /lef-col(infor + action) -->

                            <!-- right-col: detail table -->

                            <div class="col-md-8">
                                <div class="card">
                                    <div class="card-header">
                                        <h3 class="card-title">
                                            <i class="fas fa-table"></i> Chi tiết kiểm kê
                                            <span class="badge badge-secondary ml-2">${st.totalItems} sản phẩm</span>
                                        </h3>
                                    </div>
                                    <div class="card-body p-0">
                                        <c:choose>
                                            <c:when test="${not empty st.details}">
                                                <div style="overflow-x:auto;">
                                                    <table class="table table-hover table-sm mb-0">
                                                        <thead class="thead-light">
                                                            <tr>
                                                                <th>#</th>
                                                                <th>SKU</th>
                                                                <th>Tên sản phẩm</th>
                                                                <th class="text-center">Tồn HT</th>
                                                                <th class="text-center">Thực tế</th>
                                                                <th class="text-center">Chênh lệch SL</th>
                                                                <th class="text-right">Tiền chênh lệch</th>
                                                                <th>Lý do</th>
                                                                <th>Ghi chú</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="d" items="${st.details}" varStatus="status">
                                                                <tr>
                                                                    <td>${status.index + 1}</td>
                                                                    <td>${d.productSku}</td>
                                                                    <td>${d.productName}</td>
                                                                    <td class="text-center">${d.systemQuantity}</td>
                                                                    <td class="text-center"><strong>${d.actualQuantity}</strong></td>
                                                                    <td class="text-center">
                                                                        <c:set var="variance" value="${d.actualQuantity - d.systemQuantity}"/>
                                                                        <c:choose>
                                                                            <c:when test="${variance<0}">
                                                                                <span class="variance-loss">${variance}</span>
                                                                            </c:when>
                                                                            <c:when test="${variance>0}">
                                                                                <span class="variance-surplus">+${variance}</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="variance-zero">0</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>

                                                                    <td class="text-right">
                                                                        <c:set var="varVal" value="${d.varianceValue}"/>
                                                                        <c:choose>
                                                                            <c:when test="${varVal < 0}">
                                                                                <span class="variance-loss"><fmt:formatNumber value="${varVal}" type="currency" currencySymbol="đ"/></span>
                                                                            </c:when>
                                                                            <c:when test="${varVal > 0}">
                                                                                <span class="variance-surplus">+<fmt:formatNumber value="${varVal}" type="currency" currencySymbol="đ"/></span>
                                                                            </c:when>
                                                                            <c:otherwise><span class="variance-zero">0 đ</span></c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when test="${d.varianceReason == 'LOSS'}">Mất hàng</c:when>
                                                                            <c:when test="${d.varianceReason == 'DAMAGE'}">Hư hỏng</c:when>
                                                                            <c:when test="${d.varianceReason == 'THEFT'}">Trộm cắp</c:when>
                                                                            <c:when test="${d.varianceReason == 'ERROR'}">Lỗi nhập liệu</c:when>
                                                                            <c:when test="${d.varianceReason == 'OTHER'}">Khác</c:when>
                                                                            <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when test="${not empty d.notes}">${d.notes}</c:when>
                                                                            <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                        <tfoot class="thead-light">
                                                            <tr>
                                                                <td colspan="5" class="font-weight-bold">Tổng chênh lệch: </td>
                                                                <td class="text-center">
                                                                    <c:choose>
                                                                        <c:when test="${st.totalVarianceQty < 0}">
                                                                            <span class="variance-loss">${st.totalVarianceQty}</span>
                                                                        </c:when>
                                                                        <c:when test="${st.totalVarianceQty > 0}">
                                                                            <span class="variance-surplus">+${st.totalVarianceQty}</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="variance-zero">0</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="text-right font-weight-bold">
                                                                    <c:choose>
                                                                        <c:when test="${st.totalVarianceValue < 0}">
                                                                            <span class="variance-loss"><fmt:formatNumber value="${st.totalVarianceValue}" type="currency" currencySymbol="đ"/></span>
                                                                        </c:when>
                                                                        <c:when test="${st.totalVarianceValue > 0}">
                                                                            <span class="variance-surplus">+<fmt:formatNumber value="${st.totalVarianceValue}" type="currency" currencySymbol="đ"/> </span>
                                                                        </c:when>
                                                                        <c:otherwise><span class="variance-zero">0 đ</span></c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td colspan="2"></td>
                                                            </tr>
                                                        </tfoot>
                                                    </table>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="text-center p-4 text-muted">
                                                    <i class="fas fa-inbox fa-2x mb-2"></i>
                                                    <p>Chưa có chi tiết kiểm kê.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div><!-- /detail card -->
                            </div><!-- /col-8 -->   <!-- ./right-col: detail table -->


                        </div><!-- ./row -->

                    </div><!-- /container-f -->
                </section><!-- /content -->

            </div><!-- /content-warpper -->
            <jsp:include page="include/admin-footer.jsp"/>
        </div><!-- /wrapper -->

        <!-- recount modal-->
        <div class="modal fade" id="recountModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/admin/stocktake" method="post">
                        <input type="hidden" name="action" value="recount">
                        <input type="hidden" name="number" value="${st.stockTakeNumber}">
                        <div class="modal-header">
                            <h5 class="modal-title"><i class="fas fa-redo text-warning"></i> Yêu cầu kiểm kê lại</h5>
                            <button type="button" class="close" data-dismiss="modal">x</button>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label for="recountReason">Lý do yêu cầu kiểm kê lại <span class="text-danger">*</span></label>
                                <textarea class="form-control" name="recountReason" id="recountReason" rows="4"
                                          placeholder="Nhập lý do cần kiểm kê lại..." required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-redo"></i> Gửi yêu cầu kiểm kê lại
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div><!-- /modal -->
    </body>
</html>
