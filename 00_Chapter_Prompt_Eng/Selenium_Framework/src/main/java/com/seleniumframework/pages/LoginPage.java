package com.seleniumframework.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private static final String LOGIN_URL = "https://app.vwo.com/#/login";
    private static final By LOGIN_BUTTON_LOCATOR = By.xpath("//button[@id='js-login-btn']");
    private static final By LOGIN_ERROR_LOCATOR = By.xpath("//div[contains(@class,'notification-box-description') or @role='alert']");

    @FindBy(xpath = "//input[@id='login-username']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@id='login-password']")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@id='js-login-btn']")
    private WebElement loginButton;

    @FindBy(xpath = "//input[@type='checkbox']")
    private WebElement rememberMeCheckbox;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver must not be null");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        try {
            driver.get(System.getProperty("vwo.url", LOGIN_URL));
            wait.until(ExpectedConditions.visibilityOf(emailField));
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to open the VWO login page", exception);
        }
    }

    public void login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }
        try {
            wait.until(ExpectedConditions.visibilityOf(emailField)).clear();
            emailField.sendKeys(email);
            passwordField.clear();
            passwordField.sendKeys(password);
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to submit the VWO login form", exception);
        }
    }

    public void setRememberMe(boolean selected) {
        try {
            wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox));
            if (rememberMeCheckbox.isSelected() != selected) {
                rememberMeCheckbox.click();
            }
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to update the remember-me setting", exception);
        }
    }

    public String getLoginErrorMessage() {
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR_LOCATOR));
            return error.getText().trim();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("The login error message was not displayed", exception);
        }
    }

    public boolean waitForSuccessfulLogin() {
        try {
            return wait.until(currentDriver ->
                !currentDriver.getCurrentUrl().contains("#/login")
                    || ExpectedConditions.invisibilityOfElementLocated(LOGIN_BUTTON_LOCATOR).apply(currentDriver)
            );
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Successful login could not be confirmed", exception);
        }
    }
}