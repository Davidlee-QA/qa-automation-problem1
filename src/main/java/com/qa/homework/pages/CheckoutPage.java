package com.qa.homework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutPage extends BasePage {

    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By finishButton = By.id("finish");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    private final By itemTotal = By.cssSelector("[data-test='subtotal-label']");
    private final By tax = By.cssSelector("[data-test='tax-label']");
    private final By total = By.cssSelector("[data-test='total-label']");
    private final By completeHeader = By.cssSelector("[data-test='complete-header']");

    public CheckoutPage enterCustomerInformation(
            String first, String last, String zip) {
        fillCustomerInformation(first, last, zip);
        clickAndWaitFor(continueButton, total);
        return this;
    }

    public CheckoutPage continueWithoutInformation() {
        clickAndWaitFor(continueButton, errorMessage);
        return this;
    }

    public String getErrorMessage() {
        return text(errorMessage);
    }

    public double getItemTotal() {
        return parseMoney(text(itemTotal));
    }

    public double getTax() {
        return parseMoney(text(tax));
    }

    public double getTotal() {
        return parseMoney(text(total));
    }

    public CheckoutPage finishOrder() {
        clickAndWaitFor(finishButton, completeHeader);
        return this;
    }

    public String getConfirmationMessage() {
        return text(completeHeader);
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
            type(firstName, first);
            type(lastName, last);
            type(postalCode, zip);

            if (customerInformationMatches(first, last, zip)) {
                return;
            }
        }

        throw new IllegalStateException("Customer information was not entered correctly.");
    }

    private boolean customerInformationMatches(String first, String last, String zip) {
        return first.equals(value(firstName))
                && last.equals(value(lastName))
                && zip.equals(value(postalCode));
    }

    private void clickAndWaitFor(By button, By destination) {
        try {
            click(button);
            wait.until(ExpectedConditions.visibilityOfElementLocated(destination));
        } catch (TimeoutException e) {
            clickWithJavaScript(button);
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(destination));
            } catch (TimeoutException retryFailure) {
                throw new TimeoutException(
                        "Timed out after clicking " + button
                                + " and waiting for " + destination
                                + ". Current URL: " + driver.getCurrentUrl()
                                + ". Page text: " + pageText(),
                        retryFailure);
            }
        }
    }

    private String pageText() {
        String text = driver.findElement(By.tagName("body")).getText()
                .replaceAll("\\s+", " ")
                .trim();
        return text.length() > 500 ? text.substring(0, 500) + "..." : text;
    }
}
