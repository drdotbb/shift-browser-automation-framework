package com.automation.browser.utils;

import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite Started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite Finished: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test Failed: " + result.getMethod().getMethodName());
        
        String screenshotPath = ScreenshotUtil.takeScreenshot(result.getMethod().getMethodName());
        if (screenshotPath != null) {
            try (InputStream is = Files.newInputStream(Paths.get(screenshotPath))) {
                Allure.addAttachment("Screenshot", is);
            } catch (IOException e) {
                logger.error("Failed to attach screenshot to Allure report", e);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test Skipped: " + result.getMethod().getMethodName());
    }
}
