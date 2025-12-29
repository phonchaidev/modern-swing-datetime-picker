package dev.phonchai.datetime.picker;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.time.LocalDate;

public class DatetimePicker {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            JFrame frame = new JFrame("Datetime Picker Demo");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel content = new JPanel(new MigLayout("wrap 2, insets 20", "[right][grow,fill]", "[]12[]12[]"));

            JFormattedTextField dateField = new JFormattedTextField();
            DatePicker datePicker = new DatePicker();
            datePicker.setEditor(dateField);
            datePicker.setUsePanelOption(true);
            datePicker.setCloseAfterSelected(true);
            datePicker.setSelectedDate(LocalDate.now());

            JFormattedTextField timeField = new JFormattedTextField();
            TimePicker timePicker = new TimePicker();
            timePicker.setEditor(timeField);
            timePicker.set24HourView(false);
            timePicker.now();

            JCheckBox betweenMode = new JCheckBox("Between dates");
            betweenMode.addActionListener(e -> datePicker.setDateSelectionMode(
                    betweenMode.isSelected()
                            ? DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED
                            : DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED));

            JCheckBox hour24Mode = new JCheckBox("24-hour time");
            hour24Mode.addActionListener(e -> timePicker.set24HourView(hour24Mode.isSelected()));

            JComboBox<PickerLanguage> languageBox = new JComboBox<>(PickerLanguage.values());
            languageBox.setSelectedItem(PickerLanguage.THAI);
            languageBox.addActionListener(e -> {
                PickerLanguage lang = (PickerLanguage) languageBox.getSelectedItem();
                if (lang != null) {
                    datePicker.setLanguage(lang);
                    timePicker.setLanguage(lang);
                }
            });

            content.add(new JLabel("Date:"));
            content.add(dateField, "wmin 220");
            content.add(new JLabel("Time:"));
            content.add(timeField, "wmin 220");
            content.add(new JLabel("Language:"));
            content.add(languageBox, "wmin 220");
            content.add(hour24Mode, "span 2");
            content.add(betweenMode, "span 2");

            frame.setContentPane(content);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
