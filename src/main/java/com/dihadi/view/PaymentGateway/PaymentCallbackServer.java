package com.dihadi.view.PaymentGateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Local lightweight HTTP callback server that listens for Razorpay redirects
 * upon payment completion in the browser. It automatically detects successful
 * payments, serves a success page to the browser, and reverts focus to the DIHADI application.
 */
public class PaymentCallbackServer {

    private static final int DEFAULT_PORT = 18080;
    private static PaymentCallbackServer instance;
    private HttpServer server;
    private int port = -1;
    private final Map<String, Consumer<CallbackPayload>> listeners = new ConcurrentHashMap<>();
    private volatile Consumer<CallbackPayload> fallbackListener;

    public static class CallbackPayload {
        public final String paymentId;
        public final String paymentLinkId;
        public final String status;
        public final String signature;

        public CallbackPayload(String paymentId, String paymentLinkId, String status, String signature) {
            this.paymentId = paymentId;
            this.paymentLinkId = paymentLinkId;
            this.status = status;
            this.signature = signature;
        }
    }

    public static synchronized PaymentCallbackServer getInstance() {
        if (instance == null) {
            instance = new PaymentCallbackServer();
            instance.startServer();
        }
        return instance;
    }

    /** Starts the server on port 18080 or the first available fallback port. */
    public synchronized int startServer() {
        if (server != null && port > 0) {
            return port;
        }

        int[] candidatePorts = new int[]{DEFAULT_PORT, 18081, 18082, 0};
        for (int p : candidatePorts) {
            try {
                server = HttpServer.create(new InetSocketAddress("127.0.0.1", p), 0);
                server.createContext("/payment-callback", new CallbackHttpHandler());
                server.setExecutor(Executors.newCachedThreadPool());
                server.start();
                port = server.getAddress().getPort();
                System.out.println("[PaymentCallbackServer] Listening on http://localhost:" + port + "/payment-callback");
                return port;
            } catch (IOException e) {
                System.err.println("[PaymentCallbackServer] Port " + p + " unavailable: " + e.getMessage());
            }
        }
        return -1;
    }

    /** Returns the full HTTP callback URL to be registered with Razorpay payment links. */
    public String getCallbackUrl() {
        int activePort = startServer();
        if (activePort > 0) {
            return "http://localhost:" + activePort + "/payment-callback";
        }
        return null;
    }

    /** Registers a callback listener for a specific Razorpay payment link id. */
    public void registerListener(String paymentLinkId, Consumer<CallbackPayload> listener) {
        if (paymentLinkId != null && !paymentLinkId.isBlank()) {
            listeners.put(paymentLinkId, listener);
        }
        this.fallbackListener = listener;
    }

    /** Unregisters a callback listener. */
    public void unregisterListener(String paymentLinkId) {
        if (paymentLinkId != null) {
            listeners.remove(paymentLinkId);
        }
        this.fallbackListener = null;
    }

    private class CallbackHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String query = exchange.getRequestURI().getRawQuery();
                Map<String, String> queryParams = parseQueryParams(query);

                String paymentId = queryParams.getOrDefault("razorpay_payment_id", "");
                String linkId = queryParams.getOrDefault("razorpay_payment_link_id", "");
                String status = queryParams.getOrDefault("razorpay_payment_link_status", "paid");
                String signature = queryParams.getOrDefault("razorpay_signature", "");

                CallbackPayload payload = new CallbackPayload(paymentId, linkId, status, signature);

                // Notify registered JavaFX listener
                Consumer<CallbackPayload> listener = linkId.isEmpty() ? null : listeners.remove(linkId);
                if (listener == null) {
                    listener = fallbackListener;
                    fallbackListener = null;
                }

                if (listener != null) {
                    final Consumer<CallbackPayload> targetListener = listener;
                    Platform.runLater(() -> targetListener.accept(payload));
                }

