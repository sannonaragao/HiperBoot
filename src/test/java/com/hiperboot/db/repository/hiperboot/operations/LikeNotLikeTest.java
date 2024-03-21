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

import static com.hiperboot.util.HBUtils.greaterThan;
import static com.hiperboot.util.HBUtils.hbAnd;
import static com.hiperboot.util.HBUtils.hbEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.data_simulation.entity.ParentTable;
import com.hiperboot.data_simulation.entity.book.Book;
import com.hiperboot.data_simulation.repository.hiperboot.ParentTableHiperBootRepository;
import com.hiperboot.data_simulation.repository.hiperboot.book.BookHiperBootRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
class LikeNotLikeTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableRepository;

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void shouldReturnRowsMatchingCaseInsensitiveLikePattern() {
        String filterValue = "%Ab%";
        String filterColumn = "colString";

        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, filterValue));
        assertThat(results).hasSize(2);

        results.forEach(row ->
                assertThat(row.getColString().toLowerCase()).contains(filterValue.replace("%", "").toLowerCase())
        );
    }

    @Test
    void manyToOneShouldFindRowsWhenColumnStringContains() {
        // Arrange
        String authorNameSubstring = "%eo%";
        int expectedNumberOfBooks = 13;
        String filterCriteria = "author.name";

        // Act
        List<Book> booksMatchingAuthorName = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbEquals(filterCriteria, authorNameSubstring));

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksMatchingAuthorName)
                .as("Check if the number of books with an author whose name contains 'eo' (case insensitive) is equal to the expected count")
                .hasSize(expectedNumberOfBooks);

        AssertionsForInterfaceTypes.assertThat(booksMatchingAuthorName)
                .as("Check if every book's author's name contains 'eo' (case insensitive)")
                .allMatch(book -> book.getAuthor().getName().toLowerCase().contains("eo"));
    }

    @Test
    void manyToOneShouldFindRowsWhenColumnStringEnds() {
        // Arrange
        String authorNameEndsWith = "Orwell";
        int expectedNumberOfBooks = 5;
        String filterCriteria = "author.name";

        // Act
        List<Book> booksMatchingAuthorName = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbEquals(filterCriteria, "%" + authorNameEndsWith));

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksMatchingAuthorName)
                .as("Check if the number of books with an author whose name ends with 'Orwell' (case insensitive) is equal to the expected count")
                .hasSize(expectedNumberOfBooks);

        AssertionsForInterfaceTypes.assertThat(booksMatchingAuthorName)
                .as("Check if every book's author's name ends with 'Orwell'")
                .allMatch(book -> book.getAuthor().getName().endsWith(authorNameEndsWith));
    }

    @Test
    void manyToOneShouldFindRowsWhenColumnStringStarts() {
        // Arrange
        String authorNameStartsWithPattern = "J%";
        int expectedBookCount = 10;

        // Act
        List<Book> booksMatchingCriteria = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbAnd(hbEquals("author.name", authorNameStartsWithPattern)));

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksMatchingCriteria)
                .as("Check if the number of books with an author whose name starts with 'J' and price greater than 5 matches the expected count")
                .hasSize(expectedBookCount);

        AssertionsForInterfaceTypes.assertThat(booksMatchingCriteria)
                .as("Ensure every book's author name starts with 'J'")
                .allMatch(book -> book.getAuthor().getName().startsWith("J"));

    }

    @Test
    void combinedCriteriaShouldReturnRowsByWhenStringStartsWithJAndBigDecimalGreaterThan() {
        // Arrange
        String authorNameStartsWithPattern = "J%";
        BigDecimal minimumPriceThreshold = new BigDecimal("5");
        int expectedBookCount = 5;

        // Act
        List<Book> booksMatchingCriteria = bookHiperBootRepository.hiperBootFilter(
                Book.class,
                hbAnd(hbEquals("author.name", authorNameStartsWithPattern), greaterThan("price", minimumPriceThreshold.toString()))
        );

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksMatchingCriteria)
                .as("Check if the number of books with an author whose name starts with 'J' and price greater than 5 matches the expected count")
                .hasSize(expectedBookCount);

        AssertionsForInterfaceTypes.assertThat(booksMatchingCriteria)
                .as("Ensure every book's author name starts with 'J'")
                .allMatch(book -> book.getAuthor().getName().startsWith("J"));

        AssertionsForInterfaceTypes.assertThat(booksMatchingCriteria)
                .as("Ensure the price of each book is greater than 5")
                .allMatch(book -> book.getPrice().compareTo(minimumPriceThreshold) > 0);
    }
}
