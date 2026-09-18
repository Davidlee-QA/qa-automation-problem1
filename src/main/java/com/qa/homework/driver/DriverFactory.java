package com.qa.homework.driver;

import com.qa.homework.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Optional;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<String> BROWSER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initDriver(String requestedBrowser) {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("WebDriver is already initialized for this thread.");
        }

        String browser = resolveBrowser(requestedBrowser);
        WebDriver driver;

        switch (browser) {
            case "chrome" -> driver = new ChromeDriver(chromeOptions());
            case "firefox" -> driver = new FirefoxDriver(firefoxOptions());
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser + ". Supported values: chrome, firefox.");
        }

        DRIVER.set(driver);
        BROWSER.set(browser);

        getDriver().manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(Config.pageLoadTimeoutSeconds()));
        getDriver().manage().timeouts()
                .scriptTimeout(Duration.ofSeconds(30));

        if (!Config.headless()) {
            getDriver().manage().window().maximize();
        }
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized for this thread.");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static Optional<String> currentBrowser() {
        return Optional.ofNullable(BROWSER.get());
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
                BROWSER.remove();
            }
        }
    }

    public static String resolveBrowser(String requestedBrowser) {
        return System.getProperty("browser", requestedBrowser).toLowerCase().trim();
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--window-size=1920,1080",
                "--disable-dev-shm-usage",
                "--no-sandbox");
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        return options;
    }
}
