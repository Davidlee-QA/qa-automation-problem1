package com.qa.homework.pages.checkout.locators;

import org.openqa.selenium.By;

public final class CheckoutLocators {

    public static final By FIRST_NAME_INPUT = By.id("first-name");
    public static final By LAST_NAME_INPUT = By.id("last-name");
    public static final By POSTAL_CODE_INPUT = By.id("postal-code");
    public static final By CONTINUE_BUTTON = By.id("continue");
    public static final By FINISH_BUTTON = By.id("finish");
    public static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    public static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']");
    public static final By TAX = By.cssSelector("[data-test='tax-label']");
    public static final By TOTAL = By.cssSelector("[data-test='total-label']");
    public static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    private CheckoutLocators() {
    }
}