                String responseHtml = buildSuccessHtml(paymentId);
                byte[] responseBytes = responseHtml.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                byte[] err = "Internal Server Error".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(500, err.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(err);
                }
            }
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length > 0) {
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
                params.put(key, value);
            }
        }
        return params;
    }

    private static String buildSuccessHtml(String paymentId) {
        String safePaymentId = (paymentId != null && !paymentId.isBlank()) ? paymentId : "Processed";
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Payment Successful - DIHADI</title>\n" +
                "  <style>\n" +
                "    * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
                "    body {\n" +
                "      font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;\n" +
                "      background: linear-gradient(135deg, #fdfbf7 0%, #f3e7ce 100%);\n" +
                "      display: flex;\n" +
                "      align-items: center;\n" +
                "      justify-content: center;\n" +
                "      min-height: 100vh;\n" +
                "      color: #343027;\n" +
                "      padding: 20px;\n" +
                "    }\n" +
                "    .card {\n" +
                "      background: #ffffff;\n" +
                "      padding: 44px 36px;\n" +
                "      border-radius: 24px;\n" +
                "      box-shadow: 0 20px 45px rgba(52, 48, 39, 0.14);\n" +
                "      text-align: center;\n" +
                "      max-width: 460px;\n" +
                "      width: 100%;\n" +
                "      border: 1.5px solid #e8d7a8;\n" +
                "    }\n" +
                "    .icon-wrapper {\n" +
                "      width: 78px;\n" +
                "      height: 78px;\n" +
                "      background: #10b981;\n" +
                "      border-radius: 50%;\n" +
                "      display: flex;\n" +
                "      align-items: center;\n" +
                "      justify-content: center;\n" +
                "      margin: 0 auto 22px;\n" +
                "      font-size: 42px;\n" +
                "      color: white;\n" +
                "      box-shadow: 0 10px 25px rgba(16, 185, 129, 0.35);\n" +
                "    }\n" +
                "    h1 { font-size: 26px;\n" +
                "         font-weight: 800;\n" +
                "         color: #343027;\n" +
                "         margin-bottom: 8px; }\n" +
                "    .merchant-badge {\n" +
                "      display: inline-block;\n" +
                "      background: #fdf6e2;\n" +
                "      border: 1px solid #e8d7a8;\n" +
                "      color: #735c00;\n" +
                "      padding: 6px 14px;\n" +
                "      border-radius: 999px;\n" +
                "      font-weight: 700;\n" +
                "      font-size: 13px;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    p {\n" +
                "      font-size: 15px;\n" +
                "      color: #635b4c;\n" +
                "      margin-bottom: 20px;\n" +
                "      line-height: 1.5;\n" +
                "    }\n" +
                "    .info-box {\n" +
                "      background: #faf6ee;\n" +
                "      border-radius: 12px;\n" +
                "      padding: 14px;\n" +
                "      font-size: 13px;\n" +
                "      color: #736b5c;\n" +
                "      margin-bottom: 24px;\n" +
                "      text-align: left;\n" +
                "      display: flex;\n" +
                "      flex-direction: column;\n" +
                "      gap: 6px;\n" +
                "    }\n" +
                "    .info-box span { font-weight: 700; color: #343027; }\n" +
                "    .status-note {\n" +
                "      color: #059669;\n" +
                "      font-weight: 700;\n" +
                "      font-size: 14px;\n" +
                "      margin-bottom: 10px;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      font-size: 12px;\n" +
                "      color: #9c9281;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"card\">\n" +
                "    <div class=\"icon-wrapper\">&#10003;</div>\n" +
                "    <h1>Payment Successful!</h1>\n" +
                "    <div class=\"merchant-badge\">Payee: Aditya Sangale</div>\n" +
                "    <p>Your transaction has been securely processed.</p>\n" +
                "    <div class=\"info-box\">\n" +
                "      <div>Payee: <span>Aditya Sangale (DIHADI)</span></div>\n" +
                "      <div>Payment ID: <span>" + safePaymentId + "</span></div>\n" +
                "      <div>Status: <span style=\"color:#059669;\">Verified</span></div>\n" +
                "    </div>\n" +
                "    <div class=\"status-note\">Reverting automatically to DIHADI application...</div>\n" +
                "    <div class=\"footer\">You may now safely close this browser window.</div>\n" +
                "  </div>\n" +
                "  <script>\n" +
                "    setTimeout(function() {\n" +
                "      try { window.close(); } catch(e) {}\n" +
                "    }, 2000);\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
