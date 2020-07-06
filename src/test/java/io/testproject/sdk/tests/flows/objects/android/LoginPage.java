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

public final class LoginPage {

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
     * Element object for the 'name' field.
     */
    @AndroidFindBy(id = "name")
    private AndroidElement nameElement;

    /**
     * Element object for the 'password' field.
     */
    @AndroidFindBy(id = "password")
    private AndroidElement passwordElement;

    /**
     * Element object for the 'login' button.
     */
    @AndroidFindBy(id = "login")
    private AndroidElement loginElement;

    /**
     * Creates a new instance and initializes the page objects.
     * @param driver driver to use for page objects initialization
     */
    public LoginPage(final AndroidDriver<MobileElement> driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    private void typeName(final String name) {
        nameElement.sendKeys(name);
    }

    private void typePassword(final String password) {
        passwordElement.sendKeys(password);
    }

    private void clickLogin() {
        loginElement.click();
    }

    private void hideKeyboardIfVisible() {
        if (keyboard != null) {
            driver.pressKey(new KeyEvent(AndroidKey.ESCAPE));
        }
    }

    /**
     * Perform a login sequence using provided name and password.
     * @param name name to type
     * @param password password to type
     */
    public void login(final String name, final String password) {
        hideKeyboardIfVisible();
        typeName(name);
        typePassword(password);
        clickLogin();
    }
}
