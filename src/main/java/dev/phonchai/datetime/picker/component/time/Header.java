package dev.phonchai.datetime.picker.component.time;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.TimePicker;
import dev.phonchai.datetime.picker.component.time.event.TimeActionListener;
import dev.phonchai.datetime.picker.util.AppFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;

public class Header extends JPanel {

    private final TimePicker timePicker;
    private final TimeActionListener timeActionListener;
    private final DecimalFormat format = new DecimalFormat("00");
    private MigLayout layout;
    private Color color;
    private JLabel labelSplit;

    public Header(TimePicker timePicker, TimeActionListener timeActionListener) {
        this.timePicker = timePicker;
        this.timeActionListener = timeActionListener;
        init();
    }

    private void init() {
        layout = new MigLayout("fill,insets 10", "center");
        setLayout(layout);
        add(createToolBar(), "id b1");
        add(createAmPm(), "pos b1.x2+rel 0.5al n n");
        applyFonts();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        applyFonts();
    }

    private void applyFonts() {
        if (buttonHour == null || buttonMinute == null) {
            return;
        }
        Font baseFont = AppFont.resolveBaseFont(this);
        Font hourMinuteFont = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD,
                Math.max(1f, baseFont.getSize2D() + 15f));
        Font splitFont = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD, Math.max(1f, baseFont.getSize2D() + 10f));
        Font amPmFont = baseFont.deriveFont(baseFont.getStyle(), Math.max(1f, baseFont.getSize2D() + 1f));

        buttonHour.setFont(hourMinuteFont);
        buttonMinute.setFont(hourMinuteFont);
        if (labelSplit != null) {
            labelSplit.setFont(splitFont);
        }
        if (buttonAm != null) {
            buttonAm.setFont(amPmFont);
        }
        if (buttonPm != null) {
            buttonPm.setFont(amPmFont);
        }
    }

    public void updateHeader() {
        int hour = timePicker.getTimeSelectionModel().getHour();
        int minute = timePicker.getTimeSelectionModel().getMinute();

        if (hour == -1 && minute == -1) {
            buttonAm.setSelected(false);
            buttonPm.setSelected(false);
        } else {
            if (hour >= 12) {
                setSelectedAm(false);
            } else {
                setSelectedAm(true);
            }
        }

        if (!timePicker.is24HourView()) {
            if (hour >= 12) {
                hour -= 12;
            }
            if (hour == 0) {
                hour = 12;
            }
        }

        String hourText = hour == -1 ? "--" : format.format(hour);
        String minuteText = minute == -1 ? "--" : format.format(minute);
        buttonHour.setText(hourText);
        buttonMinute.setText(minuteText);
    }

    public void setOrientation(int orientation) {
        String c = orientation == SwingConstants.VERTICAL ? "pos b1.x2+rel 0.5al n n" : "pos 0.5al b1.y2+rel n n";
        amPmToolBar.setOrientation(orientation);
        layout.setComponentConstraints(amPmToolBar, c);
    }

    public boolean isAm() {
        return !buttonPm.isSelected();
    }

    public void setHourSelectionView(boolean hourSelectionView) {
        if (hourSelectionView) {
            buttonHour.setSelected(true);
        } else {
            buttonMinute.setSelected(true);
        }
    }

    public void setUse24hour(boolean use24hour) {
        amPmToolBar.setVisible(!use24hour);
    }

    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "hoverButtonGroupBackground:null;");
        buttonHour = createButton();
        buttonMinute = createButton();
        ButtonGroup group = new ButtonGroup();
        group.add(buttonHour);
        group.add(buttonMinute);
        buttonHour.setSelected(true);
        buttonHour.addActionListener(e -> timeActionListener.selectionViewChanged(true));
        buttonMinute.addActionListener(e -> timeActionListener.selectionViewChanged(false));
        toolBar.add(buttonHour);
        labelSplit = createSplit();
        toolBar.add(labelSplit);
        toolBar.add(buttonMinute);
        return toolBar;
    }

    protected JToggleButton createButton() {
        JToggleButton button = new JToggleButton("--");
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "toolbar.margin:3,5,3,5;" +
                "foreground:contrast($Component.accentColor,$ToggleButton.background,#fff);" +
                "background:null;" +
                "toolbar.hoverBackground:null");
        return button;
    }

    protected JButton createAmPmButton(boolean isAm) {
        String amOrPm = getAmPmText(isAm);
        JButton button = new JButton(amOrPm);
        button.addActionListener(e -> actionAmPmChanged(isAm));
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:contrast($Component.accentColor,$ToggleButton.background,#fff);" +
                "background:null;" +
                "toolbar.hoverBackground:null");
        return button;
    }

    public void updateLocale() {
        if (buttonAm == null || buttonPm == null) {
            return;
        }
        buttonAm.setText(getAmPmText(true));
        buttonPm.setText(getAmPmText(false));
        revalidate();
        repaint();
    }

    private String getAmPmText(boolean isAm) {
        String[] amPm = DateFormatSymbols.getInstance(timePicker.getDisplayLocale()).getAmPmStrings();
        if (amPm == null || amPm.length < 2) {
            return isAm ? "AM" : "PM";
        }
        return isAm ? amPm[0] : amPm[1];
    }

    private void actionAmPmChanged(boolean isAm) {
        TimeSelectionModel timeSelectionModel = timePicker.getTimeSelectionModel();
        int hour = timeSelectionModel.getHour();
        int minute = timeSelectionModel.getMinute();
        if (hour == -1 && minute == -1) {
            setSelectedAm(isAm);

            // need to repaint the panel clock to update the paint text selection able
            timePicker.repaint();
        } else {
            if (isAm) {
                if (hour >= 12) {
                    hour -= 12;
                }
            } else {
                if (hour < 12) {
                    hour += 12;
                }
            }
            timeSelectionModel.set(hour, minute);
        }
    }

    private void setSelectedAm(boolean isAm) {
        buttonAm.setSelected(isAm);
        buttonPm.setSelected(!isAm);
    }

    protected JLabel createSplit() {
        JLabel label = new JLabel(":");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:contrast($Component.accentColor,$Label.background,#fff)");
        return label;
    }

    protected JToolBar createAmPm() {
        amPmToolBar = new JToolBar();
        amPmToolBar.setOrientation(SwingConstants.VERTICAL);
        amPmToolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "hoverButtonGroupBackground:null");
        buttonAm = createAmPmButton(true);
        buttonPm = createAmPmButton(false);
        amPmToolBar.add(buttonAm);
        amPmToolBar.add(buttonPm);
        return amPmToolBar;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Override this method to return the background color to the JToolBar
     * When JToolBar use null background, so it will paint the parent background.
     */
    @Override
    public Color getBackground() {
        if (color != null) {
            return color;
        }
        return UIManager.getColor("Component.accentColor");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        buttonAm.setEnabled(enabled);
        buttonPm.setEnabled(enabled);
    }

    private JToggleButton buttonHour;
    private JToggleButton buttonMinute;

    private JToolBar amPmToolBar;
    private JButton buttonAm;
    private JButton buttonPm;
}
