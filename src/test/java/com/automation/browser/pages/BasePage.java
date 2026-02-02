package com.automation.browser.pages;

import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class BasePage<T> {
    protected WindowsDriver<WebElement> driver;
    protected WebDriverWait wait;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BasePage(WindowsDriver<WebElement> driver) {
        this.driver = driver;
        // Selenium 3 uses timeout in seconds (long), Selenium 4 uses Duration. 
        // Assuming Selenium 3 based on existing code (new WebDriverWait(driver, 30))
        this.wait = new WebDriverWait(driver, 30);
        PageFactory.initElements(driver, this);
    }

    @SuppressWarnings("unchecked")
    public T waitForVisibility(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (WebDriverException e) {
            logger.warn("WebDriverException occurred while waiting for visibility: {}", e.getMessage());
            try {
                wait.until(ExpectedConditions.visibilityOf(element));
            } catch (Exception retryEx) {
                 logger.error("Failed to wait for visibility after retry: {}", retryEx.getMessage());
                 throw retryEx;
            }
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T click(WebElement element) {
        waitForVisibility(element);
        waitForElementToBeClickable(element);
        try {
            element.click();
            logger.info("Clicked element: {}", getElementName(element));
        } catch (Exception e) {
            logger.error("Failed to click element: {}", getElementName(element), e);
            throw e;
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T type(WebElement element, String text) {
        waitForVisibility(element);
        try {
            element.clear();
            element.sendKeys(text);
            logger.info("Typed '{}' into element: {}", text, getElementName(element));
        } catch (Exception e) {
            logger.error("Failed to type into element: {}", getElementName(element), e);
            throw e;
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T waitForElementToBeClickable(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.warn("Element not clickable: {}", e.getMessage());
            throw e;
        }
        return (T) this;
    }

    public boolean isElementPresent(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public T pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return (T) this;
    }
    
    // Alias for pause to match example
    protected void sleep(int milliseconds) {
        pause(milliseconds);
    }

    private String getElementName(WebElement element) {
        try {
            String text = element.getText();
            if (text != null && !text.isEmpty()) {
                return text;
            }
            return element.toString();
        } catch (Exception e) {
            return "Unknown Element";
        }
    }
    
    // Additional helper methods from example structure
    
    @SuppressWarnings("unchecked")
    public T verifyElementIsPresent(WebElement element) {
        if (!isElementPresent(element)) {
            throw new AssertionError("Element is not present, but should be: " + getElementName(element));
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T retryWithDelay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return (T) this;
    }
}
