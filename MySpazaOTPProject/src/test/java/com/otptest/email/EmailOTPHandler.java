package com.otptest.email;

import com.otptest.utils.ConfigReader;

public class EmailOTPHandler {
    private ConfigReader config;
    private GmailOTPReader gmailReader;

    public EmailOTPHandler() {
        this.config = new ConfigReader();
        this.gmailReader = new GmailOTPReader();
    }

    public String getOTP(String emailSubject) {
        try {
            gmailReader.connectToGmail();
            long timeout = config.getOtpTimeout();
            String otp = gmailReader.getOTPFromEmail(emailSubject, timeout);
            System.out.println("OTP retrieved successfully: " + otp);
            return otp;
        } finally {
            gmailReader.disconnect();
        }
    }

    public String getRegistrationOTP() {
        return getOTP("Verification Code");
    }

    public String getLoginOTP() {
        return getOTP("Login Code");
    }

    public String getPasswordResetOTP() {
        return getOTP("Password Reset");
    }
}
