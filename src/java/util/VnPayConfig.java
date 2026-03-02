package util;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayConfig {

    // TODO: Thay bằng thông tin sandbox thật của bạn
    public static final String VNP_TMN_CODE = "ACYFYON7";
    public static final String VNP_HASH_SECRET = "M44ZA592DAIEJMPNN2PXQBZUUE4VIIRV";
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    /**
     * URL mà VNPay redirect sau khi thanh toán (trang quay về của bạn).
     * - Để trống = tự lấy từ request (http://localhost:9999/context/vnpay-return).
     * - Khi thanh toán bằng QR trên điện thoại, localhost không mở được trên máy → cần dùng URL công khai.
     *   Ví dụ: dùng ngrok: "https://abc123.ngrok.io/se1972_g2/vnpay-return" (thay abc123 bằng subdomain thật).
     */
    public static final String VNP_RETURN_URL_OVERRIDE = "";

    public static String createPaymentUrl(HttpServletRequest request,
                                          double amount,
                                          String orderInfo,
                                          String txnRef) {
        // VNPAY yêu cầu số tiền nhân 100 (đơn vị: VND * 100)
        long amountVnd = Math.round(amount * 100);

        Map<String, String> vnp_Params = new LinkedHashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amountVnd));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", buildReturnUrl(request));
        vnp_Params.put("vnp_IpAddr", getIpAddress(request));

        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        vnp_Params.put("vnp_CreateDate", createDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        // Áp dụng đúng mẫu ajaxServlet của VNPAY: encode value trong cả hashData và query
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.name());
                    String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.name());

                    // Build hash data
                    hashData.append(encodedFieldName)
                            .append("=")
                            .append(encodedFieldValue);

                    // Build query string
                    query.append(encodedFieldName)
                            .append("=")
                            .append(encodedFieldValue);

                    if (i < fieldNames.size() - 1) {
                        hashData.append("&");
                        query.append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    // ignore - US_ASCII luôn khả dụng
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
        // Bám sát demo: chỉ gửi vnp_SecureHash
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return VNP_PAY_URL + "?" + query;
    }

    private static String buildReturnUrl(HttpServletRequest request) {
        if (VNP_RETURN_URL_OVERRIDE != null && !VNP_RETURN_URL_OVERRIDE.trim().isEmpty()) {
            return VNP_RETURN_URL_OVERRIDE.trim();
        }
        String scheme = request.getScheme(); // http hoặc https
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        if ((scheme.equals("http") && serverPort != 80)
                || (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath).append("/vnpay-return");
        return url.toString();
    }

    private static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    private static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

