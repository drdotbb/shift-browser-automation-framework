package com.automation.browser.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.error("Sorry, unable to find " + CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            logger.error("Error loading configuration properties", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getBrowserPath() {
        String path = getProperty("browser.path");
        if (path != null && path.contains("%")) {
            // Simple expansion for %LOCALAPPDATA% and other environment variables
            for (java.util.Map.Entry<String, String> entry : System.getenv().entrySet()) {
                path = path.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        return path;
    }

    public static String getWinAppDriverUrl() {
        return getProperty("winappdriver.url");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait"));
    }

    public static boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("take.screenshot.on.failure"));
    }

    public static boolean useAppiumServer() {
        return Boolean.parseBoolean(getProperty("use.appium.server"));
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
