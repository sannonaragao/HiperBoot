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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils
{
    public static String toCamelCase(String field)
    {
        if (!field.contains("_"))
        {
            return field;
        }
        String[] words = field.split("[\\_]+");
        var builder = new StringBuilder();
        for (var i = 0; i < words.length; i++)
        {
            String word = words[i];
            if (i == 0)
            {
                word = word.isEmpty() ? word : word.toLowerCase();
            }
            else
            {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }

    public static boolean isLikeString(String str)
    {
        if (isNull(str))
        {
            return false;
        }
        return (str.startsWith("%") || str.endsWith("%"));
    }

    public static boolean isValidString(String string)
    {
        return nonNull(string) && !string.isEmpty() && !string.isBlank();
    }
}
