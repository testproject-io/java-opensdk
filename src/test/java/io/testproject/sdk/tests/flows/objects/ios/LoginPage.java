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

import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.testproject.sdk.drivers.ios.IOSDriver;
import org.openqa.selenium.support.PageFactory;

public final class LoginPage {

    /**
     * Element object for the 'name' field.
     */
    @iOSXCUITFindBy(id = "name")
    private IOSElement nameElement;

    /**
     * Element object for the 'password' field.
     */
    @iOSXCUITFindBy(id = "password")
    private IOSElement passwordElement;

    /**
     * Element object for the 'login' button.
     */
    @iOSXCUITFindBy(id = "login")
    private IOSElement loginElement;

    /**
     * Creates a new instance and initializes the page objects.
     * @param driver driver to use for page objects initialization
     */
    public LoginPage(final IOSDriver driver) {
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

    /**
     * Perform a login sequence using provided name and password.
     * @param name name to type
     * @param password password to type
     */
    public void login(final String name, final String password) {
        typeName(name);
        typePassword(password);
        clickLogin();
    }
}
