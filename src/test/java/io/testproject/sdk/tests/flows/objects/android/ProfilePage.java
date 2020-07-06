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

package io.testproject.sdk.tests.flows.objects.android;


import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.testproject.sdk.drivers.android.AndroidDriver;
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
    private final AndroidDriver<MobileElement> driver;

    /**
     * Element object for the software keyboard.
     */
    @AndroidFindBy(className = "UIAKeyboard")
    private AndroidElement keyboard;

    /**
     * Element object for the 'name' label.
     */
    @AndroidFindBy(id = "greetings")
    private AndroidElement greetingsElement;

    /**
     * Element object for the 'logout' button.
     */
    @AndroidFindBy(id = "logout")
    private AndroidElement logoutElement;

    /**
     * Element object for the 'country' drop-down.
     */
    @AndroidFindBy(id = "country")
    private AndroidElement countryElement;

    /**
     * Element object for the 'address' field.
     */
    @AndroidFindBy(id = "address")
    private AndroidElement addressElement;

    /**
     * Element object for the 'email' field.
     */
    @AndroidFindBy(id = "email")
    private AndroidElement emailElement;

    /**
     * Element object for the 'phone' field.
     */
    @AndroidFindBy(id = "phone")
    private AndroidElement phoneElement;

    /**
     * Element object for the 'save' button.
     */
    @AndroidFindBy(id = "save")
    private AndroidElement saveElement;

    /**
     * Element object for the 'saved' label.
     */
    @AndroidFindBy(id = "saved")
    private AndroidElement savedElement;

    /**
     * Creates a new instance and initializes the page objects.
     * @param driver driver to use for page objects initialization
     */
    public ProfilePage(final AndroidDriver<MobileElement> driver) {
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

    private void hideKeyboardIfVisible() {
        if (keyboard != null) {
            driver.pressKey(new KeyEvent(AndroidKey.ESCAPE));
        }
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
        hideKeyboardIfVisible();
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
