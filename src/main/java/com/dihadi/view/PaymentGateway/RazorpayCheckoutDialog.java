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
import java.util.function.Consumer;

public class RazorpayCheckoutDialog {

    public interface PaymentSuccessCallback {
        void onPaymentSuccess(String paymentId, String orderId, String signature);
    }

    public static class JavaPaymentBridge {
        private final PaymentSuccessCallback onSuccess;
        private final Consumer<String> onFailure;
        private final AtomicBoolean callbackFired;
        private final Stage stage;

        public JavaPaymentBridge(Stage stage, AtomicBoolean callbackFired, PaymentSuccessCallback onSuccess, Consumer<String> onFailure) {
            this.stage = stage;
            this.callbackFired = callbackFired;
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }

        public void handleSuccess(String paymentId, String orderId, String signature) {
            System.out.println("[RazorpayBridge] Success received: " + paymentId + " order: " + orderId);
            if (callbackFired.compareAndSet(false, true)) {
                Platform.runLater(() -> {
                    try {
                        stage.close();
                    } catch (Exception ignored) {}
                    if (onSuccess != null) {
                        onSuccess.onPaymentSuccess(paymentId, orderId, signature);
                    }
                });
            }
        }

        public void handleFailure(String errorDescription) {
            System.out.println("[RazorpayBridge] Failure received: " + errorDescription);
            if (callbackFired.compareAndSet(false, true)) {
                Platform.runLater(() -> {
                    try {
                        stage.close();
                    } catch (Exception ignored) {}
                    if (onFailure != null) {
                        onFailure.accept(errorDescription != null ? errorDescription : "Payment failed.");
                    }
                });
            }
        }

        public void handleDismiss() {
            System.out.println("[RazorpayBridge] Modal dismissed by user");
            if (callbackFired.compareAndSet(false, true)) {
                Platform.runLater(() -> {
                    try {
                        stage.close();
                    } catch (Exception ignored) {}
                    if (onFailure != null) {
                        onFailure.accept("Payment cancelled by user.");
                    }
                });
            }
        }
    }

    // Strong static reference to prevent Java Garbage Collector from removing bridge during WebView lifecycle
    private static volatile JavaPaymentBridge activeBridge;

    /**
     * Opens a modal payment window with debit/credit card input powered by Razorpay.
     */
    public static void showPaymentWindow(
            Stage parentStage,
            String razorpayKeyId,
            String orderId,
            double amountInInr,
            String workerName,
            PaymentSuccessCallback onSuccess,
            Consumer<String> onFailure
    ) {
        final AtomicBoolean callbackFired = new AtomicBoolean(false);

        Stage dialogStage = new Stage();
        if (parentStage != null) {
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
        }
        dialogStage.setTitle("Razorpay Secure Payment - Daily Wage for " + workerName);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        ProgressIndicator loader = new ProgressIndicator();
        StackPane root = new StackPane(webView, loader);
        StackPane.setAlignment(loader, Pos.CENTER);

        // Force keyboard focus into WebView on mouse interaction (essential for credit/debit card inputs)
        webView.setFocusTraversable(true);
        webView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> webView.requestFocus());
        webView.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> webView.requestFocus());

        // Java bridge class exposed to the browser
        activeBridge = new JavaPaymentBridge(dialogStage, callbackFired, onSuccess, onFailure);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loader.setVisible(false);
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaBridge", activeBridge);
                } catch (Exception ex) {
                    System.err.println("[RazorpayDialog] Could not inject JS bridge: " + ex.getMessage());
                }
            } else if (newState == Worker.State.RUNNING) {
                loader.setVisible(true);
            }
        });

        long amountInPaise = Math.round(amountInInr * 100);
        String safeWorkerName = (workerName != null ? workerName : "Worker").replace("'", "\\'");

        // Standard Razorpay Checkout HTML template
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset='utf-8'>\n" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1'>\n" +
                "  <script src='https://checkout.razorpay.com/v1/checkout.js'></script>\n" +
                "  <style>\n" +
                "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background: #faf3e8; }\n" +
                "    .loader { text-align: center; color: #735c00; font-weight: 700; font-size: 16px; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class='loader'>Opening Razorpay Secure Gateway...</div>\n" +
                "  <script>\n" +
                "    function startRazorpay() {\n" +
                "      var options = {\n" +
                "        'key': '" + razorpayKeyId + "',\n" +
                "        'amount': '" + amountInPaise + "',\n" +
                "        'currency': 'INR',\n" +
                "        'name': 'DIHADI',\n" +
                "        'description': 'Wage Payment for " + safeWorkerName + "',\n" +
                "        'order_id': '" + orderId + "',\n" +
                "        'handler': function (response){\n" +
                "           if (window.javaBridge) {\n" +
                "               window.javaBridge.handleSuccess(response.razorpay_payment_id, response.razorpay_order_id || '" + orderId + "', response.razorpay_signature || '');\n" +
                "           }\n" +
                "        },\n" +
                "        'modal': {\n" +
                "           'ondismiss': function(){\n" +
                "              if (window.javaBridge) {\n" +
                "                  window.javaBridge.handleDismiss();\n" +
                "              }\n" +
                "           }\n" +
                "        },\n" +
                "        'theme': {\n" +
                "           'color': '#735c00'\n" +
                "        }\n" +
                "      };\n" +
                "      var rzp1 = new Razorpay(options);\n" +
                "      rzp1.on('payment.failed', function (response){\n" +
                "         if (window.javaBridge) {\n" +
                "             var desc = (response && response.error && response.error.description) ? response.error.description : 'Payment failed';\n" +
                "             window.javaBridge.handleFailure(desc);\n" +
                "         }\n" +
                "      });\n" +
                "      rzp1.open();\n" +
                "    }\n" +
                "    window.onload = function() { setTimeout(startRazorpay, 300); };\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>";

        webEngine.loadContent(htmlContent);

        // Fallback API Poller to detect payment completion immediately once bank success / Payment Successful occurs
        Timer pollTimer = new Timer("RazorpayWageOrderPoller", true);
        RazorpayService pollService = new RazorpayService();

        TimerTask pollTask = new TimerTask() {
            private int attempts = 0;
            private static final int MAX_ATTEMPTS = 60; // 3 minutes max

            @Override
            public void run() {
                if (callbackFired.get() || attempts++ >= MAX_ATTEMPTS) {
                    cancel();
                    return;
                }
                try {
                    if (pollService.isOrderPaid(orderId)) {
                        System.out.println("[RazorpayDialog] Order " + orderId + " confirmed PAID via API poller.");
                        if (callbackFired.compareAndSet(false, true)) {
                            cancel();
                            Platform.runLater(() -> {
                                try {
                                    dialogStage.close();
                                } catch (Exception ignored) {}
                                if (onSuccess != null) {
                                    onSuccess.onPaymentSuccess("pay_polled_" + orderId, orderId, "api_verified");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[RazorpayDialog] Poller check error: " + e.getMessage());
                }
            }
        };

        // Poll every 2.5 seconds after initial 3 seconds delay
        pollTimer.scheduleAtFixedRate(pollTask, 3000, 2500);

        dialogStage.setOnCloseRequest(e -> {
            pollTask.cancel();
            pollTimer.cancel();
            if (callbackFired.compareAndSet(false, true)) {
                if (onFailure != null) {
                    onFailure.accept("Payment window closed by user.");
                }
            }
            activeBridge = null;
        });

        Scene scene = new Scene(root, 650, 720);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.show();

        Platform.runLater(webView::requestFocus);
    }
}
