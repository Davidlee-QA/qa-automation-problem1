package com.qa.homework.pages.checkout;

import com.qa.homework.data.SauceDemoTestData;
import com.qa.homework.data.SauceDemoTestData.CheckoutCustomer;
import com.qa.homework.pages.cart.CartPage;

public final class CheckoutPage {

    private CheckoutPage() {
    }

    public static com.qa.homework.pages.checkout.page.CheckoutPage checkoutOverviewWithDefaultCustomer() {
        CheckoutCustomer customer = SauceDemoTestData.defaultCheckoutCustomer();
        return CartPage.cartWithCheckoutProducts()
                .startCheckout()
                .enterCustomerInformation(
                        customer.firstName(),
                        customer.lastName(),
                        customer.postalCode());
    }

    public static com.qa.homework.pages.checkout.page.CheckoutPage checkoutWithoutCustomerInformation() {
        return CartPage.cartWithProduct(SauceDemoTestData.checkoutRequiredInformationProduct())
                .startCheckout()
                .continueWithoutInformation();
    }

    public static com.qa.homework.pages.checkout.page.CheckoutPage completedCheckout() {
        com.qa.homework.pages.checkout.page.CheckoutPage checkout =
                checkoutOverviewWithDefaultCustomer();
        return checkout.finishOrder();
    }
}
