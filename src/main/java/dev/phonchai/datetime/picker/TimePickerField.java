package dev.phonchai.datetime.picker;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.util.ThaiBuddhistDateUtils;

import javax.swing.*;
import java.time.LocalTime;

/**
 * A convenience component that combines {@link TimePicker} with a
 * {@link JFormattedTextField}.
 * <p>
 * This provides a simplified, ready-to-use time input field with popup picker.
 * <p>
 * <h2>Basic Usage</h2>
 * 
 * <pre>{@code
 * TimePickerField timeField = new TimePickerField();
 * timeField.setPlaceholder("เลือกเวลา");
 * timeField.set24HourView(true);
 * panel.add(timeField);
 *
 * // Get selected time
 * LocalTime time = timeField.getSelectedTime();
 * }</pre>
 *
 * <h2>Thai Time Formatting</h2>
 * 
 * <pre>{@code
 * // Get time as string
 * String timeStr = timeField.getSelectedTimeAsString(); // "14:30"
 * String timeThai = timeField.getSelectedTimeAsThai(); // "14:30 น."
 * String custom = timeField.getSelectedTimeAsString("HH:mm:ss"); // "14:30:00"
 * }</pre>
 *
 * @author dev.phonchai
 * @version 1.1.0
 * @see TimePicker
 * @see DatePickerField
 * @see ThaiBuddhistDateUtils
 */
public class TimePickerField extends JFormattedTextField {

    private final TimePicker timePicker;

    /**
     * Creates a new TimePickerField with default settings (12-hour view).
     */
    public TimePickerField() {
        this(null, false);
    }

    /**
     * Creates a new TimePickerField with a placeholder text.
     *
     * @param placeholder the placeholder text to display when empty
     */
    public TimePickerField(String placeholder) {
        this(placeholder, false);
    }

    /**
     * Creates a new TimePickerField with 24-hour view option.
     *
     * @param use24Hour true to use 24-hour format
     */
    public TimePickerField(boolean use24Hour) {
        this(null, use24Hour);
    }

    /**
     * Creates a new TimePickerField with placeholder and 24-hour view option.
     *
     * @param placeholder the placeholder text
     * @param use24Hour   true to use 24-hour format
     */
    public TimePickerField(String placeholder, boolean use24Hour) {
        this.timePicker = new TimePicker();
        timePicker.setEditor(this);
        timePicker.set24HourView(use24Hour);

        if (placeholder != null && !placeholder.isEmpty()) {
            setPlaceholder(placeholder);
        }
    }

    /**
     * Sets the placeholder text.
     *
     * @param placeholder the placeholder text
     */
    public void setPlaceholder(String placeholder) {
        putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
    }

    /**
     * Gets the underlying TimePicker instance.
     *
     * @return the TimePicker
     */
    public TimePicker getTimePicker() {
        return timePicker;
    }

    /**
     * Gets the selected time.
     *
     * @return the selected time, or null if no time is selected
     */
    public LocalTime getSelectedTime() {
        return timePicker.getSelectedTime();
    }

    /**
     * Sets the selected time.
     *
     * @param time the time to select
     */
    public void setSelectedTime(LocalTime time) {
        timePicker.setSelectedTime(time);
    }

    /**
     * Clears the selected time.
     */
    public void clearSelectedTime() {
        timePicker.clearSelectedTime();
    }

    /**
     * Sets the time to now.
     */
    public void now() {
        timePicker.now();
    }

    /**
     * Checks if a time is selected.
     *
     * @return true if a time is selected
     */
    public boolean isTimeSelected() {
        return timePicker.isTimeSelected();
    }

    /**
     * Sets the 24-hour view mode.
     *
     * @param use24Hour true to use 24-hour format, false for 12-hour
     */
    public void set24HourView(boolean use24Hour) {
        timePicker.set24HourView(use24Hour);
    }

    /**
     * Checks if using 24-hour view.
     *
     * @return true if using 24-hour format
     */
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    /**
     * Sets the language for the picker.
     *
     * @param language the language (THAI or ENGLISH)
     */
    public void setLanguage(PickerLanguage language) {
        timePicker.setLanguage(language);
    }

    /**
     * Gets the language.
     *
     * @return the current language
     */
    public PickerLanguage getLanguage() {
        return timePicker.getLanguage();
    }

    // ============================================================
    // TIME FORMATTING METHODS
    // ============================================================

    /**
     * Gets the selected time formatted as a string.
     * <p>
     * Format: "HH:mm" (e.g., "14:30")
     * <p>
     * Example:
     * 
     * <pre>{@code
     * timePickerField.setSelectedTime(LocalTime.of(14, 30));
     * String result = timePickerField.getSelectedTimeAsString();
     * // Returns: "14:30"
     * }</pre>
     *
     * @return the selected time as string (e.g., "14:30"), or null if no time
     *         selected
     * @see ThaiBuddhistDateUtils#formatTime(LocalTime)
     */
    public String getSelectedTimeAsString() {
        return ThaiBuddhistDateUtils.formatTime(getSelectedTime());
    }

    /**
     * Gets the selected time formatted with a custom pattern.
     * <p>
     * Pattern examples:
     * 
     * <pre>{@code
     * timePickerField.setSelectedTime(LocalTime.of(14, 30, 45));
     *
     * getSelectedTimeAsString("HH:mm")      // "14:30"
     * getSelectedTimeAsString("HH:mm:ss")   // "14:30:45"
     * getSelectedTimeAsString("h:mm a")     // "2:30 PM"
     * getSelectedTimeAsString("HH.mm")      // "14.30"
     * }</pre>
     *
     * @param pattern the time format pattern
     * @return the selected time in the specified format, or null if no time
     *         selected
     * @see ThaiBuddhistDateUtils#formatTime(LocalTime, String)
     */
    public String getSelectedTimeAsString(String pattern) {
        return ThaiBuddhistDateUtils.formatTime(getSelectedTime(), pattern);
    }

    /**
     * Gets the selected time formatted with Thai suffix (น.).
     * <p>
     * Format: "HH:mm น." (e.g., "14:30 น.")
     * <p>
     * Example:
     * 
     * <pre>{@code
     * timePickerField.setSelectedTime(LocalTime.of(14, 30));
     * String result = timePickerField.getSelectedTimeAsThai();
     * // Returns: "14:30 น."
     * }</pre>
     *
     * @return the selected time with Thai suffix (e.g., "14:30 น."), or null if no
     *         time selected
     * @see ThaiBuddhistDateUtils#formatTimeWithSuffix(LocalTime)
     */
    public String getSelectedTimeAsThai() {
        return ThaiBuddhistDateUtils.formatTimeWithSuffix(getSelectedTime());
    }
}
