package com.qa.homework.tests;

import com.qa.homework.base.BaseTest;
import com.qa.homework.config.Config;
import com.qa.homework.pages.InventoryPage;
import com.qa.homework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class LoginTest extends BaseTest {

    private static final String PASSWORD = "secret_sauce";
    private static final String INVALID_CREDENTIAL_MESSAGE =
            "Username and password do not match any user";
    private static final String LONG_INPUT = "a".repeat(128);
    private static final int STANDARD_PRODUCT_COUNT = 6;

    @Test(groups = {"smoke", "login", "inventory", "baseline"})
    public void standardUserCanUseBaselineInventoryBehavior() {
        InventoryPage inventory = new LoginPage()
                .loginAs("standard_user", PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for standard_user.");
        Assert.assertEquals(
                inventory.productCount(),
                STANDARD_PRODUCT_COUNT,
                "standard_user should see the standard SauceDemo product catalog.");
        Assert.assertEquals(
                inventory.uniqueProductImageCount(),
                STANDARD_PRODUCT_COUNT,
                "standard_user should see one unique product image per product.");
    }

    @Test(groups = {"regression", "login"})
    public void lockedOutUserCannotLogin() {
        LoginPage loginPage = new LoginPage()
                .enterUsername("locked_out_user")
                .enterPassword(PASSWORD)
                .clickLoginExpectingFailure();

        Assert.assertTrue(
                loginPage.getErrorMessage().contains("Sorry, this user has been locked out"),
                "Locked out user should see a locked account error.");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void problemUserShowsBrokenImagesAndPartialCartAdds() {
        InventoryPage inventory = new LoginPage()
                .loginAs("problem_user", PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for problem_user.");
        Assert.assertEquals(
                inventory.productCount(),
                STANDARD_PRODUCT_COUNT,
                "problem_user should still load the standard product catalog.");
        Assert.assertTrue(
                inventory.uniqueProductImageCount() < STANDARD_PRODUCT_COUNT,
                "problem_user should expose the known broken image issue.");

        inventory.attemptToAddAllProducts();

        Assert.assertTrue(
                inventory.cartCountOrZero() < STANDARD_PRODUCT_COUNT,
                "problem_user should expose the known partial add-to-cart issue.");
    }

    @Test(groups = {"performance", "login"})
    public void performanceGlitchUserCanLoginWithinAcceptableTime() {
        long start = System.nanoTime();

        InventoryPage inventory = new LoginPage()
                .loginAs("performance_glitch_user", PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for performance_glitch_user.");

        long durationSeconds = Duration.ofNanos(System.nanoTime() - start).toSeconds();
        int maxSeconds = Config.performanceLoginMaxSeconds();

        Assert.assertTrue(
                durationSeconds <= maxSeconds,
                "performance_glitch_user login should finish within "
                        + maxSeconds + " seconds but took " + durationSeconds + " seconds.");
    }

    @Test(groups = {"regression", "inventory", "known-issue"})
    public void errorUserCannotChangeProductSorting() {
        InventoryPage inventory = new LoginPage()
                .loginAs("error_user", PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for error_user.");

        inventory.sortByPriceLowToHigh();

        boolean sortingIssueIsVisible = inventory.sortingErrorAlertIsShown()
                || "Name (A to Z)".equals(inventory.selectedSortOption());

        Assert.assertTrue(
                sortingIssueIsVisible,
                "error_user should expose the known issue where product sorting is blocked or broken.");
    }

    @Test(groups = {"visual", "inventory", "known-issue"})
    public void visualUserShowsBrokenLowToHighPriceSorting() {
        InventoryPage inventory = new LoginPage()
                .loginAs("visual_user", PASSWORD);

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed after successful login for visual_user.");

        inventory.sortByPriceLowToHigh();
        List<Double> prices = inventory.productPrices();

        Assert.assertFalse(
                isSortedAscending(prices),
                "visual_user should expose the known visual price sorting issue.");
    }

    @DataProvider(name = "invalidLoginCases")
    public Object[][] invalidLoginCases() {
        return new Object[][]{
                {"standard_user", "wrong_password", INVALID_CREDENTIAL_MESSAGE},
                {"unknown_user", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"", PASSWORD,
                        "Username is required"},
                {"standard_user", "",
                        "Password is required"},
                {"", "",
                        "Username is required"},
                {" standard_user ", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"standard_user", " " + PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"standard_user", PASSWORD + " ", INVALID_CREDENTIAL_MESSAGE},
                {"STANDARD_USER", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"standard_user", "SECRET_SAUCE", INVALID_CREDENTIAL_MESSAGE},
                {"standard_user@example.com", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"standard_user!", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"' OR '1'='1", PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {LONG_INPUT, PASSWORD, INVALID_CREDENTIAL_MESSAGE},
                {"standard_user", LONG_INPUT, INVALID_CREDENTIAL_MESSAGE}
        };
    }

    @Test(
            dataProvider = "invalidLoginCases",
            groups = {"regression", "login"})
    public void invalidLoginShowsUsefulError(
            String username,
            String password,
            String expectedMessageFragment) {

        LoginPage loginPage = new LoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingFailure();

        Assert.assertTrue(
                loginPage.getErrorMessage().contains(expectedMessageFragment),
                "Unexpected login error message.");
    }

    private boolean isSortedAscending(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i - 1) > values.get(i)) {
                return false;
            }
        }
        return true;
    }
}
