package com.automation.browser.utils;

import com.automation.browser.pages.AdvancedSettingsPage;
import com.automation.browser.pages.BrowserPage;
import com.automation.browser.pages.SetupPage;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.WebElement;

public class PageGenerator {
    private WindowsDriver<WebElement> driver;
    private SetupPage setupPage;
    private AdvancedSettingsPage advancedSettingsPage;
    private BrowserPage browserPage;

    public PageGenerator(WindowsDriver<WebElement> driver) {
        this.driver = driver;
    }

    public SetupPage SetupPage() {
        if (setupPage == null) {
            setupPage = new SetupPage(driver);
        }
        return setupPage;
    }

    public AdvancedSettingsPage AdvancedSettingsPage() {
        if (advancedSettingsPage == null) {
            advancedSettingsPage = new AdvancedSettingsPage(driver);
        }
        return advancedSettingsPage;
    }

    public BrowserPage BrowserPage() {
        if (browserPage == null) {
            browserPage = new BrowserPage(driver);
        }
        return browserPage;
    }
}
