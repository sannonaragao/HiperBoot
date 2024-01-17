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
package com.hiperboot.pagination;

import static com.hiperboot.util.HBUtils.hbEquals;
import static com.hiperboot.util.HBUtils.sortedBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.book.Book;
import com.hiperboot.db.repository.BookHiperBootRepository;

class PaginationBookTest extends BaseTestClass {

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void sortTest() {
        var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("-title"));
        assertThat(pageTest.stream().toList())
                .extracting(Book::getTitle)
                .isSortedAccordingTo(Comparator.reverseOrder());

        assertThat(pageTest.getTotalPages()).isEqualTo(1);
        assertThat(pageTest.getTotalElements()).isEqualTo(8);
        assertThat(pageTest.getSort().toString()).isEqualTo("title: DESC");
    }

    @Test
    void staticSortTest() {
        var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, sortedBy("title"));
        assertThat(pageTest.stream().toList())
                .extracting(Book::getTitle)
                .isSortedAccordingTo(Comparator.naturalOrder());
    }

    @Test
    void paginationTest() {
        var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class, hbEquals("author.id", "3").sortedBy("- title"));
        assertThat(pageTest.stream().toList())
                .extracting(Book::getTitle)
                .isSortedAccordingTo(Comparator.reverseOrder());

        assertThat(pageTest.getTotalPages()).isEqualTo(1);
        assertThat(pageTest.getTotalElements()).isEqualTo(8);
        assertThat(pageTest.getSort().toString()).isEqualTo("title: DESC");
    }

    @Test
    void paginationOffsetTest() {
        var pageTest = bookHiperBootRepository.hiperBootPageFilter(Book.class,
                hbEquals("author.id", "3").sortedBy("title, published").offset(3).limit(5));
        assertThat(pageTest.stream().toList())
                .extracting(Book::getTitle)
                .isSortedAccordingTo(Comparator.naturalOrder());

        assertThat(pageTest.getTotalPages()).isEqualTo(2);
        assertThat(pageTest.getTotalElements()).isEqualTo(8);
        assertThat(pageTest.getContent()).hasSize(5);
        assertThat(pageTest.getSort().toString()).isEqualTo("title: ASC,published: ASC");
    }
}
