package dev.phonchai.datetime.picker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Utility class for Thai Buddhist Era (พ.ศ.) date and time operations.
 * <p>
 * This class provides comprehensive support for:
 * <ul>
 * <li><b>Parsing:</b> Converting Buddhist Era date strings to ISO
 * {@link LocalDate}</li>
 * <li><b>Formatting:</b> Converting ISO dates/times to Thai Buddhist Era
 * format</li>
 * <li><b>DateTime combining:</b> Combining date and time into
 * {@link LocalDateTime}</li>
 * </ul>
 * 
 * <h2>Buddhist Era vs Gregorian Calendar</h2>
 * <p>
 * The Buddhist Era (พ.ศ.) is 543 years ahead of the Gregorian calendar (ค.ศ.).
 * For example:
 * <ul>
 * <li>ค.ศ. 2026 = พ.ศ. 2569</li>
 * <li>ค.ศ. 2000 = พ.ศ. 2543</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <pre>{@code
 * // Format date to Thai Buddhist Era
 * LocalDate date = LocalDate.of(2026, 1, 24);
 * String thaiDate = ThaiBuddhistDateUtils.formatAsThaiDate(date); // "24/01/2569"
 * String fullThai = ThaiBuddhistDateUtils.formatAsThaiDateFull(date); // "24 มกราคม 2569"
 * String custom = ThaiBuddhistDateUtils.formatAsBuddhistEra(date, "d MMM yyyy"); // "24 ม.ค. 2569"
 * 
 * // Format time
 * LocalTime time = LocalTime.of(14, 30);
 * String timeStr = ThaiBuddhistDateUtils.formatTime(time); // "14:30"
 * String timeThai = ThaiBuddhistDateUtils.formatTimeWithSuffix(time); // "14:30 น."
 * 
 * // Combine date and time
 * LocalDateTime dateTime = ThaiBuddhistDateUtils.combine(date, time);
 * String thaiDateTime = ThaiBuddhistDateUtils.formatAsThaiDateTime(dateTime); // "24/01/2569 14:30"
 * }</pre>
 *
 * @author dev.phonchai
 * @version 1.1.0
 * @since 1.0.0
 */
public final class ThaiBuddhistDateUtils {

    // ============================================================
    // CONSTANTS
    // ============================================================

    /**
     * The offset between Buddhist Era (พ.ศ.) and Common Era (ค.ศ.) years.
     * Buddhist Era is 543 years ahead of Common Era.
     * <p>
     * Example: ค.ศ. 2026 + 543 = พ.ศ. 2569
     */
    public static final int BUDDHIST_YEAR_OFFSET = 543;

    /**
     * Heuristic threshold to detect Buddhist Era year input (e.g., 2568).
     * Any parsed year >= this value will be treated as Buddhist Era
     * and converted to ISO by subtracting {@link #BUDDHIST_YEAR_OFFSET}.
     * <p>
     * This allows automatic detection when users input Thai Buddhist Era years.
     */
    public static final int BUDDHIST_YEAR_THRESHOLD = 2400;

    /**
     * Thai locale for formatting month names and other locale-specific elements.
     */
    public static final Locale THAI_LOCALE = Locale.forLanguageTag("th-TH");

    /**
     * Default date format pattern for Thai dates: "dd/MM/yyyy".
     */
    public static final String PATTERN_THAI_DATE = "dd/MM/yyyy";

    /**
     * Full Thai date format pattern: "d MMMM yyyy" (e.g., "24 มกราคม 2569").
     */
    public static final String PATTERN_THAI_DATE_FULL = "d MMMM yyyy";

    /**
     * ISO date format pattern: "yyyy-MM-dd".
     */
    public static final String PATTERN_ISO_DATE = "yyyy-MM-dd";

    /**
     * Default time format pattern: "HH:mm".
     */
    public static final String PATTERN_TIME = "HH:mm";

