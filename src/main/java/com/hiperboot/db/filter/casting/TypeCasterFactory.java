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
package com.hiperboot.db.filter.casting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hiperboot.db.filter.casting.type.BigDecimalTypeCaster;
import com.hiperboot.db.filter.casting.type.BigIntegerTypeCaster;
import com.hiperboot.db.filter.casting.type.BooleanTypeCaster;
import com.hiperboot.db.filter.casting.type.ByteTypeCaster;
import com.hiperboot.db.filter.casting.type.CharacterTypeCaster;
import com.hiperboot.db.filter.casting.type.DoubleTypeCaster;
import com.hiperboot.db.filter.casting.type.FloatTypeCaster;
import com.hiperboot.db.filter.casting.type.InstantTypeCaster;
import com.hiperboot.db.filter.casting.type.IntegerTypeCaster;
import com.hiperboot.db.filter.casting.type.JavaSqlDateTypeCaster;
import com.hiperboot.db.filter.casting.type.JavaSqlTimestampTypeCaster;
import com.hiperboot.db.filter.casting.type.LocalDateTimeTypeCaster;
import com.hiperboot.db.filter.casting.type.LocalDateTypeCaster;
import com.hiperboot.db.filter.casting.type.LongTypeCaster;
import com.hiperboot.db.filter.casting.type.OffsetDateTimeTypeCaster;
import com.hiperboot.db.filter.casting.type.ShortTypeCaster;
import com.hiperboot.db.filter.casting.type.StringTypeCaster;
import com.hiperboot.db.filter.casting.type.TimeTypeCaster;
import com.hiperboot.db.filter.casting.type.UUIDTypeCaster;

public class TypeCasterFactory {

    private TypeCasterFactory() {

    }

    public static Map<Class<?>, TypeCaster<? extends Comparable>> buildTypeCasterMap() {
        Map<Class<?>, TypeCaster<?>> typeCasters = new HashMap<>();

        typeCasters.put(String.class, new StringTypeCaster());
        typeCasters.put(Double.class, new DoubleTypeCaster());
        typeCasters.put(Integer.class, new IntegerTypeCaster());
        typeCasters.put(Long.class, new LongTypeCaster());
        typeCasters.put(BigDecimal.class, new BigDecimalTypeCaster());
        typeCasters.put(LocalDate.class, new LocalDateTypeCaster());
        typeCasters.put(OffsetDateTime.class, new OffsetDateTimeTypeCaster());
        typeCasters.put(Date.class, new JavaSqlDateTypeCaster());
        typeCasters.put(Timestamp.class, new JavaSqlTimestampTypeCaster());
        typeCasters.put(Boolean.class, new BooleanTypeCaster());
        typeCasters.put(boolean.class, new BooleanTypeCaster());
        typeCasters.put(Instant.class, new InstantTypeCaster());
        typeCasters.put(Time.class, new TimeTypeCaster());
        typeCasters.put(LocalDateTime.class, new LocalDateTimeTypeCaster());
        typeCasters.put(Short.class, new ShortTypeCaster());
        typeCasters.put(Byte.class, new ByteTypeCaster());
        typeCasters.put(Character.class, new CharacterTypeCaster());
        typeCasters.put(Float.class, new FloatTypeCaster());
        typeCasters.put(BigInteger.class, new BigIntegerTypeCaster());
        typeCasters.put(UUID.class, new UUIDTypeCaster());
        return typeCasters;
    }
}