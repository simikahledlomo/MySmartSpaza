package com.otptest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final String PROPERTIES_FILE = "supportFiles/config.properties";
    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    public String getBrowser() { return properties.getProperty("browser", "chrome"); }
    
    public String getBaseUrl() { return properties.getProperty("base.url"); }
    
    public String getRegistrationUrl() { return properties.getProperty("registration.url"); }
    
    public String getGmailUsername() { return properties.getProperty("gmail.username"); }
    
    public String getGmailPassword() { return properties.getProperty("gmail.password"); }
    public String getGmailHost() { return properties.getProperty("gmail.host", "imap.gmail.com"); }
    public String getGmailFolder() { return properties.getProperty("gmail.folder", "INBOX"); }
    public int getOtpTimeout() { return Integer.parseInt(properties.getProperty("otp.timeout.seconds", "60")); }
    public String getOtpPattern() { return properties.getProperty("otp.pattern", "\\b\\d{6}\\b"); }
    public String getDefaultName() { return properties.getProperty("default.name"); }
    public String getDefaultPassword() { return properties.getProperty("default.password"); }
    public int getImplicitWait() { return Integer.parseInt(properties.getProperty("implicitWait", "10")); }
}
