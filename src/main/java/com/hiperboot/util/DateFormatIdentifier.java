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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DateFormatIdentifier {

    public static DateFormatType identifyDateFormat(String dateString) {
        // ISO_DATE
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
            return DateFormatType.ISO_DATE;
        }
        catch (DateTimeParseException ignored) {
        }
        // ISO_DATETIME
        try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return DateFormatType.ISO_DATETIME;
        }
        catch (DateTimeParseException ignored) {
        }
        // ISO_DATETIME_UTC
        try {
            if (dateString.endsWith("Z")) {
                ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                return DateFormatType.ISO_DATETIME_UTC;
            }
        }
        catch (DateTimeParseException ignored) {
        }
        // ISO_DATETIME_TZ
        try {
            ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            return DateFormatType.ISO_DATETIME_TZ;
        }
        catch (DateTimeParseException ignored) {
        }
        // EPOCH_SECONDS and EPOCH_MILLISECONDS
        try {
            long epoch = Long.parseLong(dateString);
            if (String.valueOf(epoch).length() <= 10) {
                return DateFormatType.EPOCH_SECONDS;
            }
            else {
                return DateFormatType.EPOCH_MILLISECONDS;
            }
        }
        catch (NumberFormatException ignored) {
        }
        // Check for SQL_DATETIME format
        if (dateString.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d")) {
            return DateFormatType.SQL_DATETIME;
        }
        // RFC_1123
        try {
            DateTimeFormatter rfcFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;
            ZonedDateTime.parse(dateString, rfcFormatter);
            return DateFormatType.RFC_1123;
        }
        catch (DateTimeParseException exception) {
            log.warn(exception.getMessage());
        }
        return DateFormatType.UNIDENTIFIED;
    }
}
