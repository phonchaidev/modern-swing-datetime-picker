# DateTime Picker Library

A reusable Thai Buddhist calendar DateTime picker component for Java Swing desktop UIs with FlatLaf integration.

## Features

- 📅 **DatePicker** - Single date and date range selection
- ⏰ **TimePicker** - 12h/24h time selection with clock UI
- 📆 **DatetimePicker** - Combined date and time picker
- 🇹🇭 **Thai Buddhist Calendar** - Native พ.ศ. (Buddhist Era) support
- 🌐 **Multi-language** - Thai and English localization
- 🎨 **FlatLaf Integration** - Modern look with dark/light theme support

## Requirements

- Java 21+
- FlatLaf 3.7+
- MigLayout Swing 11.4+

## Installation

### Maven
```xml
<dependency>
    <groupId>dev.phonchai</groupId>
    <artifactId>datetime-picker</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Basic DatePicker
```java
import dev.phonchai.datetime.picker.DatePicker;
import dev.phonchai.datetime.picker.PickerLanguage;

// Create date picker
DatePicker datePicker = new DatePicker();
datePicker.setLanguage(PickerLanguage.THAI);
datePicker.setDateFormat("dd/MM/yyyy");

// Listen for selection
datePicker.addDateSelectionListener(e -> {
    System.out.println("Selected: " + e.getDate());
});

// Get selected date
LocalDate date = datePicker.getSelectedDate();
```

### Date Range Selection
```java
DatePicker rangePicker = new DatePicker();
rangePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
rangePicker.setSeparator(" ถึง ");

// Get date range
LocalDate fromDate = rangePicker.getSelectedDate();
LocalDate toDate = rangePicker.getSelectedToDate();
```

### TimePicker
```java
import dev.phonchai.datetime.picker.TimePicker;

TimePicker timePicker = new TimePicker();
timePicker.set24HourView(false); // Use 12-hour format
timePicker.setLanguage(PickerLanguage.THAI);

timePicker.addTimeSelectionListener(e -> {
    System.out.println("Selected: " + e.getTime());
});

LocalTime time = timePicker.getSelectedTime();
```

### DatetimePicker
```java
import dev.phonchai.datetime.picker.DatetimePicker;

DatetimePicker datetimePicker = new DatetimePicker();
datetimePicker.setLanguage(PickerLanguage.THAI);

// Get date and time
LocalDate date = datetimePicker.getDatePicker().getSelectedDate();
LocalTime time = datetimePicker.getTimePicker().getSelectedTime();
```

### With Text Field Editor
```java
JFormattedTextField textField = new JFormattedTextField();
DatePicker picker = new DatePicker();
picker.setEditor(textField);

// The text field will sync with the picker
panel.add(textField);
```

### Global Language Configuration
```java
// Set default language for all pickers via UIManager
UIManager.put(PickerLanguage.UI_KEY_LANGUAGE, PickerLanguage.THAI);
```

## Date Selection Modes

| Mode | Description |
|------|-------------|
| `SINGLE_DATE_SELECTED` | Select a single date |
| `BETWEEN_DATE_SELECTED` | Select a date range (from-to) |

## Language Support

| Language | Calendar | Date Format |
|----------|----------|-------------|
| `PickerLanguage.THAI` | พ.ศ. | dd/MM/yyyy |
| `PickerLanguage.ENGLISH` | A.D. | MM/dd/yyyy |

## API Reference

### DatePicker

| Method | Description |
|--------|-------------|
| `setSelectedDate(LocalDate)` | Set the selected date |
| `getSelectedDate()` | Get the selected date |
| `setDateSelectionMode(DateSelectionMode)` | Set single or range mode |
| `setLanguage(PickerLanguage)` | Set Thai or English |
| `setDateFormat(String)` | Set date format pattern |
| `setEditor(JFormattedTextField)` | Attach text field editor |
| `setDateSelectionAble(DateSelectionAble)` | Set date selection constraints |

### TimePicker

| Method | Description |
|--------|-------------|
| `setSelectedTime(LocalTime)` | Set the selected time |
| `getSelectedTime()` | Get the selected time |
| `set24HourView(boolean)` | Toggle 12h/24h format |
| `setLanguage(PickerLanguage)` | Set Thai or English |
| `setEditor(JFormattedTextField)` | Attach text field editor |

## License

MIT License
