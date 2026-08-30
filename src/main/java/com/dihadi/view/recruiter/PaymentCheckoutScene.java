package com.dihadi.view.recruiter;

import com.dihadi.service.RazorpayService;
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
            public void onSuccess(String paymentId, String returnedOrderId, String signature) {
                if (callbackFired.compareAndSet(false, true)) {
                    System.out.println("[PaymentBridge] SUCCESS via JS bridge — closing checkout");
                    paymentStage.close();
                    activeBridge = null;
                    callback.onSuccess(paymentId, returnedOrderId, signature);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (callbackFired.compareAndSet(false, true)) {
                    System.out.println("[PaymentBridge] FAILURE via JS bridge — " + errorMessage);
                    paymentStage.close();
                    activeBridge = null;
                    callback.onFailure(errorMessage);
                }
            }
        });
        activeBridge = bridge; // prevent GC

        // ── Build the checkout HTML ─────────────────────────────────────────
        String checkoutHtml = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta charset='utf-8'>\n"
                + "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n"
                + "</head>\n"
                + "<body style='background-color:#ffffff; margin:0; display:flex; justify-content:center; align-items:center; height:100vh; font-family:sans-serif;'>\n"
                + "  <script src='https://checkout.razorpay.com/v1/checkout.js'></script>\n"
                + "  <script>\n"
                + "    window.onload = function() {\n"
                + "      var options = {\n"
                + "        'key': '" + RazorpayService.KEY_ID + "',\n"
                + "        'amount': '" + ((int)(amountInINR * 100)) + "',\n"
                + "        'currency': 'INR',\n"
                + "        'name': 'Dihadi Platform',\n"
                + "        'description': 'Recruiter Wallet Deposit',\n"
                + "        'order_id': '" + orderId + "',\n"
                + "        'prefill': {\n"
                + "          'email': '" + recruiterEmail + "',\n"
                + "          'contact': '" + recruiterPhone + "'\n"
                + "        },\n"
                + "        'theme': { 'color': '#d4af37' },\n"
                + "        'handler': function (response){\n"
                + "          try {\n"
                + "            if (window.javaBridge) {\n"
                + "              window.javaBridge.onPaymentSuccess(\n"
                + "                response.razorpay_payment_id,\n"
                + "                response.razorpay_order_id,\n"
                + "                response.razorpay_signature\n"
                + "              );\n"
                + "            }\n"
                + "          } catch(e) { console.log('JS bridge handler error: ' + e); }\n"
                + "        },\n"
                + "        'modal': {\n"
                + "          'ondismiss': function(){\n"
                + "            try { if (window.javaBridge) window.javaBridge.onPaymentError('Payment window closed by user.'); }\n"
                + "            catch(e) { console.log('dismiss error: ' + e); }\n"
                + "          }\n"
                + "        }\n"
                + "      };\n"
                + "      var rzp = new Razorpay(options);\n"
                + "      rzp.on('payment.failed', function (response){\n"
                + "        try { if (window.javaBridge) window.javaBridge.onPaymentError(response.error.description); }\n"
                + "        catch(e) { console.log('failure error: ' + e); }\n"
                + "      });\n"
                + "      rzp.open();\n"
                + "    };\n"
                + "  </script>\n"
                + "</body>\n"
                + "</html>";

        // ── Inject bridge on every page load ────────────────────────────────
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loader.setVisible(false);
                injectBridge(engine, bridge);
            } else if (newState == Worker.State.FAILED) {
                loader.setVisible(false);
                // Don't fire failure for sub-resource loads or redirects
            }
        });

        // Re-inject bridge on document changes (iframe navigation, bank redirects)
        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                Platform.runLater(() -> injectBridge(engine, bridge));
            }
        });

        engine.loadContent(checkoutHtml);

        // ── API POLLING: Reliable fallback for payment detection ────────────
        // The JS bridge fails during 3DS/bank-redirect flows because JavaFX
        // WebView loses the JavaScript context. This timer polls the Razorpay
        // Order API every 3 seconds. When the order status changes to "paid",
        // we fire the success callback and close the checkout window.
        Timer pollTimer = new Timer("razorpay-order-poll", true);
        RazorpayService pollService = new RazorpayService();

        pollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (callbackFired.get()) {
                    pollTimer.cancel();
                    return;
                }
                try {
                    if (pollService.isOrderPaid(orderId)) {
                        if (callbackFired.compareAndSet(false, true)) {
                            pollTimer.cancel();
                            System.out.println("[PaymentBridge] SUCCESS via API polling — order " + orderId + " is paid");
                            Platform.runLater(() -> {
                                paymentStage.close();
                                activeBridge = null;
                                callback.onSuccess("paid_via_poll", orderId, "polled");
                            });
                        }
                    }
                } catch (Exception e) {
                    // Polling failed this cycle, will retry next interval
                    System.err.println("[PaymentBridge] Poll cycle error: " + e.getMessage());
                }
            }
        }, 5000, 3000); // Start after 5s, repeat every 3s

        // ── Handle window close ─────────────────────────────────────────────
        paymentStage.setOnCloseRequest(e -> {
            pollTimer.cancel();
            if (callbackFired.compareAndSet(false, true)) {
                activeBridge = null;
                callback.onFailure("Payment window closed by user.");
            }
        });

        Scene scene = new Scene(root, 520, 660);
        paymentStage.setScene(scene);
        paymentStage.setResizable(false);
        paymentStage.show();
        Platform.runLater(webView::requestFocus);
    }

    /** Injects the Java bridge into the WebView's JavaScript context. */
    private static void injectBridge(WebEngine engine, JavaPaymentBridge bridge) {
        try {
            JSObject win = (JSObject) engine.executeScript("window");
            win.setMember("javaBridge", bridge);
            System.out.println("[PaymentBridge] Bridge injected into WebView");
        } catch (Exception e) {
            // May fail during redirects — that's expected, polling covers it
        }
    }
}