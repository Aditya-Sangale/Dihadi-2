package com.dihadi.view.PaymentGateway;

import com.dihadi.controller.RazorpayService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Opens a Razorpay Standard Checkout inside a JavaFX WebView modal.
 * <p>
 * Two independent mechanisms detect payment completion:
 * <ol>
 *   <li><b>JS Bridge (fast-path)</b> — Razorpay's {@code handler} callback fires
 *       {@code window.javaBridge.onPaymentSuccess()} immediately.</li>
 *   <li><b>API Polling (reliable fallback)</b> — A background timer polls the
 *       Razorpay Order API every 3 seconds. When the order status flips to
 *       {@code "paid"}, the callback fires even if the JS bridge failed
 *       (which happens during 3DS/bank-redirect flows in JavaFX WebView).</li>
 * </ol>
 * An {@code AtomicBoolean} ensures exactly one of the two mechanisms fires
 * the callback.
 */
public class PaymentCheckoutScene {

    public interface PaymentCallback {
        void onSuccess(String paymentId, String orderId, String signature);
        void onFailure(String errorMessage);
    }

    /**
     * Java-side bridge exposed to JavaScript as {@code window.javaBridge}.
     * Stored as a static strong reference to prevent Java GC from collecting it.
     */
    public static class JavaPaymentBridge {
        private final PaymentCallback callback;

        public JavaPaymentBridge(PaymentCallback callback) {
            this.callback = callback;
        }

        public void onPaymentSuccess(String paymentId, String orderId, String signature) {
            System.out.println("[PaymentBridge] JS bridge onPaymentSuccess — paymentId=" + paymentId);
            Platform.runLater(() -> callback.onSuccess(paymentId, orderId, signature));
        }

        public void onPaymentError(String description) {
            System.out.println("[PaymentBridge] JS bridge onPaymentError — " + description);
            Platform.runLater(() -> callback.onFailure(description));
        }
    }

    // Strong reference to prevent GC while checkout is open
    private static volatile JavaPaymentBridge activeBridge;

