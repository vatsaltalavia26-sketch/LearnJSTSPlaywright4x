package com.seleniumframework.tests;

import com.seleniumframework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ValidLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private String username;
    private String password;

    @BeforeTest
    public void setUp() {
        username = System.getProperty("vwo.username");
        password = System.getProperty("vwo.password");
        if (username == null || username.isBlank()) {
            username = System.getenv("VWO_USERNAME");
        }
        if (password == null || password.isBlank()) {
            password = System.getenv("VWO_PASSWORD");
        }
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new SkipException("Set vwo.username and vwo.password or VWO_USERNAME and VWO_PASSWORD to run valid login");
        }

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
            throw new IllegalStateException("Unable to initialize the browser for valid login", exception);
        }
    }

    @Test
    public void validCredentialsShouldOpenTheAccount() {
        try {
            loginPage.setRememberMe(true);
            loginPage.login(username, password);
            Assert.assertTrue(loginPage.waitForSuccessfulLogin(), "Valid credentials should open the VWO account");
        } catch (RuntimeException exception) {
            Assert.fail("Valid login flow failed", exception);
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
                throw new IllegalStateException("Unable to close the browser after valid login", exception);
            } finally {
                driver = null;
            }
        }
    }
}