    /**
     * ISO datetime format pattern: "yyyy-MM-dd'T'HH:mm:ss".
     */
    public static final String PATTERN_ISO_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Thai time suffix: " น." (นาฬิกา).
     */
    public static final String THAI_TIME_SUFFIX = " น.";

    // ============================================================
    // PRIVATE CONSTRUCTOR
    // ============================================================

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ThaiBuddhistDateUtils() {
        // Prevent instantiation
    }

    // ============================================================
    // DATE FORMATTING METHODS
    // ============================================================

    /**
     * Formats a date to Thai Buddhist Era using a custom pattern.
     * <p>
     * The year in the pattern will be automatically converted to Buddhist Era
     * (พ.ศ.).
     * Uses Thai locale for month names.
     * 
     * <h3>Pattern Examples:</h3>
     * 
     * <pre>{@code
     * LocalDate date = LocalDate.of(2026, 1, 24);
     * 
     * formatAsBuddhistEra(date, "dd/MM/yyyy")     // "24/01/2569"
     * formatAsBuddhistEra(date, "d MMMM yyyy")    // "24 มกราคม 2569"
     * formatAsBuddhistEra(date, "d MMM yy")       // "24 ม.ค. 69"
     * formatAsBuddhistEra(date, "EEEE d MMMM yyyy")  // "วันศุกร์ 24 มกราคม 2569"
     * }</pre>
     *
     * @param date    the date to format (in ISO/Gregorian calendar)
     * @param pattern the date format pattern (e.g., "dd/MM/yyyy")
     * @return the formatted date string in Buddhist Era, or null if date is null
     * @see java.time.format.DateTimeFormatter
     */
    public static String formatAsBuddhistEra(LocalDate date, String pattern) {
        return formatAsBuddhistEra(date, pattern, THAI_LOCALE);
    }

    /**
     * Formats a date to Thai Buddhist Era using a custom pattern and locale.
     * <p>
     * The year in the pattern will be automatically converted to Buddhist Era
     * (พ.ศ.).
     *
     * @param date    the date to format (in ISO/Gregorian calendar)
     * @param pattern the date format pattern (e.g., "dd/MM/yyyy")
     * @param locale  the locale to use for formatting (affects month names, etc.)
     * @return the formatted date string in Buddhist Era, or null if date is null
     */
    public static String formatAsBuddhistEra(LocalDate date, String pattern, Locale locale) {
        if (date == null) {
            return null;
        }
        LocalDate buddhistDate = toBuddhistEraDate(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale != null ? locale : THAI_LOCALE);
        return buddhistDate.format(formatter);
    }

    /**
     * Formats a date to Thai standard format: "dd/MM/yyyy" with Buddhist Era year.
     * <p>
     * Example: 2026-01-24 → "24/01/2569"
     *
     * @param date the date to format
     * @return the formatted date string (e.g., "24/01/2569"), or null if date is
     *         null
     */
    public static String formatAsThaiDate(LocalDate date) {
        return formatAsBuddhistEra(date, PATTERN_THAI_DATE);
    }

    /**
     * Formats a date to full Thai format with month name: "d MMMM yyyy".
     * <p>
     * Example: 2026-01-24 → "24 มกราคม 2569"
     *
     * @param date the date to format
     * @return the formatted date string (e.g., "24 มกราคม 2569"), or null if date
     *         is null
     */
    public static String formatAsThaiDateFull(LocalDate date) {
        return formatAsBuddhistEra(date, PATTERN_THAI_DATE_FULL);
    }

