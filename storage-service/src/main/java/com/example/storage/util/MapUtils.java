package com.example.storage.util;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MapUtils {

    public static <T> T getObject(Object item, Class<T> tClass) {
        if (item == null) return null;

        String str = item.toString().trim();
        if (str.isEmpty()) return null;

        Object result = null;
        if (tClass == Long.class) {
            result = Long.valueOf(str);
        } else if (tClass == Integer.class) {
            result = Integer.valueOf(str);
        } else if (tClass == BigInteger.class) {
            result = new BigInteger(str);
        }
        else if (tClass == Boolean.class) {
            result = Boolean.parseBoolean(str);
        }else if (tClass == String.class) {
            result = str;
        }else if (tClass == LocalDateTime.class) {
            if (item != null && !"".equals(item.toString())) {
                try {
                    result = LocalDateTime.parse(item.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Cannot parse LocalDateTime: " + item, e);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported type: " + tClass);
        }

        return tClass.cast(result);
    }
}
