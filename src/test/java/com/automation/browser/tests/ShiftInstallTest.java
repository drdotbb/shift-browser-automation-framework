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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestListener.class})
@Epic("Browser Automation")
@Feature("Installation")
public class ShiftInstallTest extends CommonFlows {

    @Story("Download, Install and Verify Shift Browser")
    @Test(groups = {"install", "smoke", "regression"})
    public void testDownloadInstallAndVerifyShift(ITestContext context) throws Exception {
        String installerVersion = "0";
        
        installerVersion = downloadAndInstallShift();

        String installedPath = getShiftExecutablePath();
        TestConfig.setProperty("browser.path", installedPath);

        // Initialize WinAppDriver
        DriverFactory.quitDriver();
        driver = DriverFactory.getDriver();
        Assert.assertNotNull(driver, "Failed to initialize WinAppDriver.");

        // Re-initialize PageGenerator since driver changed
        on = new PageGenerator(driver);

        // Handle Setup if needed
         try {
             handleSetup();
         } catch (Exception e) {
             System.out.println("Setup might have been skipped or failed: " + e.getMessage());
         }

        // Maximize the browser window
        driver.manage().window().maximize();

        if (installerVersion != null && !installerVersion.isEmpty()) {
             String actualVersion = getInstalledVersion();
             Assert.assertTrue(actualVersion.contains(installerVersion),
                     "Version mismatch! Expected version " + installerVersion + " to be part of " + actualVersion);
             System.out.println("Version verification successful! Found: " + actualVersion);
        } else {
            System.out.println("Skipping verification, version unavailable.");
        }
    }

    @AfterMethod
    public void tearDownMethod() {
        System.out.println("Tearing down test...");
        DriverFactory.quitDriver();
        uninstallShift();
    }
}
