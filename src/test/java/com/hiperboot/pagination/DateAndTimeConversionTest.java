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
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.repository.hiperboot.ParentTableHiperBootRepository;

class DateAndTimeConversionTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository parentTableHiperBootRepository;

    private static Stream<Arguments> dateTimeArguments() {
        return Stream.of(
                Arguments.of("2024-01-19", "2024-01-19T00:00"),
                Arguments.of("2024-01-18T15:00:00", "2024-01-18T15:00"),
                Arguments.of("2024-01-17T15:00:00+01:00", "2024-01-17T15:00"),
                Arguments.of("2024-01-16T14:00:00Z", "2024-01-16T14:00"),
                Arguments.of("Mon, 15 Jan 2024 15:00:00 GMT", "2024-01-15T15:00"),
                Arguments.of("2024-01-15 15:00:00.0", "2024-01-15T15:00")
        );
    }

    private static Stream<Arguments> instantArguments() {
        return Stream.of(
                Arguments.of("2024-01-19", "2024-01-18T23:00:00Z"),
                Arguments.of("2024-01-18T15:00:00", "2024-01-18T14:00:00Z"),
                Arguments.of("2024-01-17T15:00:00+01:00", "2024-01-17T14:00:00Z"),
                Arguments.of("2024-01-16T13:00:00Z", "2024-01-16T13:00:00Z"),
                Arguments.of("Mon, 15 Jan 2024 14:00:00 GMT", "2024-01-15T14:00:00Z"),
                Arguments.of("2024-01-15 15:00:00.0", "2024-01-15T14:00:00Z")
        );
    }

    private static Stream<Arguments> dateArguments() {
        return Stream.of(
                Arguments.of("2024-01-19", "2024-01-19"),
                Arguments.of("2024-01-18T15:00:00", "2024-01-18"),
                Arguments.of("2024-01-17T15:00:00+01:00", "2024-01-17"),
                Arguments.of("2024-01-16T14:00:00Z", "2024-01-16"),
                Arguments.of("Mon, 15 Jan 2024 15:00:00 GMT", "2024-01-15"),
                Arguments.of("2024-01-15 15:00:00.0", "2024-01-15")
        );
    }

    private static Stream<Arguments> timestampArguments() {
        return Stream.of(
                Arguments.of("2024-01-19", Timestamp.valueOf("2024-01-19 00:00:00.0")),
                Arguments.of("2024-01-18T15:00:00", Timestamp.valueOf("2024-01-18 15:00:00.0")),
                Arguments.of("2024-01-17T15:00:00+01:00", Timestamp.valueOf("2024-01-17 15:00:00.0")),
                Arguments.of("2024-01-16T14:00:00Z", Timestamp.valueOf("2024-01-16 14:00:00.0")),
                Arguments.of("Mon, 15 Jan 2024 15:00:00 GMT", Timestamp.valueOf("2024-01-15 15:00:00.0")),
                Arguments.of("2024-01-15 15:00:00.0", Timestamp.valueOf("2024-01-15 15:00:00.0"))
        );
    }

    @ParameterizedTest
    @MethodSource("dateTimeArguments")
    void colLocalDateTimeTest(String input, String expected) {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colLocalDateTime", input));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColLocalDateTime()).isEqualTo(LocalDateTime.parse(expected));
    }

    @ParameterizedTest
    @MethodSource("instantArguments")
    void colInstantTest(String input, String expected) {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colInstant", input));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColInstant()).isEqualTo(Instant.parse(expected));
    }

    @ParameterizedTest
    @MethodSource("dateArguments")
    void colDateTest(String input, String expected) {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colDate", input));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColDate()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("timestampArguments")
    void colTimestampTest(String input, Timestamp expected) {
        List<ParentTable> results = parentTableHiperBootRepository.hiperBootFilter(ParentTable.class,
                hbEquals("colTimestamp", input));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getColTimestamp()).isEqualTo(expected);
    }
}
