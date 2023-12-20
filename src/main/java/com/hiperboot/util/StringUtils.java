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
