package com.qa.homework.pages.login.locators;

import org.openqa.selenium.By;

public final class LoginLocators {

    public static final By USERNAME_INPUT = By.id("user-name");
    public static final By PASSWORD_INPUT = By.id("password");
    public static final By LOGIN_BUTTON = By.id("login-button");
    public static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    private LoginLocators() {
    }
}
