package com.dihadi.view.PaymentGateway;

import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Backwards-compatible entry point for wage payments. It now uses the shared
 * Razorpay Payment Links interface rather than an embedded checkout WebView.
 */
public class RazorpayCheckoutDialog {

    public interface PaymentSuccessCallback {
        void onPaymentSuccess(String paymentId, String paymentLinkId, String signature);
    }

    public static void showPaymentWindow(Stage parentStage, String ignoredKeyId, String paymentLinkId,
                                         double amount, String workerName, PaymentSuccessCallback onSuccess,
                                         Consumer<String> onFailure) {
        PaymentCheckoutScene.openCheckout(parentStage, paymentLinkId, amount, "", "",
                new PaymentCheckoutScene.PaymentCallback() {
                    @Override
                    public void onSuccess(String paymentId, String linkId, String signature) {
                        onSuccess.onPaymentSuccess(paymentId, linkId, signature);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        onFailure.accept(errorMessage);
                    }
                });
    }
}
