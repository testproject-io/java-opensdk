/*
 * Copyright (c) 2020 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.tests.flows.objects.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class ProfilePage {

    /**
     * Driver timeout in seconds.
     */
    private static final int TIMEOUT = 60;

    /**
     * Web driver to be used.
     */
    private final WebDriver driver;

    /**
     * Element object for the 'logout' button.
     */
    @FindBy(css = "#logout")
    private WebElement logoutElement;

    /**
     * Element object for the 'country' drop-down.
     */
    @FindBy(css = "#country")
    private WebElement countryElement;

    /**
     * Element object for the 'address' field.
     */
    @FindBy(css = "#address")
    private WebElement addressElement;

    /**
     * Element object for the 'email' field.
     */
    @FindBy(css = "#email")
    private WebElement emailElement;

    /**
     * Element object for the 'phone' field.
     */
    @FindBy(css = "#phone")
    private WebElement phoneElement;

    /**
     * Element object for the 'save' button.
     */
    @FindBy(css = "#save")
    private WebElement saveElement;

    /**
     * Element object for the 'saved' label.
     */
    @FindBy(css = "#saved")
    private WebElement savedElement;

    private void selectCountry(final String country) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.elementToBeClickable(countryElement));

        Select countrySelect = new Select(countryElement);
        countrySelect.selectByVisibleText(country);
    }

    private void typeAddress(final String address) {
        addressElement.sendKeys(address);
    }

    private void typeEmail(final String email) {
        emailElement.sendKeys(email);
    }

    private void typePhone(final String phone) {
        phoneElement.sendKeys(phone);
    }

    private void save() {
        saveElement.click();
    }

    /**
     * Default constructor.
     * @param driver Driver instance.
     */
    public ProfilePage(final WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Fills in all profile fields and saves it.
     * @param country Country to choose
     * @param address Address to type
     * @param email eMail to type
     * @param phone phone number to type
     */
    public void updateProfile(final String country, final String address, final String email, final String phone) {
        selectCountry(country);
        typeAddress(address);
        typeEmail(email);
        typePhone(phone);
        save();
    }

    /**
     * Checks if the 'saved' label is present and visible.
     * @return True if it's present and visible, otherwise False
     */
    public boolean isSaved() {
        return savedElement.isDisplayed();
    }
}
