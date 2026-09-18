package com.qa.homework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryPage extends BasePage {

    private static final By INVENTORY_CONTAINER = By.id("inventory_container");
    private static final By INVENTORY_ITEMS = By.cssSelector(".inventory_item");
    private static final By PRODUCT_NAME = By.cssSelector(".inventory_item_name");
    private static final By PRODUCT_PRICE = By.cssSelector(".inventory_item_price");
    private static final By PRODUCT_IMAGE = By.tagName("img");
    private static final By PRODUCT_ACTION_BUTTON = By.tagName("button");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CART_CONTENTS = By.id("cart_contents_container");
    private static final By PRODUCT_SORT = By.cssSelector("[data-test='product-sort-container']");

    public boolean isLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));
        return isDisplayed(INVENTORY_CONTAINER);
    }

    public InventoryPage addProduct(String productName) {
        int expectedCount = cartCountOrZero() + 1;
        WebElement button = productButton(productName);

        try {
            button.click();
            wait.until(driver -> cartCountOrZero() == expectedCount);
        } catch (TimeoutException e) {
            clickWithJavaScript(button);
            wait.until(driver -> cartCountOrZero() == expectedCount);
        }

        return this;
    }

    public InventoryPage addAllProducts() {
        for (String productName : productNames()) {
            addProduct(productName);
        }
        return this;
    }

    public InventoryPage attemptToAddAllProducts() {
        for (String productName : productNames()) {
            productButton(productName).click();
        }
        return this;
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

    public InventoryPage sortByPriceLowToHigh() {
        new Select(visible(PRODUCT_SORT)).selectByVisibleText("Price (low to high)");
        return this;
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

    public InventoryPage acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        return this;
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
}
