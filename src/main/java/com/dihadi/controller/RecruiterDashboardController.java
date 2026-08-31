package com.dihadi.controller;

import com.dihadi.view.PaymentGateway.PaymentCheckoutScene;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;

public class RecruiterDashboardController {

    @FXML private Label balanceLabel;
    @FXML private TextField depositAmountField;
    @FXML private Button addFundsButton;

    private final RazorpayService razorpayService = new RazorpayService();
    private double currentBalance = 0.00;

    @FXML
    public void initialize() {
        updateBalanceDisplay();
    }

    @FXML
    public void handleAddFunds() {
        String input = depositAmountField != null ? depositAmountField.getText().trim() : "";
        if (input.isEmpty()) {
            displayAlert(Alert.AlertType.WARNING, "Input Required", "Please enter the amount you want to deposit.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            displayAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric value greater than 0.");
            return;
        }

        Stage parentStage = (Stage) (addFundsButton != null ? addFundsButton.getScene().getWindow() : null);
        String receiptId = "rcpt_" + UUID.randomUUID().toString().substring(0, 8);

        try {
            // Step 1: Create Order
            String orderId = razorpayService.createOrder(amount, receiptId);

            // Step 2: Open Checkout Modal
            PaymentCheckoutScene.openCheckout(
                    parentStage,
                    orderId,
                    amount,
                    "recruiter@dihadi.com",
                    "9999999999",
                    new PaymentCheckoutScene.PaymentCallback() {
                        @Override
                        public void onSuccess(String paymentId, String orderId, String signature) {
                            // Step 3: Verify Signature
                            boolean verified = razorpayService.verifySignature(orderId, paymentId, signature);
                            if (verified) {
                                // Step 4: Update UI State on FX Thread
                                currentBalance += amount;
                                updateBalanceDisplay();
                                if (depositAmountField != null) {
                                    depositAmountField.clear();
                                }
                                displayAlert(Alert.AlertType.INFORMATION, "Success", "Payment verified and credited!\nTxn ID: " + paymentId);
                            } else {
                                displayAlert(Alert.AlertType.ERROR, "Security Mismatch", "Payment signature verification failed.");
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            displayAlert(Alert.AlertType.ERROR, "Transaction Failed", errorMessage);
                        }
                    }
            );
        } catch (Exception ex) {
            displayAlert(Alert.AlertType.ERROR, "Gateway Error", "Failed to initiate payment: " + ex.getMessage());
        }
    }

    private void updateBalanceDisplay() {
        if (balanceLabel != null) {
            balanceLabel.setText(String.format("₹ %.2f", currentBalance));
        }
    }

    private void displayAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}