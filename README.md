# Shift Browser Automation Framework

This project contains an automated test suite for the Shift Browser using WinAppDriver, Selenium, and TestNG.

## Compatibility Note

This project utilizes **Appium Java Client 7.6.0** and **Selenium 3.141.59**. This version selection is intentional to ensure native compatibility with **WinAppDriver 1.2.1**, which relies on the legacy JSON Wire Protocol. Newer versions of Appium (8.x/9.x) enforce the W3C WebDriver Protocol, which WinAppDriver does not fully support natively without an external Appium Server acting as a proxy.

## Prerequisites

1.  **Java JDK 11** or higher.
2.  **Maven** installed and configured in system path.
3.  **Windows 10/11** environment (Developer Mode enabled recommended).
4.  **WinAppDriver** installed and running.
    *   Download from: [WinAppDriver Releases](https://github.com/microsoft/WinAppDriver/releases)
    *   Recommended version: 1.2.1

## Setup & Configuration

1.  **Install WinAppDriver:**
    *   Download and install `WindowsApplicationDriver.msi`.
    *   Enable **Developer Mode** in Windows Settings -> Update & Security -> For developers.

2.  **Start WinAppDriver:**
    *   Go to the installation directory (usually `C:\Program Files (x86)\Windows Application Driver`).
    *   Run `WinAppDriver.exe`.
    *   It should start listening on `http://127.0.0.1:4723`.

3.  **Project Setup:**
    *   Clone the repository.
    *   Open the project directory in a terminal.
    *   Run `mvn clean install -DskipTests` to download dependencies.

## Running Tests

To execute the tests, ensure WinAppDriver is running, then use the following command:

```bash
mvn clean test
```

This will run the tests defined in `testng.xml`.

## Test Reports

After the test execution, you can verify the results and view a detailed report using Allure.

### Generate and View Allure Report

Run the following command to serve the report in your default browser:

```bash
mvn allure:serve
```

Or generate it to the `target/site/allure-maven-plugin` directory:

```bash
mvn allure:report
```

The report provides detailed information about test execution status (green for pass, red for fail), logs, and other metrics.
