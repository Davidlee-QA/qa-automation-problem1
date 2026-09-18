package com.qa.homework.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class SauceDemoTestData {

    private static final String DATA_FILE = "test-data/saucedemo-test-data.properties";
    private static final Properties DATA = loadProperties();

    private SauceDemoTestData() {
    }

    public static UserCredentials standardUser() {
        return user("users.standard");
    }

    public static UserCredentials lockedOutUser() {
        return user("users.lockedOut");
    }

    public static UserCredentials problemUser() {
        return user("users.problem");
    }

    public static UserCredentials performanceGlitchUser() {
        return user("users.performanceGlitch");
    }

    public static UserCredentials errorUser() {
        return user("users.error");
    }

    public static UserCredentials visualUser() {
        return user("users.visual");
    }

    public static int standardProductCount() {
        return integer("inventory.standardProductCount");
    }

    public static String defaultSortOption() {
        return value("inventory.defaultSortOption");
    }

    public static String backpack() {
        return value("products.backpack");
    }

    public static String bikeLight() {
        return value("products.bikeLight");
    }

    public static List<String> checkoutProducts() {
        return Arrays.stream(value("checkout.products").split(","))
                .map(String::trim)
                .map(SauceDemoTestData::product)
                .toList();
    }

    public static String checkoutRequiredInformationProduct() {
        return product(value("checkout.requiredInformationProduct"));
    }

    public static CheckoutCustomer defaultCheckoutCustomer() {
        return new CheckoutCustomer(
                value("checkout.firstName"),
                value("checkout.lastName"),
                value("checkout.postalCode"));
    }

    public static String lockedOutMessage() {
        return message("lockedOut");
    }

    public static String checkoutFirstNameRequiredMessage() {
        return message("checkoutFirstNameRequired");
    }

    public static String orderConfirmationMessage() {
        return message("orderConfirmation");
    }

    static String message(String key) {
        return value("messages." + key);
    }

    public static String longText() {
        return "a".repeat(integer("inputs.longTextLength"));
    }

    public static int screenshotDemoExpectedInventoryProductCount() {
        return integer("screenshotDemo.expectedInventoryProductCount");
    }

    public static String screenshotDemoMissingProduct() {
        return value("screenshotDemo.missingProduct");
    }

    private static UserCredentials user(String usernameKey) {
        return new UserCredentials(value(usernameKey), value("users.password"));
    }

    private static String product(String productKey) {
        return value("products." + productKey);
    }

    private static String value(String key) {
        String value = DATA.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing test data key: " + key);
        }
        return value;
    }

    private static int integer(String key) {
        return Integer.parseInt(value(key));
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = SauceDemoTestData.class
                .getClassLoader()
                .getResourceAsStream(DATA_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Test data file not found: " + DATA_FILE);
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load test data file: " + DATA_FILE, e);
        }
    }

    public record UserCredentials(String username, String password) {
    }

    public record CheckoutCustomer(String firstName, String lastName, String postalCode) {
    }
}
