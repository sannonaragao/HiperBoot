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
package com.hiperboot.db.filter.casting.type;

import static com.hiperboot.db.filter.DatatypeConverter.convertStringToLocalDate;

import java.time.LocalDate;

import com.hiperboot.db.filter.casting.TypeCaster;
import com.hiperboot.util.DateFormatIdentifier;

public class LocalDateTypeCaster implements TypeCaster<LocalDate> {
    @Override
    public LocalDate cast(String stringValue) {
        return convertStringToLocalDate(stringValue, DateFormatIdentifier.identifyDateFormat(stringValue));
    }
}
