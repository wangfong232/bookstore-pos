/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author qp
 */
public class DateUtil {

    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static String format(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.format(DATE_TIME_FMT);
    }

    public static String format(LocalDate d) {
        if (d == null) {
            return "";
        }
        return d.format(DATE_FMT);
    }

    public static String format(LocalDateTime dt, String pattern) {
        if (dt == null) {
            return "";
        }
        return dt.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate d, String pattern) {
        if (d == null) {
            return "";
        }
        return d.format(DateTimeFormatter.ofPattern(pattern));
    }
}
