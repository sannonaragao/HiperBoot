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
package com.hiperboot.filters;

import static com.hiperboot.util.HBUtils.hbEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.MainTable;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.entity.book.Author;
import com.hiperboot.db.repository.AuthorHiperBootRepository;
import com.hiperboot.db.repository.MainTableHiperBootRepository;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

class HiperBootManyToOneTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository level01Repository;

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Autowired
    private AuthorHiperBootRepository authorHiperBootRepository;

    @Test
    void testGetByFilter() {
        List<ParentTable> results = level01Repository.hiperBootFilter(ParentTable.class, Map.of("someTable", Map.of("id", "1")));
        assertThat(results).isNotEmpty();
    }

    @Test
    void manyToOneWithStringPKTest() {
        List<MainTable> results = mainTableHiperBootRepository.hiperBootFilter(MainTable.class,
                hbEquals("childTable.granChild.something", "Nothing3"));
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChildTable().getGranChild().get(0).getSomething()).isEqualTo("Nothing3");
    }

    @Test
    void manyToOneWithSetTest() {
        List<Author> results = authorHiperBootRepository.hiperBootFilter(Author.class, hbEquals("books.price", "1.2"));
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getBooks().stream().toList().get(0).getTitle()).isEqualTo("Harry Potter and the Sorcerer's Stone");
    }
}
