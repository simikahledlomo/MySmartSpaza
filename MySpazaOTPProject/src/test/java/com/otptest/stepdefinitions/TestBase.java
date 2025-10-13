package com.otptest.stepdefinitions;

import com.otptest.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

public class TestBase {
    protected static WebDriver driver;
    protected static ConfigReader config;
    
    // Static initializer to ensure config is always available
    static {
        config = new ConfigReader();
    }

    @BeforeSuite
    public static void setup() {
        String browser = config.getBrowser().toLowerCase();

        switch (browser) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            setup(); // Ensure driver is initialized
        }
        return driver;
    }

    public static ConfigReader getConfig() {
        if (config == null) {
            config = new ConfigReader();
        }
        return config;
    }
}