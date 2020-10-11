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
import org.apache.commons.lang3.StringUtils;
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
 * TestNG inferrer for Project and Job names.
 */
public class TestNGInferrer implements ReportSettingsInferrer {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TestNGInferrer.class);

    /**
     * Stack traces list to analyze in order to infer Project and Job names.
     */
    private final List<StackTraceElement> traces;

    /**
     * Initializes a new inferrer for TestNG.
     *
     * @param traces Stack traces list to analyze.
     */
    public TestNGInferrer(final List<StackTraceElement> traces) {
        this.traces = traces;
    }

    /**
     * Infers Project and Job names from JUnit annotations.
     * <p>
     * SDK analyzes the call stack, searching for a <b>method</b> annotated with TestNG annotation.
     * Name of <b>package</b> for the class containing the method is used for <em>Project</em> name.
     * Name of <b>class</b> or the <em>description_</em> field from <em>@BeforeSuite</em> / <em>@BeforeClass</em>
     * annotations if found, are used for the <em>Job</em> name
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

            // Check if this method has any TestNG annotations
            if (Arrays.stream(method.get().getDeclaredAnnotations())
                    .anyMatch(a -> a.annotationType().getName()
                            .startsWith(FrameworksNames.TESTNG_PACKAGE_NAME_PREFIX))) {
                ReportSettings result = inspectAnnotations(clazz);
                // If TestNG @BeforeClass or @BeforeSuite annotations with description were found -> return it.
                // Otherwise -> return clazz package name and clazz simple name
                return Objects.requireNonNullElseGet(result, () ->
                        new ReportSettings(getPackageName(clazz), clazz.getSimpleName()));
            }
        }

        LOG.warn("Something is wrong... TestNG classes in stack but no annotated methods were found.");
        return getUnnamedEntries();
    }

    /**
     * Inspects @BeforeSuite or @BeforeClass annotations to determine Job name.
     *
     * @param clazz Class to search for methods with annotations.
     * @return Description value from any of the annotations found.
     */
    private ReportSettings inspectAnnotations(final Class<?> clazz) {
        // Check for @BeforeSuite annotation with non-empty description
        ReportSettings reportSettings = inspectAnnotation(clazz, TESTNG_BEFORE_SUITE_ANNOTATION);
        if (reportSettings != null) {
            return reportSettings;
        }

        // Check for @BeforeClass annotation with non-empty description
        return inspectAnnotation(clazz, TESTNG_BEFORE_CLASS_ANNOTATION);
    }

    /**
     * Inspects a specific annotation on methods to determine Job name.
     *
     * @param clazz          Class to search for methods with annotation.
     * @param annotationName Annotation name to search for.
     * @return Description value from the annotation (if found).
     */
    private ReportSettings inspectAnnotation(final Class<?> clazz, final String annotationName) {
        for (Method method : clazz.getDeclaredMethods()) {
            Optional<Annotation> annotation = Arrays.stream(method.getAnnotations())
                    .filter(a -> a.annotationType().getName().equals(annotationName)).findFirst();

            if (annotation.isEmpty()) {
                // Method doesn't have @BeforeClass or @BeforeSuite annotations
                continue;
            }

            try {
                Method descriptionMethod = annotation.get().annotationType()
                        .getDeclaredMethod(TESTNG_DESCRIPTION_VALUE);
                String description = descriptionMethod.invoke(annotation.get()).toString();

                if (!StringUtils.isEmpty(description)) {
                    return new ReportSettings(getPackageName(clazz), description);
                }

                // Description is empty
                return null;
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOG.error("Failed to infer JobName from annotation", e);
                return null;
            }
        }

        // No methods with @BeforeClass or @BeforeSuite annotations were found
        return null;
    }

    /**
     * Infers Test name using TestNG annotations.
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
                LOG.debug("Failed to create an instance of a class [{}]: {}",
                        stackTraceElement.getClassName(), e.getMessage());
                continue;
            }

            // Find the method
            Optional<Method> method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(m -> m.getName().equals(stackTraceElement.getMethodName())).findFirst();

            if (method.isEmpty()) {
                continue;
            }

            // Check if the method has any of the supported UnitTesting frameworks prefixes.
            boolean isTestNG = Arrays.stream(method.get().getDeclaredAnnotations())
                    .anyMatch(a -> a.annotationType().getName().startsWith(TESTNG_PACKAGE_NAME_PREFIX));

            // Continue to the next trace if this method has no TestNG annotation
            if (!isTestNG) {
                continue;
            }

            // Search for @Test annotation
            Optional<Annotation> testAnnotation =
                    Arrays.stream(method.get().getDeclaredAnnotations())
                            .filter(a -> a.annotationType().getName().equals(TESTNG_TEST_ANNOTATION))
                            .findFirst();

            // Annotation @Test is present
            if (testAnnotation.isPresent()) {
                try {

                    // Get testName method of the annotation
                    Method testNameMethod = testAnnotation.get().annotationType()
                            .getDeclaredMethod(TESTNG_TEST_NAME_VALUE);

                    // Get description method of the annotation
                    Method descriptionMethod = testAnnotation.get().annotationType()
                            .getDeclaredMethod(TESTNG_DESCRIPTION_VALUE);

                    // Invoke methods to get names and description values
                    String testName = testNameMethod.invoke(testAnnotation.get()).toString();
                    String description = descriptionMethod.invoke(testAnnotation.get()).toString();

                    // Set test name if not empty
                    StringBuilder result = new StringBuilder();
                    if (!StringUtils.isEmpty(testName)) {
                        result.append(testName);
                    }

                    // Append description if not empty
                    if (!StringUtils.isEmpty(description)) {

                        // Append hyphen to separate testName and description
                        if (result.length() != 0) {
                            result.append(" - ");
                        }

                        // Append description
                        result.append(description);
                    }

                    // If result is still empty - use method name
                    if (result.length() == 0) {
                        return method.get().getName();
                    }

                    // Return result
                    return result.toString();
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    LOG.error("Failed to infer Test name from @Test annotation", e);

                    // Return the name of the method that is annotated with @Test
                    return method.get().getName();
                }
            }

            // Search for other TestNG annotations
            Optional<Annotation> otherAnnotation =
                    Arrays.stream(method.get().getDeclaredAnnotations())
                            .filter(a -> a.annotationType().getName().startsWith(TESTNG_PACKAGE_NAME_PREFIX))
                            .findFirst();

            // No other TestNG annotations were found
            if (otherAnnotation.isEmpty()) {
                // Can't happen - got this far because there was a TestNG annotation on the method!
                LOG.warn("Something went wrong, method has TestNG annotations but none of them could be used.");
                return method.get().getName();
            }

            // Check if annotation got a 'description' method
            Optional<Method> descriptionMethod =
                    Arrays.stream(otherAnnotation.get().annotationType().getDeclaredMethods())
                            .filter(m -> m.getName().equals(TESTNG_DESCRIPTION_VALUE)).findFirst();

            // No 'description' method
            if (descriptionMethod.isEmpty()) {
                LOG.debug("Something went wrong... encountered a TestNG annotation without a description method.");
                return method.get().getName();
            }

            // Return the description field of a TestNG annotation
            try {
                String description = descriptionMethod.get().invoke(otherAnnotation.get()).toString();
                return !StringUtils.isEmpty(description) ? description : method.get().getName();
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.error("Failed to infer Test name from TestNG annotation description", e);
                return method.get().getName();
            }
        }

        // No JUnit annotations found or failed to create class from name: using first caller info
        return traces.get(traces.size() - 1).getMethodName();
    }
}
