/*
 * Copyright (c) 2021 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.sdk.tests.examples.frameworks.cucumber.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.testproject.sdk.drivers.web.ChromeDriver;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

/**
 * Step definitions for login_scenario.feature.
 */
public class StepDefinitions {

    /**
     * Driver used in this web test.
     */
    private ChromeDriver driver;

    /**
     * Constructs the TestProject driver and navigates to the TestProject
     * example page.
     *
     * @throws Exception if driver initialization fails.
     */
    @Given("I navigate to the TestProject example page")
    public void navigateToPage() throws Exception {
        driver = new ChromeDriver(new ChromeOptions(), "Cucumber", "Login Scenario");
        final int timeout = 1500;
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        driver.navigate().to("https://example.testproject.io/web/");
    }

    /**
     * Performs a login in the TestProject example page.
     */
    @When("I perform a login")
    public void performLogin() {
        driver.findElement(By.cssSelector("#name")).sendKeys("John Smith");
        driver.findElement(By.cssSelector("#password")).sendKeys("12345");
        driver.findElement(By.cssSelector("#login")).click();    }

    /**
     * Validates if the login was successful by checking if the login button appears.
      */
    @Then("I should see a logout button")
    public void validateLogoutButton() {
        Assertions.assertTrue(driver.findElement(By.cssSelector("#logout")).isDisplayed());
    }

    /**
     * Quits the driver once and closes the session.
     */
    @And("I should close the browser")
    public void closeBrowser() {
        driver.quit();
    }
}
