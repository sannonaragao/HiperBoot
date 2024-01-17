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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.domain.BasePage;
import com.hiperboot.db.domain.Pagination;
import com.hiperboot.db.entity.book.Book;
import com.hiperboot.db.repository.BookHiperBootRepository;

class BasePageTest extends BaseTestClass {

    @Autowired
    private BookHiperBootRepository bookHiperBootRepository;

    @Test
    void pageBaseTest() {
        BasePage pageTest = bookHiperBootRepository.hiperBootBasePageFilter(Book.class,
                hbEquals("author.id", "3").sortedBy("title, published").offset(1).limit(3));

        List<Book> bookList = pageTest.getData();

        assertThat(bookList).hasSize(3);

        Pagination pagination = pageTest.getPagination();

        assertThat(pagination.getCurrentPage()).isEqualTo(1);
        assertThat(pagination.getTotalPages()).isEqualTo(3);
        assertThat(pagination.getPageSize()).isEqualTo(3);
        assertThat(pagination.getTotalRows()).isEqualTo(8);
    }

    @Test
    void pageBasePage2Test() {

        BasePage pageTest = bookHiperBootRepository.hiperBootBasePageFilter(Book.class,
                hbEquals("author.id", "3").sortedBy("title, published").offset(3).limit(2));
        Pagination pagination = pageTest.getPagination();

        List<Book> bookList = pageTest.getData();
        assertThat(bookList).hasSize(2);
        assertThat(pagination.getCurrentPage()).isEqualTo(2);
        assertThat(pagination.getTotalPages()).isEqualTo(4);
        assertThat(pagination.getPageSize()).isEqualTo(2);
        assertThat(pagination.getTotalRows()).isEqualTo(8);
    }
}
