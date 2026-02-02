package com.automation.browser.testBase;

import com.automation.browser.utils.PageGenerator;
import com.automation.browser.utils.DriverFactory;
import com.automation.browser.utils.ScreenshotUtil;
import io.appium.java_client.windows.WindowsDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseTest {
    protected static WindowsDriver<WebElement> driver;
    protected PageGenerator on;
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite
    public void setUp() {
        logger.info("Setting up Test Suite...");
        try {
            driver = DriverFactory.getDriver();
        } catch (Exception e) {
            logger.warn("Driver initialization failed in BeforeSuite. This is expected if the application is not yet installed. Error: " + e.getMessage());
        }
    }
    
    @BeforeMethod
    public void setUpMethod() {
        // Initialize the 'on' object before each test method
        if (driver == null) {
            try {
                driver = DriverFactory.getDriver();
            } catch (Exception e) {
                logger.warn("Driver initialization failed in BeforeMethod. Continuing execution as test might handle installation.");
            }
        }
        
        // Always initialize PageGenerator, even if driver is null (it handles null driver gracefully or throws when used)
        on = new PageGenerator(driver);
    }

    @AfterSuite
    public void tearDown() {
        logger.info("Tearing down Test Suite...");
        DriverFactory.quitDriver();
    }
    
    // Helper method to add short delays if needed for UI stability
    protected void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Step("Capture Screenshot: {name}")
    public void saveScreenshot(String name) {
        // Force screenshot capture regardless of 'take.screenshot.on.failure' config
        String path = ScreenshotUtil.takeScreenshot(name, true);
        
        if (path != null) {
            try (InputStream is = Files.newInputStream(Paths.get(path))) {
                Allure.addAttachment(name, is);
            } catch (IOException e) {
                logger.error("Failed to attach screenshot to Allure report", e);
            }
        } else {
            logger.warn("Screenshot was not saved (returned null). Check driver state.");
        }
    }
    
    public WindowsDriver<WebElement> getDriver() {
        return driver;
    }
}
