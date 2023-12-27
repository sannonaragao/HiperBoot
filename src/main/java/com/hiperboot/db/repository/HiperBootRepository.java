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
package com.hiperboot.db.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface HiperBootRepository<T, ID> extends JpaRepository<T, ID> {

//    List<T> getByHiperBootFilter();

    List<T> getByHiperBootFilter(Class<T> entity, Map<String, Object> filters);
    Page<T> getByHiperBootPageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable);
    Page<T> getByHiperBootPageFilter(Class<T> entity, Map<String, Object> filters);

    default Specification<T> getExtraCriteria(Specification<T> specifications, Class<T> type) {
        return specifications;
    }

}
