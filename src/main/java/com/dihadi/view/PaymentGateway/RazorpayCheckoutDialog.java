package com.dihadi.view.PaymentGateway;

import com.dihadi.view.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
        // Validate that recruiter wallet has sufficient funds
        double balance = 0.0;
        if (SessionManager.currentRecruiter != null) {
            balance = SessionManager.currentRecruiter.getWalletBalance();
        }

        if (balance < amount || balance <= 0.0) {
            final double finalBal = balance;
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Insufficient Funds");
                alert.setHeaderText("Insufficient Wallet Balance");
                alert.setContentText(String.format(
                        "Cannot proceed with payment for %s.\n\n" +
                        "• Available Wallet Funds: ₹%,.2f\n" +
                        "• Required Wage Amount: ₹%,.2f\n\n" +
                        "Please recharge your wallet using '+ Recharge Wallet' before disbursing payment.",
                        workerName != null && !workerName.isBlank() ? workerName : "Worker",
                        finalBal, amount
                ));
                alert.showAndWait();
            });

            if (onFailure != null) {
                onFailure.accept(String.format("INSUFFICIENT_FUNDS: Available ₹%.2f, Required ₹%.2f", balance, amount));
            }
            return;
        }

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
