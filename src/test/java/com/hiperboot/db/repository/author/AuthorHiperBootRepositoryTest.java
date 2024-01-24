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
package com.hiperboot.db.repository.author;

import static com.hiperboot.util.HBUtils.hbEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.book.Author;

class AuthorHiperBootRepositoryTest extends BaseTestClass {
    @Autowired
    private AuthorHiperBootRepository authorHiperBootRepository;

    @Test
    void manyToOneWithSet_ShouldReturnAuthorsWithSpecificBookPrice() {
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
