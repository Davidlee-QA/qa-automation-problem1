package com.qa.homework.pages.cart;

import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.pages.login.LoginPage;

public final class CartPage {

    private CartPage() {
    }

    public static com.qa.homework.pages.cart.page.CartPage cartWithCheckoutProducts() {
        return com.qa.homework.pages.inventory.InventoryPage
                .inventoryWithCheckoutProducts()
                .openCart();
    }

    public static com.qa.homework.pages.cart.page.CartPage cartWithProduct(String product) {
        return LoginPage.openInventory(SauceDemoTestData.standardUser())
                .addProduct(product)
                .openCart();
    }
}
