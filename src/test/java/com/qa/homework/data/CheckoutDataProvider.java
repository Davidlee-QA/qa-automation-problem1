package com.qa.homework.data;

import org.testng.annotations.DataProvider;

public final class CheckoutDataProvider {

    private CheckoutDataProvider() {
    }

    @DataProvider(name = "checkoutProducts")
    public static Object[][] checkoutProducts() {
        return SauceDemoTestData.checkoutProducts().stream()
                .map(product -> new Object[]{product})
                .toArray(Object[][]::new);
    }
}
