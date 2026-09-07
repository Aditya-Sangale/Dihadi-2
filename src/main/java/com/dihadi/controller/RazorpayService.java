package com.dihadi.controller;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Razorpay Payment Links gateway used by the existing wallet and wage flows. */
public class RazorpayService {

    private static final String API_BASE_URL = "https://api.razorpay.com/v1/payment_links";
    // Existing sandbox defaults keep local development working. Deployments
    // should override these via RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET.
    private static final String KEY_ID = setting("RAZORPAY_KEY_ID", "rzp_test_TW0SWmw00HlJmb");
    private static final String KEY_SECRET = setting("RAZORPAY_KEY_SECRET", "BCTMDKrkDeYXH7KfsCE3zyZ3");
    private final HttpClient client = HttpClient.newHttpClient();

    public String getKeyId() { return KEY_ID; }

    /** Creates a hosted payment link and returns its Razorpay link id. */
    public String createOrder(double amountInINR, String receiptId) throws Exception {
        return createWagePaymentOrder(amountInINR, receiptId, "Wallet Topup");
    }

    /** Compatibility method for the existing wage-payment flow. */
    public String createWagePaymentOrder(double amountInINR, String receiptId, String notes) throws Exception {
        if (amountInINR <= 0) throw new IllegalArgumentException("Payment amount must be greater than zero.");

        JSONObject body = new JSONObject();
        body.put("amount", Math.round(amountInINR * 100));
        body.put("currency", "INR");
        body.put("reference_id", receiptId);

        String desc = (notes == null || notes.isBlank()) ? "DIHADI Payment - Aditya Sangale" : notes;
        if (!desc.contains("Aditya Sangale")) {
            desc = desc + " - Aditya Sangale";
        }
        body.put("description", desc);

        JSONObject customer = new JSONObject();
        customer.put("name", "Aditya Sangale");
        customer.put("email", "adityasangale771@gmail.com");
        customer.put("contact", "+919561789599");
        body.put("customer", customer);

        JSONObject notesObj = new JSONObject();
        notesObj.put("merchant_name", "Aditya Sangale");
        notesObj.put("beneficiary", "Aditya Sangale");
        notesObj.put("payee", "Aditya Sangale");
        notesObj.put("platform", "DIHADI Workforce Ecosystem");
        body.put("notes", notesObj);

        try {
            String callbackUrl = com.dihadi.view.PaymentGateway.PaymentCallbackServer.getInstance().getCallbackUrl();
            if (callbackUrl != null && !callbackUrl.isBlank()) {
                body.put("callback_url", callbackUrl);
                body.put("callback_method", "get");
            }
        } catch (Exception ex) {
            System.err.println("[RazorpayService] Callback server url unavailable: " + ex.getMessage());
        }

        return send("POST", API_BASE_URL, body).getString("id");
    }

    /** Returns Razorpay's secure hosted URL for the supplied payment-link id. */
    public String getPaymentUrl(String paymentLinkId) throws Exception {
        return send("GET", paymentLinkUrl(paymentLinkId), null).getString("short_url");
    }

    /** Returns a payment id after successful payment, otherwise {@code null}. */
    public String getPaymentId(String paymentLinkId) {
        try {
            JSONObject response = send("GET", paymentLinkUrl(paymentLinkId), null);
            if (!"paid".equalsIgnoreCase(response.optString("status"))) return null;
            JSONObject payments = response.optJSONObject("payments");
            if (payments != null && payments.optJSONArray("items") != null && !payments.getJSONArray("items").isEmpty()) {
                return payments.getJSONArray("items").getJSONObject(0).optString("id", paymentLinkId);
            }
            return paymentLinkId;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Payment Links are verified server-side by checking Razorpay's paid status. */
    public boolean verifyPaymentSignature(String paymentLinkId, String paymentId, String signature) {
        return isOrderPaid(paymentLinkId);
    }

    /** Kept for existing callers; Payment Links use status verification instead. */
    public boolean verifySignature(String paymentLinkId, String paymentId, String signature) {
        return verifyPaymentSignature(paymentLinkId, paymentId, signature);
    }

    /** True only after Razorpay reports the hosted payment link as paid. */
    public boolean isOrderPaid(String paymentLinkId) {
        try {
            return "paid".equalsIgnoreCase(send("GET", paymentLinkUrl(paymentLinkId), null).optString("status"));
        } catch (Exception e) {
            System.err.println("[RazorpayService] Unable to check payment-link status: " + e.getMessage());
            return false;
        }
    }

    private JSONObject send(String method, String url, JSONObject body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Basic " + encodedCredentials())
                .header("Content-Type", "application/json");
        if ("POST".equals(method)) builder.POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        else builder.GET();

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Razorpay request failed (HTTP " + response.statusCode() + "): " + response.body());
        }
        return new JSONObject(response.body());
    }

    private static String paymentLinkUrl(String paymentLinkId) {
        if (paymentLinkId == null || paymentLinkId.isBlank()) throw new IllegalArgumentException("Payment link id is required.");
        return API_BASE_URL + "/" + paymentLinkId;
    }

    private static String encodedCredentials() {
        return Base64.getEncoder().encodeToString((KEY_ID + ":" + KEY_SECRET).getBytes(StandardCharsets.UTF_8));
    }

    private static String setting(String name, String fallback) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
