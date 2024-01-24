package com.hiperboot.db.repository;

import org.springframework.data.jpa.domain.Specification;

public interface ExtraCriteriaStrategy<T> {
    Specification<T> process(Specification<T> specifications, Class<T> type);
}