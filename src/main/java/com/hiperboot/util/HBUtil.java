package com.hiperboot.util;

import static com.hiperboot.pagination.PageRequestBuilder.DEFAULT_LIMIT;
import static com.hiperboot.pagination.PageRequestBuilder.LIMIT;
import static com.hiperboot.pagination.PageRequestBuilder.OFFSET;
import static com.hiperboot.pagination.PageRequestBuilder.PAGE_PAR;
import static com.hiperboot.pagination.PageRequestBuilder.SORT;
import static com.hiperboot.pagination.PageRequestBuilder.createDefaultPage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HBUtil {

    private HBUtil() {

    }

    public static Map<String, Object> columnEqualValues(String column, String... values) {
        if (values.length == 1) {
            return Map.of(column, values[0]);
        }
        return Map.of(column, List.of(values));
    }

    public static Map<String, Object> columnNotEqualValues(String column, String... values) {
        if (values.length == 1) {
            return Map.of("NOT", Map.of(column, values[0]));
        }
        return Map.of("NOT", Map.of(column, List.of(values)));
    }

    public static Map<String, Object> columnGreaterThan(String column, String value) {

        return new LinkedHashMap<>(Map.of(column, new LinkedHashMap<>(columnEqualValues("from", value))));
    }

    public static Map<String, Object> columnSmallerThan(String column, String value) {

        return new LinkedHashMap<>(Map.of(column, new LinkedHashMap<>(columnEqualValues("to", value))));
    }

    public static Map<String, Object> columnBetween(String column, String from, String to) {

        return new LinkedHashMap<>(Map.of(column, new LinkedHashMap<>(Map.of("from", from, "to", to))));
    }
    public static Map<String, Object> getPageWithStartSizeSort(Integer start, Integer size, String sort){
        Map<String, Object> pageHead = new HashMap<>();

        Map<String, Object> page = new HashMap<>();
        page.put(LIMIT, isNull(size) ? DEFAULT_LIMIT : size);
        page.put(OFFSET, isNull(start) ? 0 : start);
        if( nonNull(sort)){
            page.put(SORT, sort);
        }
        pageHead.put(PAGE_PAR, page);
        return pageHead;

    }

    public  static Map<String, Object> addPageToFilter(Map<String, Object> filter, Map<String, Object> page){
        var resultFilter = isNull(filter) ? new LinkedHashMap<String, Object>(): new LinkedHashMap<>(filter);
        resultFilter.put(PAGE_PAR, page.get(PAGE_PAR));
        return resultFilter;
    }

}
