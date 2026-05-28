package dev.phonchai.datetime.picker;

import com.formdev.flatlaf.FlatClientProperties;
import dev.phonchai.datetime.picker.component.PanelPopupEditor;
import dev.phonchai.datetime.picker.component.date.*;
import dev.phonchai.datetime.picker.component.date.event.DateControlEvent;
import dev.phonchai.datetime.picker.component.date.event.DateControlListener;
import dev.phonchai.datetime.picker.component.date.event.DateSelectionModelEvent;
import dev.phonchai.datetime.picker.component.date.event.DateSelectionModelListener;
import dev.phonchai.datetime.picker.event.DateSelectionEvent;
import dev.phonchai.datetime.picker.event.DateSelectionListener;
import dev.phonchai.datetime.picker.slider.PanelSlider;
import dev.phonchai.datetime.picker.slider.SimpleTransition;
import dev.phonchai.datetime.picker.slider.SliderTransition;
import dev.phonchai.datetime.picker.util.InputUtils;
import dev.phonchai.datetime.picker.util.InputValidationListener;
import dev.phonchai.datetime.picker.util.ThaiBuddhistDateUtils;
import dev.phonchai.datetime.picker.util.Utils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * A date picker component for Java Swing that supports both single date and
 * date range selection.
 * <p>
 * Features include:
 * <ul>
 * <li>Thai Buddhist calendar (พ.ศ.) and Gregorian calendar support</li>
 * <li>Single date and date range selection modes</li>
 * <li>Customizable date format and separator</li>
 * <li>FlatLaf styling integration</li>
 * <li>Optional text field editor integration</li>
 * </ul>
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * DatePicker picker = new DatePicker();
 * picker.setLanguage(PickerLanguage.THAI);
 * picker.setDateFormat("dd/MM/yyyy");
 * picker.addDateSelectionListener(e -> System.out.println(e.getDate()));
 * }</pre>
 *
 * @author dev.phonchai
 * @version 1.0.0
 * @see TimePicker
 * @see DatetimePicker
 * @see PickerLanguage
 */
