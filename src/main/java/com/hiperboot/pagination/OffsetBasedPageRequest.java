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
package com.hiperboot.pagination;

import java.io.Serial;
import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ToString
@EqualsAndHashCode
public class OffsetBasedPageRequest implements Pageable, Serializable {
    @Serial
    private static final long serialVersionUID = -25822477129613575L;

    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffsetBasedPageRequest(final long offset, final int limit, Sort sort) {
        if (offset < 0) {
            log.error("Offset index must not be less than zero.");
            throw new IllegalArgumentException("Offset index must not be less than zero.");
        }

        if (limit < 1) {
            log.error("Limit must not be less than one.");
            throw new IllegalArgumentException("Limit must not be less than one.");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return Math.toIntExact(offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    private OffsetBasedPageRequest previous() {
        return hasPrevious()
               ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort())
               : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
