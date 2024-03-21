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

import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.hbNot;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
class InNotInTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableRepository;

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void shouldFilterRowsByStringValuesIgnoringCase() {
        String[] expectedValues = { "abc", "xxx" };
        String filterColumn = "colString";

        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, expectedValues));
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
    void shouldNotReturnRowsInSpecificLongValues() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbNot(hbEquals("author.id", "1", "3", "4", "5")));
        AssertionsForInterfaceTypes.assertThat(list).hasSize(5);
    }

    @Test
    void shouldExcludeRowsBySpecificLongValues() {
        // Arrange
        int expectedBookCount = 5;
        List<Long> excludedAuthorIds = Arrays.asList(1L, 3L, 4L, 5L);
        String[] excludedAuthorIdsArray = excludedAuthorIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        // Act
        List<Book> booksWithExcludedAuthorIds = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbNot(hbEquals("author.id", excludedAuthorIdsArray))
        );

        // Assert
        AssertionsForInterfaceTypes.assertThat(booksWithExcludedAuthorIds)
                .as("Check if the number of books whose author's ID is not in the specified list matches the expected count")
                .hasSize(expectedBookCount);

        AssertionsForInterfaceTypes.assertThat(booksWithExcludedAuthorIds)
                .as("Ensure none of the books have an author with an ID in the excluded list")
                .noneMatch(book -> excludedAuthorIds.contains(book.getAuthor().getId()));
    }
}
