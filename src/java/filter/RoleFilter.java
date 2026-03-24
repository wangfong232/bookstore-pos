package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * RoleFilter – kiểm tra quyền truy cập theo vai trò cho từng URL.
 *
 * Thứ tự filter áp dụng (cấu hình trong web.xml hoặc annotation):
 *   AuthFilter  → kiểm tra đã đăng nhập chưa
 *   RoleFilter  → kiểm tra role có được phép truy cập URL này không
 *
 * Roles:
 *   1 = Manager        – quản lý toàn bộ hệ thống
 *   2 = Store Manager  – quản lý kho, nhà cung cấp, chấm công
 *   3 = Staff           – POS, chấm công, ca làm việc
 *   5 = Saler           – POS, khách hàng, khuyến mãi
 *
 * Mapping dựa trên admin-sidebar.jsp:
 *   /pos                           → 3, 5
 *   /staff/attendance-checkin      → 2, 3, 5
 *   /admin/shift-management        → 1, 2, 3, 5
 *   /staff/swap                    → 1, 2, 3, 5
 *   /admin/products                → 1
 *   /admin/categories              → 1
 *   /admin/brands                  → 1
 *   /admin/purchaseorder           → 1, 2, 3, 5  (quản lý kho – không giới hạn role)
 *   /admin/goodsreceipt            → 1, 2, 3, 5
 *   /admin/stocktake               → 1, 2, 3, 5
 *   /admin/stockdisposal           → 1, 2, 3, 5
 *   /admin/inventorytransaction    → 1, 2, 3, 5
 *   /admin/supplier                → 1, 2, 3, 5
 *   /admin/customers               → 2, 5
 *   /admin/customer-tiers          → 2, 5
 *   /admin/promotions              → 5
 *   /admin/employees               → 1
 *   /admin/attendance              → 1, 2
 *   /admin/hr-audit-log            → 1
 *   /admin/swap-approval           → 1, 2
 *   /purchaseorder                 → 2
 *   /supplier                      → 1, 2
 *   /customer-tiers                → 1
 *   /promotions                    → 1
 *   /staff/*                       → 1, 2, 3, 5
 *   /admin/*  (fallback)           → 1, 2
 *
 * Nếu không có quyền → redirect về /dashboard?error=unauthorized
 */
@WebFilter(urlPatterns = {"/admin/*", "/staff/*", "/supplier", "/purchaseorder",
                          "/pos", "/customer-tiers", "/promotions"})
public class RoleFilter implements Filter {

    /**
     * Map URL prefix → danh sách roleId được phép.
     * Sắp xếp từ cụ thể nhất → tổng quát nhất để match đúng.
     */
    private static final LinkedHashMap<String, Set<Integer>> ROLE_MAP = new LinkedHashMap<>();

    static {
        // ── Manager + Store Manager ──
        ROLE_MAP.put("/admin/hr-audit-log", roleSet(1, 2));
        ROLE_MAP.put("/admin/employees", roleSet(1, 2));
        ROLE_MAP.put("/admin/products", roleSet(1, 2));
        ROLE_MAP.put("/admin/categories", roleSet(1, 2));
        ROLE_MAP.put("/admin/brands", roleSet(1, 2));
        ROLE_MAP.put("/admin/combos", roleSet(1, 2));
        ROLE_MAP.put("/admin/attendance", roleSet(1, 2));
        ROLE_MAP.put("/admin/swap-approval", roleSet(1, 2));

        // ── Quản lý kho – tất cả roles ──
        ROLE_MAP.put("/admin/purchaseorder", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/goodsreceipt", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/stocktake", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/stockdisposal", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/inventorytransaction", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/supplier", roleSet(1, 2, 3, 5));

        // ── Khách hàng – Manager, Store Manager, Saler ──
        ROLE_MAP.put("/admin/customers", roleSet(1, 2, 5));
        ROLE_MAP.put("/admin/customer-tiers", roleSet(1, 2, 5));

        // ── Khuyến mãi – Manager, Store Manager, Saler ──
        ROLE_MAP.put("/admin/promotions", roleSet(1, 2, 5));

        // ── Ca làm việc – Tất cả roles ──
        ROLE_MAP.put("/admin/shift-management", roleSet(1, 2, 3, 5));

        // ── Toàn bộ /admin/* còn lại → Manager + Store Manager ──
        ROLE_MAP.put("/admin/", roleSet(1, 2));

        // ── /staff/* → mọi nhân viên đã đăng nhập ──
        ROLE_MAP.put("/staff/", roleSet(1, 2, 3, 5));

        // ── POS – Store Manager + Staff + Saler ──
        ROLE_MAP.put("/pos", roleSet(2, 3, 5));

        // ── Trang không có prefix /admin/ nhưng cần phân quyền ──
        ROLE_MAP.put("/supplier", roleSet(1, 2));
        ROLE_MAP.put("/purchaseorder", roleSet(1, 2));
        ROLE_MAP.put("/customer-tiers", roleSet(1, 2));
        ROLE_MAP.put("/promotions", roleSet(1, 2));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession         session  = request.getSession(false);

        // Nếu chưa đăng nhập → AuthFilter sẽ xử lý, cho qua filter này
        if (session == null || session.getAttribute("employeeId") == null) {
            chain.doFilter(req, res);
            return;
        }

        // Lấy roleId từ session
        Integer roleId = (Integer) session.getAttribute("roleId");

        String requestURI  = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path        = requestURI.substring(contextPath.length());

        Set<Integer> allowed = findAllowedRoles(path);

        if (allowed != null && (roleId == null || !allowed.contains(roleId))) {
            // Không có quyền → redirect về dashboard với thông báo lỗi
            response.sendRedirect(contextPath + "/dashboard?error=unauthorized");
            return;
        }

        chain.doFilter(req, res);
    }

    /**
     * Tìm danh sách role được phép cho đường dẫn này.
     * Ưu tiên theo thứ tự đăng ký (cụ thể trước, tổng quát sau).
     */
    private Set<Integer> findAllowedRoles(String path) {
        for (Map.Entry<String, Set<Integer>> entry : ROLE_MAP.entrySet()) {
            String prefix = entry.getKey();
            // So sánh chính xác hoặc theo prefix
            if (path.equals(prefix) || path.startsWith(prefix + "?")
                    || (prefix.endsWith("/") && path.startsWith(prefix))
                    || (!prefix.endsWith("/") && path.startsWith(prefix + "/"))
                    || (!prefix.endsWith("/") && path.startsWith(prefix + "?"))) {
                return entry.getValue();
            }
        }
        return null; // Không có rule → cho qua (AuthFilter đã bảo vệ)
    }

    private static Set<Integer> roleSet(Integer... roles) {
        return new HashSet<>(Arrays.asList(roles));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}

