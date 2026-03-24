package util;

import DAO.PromotionDAO;
import DAO.PromotionDiscountValueDAO;
import DAO.PromotionApplicableProductDAO;
import DAO.PromotionApplicableCategoryDAO;
import DAO.PromotionConditionDAO;
import entity.CartItem;
import entity.Promotion;
import entity.PromotionDiscountValue;
import entity.PromotionApplicableProduct;
import entity.PromotionApplicableCategory;
import entity.PromotionCondition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Service tự động áp dụng promotion vào giỏ hàng khi thanh toán.
 *
 * Luồng:
 * 1. Lấy tất cả Promotion ACTIVE
 * 2. Lọc còn hạn (startDate <= hôm nay <= endDate)
 * 3. Kiểm tra sản phẩm/danh mục khớp
 * 4. Kiểm tra điều kiện (MIN_ORDER_AMOUNT, MIN_QUANTITY...)
 * 5. Tính discount và trả về kết quả
 */
public class PromotionService {

    private final PromotionDAO promotionDAO = new PromotionDAO();
    private final PromotionDiscountValueDAO discountValueDAO = new PromotionDiscountValueDAO();
    private final PromotionApplicableProductDAO applicableProductDAO = new PromotionApplicableProductDAO();
    private final PromotionApplicableCategoryDAO applicableCategoryDAO = new PromotionApplicableCategoryDAO();
    private final PromotionConditionDAO conditionDAO = new PromotionConditionDAO();

    /**
     * Kết quả sau khi áp dụng promotion.
     */
    public static class PromotionResult {
        private double totalDiscount;           // Tổng số tiền được giảm
        private String appliedPromotionName;    // Tên (các) promotion đã áp dụng
        private List<String> appliedPromotions; // Danh sách tên promotion

        public PromotionResult() {
            this.totalDiscount = 0;
            this.appliedPromotionName = "";
            this.appliedPromotions = new ArrayList<>();
        }

        public double getTotalDiscount() {
            return totalDiscount;
        }

        public void setTotalDiscount(double totalDiscount) {
            this.totalDiscount = totalDiscount;
        }

        public String getAppliedPromotionName() {
            return appliedPromotionName;
        }

        public void setAppliedPromotionName(String appliedPromotionName) {
            this.appliedPromotionName = appliedPromotionName;
        }

        public List<String> getAppliedPromotions() {
            return appliedPromotions;
        }

        public void setAppliedPromotions(List<String> appliedPromotions) {
            this.appliedPromotions = appliedPromotions;
        }
    }

    /**
     * Tính toán và trả về tổng discount từ các promotion áp dụng được cho giỏ hàng.
     *
     * @param cart danh sách sản phẩm trong giỏ
     * @return PromotionResult chứa tổng discount và tên promotion
     */
    public PromotionResult calculatePromotionDiscount(List<CartItem> cart) {
        PromotionResult result = new PromotionResult();

        if (cart == null || cart.isEmpty()) {
            return result;
        }

        // 1. Lấy tất cả promotion ACTIVE
        List<Promotion> activePromotions = promotionDAO.getAllActive();
        if (activePromotions == null || activePromotions.isEmpty()) {
            return result;
        }

        LocalDate today = LocalDate.now();

        // Tính tổng giá gốc đơn hàng (dùng cho điều kiện MIN_ORDER_VALUE)
        double orderTotal = cart.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();

        // Tổng số lượng sản phẩm (dùng cho điều kiện MIN_QUANTITY)
        int orderTotalQty = cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        // Lọc danh sách promotion thỏa mãn điều kiện thời gian và điều kiện đơn hàng (Min Order Value/Qty)
        List<Promotion> availablePromotions = new ArrayList<>();
        for (Promotion promo : activePromotions) {
            // Kiểm tra còn hạn
            if (promo.getStartDate() != null && today.isBefore(promo.getStartDate())) {
                continue;
            }
            if (promo.getEndDate() != null && today.isAfter(promo.getEndDate())) {
                continue;
            }

            // Kiểm tra điều kiện đơn hàng (MIN_ORDER_VALUE, MIN_QUANTITY)
            List<PromotionCondition> conditions = conditionDAO.getByPromotionId(promo.getPromotionID());
            if (checkConditions(conditions, orderTotal, orderTotalQty)) {
                availablePromotions.add(promo);
            }
        }

        double totalDiscount = 0;
        Set<String> appliedPromoNames = new HashSet<>();

        // 2. Với mỗi sản phẩm, tìm 1 khuyến mãi tốt nhất
        for (CartItem item : cart) {
            Promotion bestPromoForItem = null;
            double bestUnitDiscount = 0;
            String bestTypeForItem = null;

            for (Promotion promo : availablePromotions) {
                // Kiểm tra xem sản phẩm có khớp với đối tượng áp dụng của promotion không
                if (!isProductMatched(promo, item)) {
                    continue;
                }

                PromotionDiscountValue dv = discountValueDAO.getByPromotionId(promo.getPromotionID());
                if (dv == null) continue;

                double currentUnitDiscount = calculateUnitDiscount(dv, item.getProduct().getSellingPrice());
                String currentType = dv.getDiscountType() != null ? dv.getDiscountType().toUpperCase() : "";
                
                boolean isPercent = "PERCENTAGE".equals(currentType) || "PERCENT".equals(currentType);
                boolean bestIsPercent = bestTypeForItem != null 
                        && ("PERCENTAGE".equals(bestTypeForItem) || "PERCENT".equals(bestTypeForItem));

                boolean shouldReplace = false;
                if (bestPromoForItem == null) {
                    shouldReplace = true;
                } else if (isPercent && !bestIsPercent) {
                    shouldReplace = true; // Ưu tiên PERCENT
                } else if (isPercent == bestIsPercent && currentUnitDiscount > bestUnitDiscount) {
                    shouldReplace = true; // Cùng loại thì lấy cao nhất
                }

                if (shouldReplace) {
                    bestPromoForItem = promo;
                    bestUnitDiscount = currentUnitDiscount;
                    bestTypeForItem = currentType;
                }
            }

            if (bestPromoForItem != null && bestUnitDiscount > 0) {
                totalDiscount += bestUnitDiscount * item.getQuantity();
                appliedPromoNames.add(bestPromoForItem.getPromotionName());
            }
        }

        // Đảm bảo tổng discount không vượt quá tổng đơn hàng
        if (totalDiscount > orderTotal) {
            totalDiscount = orderTotal;
        }

        if (totalDiscount > 0) {
            result.setTotalDiscount(totalDiscount);
            result.setAppliedPromotions(new ArrayList<>(appliedPromoNames));
            result.setAppliedPromotionName(String.join(", ", appliedPromoNames));
        }

        return result;
    }

