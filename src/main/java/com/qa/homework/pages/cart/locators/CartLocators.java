package com.qa.homework.pages.cart.locators;

import org.openqa.selenium.By;

public final class CartLocators {

    public static final By CART_CONTENTS = By.id("cart_contents_container");
    public static final By CART_ITEM_NAMES = By.cssSelector(".cart_item .inventory_item_name");
    public static final By CHECKOUT_BUTTON = By.id("checkout");
    public static final By FIRST_NAME_INPUT = By.id("first-name");

    private CartLocators() {
    }
}
