package com.qa.homework.pages;

import org.openqa.selenium.By;

public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By ITEM_TOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    public CheckoutPage enterCustomerInformation(
            String first, String last, String zip) {
        fillCustomerInformation(first, last, zip);
        clickAndWaitFor(CONTINUE_BUTTON, TOTAL);
        return this;
    }

    public CheckoutPage continueWithoutInformation() {
        clickAndWaitFor(CONTINUE_BUTTON, ERROR_MESSAGE);
        return this;
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

    public CheckoutPage finishOrder() {
        clickAndWaitFor(FINISH_BUTTON, COMPLETE_HEADER);
        return this;
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
}
