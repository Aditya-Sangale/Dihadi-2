package com.dihadi.service;

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

    /**
     * Creates an order with Razorpay in INR subunits (Paise).
     */
    public String createOrder(double amountInINR, String receiptId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amountInINR * 100)); // convert INR to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receiptId);
        orderRequest.put("payment_capture", 1);

        Order order = client.orders.create(orderRequest);
        return order.get("id").toString();
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