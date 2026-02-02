package com.automation.browser.tests;

import com.automation.browser.testBase.BaseTest;
import com.automation.browser.testBase.CommonFlows;
import com.automation.browser.utils.TestListener;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({TestListener.class})
@Epic("Browser Automation")
@Feature("Navigation")
public class NavigationTest extends CommonFlows {

    @Story("Navigate to URL and Verify Address Bar")
    @Test(groups = {"navigation", "regression"}, description = "Verify navigation to a URL")
    public void testNavigation(ITestContext context) {
        String url = "https://www.google.com";
        on.BrowserPage().navigateTo(url);
        
        sleep(2); // Wait for load
        
        // Validation might differ based on how the address bar behaves (e.g. might show google.com only)
        String currentAddress = on.BrowserPage().getCurrentAddress();
        Assert.assertTrue(currentAddress.contains("google.com"), "Address bar should contain the navigated URL");
    }
}
