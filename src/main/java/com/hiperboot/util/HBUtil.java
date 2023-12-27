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
package com.hiperboot.util;

import static com.hiperboot.pagination.PageRequestBuilder.DEFAULT_LIMIT;
import static com.hiperboot.pagination.PageRequestBuilder.LIMIT;
import static com.hiperboot.pagination.PageRequestBuilder.OFFSET;
import static com.hiperboot.pagination.PageRequestBuilder.PAGE_PAR;
import static com.hiperboot.pagination.PageRequestBuilder.SORT;
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

    public static Map<String, Object> columnIsNull(String column) {
        var f = new LinkedHashMap<String, Object>();
        f.put(column, null);
        return f;
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

    public static Map<String, Object> getPageWithStartSizeSort(Integer start, Integer size, String sort) {
        Map<String, Object> pageHead = new HashMap<>();

        Map<String, Object> page = new HashMap<>();
        page.put(LIMIT, isNull(size) ? DEFAULT_LIMIT : size);
        page.put(OFFSET, isNull(start) ? 0 : start);
        if (nonNull(sort)) {
            page.put(SORT, sort);
        }
        pageHead.put(PAGE_PAR, page);
        return pageHead;

    }

    public static Map<String, Object> columnSubEntity(String keyString, Object value) {
        String[] keys = keyString.split("\\.");

        Map<String, Object> currentMap = new LinkedHashMap<>();
        currentMap.put(keys[keys.length - 1], value);

        for (int i = keys.length - 2; i >= 0; i--) {
            currentMap = new LinkedHashMap<>(Map.of(keys[i], currentMap));
        }
        return currentMap;
    }

    public static Map<String, Object> addPageToFilter(Map<String, Object> filter, Map<String, Object> page) {
        var resultFilter = isNull(filter) ? new LinkedHashMap<String, Object>() : new LinkedHashMap<>(filter);
        resultFilter.put(PAGE_PAR, page.get(PAGE_PAR));
        return resultFilter;
    }

}
