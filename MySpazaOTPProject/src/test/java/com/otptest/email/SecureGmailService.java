package com.otptest.email;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecureGmailService {
    
    private Store store;
    private Folder inbox;
    private String username;
    private String appPassword;
    
    public SecureGmailService(String username, String appPassword) {
        this.username = username;
        this.appPassword = appPassword;
    }
    
    public void connect() {
        try {
            Properties properties = new Properties();
            
            // IMAP configuration
            properties.setProperty("mail.store.protocol", "imaps");
            properties.setProperty("mail.imap.host", "imap.gmail.com");
            properties.setProperty("mail.imap.port", "993");
            properties.setProperty("mail.imap.ssl.enable", "true");
            properties.setProperty("mail.imap.auth", "true");
            properties.setProperty("mail.imap.connectiontimeout", "10000");
            properties.setProperty("mail.imap.timeout", "10000");
            
            // Additional security properties
            properties.setProperty("mail.imap.starttls.enable", "true");
            properties.setProperty("mail.imap.auth.mechanisms", "XOAUTH2 PLAIN");
            
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });
            
            session.setDebug(true); // Enable debug to see connection details
            
            store = session.getStore("imaps");
            System.out.println("Connecting to Gmail with username: " + username);
            store.connect("imap.gmail.com", username, appPassword);
            
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            System.out.println("✓ Successfully connected to Gmail");
            System.out.println("Total messages in inbox: " + inbox.getMessageCount());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Gmail: " + e.getMessage(), e);
        }
    }
    
    public String getLatestOTP(String subjectKeyword, int timeoutSeconds) {
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        Pattern otpPattern = Pattern.compile("\\b\\d{6}\\b");
        
        try {
            while (System.currentTimeMillis() < endTime) {
                Message[] messages = inbox.getMessages();
                System.out.println("Checking " + messages.length + " messages...");
                
                // Search from latest to oldest
                for (int i = messages.length - 1; i >= 0; i--) {
                    try {
                        Message message = messages[i];
                        String subject = message.getSubject();
                        
                        if (subject != null && subject.contains(subjectKeyword)) {
                            System.out.println("Found matching email: " + subject);
                            
                            String content = getMessageContent(message);
                            Matcher matcher = otpPattern.matcher(content);
                            
                            if (matcher.find()) {
                                String otp = matcher.group();
                                System.out.println("✓ OTP found: " + otp);
                                return otp;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error processing message: " + e.getMessage());
                    }
                }
                
                System.out.println("OTP not found, waiting 5 seconds...");
                Thread.sleep(5000);
                
                // Refresh folder
                inbox.close(false);
                inbox.open(Folder.READ_ONLY);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving OTP: " + e.getMessage(), e);
        }
        
        throw new RuntimeException("OTP not found within " + timeoutSeconds + " seconds");
    }
    
    private String getMessageContent(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            return getTextFromMultipart((Multipart) message.getContent());
        } else if (message.isMimeType("text/html")) {
            String html = message.getContent().toString();
            return html.replaceAll("<[^>]*>", ""); // Remove HTML tags
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
                String html = bodyPart.getContent().toString();
                result.append(html.replaceAll("<[^>]*>", ""));
            } else if (bodyPart.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
    
    public void disconnect() {
        try {
            if (inbox != null && inbox.isOpen()) {
                inbox.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
            System.out.println("✓ Disconnected from Gmail");
        } catch (Exception e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
