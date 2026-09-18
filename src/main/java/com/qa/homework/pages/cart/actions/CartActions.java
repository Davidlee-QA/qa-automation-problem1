package com.qa.homework.pages.cart.actions;

import com.qa.homework.pages.BasePage;
import com.qa.homework.pages.checkout.page.CheckoutPage;
import org.openqa.selenium.TimeoutException;

import static com.qa.homework.pages.cart.locators.CartLocators.CART_CONTENTS;
import static com.qa.homework.pages.cart.locators.CartLocators.CART_ITEM_NAMES;
import static com.qa.homework.pages.cart.locators.CartLocators.CHECKOUT_BUTTON;
import static com.qa.homework.pages.cart.locators.CartLocators.FIRST_NAME_INPUT;

public abstract class CartActions extends BasePage {

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
