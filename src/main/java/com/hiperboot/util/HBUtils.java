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
import static com.hiperboot.pagination.PageRequestBuilder.createDefaultPage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HBUtils {

    public static class HBUtilBuilder extends LinkedHashMap<String, Object> {

        private HBUtilBuilder(Map<String, Object> initialMap) {
            super(initialMap);
        }

        public static HBUtilBuilder of(Map<String, Object> map) {
            return new HBUtilBuilder(map);
        }

        public static HBUtilBuilder of(String key, Object value) {
            Map<String, Object> map = new HashMap<>();
            map.put(key, value);
            return new HBUtilBuilder(map);
        }

        public void createPage() {
            if (!this.containsKey(PAGE_PAR)) {
                this.putAll(createDefaultPage());
            }
        }

        public HBUtilBuilder sortedBy(String order) {
            createPage();
            Object pageParams = this.get(PAGE_PAR);
            if (pageParams instanceof Map) {
                ((Map) pageParams).put(SORT, order);
            }
            return this;
        }

        public HBUtilBuilder offset(int offset) {
            createPage();
            Object pageParams = this.get(PAGE_PAR);
            if (pageParams instanceof Map) {
                ((Map) pageParams).put(OFFSET, offset);
            }
            return this;
        }

        public HBUtilBuilder limit(int limit) {
            createPage();
            Object pageParams = this.get(PAGE_PAR);
            if (pageParams instanceof Map) {
                ((Map) pageParams).put(LIMIT, limit);
            }
            return this;
        }
    }

    public static HBUtilBuilder sortedBy(String order) {
        var hbutil = new HBUtilBuilder(createDefaultPage());
        return hbutil.sortedBy(order);
    }

    public static HBUtilBuilder offset(int offset) {
        var hbutil = new HBUtilBuilder(createDefaultPage());
        return hbutil.offset(offset);
    }

    public static HBUtilBuilder limit(int limit) {
        var hbutil = new HBUtilBuilder(createDefaultPage());
        return hbutil.limit(limit);
    }

    public static HBUtilBuilder hbEquals(String column, String... values) {
        if (column.contains(".")) {
            return columnSubEntity(column, values);
        }
        if (values.length == 1) {
            return HBUtilBuilder.of(column, values[0]);
        }
        return HBUtilBuilder.of(column, List.of(values));
    }

    public static HBUtilBuilder hbIsNull(String column) {
        return HBUtilBuilder.of(column, null);
    }

    public static HBUtilBuilder hbNotEquals(String column, String... values) {
        if (values.length == 1) {
            return HBUtilBuilder.of("NOT", Map.of(column, values[0]));
        }
        return HBUtilBuilder.of("NOT", Map.of(column, List.of(values)));
    }

    public static HBUtilBuilder greaterThan(String column, String value) {
        return HBUtilBuilder.of(column, new LinkedHashMap<>(hbEquals("from", value)));
    }

    public static HBUtilBuilder smallerThan(String column, String value) {
        return HBUtilBuilder.of(column, new LinkedHashMap<>(hbEquals("to", value)));
    }

    public static HBUtilBuilder between(String column, String from, String to) {
        return HBUtilBuilder.of(column, new LinkedHashMap<>(Map.of("from", from, "to", to)));
    }

    public static HBUtilBuilder columnSubEntity(String keyString, String... values) {
        String[] keys = keyString.split("\\.");

        Map<String, Object> currentMap = new LinkedHashMap<>();

        if (values.length == 1) {
            currentMap.put(keys[keys.length - 1], values[0]);
        }
        else {
            currentMap.put(keys[keys.length - 1], List.of(values));
        }

        for (int i = keys.length - 2; i >= 0; i--) {
            currentMap = new LinkedHashMap<>(Map.of(keys[i], currentMap));
        }
        return HBUtilBuilder.of(currentMap);
    }

    public static boolean pageExists() {
        return true;
    }

    public static Map<String, Object> addPageToFilter(Map<String, Object> filter, Map<String, Object> page) {
        var resultFilter = isNull(filter) ? new LinkedHashMap<String, Object>() : new LinkedHashMap<>(filter);
        resultFilter.put(PAGE_PAR, page.get(PAGE_PAR));
        return resultFilter;
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
}
