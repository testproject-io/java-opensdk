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

package io.testproject.sdk.interfaces.parameterization;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to provide arguments from TestProject platform to parameterized tests.
 */
public class TestProjectParameterizer implements ArgumentsProvider {
    /**
     * Environment variable for data provider contents.
     */
    private static final String DATA_PROVIDER_ENV = "TP_TEST_DATA_PROVIDER";

    /**
     * Provides arguments for TestNG tests.
     * @return Test arguments.
     * @throws Exception If unable to read arguments
     */
    @DataProvider(name = "TestProject")
    public static Object[][] provideArguments() throws Exception {
        List<String[]> strings = getCSVLines();
        return strings.toArray(new Object[0][]);
    }

    /**
     * Provides arguments for JUnit 5 tests.
     * @param context Extension context.
     * @return Test arguments.
     * @throws Exception If unable to read arguments
     */
    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
        List<String[]> strings = getCSVLines();
        return strings.stream().map(Arguments::of);
    }

    private static List<String[]> getCSVLines() throws Exception {
        String dataProviderPath = System.getenv(DATA_PROVIDER_ENV);
        if (StringUtils.isEmpty(dataProviderPath)) {
            throw new IllegalArgumentException("No data provider was specified. "
                    + "Make sure this annotation is used for uploaded tests only.");
        }
        File dataProviderFile = new File(dataProviderPath);
        if (!dataProviderFile.exists()) {
            throw new IllegalArgumentException("No data provider was specified. "
                    + "Make sure this annotation is used for uploaded tests only.");
        }

        // Read CSV & collect the contents
        CSVParser parser = CSVParser.parse(dataProviderFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
        return parser.getRecords().stream().skip(1).map(record -> {
            Iterator<String> iterator = record.iterator();
            List<String> recordValues = new ArrayList<>();
            iterator.forEachRemaining(recordValues::add);
            return recordValues.toArray(new String[0]);
        }).collect(Collectors.toList());
    }
}
