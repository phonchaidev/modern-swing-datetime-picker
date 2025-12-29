package dev.phonchai.datetime.picker.component.date;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.DatePicker;
import dev.phonchai.datetime.picker.component.date.event.DateControlEvent;
import dev.phonchai.datetime.picker.component.date.event.DateControlListener;
import dev.phonchai.datetime.picker.util.AppFont;
import dev.phonchai.datetime.picker.util.Utils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {

    protected JButton buttonMonth;
    protected JButton buttonYear;

    private DatePicker datePicker;

    protected Icon backIcon;

    protected Icon forwardIcon;

    public Header() {
        this(10, 2023);
    }

    public Header(int month, int year) {
        init(month, year);
    }

    private void init(int month, int year) {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        setLayout(new MigLayout("fill,insets 3", "[]push[][]push[]", "fill"));

        JButton cmdBack = createButton();
        JButton cmdNext = createButton();

        backIcon = createDefaultBackIcon();
        forwardIcon = createDefaultForwardIcon();
        cmdBack.setIcon(backIcon);
        cmdNext.setIcon(forwardIcon);

        buttonMonth = createButton();
        buttonYear = createButton();

        applyFonts();

        cmdBack.addActionListener(e -> fireDateControlChanged(
                new DateControlEvent(this, DateControlEvent.DAY_STATE, DateControlEvent.BACK)));
        cmdNext.addActionListener(e -> fireDateControlChanged(
                new DateControlEvent(this, DateControlEvent.DAY_STATE, DateControlEvent.FORWARD)));
        buttonMonth.addActionListener(e -> fireDateControlChanged(
                new DateControlEvent(this, DateControlEvent.DAY_STATE, DateControlEvent.MONTH)));
        buttonYear.addActionListener(e -> fireDateControlChanged(
                new DateControlEvent(this, DateControlEvent.DAY_STATE, DateControlEvent.YEAR)));

        add(cmdBack);
        add(buttonMonth);
        add(buttonYear);
        add(cmdNext);
        setDate(month, year);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        applyFonts();
    }

    private void applyFonts() {
        if (buttonMonth == null || buttonYear == null) {
            return;
        }
        Font headerFont = AppFont.resolveHeaderFont(this);
        buttonMonth.setFont(headerFont);
        buttonYear.setFont(headerFont);
    }

    protected JButton createButton() {
        JButton button = new JButton();
        button.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null;"
                + "arc:10;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:0,5,0,5");
        return button;
    }

    protected Icon createDefaultBackIcon() {
        return Utils.createIcon("com/sfis/datetime/icon/back.svg", 1f);
    }

    protected Icon createDefaultForwardIcon() {
        return Utils.createIcon("com/sfis/datetime/icon/forward.svg", 1f);
    }

    public void addDateControlListener(DateControlListener listener) {
        listenerList.add(DateControlListener.class, listener);
    }

    public void removeDateControlListener(DateControlListener listener) {
        listenerList.remove(DateControlListener.class, listener);
    }

    public void fireDateControlChanged(DateControlEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DateControlListener.class) {
                ((DateControlListener) listeners[i + 1]).dateControlChanged(event);
            }
        }
    }

    public void setDate(int month, int year) {
        if (datePicker != null) {
            buttonMonth.setText(datePicker.getMonths()[month]);
            buttonYear.setText(Integer.toString(datePicker.toDisplayYear(year)));
        } else {
            final int buddhistYear = SingleDate.adToBuddhistYear(year); // พ.ศ. สำหรับแสดงผล
            buttonMonth.setText(DatePicker.getDefaultMonths()[month]);
            buttonYear.setText(buddhistYear + "");
        }
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public Icon getBackIcon() {
        return backIcon;
    }

    public void setBackIcon(Icon backIcon) {
        this.backIcon = backIcon;
    }

    public Icon getForwardIcon() {
        return forwardIcon;
    }

    public void setForwardIcon(Icon forwardIcon) {
        this.forwardIcon = forwardIcon;
    }
}
