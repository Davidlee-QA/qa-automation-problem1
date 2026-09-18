package com.qa.homework.pages.login.actions;

import com.qa.homework.pages.BasePage;
import com.qa.homework.pages.inventory.page.InventoryPage;

import static com.qa.homework.pages.login.locators.LoginLocators.ERROR_MESSAGE;
import static com.qa.homework.pages.login.locators.LoginLocators.LOGIN_BUTTON;
import static com.qa.homework.pages.login.locators.LoginLocators.PASSWORD_INPUT;
import static com.qa.homework.pages.login.locators.LoginLocators.USERNAME_INPUT;

public abstract class LoginActions<T extends LoginActions<T>> extends BasePage {

    public T enterUsername(String username) {
        type(USERNAME_INPUT, username);
        return self();
    }

    public T enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return self();
    }

    public T clickLoginExpectingFailure() {
        click(LOGIN_BUTTON);
        return self();
    }

    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(LOGIN_BUTTON);
        return new InventoryPage();
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }

    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }

    protected abstract T self();
}
