package br.com.bovbi.embed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {

    public Utils() {
    }

    public static boolean isNullOrEmpty(String obj) {
        return obj == null || obj.length() == 0;
    }

    public static boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static String formatDate(Date date) {
        return isNull(date) ? null : (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(date);
    }

    public static Date stringToDate(String date) {
        try {
            return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).parse(date);
        } catch (ParseException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
