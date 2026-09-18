package com.qa.homework.tests;

import com.qa.homework.base.BaseTest;
import com.qa.homework.data.CheckoutDataProvider;
import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.pages.cart.CartPage;
import com.qa.homework.pages.checkout.CheckoutPage;
import com.qa.homework.pages.inventory.InventoryPage;
import com.qa.homework.pages.login.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Test(groups = {"smoke", "checkout", "e2e"})
    public void checkoutUserCanOpenInventoryPage() {
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(SauceDemoTestData.standardUser());

        Assert.assertTrue(
                inventory.isLoaded(),
                "Inventory page should be displayed before checkout.");
    }

    @Test(groups = {"smoke", "checkout", "cart"})
    public void checkoutProductsAppearInCartBadge() {
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                InventoryPage.inventoryWithCheckoutProducts();

        Assert.assertEquals(
                inventory.cartCount(),
                SauceDemoTestData.checkoutProducts().size(),
                "Cart badge should show selected products.");
    }

    @Test(
            dataProvider = "checkoutProducts",
            dataProviderClass = CheckoutDataProvider.class,
            groups = {"smoke", "checkout", "cart"})
    public void selectedProductExistsInCart(String product) {
        com.qa.homework.pages.cart.page.CartPage cart =
                CartPage.cartWithCheckoutProducts();

        Assert.assertTrue(
                cart.containsProduct(product),
                product + " should exist in the cart.");
    }

    @Test(groups = {"regression", "checkout"})
    public void checkoutTotalEqualsItemTotalPlusTax() {
        com.qa.homework.pages.checkout.page.CheckoutPage checkout =
                CheckoutPage.checkoutOverviewWithDefaultCustomer();

        Assert.assertEquals(
                checkout.getTotal(),
                checkout.getItemTotal() + checkout.getTax(),
                0.01,
                "Checkout total should equal item total plus tax.");
    }

    @Test(groups = {"smoke", "checkout", "e2e"})
    public void userCanFinishCheckoutOrder() {
        com.qa.homework.pages.checkout.page.CheckoutPage checkout =
                CheckoutPage.completedCheckout();

        Assert.assertEquals(
                checkout.getConfirmationMessage(),
                SauceDemoTestData.orderConfirmationMessage(),
                "Order confirmation should be displayed.");
    }

    @Test(groups = {"regression", "checkout"})
    public void checkoutRequiresCustomerInformation() {
        com.qa.homework.pages.checkout.page.CheckoutPage checkout =
                CheckoutPage.checkoutWithoutCustomerInformation();

        Assert.assertTrue(
                checkout.getErrorMessage().contains(SauceDemoTestData.checkoutFirstNameRequiredMessage()),
                "Checkout should require first name.");
    }
}