    /**
     * Formats a date to ISO 8601 format: "yyyy-MM-dd".
     * <p>
     * Example: 2026-01-24 → "2026-01-24"
     * <p>
     * This format is suitable for API communication and database storage.
     *
     * @param date the date to format
     * @return the formatted ISO date string (e.g., "2026-01-24"), or null if date
     *         is null
     */
    public static String formatAsIso(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    // ============================================================
    // TIME FORMATTING METHODS
    // ============================================================

    /**
     * Formats a time to standard format: "HH:mm".
     * <p>
     * Example: 14:30:45 → "14:30"
     *
     * @param time the time to format
     * @return the formatted time string (e.g., "14:30"), or null if time is null
     */
    public static String formatTime(LocalTime time) {
        return formatTime(time, PATTERN_TIME);
    }

    /**
     * Formats a time using a custom pattern.
     * <p>
     * Examples:
     * 
     * <pre>{@code
     * LocalTime time = LocalTime.of(14, 30, 45);
     * 
     * formatTime(time, "HH:mm")      // "14:30"
     * formatTime(time, "HH:mm:ss")   // "14:30:45"
     * formatTime(time, "h:mm a")     // "2:30 PM"
     * formatTime(time, "HH.mm")      // "14.30"
     * }</pre>
     *
     * @param time    the time to format
     * @param pattern the time format pattern
     * @return the formatted time string, or null if time is null
     */
    public static String formatTime(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatter);
    }

    /**
     * Formats a time with Thai suffix: "HH:mm น.".
     * <p>
     * Example: 14:30 → "14:30 น."
     *
     * @param time the time to format
     * @return the formatted time string with Thai suffix (e.g., "14:30 น."), or
     *         null if time is null
     */
    public static String formatTimeWithSuffix(LocalTime time) {
        if (time == null) {
            return null;
        }
        return formatTime(time, PATTERN_TIME) + THAI_TIME_SUFFIX;
    }

    // ============================================================
    // DATETIME COMBINING METHODS
    // ============================================================

