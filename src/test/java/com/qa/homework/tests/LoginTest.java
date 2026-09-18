package com.qa.homework.tests;

import com.qa.homework.assertions.SortOrder;
import com.qa.homework.base.BaseTest;
import com.qa.homework.config.Config;
import com.qa.homework.data.LoginDataProvider;
import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.data.SauceDemoTestData.UserCredentials;
import com.qa.homework.pages.inventory.InventoryPage;
import com.qa.homework.pages.login.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "login", "inventory", "baseline"})
    public void standardUserCanOpenInventoryPage() {
        UserCredentials user = SauceDemoTestData.standardUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for " + user.username() + ".");
    }

    @Test(groups = {"smoke", "inventory", "baseline"})
    public void standardUserSeesStandardProductCatalog() {
        UserCredentials user = SauceDemoTestData.standardUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertEquals(
                inventory.productCount(),
                SauceDemoTestData.standardProductCount(),
                user.username() + " should see the standard SauceDemo product catalog.");
    }

    @Test(groups = {"regression", "inventory", "baseline"})
    public void standardUserSeesUniqueProductImages() {
        UserCredentials user = SauceDemoTestData.standardUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertEquals(
                inventory.uniqueProductImageCount(),
                SauceDemoTestData.standardProductCount(),
                user.username() + " should see one unique product image per product.");
    }

    @Test(groups = {"regression", "login"})
    public void lockedOutUserCannotLogin() {
        UserCredentials user = SauceDemoTestData.lockedOutUser();
        com.qa.homework.pages.login.page.LoginPage loginPage =
                LoginPage.failLogin(user.username(), user.password());

        Assert.assertTrue(
                loginPage.getErrorMessage().contains(SauceDemoTestData.lockedOutMessage()),
                "Locked out user should see a locked account error.");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void problemUserCanOpenInventoryPage() {
        UserCredentials user = SauceDemoTestData.problemUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for " + user.username() + ".");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void problemUserSeesStandardProductCatalog() {
        UserCredentials user = SauceDemoTestData.problemUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertEquals(
                inventory.productCount(),
                SauceDemoTestData.standardProductCount(),
                user.username() + " should still load the standard product catalog.");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void problemUserShowsBrokenImages() {
        UserCredentials user = SauceDemoTestData.problemUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.uniqueProductImageCount() < SauceDemoTestData.standardProductCount(),
                user.username() + " should expose the known broken image issue.");
    }

    @Test(groups = {"regression", "cart", "known-issue"})
    public void problemUserAddsOnlyPartOfCatalogToCart() {
        UserCredentials user = SauceDemoTestData.problemUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                InventoryPage.inventoryAfterAttemptingAllProductAdds(user);

        Assert.assertTrue(
                inventory.cartCountOrZero() < SauceDemoTestData.standardProductCount(),
                user.username() + " should expose the known partial add-to-cart issue.");
    }

    @Test(groups = {"performance", "login"})
    public void performanceGlitchUserCanOpenInventoryPage() {
        UserCredentials user = SauceDemoTestData.performanceGlitchUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for " + user.username() + ".");
    }

    @Test(groups = {"performance", "login"})
    public void performanceGlitchUserCanLoginWithinAcceptableTime() {
        UserCredentials user = SauceDemoTestData.performanceGlitchUser();
        long durationSeconds = LoginPage.loginDurationSeconds(user);
        int maxSeconds = Config.performanceLoginMaxSeconds();

        Assert.assertTrue(
                durationSeconds <= maxSeconds,
                user.username() + " login should finish within "
                        + maxSeconds + " seconds but took " + durationSeconds + " seconds.");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void errorUserCanOpenInventoryPage() {
        UserCredentials user = SauceDemoTestData.errorUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for " + user.username() + ".");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void errorUserCannotChangeProductSorting() {
        UserCredentials user = SauceDemoTestData.errorUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                InventoryPage.inventorySortedByPriceLowToHigh(user);
        boolean sortingIssueIsVisible = inventory.sortingErrorAlertIsShown()
                || SauceDemoTestData.defaultSortOption().equals(inventory.selectedSortOption());

        Assert.assertTrue(
                sortingIssueIsVisible,
                user.username() + " should expose the known issue where product sorting is blocked or broken.");
    }

    @Test(groups = {"visual", "inventory", "known-issue"})
    public void visualUserCanOpenInventoryPage() {
        UserCredentials user = SauceDemoTestData.visualUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(user);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for " + user.username() + ".");
    }

    @Test(groups = {"visual", "inventory", "known-issue"})
    public void visualUserShowsBrokenLowToHighPriceSorting() {
        UserCredentials user = SauceDemoTestData.visualUser();
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                InventoryPage.inventorySortedByPriceLowToHigh(user);

        Assert.assertFalse(
                SortOrder.isAscending(inventory.productPrices()),
                user.username() + " should expose the known visual price sorting issue.");
    }

    @Test(
            dataProvider = "invalidLoginCases",
            dataProviderClass = LoginDataProvider.class,
            groups = {"regression", "login"})
    public void invalidLoginShowsUsefulError(
            String username,
            String password,
            String expectedMessageFragment) {

        com.qa.homework.pages.login.page.LoginPage loginPage =
                LoginPage.failLogin(username, password);

        Assert.assertTrue(
                loginPage.getErrorMessage().contains(expectedMessageFragment),
                "Unexpected login error message.");
    }
}
