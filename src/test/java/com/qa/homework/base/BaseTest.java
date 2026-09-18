package com.qa.homework.base;

import com.qa.homework.config.Config;
import com.qa.homework.driver.DriverFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@Listeners(AllureTestNg.class)
public abstract class BaseTest {

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        String actualBrowser = DriverFactory.resolveBrowser(browser);
        Allure.parameter("browser", actualBrowser);
        DriverFactory.initDriver(actualBrowser);
        DriverFactory.getDriver().get(Config.baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
