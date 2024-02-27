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
package com.hiperboot.service;

import static com.hiperboot.db.filter.DbFilterBuilder.getDbFilters;
import static com.hiperboot.pagination.PageRequestBuilder.extractPagination;
import static com.hiperboot.pagination.PageRequestBuilder.getPageRequest;
import static com.hiperboot.pagination.PageRequestBuilder.getPagination;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hiperboot.db.domain.BasePage;
import com.hiperboot.db.domain.BasePageImpl;
import com.hiperboot.db.domain.PaginationImpl;
import com.hiperboot.db.filter.DbFilter;
import com.hiperboot.db.filter.HiperBootFilterGenerator;

//@Service
public class HiperBootService<T> {

    private final HiperBootFilterGenerator<T> filterGenerator;
    private final JpaSpecificationExecutor<T> jpaRepository;

    public HiperBootService(JpaSpecificationExecutor<T> jpaRepository) {
        super();
        this.filterGenerator = new HiperBootFilterGenerator<>();
        this.jpaRepository = jpaRepository;
    }

    public List<T> hiperBootFilter(Class<T> entity, Map<String, Object> filters) {
        Specification<T> specifications = getSpecification(getDbFilters(entity, filters));
        return jpaRepository.findAll(specifications);
    }

    public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters) {
        return hiperBootPageFilter(entity, filters, getPageRequest(getPagination(filters)));
    }

    public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable) {
        return jpaRepository.findAll(getSpecification(getDbFilters(entity, filters)), pageable);
    }

    public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters) {
        final var listEntities = this.hiperBootPageFilter(entity, filters, getPageRequest(extractPagination(filters)));
        return new BasePageImpl(listEntities.getContent(), new PaginationImpl(listEntities));
    }

    public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable) {
        final var listEntities = this.hiperBootPageFilter(entity, filters, pageable);
        return new BasePageImpl(listEntities.getContent(), new PaginationImpl(listEntities));
    }

    public Specification<T> getSpecification(List<DbFilter> filters) {
        return filterGenerator.getSpecificationFromFilters(filters);
    }
}