    private boolean isProductMatched(Promotion promo, CartItem item) {
        List<PromotionApplicableProduct> products = applicableProductDAO.getByPromotionId(promo.getPromotionID());
        List<PromotionApplicableCategory> categories = applicableCategoryDAO.getByPromotionId(promo.getPromotionID());

        // Nếu không có bộ lọc -> áp dụng cho tất cả
        if ((products == null || products.isEmpty()) && (categories == null || categories.isEmpty())) {
            return true;
        }

        if (products != null) {
            for (PromotionApplicableProduct p : products) {
                if (p.getProductID() == item.getProduct().getProductID()) return true;
            }
        }

        if (categories != null) {
            for (PromotionApplicableCategory c : categories) {
                if (c.getCategoryID() == item.getProduct().getCategoryID()) return true;
            }
        }

        return false;
    }

    private double calculateUnitDiscount(PromotionDiscountValue dv, double unitPrice) {
        String type = dv.getDiscountType().toUpperCase();
        double val = dv.getDiscountValue();
        if ("PERCENTAGE".equals(type) || "PERCENT".equals(type)) {
            return unitPrice * (Math.min(val, 100) / 100.0);
        } else if ("FIXED_AMOUNT".equals(type) || "FIXED".equals(type)) {
            return Math.min(val, unitPrice);
        }
        return 0;
    }


    /**
     * Kiểm tra các điều kiện promotion.
     * Hỗ trợ: MIN_ORDER_AMOUNT, MIN_QUANTITY.
     */
    private boolean checkConditions(List<PromotionCondition> conditions,
                                    double orderTotal, int orderTotalQty) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (PromotionCondition cond : conditions) {
            String type = cond.getConditionType();
            String operator = cond.getOperator();
            String valueStr = cond.getConditionValue();

            if (type == null || operator == null || valueStr == null) {
                continue;
            }

            try {
                double condValue = Double.parseDouble(valueStr.trim());

                switch (type.toUpperCase()) {
                    case "MIN_ORDER_AMOUNT":
                    case "MIN_ORDER_VALUE":
                        if (!compareValues(orderTotal, operator, condValue)) {
                            return false;
                        }
                        break;
                    case "MIN_QUANTITY":
                        if (!compareValues(orderTotalQty, operator, condValue)) {
                            return false;
                        }
                        break;
                    // Có thể mở rộng thêm các loại condition khác
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                // Bỏ qua condition không parse được
            }
        }
        return true;
    }

    /**
     * So sánh giá trị theo operator.
     */
    private boolean compareValues(double actual, String operator, double condValue) {
        return switch (operator.trim()) {
            case ">=" -> actual >= condValue;
            case ">" -> actual > condValue;
            case "<=" -> actual <= condValue;
            case "<" -> actual < condValue;
            case "=", "==" -> actual == condValue;
            default -> true;
        };
    }

    /**
     * Tính discount dựa trên loại giảm giá.
     *
     * @param discountValue thông tin giảm giá
     * @param matchedItems  các sản phẩm được áp dụng
     * @return số tiền giảm
     */
    private double calculateDiscount(PromotionDiscountValue discountValue, List<CartItem> matchedItems) {
        String discountType = discountValue.getDiscountType();
        double value = discountValue.getDiscountValue();

        if (discountType == null || value <= 0) {
            return 0;
        }

        double matchedTotal = matchedItems.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();

        return switch (discountType.toUpperCase()) {
            case "PERCENTAGE", "PERCENT" -> {
                double pct = Math.min(value, 100);
                yield matchedTotal * pct / 100.0;
            }
            case "FIXED_AMOUNT", "FIXED" -> Math.min(value, matchedTotal);
            default -> 0;
        };
    }
}
