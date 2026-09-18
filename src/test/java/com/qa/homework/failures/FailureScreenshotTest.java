package com.qa.homework.failures;

import com.qa.homework.base.BaseTest;
import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.pages.cart.CartPage;
import com.qa.homework.pages.login.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureScreenshotTest extends BaseTest {

    @Test(groups = {"screenshot-demo"})
    public void demoFailureOnInventoryPageCreatesScreenshot() {
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(SauceDemoTestData.standardUser());

        Assert.assertEquals(
                inventory.productCount(),
                SauceDemoTestData.screenshotDemoExpectedInventoryProductCount(),
                "Intentional failure: this assertion verifies screenshot capture on inventory page.");
    }

    @Test(groups = {"screenshot-demo"})
    public void demoFailureOnCartPageCreatesScreenshot() {
        com.qa.homework.pages.cart.page.CartPage cart =
                CartPage.cartWithProduct(SauceDemoTestData.backpack());

        Assert.assertTrue(
                cart.containsProduct(SauceDemoTestData.screenshotDemoMissingProduct()),
                "Intentional failure: this assertion verifies screenshot capture on cart page.");
    }
}
