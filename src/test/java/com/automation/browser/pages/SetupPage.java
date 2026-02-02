package com.automation.browser.pages;

import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetupPage extends BasePage<SetupPage> {

    @FindBy(name = "EULA and Privacy Policy Checkbox")
    private WebElement eulaCheckbox;

    @FindBy(name = "Go to next step")
    private WebElement nextButton;

    @FindBy(name = "Skip without adding apps")
    private WebElement skipAppsButton;

    @FindBy(name = "Next")
    private WebElement nextAfterSkipButton;

    @FindBy(name = "Select this template and continue")
    private WebElement selectTemplateButton;

    // Open Shift button is dynamic/hard to find, we might handle it specially or find by xpath dynamically

    public SetupPage(WindowsDriver<WebElement> driver) {
        super(driver);
    }

    public boolean isSetupVisible() {
        try {
            // Check for EULA checkbox or Next button
            return isElementPresent(eulaCheckbox) || isElementPresent(nextButton);
        } catch (Exception e) {
            return false;
        }
    }

    public SetupPage acceptEula() {
        logger.info("Accepting EULA...");
        wait.until(ExpectedConditions.elementToBeClickable(eulaCheckbox));
        
        // Retry logic for checkbox as in CommonFlows
        for (int attempts = 0; attempts < 2; attempts++) {
            if (isChecked(eulaCheckbox)) break;
            eulaCheckbox.click();
            sleep(300);
            // Refresh element if needed (PageFactory proxies handle re-finding usually, but explicit find might be safer if stale)
        }
        
        if (!isChecked(eulaCheckbox)) {
            throw new AssertionError("Failed to check the EULA and Privacy Policy Checkbox");
        }
        return this;
    }

    public SetupPage clickNext() {
        logger.info("Clicking Next...");
        click(nextButton);
        return this;
    }

    public SetupPage clickNextAgain() {
        logger.info("Clicking Next again...");
        sleep(1000); // Wait for transition
        wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        click(nextButton);
        return this;
    }

    public SetupPage skipApps() {
        logger.info("Skipping apps...");
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(skipAppsButton));
        click(skipAppsButton);
        return this;
    }

    public SetupPage clickNextAfterSkip() {
        logger.info("Clicking Next after skip...");
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(nextAfterSkipButton));
        click(nextAfterSkipButton);
        return this;
    }

    public SetupPage selectTemplate() {
        logger.info("Selecting template...");
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(selectTemplateButton));
        click(selectTemplateButton);
        return this;
    }

    public SetupPage openShift() {
        logger.info("Waiting for 'Open Shift'...");
        sleep(3000);

        WebElement openShiftBtn = findOpenShiftButtonWithSweep();
        
        if (openShiftBtn != null) {
            logger.info("Clicking 'Open Shift'...");
            // Use Actions for a more robust click, as direct click can be flaky for this element
            Actions actions = new Actions(driver);
            actions.moveToElement(openShiftBtn).click().perform();
        } else {
            throw new RuntimeException("Could not find 'Open Shift' button even after mouse sweep.");
        }
        return this;
    }

    private boolean isChecked(WebElement element) {
        String ariaChecked = element.getAttribute("AriaProperties");
        return ariaChecked != null && ariaChecked.contains("checked=true");
    }

    private WebElement findOpenShiftButtonWithSweep() {
        Actions actions = new Actions(driver);
        WebElement openShift = null;

        // Save original implicit wait
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        actions.moveByOffset(0, -100).perform();
        
        try {
            logger.info("Sweeping mouse left and looking for 'Open Shift'...");
            for (int i = 0; i < 100; i++) { 
                try {
                    List<WebElement> elements = driver.findElements(By.xpath("//*[contains(@Name, 'Open Shift')]"));
                    if (!elements.isEmpty()) {
                        WebElement el = elements.get(0);
                        if (el.isDisplayed()) {
                            openShift = el;
                            logger.info("Element found at sweep step " + i);
                            break;
                        }
                    }
                } catch (Exception e) {}

                actions.moveByOffset(-30, 0).perform();
                sleep(10);
            }
        } catch (Exception e) {
            logger.error("Error during sweep: " + e.getMessage());
        } finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return openShift;
    }
}
