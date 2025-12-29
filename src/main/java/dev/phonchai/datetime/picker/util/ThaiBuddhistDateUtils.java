package dev.phonchai.datetime.picker.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class ThaiBuddhistDateUtils {

    public static final int BUDDHIST_YEAR_OFFSET = 543;
    /**
     * Heuristic threshold to detect Buddhist Era year input (e.g. 2568).
     * Any parsed year >= this value will be treated as BE and converted to ISO by subtracting {@link #BUDDHIST_YEAR_OFFSET}.
     */
    public static final int BUDDHIST_YEAR_THRESHOLD = 2400;

    private ThaiBuddhistDateUtils() {
    }

    public static DateTimeFormatter createStrictDateFormatter(String pattern, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.getDefault();
        String normalizedPattern = normalizeStrictPattern(pattern);
        return DateTimeFormatter.ofPattern(normalizedPattern, effectiveLocale).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * When using {@link ResolverStyle#STRICT}, patterns using {@code y} (year-of-era)
     * require an era field to resolve to a {@link LocalDate}. Most UIs use
     * {@code yyyy} without eras, so we normalize {@code y -> u} (proleptic-year)
     * outside of quoted literals to keep strict parsing working.
     */
    private static String normalizeStrictPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return pattern;
        }
        StringBuilder out = new StringBuilder(pattern.length());
        boolean inQuote = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\'') {
                out.append(c);
                // Escaped quote inside a literal: '' -> append and keep current state.
                if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
                    out.append('\'');
                    i++;
                } else {
                    inQuote = !inQuote;
                }
                continue;
            }
            if (!inQuote && c == 'y') {
                out.append('u');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static LocalDate parseToIsoLocalDate(String text, DateTimeFormatter strictFormatter) {
        return parseToIsoLocalDate(text, strictFormatter, true);
    }

    public static LocalDate parseToIsoLocalDate(String text, DateTimeFormatter strictFormatter, boolean convertBuddhistYear) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        LocalDate parsed = LocalDate.from(strictFormatter.parse(trimmed));
        if (convertBuddhistYear) {
            int year = parsed.getYear();
            if (year >= BUDDHIST_YEAR_THRESHOLD) {
                int isoYear = year - BUDDHIST_YEAR_OFFSET;
                return LocalDate.of(isoYear, parsed.getMonthValue(), parsed.getDayOfMonth());
            }
        }
        return parsed;
    }

    public static LocalDate[] parseRangeToIsoLocalDate(String text, String separator, DateTimeFormatter strictFormatter) {
        return parseRangeToIsoLocalDate(text, separator, strictFormatter, true);
    }

    public static LocalDate[] parseRangeToIsoLocalDate(String text, String separator, DateTimeFormatter strictFormatter, boolean convertBuddhistYear) {
        if (text == null) {
            return null;
        }
        if (separator == null || separator.isEmpty()) {
            throw new IllegalArgumentException("separator can't be null or empty");
        }
        int separatorIndex = text.indexOf(separator);
        if (separatorIndex < 0) {
            return null;
        }
        LocalDate from = parseToIsoLocalDate(text.substring(0, separatorIndex), strictFormatter, convertBuddhistYear);
        LocalDate to = parseToIsoLocalDate(text.substring(separatorIndex + separator.length()), strictFormatter, convertBuddhistYear);
        if (from == null || to == null) {
            return null;
        }
        return new LocalDate[]{from, to};
    }
}
