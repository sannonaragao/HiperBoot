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

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.hiperboot.db.entity.book.Book;
import com.hiperboot.db.repository.ExtraCriteriaStrategy;

import jakarta.persistence.criteria.Predicate;

@Component
public class BookExtraCriteriaStrategy implements ExtraCriteriaStrategy<Book> {

    @Override
    public Specification<Book> process(Specification<Book> existingSpec, Class<Book> type) {
        return Specification.where(existingSpec).and((root, query, criteriaBuilder) -> {
            // Create the new predicate
            Predicate notDeletedPredicate = criteriaBuilder.isFalse(root.get("deleted"));

            // If there are existing specifications, combine them with the new predicate
            if (existingSpec != null) {
                return criteriaBuilder.and(existingSpec.toPredicate(root, query, criteriaBuilder), notDeletedPredicate);
            }
            else {
                // If there are no existing specifications, just return the new predicate
                return notDeletedPredicate;
            }
        });
    }
}