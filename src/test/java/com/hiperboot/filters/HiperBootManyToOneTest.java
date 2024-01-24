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

import static com.hiperboot.util.HBUtils.hbEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.MainTable;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.repository.MainTableHiperBootRepository;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

class HiperBootManyToOneTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository level01Repository;

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Test
    void getByFilter_ShouldReturnNonEmptyResultsForSpecificSomeTableId() {
        // Arrange
        String someTableId = "1";
        String filterCriteria = "someTable.id";

        // Act
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, hbEquals(filterCriteria, someTableId));

        // Assert
        assertThat(results)
                .as("Check if the results are not empty when filtering by someTable.id equal to " + someTableId)
                .isNotEmpty();

        assertThat(results)
                .as("Verify that each result has someTable.id equal to " + someTableId)
                .allMatch(result -> result.getSomeTable().getId().toString().equals(someTableId));
    }


    @Test
    void manyToOneWithStringPKTest() {
        // Arrange
        String expectedValue = "Nothing3";
        String filterCriteria = "childTable.granChild.something";

        // Act
        List<MainTable> results = mainTableHiperBootRepository.hiperBootFilter(MainTable.class, hbEquals(filterCriteria, expectedValue));

        // Assert
        assertThat(results).as("Check if results are not empty")
                .isNotEmpty();
        assertThat(results.get(0).getChildTable().getGranChild().get(0).getSomething())
                .as("Check if the first result matches the expected value")
                .isEqualTo(expectedValue);
    }
}