    public static void openCheckout(
            Stage ownerStage,
            String orderId,
            double amountInINR,
            String recruiterEmail,
            String recruiterPhone,
            PaymentCallback callback
    ) {
        // ── Shared flag: ensures exactly ONE callback fires ─────────────────
        final AtomicBoolean callbackFired = new AtomicBoolean(false);

        Stage paymentStage = new Stage();
        paymentStage.setTitle("Razorpay Secure Checkout");
        if (ownerStage != null) {
            paymentStage.initOwner(ownerStage);
            paymentStage.initModality(Modality.APPLICATION_MODAL);
        }

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        // ── FIX: Force keyboard focus into WebView on mouse interaction ─────
        webView.setFocusTraversable(true);
        webView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> webView.requestFocus());
        webView.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> webView.requestFocus());

        ProgressIndicator loader = new ProgressIndicator();
        StackPane root = new StackPane(webView, loader);
        StackPane.setAlignment(loader, Pos.CENTER);

        // ── Create JS bridge with AtomicBoolean guard ───────────────────────
        JavaPaymentBridge bridge = new JavaPaymentBridge(new PaymentCallback() {
            @Override
            public void onSuccess(String paymentId, String oid, String signature) {
                if (callbackFired.compareAndSet(false, true)) {
                    Platform.runLater(() -> {
                        paymentStage.close();
                        callback.onSuccess(paymentId, oid, signature);
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (callbackFired.compareAndSet(false, true)) {
                    Platform.runLater(() -> {
                        paymentStage.close();
                        callback.onFailure(errorMessage);
                    });
                }
            }
        });
        activeBridge = bridge;

        // ── Inject JS bridge on every page/frame load ───────────────────────
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loader.setVisible(false);
                try {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaBridge", activeBridge);
                } catch (Exception e) {
                    System.err.println("[PaymentCheckout] Could not inject JS bridge: " + e.getMessage());
                }
            } else if (newState == Worker.State.RUNNING) {
                loader.setVisible(true);
            }
        });

        // ── HTML content with Razorpay standard checkout ───────────────────
        long amountInSubunits = Math.round(amountInINR * 100);
        String htmlContent = "<!DOCTYPE html>\n"
                + "<html><head>\n"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"
                + "<script src='https://checkout.razorpay.com/v1/checkout.js'></script>\n"
                + "<style>\n"
                + "  body { font-family: 'Segoe UI', Arial, sans-serif; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; background: #faf3e8; }\n"
                + "  .card { background: #ffffff; border-radius: 16px; border: 1.5px solid #d4af37; padding: 36px 40px; text-align: center; box-shadow: 0 8px 30px rgba(115,92,0,0.12); max-width: 380px; }\n"
                + "  h2 { color: #735c00; margin-top: 0; font-size: 22px; }\n"
                + "  p { color: #4c4637; font-size: 14px; margin: 8px 0 20px; }\n"
                + "  .amount { font-size: 28px; font-weight: 800; color: #1e1b15; margin: 12px 0 24px; }\n"
                + "  button { background: linear-gradient(135deg, #d4af37 0%, #b8921e 100%); color: #ffffff; border: none; padding: 13px 32px; font-size: 15px; font-weight: 700; border-radius: 8px; cursor: pointer; width: 100%; transition: opacity 0.2s; }\n"
                + "  button:hover { opacity: 0.92; }\n"
                + "</style>\n"
                + "</head><body>\n"
                + "<div class='card'>\n"
                + "  <h2>DIHADI Escrow Deposit</h2>\n"
                + "  <p>Secure payment powered by Razorpay</p>\n"
                + "  <div class='amount'>&#8377;" + String.format("%.2f", amountInINR) + "</div>\n"
                + "  <button id='payBtn' onclick='openRazorpay()'>Proceed to Pay</button>\n"
                + "</div>\n"
                + "<script>\n"
                + "  function openRazorpay() {\n"
                + "    var options = {\n"
                + "        'key': '" + RazorpayService.KEY_ID + "',\n"
                + "        'amount': '" + amountInSubunits + "',\n"
                + "        'currency': 'INR',\n"
                + "        'name': 'DIHADI',\n"
                + "        'description': 'Escrow / Wallet Top-up',\n"
                + "        'image': 'https://i.imgur.com/your-logo.png',\n"
                + "        'order_id': '" + orderId + "',\n"
                + "        'prefill': {\n"
                + "            'email': '" + recruiterEmail + "',\n"
                + "            'contact': '" + recruiterPhone + "'\n"
                + "        },\n"
                + "        'theme': { 'color': '#735c00' },\n"
                + "        'handler': function (response){\n"
                + "            if (window.javaBridge) {\n"
                + "                window.javaBridge.onPaymentSuccess(\n"
                + "                    response.razorpay_payment_id,\n"
                + "                    response.razorpay_order_id || '" + orderId + "',\n"
                + "                    response.razorpay_signature || ''\n"
                + "                );\n"
                + "            }\n"
                + "        },\n"
                + "        'modal': {\n"
                + "            'ondismiss': function(){\n"
                + "                if (window.javaBridge) {\n"
                + "                    window.javaBridge.onPaymentError('Payment modal was dismissed by user.');\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    };\n"
                + "    var rzp = new Razorpay(options);\n"
                + "    rzp.on('payment.failed', function (response){\n"
                + "        if (window.javaBridge) {\n"
                + "            window.javaBridge.onPaymentError(response.error.description || 'Payment Failed');\n"
                + "        }\n"
                + "    });\n"
                + "    rzp.open();\n"
                + "  }\n"
                + "  // Auto-trigger on page load\n"
                + "  window.onload = function() { setTimeout(openRazorpay, 300); };\n"
                + "</script>\n"
                + "</body></html>";

        engine.loadContent(htmlContent);

        Scene scene = new Scene(root, 650, 720);
        paymentStage.setScene(scene);
        paymentStage.setResizable(false);

        // ── Mechanism 2: Fallback API Poller ─────────────────────────────────
        // Polls the Razorpay REST API every 3s to detect payment completion
        // even if WebView redirects away from the JS bridge callback.
        Timer pollTimer = new Timer("RazorpayOrderPoller", true);
        RazorpayService pollService = new RazorpayService();

        TimerTask pollTask = new TimerTask() {
            private int attempts = 0;
            private static final int MAX_ATTEMPTS = 60; // poll for up to 3 minutes

            @Override
            public void run() {
                if (callbackFired.get() || attempts++ >= MAX_ATTEMPTS) {
                    cancel();
                    return;
                }
                try {
                    if (pollService.isOrderPaid(orderId)) {
                        System.out.println("[PaymentPoller] Order " + orderId + " confirmed PAID via API.");
                        if (callbackFired.compareAndSet(false, true)) {
                            cancel();
                            Platform.runLater(() -> {
                                paymentStage.close();
                                // Synthetic payment ID prefix indicates poller resolution
                                callback.onSuccess("pay_polled_" + orderId, orderId, "api_verified");
                            });
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[PaymentPoller] Error: " + e.getMessage());
                }
            }
        };

        // Start polling after 5s initial delay, then every 3s
        pollTimer.scheduleAtFixedRate(pollTask, 5000, 3000);

        // ── Clean up on window close ─────────────────────────────────────────
        paymentStage.setOnCloseRequest(e -> {
            pollTask.cancel();
            pollTimer.cancel();
            if (callbackFired.compareAndSet(false, true)) {
                callback.onFailure("Payment window closed by user.");
            }
            activeBridge = null;
        });

        paymentStage.show();

        // Ensure webview receives focus once stage is visible
        Platform.runLater(webView::requestFocus);
    }
}
