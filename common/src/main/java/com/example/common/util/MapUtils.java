package com.example.common.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

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

        } else if (tClass == Boolean.class) {
            result = Boolean.parseBoolean(str);

        } else if (tClass == String.class) {
            result = str;

        } else if (tClass == LocalDateTime.class) {
            try {
                result = LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Cannot parse LocalDateTime: " + item, e);
            }

        } else if (tClass == Date.class) {
            String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "dd/MM/yyyy",
                    "MM/dd/yyyy"
            };

            Date parsedDate = null;
            for (String format : formats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setLenient(false);
                    parsedDate = sdf.parse(str);
                    break;
                } catch (ParseException ignored) {}
            }

            if (parsedDate == null) {
                throw new IllegalArgumentException("Cannot parse Date: " + item);
            }
            result = parsedDate;

        } else {
            throw new IllegalArgumentException("Unsupported type: " + tClass);
        }

        return tClass.cast(result);
    }
}
