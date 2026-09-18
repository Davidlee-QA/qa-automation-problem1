package com.qa.homework.pages.checkout.actions;

import com.qa.homework.pages.BasePage;

import static com.qa.homework.pages.checkout.locators.CheckoutLocators.COMPLETE_HEADER;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.CONTINUE_BUTTON;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.ERROR_MESSAGE;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.FINISH_BUTTON;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.FIRST_NAME_INPUT;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.ITEM_TOTAL;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.LAST_NAME_INPUT;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.POSTAL_CODE_INPUT;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.TAX;
import static com.qa.homework.pages.checkout.locators.CheckoutLocators.TOTAL;

public abstract class CheckoutActions<T extends CheckoutActions<T>> extends BasePage {

    public T enterCustomerInformation(
            String first, String last, String zip) {
        fillCustomerInformation(first, last, zip);
        clickAndWaitFor(CONTINUE_BUTTON, TOTAL);
        return self();
    }

    public T continueWithoutInformation() {
        clickAndWaitFor(CONTINUE_BUTTON, ERROR_MESSAGE);
        return self();
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }

    public double getItemTotal() {
        return parseMoney(text(ITEM_TOTAL));
    }

    public double getTax() {
        return parseMoney(text(TAX));
    }

    public double getTotal() {
        return parseMoney(text(TOTAL));
    }

    public T finishOrder() {
        clickAndWaitFor(FINISH_BUTTON, COMPLETE_HEADER);
        return self();
    }

    public String getConfirmationMessage() {
        return text(COMPLETE_HEADER);
    }

    private double parseMoney(String label) {
        int dollar = label.indexOf('$');
        if (dollar < 0) {
            throw new IllegalStateException("Unable to parse money from label: " + label);
        }
        return Double.parseDouble(label.substring(dollar + 1).trim());
    }

    private void fillCustomerInformation(String first, String last, String zip) {
        for (int attempt = 0; attempt < 3; attempt++) {
            type(FIRST_NAME_INPUT, first);
            type(LAST_NAME_INPUT, last);
            type(POSTAL_CODE_INPUT, zip);

            if (customerInformationMatches(first, last, zip)) {
                return;
            }
        }

        throw new IllegalStateException("Customer information was not entered correctly.");
    }

    private boolean customerInformationMatches(String first, String last, String zip) {
        return first.equals(value(FIRST_NAME_INPUT))
                && last.equals(value(LAST_NAME_INPUT))
                && zip.equals(value(POSTAL_CODE_INPUT));
    }

    protected abstract T self();
}
