package dev.phonchai.datetime.picker;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.util.ThaiBuddhistDateUtils;

import javax.swing.*;
import java.time.LocalDate;

/**
 * A convenience component that combines {@link DatePicker} with a
 * {@link JFormattedTextField}.
 * <p>
 * This provides a simplified, ready-to-use date input field with popup picker.
 * <p>
 * <h2>Basic Usage</h2>
 * 
 * <pre>{@code
 * DatePickerField dateField = new DatePickerField();
 * dateField.setPlaceholder("เลือกวันที่");
 * panel.add(dateField);
 * 
 * // Get selected date
 * LocalDate date = dateField.getSelectedDate();
 * }</pre>
 * 
 * <h2>Thai Buddhist Era Support</h2>
 * 
 * <pre>{@code
 * // Get date in Thai Buddhist Era format (พ.ศ.)
 * String thaiDate = dateField.getSelectedDateAsBuddhistEra(); // "24/01/2569"
 * String fullThai = dateField.getSelectedDateAsFullThai(); // "24 มกราคม 2569"
 * String custom = dateField.getSelectedDateAsBuddhistEra("d MMM yyyy"); // "24 ม.ค. 2569"
 * 
 * // Get date in ISO format for API
 * String isoDate = dateField.getSelectedDateAsIso(); // "2026-01-24"
 * }</pre>
 *
 * @author dev.phonchai
 * @version 1.1.0
 * @see DatePicker
 * @see TimePickerField
 * @see ThaiBuddhistDateUtils
 */
public class DatePickerField extends JFormattedTextField {

    private final DatePicker datePicker;

    /**
     * Creates a new DatePickerField with default settings.
     */
    public DatePickerField() {
        this(null);
    }

    /**
     * Creates a new DatePickerField with a placeholder text.
     *
     * @param placeholder the placeholder text to display when empty
     */
    public DatePickerField(String placeholder) {
        this.datePicker = new DatePicker();
        datePicker.setEditor(this);

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
     * Gets the underlying DatePicker instance.
     *
     * @return the DatePicker
     */
    public DatePicker getDatePicker() {
        return datePicker;
    }

    /**
     * Gets the selected date.
     *
     * @return the selected date, or null if no date is selected
     */
    public LocalDate getSelectedDate() {
        return datePicker.getSelectedDate();
    }

    /**
     * Sets the selected date.
     *
     * @param date the date to select
     */
    public void setSelectedDate(LocalDate date) {
        datePicker.setSelectedDate(date);
    }

    /**
     * Clears the selected date.
     */
    public void clearSelectedDate() {
        datePicker.clearSelectedDate();
    }

    /**
     * Sets the date to today.
     */
    public void now() {
        datePicker.now();
    }

    /**
     * Checks if a date is selected.
     *
     * @return true if a date is selected
     */
    public boolean isDateSelected() {
        return datePicker.isDateSelected();
    }

    /**
     * Sets the language for the picker.
     *
     * @param language the language (THAI or ENGLISH)
     */
    public void setLanguage(PickerLanguage language) {
        datePicker.setLanguage(language);
    }

    /**
     * Gets the language.
     *
     * @return the current language
     */
    public PickerLanguage getLanguage() {
        return datePicker.getLanguage();
    }

    /**
     * Sets the date format pattern.
     *
     * @param format the date format pattern (e.g., "dd/MM/yyyy")
     */
    public void setDateFormat(String format) {
        datePicker.setDateFormat(format);
    }

    /**
     * Gets the date format pattern.
     *
     * @return the date format pattern
     */
    public String getDateFormat() {
        return datePicker.getDateFormat();
    }

    // ============================================================
    // THAI BUDDHIST ERA FORMATTING METHODS
    // ============================================================

    /**
     * Gets the selected date formatted in Thai Buddhist Era (พ.ศ.) standard format.
     * <p>
     * Format: "dd/MM/yyyy" with Buddhist Era year (e.g., "24/01/2569")
     * <p>
     * Example:
     * 
     * <pre>{@code
     * datePickerField.setSelectedDate(LocalDate.of(2026, 1, 24));
     * String result = datePickerField.getSelectedDateAsBuddhistEra();
     * // Returns: "24/01/2569"
     * }</pre>
     *
     * @return the selected date in Thai format (e.g., "24/01/2569"), or null if no
     *         date selected
     * @see ThaiBuddhistDateUtils#formatAsThaiDate(LocalDate)
     */
    public String getSelectedDateAsBuddhistEra() {
        return ThaiBuddhistDateUtils.formatAsThaiDate(getSelectedDate());
    }

    /**
     * Gets the selected date formatted in Thai Buddhist Era (พ.ศ.) with custom
     * pattern.
     * <p>
     * The year in the pattern will be automatically converted to Buddhist Era.
     * Uses Thai locale for month names.
     * <p>
     * Pattern examples:
     * 
     * <pre>{@code
     * datePickerField.setSelectedDate(LocalDate.of(2026, 1, 24));
     * 
     * getSelectedDateAsBuddhistEra("dd/MM/yyyy")     // "24/01/2569"
     * getSelectedDateAsBuddhistEra("d MMMM yyyy")    // "24 มกราคม 2569"
     * getSelectedDateAsBuddhistEra("d MMM yy")       // "24 ม.ค. 69"
     * getSelectedDateAsBuddhistEra("EEEE d MMMM yyyy")  // "วันศุกร์ 24 มกราคม 2569"
     * }</pre>
     *
     * @param pattern the date format pattern (e.g., "dd/MM/yyyy", "d MMMM yyyy")
     * @return the selected date in the specified format with Buddhist Era year, or
     *         null if no date selected
     * @see ThaiBuddhistDateUtils#formatAsBuddhistEra(LocalDate, String)
     */
    public String getSelectedDateAsBuddhistEra(String pattern) {
        return ThaiBuddhistDateUtils.formatAsBuddhistEra(getSelectedDate(), pattern);
    }

    /**
     * Gets the selected date formatted in full Thai format with month name.
     * <p>
     * Format: "d MMMM yyyy" with Buddhist Era year and Thai month name.
     * <p>
     * Example:
     * 
     * <pre>{@code
     * datePickerField.setSelectedDate(LocalDate.of(2026, 1, 24));
     * String result = datePickerField.getSelectedDateAsFullThai();
     * // Returns: "24 มกราคม 2569"
     * }</pre>
     *
     * @return the selected date in full Thai format (e.g., "24 มกราคม 2569"), or
     *         null if no date selected
     * @see ThaiBuddhistDateUtils#formatAsThaiDateFull(LocalDate)
     */
    public String getSelectedDateAsFullThai() {
        return ThaiBuddhistDateUtils.formatAsThaiDateFull(getSelectedDate());
    }

    /**
     * Gets the selected date formatted in ISO 8601 format.
     * <p>
     * Format: "yyyy-MM-dd" (e.g., "2026-01-24")
     * <p>
     * This format is suitable for API communication and database storage.
     * <p>
     * Example:
     * 
     * <pre>{@code
     * datePickerField.setSelectedDate(LocalDate.of(2026, 1, 24));
     * String result = datePickerField.getSelectedDateAsIso();
     * // Returns: "2026-01-24"
     * }</pre>
     *
     * @return the selected date in ISO format (e.g., "2026-01-24"), or null if no
     *         date selected
     * @see ThaiBuddhistDateUtils#formatAsIso(LocalDate)
     */
    public String getSelectedDateAsIso() {
        return ThaiBuddhistDateUtils.formatAsIso(getSelectedDate());
    }
}
