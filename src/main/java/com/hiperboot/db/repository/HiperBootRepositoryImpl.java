package com.hiperboot.db.repository;

import static com.hiperboot.db.filter.DbFilterBuilder.getDbFilters;
import static com.hiperboot.pagination.PageRequestBuilder.getPageRequest;
import static com.hiperboot.pagination.PageRequestBuilder.getPagination;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.List;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.hiperboot.db.filter.BaseFilterGenerator;
import com.hiperboot.db.filter.DbFilter;
import com.hiperboot.db.filter.HiperBootFilterGenerator;
import com.hiperboot.pagination.PageRequestBuilder;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements HiperBootRepository<T, ID> {

    private final HiperBootFilterGenerator filterGenerator;

    public HiperBootRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.filterGenerator = new HiperBootFilterGenerator();
    }

    @Override
    public List<T> getByHiperBootFilter(Class<T> entity, Map<String, Object> filters) {

        Specification<T> specifications = getSpecification(entity, getDbFilters(entity, filters));
        if (nonNull(specifications)) {
            return findAll(specifications);
        }
        else {
            return findAll();
        }
    }

    @Override
    public Page<T> getByHiperBootPageFilter(Class<T> entity, Map<String, Object> filters) {
        return getByHiperBootPageFilter(entity, filters, getPageRequest(getPagination(filters)));
    }

    @Override
    public Page<T> getByHiperBootPageFilter(Class<T> entity, Map<String, Object> filters, Pageable pageable) {
        final var pageParam = PageRequestBuilder.getPagination(filters);

        Specification<T> specifications = getSpecification(entity, getDbFilters(entity, filters));
        if (nonNull(specifications)) {
            return findAll(specifications, getPageRequest(pageParam));
        }
        else {
            return findAll(getPageRequest(pageParam));
        }
    }

    protected Specification<T> getSpecification(Class<T> entity, List<DbFilter> filters) {
        Specification<T> specifications = filterGenerator.getSpecificationFromFilters(filters);
        Specification<T> extraCriteria = getExtraCriteria(specifications, entity);
        if (nonNull(extraCriteria)) {
            specifications = isNull(specifications) ? where(extraCriteria) : specifications.and(extraCriteria);
        }
        return specifications;
    }
}
