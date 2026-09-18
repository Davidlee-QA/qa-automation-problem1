package com.qa.homework.pages.inventory.locators;

import org.openqa.selenium.By;

public final class InventoryLocators {

    public static final By INVENTORY_CONTAINER = By.id("inventory_container");
    public static final By INVENTORY_ITEMS = By.cssSelector(".inventory_item");
    public static final By PRODUCT_NAME = By.cssSelector(".inventory_item_name");
    public static final By PRODUCT_PRICE = By.cssSelector(".inventory_item_price");
    public static final By PRODUCT_IMAGE = By.tagName("img");
    public static final By PRODUCT_ACTION_BUTTON = By.tagName("button");
    public static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    public static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    public static final By CART_CONTENTS = By.id("cart_contents_container");
    public static final By PRODUCT_SORT = By.cssSelector("[data-test='product-sort-container']");

    private InventoryLocators() {
    }
}
