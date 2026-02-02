package com.automation.browser.utils;

import com.automation.browser.config.TestConfig;
import io.appium.java_client.windows.WindowsDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtil.class);

    public static String takeScreenshot(String testName) {
        return takeScreenshot(testName, false);
    }

    public static String takeScreenshot(String testName, boolean force) {
        if (!force && !TestConfig.isScreenshotOnFailure()) {
            return null;
        }

        try {
            WindowsDriver<WebElement> driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot take screenshot");
                return null;
            }

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String destPath = "screenshots/" + fileName;
            
            File destFile = new File(destPath);
            FileUtils.copyFile(srcFile, destFile);
            
            logger.info("Screenshot saved: {}", destPath);
            return destPath;
        } catch (IOException e) {
            logger.error("Failed to save screenshot", e);
            return null;
        } catch (Exception e) {
            logger.error("Error capturing screenshot", e);
            return null;
        }
    }
}
