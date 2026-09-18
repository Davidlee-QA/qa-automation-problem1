package com.qa.homework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

public class CartPage extends BasePage {

    private static final By CART_CONTENTS = By.id("cart_contents_container");
    private static final By CART_ITEM_NAMES = By.cssSelector(".cart_item .inventory_item_name");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By FIRST_NAME_INPUT = By.id("first-name");

    public boolean containsProduct(String productName) {
        try {
            visible(CART_CONTENTS);
            wait.until(ignoredDriver -> cartContainsProduct(productName));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public CheckoutPage startCheckout() {
        clickAndWaitFor(CHECKOUT_BUTTON, FIRST_NAME_INPUT);
        return new CheckoutPage();
    }

    private boolean cartContainsProduct(String productName) {
        return driver.findElements(CART_ITEM_NAMES).stream()
                .anyMatch(cartItem -> productName.equals(cartItem.getText()));
    }
}
