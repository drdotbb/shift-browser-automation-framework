package com.automation.browser.tests;

import com.automation.browser.testBase.BaseTest;
import com.automation.browser.testBase.CommonFlows;
import com.automation.browser.config.TestConfig;
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
@Feature("Navigation")
public class NavigationTest extends CommonFlows {

    @Story("Navigate to URL and Verify Address Bar")
    @Test(groups = {"navigation", "regression"}, description = "Verify navigation to a URL")
    public void testNavigation(ITestContext context) throws Exception {
        // Ensure Shift is installed before running navigation test
        String shiftPath = getShiftExecutablePath();
        File shiftExe = new File(shiftPath);
        if (!shiftExe.exists()) {
            System.out.println("Shift browser not found. Installing...");
            downloadAndInstallShift();
            
            // Re-initialize driver
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
            // Ensure driver is initialized if app exists
             if (driver == null) {
                  driver = DriverFactory.getDriver();
                  on = new PageGenerator(driver);
             }
        }

        String url = "https://www.google.com";
        on.BrowserPage().navigateTo(url);
        
        sleep(2); // Wait for load
        
        // Validation might differ based on how the address bar behaves (e.g. might show google.com only)
        String currentAddress = on.BrowserPage().getCurrentAddress();
        Assert.assertTrue(currentAddress.contains("google.com"), "Address bar should contain the navigated URL");
    }
}
