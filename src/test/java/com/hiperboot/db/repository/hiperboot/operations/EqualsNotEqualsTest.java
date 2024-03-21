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
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.data_simulation.entity.MainTable;
import com.hiperboot.data_simulation.entity.ParentTable;
import com.hiperboot.data_simulation.entity.book.Author;
import com.hiperboot.data_simulation.repository.hiperboot.MainTableHiperBootRepository;
import com.hiperboot.data_simulation.repository.hiperboot.ParentTableHiperBootRepository;
import com.hiperboot.data_simulation.repository.hiperboot.author.AuthorHiperBootRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
class EqualsNotEqualsTest extends BaseTestClass {

    @Autowired
    private AuthorHiperBootRepository authorHiperBootRepository;

    @Autowired
    private ParentTableHiperBootRepository parentTableRepository;

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Test
    void shouldReturnSingleRowWhenFilteredByStringColumnWithExactMatch() {
        // Arrange
        String expectedValue = "RandomString1";
        String filterColumn = "colString";

        // Act
        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, expectedValue));

        // Assert
        assertThat(results)
                .as("Check if the results are not empty when filtering by " + filterColumn + " equal to " + expectedValue)
                .hasSize(1)
                .as("Verify that each result's " + filterColumn + " matches the expected value (case insensitive)")
                .allMatch(result -> expectedValue.equalsIgnoreCase(result.getColString()));
    }

    @Test
    void shouldReturnTwoRowsWhenFilteredByStringColumnWithCaseInsensitiveMatch() {
        String expectedValue = "abc";
        String filterColumn = "colString";

        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, expectedValue));
        assertThat(results).hasSize(2);

        results.forEach(row ->
                assertThat(row.getColString()).as("Check if " + filterColumn + " matches the expected value (case insensitive)")
                        .isEqualToIgnoringCase(expectedValue)
        );
    }

    @Test
    void manyToOneRelationShouldReturnMatchingRowsForIgnoringCase() {
        String expectedValue = "Name C";
        String filterColumn = "someTable.name";

        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, "NAme c"));
        assertThat(results).hasSize(1);

        results.forEach(row ->
                assertThat(row.getSomeTable().getName()).isEqualToIgnoringCase(expectedValue)
        );
    }

    @Test
    void manyToOneRelationshipShouldFilterUsingPrimaryKey() {
        // Arrange
        String expectedValue = "1";
        String filterCriteria = "someTable.id";

        // Act
        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterCriteria, expectedValue));

        // Assert
        assertThat(results).hasSize(1)
                .as("Verify that each result has someTable.id equal to " + expectedValue)
                .allMatch(result -> result.getSomeTable().getId().toString().equals(expectedValue));
    }

    @Test
    void manyToOneWithOneToManyWithStringShouldReturnMatchingRow() {
        // Arrange
        String expectedValue = "Nothing3";
        String filterCriteria = "childTable.granChild.something";

        // Act
        List<MainTable> results = mainTableHiperBootRepository.hiperBootFilter(MainTable.class, hbEquals(filterCriteria, expectedValue));

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.stream().findFirst().get().getChildTable().getGranChild().stream().findFirst().get().getSomething())
                .as("Check if the first result matches the expected value")
                .isEqualTo(expectedValue);
    }

    @Test
    void oneToManyShouldReturnParentForMatchingChildNumber() {
        String expectedValue = "30";
        String filterColumn = "children.number";

        List<ParentTable> results = parentTableRepository.hiperBootFilter(ParentTable.class, hbEquals(filterColumn, expectedValue));
        assertThat(results).hasSize(1);

        results.forEach(parentTable ->
                parentTable.getChildren().forEach(child ->
                        assertThat(child.getNumber().toString()).isEqualToIgnoringCase(expectedValue)
                )
        );
    }

    @Test
    void manyToOneWithSetTypeShouldReturnAuthorsWithBooksPricedAtSpecificValue() {
        // Arrange
        BigDecimal expectedPrice = new BigDecimal("1.2");
        String expectedBookTitle = "Harry Potter and the Sorcerer's Stone";
        String filterCriteria = "books.price";

        // Act
        List<Author> results = authorHiperBootRepository.hiperBootFilter(Author.class, hbEquals(filterCriteria, expectedPrice.toString()));

        // Assert
        assertThat(results)
                .as("Check if results are not empty when filtering authors by book price")
                .isNotEmpty();

        assertThat(results.stream()
                .flatMap(author -> author.getBooks().stream())
                .anyMatch(book -> book.getTitle().equals(expectedBookTitle)))
                .as("Check if any author has a book with the specified title")
                .isTrue();
    }
}