public class DatePicker extends PanelPopupEditor
        implements DateSelectionModelListener, DateControlListener, ChangeListener {

    public static final Locale THAI = Locale.forLanguageTag("th-TH");
    public static final Locale ENGLISH = Locale.ENGLISH;

    private static String[] defaultWeekdays = null;
    private static String[] defaultMonths = null;
    private static final String[] THAI_SHORT_WEEKDAYS = DateFormatSymbols.getInstance(THAI).getShortWeekdays();
    private static final String[] THAI_MONTHS = DateFormatSymbols.getInstance(THAI).getMonths();
    private static final String[] ENGLISH_SHORT_WEEKDAYS = DateFormatSymbols.getInstance(ENGLISH).getShortWeekdays();
    private static final String[] ENGLISH_MONTHS = DateFormatSymbols.getInstance(ENGLISH).getMonths();

    private PickerLanguage language = PickerLanguage.THAI;
    private String[] weekdays;
    private String[] months;

    private String dateFormatPattern = "dd/MM/yyyy";
    private DateTimeFormatter strictDateFormatter;
    private DateTimeFormatter displayDateFormatter;
    private DateSelectionListener dateSelectionListener;
    private InputValidationListener<LocalDate> inputValidationListener;
    private DateSelectionModel dateSelectionModel;
    private PanelDateOption panelDateOption;
    private PanelDateOptionLabel panelDateOptionLabel;
    private InputUtils.ValueCallback valueCallback;
    private Icon editorIcon;
    private String separator = " ถึง ";
    private boolean separatorCustomized;
    private boolean usePanelOption;
    private boolean closeAfterSelected;
    private boolean animationEnabled = true;
    private boolean startWeekOnMonday;
    private float selectionArc = 999;
    private int month = 10;
    private int year = 2023;
    private Color color;
    private JButton editorButton;
    private SelectionState selectionState = SelectionState.DATE;
    private PanelDate panelDate;
    private PanelMonth panelMonth;
    private PanelYear panelYear;

    private DefaultDateCellRenderer defaultDateCellRenderer = new DefaultDateCellRenderer();
    private final Header header = new Header();
    private final PanelSlider panelSlider = new PanelSlider();

    /**
     * Creates a new DatePicker with default settings.
     * <p>
     * Default configuration:
     * <ul>
     * <li>Language: Thai</li>
     * <li>Date format: dd/MM/yyyy</li>
     * <li>Selection mode: Single date</li>
     * </ul>
     */
    public DatePicker() {
        this(null);
    }

    /**
     * Creates a new DatePicker with a custom date selection model.
     *
     * @param dateSelectionModel the date selection model to use, or null to use
     *                           default
     */
    public DatePicker(DateSelectionModel dateSelectionModel) {
        init(dateSelectionModel);
    }

    private void init(DateSelectionModel dateSelectionModel) {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken($Panel.background,2%);"
                + "[dark]background:lighten($Panel.background,2%);");
        setLayout(new MigLayout(
                "wrap,insets 10,fill",
                "[fill]",
                "[top,grow 0][center,fill]"));

        PickerLanguage configuredLanguage = PickerLanguage.fromValue(UIManager.get(PickerLanguage.UI_KEY_LANGUAGE));
        if (configuredLanguage != null) {
            this.language = configuredLanguage;
            setSeparatorInternal(getDefaultSeparator(configuredLanguage), false);
        }

        header.setDatePicker(this);
        updateFormatters();
        header.addDateControlListener(this);

        if (dateSelectionModel == null) {
            dateSelectionModel = createDefaultDateSelection();
        }
        setDateSelectionModel(dateSelectionModel);

        add(header);
        add(panelSlider);
        initDate();
        // System.out.println("W :" + getWidth());
        // System.out.println("H :" + getHeight());
    }

    private void initDate() {
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue() - 1;
        int year = date.getYear();
        this.month = month;
        this.year = year;
        header.setDate(month, year);
        panelSlider.addSlide(createPanelDate(month, year), null);
    }

    public void setToBack() {
        if (selectionState == SelectionState.DATE) {
            if (month == 0) {
                month = 11;
                year--;
            } else {
                month--;
            }
            header.setDate(month, year);
            panelSlider.addSlide(createPanelDate(month, year), getSliderTransition(SimpleTransition.SliderType.BACK));
        } else if (selectionState == SelectionState.MONTH) {
            year--;
            header.setDate(month, year);
            panelSlider.addSlide(createPanelMonth(year), getSliderTransition(SimpleTransition.SliderType.BACK));
        } else {
            int oldYear = this.panelYear.getYear();
            panelSlider.addSlide(createPanelYear(oldYear - PanelYear.YEAR_CELL),
                    getSliderTransition(SimpleTransition.SliderType.BACK));
        }
    }

    public void setToForward() {
        if (selectionState == SelectionState.DATE) {
            if (month == 11) {
                month = 0;
                year++;
            } else {
                month++;
            }
            header.setDate(month, year);
            panelSlider.addSlide(createPanelDate(month, year),
                    getSliderTransition(SimpleTransition.SliderType.FORWARD));
        } else if (selectionState == SelectionState.MONTH) {
            year++;
            header.setDate(month, year);
            panelSlider.addSlide(createPanelMonth(year), getSliderTransition(SimpleTransition.SliderType.FORWARD));
        } else {
            int oldYear = this.panelYear.getYear();
            panelSlider.addSlide(createPanelYear(oldYear + PanelYear.YEAR_CELL),
                    getSliderTransition(SimpleTransition.SliderType.FORWARD));
        }
    }

    public void selectMonth() {
        if (selectionState != SelectionState.MONTH) {
            panelSlider.addSlide(createPanelMonth(year),
                    getSliderTransition(selectionState == SelectionState.DATE ? SimpleTransition.SliderType.TOP_DOWN
                            : SimpleTransition.SliderType.DOWN_TOP));
            selectionState = SelectionState.MONTH;
        } else {
            panelSlider.addSlide(createPanelDate(month, year),
                    getSliderTransition(SimpleTransition.SliderType.DOWN_TOP));
            selectionState = SelectionState.DATE;
        }
    }

    public void selectYear() {
        if (selectionState != SelectionState.YEAR) {
            panelSlider.addSlide(createPanelYear(year), getSliderTransition(SimpleTransition.SliderType.TOP_DOWN));
            selectionState = SelectionState.YEAR;
        } else {
            panelSlider.addSlide(createPanelDate(month, year),
                    getSliderTransition(SimpleTransition.SliderType.DOWN_TOP));
            selectionState = SelectionState.DATE;
        }
    }

    public DateSelectionMode getDateSelectionMode() {
        return dateSelectionModel.getDateSelectionMode();
    }

    public void setDateSelectionMode(DateSelectionMode dateSelectionMode) {
        if (getDateSelectionMode() != dateSelectionMode) {
            this.dateSelectionModel.setDateSelectionMode(dateSelectionMode);
            if (editor != null) {
                InputUtils.changeDateFormatted(editor, dateFormatPattern,
                        getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED, separator,
                        getInputValidationListener());
                this.defaultPlaceholder = null;
                clearSelectedDate();
                commitEdit();
            }
            repaint();
        }
    }

    public void now() {
        LocalDate date = LocalDate.now();
        if (getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED) {
            setSelectedDateRange(date, date);
        } else {
            setSelectedDate(date);
        }
    }

    public void toDateSelectionView() {
        LocalDate date = getSelectedDate();
        if (date == null) {
            date = LocalDate.now();
        }
        int m = date.getMonthValue() - 1;
        int y = date.getYear();
        if (selectionState != SelectionState.DATE || y != year || m != month) {
            panelSlider.addSlide(createPanelDate(m, y), getSliderTransition(SimpleTransition.SliderType.DEFAULT));
            month = m;
            year = y;
            selectionState = SelectionState.DATE;
            header.setDate(month, year);
            updateSelected();
        }
    }

    public void selectCurrentMonth() {
        LocalDate date = LocalDate.now();
        if (getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED) {
            setSelectedDateRange(date.withDayOfMonth(1), date);
        } else {
            setSelectedDate(date);
        }
    }

    public void setOrClear(LocalDate date) {
        if (date == null) {
            clearSelectedDate();
        } else {
            setSelectedDate(date);
        }
    }

    /**
     * Sets the selected date.
     * <p>
     * In {@link DateSelectionMode#BETWEEN_DATE_SELECTED} mode, this also sets the
     * end date
     * to the same value.
     *
     * @param date the date to select
     * @see #getSelectedDate()
     * @see #setSelectedDateRange(LocalDate, LocalDate)
     */
    public void setSelectedDate(LocalDate date) {
        dateSelectionModel.setDate(new SingleDate(date));
        if (getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED) {
            dateSelectionModel.setToDate(new SingleDate(date));
        }
        slideTo(date);
    }

    /**
     * Sets the selected date range for
     * {@link DateSelectionMode#BETWEEN_DATE_SELECTED} mode.
     *
     * @param from the start date of the range
     * @param to   the end date of the range
     * @throws IllegalArgumentException if called in
     *                                  {@link DateSelectionMode#SINGLE_DATE_SELECTED}
     *                                  mode
     * @see #getSelectedDate()
     * @see #getSelectedToDate()
     */
    public void setSelectedDateRange(LocalDate from, LocalDate to) {
        if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
            throw new IllegalArgumentException("Single date mode can't accept the range date");
        }
        dateSelectionModel.setSelectDate(new SingleDate(from), new SingleDate(to));
        slideTo(from);
    }

    public void setEditor(JFormattedTextField editor) {
        if (editor != this.editor) {
            JFormattedTextField old = this.editor;
            if (old != null) {
                uninstallEditor(old);
            }
            this.editor = editor;
            if (this.editor != null) {
                installEditor(editor);
                if (editorValidation) {
                    validChanged(editor, isValid);
                } else {
                    validChanged(editor, true);
                }
            }
        }
    }

    public Icon getEditorIcon() {
        return editorIcon;
    }

    public void setEditorIcon(Icon editorIcon) {
        this.editorIcon = editorIcon;
        editorButton.setIcon(editorIcon);
    }

    public DateSelectionAble getDateSelectionAble() {
        return dateSelectionModel.getDateSelectionAble();
    }

    public void setDateSelectionAble(DateSelectionAble dateSelectionAble) {
        this.dateSelectionModel.setDateSelectionAble(dateSelectionAble);
        if (selectionState == SelectionState.DATE) {
            panelDate.load();
        }
        commitEdit();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        setSeparatorInternal(separator, true);
    }

    private void setSeparatorInternal(String separator, boolean customized) {
        if (separator == null) {
            throw new IllegalArgumentException("separator can't be null");
        }
        if (!this.separator.equals(separator)) {
            this.separator = separator;
            this.separatorCustomized |= customized;
            if (editor != null) {
                InputUtils.changeDateFormatted(
                        editor,
                        dateFormatPattern,
                        getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED,
                        separator,
                        getDisplayLocale(),
                        isBuddhistEraLanguage(),
                        getInputValidationListener());
                this.defaultPlaceholder = null;
                setEditorValue();
            }
        }
    }

    public String getDateFormat() {
        return this.dateFormatPattern;
    }

    public void setDateFormat(String format) {
        if (format == null) {
            throw new IllegalArgumentException("format can't be null");
        }
        if (!this.dateFormatPattern.equals(format)) {
            this.dateFormatPattern = format;
            updateFormatters();
            if (editor != null) {
                InputUtils.changeDateFormatted(
                        editor,
                        format,
                        getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED,
                        separator,
                        getDisplayLocale(),
                        isBuddhistEraLanguage(),
                        getInputValidationListener());
                this.defaultPlaceholder = null;
            }
        }
    }

    public boolean isUsePanelOption() {
        return usePanelOption;
    }

    public void setUsePanelOption(boolean usePanelOption) {
        if (this.usePanelOption != usePanelOption) {
            this.usePanelOption = usePanelOption;
            if (usePanelOption) {
                if (panelDateOption == null) {
                    panelDateOption = new PanelDateOption(this);
                    panelDateOption.installDateOptionLabel();
                }
                add(panelDateOption, "dock east,gap 0 10 10 10");
                repaint();
                revalidate();
            } else {
                if (panelDateOption != null) {
                    remove(panelDateOption);
                    panelDateOption = null;
                    repaint();
                    revalidate();
                }
            }
        }
    }

    public PanelDateOptionLabel getPanelDateOptionLabel() {
        return panelDateOptionLabel;
    }

    public void setPanelDateOptionLabel(PanelDateOptionLabel panelDateOptionLabel) {
        if (panelDateOptionLabel == null) {
            throw new IllegalArgumentException("panelDateOptionLabel can't be null");
        }
        this.panelDateOptionLabel = panelDateOptionLabel;
        if (panelDateOption != null) {
            panelDateOption.installDateOptionLabel();
        }
    }

    public boolean isCloseAfterSelected() {
        return closeAfterSelected;
    }

    public void setCloseAfterSelected(boolean closeAfterSelected) {
        this.closeAfterSelected = closeAfterSelected;
    }

    public boolean isAnimationEnabled() {
        return animationEnabled;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        this.animationEnabled = animationEnabled;
    }

    public boolean isStartWeekOnMonday() {
        return startWeekOnMonday;
    }

    public void setStartWeekOnMonday(boolean startWeekOnMonday) {
        if (this.startWeekOnMonday != startWeekOnMonday) {
            this.startWeekOnMonday = startWeekOnMonday;
            if (selectionState == SelectionState.DATE && panelDate != null) {
                // update the panel date
                panelDate.load();
                panelDate.repaint();
                panelDate.revalidate();
            }
        }
    }

    public float getSelectionArc() {
        return selectionArc;
    }

    public void setSelectionArc(float selectionArc) {
        if (this.selectionArc != selectionArc) {
            this.selectionArc = selectionArc;
            updateSelected();
        }
    }

    public void clearSelectedDate() {
        dateSelectionModel.setSelectDate(null, null);
        updateSelected();
    }

    public boolean isDateSelected() {
        if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
            return dateSelectionModel.getDate() != null;
        } else {
            return dateSelectionModel.getDate() != null && dateSelectionModel.getToDate() != null;
        }
    }

    public LocalDate getSelectedDate() {
        SingleDate date = dateSelectionModel.getDate();
        if (date != null) {
            return date.toLocalDate();
        }
        return null;
    }

    public LocalDate[] getSelectedDateRange() {
        SingleDate from = dateSelectionModel.getDate();
        if (from != null) {
            LocalDate[] dates = new LocalDate[2];
            dates[0] = from.toLocalDate();
            SingleDate to = dateSelectionModel.getToDate();
            if (to != null) {
                dates[1] = to.toLocalDate();
                return dates;
            }
        }
        return null;
    }

    // public String getSelectedDateAsString() {
    // if (isDateSelected()) {
    // if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
    // return format.format(getSelectedDate());
    // } else {
    // LocalDate[] dates = getSelectedDateRange();
    // return format.format(dates[0]) + separator + format.format(dates[1]);
    // }
    // } else {
    // return null;
    // }
    // }
    public String getSelectedDateAsString() {
        if (!isDateSelected()) {
            return null;
        }
        if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
            LocalDate d = getSelectedDate();
            return formatBuddhistDate(d);
        } else {
            LocalDate[] dates = getSelectedDateRange();
            return formatBuddhistDate(dates[0]) + separator + formatBuddhistDate(dates[1]);
        }
    }

    private String formatBuddhistDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return displayDateFormatter.format(date);
    }

    public void slideTo(LocalDate date) {
        int m = date.getMonthValue() - 1;
        int y = date.getYear();
        if (year != y || month != m) {
            if (year < y || (year <= y && month < m)) {
                panelSlider.addSlide(createPanelDate(m, y), getSliderTransition(SimpleTransition.SliderType.FORWARD));
            } else {
                panelSlider.addSlide(createPanelDate(m, y), getSliderTransition(SimpleTransition.SliderType.BACK));
            }
            month = m;
            year = y;
            selectionState = SelectionState.DATE;
            header.setDate(month, year);
        } else {
            if (selectionState != SelectionState.DATE) {
                panelSlider.addSlide(createPanelDate(m, y), getSliderTransition(SimpleTransition.SliderType.DOWN_TOP));
                selectionState = SelectionState.DATE;
            }
        }
        updateSelected();
    }

    public void addDateSelectionListener(DateSelectionListener listener) {
        listenerList.add(DateSelectionListener.class, listener);
    }

    public void removeDateSelectionListener(DateSelectionListener listener) {
        listenerList.remove(DateSelectionListener.class, listener);
    }

    public DefaultDateCellRenderer getDefaultDateCellRenderer() {
        return defaultDateCellRenderer;
    }

    public void setDefaultDateCellRenderer(DefaultDateCellRenderer defaultDateCellRenderer) {
        this.defaultDateCellRenderer = defaultDateCellRenderer;
        repaint();
    }

    public Header getHeader() {
        return header;
    }

    public DateSelectionModel getDateSelectionModel() {
        return dateSelectionModel;
    }

    public void setDateSelectionModel(DateSelectionModel dateSelectionModel) {
        if (dateSelectionModel == null) {
            throw new IllegalArgumentException("dateSelectionModel can't be null");
        }
        if (this.dateSelectionModel != dateSelectionModel) {
            DateSelectionModel old = this.dateSelectionModel;
            if (old != null) {
                old.removeDatePickerSelectionListener(this);
            }
            this.dateSelectionModel = dateSelectionModel;
            this.dateSelectionModel.addDatePickerSelectionListener(this);
        }
    }

    @Override
    public void dateSelectionModelChanged(DateSelectionModelEvent e) {
        if (e.getAction() == DateSelectionModelEvent.DATE) {
            verifyDateSelection();
        }
        repaint();
    }

    @Override
    public void dateControlChanged(DateControlEvent e) {
        if (e.getType() == DateControlEvent.BACK) {
            setToBack();
        } else if (e.getType() == DateControlEvent.FORWARD) {
            setToForward();
        } else if (e.getType() == DateControlEvent.MONTH) {
            selectMonth();
        } else if (e.getType() == DateControlEvent.YEAR) {
            selectYear();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == panelMonth) {
            this.month = panelMonth.getSelectedMonth();
            header.setDate(month, year);
            panelSlider.addSlide(createPanelDate(month, year),
                    getSliderTransition(SimpleTransition.SliderType.DOWN_TOP));
            selectionState = SelectionState.DATE;
        } else if (e.getSource() == panelYear) {
            this.year = panelYear.getSelectedYear();
            header.setDate(month, year);
            panelSlider.addSlide(createPanelMonth(year), getSliderTransition(SimpleTransition.SliderType.DOWN_TOP));
            selectionState = SelectionState.MONTH;
        }
    }

    protected DateSelectionModel createDefaultDateSelection() {
        return new DateSelectionModel();
    }

    protected SliderTransition getSliderTransition(SimpleTransition.SliderType type) {
        if (!animationEnabled) {
            return null;
        }
        return SimpleTransition.get(type);
    }

    private void updateSelected() {
        if (selectionState == SelectionState.DATE) {
            panelDate.checkSelection();
        } else if (selectionState == SelectionState.MONTH) {
            panelMonth.checkSelection();
        } else if (selectionState == SelectionState.YEAR) {
            panelYear.checkSelection();
        }
    }

    private void installEditor(JFormattedTextField editor) {
        if (editor != null) {
            JToolBar toolBar = new JToolBar();
            editorButton = new JButton(
                    editorIcon != null ? editorIcon
                            : Utils.createIcon("dev/phonchai/datetime/picker/icon/calendar.svg", 0.48f));
            toolBar.add(editorButton);
            editorButton.addActionListener(e -> {
                if (editor.isEnabled()) {
                    editor.grabFocus();
                    showPopup();
                }
            });

            editor.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (editor.isEnabled()) {
                        editor.grabFocus();
                        showPopup();
                    }
                }
            });

            InputUtils.useDateInput(
                    editor,
                    dateFormatPattern,
                    getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED,
                    separator,
                    getDisplayLocale(),
                    isBuddhistEraLanguage(),
                    getValueCallback(),
                    getInputValidationListener());
            setEditorValue();
            editor.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
            addDateSelectionListener(getDateSelectionListener());
        }
    }

    private void uninstallEditor(JFormattedTextField editor) {
        if (editor != null) {
            editorButton = null;
            InputUtils.removePropertyChange(editor);
            if (dateSelectionListener != null) {
                removeDateSelectionListener(dateSelectionListener);
            }
        }
    }

    // private InputUtils.ValueCallback getValueCallback() {
    // if (valueCallback == null) {
    // valueCallback = value -> {
    // if (value == null && isDateSelected()) {
    // clearSelectedDate();
    // } else {
    // if (value != null && !value.equals(getSelectedDateAsString())) {
    // if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
    // LocalDate date = InputUtils.stringToDate(format, value.toString());
    // if (date != null) {
    // setSelectedDate(date);
    // }
    // } else {
    // LocalDate[] dates = InputUtils.stringToDate(format, separator,
    // value.toString());
    // if (dates != null) {
    // setSelectedDateRange(dates[0], dates[1]);
    // }
    // }
    // }
    // }
    // };
    // }
    // return valueCallback;
    // }
    private InputUtils.ValueCallback getValueCallback() {
        if (valueCallback == null) {
            valueCallback = value -> {
                if (value == null && isDateSelected()) {
                    clearSelectedDate();
                } else if (value != null && !value.equals(getSelectedDateAsString())) {
                    if (getDateSelectionMode() == DateSelectionMode.SINGLE_DATE_SELECTED) {
                        LocalDate date = ThaiBuddhistDateUtils.parseToIsoLocalDate(value.toString(),
                                strictDateFormatter, isBuddhistEraLanguage());
                        if (date != null) {
                            setSelectedDate(date);
                        }
                    } else {
                        LocalDate[] dates = ThaiBuddhistDateUtils.parseRangeToIsoLocalDate(value.toString(), separator,
                                strictDateFormatter, isBuddhistEraLanguage());
                        if (dates != null) {
                            setSelectedDateRange(dates[0], dates[1]);
                        }
                    }
                }
            };
        }
        return valueCallback;
    }

    private DateSelectionListener getDateSelectionListener() {
        if (dateSelectionListener == null) {
            dateSelectionListener = dateSelectionEvent -> setEditorValue();
        }
        return dateSelectionListener;
    }

    // private void setEditorValue() {
    // String value = getSelectedDateAsString();
    // if (value != null) {
    // if (!editor.getText().equalsIgnoreCase(value)) {
    // editor.setValue(value);
    // }
    // } else {
    // editor.setValue(null);
    // }
    // }
    private void setEditorValue() {
        if (editor == null) {
            return;
        }
        String value = getSelectedDateAsString();
        if (!Objects.equals(editor.getValue(), value)) {
            editor.setValue(value);
        }
    }

    private void updateFormatters() {
        Locale locale = getDisplayLocale();
        strictDateFormatter = ThaiBuddhistDateUtils.createStrictDateFormatter(dateFormatPattern, locale);
        displayDateFormatter = DateTimeFormatter.ofPattern(dateFormatPattern)
                .withLocale(locale)
                .withChronology(isBuddhistEraLanguage() ? ThaiBuddhistChronology.INSTANCE : IsoChronology.INSTANCE);
    }

    public PickerLanguage getLanguage() {
        return language;
    }

    public void setLanguage(PickerLanguage language) {
        if (language == null) {
            throw new IllegalArgumentException("language can't be null");
        }
        if (this.language == language) {
            return;
        }
        this.language = language;

        if (!separatorCustomized) {
            setSeparatorInternal(getDefaultSeparator(language), false);
        }

        updateFormatters();
        this.defaultPlaceholder = null;

        if (editor != null) {
            InputUtils.changeDateFormatted(
                    editor,
                    dateFormatPattern,
                    getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED,
                    separator,
                    getDisplayLocale(),
                    isBuddhistEraLanguage(),
                    getInputValidationListener());
            setEditorValue();
        }

        if (panelDateOption != null) {
            panelDateOption.installDateOptionLabel();
        }

        header.setDate(month, year);
        refreshCurrentViewForLanguage();
    }

    public Locale getDisplayLocale() {
        return language.getLocale();
    }

    public boolean isBuddhistEraLanguage() {
        return language.isThai();
    }

    public int toDisplayYear(int isoYear) {
        return isBuddhistEraLanguage() ? isoYear + ThaiBuddhistDateUtils.BUDDHIST_YEAR_OFFSET : isoYear;
    }

    public String[] getWeekdays() {
        if (weekdays != null) {
            return Arrays.copyOf(weekdays, weekdays.length);
        }
        if (defaultWeekdays != null) {
            return Arrays.copyOf(defaultWeekdays, defaultWeekdays.length);
        }
        String[] source = isBuddhistEraLanguage() ? THAI_SHORT_WEEKDAYS : ENGLISH_SHORT_WEEKDAYS;
        return Arrays.copyOf(source, source.length);
    }

    public void setWeekdays(String[] weekdays) {
        if (weekdays == null) {
            this.weekdays = null;
        } else {
            this.weekdays = Arrays.copyOf(weekdays, weekdays.length);
        }
        refreshCurrentViewForLanguage();
    }

    public String[] getMonths() {
        if (months != null) {
            return Arrays.copyOf(months, months.length);
        }
        if (defaultMonths != null) {
            return Arrays.copyOf(defaultMonths, defaultMonths.length);
        }
        String[] source = isBuddhistEraLanguage() ? THAI_MONTHS : ENGLISH_MONTHS;
        return Arrays.copyOf(source, source.length);
    }

    public void setMonths(String[] months) {
        if (months == null) {
            this.months = null;
        } else {
            this.months = Arrays.copyOf(months, months.length);
        }
        refreshCurrentViewForLanguage();
    }

    private void refreshCurrentViewForLanguage() {
        if (selectionState == null) {
            return;
        }
        if (selectionState == SelectionState.DATE) {
            panelSlider.addSlide(createPanelDate(month, year), null);
        } else if (selectionState == SelectionState.MONTH) {
            panelSlider.addSlide(createPanelMonth(year), null);
        } else {
            panelSlider.addSlide(createPanelYear(year), null);
        }
        updateSelected();
        repaint();
        revalidate();
    }

    private static String getDefaultSeparator(PickerLanguage language) {
        return language == PickerLanguage.ENGLISH ? " to " : " ถึง ";
    }

    private InputValidationListener<LocalDate> getInputValidationListener() {
        if (inputValidationListener == null) {
            inputValidationListener = new InputValidationListener<LocalDate>() {

                @Override
                public boolean isValidation() {
                    return dateSelectionModel.getDateSelectionAble() != null;
                }

                @Override
                public void inputChanged(boolean status) {
                    checkValidation(status);
                }

                @Override
                public boolean checkSelectionAble(LocalDate date) {
                    if (dateSelectionModel.getDateSelectionAble() == null) {
                        return true;
                    }
                    return dateSelectionModel.getDateSelectionAble().isDateSelectedAble(date);
                }
            };
        }
        return inputValidationListener;
    }

    @Override
    protected String getDefaultPlaceholder() {
        if (defaultPlaceholder == null) {
            if (getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED) {
                String d = InputUtils.datePatternToInputFormat(dateFormatPattern, "-");
                defaultPlaceholder = d + separator + d;
            } else {
                defaultPlaceholder = InputUtils.datePatternToInputFormat(dateFormatPattern, "-");
            }
        }
        return defaultPlaceholder;
    }

    // @Override
    // protected void popupOpen() {
    // toDateSelectionView();
    // }
    @Override
    protected void popupOpen() {
        // เดิม: เปิดไปยัง view ปัจจุบัน
        toDateSelectionView();

        // // ให้รอจน layout/paint เสร็จ แล้วค่อยอ่านขนาดจริง
        // SwingUtilities.invokeLater(() -> {
        // System.out.println("===== DatePicker POPUP SIZE DEBUG =====");
        //
        // // ขนาดที่คาดหวังจาก layout
        // Dimension dpPref = this.getPreferredSize();
        // Dimension dpSize = this.getSize();
        // System.out.println("DatePicker.getPreferredSize(): " + dpPref);
        // System.out.println("DatePicker.getSize(): " + dpSize);
        //
        // // header
        // if (header != null) {
        // System.out.println("header.getPreferredSize(): " + header.getPreferredSize()
        // + " header.getSize(): " + header.getSize());
        // }
        //
        // // panelSlider
        // if (panelSlider != null) {
        // System.out.println("panelSlider.getPreferredSize(): " +
        // panelSlider.getPreferredSize() + " panelSlider.getSize(): " +
        // panelSlider.getSize());
        // }
        //
        // // panelDate (grid ของวัน)
        // if (panelDate != null) {
        // System.out.println("panelDate.getPreferredSize(): " +
        // panelDate.getPreferredSize() + " panelDate.getSize(): " +
        // panelDate.getSize());
        //
        // // ตรวจสอบปุ่มใน panelDate (หา max preferred size ของปุ่ม)
        // int maxW = 0, maxH = 0, btnCount = 0;
        // for (Component c : panelDate.getComponents()) {
        // if (c instanceof JButton) {
        // Dimension d = c.getPreferredSize();
        // maxW = Math.max(maxW, d.width);
        // maxH = Math.max(maxH, d.height);
        // btnCount++;
        // }
        // }
        // System.out.println("panelDate - button count: " + btnCount + ", max button
        // preferred: " + maxW + "x" + maxH);
        // }
        //
        // // ตรวจสอบ parent chain (จะช่วยให้เห็นว่า popup อยู่ใน Window หรือ JPopupMenu
        // อะไร)
        // Window w = SwingUtilities.getWindowAncestor(this);
        // if (w != null) {
        // System.out.println("Window ancestor: " + w.getClass().getName() + " bounds="
        // + w.getBounds());
        // } else {
        // System.out.println("Window ancestor: null");
        // }
        //
        // // ไล่ parent chain ของ component เพื่อ debug layout container
        // Component p = this.getParent();
        // System.out.println("Parent chain:");
        // while (p != null) {
        // System.out.println(" - " + p.getClass().getName() + " bounds=" +
        // p.getBounds() + " pref=" + p.getPreferredSize());
        // p = p.getParent();
        // }
        //
        // System.out.println("===== END POPUP SIZE DEBUG =====");
        // });
    }

    private void verifyDateSelection() {
        if (getDateSelectionMode() == DateSelectionMode.BETWEEN_DATE_SELECTED) {
            SingleDate fromDate = dateSelectionModel.getDate();
            SingleDate toDate = dateSelectionModel.getToDate();
            if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
                return;
            }
        }
        if (isCloseAfterSelected()) {
            closePopup();
        }
        fireDateSelectionChanged(new DateSelectionEvent(this));
        if (panelDateOption != null) {
            panelDateOption.setSelectedCustom();
        }
    }

    public void fireDateSelectionChanged(DateSelectionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DateSelectionListener.class) {
                ((DateSelectionListener) listeners[i + 1]).dateSelected(event);
            }
        }
    }

    private void setPanelDate(PanelDate panelDate) {
        this.panelDate = panelDate;
    }

    private void setPanelMonth(PanelMonth panelMonth) {
        PanelMonth old = this.panelMonth;
        if (old != null) {
            old.removeChangeListener(this);
        }
        this.panelMonth = panelMonth;
        this.panelMonth.addChangeListener(this);
    }

    private void setPanelYear(PanelYear panelYear) {
        PanelYear old = this.panelYear;
        if (old != null) {
            old.removeChangeListener(this);
        }
        this.panelYear = panelYear;
        this.panelYear.addChangeListener(this);
    }

    private PanelDate createPanelDate(int month, int year) {
        PanelDate panelDate = new PanelDate(this, month, year);
        setPanelDate(panelDate);
        return panelDate;
    }

    private PanelMonth createPanelMonth(int year) {
        PanelMonth panelMonth = new PanelMonth(this, year);
        setPanelMonth(panelMonth);
        return panelMonth;
    }

    private PanelYear createPanelYear(int year) {
        PanelYear panelYear = new PanelYear(this, year);
        setPanelYear(panelYear);
        return panelYear;
    }

    public static void setDefaultWeekdays(String[] defaultWeekdays) {
        if (defaultWeekdays == null) {
            DatePicker.defaultWeekdays = null;
        } else {
            DatePicker.defaultWeekdays = Arrays.copyOf(defaultWeekdays, defaultWeekdays.length);
        }
    }

    public static String[] getDefaultWeekdays() {
        if (defaultWeekdays == null) {
            return Arrays.copyOf(THAI_SHORT_WEEKDAYS, THAI_SHORT_WEEKDAYS.length);
        }
        return Arrays.copyOf(defaultWeekdays, defaultWeekdays.length);
    }

    public static void setDefaultMonths(String[] defaultMonths) {
        if (defaultMonths == null) {
            DatePicker.defaultMonths = null;
        } else {
            DatePicker.defaultMonths = Arrays.copyOf(defaultMonths, defaultMonths.length);
        }
    }

    public static String[] getDefaultMonths() {
        if (defaultMonths == null) {
            return Arrays.copyOf(THAI_MONTHS, THAI_MONTHS.length);
        }
        return Arrays.copyOf(defaultMonths, defaultMonths.length);
    }

    public enum DateSelectionMode {
        SINGLE_DATE_SELECTED, BETWEEN_DATE_SELECTED
    }

    private enum SelectionState {
        DATE, MONTH, YEAR
    }
}
