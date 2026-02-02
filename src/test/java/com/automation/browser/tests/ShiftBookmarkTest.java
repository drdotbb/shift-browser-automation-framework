package com.automation.browser.tests;

import com.automation.browser.config.TestConfig;
import com.automation.browser.testBase.CommonFlows;
import com.automation.browser.utils.DriverFactory;
import com.automation.browser.utils.PageGenerator;
import com.automation.browser.utils.TestListener;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;

@Listeners({TestListener.class})
@Epic("Browser Automation")
@Feature("Bookmarks")
public class ShiftBookmarkTest extends CommonFlows {

    @Story("Create and Verify Bookmark")
    @Test(groups = {"bookmark", "regression"})
    public void testCreateAndVerifyBookmark(ITestContext context) throws Exception {
        String shiftPath = getShiftExecutablePath();
        File shiftExe = new File(shiftPath);

        System.out.println("Checking for Shift installation at: " + shiftPath);

        if (!shiftExe.exists()) {
            System.out.println("Shift browser not found. Proceeding with installation...");
            
            // Download and install
            downloadAndInstallShift();
            
            // Re-initialize driver after install to point to the new executable
            TestConfig.setProperty("browser.path", shiftPath);
            DriverFactory.quitDriver();
            driver = DriverFactory.getDriver();
            on = new PageGenerator(driver);
            
            try {
                handleSetup();
            } catch (Exception e) {
                System.out.println("Setup might have been skipped or failed: " + e.getMessage());
            }
        } else {
             System.out.println("Shift browser found.");
             
             // Ensure driver is pointing to the correct executable and is running
             TestConfig.setProperty("browser.path", shiftPath);
             if (driver == null) {
                  driver = DriverFactory.getDriver();
                  on = new PageGenerator(driver);
             }
        }
        
        // Maximize window for better visibility
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            System.out.println("Could not maximize window: " + e.getMessage());
        }

        // 1. Enable Bookmarks Bar
        on.BrowserPage().ensureBookmarksBarVisible();
        sleep(2);

        // 2. Navigate to https://www.rdbrck.com/
        String url = "https://www.rdbrck.com/";
        System.out.println("Navigating to: " + url);
        on.BrowserPage().navigateTo(url);
        
        // Wait for page load
        sleep(5);
        saveScreenshot("Page Loaded - Redbrick");

        // 3. Save bookmark
        System.out.println("Saving bookmark...");
        on.BrowserPage().saveBookmark();
        sleep(2); // Wait for animation/popup
        saveScreenshot("Bookmark Action Performed");

        // 4. Verify bookmark in Bookmarks Bar
        // The title of rdbrck.com contains "Redbrick"
        String expectedTitle = "RedBrck Video | Photo Production"; // Exact name from inspector dump
        
        System.out.println("Verifying bookmark for: " + expectedTitle);
        boolean isSaved = on.BrowserPage().verifyBookmarkInBar(expectedTitle);
        
        if (!isSaved) {
             // Try partial match if exact match fails
             isSaved = on.BrowserPage().verifyBookmarkInBar("Redbrick");
        }
        
        saveScreenshot("Bookmark Verification Result");
        
        Assert.assertTrue(isSaved, "Bookmark for '" + expectedTitle + "' was not found in the Bookmarks Bar.");
    }
}
