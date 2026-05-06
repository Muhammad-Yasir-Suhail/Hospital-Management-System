package com.hospital.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public class Validator {
    public static String errorMessage = "";

    public static boolean isNotEmpty(String value) {
        boolean ok = value != null && !value.trim().isEmpty();
        errorMessage = ok ? "" : "Field is required.";
        return ok;
    }

    public static boolean isValidPhone(String phone) {
        boolean ok = phone != null && phone.matches("\\d{10,11}");
        errorMessage = ok ? "" : "Phone must be 10 or 11 digits.";
        return ok;
    }

    public static boolean isValidDate(String date) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(date, fmt);
            errorMessage = "";
            return true;
        } catch (Exception e) {
            errorMessage = "Date format must be DD/MM/YYYY.";
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        boolean ok = email == null || email.isBlank() || Pattern.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$", email);
        errorMessage = ok ? "" : "Invalid email format.";
        return ok;
    }

    public static boolean isValidDecimal(String value) {
        boolean ok = value != null && value.matches("\\d+(\\.\\d{1,2})?");
        errorMessage = ok ? "" : "Invalid decimal amount.";
        return ok;
    }
}
