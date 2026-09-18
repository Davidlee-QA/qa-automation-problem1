package com.qa.homework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    private final By cartContents = By.id("cart_contents_container");
    private final By checkoutButton = By.id("checkout");
    private final By firstName = By.id("first-name");

    public boolean containsProduct(String productName) {
        By product = By.xpath(
                "//div[contains(@class,'cart_item')]//div[@class='inventory_item_name' and normalize-space()='"
                        + productName + "']");
        try {
            visible(cartContents);
            wait.until(ExpectedConditions.visibilityOfElementLocated(product));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public CheckoutPage startCheckout() {
        clickAndWaitFor(checkoutButton, firstName);
        return new CheckoutPage();
    }

    private void clickAndWaitFor(By button, By destination) {
        try {
            click(button);
            wait.until(ExpectedConditions.visibilityOfElementLocated(destination));
        } catch (TimeoutException e) {
            clickWithJavaScript(button);
            wait.until(ExpectedConditions.visibilityOfElementLocated(destination));
        }
    }
}
