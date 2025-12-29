package dev.phonchai.datetime.picker;

import java.util.Locale;

public enum PickerLanguage {
    THAI(Locale.forLanguageTag("th-TH")),
    ENGLISH(Locale.ENGLISH);

    /**
     * Optional {@link javax.swing.UIManager} key to define a global default
     * language for newly created pickers.
     * Supported values: {@link PickerLanguage}, {@link Locale}, or a {@link String}
     * like {@code "THAI"} / {@code "ENGLISH"} / {@code "th"} / {@code "en"}.
     */
    public static final String UI_KEY_LANGUAGE = "dev.phonchai.DateTimePicker.language";

    private final Locale locale;

    PickerLanguage(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isThai() {
        return this == THAI;
    }

    public static PickerLanguage fromLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        return "th".equalsIgnoreCase(locale.getLanguage()) ? THAI : ENGLISH;
    }

    public static PickerLanguage fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        String key = normalized.replace('-', '_').toUpperCase(Locale.ROOT);
        if ("TH".equals(key) || "THAI".equals(key) || key.startsWith("TH_")) {
            return THAI;
        }
        if ("EN".equals(key) || "ENGLISH".equals(key) || key.startsWith("EN_")) {
            return ENGLISH;
        }
        try {
            return PickerLanguage.valueOf(key);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static PickerLanguage fromValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof PickerLanguage language) {
            return language;
        }
        if (value instanceof Locale locale) {
            return fromLocale(locale);
        }
        if (value instanceof String s) {
            return fromString(s);
        }
        return null;
    }
}
