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

public enum DateFormatType {
    ISO_DATE,            // YYYY-MM-DD
    ISO_DATETIME,        // YYYY-MM-DDThh:mm:ss
    ISO_DATETIME_TZ,     // YYYY-MM-DDThh:mm:ss+hh:mm
    ISO_DATETIME_UTC,    // YYYY-MM-DDThh:mm:ssZ
    RFC_1123,            // EEE, dd MMM yyyy HH:mm:ss zzz
    EPOCH_SECONDS,       // Unix timestamp in seconds
    EPOCH_MILLISECONDS,  // Unix timestamp in milliseconds
    SQL_DATETIME,         // Format like "YYYY-MM-DD HH:MM:SS.s"
    UNIDENTIFIED         // Unidentified Format
}