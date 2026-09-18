package com.qa.homework.pages.inventory;

import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.data.SauceDemoTestData.UserCredentials;
import com.qa.homework.pages.login.LoginPage;

public final class InventoryPage {

    private InventoryPage() {
    }

    public static com.qa.homework.pages.inventory.page.InventoryPage inventorySortedByPriceLowToHigh(
            UserCredentials user) {

        return LoginPage.openInventory(user).sortByPriceLowToHigh();
    }

    public static com.qa.homework.pages.inventory.page.InventoryPage inventoryAfterAttemptingAllProductAdds(
            UserCredentials user) {

        return LoginPage.openInventory(user).attemptToAddAllProducts();
    }

    public static com.qa.homework.pages.inventory.page.InventoryPage inventoryWithCheckoutProducts() {
        com.qa.homework.pages.inventory.page.InventoryPage inventory =
                LoginPage.openInventory(SauceDemoTestData.standardUser());

        SauceDemoTestData.checkoutProducts().forEach(inventory::addProduct);
        return inventory;
    }
}
