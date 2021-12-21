/*
 * Copyright 2020 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.addons;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.addons.interfaces.Action;
import io.testproject.sdk.internal.addons.interfaces.ElementAction;
import io.testproject.sdk.internal.rest.AgentClient;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.ParameterizedType;

/**
 * Helper class allowing to execute Addons.
 * @param <D> The driver type executing the actions.
 */
public class AddonsHelper<D extends WebDriver> extends GenericAddonsHelper {
    /**
     * Driver used in action execution.
     */
    private final D driver;

    /**
     * Initializes a new instance of the helper.
     *
     * @param agentClient Agent client to use for communicating with the Agent.
     * @param driver Driver.
     */
    public AddonsHelper(final D driver, final AgentClient agentClient) {
        super((ReportingDriver) driver, agentClient);
        this.driver = driver;
    }

    /**
     * Runs the action.
     * @param action Action to run.
     * @param <T> The driver type that this action supports.
     * @return True if hte action passed.
     */
    public <T extends WebDriver> boolean run(final Action<T> action) {
        validateAction(action.getClass());
        return action.run((T) driver);
    }

    /**
     * Runs the action.
     * @param action Action to run.
     * @param elementSearchCriteria Element search criteria.
     * @param <T> The driver type that this action supports.
     * @return True if hte action passed.
      */
    public <T extends WebDriver> boolean run(final ElementAction<T> action, final By elementSearchCriteria) {
        validateAction(action.getClass());
        return action.run((T) driver, elementSearchCriteria);
    }

    private void validateAction(final Class<?> actionClass) {
        ParameterizedType genericInterface = (ParameterizedType) actionClass.getGenericInterfaces()[0];
        String typeName = genericInterface.getActualTypeArguments()[0].getTypeName()
                .replaceAll("\\<.*\\>", "");
        try {
            if (!Class.forName(typeName).isAssignableFrom(driver.getClass())) {
                throw new ClassNotFoundException();
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidArgumentException(String.format(
                    "Driver %s cannot run this action because its driver type must inherit from %s",
                    driver.getClass().getName(), typeName), e);
        }
    }

    /**
     * Executes an Action using it's proxy.
     * Addons are tiny automation building blocks that have one or more actions.
     * Addon Proxy can be obtained from the Addons page.
     *
     * @see <a href="https://app.testproject.io/#/addons">TestProject Addons Page</a>
     * @param action Specific Action proxy.
     * @param by     Element locator in case the Action needs one.
     * @return Presumably modified class with updated output fields.
     */
    public ActionProxy execute(final ActionProxy action, final By by) {
        return execute(action, by, -1);
    }

    /**
     * Executes an Action using it's proxy.
     * <p>
     * Addons are tiny automation building blocks that have one or more actions.
     * Addon Proxy can be obtained from the Addons page.
     *
     * @see <a href="https://app.testproject.io/#/addons">TestProject Addons page</a>.
     * @param action  Specific Action proxy.
     * @param by      Element locator in case the Action needs one.
     * @param timeout maximum amount of time allowed to wait for action execution to complete.
     * @return Potentially modified class with updated output fields (if any).
     */
    public ActionProxy execute(final ActionProxy action, final By by, final int timeout) {
        // Set element locator
        action.getDescriptor().setBy(by);
        return execute(action, timeout);
    }

}
