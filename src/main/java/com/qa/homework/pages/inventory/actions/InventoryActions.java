package com.qa.homework.pages.inventory.actions;

import com.qa.homework.pages.BasePage;
import com.qa.homework.pages.cart.page.CartPage;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.qa.homework.pages.inventory.locators.InventoryLocators.CART_BADGE;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.CART_CONTENTS;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.CART_LINK;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.INVENTORY_CONTAINER;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.INVENTORY_ITEMS;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.PRODUCT_ACTION_BUTTON;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.PRODUCT_IMAGE;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.PRODUCT_NAME;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.PRODUCT_PRICE;
import static com.qa.homework.pages.inventory.locators.InventoryLocators.PRODUCT_SORT;

public abstract class InventoryActions<T extends InventoryActions<T>> extends BasePage {

    public boolean isLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));
        return isDisplayed(INVENTORY_CONTAINER);
    }

    public T addProduct(String productName) {
        int expectedCount = cartCountOrZero() + 1;
        WebElement button = productButton(productName);

        try {
            button.click();
            wait.until(driver -> cartCountOrZero() == expectedCount);
        } catch (TimeoutException e) {
            clickWithJavaScript(button);
            wait.until(driver -> cartCountOrZero() == expectedCount);
        }

        return self();
    }

    public T addAllProducts() {
        for (String productName : productNames()) {
            addProduct(productName);
        }
        return self();
    }

    public T attemptToAddAllProducts() {
        for (String productName : productNames()) {
            productButton(productName).click();
        }
        return self();
    }

    public int productCount() {
        return productElements().size();
    }

    public List<String> productNames() {
        return productElements().stream()
                .map(this::productName)
                .collect(Collectors.toList());
    }

    public List<Double> productPrices() {
        return productElements().stream()
                .map(this::productPrice)
                .map(this::parsePrice)
                .collect(Collectors.toList());
    }

    public long uniqueProductImageCount() {
        return productElements().stream()
                .map(this::productImageSource)
                .distinct()
                .count();
    }

    public T sortByPriceLowToHigh() {
        new Select(visible(PRODUCT_SORT)).selectByVisibleText("Price (low to high)");
        return self();
    }

    public String selectedSortOption() {
        return new Select(visible(PRODUCT_SORT)).getFirstSelectedOption().getText();
    }

    public boolean sortingErrorAlertIsShown() {
        try {
            String alertMessage = new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(ExpectedConditions.alertIsPresent())
                    .getText();
            driver.switchTo().alert().accept();
            return alertMessage.contains("Sorting is broken");
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int cartCount() {
        return Integer.parseInt(text(CART_BADGE));
    }

    public int cartCountOrZero() {
        if (driver.findElements(CART_BADGE).isEmpty()) {
            return 0;
        }
        return cartCount();
    }

    public String alertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    public T acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        return self();
    }

    public CartPage openCart() {
        clickAndWaitFor(CART_LINK, CART_CONTENTS);
        return new CartPage();
    }

    private double parsePrice(String price) {
        return Double.parseDouble(price.replace("$", "").trim());
    }

    private List<WebElement> productElements() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(INVENTORY_ITEMS, 0));
        return driver.findElements(INVENTORY_ITEMS);
    }

    private String productName(WebElement product) {
        return product.findElement(PRODUCT_NAME).getText();
    }

    private String productPrice(WebElement product) {
        return product.findElement(PRODUCT_PRICE).getText();
    }

    private String productImageSource(WebElement product) {
        return product.findElement(PRODUCT_IMAGE).getAttribute("src");
    }

    private WebElement productActionButton(WebElement product) {
        return product.findElement(PRODUCT_ACTION_BUTTON);
    }

    private WebElement productButton(String productName) {
        for (WebElement product : productElements()) {
            if (productName(product).equals(productName)) {
                return productActionButton(product);
            }
        }

        throw new IllegalArgumentException("Product not found: " + productName);
    }

    protected abstract T self();
}
