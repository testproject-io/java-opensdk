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

package io.testproject.sdk.internal.reporting;

public final class FrameworksNames {
    /**
     * A prefix of JUnit 4/5 annotation classes.
     */
    public static final String JUNIT_PACKAGE_NAME_PREFIX = "org.junit";

    /**
     * A prefix of JUnit 4/5 annotation classes.
     */
    public static final String TESTNG_PACKAGE_NAME_PREFIX = "org.testng";

    /**
     * JUnit 4 @Test Annotation.
     */
    public static final String JUNIT4_TEST_ANNOTATION = "org.junit.Test";

    /**
     * JUnit 5 @Test Annotation.
     */
    public static final String JUNIT5_TEST_ANNOTATION = "org.junit.jupiter.api.Test";

    /**
     * JUnit 5 Dynamic Test Descriptor class name.
     */
    public static final String JUNIT5_DYNAMIC_TEST_DESCRIPTOR
            = "org.junit.jupiter.engine.descriptor.DynamicTestTestDescriptor";

    /**
     * TestNG @Test Annotation.
     */
    public static final String TESTNG_TEST_ANNOTATION = "org.testng.annotations.Test";

    /**
     * TestNG @BeforeSuite Annotation.
     */
    public static final String TESTNG_BEFORE_SUITE_ANNOTATION = "org.testng.annotations.BeforeSuite";

    /**
     * TestNG @BeforeClass Annotation.
     */
    public static final String TESTNG_BEFORE_CLASS_ANNOTATION = "org.testng.annotations.BeforeClass";

    /**
     * JUnit5 @DisplayName annotation class name.
     */
    public static final String JUNIT5_DISPLAY_NAME_ANNOTATION = "org.junit.jupiter.api.DisplayName";

    /**
     * JUnit5 @DisplayName annotation value field name.
     */
    public static final String JUNIT5_DISPLAY_NAME_VALUE = "value";

    /**
     * TestNG @Test annotation testName field name.
     */
    public static final String TESTNG_TEST_NAME_VALUE = "testName";

    /**
     * TestNG @BeforeSuite / @BeforeClass annotation description field name.
     */
    public static final String TESTNG_DESCRIPTION_VALUE = "description";

    /**
     * Private default constructor to prevent instance initialization of this utility class.
     */
    private FrameworksNames() {
    }
}
