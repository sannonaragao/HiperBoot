package com.hiperboot.db.filter;

import static com.hiperboot.db.filter.LogicalOperator.NOT;
import static com.hiperboot.util.StringUtils.isLikeString;
import static com.hiperboot.util.StringUtils.toCamelCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public static List<DbFilter> buildFilter(Map<String, Object> mapFilter, List<String> errorList,
            Map<String, Object> fieldList) {
        return buildFilter(mapFilter, errorList, fieldList, LogicalOperator.AND);
    }

    public static List<DbFilter> buildFilter(Map<String, Object> mapFilter, List<String> errorList, Map<String, Object> fieldList,
            LogicalOperator logicalOperator) {
        final var filters = new ArrayList<DbFilter>();
        mapFilter.forEach((key, value) ->
        {

            LogicalOperator logicalWrapper = null;
            if (PageRequestBuilder.PAGE_PAR.equals(key)) {
                log.debug("Empty _page was sent as filter");
            }
            else if (key.equalsIgnoreCase("NOT")) {
                logicalWrapper = NOT;

//                LinkedHashMap<String, Object> wrapList = (LinkedHashMap) mapFilter.get(key);

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
                processFilterItem(errorList, fieldList, logicalOperator, filters, key, value, logicalWrapper);
            }
        });
        return filters;
    }

    private static void processFilterItem(List<String> errorList, Map<String, Object> fieldList, LogicalOperator logicalOperator,
            ArrayList<DbFilter> filters, String key, Object value, LogicalOperator logicalWrapper) {
        key = toCamelCase(key);
        log.trace("Filter attributes: {} : {}", key, value);
        if (isNull(fieldList.get(key))) {
            errorList.add(key);
            return;
        }
        final var filter = DbFilter.builder()
                .field(key)
                .operator(identifyOperator(value))
                .entity(isFieldEntity(fieldList.get(key)))
                .type(getFieldType(fieldList, key))
                .value(value)
                .logicalOperator(logicalOperator)
                .wrappedLogicalOperator(logicalWrapper)
                .controlFlag(getFlags(value))
                .build();

        //TODO: REMOVE DEPENDENCY FROM THE PACKAGE.  Test moving ENUM inside the .db.entity
        if ((filter.isEntity() && value instanceof Map) || (fieldList.get(key).toString().contains(".db.entity."))) {
            filter.setOperator(QueryOperator.JOIN);
        }

        if (!filter.isEntity() && isFieldEntity(value) && !(fieldList.get(key).toString().contains(".db.entity."))) {
            filter.setOperator(QueryOperator.IN);
            filter.setValue(null);
            var listValues = convertToList(filter, value);
            //            if(String.class.isAssignableFrom(filter.getType())){
            //                for (Object listValue : listValues) {
            //                    listValue = listValue.toString().toUpperCase();
            //                }   //.forEach( v -> v.toString().toUpperCase());
            //            }
            filter.setValues(listValues);
        }
        else if (value instanceof LinkedHashMap map) {
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
        return field instanceof List || field instanceof Map || (field instanceof Class<?> clazz && clazz.isAssignableFrom(List.class));
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
                //                if (BaseEntity.class.isAssignableFrom(f.getType())) {
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
}
