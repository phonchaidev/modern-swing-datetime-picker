package dev.phonchai.datetime.picker.component.date;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.DatePicker;
import dev.phonchai.datetime.picker.util.AppFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

public class PanelDate extends JPanel {

    private final DatePicker datePicker;
    private final int month;
    private final int year;
    private Font dayLabelFont;
    private Font dayButtonFont;

    public PanelDate(DatePicker datePicker, int month, int year) {
        this.datePicker = datePicker;
        this.month = month;
        this.year = year; // ยังคงเก็บเป็น ค.ศ. สำหรับการคำนวณ
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null;");
        setLayout(new MigLayout(
                "novisualpadding,wrap 7,insets 3,gap 0,al center center",
                "[fill]",
                "[fill]10[fill][fill]"));
        applyFonts();
        load();
    }

    private void applyFonts() {
        dayLabelFont = AppFont.resolveDayLabelFont(datePicker);
        dayButtonFont = AppFont.resolveDayButtonFont(datePicker);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        applyFonts();
        for (Component component : getComponents()) {
            if (component instanceof JLabel label) {
                label.setFont(dayLabelFont);
            } else if (component instanceof ButtonDate button) {
                button.setFont(dayButtonFont);
            }
        }
    }

    // เพิ่ม utility methods สำหรับแปลงปี
    public static int adToBuddhistYear(int adYear) {
        return adYear + 543;
    }

    public static int buddhistToAdYear(int buddhistYear) {
        return buddhistYear - 543;
    }

    public void load() {
        removeAll();
        createDateHeader();
        final int col = 7;
        final int row = 6;
        final int t = col * row;

        // ใช้ LocalDate แทน Calendar เพื่อความแม่นยำ
        YearMonth yearMonth = YearMonth.of(year, month + 1); // year ยังคงเป็น ค.ศ. สำหรับคำนวณ
        LocalDate firstDayOfMonth = yearMonth.atDay(1);

        // คำนวณวันเริ่มต้นของ calendar grid
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();
        int startOffset;
        if (datePicker.isStartWeekOnMonday()) {
            startOffset = firstDayOfWeek.getValue() - 1; // Monday = 1, Sunday = 7
        } else {
            startOffset = firstDayOfWeek.getValue() == 7 ? 0 : firstDayOfWeek.getValue(); // Sunday = 0
        }

        LocalDate startDate = firstDayOfMonth.minusDays(startOffset);

        int rowIndex = 0;
        for (int i = 0; i < t; i++) {
            LocalDate currentDate = startDate.plusDays(i);

            // สร้าง SingleDate โดยตรงจาก LocalDate (ไม่ผ่าน Calendar)
            SingleDate singleDate = new SingleDate(currentDate);

            boolean selectable = datePicker.getDateSelectionModel().getDateSelectionAble() == null
                    || datePicker.getDateSelectionModel().getDateSelectionAble()
                            .isDateSelectedAble(singleDate.toLocalDate());
            boolean enable = currentDate.getMonthValue() == (month + 1) && currentDate.getYear() == year;

            JButton button = createButton(singleDate, enable, rowIndex);
            if (!selectable) {
                button.setEnabled(false);
            }
            add(button);

            if (rowIndex == 6) {
                rowIndex = 0;
            } else {
                rowIndex++;
            }
        }
        checkSelection();
    }

    protected void createDateHeader() {
        String[] weekdays = datePicker.getWeekdays();
        // swap monday to the start day of week
        if (datePicker.isStartWeekOnMonday()) {
            String sunday = weekdays[1];
            for (int i = 2; i < weekdays.length; i++) {
                weekdays[i - 1] = weekdays[i];
            }
            weekdays[weekdays.length - 1] = sunday;
        }
        for (String week : weekdays) {
            if (!week.isEmpty()) {
                add(createLabel(week));
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(dayLabelFont);
        label.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]foreground:lighten($Label.foreground,30%);"
                + "[dark]foreground:darken($Label.foreground,30%)");
        return label;
    }

    protected JButton createButton(SingleDate date, boolean enable, int rowIndex) {
        ButtonDate button = new ButtonDate(datePicker, date, enable, rowIndex);
        button.setFont(dayButtonFont);
        if (button.isDateSelected()) {
            button.setSelected(true);
        }
        return button;
    }

    public void checkSelection() {
        for (int i = 0; i < getComponentCount(); i++) {
            Component com = getComponent(i);
            if (com instanceof ButtonDate) {
                ButtonDate buttonDate = (ButtonDate) com;
                if (datePicker.getDateSelectionModel()
                        .getDateSelectionMode() == DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED) {
                    buttonDate.setSelected(buttonDate.getDate().same(datePicker.getDateSelectionModel().getDate()));
                } else {
                    buttonDate.setSelected(buttonDate.getDate().same(datePicker.getDateSelectionModel().getDate())
                            || buttonDate.getDate().same(datePicker.getDateSelectionModel().getToDate()));
                }
            }
        }
    }
}
