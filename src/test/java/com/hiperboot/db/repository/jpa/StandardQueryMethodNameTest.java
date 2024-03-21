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
package com.hiperboot.db.repository.jpa;

import static com.hiperboot.util.HBUtils.hbIsNull;
import static com.hiperboot.util.HBUtils.hbNot;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.List;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.data_simulation.entity.book.Author;
import com.hiperboot.data_simulation.repository.hiperboot.author.AuthorHiperBootRepository;

class StandardQueryMethodNameTest extends BaseTestClass {
    @Autowired
    private AuthorHiperBootRepository authorHiperBootRepository;

    @Test
    void whenFindByBooksTitle_thenReturnAuthors() {
        // Test
        List<Author> foundAuthors = authorHiperBootRepository.findByBooks_Title("Harry Potter and the Chamber of Secrets");
        assertThat(foundAuthors).isNotEmpty();
        assertThat(foundAuthors.get(0).getName()).isEqualTo("J.K. Rowling");
    }

    @Test
    void whenFindAuthorsByName_thenReturnAuthors() {
        List<Author> authors = authorHiperBootRepository.findAuthorsByName("George Orwell");
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("George Orwell");
    }

    @Test
    void whenFindAuthorsBornAfter_thenReturnAuthors() {
        Date date = Date.valueOf("1965-07-30");
        List<Author> authors = authorHiperBootRepository.findAuthorsBornAfter(date);
        assertThat(authors).isNotEmpty();
        assertThat(authors).extracting(Author::getName).contains("J.K. Rowling");
    }

    @Test
    void whenFindByName_thenReturnAuthor() {
        List<Author> authors = authorHiperBootRepository.findByName("Jane Austen");
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Jane Austen");
    }

    @Test
    void isNullTest_ShouldReturnAuthorWithoutBooks() {
        // Arrange
        int expectedAuthorCount = 1;

        // Act
        List<Author> authorsWithoutBooks = authorHiperBootRepository.hiperBootFilter(Author.class, hbIsNull("books"));

        // Assert
        AssertionsForInterfaceTypes.assertThat(authorsWithoutBooks)
                .as("Check if the number of authors without books matches the expected count")
                .hasSize(expectedAuthorCount);

        AssertionsForInterfaceTypes.assertThat(authorsWithoutBooks)
                .as("Ensure the right author is returned")
                .allMatch(author -> author.getName().equals("No Buck"));
    }

    @Test
    void isNotNullTest_ShouldReturnAuthorWithoutBooks() {
        // Arrange
        int expectedAuthorCount = 5;

        // Act
        List<Author> authorsWithoutBooks = authorHiperBootRepository.hiperBootFilter(Author.class, hbNot(hbIsNull("books")));

        // Assert
        AssertionsForInterfaceTypes.assertThat(authorsWithoutBooks)
                .as("Check if the number of authors without books matches the expected count")
                .hasSize(expectedAuthorCount);

        AssertionsForInterfaceTypes.assertThat(authorsWithoutBooks)
                .as("Ensure the right author is returned")
                .noneMatch(author -> author.getName().equals("No Buck"));
    }
}