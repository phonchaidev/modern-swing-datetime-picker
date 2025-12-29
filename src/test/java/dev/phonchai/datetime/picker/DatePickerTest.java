package dev.phonchai.datetime.picker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DatePicker}.
 */
@DisplayName("DatePicker")
class DatePickerTest {

    private DatePicker datePicker;

    @BeforeEach
    void setUp() {
        datePicker = new DatePicker();
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should create DatePicker with default values")
        void shouldCreateWithDefaults() {
            assertThat(datePicker).isNotNull();
            assertThat(datePicker.getDateSelectionMode())
                    .isEqualTo(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
            assertThat(datePicker.getLanguage()).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should have default Thai language")
        void shouldHaveDefaultThaiLanguage() {
            assertThat(datePicker.getLanguage()).isEqualTo(PickerLanguage.THAI);
        }
    }

    @Nested
    @DisplayName("Date Selection")
    class DateSelectionTests {

        @Test
        @DisplayName("should set and get selected date")
        void shouldSetAndGetSelectedDate() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            datePicker.setSelectedDate(date);

            assertThat(datePicker.getSelectedDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("should clear selected date")
        void shouldClearSelectedDate() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            datePicker.setSelectedDate(date);
            datePicker.clearSelectedDate();

            assertThat(datePicker.isDateSelected()).isFalse();
        }

        @Test
        @DisplayName("should set date range in BETWEEN mode")
        void shouldSetDateRange() {
            datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 12, 31);

            datePicker.setSelectedDateRange(from, to);

            assertThat(datePicker.getSelectedDate()).isEqualTo(from);
            assertThat(datePicker.getDateSelectionModel().getToDate().toLocalDate()).isEqualTo(to);
        }
    }

    @Nested
    @DisplayName("Language Settings")
    class LanguageTests {

        @Test
        @DisplayName("should switch to English language")
        void shouldSwitchToEnglish() {
            datePicker.setLanguage(PickerLanguage.ENGLISH);

            assertThat(datePicker.getLanguage()).isEqualTo(PickerLanguage.ENGLISH);
            assertThat(datePicker.getDisplayLocale()).isEqualTo(Locale.ENGLISH);
        }

        @Test
        @DisplayName("should switch to Thai language")
        void shouldSwitchToThai() {
            datePicker.setLanguage(PickerLanguage.ENGLISH);
            datePicker.setLanguage(PickerLanguage.THAI);

            assertThat(datePicker.getLanguage()).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should support Thai Buddhist calendar")
        void shouldSupportThaiBuddhistCalendar() {
            datePicker.setLanguage(PickerLanguage.THAI);

            assertThat(datePicker.isBuddhistEraLanguage()).isTrue();
        }
    }

    @Nested
    @DisplayName("Date Format")
    class DateFormatTests {

        @Test
        @DisplayName("should set custom date format")
        void shouldSetCustomDateFormat() {
            datePicker.setDateFormat("yyyy-MM-dd");

            assertThat(datePicker.getDateFormat()).isEqualTo("yyyy-MM-dd");
        }

        @Test
        @DisplayName("should have default date format")
        void shouldHaveDefaultDateFormat() {
            assertThat(datePicker.getDateFormat()).isEqualTo("dd/MM/yyyy");
        }
    }

    @Nested
    @DisplayName("Selection Mode")
    class SelectionModeTests {

        @Test
        @DisplayName("should switch to date range mode")
        void shouldSwitchToRangeMode() {
            datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);

            assertThat(datePicker.getDateSelectionMode())
                    .isEqualTo(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        }

        @Test
        @DisplayName("should switch back to single date mode")
        void shouldSwitchToSingleMode() {
            datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
            datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);

            assertThat(datePicker.getDateSelectionMode())
                    .isEqualTo(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        }
    }

    @Nested
    @DisplayName("Separator")
    class SeparatorTests {

        @Test
        @DisplayName("should have Thai default separator")
        void shouldHaveThaiDefaultSeparator() {
            assertThat(datePicker.getSeparator()).isEqualTo(" ถึง ");
        }

        @Test
        @DisplayName("should set custom separator")
        void shouldSetCustomSeparator() {
            datePicker.setSeparator(" - ");

            assertThat(datePicker.getSeparator()).isEqualTo(" - ");
        }
    }

    @Nested
    @DisplayName("Now Button")
    class NowTests {

        @Test
        @DisplayName("should set date to today")
        void shouldSetDateToToday() {
            datePicker.now();

            assertThat(datePicker.getSelectedDate()).isEqualTo(LocalDate.now());
        }
    }
}
