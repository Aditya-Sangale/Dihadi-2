package com.dihadi.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles Firebase phone-number authentication using the Identity Toolkit REST
 * API.
 * Enable Phone Authentication in Firebase Console → Authentication → Sign-in
 * method.
 *
 * Replace the API_KEY with your Firebase project's Web API Key
 * (Firebase Console → Project Settings → General → Web API Key).
 */
public class AuthService {

    // TODO: Replace with your actual Firebase Web API Key
    private static final String API_KEY = "AIzaSyCn8l9eW9g9RAc_a9fsaz6KccxHluM9frY";

    private static final String SEND_OTP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:sendVerificationCode?key="
            + API_KEY;

    private static final String VERIFY_OTP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPhoneNumber?key="
            + API_KEY;

    /**
     * Sends an OTP to the given phone number.
     *
     * @param phoneNumber full international phone number, e.g. "+919876543210"
     * @return sessionInfo token needed to verify the OTP, or null on failure
     */
    public static String sendOtp(String phoneNumber) {
        try {
            String jsonBody = "{\"phoneNumber\":\"" + phoneNumber + "\",\"recaptchaToken\":\"RECAPTCHA_EXEMPT\"}";

            String response = postRequest(SEND_OTP_URL, jsonBody);
            if (response != null && response.contains("sessionInfo")) {
                // Extract sessionInfo value from JSON response
                int start = response.indexOf("\"sessionInfo\"") + 16;
                int end = response.indexOf("\"", start);
                String sessionInfo = response.substring(start, end);
                System.out.println("OTP sent successfully to " + phoneNumber);
                return sessionInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifies the OTP entered by the user.
     *
     * @param sessionInfo the session token returned by sendOtp()
     * @param otp         the 6-digit OTP entered by the user
     * @return the Firebase UID of the authenticated user, or null on failure
     */
    public static String verifyOtp(String sessionInfo, String otp) {
        try {
            String jsonBody = "{\"sessionInfo\":\"" + sessionInfo + "\",\"code\":\"" + otp + "\"}";

            String response = postRequest(VERIFY_OTP_URL, jsonBody);
            if (response != null && response.contains("localId")) {
                // Extract localId (UID) from JSON response
                int start = response.indexOf("\"localId\"") + 12;
                int end = response.indexOf("\"", start);
                String uid = response.substring(start, end);
                System.out.println("OTP verified successfully. UID: " + uid);
                return uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends an HTTP POST request with a JSON body and returns the response as a
     * string.
     */
    private static String postRequest(String urlString, String jsonBody) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode >= 200 && responseCode < 300) {
                return response.toString();
            } else {
                System.err.println("Firebase Auth Error (" + responseCode + "): " + response);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
