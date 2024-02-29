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

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.hiperboot.util.DateFormatType;

public class DatatypeConverter {

    private static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
    private static final String UNRECOGNIZED = "Unrecognized date format type";

    private DatatypeConverter() {
    }

    public static Date convertStringToDate(String dateString, DateFormatType formatType) {
        LocalDate date;

        switch (formatType) {
            case ISO_DATE -> date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            case ISO_DATETIME -> date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> date = ZonedDateTime.parse(dateString).toLocalDate();
            case RFC_1123 -> date = ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDate();
            case EPOCH_SECONDS -> date = Instant.ofEpochSecond(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone()).toLocalDate();
            case EPOCH_MILLISECONDS -> date = Instant.ofEpochMilli(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone()).toLocalDate();
            case SQL_DATETIME -> date = LocalDate.parse(dateString.split(" ")[0], DateTimeFormatter.ISO_LOCAL_DATE);
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        }
        return Date.valueOf(date);
    }

    public static Timestamp convertStringToTimestamp(String dateString, DateFormatType formatType) {
        LocalDateTime dateTime;

        switch (formatType) {
            case ISO_DATE -> dateTime = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE).atStartOfDay();
            case ISO_DATETIME -> dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> dateTime = ZonedDateTime.parse(dateString).toLocalDateTime();
            case RFC_1123 -> dateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDateTime();
            case EPOCH_SECONDS -> dateTime = Instant.ofEpochSecond(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone()).toLocalDateTime();
            case EPOCH_MILLISECONDS -> dateTime = Instant.ofEpochMilli(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone()).toLocalDateTime();
            case SQL_DATETIME -> dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_S));
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        }
        return Timestamp.valueOf(dateTime);
    }

    public static Instant convertStringToInstant(String dateString, DateFormatType formatType) {
        return switch (formatType) {
            case ISO_DATE -> LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
                    .atStartOfDay()
                    .toInstant(ZonedDateTime.now().getOffset());
            case ISO_DATETIME -> LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .toInstant(ZonedDateTime.now().getOffset());
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> ZonedDateTime.parse(dateString).toInstant();
            case RFC_1123 -> ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
            case EPOCH_SECONDS -> Instant.ofEpochSecond(Long.parseLong(dateString));
            case EPOCH_MILLISECONDS -> Instant.ofEpochMilli(Long.parseLong(dateString));
            case SQL_DATETIME ->
                    LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_S)).atZone(ZoneId.systemDefault())
                            .toInstant();
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        };
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateString, DateFormatType formatType) {
        return switch (formatType) {
            case ISO_DATE -> LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE).atStartOfDay();
            case ISO_DATETIME -> LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> ZonedDateTime.parse(dateString).toLocalDateTime();
            case RFC_1123 -> ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDateTime();
            case EPOCH_SECONDS -> LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(dateString)), ZoneId.systemDefault());
            case EPOCH_MILLISECONDS -> LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(dateString)), ZoneId.systemDefault());
            case SQL_DATETIME -> LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_S));
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        };
    }

    public static OffsetDateTime convertStringToOffsetDateTime(String dateString, DateFormatType formatType) {
        return switch (formatType) {
            case ISO_DATE -> LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
                    .atStartOfDay()
                    .atOffset(ZoneOffset.UTC);
            case ISO_DATETIME -> DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .parse(dateString, OffsetDateTime::from);
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> ZonedDateTime.parse(dateString).toOffsetDateTime();
            case RFC_1123 -> ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toOffsetDateTime();
            case EPOCH_SECONDS -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(dateString)), ZoneOffset.UTC);
            case EPOCH_MILLISECONDS -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(dateString)), ZoneOffset.UTC);
            case SQL_DATETIME ->
                    LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_S)).atOffset(ZoneOffset.UTC);
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        };
    }

    public static LocalDate convertStringToLocalDate(String dateString, DateFormatType formatType) {
        return switch (formatType) {
            case ISO_DATE -> LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            case ISO_DATETIME -> LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            case ISO_DATETIME_TZ, ISO_DATETIME_UTC -> ZonedDateTime.parse(dateString).toLocalDate();
            case RFC_1123 -> ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDate();
            case EPOCH_SECONDS -> Instant.ofEpochSecond(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone())
                    .toLocalDate();
            case EPOCH_MILLISECONDS -> Instant.ofEpochMilli(Long.parseLong(dateString))
                    .atZone(ZonedDateTime.now().getZone())
                    .toLocalDate();
            case SQL_DATETIME -> LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_S)).toLocalDate();
            default -> throw new IllegalArgumentException(UNRECOGNIZED);
        };
    }
}
