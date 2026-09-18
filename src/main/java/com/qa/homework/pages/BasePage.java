package com.qa.homework.pages;

import com.qa.homework.config.Config;
import com.qa.homework.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(Config.explicitWaitSeconds()));
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement clickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void clickAndWaitFor(By button, By destination) {
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

    protected void clickWithJavaScript(By locator) {
        WebElement element = visible(locator);
        clickWithJavaScript(element);
    }

    protected void clickWithJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String input) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(input);
        try {
            wait.until(driver -> input.equals(value(locator)));
        } catch (TimeoutException e) {
            setValueWithJavaScript(locator, input);
            try {
                wait.until(driver -> input.equals(value(locator)));
            } catch (TimeoutException retryFailure) {
                throw new TimeoutException(
                        "Timed out entering text into " + locator
                                + ". Expected value: " + input
                                + ". Current value: " + value(locator)
                                + ". Current URL: " + driver.getCurrentUrl(),
                        retryFailure);
            }
        }
    }

    protected String text(By locator) {
        return visible(locator).getText();
    }

    protected String value(By locator) {
        String currentValue = visible(locator).getDomProperty("value");
        return currentValue == null ? "" : currentValue;
    }

    private void setValueWithJavaScript(By locator, String input) {
        WebElement element = visible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "const valueSetter = Object.getOwnPropertyDescriptor("
                        + "window.HTMLInputElement.prototype, 'value').set;"
                        + "valueSetter.call(arguments[0], arguments[1]);"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element,
                input);
    }

    protected boolean isDisplayed(By locator) {
        return !driver.findElements(locator).isEmpty()
                && driver.findElement(locator).isDisplayed();
    }

    private String pageText() {
        String text = driver.findElement(By.tagName("body")).getText()
                .replaceAll("\\s+", " ")
                .trim();
        return text.length() > 500 ? text.substring(0, 500) + "..." : text;
    }
}
