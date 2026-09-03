package com.dihadi.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class RazorpayService {

    // Test API Keys (Replace with your sandbox credentials)
    public static final String KEY_ID = "rzp_test_TW0SWmw00HlJmb";
    public static final String KEY_SECRET = "BCTMDKrkDeYXH7KfsCE3zyZ3";

    private final RazorpayClient client;

    public RazorpayService() {
        try {
            this.client = new RazorpayClient(KEY_ID, KEY_SECRET);
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to initialize Razorpay client", e);
        }
    }

    public String getKeyId() {
        return KEY_ID;
    }

    public String getKeySecret() {
        return KEY_SECRET;
    }

    /**
     * Creates an order with Razorpay in INR subunits (Paise).
     */
    public String createOrder(double amountInINR, String receiptId) throws RazorpayException {
        return createWagePaymentOrder(amountInINR, receiptId, "Wallet Topup");
    }

    /**
     * Creates an official Razorpay Order for the wage amount.
     */
    public String createWagePaymentOrder(double amountInInr, String receiptId, String notes) throws RazorpayException {
        JSONObject orderReq = new JSONObject();
        // Razorpay accepts amount in sub-units (paise): 1 INR = 100 paise
        orderReq.put("amount", Math.round(amountInInr * 100));
        orderReq.put("currency", "INR");
        orderReq.put("receipt", receiptId);
        orderReq.put("payment_capture", 1);

        if (notes != null && !notes.isBlank()) {
            JSONObject notesObj = new JSONObject();
            notesObj.put("purpose", notes);
            orderReq.put("notes", notesObj);
        }

        Order order = client.orders.create(orderReq);
        return order.get("id").toString();
    }

    /**
     * Cryptographically verifies the payment signature returned by the gateway.
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        return verifySignature(orderId, paymentId, signature);
    }

    /**
     * Verifies payment signature using HMAC SHA256 (prevents external SDK verification issues).
     */
    public boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {
        if (orderId == null || paymentId == null || razorpaySignature == null) {
            return false;
        }

        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(KEY_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().equalsIgnoreCase(razorpaySignature);
        } catch (GeneralSecurityException e) {
            System.err.println("HMAC signature verification error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Polls the Razorpay API to check if the given order has been paid.
     * Used as a reliable fallback when the JS bridge callback doesn't fire
     * (e.g. during 3DS bank redirect flows in JavaFX WebView).
     *
     * @return true if the order status is "paid"
     */
    public boolean isOrderPaid(String orderId) {
        try {
            Order order = client.orders.fetch(orderId);
            String status = order.get("status").toString();
            System.out.println("[RazorpayService] Order " + orderId + " status: " + status);
            return "paid".equalsIgnoreCase(status);
        } catch (Exception e) {
            System.err.println("[RazorpayService] Failed to poll order status: " + e.getMessage());
            return false;
        }
    }
}
