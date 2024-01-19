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

import static com.hiperboot.db.filter.ControlFlag.DATE_TIME_SPLIT;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaSqlDate;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaSqlTimestamp;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaTimeInstant;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaTimeLocalDate;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaTimeLocalDateTime;
import static com.hiperboot.db.filter.DatatypeConverter.toJavaTimeOffsetDateTime;
import static com.hiperboot.db.filter.DbFilterBuilder.buildFilter;
import static com.hiperboot.db.filter.LogicalOperator.AND;
import static com.hiperboot.db.filter.LogicalOperator.NOT;
import static com.hiperboot.util.StringUtils.toCamelCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

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
            case JOIN:
                if (isNull(input.getValue())) {
                    predicate = cb.isNull(rootField);
                }
                else {
                    predicate = getPredicateJoin(input, root, cb);
                }
                break;
            case EQUALS:
                if (isNull(input.getValue())) {
                    predicate = cb.isNull(rootField);
                }
                else {
                    predicate = cb.equal(rootFieldUpper, castToRequiredType(rootFieldType, input.getValue()));
                }
                break;
            case LIKE:
                predicate = cb.like((Expression<String>) rootFieldUpper, input.getValue().toString().toUpperCase());

                break;
            case IN:
                validateTypeByOperation(input);

                if (String.class.isAssignableFrom(rootFieldType)) {
                    predicate = getInPredicate(input, cb, root);
                }
                else {
                    predicate = cb.in(root.get(input.getField())).value(castToList(rootFieldType, (List<String>) input.getValues()));
                }
                break;
            case BETWEEN:
                if (input.getControlFlag().contains(DATE_TIME_SPLIT)) {
                    predicate = cb.and(getBetween(input, cb, rootField, rootFieldType),
                            getDateTimeSplit(input, cb, rootField, rootFieldType));
                }
                else {
                    predicate = getBetween(input, cb, rootField, rootFieldType);
                }
                break;
            case GREATER_THAN:
                if (String.class.isAssignableFrom(rootFieldType)) {
                    predicate = cb.greaterThanOrEqualTo((Expression<String>) rootFieldUpper,
                            cb.literal((String) getFrom(input, rootFieldType)));
                }
                else {
                    predicate = cb.greaterThanOrEqualTo(root.get(input.getField()), getFrom(input, rootFieldType));
                }

                break;
            case LESS_THAN:
                predicate = cb.lessThanOrEqualTo(root.get(input.getField()), getTo(input, rootFieldType));
                break;
            default:
                log.warn("Operation not supported");
                throw new HiperBootException("Operation not supported");
        }
        if (NOT.equals(input.getWrappedLogicalOperator())) {
            return cb.not(predicate);
        }

        return predicate;
    }

    private void validateTypeByOperation(DbFilter input) {
        String ERROR_MSG_OPERATION = "Can't perform a {} operation with a {} for field {}";
        switch (input.getOperator()) {
            case IN:
                if (Boolean.class.isAssignableFrom(input.getType()) || boolean.class.isAssignableFrom(input.getType())) {
                    log.warn(ERROR_MSG_OPERATION, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(
                            String.format("Can't perform a %s operation with a %s for field %s", input.getOperator(), input.getType(),
                                    input.getField()));
                }
                break;
            case GREATER_THAN:
                if ((Enum.class.isAssignableFrom(input.getType())) ||
                    (Boolean.class.isAssignableFrom(input.getType()) || boolean.class.isAssignableFrom(input.getType()))) {
                    log.warn(ERROR_MSG_OPERATION, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(String.format("Can't perform a %s operation with a %s for field %s", input.getOperator(),
                            input.getType(), input.getField()));
                }
                break;
            case LIKE:
                if (!String.class.isAssignableFrom(input.getType())) {
                    log.warn(ERROR_MSG_OPERATION, input.getOperator(), input.getType(), input.getField());
                    throw new HiperBootException(String.format("Can't perform a %s operation with a %s for field %s", input.getOperator(),
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

    private Predicate getDateTimeSplit(DbFilter input, CriteriaBuilder cb, Expression<Comparable> rootField, Class<?> rootFieldType) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        var fromPredicate = cb.greaterThanOrEqualTo(
                cb.function("to_char", String.class, cb.function("SYS_EXTRACT_UTC", Timestamp.class, rootField), cb.literal("HH24:MI:SS")),
                timeFormat.format(getFrom(input, rootFieldType)));

        var toPredicate = cb.lessThanOrEqualTo(
                cb.function("to_char", String.class, cb.function("SYS_EXTRACT_UTC", Timestamp.class, rootField), cb.literal("HH24:MI:SS")),
                timeFormat.format(getTo(input, rootFieldType)));
        return cb.and(fromPredicate, toPredicate);
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

        if (String.class.isAssignableFrom(fieldType)) {
            return stringValue.toUpperCase();
        }
        else if (Double.class.isAssignableFrom(fieldType)) {
            return Double.valueOf(stringValue);
        }
        else if (Integer.class.isAssignableFrom(fieldType)) {
            return safelyParseInteger(stringValue);
        }
        else if (Long.class.isAssignableFrom(fieldType)) {
            return Long.valueOf(stringValue);
        }
        else if (BigDecimal.class.isAssignableFrom(fieldType)) {
            return new BigDecimal(stringValue);
        }
        else if (Enum.class.isAssignableFrom(fieldType)) {
            return (Comparable<?>) castToEnum(fieldType, stringValue);
        }
        else if (LocalDate.class.isAssignableFrom(fieldType)) {
            return toJavaTimeLocalDate(stringValue);
        }
        else if (OffsetDateTime.class.isAssignableFrom(fieldType)) {
            return toJavaTimeOffsetDateTime(stringValue);
        }
        else if (Date.class.isAssignableFrom(fieldType)) {
            return toJavaSqlDate(stringValue);
        }
        else if (Timestamp.class.isAssignableFrom(fieldType)) {
            return toJavaSqlTimestamp(stringValue);
        }
        else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
            return Boolean.parseBoolean(stringValue);
        }
        else if (Instant.class.isAssignableFrom(fieldType)) {
            return toJavaTimeInstant(stringValue);
        }
        else if (Time.class.isAssignableFrom(fieldType)) {
            return Time.valueOf(stringValue);
        }
        else if (LocalDateTime.class.isAssignableFrom(fieldType)) {
            return toJavaTimeLocalDateTime(stringValue);
        }
        else if (Short.class.isAssignableFrom(fieldType)) {
            return Short.valueOf(stringValue);
        }
        else if (Byte.class.isAssignableFrom(fieldType)) {
            return Byte.valueOf(stringValue);
        }
        else if (Character.class.isAssignableFrom(fieldType)) {
            return safelyConvertToCharacter(stringValue);
        }
        else if (Float.class.isAssignableFrom(fieldType)) {
            return Float.valueOf(stringValue);
        }
        else if (BigInteger.class.isAssignableFrom(fieldType)) {
            return new BigInteger(stringValue);
        }
        log.error("Impossible to castToRequiredType. Type {} wasn't found.", fieldType.toString());
        return (Comparable<?>) value;
    }

    private Character safelyConvertToCharacter(String stringValue) {
        if (stringValue != null && !stringValue.isEmpty()) {
            return stringValue.charAt(0);
        }
        else {
            throw new IllegalArgumentException("String is empty, cannot convert to Character");
        }
    }

    private Integer safelyParseInteger(String stringValue) {
        Long longTest = Long.parseLong(stringValue);
        if (longTest.compareTo((long) Integer.MAX_VALUE) > 0) {
            return Integer.MAX_VALUE;
        }
        return Integer.valueOf(stringValue);
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
            System.out.println("Field not found: " + e.getMessage());
        }
        return true;
    }
}
