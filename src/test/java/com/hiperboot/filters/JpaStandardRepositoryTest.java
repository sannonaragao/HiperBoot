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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.repository.ParentTableStandardRepository;

@SpringBootTest
public class JpaStandardRepositoryTest {

    @Autowired
    private ParentTableStandardRepository parentTableStandardRepository;

    @BeforeEach
    public void setUp() {
        // Set up your test environment here
    }

    @Test
    public void test000GetByFilter() {
        Map<String, Object> filter = Collections.emptyMap();
        List<ParentTable> results = parentTableStandardRepository.findAll();
        assertNotNull(results);
        assertThat(results.size()).isNotZero();
        // Additional assertions
    }
}
