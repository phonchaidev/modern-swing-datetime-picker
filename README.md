# Modern Swing DateTime Picker

The most advanced, comprehensive, and modern Date-Time picker component for Java Swing desktop UIs. Featuring full support for the Thai Buddhist Era (พ.ศ.) and seamless integration with FlatLaf.

## Features

- 📅 **DatePicker** - Single date and date range selection
- ⏰ **TimePicker** - 12h/24h time selection with clock UI
- 📆 **DatetimePicker** - Combined date and time picker
- 🇹🇭 **Thai Buddhist Calendar** - Native พ.ศ. (Buddhist Era) support
- 🌐 **Multi-language** - Thai and English localization
- 🎨 **FlatLaf Integration** - Modern look with dark/light theme support
- 🔧 **Thai Date/Time Formatting** - Built-in utilities for พ.ศ. formatting

## Requirements

- Java 21+
- FlatLaf 3.7+
- MigLayout Swing 11.4+

## Installation

### Maven
```xml
<dependency>
    <groupId>dev.phonchai</groupId>
    <artifactId>modern-swing-datetime-picker</artifactId>
    <version>1.2.1</version>
</dependency>
```

## Usage

### DatePickerField (Recommended)
```java
import dev.phonchai.datetime.picker.DatePickerField;

DatePickerField dateField = new DatePickerField("เลือกวันที่");
panel.add(dateField);

// Get date
LocalDate date = dateField.getSelectedDate();

// Get date as Thai Buddhist Era (พ.ศ.)
String thaiDate = dateField.getSelectedDateAsBuddhistEra();     // "24/01/2569"
String fullThai = dateField.getSelectedDateAsFullThai();        // "24 มกราคม 2569"
String custom = dateField.getSelectedDateAsBuddhistEra("d MMM yyyy");  // "24 ม.ค. 2569"

// Get date as ISO for API
String isoDate = dateField.getSelectedDateAsIso();              // "2026-01-24"
```

### TimePickerField
```java
import dev.phonchai.datetime.picker.TimePickerField;

TimePickerField timeField = new TimePickerField("เลือกเวลา", true); // 24-hour
panel.add(timeField);

// Get time
LocalTime time = timeField.getSelectedTime();

// Get time as string
String timeStr = timeField.getSelectedTimeAsString();           // "14:30"
String timeThai = timeField.getSelectedTimeAsThai();            // "14:30 น."
String custom = timeField.getSelectedTimeAsString("HH:mm:ss");  // "14:30:00"
```

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

### Thai Buddhist Era Utility
```java
import dev.phonchai.datetime.picker.util.ThaiBuddhistDateUtils;

LocalDate date = LocalDate.of(2026, 1, 24);
LocalTime time = LocalTime.of(14, 30);

// Date formatting
String thaiDate = ThaiBuddhistDateUtils.formatAsThaiDate(date);       // "24/01/2569"
String fullThai = ThaiBuddhistDateUtils.formatAsThaiDateFull(date);   // "24 มกราคม 2569"
String isoDate = ThaiBuddhistDateUtils.formatAsIso(date);             // "2026-01-24"
String custom = ThaiBuddhistDateUtils.formatAsBuddhistEra(date, "d MMM yy");  // "24 ม.ค. 69"

// Time formatting
String timeStr = ThaiBuddhistDateUtils.formatTime(time);              // "14:30"
String timeThai = ThaiBuddhistDateUtils.formatTimeWithSuffix(time);   // "14:30 น."

// DateTime combining
LocalDateTime dateTime = ThaiBuddhistDateUtils.combine(date, time);
String thaiDateTime = ThaiBuddhistDateUtils.formatAsThaiDateTime(dateTime);   // "24/01/2569 14:30"
String isoDateTime = ThaiBuddhistDateUtils.formatAsIsoDateTime(dateTime);     // "2026-01-24T14:30:00"

// Conversion helpers
LocalDate buddhistDate = ThaiBuddhistDateUtils.toBuddhistEraDate(date);  // +543 years
LocalDate isoDateBack = ThaiBuddhistDateUtils.toIsoDate(buddhistDate);   // -543 years
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

### DatePickerField

| Method | Description |
|--------|-------------|
| `getSelectedDate()` | Get selected date as LocalDate |
| `getSelectedDateAsBuddhistEra()` | Get date as "24/01/2569" |
| `getSelectedDateAsBuddhistEra(pattern)` | Get date with custom pattern |
| `getSelectedDateAsFullThai()` | Get date as "24 มกราคม 2569" |
| `getSelectedDateAsIso()` | Get date as "2026-01-24" |

### TimePickerField

| Method | Description |
|--------|-------------|
| `getSelectedTime()` | Get selected time as LocalTime |
| `getSelectedTimeAsString()` | Get time as "14:30" |
| `getSelectedTimeAsString(pattern)` | Get time with custom pattern |
| `getSelectedTimeAsThai()` | Get time as "14:30 น." |

### ThaiBuddhistDateUtils

| Method | Description |
|--------|-------------|
| `formatAsThaiDate(date)` | Format as "24/01/2569" |
| `formatAsThaiDateFull(date)` | Format as "24 มกราคม 2569" |
| `formatAsIso(date)` | Format as "2026-01-24" |
| `formatAsBuddhistEra(date, pattern)` | Format with custom pattern |
| `formatTime(time)` | Format as "14:30" |
| `formatTimeWithSuffix(time)` | Format as "14:30 น." |
| `combine(date, time)` | Combine to LocalDateTime |
| `formatAsThaiDateTime(dt)` | Format as "24/01/2569 14:30" |
| `toBuddhistEraDate(date)` | Convert ISO → Buddhist Era |
| `toIsoDate(buddhistDate)` | Convert Buddhist Era → ISO |

## Credits

This library is based on [DJ-Raven's swing-datetime-picker](https://github.com/DJ-Raven/swing-datetime-picker).
Modified to support Thai Buddhist Era (พ.ศ.) calendar system.

## License

MIT License
