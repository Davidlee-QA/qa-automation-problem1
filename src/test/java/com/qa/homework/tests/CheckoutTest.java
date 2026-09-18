package com.qa.homework.tests;

import com.qa.homework.base.BaseTest;
import com.qa.homework.pages.CartPage;
import com.qa.homework.pages.CheckoutPage;
import com.qa.homework.pages.InventoryPage;
import com.qa.homework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(groups = {"smoke", "checkout", "e2e"})
    public void userCanAddMultipleItemsAndCheckout() {
        InventoryPage inventory = new LoginPage()
                .loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(inventory.isLoaded());

        inventory
                .addProduct(BACKPACK)
                .addProduct(BIKE_LIGHT);

        Assert.assertEquals(
                inventory.cartCount(),
                2,
                "Cart badge should show two products.");

        CartPage cart = inventory.openCart();

        Assert.assertTrue(
                cart.containsProduct(BACKPACK),
                "Backpack should exist in the cart.");
        Assert.assertTrue(
                cart.containsProduct(BIKE_LIGHT),
                "Bike Light should exist in the cart.");

        CheckoutPage checkout = cart
                .startCheckout()
                .enterCustomerInformation(
                        "Senior",
                        "QA",
                        "700000");

        Assert.assertEquals(
                checkout.getTotal(),
                checkout.getItemTotal() + checkout.getTax(),
                0.01,
                "Checkout total should equal item total plus tax.");

        checkout.finishOrder();

        Assert.assertEquals(
                checkout.getConfirmationMessage(),
                "Thank you for your order!",
                "Order confirmation should be displayed.");
    }

    @Test(groups = {"regression", "checkout"})
    public void checkoutRequiresCustomerInformation() {
        CheckoutPage checkout = new LoginPage()
                .loginAs("standard_user", "secret_sauce")
                .addProduct(BACKPACK)
                .openCart()
                .startCheckout()
                .continueWithoutInformation();

        Assert.assertTrue(
                checkout.getErrorMessage().contains("First Name is required"),
                "Checkout should require first name.");
    }
}
