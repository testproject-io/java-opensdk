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
import io.testproject.sdk.internal.addons.annotations.Parameter;
import io.testproject.sdk.internal.addons.interfaces.ElementAction;
import org.openqa.selenium.*;

import java.util.Optional;

/**
 * Action that types test and presses the Enter key afterwards.
 */
@AddonAction(
        platforms = {Platform.Android, Platform.iOS, Platform.Web},
        name = "Type & press Enter",
        summary = "Types text & presses Enter",
        description = "Type {{text}} and press Enter")
public final class TypeWithEnterAction implements ElementAction<WebDriver> {
    /**
     * Text to type.
     */
    @Parameter(description = "Text to type.")
    private String text;

    /**
     * Sets the text parameter. Used for testing the action only.
     * @param text Text value.
     */
    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public boolean run(final WebDriver driver, final By elementSearchCriteria) {
        text = Optional.of(text).orElse("");
        WebElement element = driver.findElement(elementSearchCriteria);
        element.click();
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
        return true;
    }
}
