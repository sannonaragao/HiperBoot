package com.hiperboot.filters;

import static com.hiperboot.util.HBUtil.addPageToFilter;
import static com.hiperboot.util.HBUtil.columnBetween;
import static com.hiperboot.util.HBUtil.columnEqualValues;
import static com.hiperboot.util.HBUtil.columnGreaterThan;
import static com.hiperboot.util.HBUtil.columnIsNull;
import static com.hiperboot.util.HBUtil.columnNotEqualValues;
import static com.hiperboot.util.HBUtil.columnSmallerThan;
import static com.hiperboot.util.HBUtil.columnSubEntity;
import static com.hiperboot.util.HBUtil.getPageWithStartSizeSort;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.entity.SomeTable;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
class HiperBootRepositoryColumnsTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository level01Repository;

    private List<String> columnsGreaterThanIncompatible = List.of("colBoolean", "colStatusEnum", "someTable", "children");
    private List<String> columnsInIncompatible = List.of("colBoolean", "someTable", "children");
    private List<String> columnsToIgnore = List.of("someTable", "children");

    private List<String> columsToBeCaseInsensitive = List.of("colString", "colUUID");

    @Test
    void testGetByFilter() {
        Map<String, Object> filter = Collections.emptyMap();
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, Map.of("colString", "RandomString1"));
        assertThat(results).isNotEmpty();
    }

    @Test
    void randomEqualsFilterAllColumnsTest() {
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
            results = level01Repository.getByHiperBootFilter(ParentTable.class, columnEqualValues(columnName, val.toString()));
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
            results = level01Repository.getByHiperBootFilter(ParentTable.class, columnNotEqualValues(columnName, val.toString()));
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

            //            System.out.printf("columnName %s  /  val1 %s  / val2 %s \n", columnName, val1, val2);
            results = level01Repository.getByHiperBootFilter(ParentTable.class,
                    columnEqualValues(columnName, val1.toString(), val2.toString()));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                //                System.out.printf("columnName %s  /  val1 %s  / val2 %s / Result %s\n", columnName, val1, val2, valResult);
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
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, columnEqualValues("colString", "abc", "xxx"));
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
            System.out.printf("columnName %s  /  val1 %s  / val2 %s \n", columnName, val1, val2);
            results = level01Repository.getByHiperBootFilter(ParentTable.class, columnNotEqualValues(columnName, val1, val2));

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
            results = level01Repository.getByHiperBootFilter(ParentTable.class, columnGreaterThan(columnName, valFrom.toString()));
            System.out.printf("columnName %s  /  valFrom %s  \n", columnName, valFrom);

            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                System.out.printf("columnName %s  /  valFrom %s  / valResult %s \n", columnName, valFrom, valResult);
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
            results = level01Repository.getByHiperBootFilter(ParentTable.class, columnSmallerThan(columnName, valFrom.toString()));
            System.out.printf("columnName %s  /  valFrom %s  \n", columnName, valFrom);

            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                System.out.printf("columnName %s  /  valFrom %s  / valResult %s \n", columnName, valFrom, valResult);
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
            Page<ParentTable> pageResults = level01Repository.getByHiperBootPageFilter(ParentTable.class, filter);

            assertThat(pageResults).isNotEmpty();
            var valFrom = getFieldValue(pageResults.getContent().get(1), field);
            var valTo = getFieldValue(pageResults.getContent().get(pageResults.getContent().size() - 2), field);

            results = level01Repository.getByHiperBootFilter(ParentTable.class,
                    columnBetween(columnName, valFrom.toString(), valTo.toString()));
            assertThat(results).isNotEmpty();

            for (ParentTable row : results) {
                var valResult = getFieldValue(row, field);
                System.out.printf("columnName %s  /  valFrom %s  / valResult %s \n", columnName, valFrom, valResult);
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
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, columnEqualValues("colString", "%Ab%"));
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
            System.out.printf("columnName %s \n", columnName);
            List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, columnIsNull(columnName));
            assertThat(results).hasSize(1);
        }
    }

    @Test
    void manyToOneTest() {
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, columnSubEntity("someTable.name", "NAme c"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSomeTable().getName()).isEqualTo("Name C");
    }

    @Test
    void oneToManyTest() {
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, columnSubEntity("children.number", "30"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getChildren()).hasSize(4);
    }

    protected ParentTable getRandomRow() {
        List<ParentTable> results = level01Repository.findAll();
        assertThat(results).isNotEmpty();

        Random random = new Random();
        int randomInt = random.nextInt(results.size() - 1);
        //        var randomRow = results.get(0);
        return results.get(randomInt);
    }
}