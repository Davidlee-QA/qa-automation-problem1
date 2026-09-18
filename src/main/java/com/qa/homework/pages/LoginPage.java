package com.qa.homework.pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public LoginPage clickLoginExpectingFailure() {
        click(loginButton);
        return this;
    }

    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return new InventoryPage();
    }

    public String getErrorMessage() {
        return text(errorMessage);
    }

    public boolean isLoaded() {
        return isDisplayed(loginButton);
    }
}
