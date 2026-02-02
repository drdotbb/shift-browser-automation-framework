package com.automation.browser.testBase;

import com.automation.browser.pages.AdvancedSettingsPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebElement;
import io.appium.java_client.windows.WindowsDriver;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.interactions.Actions;
import com.automation.browser.config.TestConfig;

public class CommonFlows extends BaseTest {

    private final String DOWNLOAD_DIR = System.getProperty("user.home") + File.separator + "Downloads";

    public String getShiftExecutablePath() {
        String localAppData = System.getenv("LOCALAPPDATA");
        return localAppData + "\\Shift\\chromium\\shift.exe";
    }

    public String downloadAndInstallShift() throws Exception {
        cleanOldInstallers(DOWNLOAD_DIR);

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_DIR);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

        WebDriverManager.chromedriver().setup();

        System.out.println("Starting Chrome for download...");
        ChromeDriver chromeDriver = new ChromeDriver(options);
        String installerVersion = "";

        try {
            chromeDriver.get("https://shift.com/download/");
            System.out.println("Navigated to download page. Waiting for download...");
            File downloadedFile = waitForDownloadToComplete(DOWNLOAD_DIR, 30);
            Assert.assertNotNull(downloadedFile, "Shift installer was not downloaded successfully.");
            System.out.println("Downloaded file: " + downloadedFile.getAbsolutePath());

            installerVersion = getFileVersion(downloadedFile.getAbsolutePath());
            System.out.println("Installer Version: " + installerVersion);
            Assert.assertNotNull(installerVersion, "Could not determine installer version.");
            Assert.assertFalse(installerVersion.isEmpty(), "Installer version is empty.");

            System.out.println("Installing Shift...");
            installShift(downloadedFile);

            File installedFile = new File(getShiftExecutablePath());

            System.out.println("Waiting for installation to complete (Shift.exe to appear)...");
            int waitTime = 0;
            while (!installedFile.exists() && waitTime < 240) {
                Thread.sleep(1000);
                waitTime++;
            }
            Assert.assertTrue(installedFile.exists(), "Shift executable not found at: " + getShiftExecutablePath());

            System.out.println("Shift installed. Waiting 20 seconds for the app to launch and initialize...");
            Thread.sleep(20000);

            return installerVersion;

        } finally {
            if (chromeDriver != null) {
                chromeDriver.quit();
            }
        }
    }

    public void handleSetup() {
        on.SetupPage().acceptEula()
                .clickNext()
                .pause(500)
                .clickNextAgain()
                .pause(500)
                .skipApps()
                .pause(500)
                .clickNextAfterSkip()
                .pause(500)
                .selectTemplate()
                .pause(500)
                .openShift();
    }

    public void uninstallShift() {
        System.out.println("Uninstalling Shift...");
        String localAppData = System.getenv("LOCALAPPDATA");
        File uninstallExe = new File(localAppData + "\\Shift\\unins000.exe");

        if (uninstallExe.exists()) {
            try {
                // 1. Start the uninstaller without silent flags to trigger the popup
                ProcessBuilder pb = new ProcessBuilder(uninstallExe.getAbsolutePath(), "--uninstall");
                Process process = pb.start();
                
                // 2. Wait for the confirmation popup to appear
                Thread.sleep(3000);

                // 3. Use a Root session to find the "Yes" button by AutomationId "6"
                WindowsDriver<WebElement> rootDriver = null;
                try {
                    DesiredCapabilities caps = new DesiredCapabilities();
                    caps.setCapability("app", "Root");
                    caps.setCapability("platformName", "Windows");
                    caps.setCapability("deviceName", "WindowsPC");
                    
                    // Ensure WinAppDriver is running and accessible
                    rootDriver = new WindowsDriver<>(new URL(TestConfig.getWinAppDriverUrl()), caps);

                    // Find "Yes" button by AccessibilityId (AutomationId)
                    WebElement yesButton = rootDriver.findElementByAccessibilityId("6");
                    if (yesButton != null) {
                        System.out.println("Found 'Yes' button (AutomationId: 6). Clicking it...");
                        
                        // Try standard click
                        try {
                            yesButton.click();
                            System.out.println("Clicked 'Yes' button.");
                        } catch (Exception e) {
                            System.out.println("Standard click failed, trying Actions click...");
                            Actions actions = new Actions(rootDriver);
                            actions.moveToElement(yesButton).click().perform();
                        }
                        
                    } else {
                        System.out.println("Warning: 'Yes' button not found via AutomationId '6'.");
                    }
                } catch (Exception e) {
                    System.err.println("Error interacting with uninstall popup: " + e.getMessage());
                } finally {
                    if (rootDriver != null) {
                        try { rootDriver.quit(); } catch (Exception ignored) {}
                    }
                }

                // 4. Wait for the uninstaller process to complete
                boolean finished = process.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
                if (finished) {
                    System.out.println("Uninstall process finished successfully.");
                } else {
                    System.out.println("Uninstall process timed out (it might be running in background).");
                }
                
                // Extra safety wait
                Thread.sleep(2000);
                
            } catch (Exception e) {
                System.err.println("Failed to uninstall Shift: " + e.getMessage());
            }
        } else {
            System.out.println("Shift uninstaller not found at " + uninstallExe.getAbsolutePath());
        }
    }

    private void cleanOldInstallers(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.contains("Shift") && name.endsWith(".exe"));
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    private File waitForDownloadToComplete(String dirPath, int timeoutSeconds) throws InterruptedException {
        File dir = new File(dirPath);
        int time = 0;
        while (time < timeoutSeconds) {
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.contains("Shift") && name.endsWith(".exe"));
               if(files != null && files.length > 0) {
                return files[0];
               }
            }
            Thread.sleep(1000);
            time++;
        }
        return null;
    }

    private String getFileVersion(String filePath) {
        try {
            String command = "(Get-Item '" + filePath + "').VersionInfo.ProductVersion";
            ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-Command", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String version = reader.readLine();
            process.waitFor();

            if (version != null) {
                return version.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void installShift(File installer) {
        try {
            System.out.println("Running installer: " + installer.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(installer.getAbsolutePath());
            pb.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to run installer", e);
        }
    }

    public AdvancedSettingsPage verifyInstalledVersion(String expectedVersion) {
        on.AdvancedSettingsPage().openQuickSettings()
                .openAdvancedSettings()
                .openAboutShift();
        
        saveScreenshot("About Shift - Version Visible");
        
        on.AdvancedSettingsPage().verifyVersionContains(expectedVersion);
        
        return on.AdvancedSettingsPage();
    }

    public String getInstalledVersion() {
        on.AdvancedSettingsPage().openQuickSettings()
                .openAdvancedSettings();
                //.openAboutShift(); opens about shift on default
        
        saveScreenshot("About Shift - Version Visible");
        
        return on.AdvancedSettingsPage().getVersionText();
    }
}
