package com.qa.homework.failures;

import com.qa.homework.base.BaseTest;
import com.qa.homework.pages.CartPage;
import com.qa.homework.pages.InventoryPage;
import com.qa.homework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureScreenshotTest extends BaseTest {

    private static final String STANDARD_USER = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(groups = {"screenshot-demo"})
    public void demoFailureOnInventoryPageCreatesScreenshot() {
        InventoryPage inventory = new LoginPage()
                .loginAs(STANDARD_USER, PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should load before forcing the demo failure.");
        Assert.assertEquals(
                inventory.productCount(),
                999,
                "Intentional failure: this assertion verifies screenshot capture on inventory page.");
    }

    @Test(groups = {"screenshot-demo"})
    public void demoFailureOnCartPageCreatesScreenshot() {
        CartPage cart = new LoginPage()
                .loginAs(STANDARD_USER, PASSWORD)
                .addProduct(BACKPACK)
                .openCart();

        Assert.assertTrue(
                cart.containsProduct("Intentional Missing Product"),
                "Intentional failure: this assertion verifies screenshot capture on cart page.");
    }
}
