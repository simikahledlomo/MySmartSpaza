package com.otptest.email;

import com.otptest.utils.ConfigReader;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GmailOTPReader {
    private Store store;
    private Folder folder;
    private ConfigReader config;

    public GmailOTPReader() {
        this.config = new ConfigReader();
    }

    public void connectToGmail() {
        try {
            String host = "imap.gmail.com";
            String username = config.getGmailUsername();
            String password = config.getGmailPassword();
            String folderName = config.getGmailFolder();

            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.connectiontimeout", "10000");
            properties.put("mail.imap.timeout", "10000");

            Session session = Session.getInstance(properties);
            store = session.getStore("imaps");
            store.connect(host, "simikahlerifumodlomo@gmail.com", "DLO@SIM_mabheka1!");

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            System.out.println("Successfully connected to Gmail");

        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Gmail: " + e.getMessage(), e);
        }
    }

    public String getOTPFromEmail(String subjectKeyword, long timeoutSeconds) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        Pattern otpPattern = Pattern.compile(config.getOtpPattern());
        int attempt = 0;

        try {
            while (System.currentTimeMillis() < endTime) {
                attempt++;
                System.out.println("Checking for OTP email (Attempt " + attempt + ")...");

                Message[] messages = folder.getMessages();

                for (int i = messages.length - 1; i >= 0; i--) {
                    Message message = messages[i];
                    String subject = message.getSubject();

                    if (subject != null && subject.contains(subjectKeyword)) {
                        String content = getMessageContent(message);
                        Matcher matcher = otpPattern.matcher(content);

                        if (matcher.find()) {
                            String otp = matcher.group();
                            System.out.println("OTP found: " + otp);
                            return otp;
                        }
                    }
                }

                if (System.currentTimeMillis() + 5000 > endTime) {
                    break;
                }

                Thread.sleep(5000);
                folder.close(false);
                folder.open(Folder.READ_ONLY);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading OTP from email: " + e.getMessage(), e);
        }

        throw new RuntimeException("OTP not found within " + timeoutSeconds + " seconds after " + attempt + " attempts");
    }

    private String getMessageContent(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMultipart((Multipart) message.getContent());
        } else if (message.isMimeType("text/html")) {
            return message.getContent().toString();
        }
        return "";
    }

    private String getTextFromMultipart(Multipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(html.replaceAll("\\<[^>]*>", ""));
            } else if (bodyPart.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public void disconnect() {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
            System.out.println("Disconnected from Gmail");
        } catch (Exception e) {
            System.err.println("Error disconnecting from email: " + e.getMessage());
        }
    }
}
