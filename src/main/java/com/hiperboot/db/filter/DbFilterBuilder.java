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

import static com.hiperboot.db.filter.LogicalOperator.NOT;
import static com.hiperboot.util.StringUtils.isLikeString;
import static com.hiperboot.util.StringUtils.toCamelCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;

import com.hiperboot.exception.WrongFilterException;
import com.hiperboot.pagination.PageRequestBuilder;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DbFilterBuilder {
    public static List<DbFilter> getDbFilters(Class<?> clazz, final Map<String, Object> mapFilter) {
        return getDbFilters(clazz, mapFilter, LogicalOperator.AND);
    }

    public static List<DbFilter> getDbFilters(Class<?> clazz, final Map<String, Object> mapFilter, LogicalOperator logicalOperator) {
        if (mapFilter.isEmpty()) {
            return new ArrayList<>();
        }
        final var errorList = new ArrayList<String>();
        final var filters = buildFilter(mapFilter, errorList, getFieldList(clazz), logicalOperator);

        if (!errorList.isEmpty()) {
            throw new WrongFilterException(clazz, errorList);
        }
        return filters;
    }

    public static List<DbFilter> buildFilter(Map<String, Object> mapFilter, List<String> errorList, Map<String, Object> fieldList) {
        return buildFilter(mapFilter, errorList, fieldList, LogicalOperator.AND);
    }

    public static List<DbFilter> buildFilter(Map<String, Object> mapFilter, List<String> errorList, Map<String, Object> fieldList,
            LogicalOperator logicalOperator) {
        final var filters = new ArrayList<DbFilter>();
        mapFilter.forEach((key, filterValue) ->
        {
            LogicalOperator logicalWrapper = null;
            if (PageRequestBuilder.PAGE_PAR.equals(key)) {
                log.debug("Empty _page was sent as filter");
            }
            else if (key.equalsIgnoreCase("NOT")) {
                logicalWrapper = NOT;

                LinkedHashMap<String, Object> wrapList = new LinkedHashMap<>();
                if (mapFilter.get(key) instanceof Map) {
                    Map<?, ?> tempMap = (Map<?, ?>) mapFilter.get(key);
                    for (Map.Entry<?, ?> entry : tempMap.entrySet()) {
                        wrapList.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                }
                for (Object oKey : wrapList.keySet()) {
                    processFilterItem(errorList, fieldList, logicalOperator, filters, oKey.toString(), wrapList.get(oKey), logicalWrapper);
                }
            }
            else {
                processFilterItem(errorList, fieldList, logicalOperator, filters, key, filterValue, logicalWrapper);
            }
        });
        return filters;
    }

    private static void processFilterItem(List<String> errorList, Map<String, Object> fieldList, LogicalOperator logicalOperator,
            ArrayList<DbFilter> filters, String key, Object filterValue, LogicalOperator logicalWrapper) {
        key = toCamelCase(key);
        log.trace("Filter attributes: {} : {}", key, filterValue);
        if (isNull(fieldList.get(key))) {
            errorList.add(key);
            return;
        }
        final var filter = DbFilter.builder()
                .field(key)
                .operator(identifyOperator(filterValue))
                .entity(isFieldEntity(fieldList.get(key)))
                .type(getFieldType(fieldList, key))
                .value(filterValue)
                .logicalOperator(logicalOperator)
                .wrappedLogicalOperator(logicalWrapper)
                .controlFlag(getFlags(filterValue))
                .build();

        if ((filter.isEntity() && filterValue instanceof Map) || hasAnnotation(fieldList.get(key).toString(), Entity.class)) {
            filter.setOperator(QueryOperator.JOIN);
        }

        if (!filter.isEntity() && isFieldEntity(filterValue) && !hasAnnotation(fieldList.get(key).toString(), Entity.class)) {
            filter.setOperator(QueryOperator.IN);
            filter.setValue(null);
            var listValues = convertToList(filter, filterValue);
            filter.setValues(listValues);
        }
        else if (filterValue instanceof LinkedHashMap map) {
            betweenFilter(map, filter);
        }
        filters.add(filter);
    }

    private static ArrayList<ControlFlag> getFlags(Object value) {
        if (value instanceof Map map) {
            ArrayList<String> stringList = (ArrayList<String>) map.get("flags");
            if (nonNull(stringList)) {
                return stringList.stream()
                        .map(ControlFlag::valueOf)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }
        return new ArrayList<>();
    }

    private static ArrayList<?> convertToList(DbFilter filter, Object value) {
        ArrayList<String> converted;
        if (isNull(value)) {
            return new ArrayList<>();
        }
        if (value instanceof Map map) {
            converted = new ArrayList<>(map.values());
        }
        else {
            List<String> immutableList = (List<String>) value;
            if (String.class.isAssignableFrom(filter.getType())) {
                converted = new ArrayList<>();
                immutableList.forEach(s -> converted.add(s.toUpperCase()));
            }
            else {
                converted = new ArrayList<>(immutableList);
            }
        }

        if (!((List<?>) converted).isEmpty() && ((((ArrayList) converted).get(0).getClass()).getTypeName()).equals("java.lang.Integer")) {
            return (ArrayList<?>) converted.stream().map(Long::valueOf).collect(Collectors.toList());
        }
        return converted;
    }

    private static QueryOperator identifyOperator(Object value) {
        if (isNull(value)) {
            return QueryOperator.EQUALS;
        }
        return isLikeString(value.toString()) ? QueryOperator.LIKE : QueryOperator.EQUALS;
    }

    private static Class<?> getFieldType(Map<String, Object> fieldList, String key) {
        return isFieldEntity(fieldList.get(key)) ? List.class : (Class<?>) fieldList.get(key);
    }

    private static boolean isFieldEntity(Object field) {
        if (field instanceof Map map) {
            if (map.containsKey("from") || map.containsKey("to")) {
                return false;
            }
        }
        return field instanceof Collection || field instanceof Map || (field instanceof Class<?> clazz && (
                clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Set.class)));
    }

    private static void betweenFilter(LinkedHashMap value, DbFilter filter) {
        var from = value.get("from");
        var to = value.get("to");
        if (nonNull(from) || nonNull(to)) {
            filter.setValues(new ArrayList<>(Arrays.asList(from, to)));
            filter.setOperator(QueryOperator.BETWEEN);
            if (isNull(from)) {
                filter.setOperator(QueryOperator.LESS_THAN);
            }
            else if (isNull(to)) {
                filter.setOperator(QueryOperator.GREATER_THAN);
            }
        }
    }

    private static HashMap<String, Object> getFieldList(Class<?> clazz) {
        var fields = new HashMap<String, Object>();
        List<Class<?>> classes = new ArrayList<>();

        Class<?> cls = clazz;
        do {
            classes.add(cls);
            cls = cls.getSuperclass();
        }
        while (cls != null && !cls.equals(Object.class));

        for (int i = classes.size() - 1; i >= 0; i--) {
            for (Field f : classes.get(i).getDeclaredFields()) {
                if (nonNull(f.getType().getAnnotation(Entity.class))) {
                    fields.put(f.getName(), new ArrayList<>());
                    for (Field nestedField : f.getType().getDeclaredFields()) {
                        ((List) fields.get(f.getName())).add(Pair.of(nestedField.getName(), nestedField.getType()));
                    }
                }
                else {
                    var type = f.getType();
                    if (f.getType().toString().contains(clazz.getCanonicalName())) {
                        type = type.getSuperclass();
                    }
                    fields.put(f.getName(), type);
                }
            }
        }
        return fields;
    }

    public static boolean hasAnnotation(String className, Class<? extends Annotation> annotationClass) {
        try {
            if (className.startsWith("class ")) {
                className = className.substring(6);
            }
            if (className.startsWith("interface ")) {
                className = className.substring(10);
            }
            Class<?> cls = Class.forName(className);
            return cls.isAnnotationPresent(annotationClass);

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.warn("ClassNotFoundException in hasAnnotation.");
            return false;
        }
    }
}
