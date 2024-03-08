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

import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static java.util.Objects.isNull;

import java.util.List;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.MainTable;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainTableHiperBootRepositoryTest extends BaseTestClass {

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Test
    void isNullTest_ShouldReturnAuthorWithoutBooks() {
        // Arrange
        int expectedCount = 1;

        // Act
        List<MainTable> mainTableWithoutChild = mainTableHiperBootRepository.hiperBootFilter(MainTable.class, hbIsNull("childTable"));

        // Assert
        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Check if the number of records without child records matches the expected count")
                .hasSize(expectedCount);

        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Ensure the right author is returned")
                .allMatch(main -> isNull(main.getChildTable()));
    }

    @Test
    void isNotNullTest_ShouldReturnAuthorWithoutBooks() {
        // Arrange
        int expectedCount = 2;

        // Act
        List<MainTable> mainTableWithoutChild = mainTableHiperBootRepository.hiperBootFilter(MainTable.class,
                hbNot(hbIsNull("childTable")));

        // Assert
        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Check if the number of records with child records matches the expected count")
                .hasSize(expectedCount);

        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Ensure the right author is returned")
                .allMatch(main -> !isNull(main.getChildTable()));
    }
}