/*
 * Copyright 2002-2024 by Sannon Gualda de Aragão.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiperboot.db.repository.hiperboot;

import static com.hiperboot.util.HBUtils.addPageToFilter;
import static com.hiperboot.util.HBUtils.between;
import static com.hiperboot.util.HBUtils.getPageWithStartSizeSort;
import static com.hiperboot.util.HBUtils.greaterThan;
import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static com.hiperboot.util.HBUtils.smallerThan;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.ParentTable;

import lombok.extern.log4j.Log4j2;

@Log4j2
class ParentTableHiperBootRepositoryTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableHiperBootRepository;

    private final List<String> columnsGreaterThanIncompatible = List.of("colBoolean", "colStatusEnum", "someTable", "children", "colUUID");
    private final List<String> columnsInIncompatible = List.of("colBoolean", "someTable", "children");
    private final List<String> columnsThatAreEntities = List.of("someTable", "children");
    private final List<String> columnsToBeCaseInsensitive = List.of("colString", "colUUID");

    @Test
    void getByFilter_Null() {

        // Act
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, null);

        // Assert
        assertThat(results)
                .as("Check if the results are not empty when filtering by null equal to have rows")
                .isNotEmpty();
    }

    @Test
    void getByFilter_ShouldReturnResultsWithMatchingColStringCaseInsensitive() {
        // Arrange
        String expectedValue = "RandomString1";
        String filterColumn = "colString";

        // Act
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals(filterColumn, expectedValue));

        // Assert
        assertThat(results)
                .as("Check if the results are not empty when filtering by " + filterColumn + " equal to " + expectedValue)
                .isNotEmpty()
                .as("Verify that each result's " + filterColumn + " matches the expected value (case insensitive)")
                .allMatch(result -> expectedValue.equalsIgnoreCase(result.getColString()));
    }

    @Test
    void caseInsensitiveEqualsFilterStringColumnTest() {
        String expectedValue = "abc";
        String filterColumn = "colString";

        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals(filterColumn, expectedValue));
        assertThat(results).hasSize(2);

        results.forEach(row ->
                assertThat(row.getColString()).as("Check if " + filterColumn + " matches the expected value (case insensitive)")
                        .isEqualToIgnoringCase(expectedValue)
        );
    }

    @Test
    void equalsFilterAllRowsAndAllColumnsTest_CaseInsensitive() {
        List<ParentTable> rowsToTest = parentTableHiperBootRepository.findAll();

        for (ParentTable testRow : rowsToTest) {
            var fieldMaps = getFieldList(ParentTable.class);
            for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
                String columnName = entry.getKey();
                Field field = entry.getValue();

                if (columnsThatAreEntities.contains(columnName)) {
                    continue;
                }

                var val = getFieldValue(testRow, field);
                if (isNull(val)) {
                    continue;
                }
                List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                        hbEquals(columnName, val.toString()));
                assertThat(results).isNotEmpty();

                results.forEach(row -> {
                    var valResult = getFieldValue(row, field);
                    assertThat(valResult.toString()).isEqualToIgnoringCase(val.toString());
                });
            }
        }
    }

    @Test
    void randomNotEqualsFilterAllColumnsTest_CaseInsensitive() {
        List<ParentTable> rowsToTest = parentTableHiperBootRepository.findAll();

        for (ParentTable testRow : rowsToTest) {
            var fieldMaps = getFieldList(ParentTable.class);
            for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
                String columnName = entry.getKey();
                Field field = entry.getValue();

                if (columnsThatAreEntities.contains(columnName)) {
                    continue;
                }

                var val = getFieldValue(testRow, field);
                if (isNull(val)) {
                    continue;
                }

                List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                        hbNot(hbEquals(columnName, val.toString())));
                assertThat(results).isNotEmpty();

                results.forEach(row -> {
                    var valResult = getFieldValue(row, field);
                    assertThat(valResult.toString()).isNotEqualToIgnoringCase(val.toString());
                });
            }
        }
    }

    @Test
    void randomInFilterAllColumnsTest_CaseInsensitive() {
        ParentTable randomRow1 = getRandomRow();
        ParentTable randomRow2 = getRandomRow();

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();

            if (columnsInIncompatible.contains(columnName)) {
                continue;
            }

            var val1 = getFieldValue(randomRow1, field);
            var val2 = getFieldValue(randomRow2, field);

            if (isNull(val1) || isNull(val2)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                    hbEquals(columnName, val1.toString(), val2.toString()));
            assertThat(results).isNotEmpty();

            results.forEach(row -> {
                var valResult = getFieldValue(row, field);
                if (columnsToBeCaseInsensitive.contains(columnName)) {
                    assertThat(valResult.toString().toUpperCase()).isIn(val1.toString().toUpperCase(), val2.toString().toUpperCase());
                }
                else {
                    assertThat(valResult).isIn(val1, val2);
                }
            });
        }
    }

    @Test
    void randomInCaseInsensitiveFilterStringTest() {
        String[] expectedValues = { "abc", "xxx" };
        String filterColumn = "colString";

        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals(filterColumn, expectedValues));
        assertThat(results).hasSize(2);

        results.forEach(row -> {
            String actualValue = row.getColString().toLowerCase();
            List<String> lowerCaseExpectedValues = Arrays.stream(expectedValues)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            assertThat(lowerCaseExpectedValues).as("Check if " + filterColumn + " matches one of the expected values (case insensitive)")
                    .contains(actualValue);
        });
    }

    @Test
    void randomNotInFilterAllColumnsTest_CaseInsensitive() {
        ParentTable randomRow1 = getRandomRow();
        ParentTable randomRow2 = getRandomRow();

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();

            if (columnsInIncompatible.contains(columnName)) {
                continue;
            }

            var val1 = getFieldValue(randomRow1, field);
            var val2 = getFieldValue(randomRow2, field);
            if (isNull(val1) || isNull(val2)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                    hbNot(hbEquals(columnName, val1.toString(), val2.toString())));
            assertThat(results).isNotEmpty();

            results.forEach(row -> {
                var valResult = getFieldValue(row, field);
                List<String> expectedValues = Arrays.asList(val1.toString().toLowerCase(), val2.toString().toLowerCase());
                assertThat(valResult.toString().toLowerCase()).isNotIn(expectedValues);
            });
        }
    }

    @Test
    void randomGreaterThanFilterAllColumnsTest_CaseInsensitive() {
        ParentTable randomRow = getRandomRow();

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }

            Field field = entry.getValue();
            var valFrom = getFieldValue(randomRow, field);
            if (isNull(valFrom)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                    greaterThan(columnName, valFrom.toString()));
            assertThat(results).isNotEmpty();

            results.forEach(row -> {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat(valResult.toString().toLowerCase()).isGreaterThanOrEqualTo(valFrom.toString().toLowerCase());
                }
                else {
                    assertThat((Comparable) valResult).isGreaterThanOrEqualTo((Comparable) valFrom);
                }
            });
        }
    }

    @Test
    void randomSmallerThanFilterAllColumnsTest_CaseInsensitive() {
        ParentTable randomRow = getRandomRow();

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }

            Field field = entry.getValue();
            var valFrom = getFieldValue(randomRow, field);
            if (isNull(valFrom)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                    smallerThan(columnName, valFrom.toString()));
            assertThat(results).isNotEmpty();

            results.forEach(row -> {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat(valResult.toString().toLowerCase()).isLessThanOrEqualTo(valFrom.toString().toLowerCase());
                }
                else {
                    assertThat((Comparable) valResult).isLessThanOrEqualTo((Comparable) valFrom);
                }
            });
        }
    }

    @Test
    void randomBetweenFilterAllColumnsTest_CaseInsensitive() {
        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }

            Field field = entry.getValue();
            var filter = addPageToFilter(null, getPageWithStartSizeSort(null, null, columnName));
            Page<ParentTable> pageResults = parentTableHiperBootRepository.hiperBootPageFilter(ParentTable.class, filter);

            assertThat(pageResults).isNotEmpty();
            var valFrom = getFieldValue(pageResults.getContent().get(1), field);
            var valTo = getFieldValue(pageResults.getContent().get(pageResults.getContent().size() - 2), field);
            if (isNull(valFrom) || isNull(valTo)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                    between(columnName, valFrom.toString(), valTo.toString()));
            assertThat(results).isNotEmpty();

            results.forEach(row -> {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat(valResult.toString().toLowerCase()).isBetween(valFrom.toString().toLowerCase(),
                            valTo.toString().toLowerCase());
                }
                else {
                    assertThat((Comparable) valResult).isBetween((Comparable) valFrom, (Comparable) valTo);
                }
            });
        }
    }

    @Test
    void randomLikeFilterAllColumnsTest_CaseInsensitive() {
        String filterValue = "%Ab%";
        String filterColumn = "colString";

        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, filterValue));
        assertThat(results).hasSize(2);

        results.forEach(row ->
                assertThat(row.getColString().toLowerCase()).contains(filterValue.replace("%", "").toLowerCase())
        );
    }

    @Test
    void randomNullFilterAllColumnsTest() {
        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnName.equals("id") || columnsThatAreEntities.contains(columnName)) {
                continue;
            }

            List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, hbIsNull(columnName));
            assertThat(results).hasSize(1);

            results.forEach(row -> {
                Field field = entry.getValue();
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(row);
                    assertThat(fieldValue).isNull();
                }
                catch (IllegalAccessException e) {
                    fail("Unable to access field value for column: " + columnName);
                }
            });
        }
    }

    @Test
    void manyToOneTest_CaseInsensitive() {
        String expectedValue = "Name C";
        String filterColumn = "someTable.name";

        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, "NAme c"));
        assertThat(results).hasSize(1);

        results.forEach(row ->
                assertThat(row.getSomeTable().getName()).isEqualToIgnoringCase(expectedValue)
        );
    }

    @Test
    void oneToManyTest() {
        String expectedValue = "30";
        String filterColumn = "children.number";

        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals(filterColumn, expectedValue));
        assertThat(results).hasSize(1);

        results.forEach(parentTable ->
                parentTable.getChildren().forEach(child ->
                        assertThat(child.getNumber().toString()).isEqualToIgnoringCase(expectedValue)
                )
        );
    }

    protected ParentTable getRandomRow() {
        List<ParentTable> results = parentTableHiperBootRepository.findAll();
        assertThat(results).isNotEmpty();

        Random random = new Random();
        int randomInt = random.nextInt(results.size() - 1);
        return results.get(randomInt);
    }
}