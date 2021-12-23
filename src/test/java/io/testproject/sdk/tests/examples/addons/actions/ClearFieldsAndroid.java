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

import io.testproject.sdk.drivers.android.AndroidDriver;
import io.testproject.sdk.internal.addons.interfaces.Action;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Clears all edit text boxes in android.
 * This action can run on android only and therefore supports only AndroidDriver.
 * However, it doesn't require a specific annotation since the driver is specific enough.
 */
public final class ClearFieldsAndroid implements Action<AndroidDriver<WebElement>> {
    @Override
    public boolean run(final AndroidDriver<WebElement> driver) {
        for (WebElement element : driver.findElements(By.className("android.widget.EditText"))) {
            element.clear();
        }

        return true;
    }
}
