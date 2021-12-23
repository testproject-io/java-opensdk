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

package io.testproject.sdk.tests.examples.addons.actions;

import io.testproject.sdk.internal.addons.Platform;
import io.testproject.sdk.internal.addons.annotations.AddonAction;
import io.testproject.sdk.internal.addons.interfaces.Action;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;

/**
 * Clears fields in a web application.
 * This action can work for all browsers and must therefore use selenium's WebDriver as its driver type.
 * To prevent it from being available for mobile, we add the AddonAction annotation.
 */
@AddonAction(platforms = Platform.Web, name = "Clear Fields")
public final class ClearFieldsWeb implements Action<WebDriver> {
    @Override
    public boolean run(final WebDriver driver) {
        // Search for Form elements
        for (WebElement form : driver.findElements(By.tagName("form"))) {

            // Ignore invisible forms
            if (!form.isDisplayed()) {
                continue;
            }

            // Clear all inputs
            for (WebElement element : form.findElements(By.tagName("input"))) {
                element.clear();
            }
        }

        return true;
    }
}
