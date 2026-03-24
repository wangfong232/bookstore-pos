package controller;

import DAO.*;
import entity.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "PromotionController", urlPatterns = { "/promotions" })
public class PromotionController extends HttpServlet {

    private final PromotionDAO promotionDAO = new PromotionDAO();
    private final PromotionDiscountValueDAO discountDAO = new PromotionDiscountValueDAO();
    private final PromotionConditionDAO conditionDAO = new PromotionConditionDAO();
    private final PromotionApplicableCategoryDAO categoryDAO = new PromotionApplicableCategoryDAO();
    private final PromotionApplicableProductDAO productDAO = new PromotionApplicableProductDAO();
    private final PromotionCustomerTierDAO tierDAO = new PromotionCustomerTierDAO();

    private static final DateTimeFormatter FORM_DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null)
            action = "";

        switch (action) {
            case "create":
                request.setAttribute("customerTiers", new CustomerTierDAO().getAll());
                request.setAttribute("categories", new CategoryDAO().getAllActiveCategories());
                request.getRequestDispatcher("/AdminLTE-3.2.0/promotion-create.jsp").forward(request, response);
                break;
            case "edit":
                handleEditForm(request, response);
                break;
            case "toggle":
                handleToggle(request, response);
                break;
            default:
                handleList(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null)
            action = "";

        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/promotions");
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String status = request.getParameter("status");
        String type = request.getParameter("type");

        List<Promotion> promotions = promotionDAO.getByFilter(status, type);
        for (Promotion p : promotions) {
            p.setDiscount(discountDAO.getByPromotionId(p.getPromotionID()));
        }
        request.setAttribute("promotions", promotions);

        request.getRequestDispatcher("/AdminLTE-3.2.0/promotion-list.jsp").forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/promotions");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            Promotion promotion = promotionDAO.getById(id);
            if (promotion == null) {
                request.setAttribute("errorMessage", "Không tìm thấy chương trình khuyến mãi có ID = " + id);
                handleList(request, response);
                return;
            }
            // Fetch all related data
            promotion.setDiscount(discountDAO.getByPromotionId(id));
            promotion.setConditions(conditionDAO.getByPromotionId(id));
            promotion.setApplicableCategories(categoryDAO.getByPromotionId(id));
            promotion.setApplicableProducts(productDAO.getByPromotionId(id));
            promotion.setApplicableCustomerTiers(tierDAO.getByPromotionId(id));

            request.setAttribute("customerTiers", new CustomerTierDAO().getAll());
            request.setAttribute("categories", new CategoryDAO().getAllActiveCategories());
            request.setAttribute("promotion", promotion);
            request.getRequestDispatcher("/AdminLTE-3.2.0/promotion-edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/promotions");
        }
    }

    private void handleToggle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idStr = request.getParameter("id");
        String currentStatus = request.getParameter("currentStatus");

        if (idStr != null && currentStatus != null) {
            try {
                int id = Integer.parseInt(idStr);
                String newStatus = "ACTIVE".equals(currentStatus) ? "INACTIVE" : "ACTIVE";
                promotionDAO.updateStatus(id, newStatus);
            } catch (NumberFormatException ignored) {
            }
        }

        String redirectUrl = request.getContextPath() + "/promotions";
        String statusFilter = request.getParameter("status");
        String typeFilter = request.getParameter("type");
        if (statusFilter != null || typeFilter != null) {
            redirectUrl += "?";
            if (statusFilter != null)
                redirectUrl += "status=" + statusFilter;
            if (typeFilter != null)
                redirectUrl += (statusFilter != null ? "&" : "") + "type=" + typeFilter;
        }

        response.sendRedirect(redirectUrl);
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Promotion p = buildPromotionFromForm(request);

        // Validation
        boolean hasError = false;
        if (promotionDAO.getPromotionByCode(p.getPromotionCode()) != null) {
            request.setAttribute("error_promotionCode", "Mã chiến dịch đã tồn tại.");
            hasError = true;
        }

        if (validatePromotionValues(p, request)) {
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("promotion", p);
            request.setAttribute("customerTiers", new CustomerTierDAO().getAll());
            request.setAttribute("categories", new CategoryDAO().getAllActiveCategories());
            request.getRequestDispatcher("/AdminLTE-3.2.0/promotion-create.jsp").forward(request, response);
            return;
        }

        int promoId = promotionDAO.insert(p);

        if (promoId > 0) {
            // Save Discount
            if (p.getDiscount() != null) {
                p.getDiscount().setPromotionID(promoId);
                discountDAO.insert(p.getDiscount());
            }
            // Save Conditions
            if (p.getConditions() != null) {
                for (PromotionCondition pc : p.getConditions()) {
                    pc.setPromotionID(promoId);
                    conditionDAO.insert(pc);
                }
            }
            // Save Categories
            if (p.getApplicableCategories() != null) {
                for (PromotionApplicableCategory pac : p.getApplicableCategories()) {
                    pac.setPromotionID(promoId);
                    categoryDAO.insert(pac);
                }
            }
            // Save Products
            if (p.getApplicableProducts() != null) {
                for (PromotionApplicableProduct pap : p.getApplicableProducts()) {
                    pap.setPromotionID(promoId);
                    productDAO.insert(pap);
                }
            }
            // Save Tiers
            if (p.getApplicableCustomerTiers() != null) {
                for (PromotionCustomerTier pct : p.getApplicableCustomerTiers()) {
                    pct.setPromotionID(promoId);
                    tierDAO.insert(pct);
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/promotions");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("promotionID");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/promotions");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Promotion p = buildPromotionFromForm(request);
            p.setPromotionID(id);

            // Validation
            boolean hasError = false;
            Promotion existing = promotionDAO.getPromotionByCode(p.getPromotionCode());
            if (existing != null && existing.getPromotionID() != id) {
                request.setAttribute("error_promotionCode", "Mã chiến dịch đã tồn tại.");
                hasError = true;
            }

            if (validatePromotionValues(p, request)) {
                hasError = true;
            }

            if (hasError) {
                request.setAttribute("promotion", p);
                request.setAttribute("customerTiers", new CustomerTierDAO().getAll());
                request.setAttribute("categories", new CategoryDAO().getAllActiveCategories());
                request.getRequestDispatcher("/AdminLTE-3.2.0/promotion-edit.jsp").forward(request, response);
                return;
            }

            promotionDAO.update(p);

            // Sync Discount
            if (p.getDiscount() != null) {
                p.getDiscount().setPromotionID(id);
                if (discountDAO.getByPromotionId(id) != null) {
                    discountDAO.updateByPromotionId(p.getDiscount());
                } else {
                    discountDAO.insert(p.getDiscount());
                }
            }

            // Sync multi-value tables
            conditionDAO.deleteByPromotionId(id);
            if (p.getConditions() != null) {
                for (PromotionCondition pc : p.getConditions()) {
                    pc.setPromotionID(id);
                    conditionDAO.insert(pc);
                }
            }

            categoryDAO.deleteByPromotionId(id);
            if (p.getApplicableCategories() != null) {
                for (PromotionApplicableCategory pac : p.getApplicableCategories()) {
                    pac.setPromotionID(id);
                    categoryDAO.insert(pac);
                }
            }

            productDAO.deleteByPromotionId(id);
            if (p.getApplicableProducts() != null) {
                for (PromotionApplicableProduct pap : p.getApplicableProducts()) {
                    pap.setPromotionID(id);
                    productDAO.insert(pap);
                }
            }

            tierDAO.deleteByPromotionId(id);
            if (p.getApplicableCustomerTiers() != null) {
                for (PromotionCustomerTier pct : p.getApplicableCustomerTiers()) {
                    pct.setPromotionID(id);
                    tierDAO.insert(pct);
                }
            }

            response.sendRedirect(request.getContextPath() + "/promotions");

        } catch (NumberFormatException | ServletException e) {
            response.sendRedirect(request.getContextPath() + "/promotions");
        }
    }

    private boolean validatePromotionValues(Promotion p, HttpServletRequest request) {
        boolean hasError = false;
        double val = p.getDiscount().getDiscountValue();
        if ("PERCENT".equals(p.getPromotionType())) {
            if (val < 0 || val > 100) {
                request.setAttribute("error_discountPercent", "Tỉ lệ giảm giá phải từ 0 đến 100.");
                hasError = true;
            }
        } else if ("FIXED".equals(p.getPromotionType())) {
            if (val < 0) {
                request.setAttribute("error_fixedAmount", "Số tiền giảm không được nhỏ hơn 0.");
                hasError = true;
            }
        }
        return hasError;
    }

    private Promotion buildPromotionFromForm(HttpServletRequest request) {
        Promotion p = new Promotion();
        p.setPromotionCode(request.getParameter("promotionCode"));
        p.setPromotionName(request.getParameter("promotionName"));
        p.setPromotionType(request.getParameter("promotionType"));
        p.setStatus(request.getParameter("status") != null ? "ACTIVE" : "INACTIVE");

        try {
            p.setStartDate(LocalDate.parse(request.getParameter("startDate"), FORM_DATE_FMT));
            p.setEndDate(LocalDate.parse(request.getParameter("endDate"), FORM_DATE_FMT));
        } catch (DateTimeParseException | NullPointerException ignored) {
        }

        p.setPriority(0);
        if ("PERCENT".equals(p.getPromotionType())) {
            p.setIsStackable(false);
        } else if ("FIXED".equals(p.getPromotionType())) {
            p.setIsStackable(true);
        } else {
            p.setIsStackable(false);
        }

        // Handle Discount
        PromotionDiscountValue dv = new PromotionDiscountValue();
        dv.setDiscountType(p.getPromotionType());
        String valStr = request.getParameter("discountValue");
        if (valStr == null || valStr.isBlank()) {
            if ("PERCENT".equals(p.getPromotionType())) {
                valStr = request.getParameter("discountPercent");
            } else if ("FIXED".equals(p.getPromotionType())) {
                valStr = request.getParameter("fixedAmount");
            }
        }
        try {
            dv.setDiscountValue(Double.parseDouble(valStr != null ? valStr.replace(",", "") : "0"));
        } catch (NumberFormatException ignored) {
        }
        p.setDiscount(dv);

        // Handle Applicable Category
        String applyCategory = request.getParameter("applyCategory");
        if ("true".equals(applyCategory)) {
            String categoryId = request.getParameter("bookCategory");
            if (categoryId != null && !categoryId.isBlank() && !"ALL".equals(categoryId)) {
                List<PromotionApplicableCategory> cats = new ArrayList<>();
                PromotionApplicableCategory pac = new PromotionApplicableCategory();
                try {
                    pac.setCategoryID(Integer.parseInt(categoryId));
                    cats.add(pac);
                    p.setApplicableCategories(cats);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Handle Customer Tier
        String applyTier = request.getParameter("applyTier");
        if ("true".equals(applyTier)) {
            String tierId = request.getParameter("customerTier");
            if (tierId != null && !tierId.isBlank()) {
                List<PromotionCustomerTier> tiers = new ArrayList<>();
                PromotionCustomerTier pct = new PromotionCustomerTier();
                try {
                    pct.setTierID(Integer.parseInt(tierId));
                    tiers.add(pct);
                    p.setApplicableCustomerTiers(tiers);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Handle Conditions
        String minOrderValue = request.getParameter("minOrderValue");
        if (minOrderValue != null && !minOrderValue.isBlank()) {
            List<PromotionCondition> conds = new ArrayList<>();
            PromotionCondition pc = new PromotionCondition();
            pc.setConditionType("MIN_ORDER_VALUE");
            pc.setOperator(">=");
            pc.setConditionValue(minOrderValue.replace(",", ""));
            pc.setLogicalGroup("G1");
            conds.add(pc);
            p.setConditions(conds);
        }

        return p;
    }
}
