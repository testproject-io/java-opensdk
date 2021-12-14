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

package io.testproject.sdk.tests.flows.objects.ios;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.testproject.sdk.drivers.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public final class ProfilePage {

    /**
     * Timeout to use when initializing page elements.
     */
    private static final int TIMEOUT = 10;

    /**
     * Driver to use for AUT automation.
     */
    private final IOSDriver driver;

    /**
     * Element object for the 'name' label.
     */
    @iOSXCUITFindBy(id = "greetings")
    private WebElement greetingsElement;

    /**
     * Element object for the 'logout' button.
     */
    @iOSXCUITFindBy(id = "logout")
    private WebElement logoutElement;

    /**
     * Element object for the 'country' drop-down.
     */
    @iOSXCUITFindBy(id = "country")
    private WebElement countryElement;

    /**
     * Element object for the 'address' field.
     */
    @iOSXCUITFindBy(id = "address")
    private WebElement addressElement;

    /**
     * Element object for the 'email' field.
     */
    @iOSXCUITFindBy(id = "email")
    private WebElement emailElement;

    /**
     * Element object for the 'phone' field.
     */
    @iOSXCUITFindBy(id = "phone")
    private WebElement phoneElement;

    /**
     * Element object for the 'save' button.
     */
    @iOSXCUITFindBy(id = "save")
    private WebElement saveElement;

    /**
     * Element object for the 'saved' label.
     */
    @iOSXCUITFindBy(id = "saved")
    private WebElement savedElement;

    /**
     * Creates a new instance and initializes the page objects.
     * @param driver driver to use for page objects initialization
     */
    public ProfilePage(final IOSDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(TIMEOUT)), this);
    }

    private void typeCountry(final String country) {
        countryElement.sendKeys(country);

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

    private void hideKeyboard() {
        driver.hideKeyboard();
    }

    private void save() {
        saveElement.click();
    }

    /**
     * Fills in all profile fields and saves it.
     * @param country Country to choose
     * @param address Address to type
     * @param email eMail to type
     * @param phone phone number to type
     */
    public void updateProfile(final String country, final String address, final String email, final String phone) {
        typeCountry(country);
        typeAddress(address);
        typeEmail(email);
        typePhone(phone);
        hideKeyboard();
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
