package dev.phonchai.datetime.picker.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class InputUtils extends MaskFormatter {

    private static Map<Component, OldEditorProperty> inputMap;
    private static final DateTimeFormatter TIME_24H_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final Map<Locale, DateTimeFormatter> TIME_12H_FORMATTERS = new ConcurrentHashMap<>();

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime stringToTime(boolean use24h, String value) {
        return stringToTime(use24h, Locale.ENGLISH, value);
    }

    public static LocalTime stringToTime(boolean use24h, Locale locale, String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = use24h ? TIME_24H_FORMATTER : getTime12hFormatter(locale);
            return LocalTime.from(formatter.parse(trimmed));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static DateTimeFormatter getTime12hFormatter(Locale locale) {
        Locale effectiveLocale = locale != null ? locale : Locale.ENGLISH;
        return TIME_12H_FORMATTERS.computeIfAbsent(effectiveLocale, l -> new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("hh:mm a")
                .toFormatter(l));
    }

    public static LocalDate stringToDate(DateTimeFormatter format, String value) {
        try {
            return LocalDate.from(format.parse(value));
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate[] stringToDate(DateTimeFormatter format, String separator, String value) {
        try {
            int sepIndex = value.indexOf(separator);
            if (sepIndex < 0) {
                return null;
            }
            LocalDate from = LocalDate.from(format.parse(value.substring(0, sepIndex)));
            LocalDate to = LocalDate.from(format.parse(value.substring(sepIndex + separator.length())));
            return new LocalDate[]{from, to};
        } catch (Exception e) {
            return null;
        }
    }

    public static void useTimeInput(JFormattedTextField txt, boolean use24h, ValueCallback callback, InputValidationListener<LocalTime> inputValidationListener) {
        try {
            TimeInputFormat mask = new TimeInputFormat(use24h ? "##:##" : "##:## ??", use24h, Locale.ENGLISH, null, inputValidationListener);
            OldEditorProperty oldEditorProperty = initEditor(txt, mask, callback);

            PropertyChangeListener propertyChangeListener = evt -> callback.valueChanged(txt.getValue());
            txt.addPropertyChangeListener("value", propertyChangeListener);
            oldEditorProperty.propertyChangeListener = propertyChangeListener;
            putPropertyChange(txt, oldEditorProperty);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void useTimeInput(JFormattedTextField txt, boolean use24h, Locale locale, BooleanSupplier isAmSupplier, ValueCallback callback, InputValidationListener<LocalTime> inputValidationListener) {
        try {
            String mark = use24h ? "##:##" : createAmPmMask(locale);
            TimeInputFormat mask = new TimeInputFormat(mark, use24h, locale, isAmSupplier, inputValidationListener);
            OldEditorProperty oldEditorProperty = initEditor(txt, mask, callback);

            if (callback != null) {
                PropertyChangeListener propertyChangeListener = evt -> callback.valueChanged(txt.getValue());
                txt.addPropertyChangeListener("value", propertyChangeListener);
                oldEditorProperty.propertyChangeListener = propertyChangeListener;
            }
            putPropertyChange(txt, oldEditorProperty);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void useDateInput(JFormattedTextField txt, String pattern, boolean between, String separator, ValueCallback callback, InputValidationListener<LocalDate> inputValidationListener) {
        useDateInput(txt, pattern, between, separator, Locale.getDefault(), true, callback, inputValidationListener);
    }

    public static void useDateInput(JFormattedTextField txt, String pattern, boolean between, String separator, Locale locale, boolean convertBuddhistYear, ValueCallback callback, InputValidationListener<LocalDate> inputValidationListener) {
        try {
            String format = datePatternToInputFormat(pattern, "#");
            DateInputFormat mask = new DateInputFormat(between ? format + separator + format : format, between, separator, pattern, locale, convertBuddhistYear, inputValidationListener);
            OldEditorProperty oldEditorProperty = initEditor(txt, mask, callback);

            if (callback != null) {
                PropertyChangeListener propertyChangeListener = evt -> callback.valueChanged(txt.getValue());
                txt.addPropertyChangeListener("value", propertyChangeListener);
                oldEditorProperty.propertyChangeListener = propertyChangeListener;
            }
            putPropertyChange(txt, oldEditorProperty);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void changeTimeFormatted(JFormattedTextField txt, boolean use24h, InputValidationListener<LocalTime> inputValidationListener) {
        try {
            TimeInputFormat mask = new TimeInputFormat(use24h ? "##:##" : "##:## ??", use24h, Locale.ENGLISH, null, inputValidationListener);
            mask.setCommitsOnValidEdit(true);
            mask.setPlaceholderCharacter('-');
            DefaultFormatterFactory df = new DefaultFormatterFactory(mask);
            txt.setFormatterFactory(df);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void changeTimeFormatted(JFormattedTextField txt, boolean use24h, Locale locale, BooleanSupplier isAmSupplier, InputValidationListener<LocalTime> inputValidationListener) {
        try {
            String mark = use24h ? "##:##" : createAmPmMask(locale);
            TimeInputFormat mask = new TimeInputFormat(mark, use24h, locale, isAmSupplier, inputValidationListener);
            mask.setCommitsOnValidEdit(true);
            mask.setPlaceholderCharacter('-');
            DefaultFormatterFactory df = new DefaultFormatterFactory(mask);
            txt.setFormatterFactory(df);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void changeDateFormatted(JFormattedTextField txt, String pattern, boolean between, String separator, InputValidationListener<LocalDate> inputValidationListener) {
        changeDateFormatted(txt, pattern, between, separator, Locale.getDefault(), true, inputValidationListener);
    }

    public static void changeDateFormatted(JFormattedTextField txt, String pattern, boolean between, String separator, Locale locale, boolean convertBuddhistYear, InputValidationListener<LocalDate> inputValidationListener) {
        try {
            String format = datePatternToInputFormat(pattern, "#");
            DateInputFormat mask = new DateInputFormat(between ? format + separator + format : format, between, separator, pattern, locale, convertBuddhistYear, inputValidationListener);
            mask.setCommitsOnValidEdit(true);
            mask.setPlaceholderCharacter('-');
            DefaultFormatterFactory df = new DefaultFormatterFactory(mask);
            txt.setFormatterFactory(df);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String datePatternToInputFormat(String pattern, String rpm) {
        String regex = "[dmy]";
        return pattern.toLowerCase().replaceAll(regex, rpm);
    }

    private static OldEditorProperty initEditor(JFormattedTextField txt, MaskFormatter format, ValueCallback callback) {
        removePropertyChange(txt);
        OldEditorProperty oldEditorProperty = OldEditorProperty.getFromOldEditor(txt);
        format.setCommitsOnValidEdit(true);
        format.setPlaceholderCharacter('-');
        DefaultFormatterFactory df = new DefaultFormatterFactory(format);
        txt.setFormatterFactory(df);

        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txt.putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, (Consumer<?>) o -> {
            txt.setValue(null);
            if (callback != null) {
                callback.valueChanged(null);
            }
        });
        return oldEditorProperty;
    }

    private static void putPropertyChange(JFormattedTextField txt, OldEditorProperty oldEditorProperty) {
        if (inputMap == null) {
            inputMap = new WeakHashMap<>();
        }
        inputMap.put(txt, oldEditorProperty);
    }

    public static void removePropertyChange(JFormattedTextField txt) {
        if (inputMap == null) {
            return;
        }
        OldEditorProperty oldEditorProperty = inputMap.get(txt);
        if (oldEditorProperty != null) {
            oldEditorProperty.removeFromEditor(txt);
            inputMap.remove(txt);
        }
    }

    private static class TimeInputFormat extends MaskFormatter {

        private final InputValidationListener<LocalTime> inputValidationListener;
        private final DateTimeFormatter timeFormat;
        private final BooleanSupplier isAmSupplier;
        private final String amText;
        private final String pmText;
        private final int amPmLen;
        private final boolean use24h;

        public TimeInputFormat(String mark, boolean use24h, Locale locale, BooleanSupplier isAmSupplier, InputValidationListener<LocalTime> inputValidationListener) throws ParseException {
            super(mark);
            this.use24h = use24h;
            this.inputValidationListener = inputValidationListener;
            this.isAmSupplier = isAmSupplier;
            this.timeFormat = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(use24h ? "HH:mm" : "hh:mm a")
                    .toFormatter(locale != null ? locale : Locale.ENGLISH);
            if (use24h) {
                amText = null;
                pmText = null;
                amPmLen = 0;
            } else {
                String[] amPm = java.text.DateFormatSymbols.getInstance(locale != null ? locale : Locale.ENGLISH).getAmPmStrings();
                amText = amPm[0];
                pmText = amPm[1];
                amPmLen = Math.max(amText.length(), pmText.length());
            }
        }

        @Override
        public Object stringToValue(String value) throws ParseException {
            String normalized = normalize(value);
            checkTime(normalized);
            return super.stringToValue(normalized);
        }

        public void checkTime(String value) throws ParseException {
            try {
                LocalTime time = LocalTime.parse(value, timeFormat);

                if (inputValidationListener == null) return;

                // validate time selection able
                if (inputValidationListener.isValidation()) {
                    if (!inputValidationListener.checkSelectionAble(time)) {
                        throw new DateTimeException("error selection able");
                    }
                }
                inputValidationListener.inputChanged(true);
            } catch (DateTimeException e) {
                if (inputValidationListener != null) {
                    inputValidationListener.inputChanged(false);
                }
                throw new ParseException(e.getMessage(), 0);
            }
        }

        private String normalize(String value) {
            if (use24h || value == null) {
                return value;
            }
            int spaceIndex = value.lastIndexOf(' ');
            if (spaceIndex < 0 || spaceIndex + 1 >= value.length()) {
                return value;
            }
            String amPmPart = value.substring(spaceIndex + 1);
            if (!isOnlyPlaceholders(amPmPart)) {
                return value;
            }
            boolean isAm = isAmSupplier == null || isAmSupplier.getAsBoolean();
            String replacement = padOrTrim(isAm ? amText : pmText, amPmPart.length());
            return value.substring(0, spaceIndex + 1) + replacement;
        }

        private boolean isOnlyPlaceholders(String text) {
            if (text.isEmpty()) {
                return true;
            }
            char placeholder = getPlaceholderCharacter();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c != placeholder && c != ' ') {
                    return false;
                }
            }
            return true;
        }

        private static String padOrTrim(String value, int len) {
            if (value == null) {
                return "";
            }
            if (value.length() == len) {
                return value;
            }
            if (value.length() > len) {
                return value.substring(0, len);
            }
            return value + " ".repeat(len - value.length());
        }
    }

    private static class DateInputFormat extends MaskFormatter {

        private final boolean between;
        private final String separator;
        private final DateTimeFormatter strictFormatter;
        private final boolean convertBuddhistYear;
        private final InputValidationListener<LocalDate> inputValidationListener;

        public DateInputFormat(String mark, boolean between, String separator, String pattern, Locale locale, boolean convertBuddhistYear, InputValidationListener<LocalDate> inputValidationListener) throws ParseException {
            super(mark);
            this.between = between;
            this.separator = separator;
            this.inputValidationListener = inputValidationListener;
            this.convertBuddhistYear = convertBuddhistYear;
            this.strictFormatter = ThaiBuddhistDateUtils.createStrictDateFormatter(pattern, locale != null ? locale : Locale.getDefault())
                    .withResolverStyle(ResolverStyle.STRICT);
        }

        @Override
        public Object stringToValue(String value) throws ParseException {
            checkTime(value);
            return super.stringToValue(value);
        }

        public void checkTime(String value) throws ParseException {
            try {
                if (between) {
                    int separatorIndex = value.indexOf(separator);
                    if (separatorIndex < 0) {
                        throw new ParseException("invalid date range", 0);
                    }
                    LocalDate date1 = ThaiBuddhistDateUtils.parseToIsoLocalDate(value.substring(0, separatorIndex), strictFormatter, convertBuddhistYear);
                    LocalDate date2 = ThaiBuddhistDateUtils.parseToIsoLocalDate(value.substring(separatorIndex + separator.length()), strictFormatter, convertBuddhistYear);
                    if (date1 == null || date2 == null) {
                        throw new ParseException("invalid date range", 0);
                    }

                    if (inputValidationListener == null) return;

                    // validate date selection able
                    if (inputValidationListener.isValidation()) {
                        if (!inputValidationListener.checkSelectionAble(date1) ||
                                !inputValidationListener.checkSelectionAble(date2)) {
                            throw new ParseException("error selection able", 0);
                        }
                    }
                } else {
                    LocalDate date = ThaiBuddhistDateUtils.parseToIsoLocalDate(value, strictFormatter, convertBuddhistYear);
                    if (date == null) {
                        throw new ParseException("invalid date", 0);
                    }

                    if (inputValidationListener == null) return;

                    // validate date selection able
                    if (inputValidationListener.isValidation()) {
                        if (!inputValidationListener.checkSelectionAble(date)) {
                            throw new ParseException("error selection able", 0);
                        }
                    }
                }
                inputValidationListener.inputChanged(true);
            } catch (ParseException e) {
                if (inputValidationListener != null) {
                    inputValidationListener.inputChanged(false);
                }
                throw e;
            } catch (Exception e) {
                if (inputValidationListener != null) {
                    inputValidationListener.inputChanged(false);
                }
                throw new ParseException(e.getMessage(), 0);
            }
        }
    }

    private static String createAmPmMask(Locale locale) {
        String[] amPm = java.text.DateFormatSymbols.getInstance(locale != null ? locale : Locale.ENGLISH).getAmPmStrings();
        int len = Math.max(amPm[0].length(), amPm[1].length());
        if (len <= 2 && isSimpleLetters(amPm[0]) && isSimpleLetters(amPm[1])) {
            return "##:## " + "?".repeat(len);
        }
        return "##:## " + "*".repeat(len);
    }

    private static boolean isSimpleLetters(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public interface ValueCallback {
        void valueChanged(Object value);
    }

    private static class OldEditorProperty {

        protected PropertyChangeListener propertyChangeListener;
        protected JFormattedTextField.AbstractFormatterFactory formatter;
        protected Component oldTrailingComponent;
        private boolean isShowClearButton;
        private Consumer<?> clearButtonCallback;
        private String outline;
        protected Object value;
        protected String text;

        protected static OldEditorProperty getFromOldEditor(JFormattedTextField editor) {
            OldEditorProperty oldEditorProperty = new OldEditorProperty();
            oldEditorProperty.formatter = editor.getFormatterFactory();
            oldEditorProperty.oldTrailingComponent = FlatClientProperties.clientProperty(editor, FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, null, Component.class);
            oldEditorProperty.isShowClearButton = FlatClientProperties.clientPropertyBoolean(editor, FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, false);
            oldEditorProperty.clearButtonCallback = FlatClientProperties.clientProperty(editor, FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, null, Consumer.class);
            oldEditorProperty.outline = FlatClientProperties.clientProperty(editor, FlatClientProperties.OUTLINE, null, String.class);
            oldEditorProperty.value = editor.getValue();
            oldEditorProperty.text = editor.getText();
            return oldEditorProperty;
        }

        protected void removeFromEditor(JFormattedTextField editor) {
            if (propertyChangeListener != null) {
                editor.removePropertyChangeListener("value", propertyChangeListener);
            }
            editor.setFormatterFactory(formatter);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, oldTrailingComponent);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, isShowClearButton);
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, clearButtonCallback);
            editor.putClientProperty(FlatClientProperties.OUTLINE, outline);
            editor.setValue(value);
            editor.setText(text);
        }
    }
}
