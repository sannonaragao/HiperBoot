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

import static com.hiperboot.util.HBUtils.hbEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

class DateAndTimeConversionTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableHiperBootRepository;

    @Test
    void colDateIsoDateTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, hbEquals("colDate", "2024-01-19"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-19");
    }

    @Test
    void colDateIsoDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", "2024-01-18T15:00:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-18");
    }

    @Test
    void colDateIsoDatetimeTzTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", "2024-01-17T15:00:00+01:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-17");
    }

    @Test
    void colDateIsoDatetimeUtcTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", "2024-01-16T14:00:00Z"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-16");
    }

    @Test
    void colDateRfc1123Test() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", "Mon, 15 Jan 2024 15:00:00 GMT"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-15");
    }

    @Test
    void colDateSqlDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", "2024-01-15 15:00:00.0"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo("2024-01-15");
    }

    @Test
    void colTimestampIsoDateTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "2024-01-19"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-19 00:00:00.0"));
    }

    @Test
    void colTimestampIsoDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "2024-01-18T15:00:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-18 15:00:00.0"));
    }

    @Test
    void colTimestampIsoDatetimeTzTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "2024-01-17T15:00:00+01:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-17 15:00:00.0"));
    }

    @Test
    void colTimestampIsoDatetimeUtcTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "2024-01-16T14:00:00Z"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-16 14:00:00.0"));
    }

    @Test
    void colTimestampRfc1123Test() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "Mon, 15 Jan 2024 15:00:00 GMT"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-15 15:00:00.0"));
    }

    @Test
    void colTimestampSqlDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", "2024-01-15 15:00:00.0"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(Timestamp.valueOf("2024-01-15 15:00:00.0"));
    }

    @Test
    void colInstantIsoDateTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class, hbEquals("colInstant", "2024-01-19"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-18T23:00:00Z"));
    }

    @Test
    void colInstantIsoDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", "2024-01-18T15:00:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-18T14:00:00Z"));
    }

    @Test
    void colInstantIsoDatetimeTzTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", "2024-01-17T15:00:00+01:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-17T14:00:00Z"));
    }

    @Test
    void colInstantIsoDatetimeUtcTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", "2024-01-16T13:00:00Z"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-16T13:00:00Z"));
    }

    @Test
    void colInstantRfc1123Test() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", "Mon, 15 Jan 2024 14:00:00 GMT"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-15T14:00:00Z"));
    }

    @Test
    void colInstantSqlDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", "2024-01-15 15:00:00.0"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse("2024-01-15T14:00:00Z"));
    }

    @Test
    void colLocalDateTimeIsoDateTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "2024-01-19"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-19T00:00"));
    }

    @Test
    void colLocalDateTimeIsoDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "2024-01-18T15:00:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-18T15:00"));
    }

    @Test
    void colLocalDateTimeIsoDatetimeTzTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "2024-01-17T15:00:00+01:00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-17T15:00"));
    }

    @Test
    void colLocalDateTimeIsoDatetimeUtcTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "2024-01-16T14:00:00Z"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-16T14:00"));
    }

    @Test
    void colLocalDateTimeRfc1123Test() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "Mon, 15 Jan 2024 15:00:00 GMT"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-15T15:00"));
    }

    @Test
    void colLocalDateTimeSqlDatetimeTest() {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", "2024-01-15 15:00:00.0"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse("2024-01-15T15:00"));
    }
}
