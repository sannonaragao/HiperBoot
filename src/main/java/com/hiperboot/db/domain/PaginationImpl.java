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
package com.hiperboot.db.domain;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PaginationImpl implements Pagination {

    private final Integer currentPage;
    private final Integer totalPages;
    private final Integer pageSize;
    private final Long totalRows;

    @Getter(PRIVATE)
    private final Page<?> page;

    public PaginationImpl(Page<?> page) {
        super();
        this.page = page;
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.pageSize = page.getSize();
        this.totalRows = page.getTotalElements();
    }

    public boolean hasNext() {
        return this.page.getNumber() + 1 < this.page.getTotalPages();
    }

    public boolean hasPrevious() {
        return page.getNumber() > 0;
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean isLast() {
        return !hasNext();
    }

}