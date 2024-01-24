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