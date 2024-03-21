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
package com.hiperboot.data_simulation.repository.hiperboot.author;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hiperboot.data_simulation.entity.book.Author;
import com.hiperboot.db.repository.HiperBootRepository;

@Repository
public interface AuthorHiperBootRepository extends HiperBootRepository<Author>, JpaRepository<Author, Long> {
    List<Author> findByBooks_Title(String title);

    @Query("SELECT a FROM Author a WHERE a.name = ?1")
    List<Author> findAuthorsByName(String name);

    @Query(value = "SELECT * FROM author WHERE birthday > ?1", nativeQuery = true)
    List<Author> findAuthorsBornAfter(Date date);

    List<Author> findByName(@Param("name") String name);
}