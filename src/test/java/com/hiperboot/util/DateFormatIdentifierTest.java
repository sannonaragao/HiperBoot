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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DateFormatIdentifierTest {

    @Test
    public void testIsoDate() {
        assertThat(DateFormatIdentifier.identifyDateFormat("2024-01-18")).isEqualTo(DateFormatType.ISO_DATE);
    }

    @Test
    public void testIsoDateTime() {
        assertThat(DateFormatIdentifier.identifyDateFormat("2024-01-18T15:00:00")).isEqualTo(DateFormatType.ISO_DATETIME);
    }

    @Test
    public void testIsoDateTimeWithTimeZone() {
        assertThat(DateFormatIdentifier.identifyDateFormat("2024-01-18T15:00:00+01:00")).isEqualTo(DateFormatType.ISO_DATETIME_TZ);
    }

    @Test
    public void testIsoDateTimeUtc() {
        assertThat(DateFormatIdentifier.identifyDateFormat("2024-01-18T15:00:00Z")).isEqualTo(DateFormatType.ISO_DATETIME_UTC);
    }

    @Test
    public void testRfc1123() {
        assertThat(DateFormatIdentifier.identifyDateFormat("Thu, 18 Jan 2024 15:00:00 GMT")).isEqualTo(DateFormatType.RFC_1123);
    }

    @Test
    public void testEpochSeconds() {
        assertThat(DateFormatIdentifier.identifyDateFormat("1702992000")).isEqualTo(DateFormatType.EPOCH_SECONDS);
    }

    @Test
    public void testEpochMilliseconds() {
        assertThat(DateFormatIdentifier.identifyDateFormat("1702992000000")).isEqualTo(DateFormatType.EPOCH_MILLISECONDS);
    }

    @Test
    public void testUnidentifiedFormat() {
        assertThat(DateFormatIdentifier.identifyDateFormat("not a date")).isEqualTo(DateFormatType.UNIDENTIFIED);
    }
}
