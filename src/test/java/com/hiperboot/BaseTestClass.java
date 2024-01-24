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
package com.hiperboot;

import static java.util.Objects.nonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.SpringBootTest;

import com.hiperboot.container.PostgresContainerConfig;

@SpringBootTest
public class BaseTestClass {

    static {
        PostgresContainerConfig.startPostgres();
    }

    protected static Map<String, Field> getFieldList(Class<?> clazz) {
        var fields = new HashMap<String, Field>();
        List<Class<?>> classes = new ArrayList<>();

        Class<?> cls = clazz;
        do {
            classes.add(cls);
            cls = cls.getSuperclass();
        }
        while (cls != null && !cls.equals(Object.class));

        for (int i = classes.size() - 1; i >= 0; i--) {
            for (Field f : classes.get(i).getDeclaredFields()) {
                fields.put(f.getName(), f);
            }
        }
        return fields;
    }

    protected Object getFieldValue(Object row, Field field) {
        field.setAccessible(true);
        Object v = null;
        try {
            v = field.get(row);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (String.class.isAssignableFrom(field.getType()) && nonNull(v)) {
            v = v.toString();
        }
        return v;
    }
}
