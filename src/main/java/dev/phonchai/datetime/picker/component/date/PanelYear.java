package dev.phonchai.datetime.picker.component.date;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.DatePicker;
import dev.phonchai.datetime.picker.util.AppFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class PanelYear extends JPanel {

    public static final int YEAR_CELL = 28;
    private final DatePicker datePicker;
    private final int year; // ค.ศ. สำหรับการคำนวณ
    private int selectedYear = -1; // ค.ศ.

    public PanelYear(DatePicker datePicker, int year) {
        this.datePicker = datePicker;
        this.year = year; // รับเป็น ค.ศ.
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        setLayout(new MigLayout(
                "novisualpadding,wrap 4,insets 0,fillx,gap 0,al center center",
                "fill,sg main",
                "fill"));

        final int count = YEAR_CELL;
        Font yearButtonFont = AppFont.resolveYearButtonFont(datePicker);
        for (int i = 0; i < count; i++) {
            final int adYear = getStartYear(year) + i; // ค.ศ. สำหรับการคำนวณ
            final int displayYear = datePicker.isBuddhistEraLanguage() ? adToBuddhistYear(adYear) : adYear;

            ButtonMonthYear button = new ButtonMonthYear(datePicker, adYear); // ส่ง ค.ศ. ให้ button
            button.setText(Integer.toString(displayYear));
            button.setFont(yearButtonFont); // ตั้ง font ปี

            if (checkSelected(adYear)) {
                button.setSelected(true);
            }
            button.addActionListener(e -> {
                this.selectedYear = adYear; // เก็บเป็น ค.ศ. เพื่อให้ DatePicker ใช้ได้
                fireYearChanged(new ChangeEvent(this));
            });
            add(button);
        }
        checkSelection();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        Font yearButtonFont = AppFont.resolveYearButtonFont(this);
        for (Component component : getComponents()) {
            if (component instanceof ButtonMonthYear button) {
                button.setFont(yearButtonFont);
            }
        }
    }

    private int getStartYear(int year) {
        int initYear = 1900; // ค.ศ.
        int currentYear = year; // ค.ศ.
        int yearsPerPage = YEAR_CELL;
        int yearsPassed = currentYear - initYear;
        int pages = yearsPassed / yearsPerPage;
        int startingYearOnPage = initYear + (pages * yearsPerPage);
        return startingYearOnPage; // คืน ค.ศ.
    }

    // เพิ่ม utility methods
    public static int adToBuddhistYear(int adYear) {
        return adYear + 543;
    }

    public static int buddhistToAdYear(int buddhistYear) {
        return buddhistYear - 543;
    }

    protected boolean checkSelected(int adYear) {
        DateSelectionModel dateSelectionModel = datePicker.getDateSelectionModel();
        if (dateSelectionModel.getDateSelectionMode() == DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED) {
            return dateSelectionModel.getDate() != null && adYear == dateSelectionModel.getDate().getYear();
        } else {
            return (dateSelectionModel.getDate() != null && adYear == dateSelectionModel.getDate().getYear())
                    || (dateSelectionModel.getToDate() != null && adYear == dateSelectionModel.getToDate().getYear());
        }
    }

    public void checkSelection() {
        for (int i = 0; i < getComponentCount(); i++) {
            Component com = getComponent(i);
            if (com instanceof ButtonMonthYear) {
                ButtonMonthYear button = (ButtonMonthYear) com;
                button.setSelected(checkSelected(button.getValue()));
            }
        }
    }

    public int getYear() {
        return year; // คืน ค.ศ.
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    public void fireYearChanged(ChangeEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }

    public int getSelectedYear() {
        return selectedYear; // คืน ค.ศ.
    }
}
