package com.dihadi.view.PaymentGateway;

import com.dihadi.controller.RazorpayService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * In-app payment interface for Razorpay Payment Links.
 * Seamlessly opens the hosted checkout in the user's browser, automatically listens
 * for payment completion via local callback and API polling, and reverts focus back
 * to the DIHADI application with a success popup.
 */
public class PaymentCheckoutScene {

    public interface PaymentCallback {
        void onSuccess(String paymentId, String paymentLinkId, String signature);
        void onFailure(String errorMessage);
    }

    public static void openCheckout(Stage ownerStage, String paymentLinkId, double amount, String email,
                                    String phone, PaymentCallback callback) {
        Stage stage = new Stage();
        stage.setTitle("DIHADI Secure Payment - Aditya Sangale");
        if (ownerStage != null) {
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        Label title = new Label("DIHADI Secure Payment");
        title.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #343027;");

        Label payeeBadge = new Label("Payee: Aditya Sangale");
        payeeBadge.setStyle("-fx-background-color: #fdf6e2; -fx-border-color: #e8d7a8; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 4px 12px; -fx-font-weight: 700; -fx-font-size: 12px; -fx-text-fill: #735c00;");

        Label amountLabel = new Label(String.format("₹%.2f", amount));
        amountLabel.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #735c00;");

        Label instruction = new Label("Razorpay payment page will open in your browser.\nOnce payment is complete, this window will automatically detect it and return here.");
        instruction.setWrapText(true);
        instruction.setMaxWidth(380);
        instruction.setAlignment(Pos.CENTER);
        instruction.setStyle("-fx-font-size: 13px; -fx-text-fill: #635b4c; -fx-text-alignment: center; -fx-line-spacing: 2px;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(36, 36);

        Label status = new Label("Preparing secure payment gateway for Aditya Sangale…");
        status.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #343027;");

        VBox statusBox = new VBox(10, spinner, status);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 16px; -fx-border-radius: 14px; -fx-background-radius: 14px; -fx-border-color: #e8d7a8;");

        Button reopenButton = new Button("Re-open in Browser");
        reopenButton.setStyle("-fx-background-color: #735c00; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        reopenButton.setDisable(true);

        Button cancelButton = new Button("Cancel Payment");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: #d5caaf; -fx-border-radius: 8px; -fx-text-fill: #635b4c; -fx-font-weight: 600; -fx-padding: 8px 16px;");

        HBox actionButtons = new HBox(12, reopenButton, cancelButton);
        actionButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(16, title, payeeBadge, amountLabel, instruction, statusBox, actionButtons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setStyle("-fx-background-color: #fdfbf7;");
        stage.setScene(new Scene(root, 490, 390));
        stage.setResizable(false);

        AtomicBoolean resolved = new AtomicBoolean(false);
        final String[] paymentUrl = new String[1];

        // Ensure local callback server is started and register listener
        PaymentCallbackServer server = PaymentCallbackServer.getInstance();
        server.startServer();

        // Method to finalize success and revert back to DIHADI
        java.util.function.BiConsumer<String, String> onPaymentVerified = (pId, sig) -> {
            if (resolved.compareAndSet(false, true)) {
                server.unregisterListener(paymentLinkId);
                Platform.runLater(() -> {
                    status.setText("✓ Payment Verified! Reverting to DIHADI...");
                    status.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #059669;");
                    spinner.setVisible(false);

                    try {
                        stage.close();
                    } catch (Exception ignored) {}

                    // Revert focus to DIHADI desktop application
                    if (ownerStage != null) {
                        try {
                            ownerStage.setIconified(false);
                            ownerStage.toFront();
                            ownerStage.setAlwaysOnTop(true);
                            ownerStage.setAlwaysOnTop(false);
                            ownerStage.requestFocus();
                        } catch (Exception ex) {
                            System.err.println("[PaymentCheckout] Focus revert error: " + ex.getMessage());
                        }
                    }

                    if (callback != null) {
                        callback.onSuccess(pId, paymentLinkId, sig != null ? sig : "");
                    }
                });
            }
        };

        // 1. Listen for browser redirect to local HTTP server
        server.registerListener(paymentLinkId, payload -> {
            System.out.println("[PaymentCheckout] HTTP callback received for " + payload.paymentLinkId + " txn: " + payload.paymentId);
            onPaymentVerified.accept(payload.paymentId, payload.signature);
        });

        // 2. Continuous background poller as fail-safe
        Thread pollerThread = new Thread(() -> {
            RazorpayService razorpayService = new RazorpayService();
            int attempts = 0;
            while (!resolved.get() && attempts++ < 150) { // Poll for ~4 minutes
                try {
                    Thread.sleep(1500);
                    if (resolved.get()) break;

                    String pId = razorpayService.getPaymentId(paymentLinkId);
                    if (pId != null) {
                        System.out.println("[PaymentCheckout] API poller detected payment: " + pId);
                        onPaymentVerified.accept(pId, "");
                        break;
                    }
                } catch (InterruptedException ie) {
                    break;
                } catch (Exception e) {
                    // Ignore transient network errors during poll
                }
            }
        }, "PaymentAutoPoller-" + paymentLinkId);
        pollerThread.setDaemon(true);
        pollerThread.start();

        // Button handlers
        reopenButton.setOnAction(event -> {
            try {
                if (paymentUrl[0] != null && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create(paymentUrl[0]));
                    status.setText("Payment page re-opened in browser. Awaiting completion...");
                }
            } catch (Exception e) {
                status.setText("Could not open browser: " + e.getMessage());
            }
        });

        cancelButton.setOnAction(event -> {
            if (resolved.compareAndSet(false, true)) {
                server.unregisterListener(paymentLinkId);
                if (callback != null) callback.onFailure("Payment cancelled by user.");
            }
            stage.close();
        });

        stage.setOnCloseRequest(event -> {
            if (resolved.compareAndSet(false, true)) {
                server.unregisterListener(paymentLinkId);
                if (callback != null) callback.onFailure("Payment window closed by user.");
            }
        });

        // Initialize payment link and automatically open the default browser
        new Thread(() -> {
            try {
                paymentUrl[0] = new RazorpayService().getPaymentUrl(paymentLinkId);
                Platform.runLater(() -> {
                    reopenButton.setDisable(false);
                    status.setText("Opening payment page in browser…");
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI.create(paymentUrl[0]));
                            status.setText("Browser opened. Waiting for payment completion…");
                        } else {
                            status.setText("Click 'Re-open in Browser' to complete payment.");
                        }
                    } catch (Exception e) {
                        status.setText("Click 'Re-open in Browser' to complete payment.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    status.setText("Unable to create payment page.");
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                    if (resolved.compareAndSet(false, true)) {
                        server.unregisterListener(paymentLinkId);
                        if (callback != null) callback.onFailure(e.getMessage());
                    }
                    stage.close();
                });
            }
        }, "RazorpayPaymentLinkLoader").start();

        stage.show();
    }
}
