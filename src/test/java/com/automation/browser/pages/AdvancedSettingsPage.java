package com.automation.browser.pages;

import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

public class AdvancedSettingsPage extends BasePage<AdvancedSettingsPage> {

    @FindBy(name = "Quick Settings")
    private WebElement quickSettingsBtn;

    @FindBy(name = "Advanced Settings")
    private WebElement advancedSettingsBtn;

    // AccessibilityId "about-menu"
    @FindBy(id = "about-menu") 
    private WebElement aboutShiftBtn;

    @FindBy(xpath = "//*[starts-with(@Name, 'Version ')]")
    private WebElement versionText;

    public AdvancedSettingsPage(WindowsDriver<WebElement> driver) {
        super(driver);
    }

    public AdvancedSettingsPage openQuickSettings() {
        logger.info("Clicking 'Quick Settings'...");
        try {
            new Actions(driver).moveToElement(quickSettingsBtn).click().perform();
        } catch (Exception e) {
            logger.warn("Actions click failed, fallback to standard click", e);
            click(quickSettingsBtn);
        }
        sleep(1000);
        return this;
    }

    public AdvancedSettingsPage openAdvancedSettings() {
        logger.info("Clicking 'Advanced Settings'...");
        click(advancedSettingsBtn);
        sleep(1000);
        return this;
    }

    public AdvancedSettingsPage openAboutShift() {
        logger.info("Clicking 'About Shift'...");
        driver.findElement(By.id("about-menu")).click();
        sleep(1000);
        return this;
    }

    public String getVersionText() {
        logger.info("Getting version text...");
        wait.until(ExpectedConditions.visibilityOf(versionText));
        String text = versionText.getAttribute("Name");
        logger.info("Found version text: {}", text);
        return text;
    }

    public void verifyVersionContains(String expectedVersion) {
        String currentVersion = getVersionText();
        Assert.assertTrue(currentVersion.contains(expectedVersion),
                "Version mismatch! Expected version " + expectedVersion + " to be part of " + currentVersion);
        logger.info("Version verification successful!");
    }
}
