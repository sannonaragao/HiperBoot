package com.hiperboot.pagination;

import static com.hiperboot.util.StringUtils.toCamelCase;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.hiperboot.exception.HiperBootException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class PageRequestBuilder {
    private static final int MIN_LIMIT = 1;
    private static final int MIN_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 10000;
    private static final int MAX_LIMIT = 10000000;
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String PAGE_PAR = "_page";
    public static final String SORT = "sort";

    private PageRequestBuilder() {
    }

    public static Map<String, Object> getPagination(Map<String, Object> filter) {
        return extractPagination(new LinkedHashMap<>(filter));
    }
    public static Map<String, Object> extractPagination(Map<String, Object> filter) {
        var page = (Map<String, Object>) filter.remove(PAGE_PAR);
        if (isNull(page)) {
            page = createDefaultPage();
            page.put(SORT, filter.remove(SORT));
        }

        if (isNull(page.get(LIMIT))) {
            page.put(LIMIT, DEFAULT_LIMIT);
        }
        if (isNull(page.get(OFFSET))) {
            page.put(OFFSET, 0);
        }
        if (nonNull(page.get(SORT)) && page.get(SORT) instanceof String s) {
            page.put(SORT, Arrays.asList(s.split(",")));
        }

        return page;
    }

    public static Map<String, Object> createDefaultPage() {
        Map<String, Object> page = new HashMap<>();
        page.put(LIMIT, DEFAULT_LIMIT);
        page.put(OFFSET, MIN_OFFSET);
        Map<String, Object> pageHead = new HashMap<>();
        pageHead.put(PAGE_PAR, page);
        return pageHead;
    }

    public static Pageable getPageRequest(Integer offset, Integer limit) {
        final int validLimit = getValidLimit(limit);
        return new OffsetBasedPageRequest(offset, validLimit, Sort.unsorted());
    }

    public static Pageable getPageRequest(final Map<String, Object> filter) {
        if (isNull(filter)) {
            return null;
        }
        final int limit = (Integer) filter.get(LIMIT);
        final int offset = (Integer) filter.get(OFFSET);
        final List<String> sortingFields = (List<String>) filter.get("sort");
        return getPageRequest(offset, limit, sortingFields);
    }

    public static Pageable getPageRequest(Integer offset, Integer limit, final List<String> sortingFields) {
        final int validLimit = getValidLimit(limit);

        if (nonNull(sortingFields) && !sortingFields.isEmpty()) {
            final List<Sort.Order> sortingOrders =
                    sortingFields.stream().map(PageRequestBuilder::getOrder).toList();
            final List<Sort.Order> orderList = mapSortingFields(sortingOrders);
            return new OffsetBasedPageRequest(offset, validLimit, Sort.by(orderList));
        }
        return new OffsetBasedPageRequest(offset, validLimit, Sort.unsorted());
    }

    private static List<Sort.Order> mapSortingFields(final List<Sort.Order> sortingFields) {
        final List<Sort.Order> mappedSortingFields = new ArrayList<>();
        sortingFields.forEach(order -> mapSortingField(order, mappedSortingFields));

        return mappedSortingFields;
    }

    private static void mapSortingField(final Sort.Order order, final List<Sort.Order> mappedSortingFields) {
        try {
            final Sort.Order mappedOrder = new Sort.Order(order.getDirection(), order.getProperty());
            mappedSortingFields.add(mappedOrder);
        }
        catch (final IllegalArgumentException e) {
            log.error("Wrong sorting parameter " + order.getProperty());
            throw new HiperBootException("Wrong sorting parameter " + order.getProperty());
        }
    }

    private static Sort.Order getOrder(String value) {
        value = toCamelCase(value);
        if (startsWith(value, "-")) {
            return new Sort.Order(DESC, substringAfter(value, "-"));
        }
        else if (startsWith(value, "+")) {
            return new Sort.Order(ASC, substringAfter(value, "+"));
        }
        else {
            return new Sort.Order(ASC, trim(value));
        }
    }

    private static int getValidLimitWithoutMaxRestriction(Integer limit) {
        if (isNull(limit)) {
            return DEFAULT_LIMIT;
        }

        if (limit < MIN_LIMIT) {
            return MIN_LIMIT;
        }

        return limit;
    }

    private static int getValidLimit(Integer limit) {
        if (isNull(limit)) {
            limit = PageRequestBuilder.DEFAULT_LIMIT;
        }
        return Math.min(getValidLimitWithoutMaxRestriction(limit), PageRequestBuilder.MAX_LIMIT);
    }
}
