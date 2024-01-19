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
package com.hiperboot.filters;

import static com.hiperboot.util.HBUtils.addPageToFilter;
import static com.hiperboot.util.HBUtils.between;
import static com.hiperboot.util.HBUtils.getPageWithStartSizeSort;
import static com.hiperboot.util.HBUtils.greaterThan;
import static com.hiperboot.util.HBUtils.hbAnd;
import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static com.hiperboot.util.HBUtils.smallerThan;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.entity.book.Book;
import com.hiperboot.db.repository.BookHiperBootRepository;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
class HiperBootRepositoryColumnsTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository level01Repository;

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    private List<String> columnsGreaterThanIncompatible = List.of("colBoolean", "colStatusEnum", "someTable", "children");
    private List<String> columnsInIncompatible = List.of("colBoolean", "someTable", "children");
    private List<String> columnsToIgnore = List.of("someTable", "children");

    private List<String> columsToBeCaseInsensitive = List.of("colString", "colUUID");

    @Test
    void testGetByFilter() {
        Map<String, Object> filter = Collections.emptyMap();
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, Map.of("colString", "RandomString1"));
        assertThat(results).isNotEmpty();
    }

    @Test
    void caseInsensitiveEqualsFilterStringColumnTest() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals("colString", "abc"));
        assertThat(results).hasSize(2);

        for (ParentTable row : results) {
            var valResult = row.getColString();
            assertThat(valResult.toUpperCase()).isEqualTo("ABC");
        }
    }

    @Test
    void randomEqualsFilterAllColumnsTest() {
        ParentTable randomRow = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            Field field = entry.getValue();
            String columnName = entry.getKey();
            if (columnsToIgnore.contains(columnName) || columnName.equals("colString")) {
                continue;
            }
            var val = getFieldValue(randomRow, field);
            results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals(columnName, val.toString()));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                assertThat(valResult).isEqualTo(val);
            }
        }
    }

    @Test
    void randomNotEqualsFilterAllColumnsTest() {
        ParentTable randomRow = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            Field field = entry.getValue();
            String columnName = entry.getKey();
            if (columnsToIgnore.contains(columnName)) {
                continue;
            }

            var val = getFieldValue(randomRow, field);
            results = level01Repository.hiperBootFilter(ParentTable.class, hbNot(hbEquals(columnName, val.toString())));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                assertThat(valResult).isNotEqualTo(val);
            }
        }
    }

    @Test
    void randomInFilterAllColumnsTest() {
        ParentTable randomRow1 = getRandomRow();
        ParentTable randomRow2 = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            Field field = entry.getValue();
            String columnName = entry.getKey();
            if (columnsInIncompatible.contains(columnName)) {
                continue;
            }

            var val1 = getFieldValue(randomRow1, field);
            var val2 = getFieldValue(randomRow2, field);

            results = level01Repository.hiperBootFilter(ParentTable.class,
                    hbEquals(columnName, val1.toString(), val2.toString()));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                if (columsToBeCaseInsensitive.contains(columnName)) {
                    assertThat(valResult.toString().toUpperCase()).isIn(val1.toString().toUpperCase(), val2.toString().toUpperCase());
                }
                else {
                    assertThat(valResult).isIn(val1, val2);
                }

            }
        }
    }

    @Test
    void randomInCaseInsensitiveFilterStringTest() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals("colString", "abc", "xxx"));
        assertThat(results).hasSize(2);
    }

    @Test
    void randomNotInFilterAllColumnsTest() {
        ParentTable randomRow1 = getRandomRow();
        ParentTable randomRow2 = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            Field field = entry.getValue();
            String columnName = entry.getKey();

            if (columnsInIncompatible.contains(columnName)) {
                continue;
            }
            var val1 = getFieldValue(randomRow1, field).toString();
            var val2 = getFieldValue(randomRow2, field).toString();
            results = level01Repository.hiperBootFilter(ParentTable.class, hbNot(hbEquals(columnName, val1, val2)));

            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                assertThat(valResult).isNotIn(val1.toUpperCase(), val2.toUpperCase());
            }
        }
    }

    @Test
    void randomGreaterThanFilterAllColumnsTest() {
        ParentTable randomRow = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }

            Field field = entry.getValue();
            var valFrom = getFieldValue(randomRow, field);
            results = level01Repository.hiperBootFilter(ParentTable.class, greaterThan(columnName, valFrom.toString()));

            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat(((Comparable) valResult.toString().toUpperCase()).compareTo(
                            valFrom.toString().toUpperCase())).isGreaterThanOrEqualTo(0);
                }
                else {
                    assertThat(((Comparable) valResult).compareTo(valFrom)).isGreaterThanOrEqualTo(0);
                }
            }
        }
    }

    @Test
    void randomSmallerThanFilterAllColumnsTest() {
        ParentTable randomRow = getRandomRow();
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }

            Field field = entry.getValue();
            var valFrom = getFieldValue(randomRow, field);
            results = level01Repository.hiperBootFilter(ParentTable.class, smallerThan(columnName, valFrom.toString()));

            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat(((Comparable) valResult.toString().toUpperCase()).compareTo(
                            valFrom.toString().toUpperCase())).isLessThanOrEqualTo(0);
                }
                else {
                    assertThat(((Comparable) valResult).compareTo(valFrom)).isLessThanOrEqualTo(0);
                }
            }
        }
    }

    @Test
    void randomBetweenFilterAllColumnsTest() {
        List<ParentTable> results;

        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnsGreaterThanIncompatible.contains(columnName)) {
                continue;
            }
            Field field = entry.getValue();
            var filter = addPageToFilter(null, getPageWithStartSizeSort(null, null, columnName));
            Page<ParentTable> pageResults = level01Repository.hiperBootPageFilter(ParentTable.class, filter);

            assertThat(pageResults).isNotEmpty();
            var valFrom = getFieldValue(pageResults.getContent().get(1), field);
            var valTo = getFieldValue(pageResults.getContent().get(pageResults.getContent().size() - 2), field);

            results = level01Repository.hiperBootFilter(ParentTable.class,
                    between(columnName, valFrom.toString(), valTo.toString()));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                if (columnName.equals("colString")) {
                    assertThat((Comparable) valResult.toString().toUpperCase()).isBetween(valFrom.toString().toUpperCase(),
                            valTo.toString().toUpperCase());
                }
                else {
                    assertThat((Comparable) valResult).isBetween((Comparable) valFrom, (Comparable) valTo);
                }
            }
        }
    }

    @Test
    void randomLikeFilterAllColumnsTest() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals("colString", "%Ab%"));
        assertThat(results).hasSize(2);
    }

    @Test
    void randomNullFilterAllColumnsTest() {
        var fieldMaps = getFieldList(ParentTable.class);
        for (Map.Entry<String, Field> entry : fieldMaps.entrySet()) {
            String columnName = entry.getKey();
            if (columnName.equals("id") || columnsToIgnore.contains(columnName)) {
                continue;
            }
            List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbIsNull(columnName));
            assertThat(results).hasSize(1);
        }
    }

    @Test
    void manyToOneTest() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals("someTable.name", "NAme c"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSomeTable().getName()).isEqualTo("Name C");
    }

    @Test
    void oneToManyTest() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals("children.number", "30"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getChildren()).hasSize(2);
    }

    @Test
    void likeContainsTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbEquals("author.name", "%eo%"));
        assertThat(list).hasSize(13);
    }

    @Test
    void likeEndsTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbEquals("author.name", "%Orwell"));
        assertThat(list).hasSize(5);
    }

    @Test
    void likeStartsTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbAnd(hbEquals("author.name", "J%"), greaterThan("price", "5")));
        assertThat(list).hasSize(5);
    }

    @Test
    void isNullTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbIsNull("price"));
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getPrice()).isNull();
    }

    @Test
    void isNotNullTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbAnd(hbEquals("author.id", "4"), hbNot(hbIsNull("price"))));
        assertThat(list).hasSize(9);
    }

    @Test
    void isNotInTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbNot(hbEquals("author.id", "1", "3", "4", "5")));

        assertThat(list).hasSize(5);
    }

    protected ParentTable getRandomRow() {
        List<ParentTable> results = level01Repository.findAll();
        assertThat(results).isNotEmpty();

        Random random = new Random();
        int randomInt = random.nextInt(results.size() - 1);
        return results.get(randomInt);
    }
}