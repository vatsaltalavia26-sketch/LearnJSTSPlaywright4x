package com.seleniumframework.tests;

import com.seleniumframework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class InvalidLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeTest
    public void setUp() {
        try {
            ChromeOptions options = new ChromeOptions();
            if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1440,1000", "--disable-dev-shm-usage", "--no-sandbox");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ZERO);
            loginPage = new LoginPage(driver);
            loginPage.open();
        } catch (WebDriverException exception) {
            closeDriver();
            throw new IllegalStateException("Unable to initialize the browser for invalid login", exception);
        }
    }

    @Test
    public void blankCredentialsShouldShowValidationError() {
        try {
            loginPage.login("", "");
            Assert.assertFalse(loginPage.getLoginErrorMessage().isBlank(), "Blank credentials should display a validation error");
        } catch (RuntimeException exception) {
            Assert.fail("Blank-credential validation failed", exception);
        }
    }

    @Test
    public void incorrectCredentialsShouldShowAuthenticationError() {
        try {
            loginPage.open();
            loginPage.login("invalid.user@example.com", "incorrect-password");
            Assert.assertFalse(loginPage.getLoginErrorMessage().isBlank(), "Incorrect credentials should display an authentication error");
        } catch (RuntimeException exception) {
            Assert.fail("Incorrect-credential validation failed", exception);
        }
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        closeDriver();
    }

    private void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException exception) {
                throw new IllegalStateException("Unable to close the browser after invalid login", exception);
            } finally {
                driver = null;
            }
        }
    }
}