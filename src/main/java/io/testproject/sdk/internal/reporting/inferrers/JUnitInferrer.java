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

package io.testproject.sdk.internal.reporting.inferrers;

import io.testproject.sdk.internal.reporting.FrameworksNames;
import io.testproject.sdk.internal.rest.ReportSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.testproject.sdk.internal.reporting.FrameworksNames.*;

/**
 * JUnit inferrer for Project and Job names.
 */
public class JUnitInferrer implements ReportSettingsInferrer {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JUnitInferrer.class);

    /**
     * Stack traces list to analyze in order to infer Project and Job names.
     */
    private final List<StackTraceElement> traces;

    /**
     * Initializes a new inferrer for JUnit.
     *
     * @param traces Stack traces list to analyze.
     */
    public JUnitInferrer(final List<StackTraceElement> traces) {
        this.traces = traces;
    }

    /**
     * Infers Project and Job names from JUnit annotations.
     * <p>
     * SDK analyzes the call stack, searching for a <b>method</b> annotated with JUnit annotation.
     * Name of <b>package</b> for the class containing the method is used for <em>Project</em> name.
     * Name of <b>class</b> or the <em>@DisplayName</em> annotation (JUnit 5 only) is used for the <em>Job</em> name.
     *
     * @return An instance of {@link ReportSettings} containing Project and Job names.
     */
    @Override
    public ReportSettings inferReportSettings() {
        // Iterate over stack trace trying to find JUnit annotations
        for (StackTraceElement stackTraceElement : this.traces) {
            Class<?> clazz;
            try {
                clazz = Class.forName(stackTraceElement.getClassName());
            } catch (ClassNotFoundException e) {
                LOG.error("Failed to create an instance of a class", e);
                return getUnnamedEntries();
            }

            // Find the method
            Optional<Method> method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(stackTraceElement.getMethodName())).findFirst();

            if (method.isEmpty()) {
                continue;
            }

            // Check if this method has any JUnit annotations
            if (Arrays.stream(method.get().getDeclaredAnnotations())
                    .anyMatch(a -> a.annotationType().getName()
                            .startsWith(FrameworksNames.JUNIT_PACKAGE_NAME_PREFIX))) {
                ReportSettings result = inspectDisplayName(clazz);
                // If JUnit's DisplayName annotation was found -> return it.
                // Otherwise -> return clazz package name and clazz simple name
                return Objects.requireNonNullElseGet(result, () ->
                        new ReportSettings(clazz.getPackageName(), clazz.getSimpleName()));
            }
        }

        LOG.warn("Something is wrong... JUnit classes in stack but no annotated methods were found.");
        return getUnnamedEntries();
    }

    /**
     * Searches for a JUnit5 'DisplayName' annotation and extracts the value.
     *
     * @param clazz Class to search for the annotation on.
     * @return Value specified in the annotation.
     */
    private ReportSettings inspectDisplayName(final Class<?> clazz) {
        Optional<Annotation> annotation = Arrays.stream(clazz.getAnnotations())
                .filter(a -> a.annotationType().getName().equals(JUNIT5_DISPLAY_NAME_ANNOTATION)).findFirst();

        if (annotation.isEmpty()) {
            return null;
        }

        try {
            Method valueMethod = annotation.get().annotationType().getDeclaredMethod(JUNIT5_DISPLAY_NAME_VALUE);
            return new ReportSettings(
                    clazz.getPackageName(),
                    valueMethod.invoke(annotation.get()).toString());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error("Failed to infer JobName from DisplayName annotation", e);
            return null;
        }
    }

    /**
     * Infers Test name using Junit annotations.
     *
     * @return A name of the Test.
     */
    @Override
    public String inferTestName() {
        for (StackTraceElement stackTraceElement : traces) {
            Class<?> clazz;
            try {
                clazz = Class.forName(stackTraceElement.getClassName());
            } catch (ClassNotFoundException e) {
                LOG.error("Failed to create an instance of a class", e);

                // Return a name of the first method in call stack.
                return traces.get(traces.size() - 1).getMethodName();
            }

            // Find the method
            Optional<Method> method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(stackTraceElement.getMethodName())).findFirst();

            if (method.isEmpty()) {
                continue;
            }

            // Check if the method has any of the supported UnitTesting frameworks prefixes.
            boolean annotated = Arrays.stream(method.get().getDeclaredAnnotations())
                    .anyMatch(a -> a.annotationType().getName().startsWith(JUNIT_PACKAGE_NAME_PREFIX));

            // Dynamic test
            if (stackTraceElement.getClassName().equals(JUNIT5_DYNAMIC_TEST_DESCRIPTOR)) {
                return "Dynamic Test";
            }

            // Continue to the next trace if this method has no JUnit annotation
            if (!annotated) {
                continue;
            }

            // Search for @DisplayName annotation
            Optional<Annotation> displayNameAnnotation =
                    Arrays.stream(method.get().getDeclaredAnnotations())
                            .filter(a -> a.annotationType().getName().equals(JUNIT5_DISPLAY_NAME_ANNOTATION))
                            .findFirst();

            // Annotation @DisplayName is present
            if (displayNameAnnotation.isPresent()) {
                try {
                    Method valueMethod = displayNameAnnotation.get().annotationType()
                            .getDeclaredMethod(JUNIT5_DISPLAY_NAME_VALUE);
                    return valueMethod.invoke(displayNameAnnotation.get()).toString();
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    LOG.error("Failed to infer Test name from DisplayName annotation", e);
                    return method.get().getName();
                }
            }

            // No @DisplayName found
            return method.get().getName();
        }

        // No JUnit annotations found: using first caller info
        return traces.get(traces.size() - 1).getMethodName();
    }
}
