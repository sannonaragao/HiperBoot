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

import com.hiperboot.db.filter.casting.TypeCaster;

public class IntegerTypeCaster implements TypeCaster<Integer> {
    @Override
    public Integer cast(String stringValue) {
        return safelyParseInteger(stringValue);
    }

    private Integer safelyParseInteger(String stringValue) {
        Long longTest = Long.parseLong(stringValue);
        if (longTest.compareTo((long) Integer.MAX_VALUE) > 0) {
            return Integer.MAX_VALUE;
        }
        return Integer.valueOf(stringValue);
    }
}
