package com.qa.homework.pages.login;

import com.qa.homework.data.SauceDemoTestData.UserCredentials;
import com.qa.homework.pages.inventory.page.InventoryPage;

import java.time.Duration;

public final class LoginPage {

    private LoginPage() {
    }

    public static InventoryPage openInventory(UserCredentials user) {
        return new com.qa.homework.pages.login.page.LoginPage()
                .loginAs(user.username(), user.password());
    }

    public static com.qa.homework.pages.login.page.LoginPage failLogin(
            String username,
            String password) {

        return new com.qa.homework.pages.login.page.LoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingFailure();
    }

    public static long loginDurationSeconds(UserCredentials user) {
        long start = System.nanoTime();
        openInventory(user);
        return Duration.ofNanos(System.nanoTime() - start).toSeconds();
    }
}
