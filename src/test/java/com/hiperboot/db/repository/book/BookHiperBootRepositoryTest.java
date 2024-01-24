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
package com.hiperboot.db.repository.book;

import static com.hiperboot.util.HBUtils.greaterThan;
import static com.hiperboot.util.HBUtils.hbAnd;
import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.book.Book;

class BookHiperBootRepositoryTest extends BaseTestClass {

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void likeContains_ShouldReturnBooksByAuthorNameContainingSubstringCaseInsensitive() {
        // Arrange
        String authorNameSubstring = "%eo%";
        int expectedNumberOfBooks = 12;
        String filterCriteria = "author.name";

        // Act
        List<Book> booksMatchingAuthorName = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbEquals(filterCriteria, authorNameSubstring));

        // Assert
        assertThat(booksMatchingAuthorName)
                .as("Check if the number of books with an author whose name contains 'eo' (case insensitive) is equal to the expected count")
                .hasSize(expectedNumberOfBooks);

        assertThat(booksMatchingAuthorName)
                .as("Check if every book's author's name contains 'eo' (case insensitive)")
                .allMatch(book -> book.getAuthor().getName().toLowerCase().contains("eo"));
    }

    @Test
    void likeContains_ShouldReturnBooksByAuthorNameContainingAndEndingWithSpecificSubstrings() {
        // Arrange
        String authorNameEndsWith = "Orwell";
        int expectedNumberOfBooks = 4;
        String filterCriteria = "author.name";

        // Act
        List<Book> booksMatchingAuthorName = bookHiperBootRepository.hiperBootFilter(Book.class,
                hbEquals(filterCriteria, "%" + authorNameEndsWith));

        // Assert
        assertThat(booksMatchingAuthorName)
                .as("Check if the number of books with an author whose name ends with 'Orwell' (case insensitive) is equal to the expected count")
                .hasSize(expectedNumberOfBooks);

        assertThat(booksMatchingAuthorName)
                .as("Check if every book's author's name ends with 'Orwell'")
                .allMatch(book -> book.getAuthor().getName().endsWith(authorNameEndsWith));
    }

    @Test
    void likeStartsWithAuthorNameAndPriceGreaterThan_ShouldValidateEachBook() {
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
        assertThat(booksMatchingCriteria)
                .as("Check if the number of books with an author whose name starts with 'J' and price greater than 5 matches the expected count")
                .hasSize(expectedBookCount);

        assertThat(booksMatchingCriteria)
                .as("Ensure every book's author name starts with 'J'")
                .allMatch(book -> book.getAuthor().getName().startsWith("J"));

        assertThat(booksMatchingCriteria)
                .as("Ensure the price of each book is greater than 5")
                .allMatch(book -> book.getPrice().compareTo(minimumPriceThreshold) > 0);
    }

    @Test
    void isNullTest_ShouldReturnBooksWithNullPrice() {
        // Arrange
        int expectedBookCount = 1;

        // Act
        List<Book> booksWithNullPrice = bookHiperBootRepository.hiperBootFilter(Book.class, hbIsNull("price"));

        // Assert
        assertThat(booksWithNullPrice)
                .as("Check if the number of books with null price matches the expected count")
                .hasSize(expectedBookCount);

        assertThat(booksWithNullPrice)
                .as("Ensure the price of each book in the list is null")
                .allMatch(book -> book.getPrice() == null);
    }

    @Test
    void isNotNullAndAuthorIdIsFour_ShouldReturnMatchingBooks() {
        // Arrange
        int expectedBookCount = 9;
        Long authorIdCriteria = 4L;

        // Act
        List<Book> booksWithNonNullPriceAndAuthorId = bookHiperBootRepository.hiperBootFilter(
                Book.class,
                hbAnd(hbEquals("author.id", authorIdCriteria.toString()), hbNot(hbIsNull("price")))
        );

        // Assert
        assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Check if the number of books with non-null price and author ID 4 matches the expected count")
                .hasSize(expectedBookCount);

        assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Ensure every book has a non-null price")
                .allMatch(book -> book.getPrice() != null);

        assertThat(booksWithNonNullPriceAndAuthorId)
                .as("Ensure every book's author has an ID of 4")
                .allMatch(book -> book.getAuthor().getId().equals(authorIdCriteria));
    }

    @Test
    void isNotInTest() {
        var list = bookHiperBootRepository.hiperBootFilter(Book.class, hbNot(hbEquals("author.id", "1", "3", "4", "5")));
        assertThat(list).hasSize(4);
    }

    @Test
    void isNotInTest_ShouldReturnBooksWhoseAuthorIdIsNotInList() {
        // Arrange
        int expectedBookCount = 4;
        List<Long> excludedAuthorIds = Arrays.asList(1L, 3L, 4L, 5L);
        String[] excludedAuthorIdsArray = excludedAuthorIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        // Act
        List<Book> booksWithExcludedAuthorIds = bookHiperBootRepository.hiperBootFilter(
                Book.class,
                hbNot(hbEquals("author.id", excludedAuthorIdsArray))
        );

        // Assert
        assertThat(booksWithExcludedAuthorIds)
                .as("Check if the number of books whose author's ID is not in the specified list matches the expected count")
                .hasSize(expectedBookCount);

        assertThat(booksWithExcludedAuthorIds)
                .as("Ensure none of the books have an author with an ID in the excluded list")
                .noneMatch(book -> excludedAuthorIds.contains(book.getAuthor().getId()));
    }
}