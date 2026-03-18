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
 * Mapping mặc định:
 *   /admin/*               → roleId 1, 2  (Manager, Store Manager)
 *   /admin/attendance*     → roleId 1, 2  (chấm công / thống kê)
 *   /admin/hr-audit-log*   → roleId 1     (chỉ Manager)
 *   /staff/*               → roleId 1, 2, 3, 4, 5 (tất cả nhân viên đã đăng nhập)
 *
 * Nếu không có quyền → redirect về /dashboard?error=unauthorized
 */
@WebFilter(urlPatterns = {"/admin/*", "/staff/*", "/supplier", "/purchaseorder"})
public class RoleFilter implements Filter {

    /**
     * Map URL prefix → danh sách roleId được phép.
     * Sắp xếp từ cụ thể nhất → tổng quát nhất để match đúng.
     */
    private static final LinkedHashMap<String, Set<Integer>> ROLE_MAP = new LinkedHashMap<>();

    static {
        // ── Trang chỉ dành riêng cho Manager (roleId=1) ──
        ROLE_MAP.put("/admin/hr-audit-log", roleSet(1));

        // ── Trang dành cho Manager + Store Manager ──
        ROLE_MAP.put("/admin/attendance", roleSet(1, 2));
        ROLE_MAP.put("/admin/employees", roleSet(1, 2));
        ROLE_MAP.put("/admin/shift-management", roleSet(1, 2, 3, 5));
        ROLE_MAP.put("/admin/swap-approval", roleSet(1, 2));
        ROLE_MAP.put("/admin/promotions", roleSet(1, 2));
        ROLE_MAP.put("/admin/products", roleSet(1, 2));
        ROLE_MAP.put("/admin/categories", roleSet(1, 2));
        ROLE_MAP.put("/admin/brands", roleSet(1, 2));
        ROLE_MAP.put("/admin/customer-tiers", roleSet(1, 2));

        // ── Toàn bộ /admin/* còn lại → Manager + Store Manager ──
        ROLE_MAP.put("/admin/", roleSet(1, 2));

        // ── /staff/* → mọi nhân viên đã đăng nhập ──
        ROLE_MAP.put("/staff/", roleSet(1, 2, 3, 4, 5));

        // ── Trang không có prefix /admin/ nhưng cần phân quyền ──
        ROLE_MAP.put("/supplier", roleSet(1, 2));
        ROLE_MAP.put("/purchaseorder", roleSet(1, 2));
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
