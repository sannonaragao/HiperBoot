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