    /**
     * Combines a date and time into a {@link LocalDateTime}.
     * <p>
     * Example:
     * 
     * <pre>{@code
     * LocalDate date = LocalDate.of(2026, 1, 24);
     * LocalTime time = LocalTime.of(14, 30);
     * LocalDateTime dateTime = ThaiBuddhistDateUtils.combine(date, time);
     * // Result: 2026-01-24T14:30
     * }</pre>
     *
     * @param date the date component
     * @param time the time component
     * @return the combined LocalDateTime, or null if either date or time is null
     */
    public static LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null;
        }
        return LocalDateTime.of(date, time);
    }

    // ============================================================
    // DATETIME FORMATTING METHODS
    // ============================================================

    /**
     * Formats a datetime using a custom pattern with Buddhist Era year.
     * <p>
     * The year in the pattern will be automatically converted to Buddhist Era
     * (พ.ศ.).
     * Uses Thai locale for month names.
     * 
     * <h3>Pattern Examples:</h3>
     * 
     * <pre>{@code
     * LocalDateTime dt = LocalDateTime.of(2026, 1, 24, 14, 30);
     * 
     * formatAsBuddhistEra(dt, "dd/MM/yyyy HH:mm")       // "24/01/2569 14:30"
     * formatAsBuddhistEra(dt, "d MMMM yyyy HH:mm น.")   // "24 มกราคม 2569 14:30 น."
     * formatAsBuddhistEra(dt, "d MMM yy, HH:mm")        // "24 ม.ค. 69, 14:30"
     * }</pre>
     *
     * @param dateTime the datetime to format
     * @param pattern  the datetime format pattern
     * @return the formatted datetime string in Buddhist Era, or null if dateTime is
     *         null
     */
    public static String formatAsBuddhistEra(LocalDateTime dateTime, String pattern) {
        return formatAsBuddhistEra(dateTime, pattern, THAI_LOCALE);
    }

    /**
     * Formats a datetime using a custom pattern with Buddhist Era year and
     * specified locale.
     *
     * @param dateTime the datetime to format
     * @param pattern  the datetime format pattern
     * @param locale   the locale to use for formatting
     * @return the formatted datetime string in Buddhist Era, or null if dateTime is
     *         null
     */
    public static String formatAsBuddhistEra(LocalDateTime dateTime, String pattern, Locale locale) {
        if (dateTime == null) {
            return null;
        }
        LocalDateTime buddhistDateTime = toBuddhistEraDateTime(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale != null ? locale : THAI_LOCALE);
        return buddhistDateTime.format(formatter);
    }

    /**
     * Formats a datetime to Thai standard format: "dd/MM/yyyy HH:mm" with Buddhist
     * Era year.
     * <p>
     * Example: 2026-01-24T14:30:00 → "24/01/2569 14:30"
     *
     * @param dateTime the datetime to format
     * @return the formatted datetime string (e.g., "24/01/2569 14:30"), or null if
     *         dateTime is null
     */
    public static String formatAsThaiDateTime(LocalDateTime dateTime) {
        return formatAsBuddhistEra(dateTime, PATTERN_THAI_DATE + " " + PATTERN_TIME);
    }

    /**
     * Formats a datetime to Thai format with time suffix: "dd/MM/yyyy HH:mm น.".
     * <p>
     * Example: 2026-01-24T14:30:00 → "24/01/2569 14:30 น."
     *
     * @param dateTime the datetime to format
     * @return the formatted datetime string with suffix (e.g., "24/01/2569 14:30
     *         น."), or null if dateTime is null
     */
    public static String formatAsThaiDateTimeWithSuffix(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return formatAsThaiDateTime(dateTime) + THAI_TIME_SUFFIX;
    }

    /**
     * Formats a datetime to ISO 8601 format: "yyyy-MM-dd'T'HH:mm:ss".
     * <p>
     * Example: 2026-01-24T14:30:45 → "2026-01-24T14:30:45"
     * <p>
     * This format is suitable for API communication and database storage.
     *
     * @param dateTime the datetime to format
     * @return the formatted ISO datetime string, or null if dateTime is null
     */
    public static String formatAsIsoDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(PATTERN_ISO_DATETIME));
    }

    // ============================================================
    // BUDDHIST ERA CONVERSION HELPERS
    // ============================================================

    /**
     * Converts an ISO/Gregorian date to Buddhist Era date.
     * <p>
     * This adds {@link #BUDDHIST_YEAR_OFFSET} (543) to the year.
     * Only the year is adjusted; month and day remain the same.
     * <p>
     * Example: 2026-01-24 → 2569-01-24
     *
     * @param isoDate the ISO/Gregorian date
     * @return the date with Buddhist Era year, or null if isoDate is null
     */
    public static LocalDate toBuddhistEraDate(LocalDate isoDate) {
        if (isoDate == null) {
            return null;
        }
        return isoDate.plusYears(BUDDHIST_YEAR_OFFSET);
    }

    /**
     * Converts an ISO/Gregorian datetime to Buddhist Era datetime.
     * <p>
     * This adds {@link #BUDDHIST_YEAR_OFFSET} (543) to the year.
     * Only the year is adjusted; other fields remain the same.
     *
     * @param isoDateTime the ISO/Gregorian datetime
     * @return the datetime with Buddhist Era year, or null if isoDateTime is null
     */
    public static LocalDateTime toBuddhistEraDateTime(LocalDateTime isoDateTime) {
        if (isoDateTime == null) {
            return null;
        }
        return isoDateTime.plusYears(BUDDHIST_YEAR_OFFSET);
    }

    /**
     * Converts a Buddhist Era date back to ISO/Gregorian date.
     * <p>
     * This subtracts {@link #BUDDHIST_YEAR_OFFSET} (543) from the year.
     * <p>
     * Example: 2569-01-24 → 2026-01-24
     *
     * @param buddhistDate the Buddhist Era date
     * @return the ISO/Gregorian date, or null if buddhistDate is null
     */
    public static LocalDate toIsoDate(LocalDate buddhistDate) {
        if (buddhistDate == null) {
            return null;
        }
        return buddhistDate.minusYears(BUDDHIST_YEAR_OFFSET);
    }

    /**
     * Converts a Buddhist Era datetime back to ISO/Gregorian datetime.
     * <p>
     * This subtracts {@link #BUDDHIST_YEAR_OFFSET} (543) from the year.
     *
     * @param buddhistDateTime the Buddhist Era datetime
     * @return the ISO/Gregorian datetime, or null if buddhistDateTime is null
     */
    public static LocalDateTime toIsoDateTime(LocalDateTime buddhistDateTime) {
        if (buddhistDateTime == null) {
            return null;
        }
        return buddhistDateTime.minusYears(BUDDHIST_YEAR_OFFSET);
    }

    // ============================================================
    // PARSING METHODS (EXISTING)
    // ============================================================

    /**
     * Creates a strict date formatter with the given pattern and locale.
     * <p>
     * This method normalizes the pattern to use proleptic year ('u') instead of
     * year-of-era ('y') to ensure strict parsing works correctly without era
     * information.
     *
     * @param pattern the date format pattern
     * @param locale  the locale for the formatter (null uses default locale)
     * @return a strict DateTimeFormatter
     */
    public static DateTimeFormatter createStrictDateFormatter(String pattern, Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.getDefault();
        String normalizedPattern = normalizeStrictPattern(pattern);
        return DateTimeFormatter.ofPattern(normalizedPattern, effectiveLocale).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * When using {@link ResolverStyle#STRICT}, patterns using {@code y}
     * (year-of-era)
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

    /**
     * Parses a date string to ISO {@link LocalDate}, auto-detecting Buddhist Era
     * input.
     * <p>
     * If the parsed year is >= {@link #BUDDHIST_YEAR_THRESHOLD} (2400), it's
     * assumed
     * to be a Buddhist Era year and is automatically converted to ISO year by
     * subtracting {@link #BUDDHIST_YEAR_OFFSET}.
     *
     * @param text            the date string to parse
     * @param strictFormatter the formatter to use for parsing
     * @return the parsed LocalDate in ISO calendar, or null if text is empty/null
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parseToIsoLocalDate(String text, DateTimeFormatter strictFormatter) {
        return parseToIsoLocalDate(text, strictFormatter, true);
    }

    /**
     * Parses a date string to ISO {@link LocalDate}.
     *
     * @param text                the date string to parse
     * @param strictFormatter     the formatter to use for parsing
     * @param convertBuddhistYear if true, automatically detects and converts
     *                            Buddhist Era years
     * @return the parsed LocalDate in ISO calendar, or null if text is empty/null
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parseToIsoLocalDate(String text, DateTimeFormatter strictFormatter,
            boolean convertBuddhistYear) {
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

    /**
     * Parses a date range string to an array of two ISO {@link LocalDate}s.
     * <p>
     * Example: "24/01/2569 - 31/01/2569" → [2026-01-24, 2026-01-31]
     *
     * @param text            the date range string to parse
     * @param separator       the separator between dates (e.g., " - ")
     * @param strictFormatter the formatter to use for parsing
     * @return an array of two LocalDates [from, to], or null if parsing fails
     * @throws IllegalArgumentException if separator is null or empty
     */
    public static LocalDate[] parseRangeToIsoLocalDate(String text, String separator,
            DateTimeFormatter strictFormatter) {
        return parseRangeToIsoLocalDate(text, separator, strictFormatter, true);
    }

    /**
     * Parses a date range string to an array of two ISO {@link LocalDate}s.
     *
     * @param text                the date range string to parse
     * @param separator           the separator between dates (e.g., " - ")
     * @param strictFormatter     the formatter to use for parsing
     * @param convertBuddhistYear if true, automatically detects and converts
     *                            Buddhist Era years
     * @return an array of two LocalDates [from, to], or null if parsing fails
     * @throws IllegalArgumentException if separator is null or empty
     */
    public static LocalDate[] parseRangeToIsoLocalDate(String text, String separator, DateTimeFormatter strictFormatter,
            boolean convertBuddhistYear) {
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
        LocalDate to = parseToIsoLocalDate(text.substring(separatorIndex + separator.length()), strictFormatter,
                convertBuddhistYear);
        if (from == null || to == null) {
            return null;
        }
        return new LocalDate[] { from, to };
    }
}
