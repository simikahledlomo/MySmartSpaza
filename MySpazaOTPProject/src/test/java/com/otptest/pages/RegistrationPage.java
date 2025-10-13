package com.otptest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegistrationPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//div[@class='w-full max-w-md']//input[@id='fullName']")
    private WebElement fullNameField;

    @FindBy(xpath = "//div[@class='w-full max-w-md']//input[@placeholder='Enter your email address or phone number']")
    private WebElement emailField;

    @FindBy(xpath = "//div[@class='w-full max-w-md']//input[@id='password']")
    private WebElement passwordField;

    @FindBy(xpath = "//div[@class='w-full max-w-md']//input[@id='confirmPassword']")
    private WebElement confirmPasswordField;

    @FindBy(xpath = "//div[@class='w-full max-w-md']//button[@type='submit']")
    private WebElement submitButton;

    @FindBy(xpath = "//div[@class = 'w-full']//div[@class = 'flex justify-center gap-3 mb-6']//input[@inputmode='numeric']")
    private WebElement otpField;


    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public void enterFullName(String fullName) {
        fullNameField.clear();
        fullNameField.sendKeys(fullName);
    }

    public void enterEmail(String email) {
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void enterConfirmPassword(String password) {
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(password);
    }

    public void clickSubmit() {
        submitButton.click();
    }

    public void enterOTP(String otp) {
        wait.until(d -> otpField.isDisplayed());
        otpField.clear();
        otpField.sendKeys(otp);
    }

    public void completeRegistration(String fullName, String email, String password) {
        enterFullName(fullName);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(password);
        clickSubmit();
    }
}
