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
package com.hiperboot.db.filter;

import static com.hiperboot.db.filter.DbFilterBuilder.buildFilter;
import static com.hiperboot.db.filter.LogicalOperator.AND;
import static com.hiperboot.db.filter.LogicalOperator.NOT;
import static com.hiperboot.util.StringUtils.toCamelCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.hiperboot.db.filter.casting.TypeCaster;
import com.hiperboot.db.filter.casting.TypeCasterFactory;
import com.hiperboot.db.persistence.RetrievalStrategy;
import com.hiperboot.db.persistence.Strategy;
import com.hiperboot.exception.HiperBootException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BaseFilterGenerator<T> {

    private static final Map<Class<?>, TypeCaster<?>> typeCasterMap = TypeCasterFactory.buildTypeCasterMap();

    public Specification<T> getSpecificationFromFilters(List<DbFilter> filters) {
        if (isNull(filters) || filters.isEmpty()) {
            return null;
        }
        Specification<T> specification = null;
        for (DbFilter input : filters) {
            Specification<T> clause = createSpecification(input);
            if (isNull(specification)) {
                specification = where(clause);
            }
            else {
                specification = input.getLogicalOperator().equals(AND) ?
                                specification.and(clause) : specification.or(clause);
            }
        }
        return specification;
    }

    protected Specification<T> createSpecification(DbFilter input) {
        return (root, query, criteriaBuilder) ->
        {
            Expression<Comparable> rootField = root.get(input.getField());
            Class<?> rootFieldType = rootField.getJavaType();
            Expression<?> rootFieldUpper = String.class.isAssignableFrom(input.getType()) ?
                                           criteriaBuilder.upper(root.get(input.getField())) :
                                           root.get(input.getField());

            return getPredicate(input, root, criteriaBuilder, rootField, rootFieldType, rootFieldUpper);
        };
    }

    private Predicate getPredicate(DbFilter input,
            From<T, T> root,
            CriteriaBuilder cb,
            Expression<Comparable> rootField,
            Class<?> rootFieldType,
            Expression<?> rootFieldUpper) {
        Predicate predicate = null;
        validateTypeByOperation(input);
        switch (input.getOperator()) {
            case JOIN -> predicate = isNull(input.getValue()) ? cb.isNull(rootField) : getPredicateJoin(input, root, cb);
            case EQUALS -> predicate = isNull(input.getValue()) ?
                                       cb.isNull(rootField) :
                                       cb.equal(rootFieldUpper, castToRequiredType(rootFieldType, input.getValue()));
            case LIKE -> predicate = cb.like((Expression<String>) rootFieldUpper, input.getValue().toString().toUpperCase());
            case IN -> predicate = (String.class.isAssignableFrom(rootFieldType)) ?
                                   getInPredicate(input, cb, root) :
                                   cb.in(root.get(input.getField())).value(castToList(rootFieldType, (List<String>) input.getValues()));
            case BETWEEN -> predicate = getBetween(input, cb, rootField, rootFieldType);
            case GREATER_THAN -> predicate = (String.class.isAssignableFrom(rootFieldType)) ?
                                             cb.greaterThanOrEqualTo((Expression<String>) rootFieldUpper,
                                                     cb.literal((String) getFrom(input, rootFieldType))) :
                                             cb.greaterThanOrEqualTo(root.get(input.getField()), getFrom(input, rootFieldType));
            case LESS_THAN -> predicate = cb.lessThanOrEqualTo(root.get(input.getField()), getTo(input, rootFieldType));
            default -> {
                log.warn("Operation not supported");
                throw new HiperBootException("Operation not supported");
            }

        }
        if (NOT.equals(input.getWrappedLogicalOperator())) {
            return cb.not(predicate);
        }

        return predicate;
    }

    private void validateTypeByOperation(DbFilter input) {
        final String errorMsgOperation = "Can't perform a {} operation with a {} for field {}";
        switch (input.getOperator()) {
            case IN:
                if (Boolean.class.isAssignableFrom(input.getType()) || boolean.class.isAssignableFrom(input.getType())) {
                    log.warn(errorMsgOperation, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(
                            String.format(errorMsgOperation, input.getOperator(), input.getType(),
                                    input.getField()));
                }
                break;
            case GREATER_THAN, LESS_THAN, BETWEEN:
                if ((Enum.class.isAssignableFrom(input.getType())) ||
                    (Boolean.class.isAssignableFrom(input.getType()) || boolean.class.isAssignableFrom(input.getType()))) {
                    log.warn(errorMsgOperation, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(String.format(errorMsgOperation, input.getOperator(),
                            input.getType(), input.getField()));
                }
                break;
            case JOIN:
                break;
            case EQUALS:
                break;
            case LIKE:
                if (!String.class.isAssignableFrom(input.getType())) {
                    log.warn(errorMsgOperation, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(String.format(errorMsgOperation, input.getOperator(),
                            input.getType(), input.getField()));
                }
                break;
        }
    }

    private CriteriaBuilder.In<String> getInPredicate(DbFilter input, CriteriaBuilder cb, From<T, T> root) {
        CriteriaBuilder.In<String> inClause = cb.in(cb.upper(root.get(input.getField())));

        for (Object item : input.getValues()) {
            inClause.value(cb.upper(cb.literal(item.toString())));
        }
        return inClause;
    }

    private Predicate getBetween(DbFilter input, CriteriaBuilder cb, Expression<Comparable> rootField, Class<?> rootFieldType) {
        return cb.between(rootField, getFrom(input, rootFieldType), getTo(input, rootFieldType));
    }

    private Predicate getPredicateJoin(DbFilter input, From<T, T> root, CriteriaBuilder cb) {
        Join<Object, Object> joinChildren;

        if (fetchData(input.getOriginalClass(), input.getField())) {
            joinChildren = (Join<Object, Object>) root.fetch(input.getField(), JoinType.INNER);
        }
        else {
            joinChildren = root.join(input.getField(), JoinType.INNER);
        }

        var filterMap = (Map<String, Object>) input.getValue();
        var childrenList = new ArrayList<LinkedHashMap<String, Object>>();

        for (Field childField : joinChildren.getModel().getBindableJavaType().getDeclaredFields()) {
            final var match = filterMap.entrySet().stream()
                    .filter(stringObjectEntry -> toCamelCase(stringObjectEntry.getKey()).equals(childField.getName()))
                    .findFirst().orElse(null);
            if (nonNull(match)) {
                final var map = new LinkedHashMap<String, Object>();
                map.put(toCamelCase(match.getKey()), match.getValue());
                childrenList.add(map);
            }
        }

        if (childrenList.isEmpty()) {
            return null;
        }

        var andPredicates = getChildrenPredicates(cb, joinChildren, filterMap, childrenList);
        return input.getLogicalOperator().equals(AND) ?
               cb.and(andPredicates.toArray(new Predicate[0])) : cb.or(andPredicates.toArray(new Predicate[0]));
    }

    private ArrayList<Predicate> getChildrenPredicates(CriteriaBuilder cb,
            Join<Object, Object> joinChildren,
            Map<String, Object> filterMap,
            ArrayList<LinkedHashMap<String, Object>> childrenList) {
        var andPredicates = new ArrayList<Predicate>();
        for (var child : childrenList) {
            for (var childEntry : child.entrySet()) {
                for (var filterEntry : filterMap.entrySet()) {
                    final var filterKey = toCamelCase(filterEntry.getKey());
                    if (filterKey.equals(childEntry.getKey())) {
                        andPredicates.add(addChildrenPredicate(cb, joinChildren, child, filterKey));
                    }
                }
            }
        }
        return andPredicates;
    }

    private Predicate addChildrenPredicate(CriteriaBuilder cb,
            From<Object, Object> joinChildren,
            LinkedHashMap<String, Object> childFilter,
            String field) {
        Expression<Comparable> rootField = joinChildren.get(field);
        Class<?> rootFieldType = rootField.getJavaType();
        var fieldJoinArg = joinChildren.get(field);

        Expression<String> rootFieldUpper = String.class.isAssignableFrom(fieldJoinArg.getJavaType()) ?
                                            cb.upper(joinChildren.get(field)) :
                                            joinChildren.get(field);

        final var errorList = new ArrayList<String>();
        List<DbFilter> filters = buildFilter(childFilter, errorList, new HashMap<>(Map.of(field, rootFieldType)), rootFieldType);

        return getPredicate(filters.get(0), (From<T, T>) joinChildren, cb, rootField, rootFieldType, rootFieldUpper);
    }

    private Comparable<?> castToRequiredType(Class<?> fieldType, Object value) {
        if (isNull(value)) {
            return null;
        }
        String stringValue = value.toString();

        if (Enum.class.isAssignableFrom(fieldType)) {
            return (Comparable<?>) castToEnum(fieldType, stringValue);
        }
        TypeCaster<?> caster = typeCasterMap.get(fieldType);
        if (caster != null) {
            return caster.cast(stringValue);
        }
        log.error("Impossible to castToRequiredType. Type {} wasn't found.", fieldType.toString());
        return (Comparable<?>) value;
    }

    private List<Object> castToList(Class<?> fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (Object s : value) {
            if (s instanceof List) {
                lists.add(castToList(fieldType, (List<String>) s));
            }
            else {
                lists.add(castToRequiredType(fieldType, s));
            }
        }
        return lists;
    }

    private Object castToEnum(Class<?> fieldType, String value) {
        Object[] possibleValues = fieldType.getEnumConstants();

        for (Object possibleValue : possibleValues) {
            if (possibleValue.toString().equalsIgnoreCase(value)) {
                return possibleValue;
            }
        }
        log.warn("Enum type not found.");
        return null;
    }

    private Comparable getFrom(DbFilter input, Class<?> rootFieldType) {
        return castToRequiredType(rootFieldType, input.getValues().get(0));
    }

    private Comparable getTo(DbFilter input, Class<?> rootFieldType) {
        return castToRequiredType(rootFieldType, input.getValues().get(1));
    }

    private boolean fetchData(Class<?> originalClass, String field) {
        try {
            Field classField = originalClass.getDeclaredField(field);
            RetrievalStrategy retrievalStrategy = classField.getAnnotation(RetrievalStrategy.class);

            if (retrievalStrategy != null) {
                return retrievalStrategy.value() == Strategy.FETCH;
            }
        }
        catch (NoSuchFieldException e) {
            log.debug("Field not found: " + e.getMessage());
        }
        return true;
    }
}
