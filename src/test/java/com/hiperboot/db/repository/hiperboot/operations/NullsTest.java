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
package com.hiperboot.db.repository.hiperboot.operations;

import static com.hiperboot.util.HBUtils.hbAnd;
import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.data_simulation.entity.MainTable;
import com.hiperboot.data_simulation.entity.ParentTable;
import com.hiperboot.data_simulation.entity.book.Book;
import com.hiperboot.data_simulation.repository.hiperboot.MainTableHiperBootRepository;
import com.hiperboot.data_simulation.repository.hiperboot.ParentTableHiperBootRepository;
import com.hiperboot.data_simulation.repository.hiperboot.book.BookHiperBootRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
class NullsTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableHiperBootRepository;

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void shouldReturnAllRowsWhenFilterIsNull() {
        // Act
        List<ParentTable> resultWithFilter = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, null);

        List<ParentTable> resultAllRows = parentTableHiperBootRepository.findAll();

        // Assert
        assertThat(resultWithFilter)
                .as("Check if both results the same number of rows")
                .hasSize(resultAllRows.size());
    }

    @Test
    void manyToOneShouldReturnRowWhenEntityIsNull() {
        // Arrange
        int expectedCount = 1;
        String description = "other description with null child";

        // Act -
        List<MainTable> mainTableWithoutChild = mainTableHiperBootRepository.hiperBootFilter(MainTable.class, hbIsNull("childTable"));

        // Assert
        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Check if the number of records without child records matches the expected count")
                .hasSize(expectedCount)
                .allMatch(mainTable -> description.equals(mainTable.getDescription()));

        AssertionsForInterfaceTypes.assertThat(mainTableWithoutChild)
                .as("Ensure the right author is returned")
                .allMatch(main -> isNull(main.getChildTable()));
    }

    @Test
    void manyToOneShouldReturnRowsWhereChildIsNotNull() {
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

    @Test
    void shouldReturnRowsWithNullBigDecimal() {
        // Arrange
        int expectedBookCount = 1;

        // Act
        List<Book> booksWithNullPrice = bookHiperBootRepository.hiperBootFilter(Book.class, hbIsNull("price"));

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksWithNullPrice)
                .as("Check if the number of books with null price matches the expected count")
                .hasSize(expectedBookCount);

        AssertionsForInterfaceTypes.assertThat(booksWithNullPrice)
                .as("Ensure the price of each book in the list is null")
                .allMatch(book -> book.getPrice() == null);
    }

    @Test
    void combinedCriteriaShouldReturnRowsWithNonNullBigDecimalAndSpecificLong() {
        // Arrange
        int expectedBookCount = 9;
        Long authorIdCriteria = 4L;

        // Act
        List<Book> booksWithNonNullPriceAndAuthorId = bookHiperBootRepository.hiperBootFilter(
                Book.class,
                hbAnd(hbEquals("author.id", authorIdCriteria.toString()), hbNot(hbIsNull("price")))
        );

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Check if the number of books with non-null price and author ID 4 matches the expected count")
                .hasSize(expectedBookCount);

        AssertionsForInterfaceTypes.assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Ensure every book has a non-null price")
                .allMatch(book -> book.getPrice() != null);

        AssertionsForInterfaceTypes.assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Ensure every book's author has an ID of 4")
                .allMatch(book -> book.getAuthor().getId().equals(authorIdCriteria));
    }
}
