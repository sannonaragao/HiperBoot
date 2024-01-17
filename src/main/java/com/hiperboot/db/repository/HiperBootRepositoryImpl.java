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

import static com.hiperboot.db.filter.DbFilterBuilder.getDbFilters;
import static com.hiperboot.pagination.PageRequestBuilder.getPageRequest;
import static com.hiperboot.pagination.PageRequestBuilder.getPagination;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.hiperboot.db.domain.BasePage;
import com.hiperboot.db.domain.BasePageImpl;
import com.hiperboot.db.domain.PaginationImpl;
import com.hiperboot.db.filter.DbFilter;
import com.hiperboot.db.filter.HiperBootFilterGenerator;
import com.hiperboot.pagination.PageRequestBuilder;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements HiperBootRepository<T, ID> {

    private final HiperBootFilterGenerator<T> filterGenerator;

    public HiperBootRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.filterGenerator = new HiperBootFilterGenerator<>();
    }

    @Override
    public List<T> hiperBootFilter(Class<T> entity, Map<String, Object> filters) {
        Specification<T> specifications = getSpecification(entity, getDbFilters(entity, filters));
        return nonNull(specifications) ? findAll(specifications) : findAll();
    }

    @Override
    public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters) {
        return hiperBootPageFilter(entity, filters, getPageRequest(getPagination(filters)));
    }

    @Override
    public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable) {
        final var listEntities = this.hiperBootPageFilter(entity, filters, pageable);
        return new BasePageImpl(listEntities.getContent(), new PaginationImpl(listEntities));
    }

    @Override
    public BasePage hiperBootBasePageFilter(Class<T> entity, Map<String, Object> filters) {
        var pageable = getPageRequest(PageRequestBuilder.extractPagination(filters));
        return hiperBootBasePageFilter(entity, filters, pageable);
    }

    @Override
    public Page<T> hiperBootPageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable) {
        Specification<T> specifications = getSpecification(entity, getDbFilters(entity, filters));
        return nonNull(specifications) ? findAll(specifications, pageable) : findAll(pageable);
    }

    private Specification<T> getSpecification(Class<T> entity, List<DbFilter> filters) {
        Specification<T> specifications = filterGenerator.getSpecificationFromFilters(filters);
        Specification<T> extraCriteria = getExtraCriteria(specifications, entity);
        if (nonNull(extraCriteria)) {
            specifications = isNull(specifications) ? where(extraCriteria) : specifications.and(extraCriteria);
        }
        return specifications;
    }
}
