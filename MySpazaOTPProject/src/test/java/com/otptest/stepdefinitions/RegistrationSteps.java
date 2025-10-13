package com.otptest.stepdefinitions;

import com.otptest.email.EmailOTPHandler;
import com.otptest.pages.RegistrationPage;
import com.otptest.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class RegistrationSteps {
    private WebDriver driver;
    private RegistrationPage registrationPage;
    private EmailOTPHandler otpHandler;
    private ConfigReader config;
    private String generatedEmail;

    public RegistrationSteps() {
        this.driver = TestBase.getDriver();
        this.config = TestBase.getConfig();
        this.otpHandler = new EmailOTPHandler();
    }

    @Given("the user navigates to the registration page")
    public void the_user_navigates_to_the_registration_page() {
        driver.get(config.getRegistrationUrl());
        registrationPage = new RegistrationPage(driver);
    }

   @When("the user enters full name {string}")
    public void the_user_enters_full_name(String fullName) {
        registrationPage.enterFullName(fullName);
    }

    @When("the user enters email address {string}")
    public void the_user_enters_email_address(String email) {
    	registrationPage.enterEmail(email);
    }

    @When("the user enters password {string}")
    public void the_user_enters_password(String password) {
        registrationPage.enterPassword(password);
    }

    @When("the user confirms password {string}")
    public void the_user_confirms_password(String password) {
        registrationPage.enterConfirmPassword(password);
    }

    @When("the user clicks the submit button")
    public void the_user_clicks_the_submit_button() throws InterruptedException {
        registrationPage.clickSubmit();
        Thread.sleep(5000);
        driver.close();
    }
}
