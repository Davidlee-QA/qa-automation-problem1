package com.qa.homework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException("config.properties was not found on the classpath.");
            }

            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Config() {
    }

    public static String baseUrl() {
        return System.getProperty("base.url",
                PROPERTIES.getProperty("base.url", "https://www.saucedemo.com/"));
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(System.getProperty(
                "explicit.wait.seconds",
                PROPERTIES.getProperty("explicit.wait.seconds", "10")));
    }

    public static int pageLoadTimeoutSeconds() {
        return Integer.parseInt(System.getProperty(
                "page.load.timeout.seconds",
                PROPERTIES.getProperty("page.load.timeout.seconds", "30")));
    }

    public static int performanceLoginMaxSeconds() {
        return Integer.parseInt(System.getProperty(
                "performance.login.max.seconds",
                PROPERTIES.getProperty("performance.login.max.seconds", "10")));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty(
                "headless",
                PROPERTIES.getProperty("headless", "false")));
    }
}
