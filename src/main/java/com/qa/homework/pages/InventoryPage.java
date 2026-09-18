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

    private final By inventoryContainer = By.id("inventory_container");
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By cartLink = By.cssSelector("[data-test='shopping-cart-link']");
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");
    private final By cartContents = By.id("cart_contents_container");
    private final By productSort = By.cssSelector("[data-test='product-sort-container']");

    public boolean isLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
        return isDisplayed(inventoryContainer);
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
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        return driver.findElements(inventoryItems).size();
    }

    public List<String> productNames() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        return driver.findElements(inventoryItems).stream()
                .map(product -> product.findElement(By.cssSelector(".inventory_item_name")).getText())
                .collect(Collectors.toList());
    }

    public List<Double> productPrices() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        return driver.findElements(inventoryItems).stream()
                .map(product -> product.findElement(By.cssSelector(".inventory_item_price")).getText())
                .map(this::parsePrice)
                .collect(Collectors.toList());
    }

    public long uniqueProductImageCount() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        return driver.findElements(inventoryItems).stream()
                .map(product -> product.findElement(By.tagName("img")).getAttribute("src"))
                .distinct()
                .count();
    }

    public InventoryPage sortByPriceLowToHigh() {
        new Select(visible(productSort)).selectByVisibleText("Price (low to high)");
        return this;
    }

    public String selectedSortOption() {
        return new Select(visible(productSort)).getFirstSelectedOption().getText();
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
        return Integer.parseInt(text(cartBadge));
    }

    public int cartCountOrZero() {
        if (driver.findElements(cartBadge).isEmpty()) {
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
        try {
            click(cartLink);
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartContents));
        } catch (TimeoutException e) {
            clickWithJavaScript(cartLink);
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartContents));
        }
        return new CartPage();
    }

    private double parsePrice(String price) {
        return Double.parseDouble(price.replace("$", "").trim());
    }

    private WebElement productButton(String productName) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        List<WebElement> products = driver.findElements(inventoryItems);

        for (WebElement product : products) {
            String name = product.findElement(By.cssSelector(".inventory_item_name")).getText();

            if (name.equals(productName)) {
                return product.findElement(By.tagName("button"));
            }
        }

        throw new IllegalArgumentException("Product not found: " + productName);
    }
}
