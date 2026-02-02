package com.automation.browser.utils;

import com.automation.browser.config.TestConfig;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * DriverFactory for WinAppDriver using legacy Selenium 3.x + Appium 7.x
 * Uses WindowsDriver with plain capabilities (no vendor prefixes)
 */
public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static WindowsDriver<WebElement> driver;

    private DriverFactory() {
        // Private constructor to prevent instantiation
    }

    public static WindowsDriver<WebElement> getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    private static void initializeDriver() {
        String winAppDriverUrl = TestConfig.getWinAppDriverUrl();
        logger.info("Initializing Windows Driver at URL: " + winAppDriverUrl);
        try {
            URL url = new URL(winAppDriverUrl);
            
            // Try to attach to existing Shift window first
            if (attachToExistingSession(url)) {
                return;
            }
            
            // Fallback: Launch new session using app path
            launchNewSession(url);
            
        } catch (Exception e) {
            logger.error("Failed to start Windows Driver", e);
            throw new RuntimeException("Failed to start driver. Ensure WinAppDriver is running at " + winAppDriverUrl, e);
        }
    }

    private static void launchNewSession(URL url) {
        logger.info("Launching new Shift session...");
        String browserPath = TestConfig.getBrowserPath();
        logger.info("Using browser path: " + browserPath);

        // Legacy capabilities for WinAppDriver (no prefixes needed)
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("app", browserPath);
        caps.setCapability("platformName", "Windows");
        caps.setCapability("deviceName", "WindowsPC");
        
        driver = new WindowsDriver<>(url, caps);
        driver.manage().timeouts().implicitlyWait(TestConfig.getImplicitWait(), TimeUnit.SECONDS);

        logger.info("Windows Driver initialized successfully (New Session).");
    }

    private static boolean attachToExistingSession(URL url) {
        WindowsDriver<WebElement> rootDriver = null;
        try {
            logger.info("Attempting to attach via Root session...");
            
            // Create Root session to find existing windows
            DesiredCapabilities rootCaps = new DesiredCapabilities();
            rootCaps.setCapability("app", "Root");
            rootCaps.setCapability("platformName", "Windows");
            rootCaps.setCapability("deviceName", "WindowsPC");
            
            rootDriver = new WindowsDriver<>(url, rootCaps);
            
            // Search for Shift window by name
            WebElement shiftWindow = null;
            String[] possibleNames = {"Untitled - Shift Browser", "Shift Browser", "Welcome to Shift", "Shift"};
            logger.info("Looking for Shift window...");
            
            for (String name : possibleNames) {
                try {
                    shiftWindow = rootDriver.findElementByName(name);
                    if (shiftWindow != null) {
                        logger.info("Found window with name: " + name);
                        break;
                    }
                } catch (Exception ignored) {}
            }
            
            if (shiftWindow != null) {
                String windowHandle = shiftWindow.getAttribute("NativeWindowHandle");
                logger.info("Found Shift window handle via Root: " + windowHandle);
                
                // Convert to hex for appTopLevelWindow
                long handleLong = Long.parseLong(windowHandle);
                String hex = "0x" + Long.toHexString(handleLong);
                
                // Quit root driver before creating new session
                rootDriver.quit();
                rootDriver = null;
                
                // Attach to existing window using appTopLevelWindow
                DesiredCapabilities appCaps = new DesiredCapabilities();
                appCaps.setCapability("appTopLevelWindow", hex);
                appCaps.setCapability("platformName", "Windows");
                appCaps.setCapability("deviceName", "WindowsPC");
                
                driver = new WindowsDriver<>(url, appCaps);
                driver.manage().timeouts().implicitlyWait(TestConfig.getImplicitWait(), TimeUnit.SECONDS);
                
                logger.info("Attached to existing Shift session (Handle: " + hex + ")");
                return true;
            }
        } catch (Exception e) {
            logger.warn("Failed to attach via Root session: " + e.getMessage());
        } finally {
            if (rootDriver != null) {
                try { rootDriver.quit(); } catch (Exception ignored) {}
            }
        }
        return false;
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                logger.info("Quitting Windows Driver...");
                driver.quit();
                driver = null;
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            }
        }
    }
}
