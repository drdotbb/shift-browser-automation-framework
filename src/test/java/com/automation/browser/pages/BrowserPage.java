package com.automation.browser.pages;

import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class BrowserPage extends BasePage<BrowserPage> {

    @FindBy(id = "omnibox-textbox")
    private WebElement addressBar;

    @FindBy(name = "Back")
    private WebElement backButton;

    @FindBy(name = "Forward")
    private WebElement forwardButton;

    @FindBy(name = "Refresh")
    private WebElement refreshButton;

    @FindBy(name = "New Tab")
    private WebElement newTabButton;

    @FindBy(name = "Save")
    private WebElement saveBookmarkButton;

    @FindBy(name = "Bookmarks")
    private WebElement bookmarksBar;

    @FindBy(name = "Show Bookmarks Bar")
    private WebElement showBookmarksBarText;

    public BrowserPage(WindowsDriver<WebElement> driver) {
        super(driver);
    }

    public BrowserPage navigateTo(String url) {
        logger.info("Navigating to: {}", url);
        try {
            // Click to focus
            addressBar.click();
            // Clear can be flaky on some edit controls, but let's try
            addressBar.clear();
            // Send keys
            addressBar.sendKeys(url);
            addressBar.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            logger.warn("Address bar interaction failed. Error: {}", e.getMessage());
            try {
                // Fallback: Send Ctrl+L to the active window
                Actions actions = new Actions(driver);
                actions.keyDown(Keys.CONTROL).sendKeys("l").keyUp(Keys.CONTROL).perform();
                sleep(1000); 
                actions.sendKeys(url).sendKeys(Keys.ENTER).perform();
            } catch (Exception ex) {
                logger.error("Fallback navigation failed", ex);
                throw new RuntimeException("Failed to navigate to " + url, ex);
            }
        }
        return this;
    }

    public BrowserPage ensureBookmarksBarVisible() {
        logger.info("Ensuring Bookmarks Bar is visible...");
        if (isElementPresent(bookmarksBar)) {
            logger.info("Bookmarks Bar is already visible.");
            return this;
        }

        logger.info("Bookmarks Bar not found. Attempting to enable via Settings...");
        // Navigate to Appearance settings where the toggle usually resides
        navigateTo("chrome://settings/appearance");
        sleep(2000);

        try {
            // Try to find the "Show Bookmarks Bar" text and click it (or its parent/toggle)
            if (isElementPresent(showBookmarksBarText)) {
                click(showBookmarksBarText);
                logger.info("Clicked 'Show Bookmarks Bar' option.");
                sleep(1000);
            } else {
                logger.warn("'Show Bookmarks Bar' text not found in Settings.");
                // Fallback: Hotkey
                logger.info("Using Ctrl+Shift+B shortcut.");
                Actions actions = new Actions(driver);
                actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("b").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).perform();
                sleep(1000);
            }
        } catch (Exception e) {
             logger.error("Failed to enable Bookmarks Bar", e);
        }
        
        return this;
    }

    public BrowserPage clickBack() {
        logger.info("Clicking Back button");
        click(backButton);
        return this;
    }

    public BrowserPage clickForward() {
        logger.info("Clicking Forward button");
        click(forwardButton);
        return this;
    }

    public BrowserPage clickRefresh() {
        logger.info("Clicking Refresh button");
        click(refreshButton);
        return this;
    }

    public BrowserPage openNewTab() {
        logger.info("Opening new tab");
        click(newTabButton);
        return this;
    }

    public String getCurrentAddress() {
        return addressBar.getText();
    }
    
    public boolean isBackButtonEnabled() {
        return backButton.isEnabled();
    }

    public BrowserPage saveBookmark() {
        logger.info("Saving current page as bookmark using Ctrl+D...");
        
        // Ensure focus is on the page or address bar to accept hotkeys
        try { addressBar.click(); } catch(Exception e) {}
        
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("d").keyUp(Keys.CONTROL).perform();
        sleep(1000); // Wait for popup

        try {
            waitForVisibility(saveBookmarkButton);
            click(saveBookmarkButton);
            logger.info("Clicked 'Save' button.");
        } catch (Exception e) {
            logger.warn("'Save' button not found or not visible. It might be already saved or popup dismissed.");
        }
        return this;
    }

    public boolean verifyBookmarkInBar(String pageTitlePart) {
        logger.info("Verifying bookmark in Bookmarks Bar for: {}", pageTitlePart);
        
        if (!isElementPresent(bookmarksBar)) {
             logger.warn("Bookmarks bar is not visible!");
             return false;
        }

        try {
            // Find child with matching name
            // We use xpath to search immediate children
            // WinAppDriver Xpath support can be limited, but let's try finding by Name first
            WebElement bookmark = bookmarksBar.findElement(org.openqa.selenium.By.name(pageTitlePart));
            boolean isDisplayed = bookmark.isDisplayed();
            logger.info("Found bookmark '{}': {}", pageTitlePart, isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.info("Exact match by Name failed. Checking children of Bookmarks Bar...");
            try {
                // Get all children of bookmarks bar
                java.util.List<WebElement> children = bookmarksBar.findElements(org.openqa.selenium.By.xpath(".//*"));
                for (WebElement child : children) {
                    String name = child.getAttribute("Name");
                    if (name != null && name.contains(pageTitlePart)) {
                        logger.info("Found matching bookmark child: {}", name);
                        return true;
                    }
                }
            } catch (Exception ex) {
                logger.error("Error inspecting bookmarks bar children", ex);
            }
            return false;
        }
    }
}
