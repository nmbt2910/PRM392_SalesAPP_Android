package com.prm392.salesapp.payment;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * VNPay Payment Helper
 * Handles VNPay payment URL generation and signature verification
 */
public class VNPayHelper {
    
    // VNPay Configuration - Sandbox TEST environment
    private static final String VNP_TMN_CODE = "VV0IKIOG"; // Terminal ID from VNPay
    private static final String VNP_HASH_SECRET = "EVPL23EFF2FM0RTQSU5KBZRP07GMSNUQ"; // Secret key from VNPay
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // Sandbox URL
    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_ORDER_TYPE = "other";
    private static final String VNP_LOCALE = "vn";
    private static final String VNP_CURRENCY_CODE = "VND";
    
    /**
     * Generate VNPay payment URL
     * 
     * @param orderId Order ID
     * @param amount Payment amount in VND
     * @param orderInfo Order description
     * @param returnUrl URL to redirect after payment
     * @return VNPay payment URL
     */
    public static String generatePaymentUrl(int orderId, double amount, String orderInfo, String returnUrl) {
        try {
            // Convert amount to VND (integer, no decimal)
            long amountInVnd = (long) (amount);
            
            // Create timestamp
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            String createDate = formatter.format(new Date());
            
            // Create expire date (15 minutes from now)
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            calendar.add(Calendar.MINUTE, 15);
            String expireDate = formatter.format(calendar.getTime());
            
            // Build parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", VNP_VERSION);
            vnpParams.put("vnp_Command", VNP_COMMAND);
            vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
            vnpParams.put("vnp_Amount", String.valueOf(amountInVnd * 100)); // VNPay uses smallest unit
            vnpParams.put("vnp_CurrCode", VNP_CURRENCY_CODE);
            vnpParams.put("vnp_TxnRef", String.valueOf(orderId));
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", VNP_ORDER_TYPE);
            vnpParams.put("vnp_Locale", VNP_LOCALE);
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", "127.0.0.1"); // Should be actual IP
            vnpParams.put("vnp_CreateDate", createDate);
            vnpParams.put("vnp_ExpireDate", expireDate);
            
            // Build query string
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }
            
            // Generate secure hash
            String vnpSecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
            query.append("&vnp_SecureHash=");
            query.append(vnpSecureHash);
            
            // Build final URL
            String paymentUrl = VNP_URL + "?" + query.toString();
            
            Log.d("VNPayHelper", "Payment URL generated: " + paymentUrl);
            return paymentUrl;
            
        } catch (Exception e) {
            Log.e("VNPayHelper", "Error generating payment URL", e);
            return null;
        }
    }
    
    /**
     * Verify VNPay return signature
     * 
     * @param params Return parameters from VNPay
     * @return true if signature is valid
     */
    public static boolean verifySignature(Map<String, String> params) {
        try {
            String vnpSecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            
            // Build hash data
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    
                    if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                        hashData.append('&');
                    }
                }
            }
            
            String calculatedHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
            return calculatedHash.equals(vnpSecureHash);
            
        } catch (Exception e) {
            Log.e("VNPayHelper", "Error verifying signature", e);
            return false;
        }
    }
    
    /**
     * Generate HMAC SHA512 hash
     */
    private static String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
            
        } catch (Exception e) {
            Log.e("VNPayHelper", "Error generating HMAC SHA512", e);
            return "";
        }
    }
    
    /**
     * Parse VNPay response code to readable message
     */
    public static String getResponseMessage(String responseCode) {
        switch (responseCode) {
            case "00":
                return "Transaction successful";
            case "07":
                return "Transaction successful. Transaction is being verified";
            case "09":
                return "Transaction failed: Card/Account has not registered for Internet Banking";
            case "10":
                return "Transaction failed: Incorrect card/account information";
            case "11":
                return "Transaction failed: Card/Account has expired";
            case "12":
                return "Transaction failed: Card/Account is locked";
            case "13":
                return "Transaction failed: Incorrect OTP";
            case "24":
                return "Transaction cancelled";
            case "51":
                return "Transaction failed: Insufficient balance";
            case "65":
                return "Transaction failed: Daily transaction limit exceeded";
            case "75":
                return "Payment system is under maintenance";
            case "79":
                return "Transaction failed: Incorrect payment password";
            default:
                return "Transaction failed: Unknown error";
        }
    }
}
