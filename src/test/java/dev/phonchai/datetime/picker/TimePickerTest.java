package dev.phonchai.datetime.picker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TimePicker}.
 */
@DisplayName("TimePicker")
class TimePickerTest {

    private TimePicker timePicker;

    @BeforeEach
    void setUp() {
        timePicker = new TimePicker();
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should create TimePicker with default values")
        void shouldCreateWithDefaults() {
            assertThat(timePicker).isNotNull();
            assertThat(timePicker.getLanguage()).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should default to 12-hour view")
        void shouldDefaultTo12HourView() {
            assertThat(timePicker.is24HourView()).isFalse();
        }
    }

    @Nested
    @DisplayName("Time Selection")
    class TimeSelectionTests {

        @Test
        @DisplayName("should set and get selected time")
        void shouldSetAndGetSelectedTime() {
            LocalTime time = LocalTime.of(14, 30);
            timePicker.setSelectedTime(time);

            assertThat(timePicker.getSelectedTime()).isEqualTo(time);
        }

        @Test
        @DisplayName("should clear selected time")
        void shouldClearSelectedTime() {
            timePicker.setSelectedTime(LocalTime.of(10, 15));
            timePicker.clearSelectedTime();

            assertThat(timePicker.isTimeSelected()).isFalse();
        }

        @Test
        @DisplayName("should set time to now")
        void shouldSetTimeToNow() {
            timePicker.now();

            LocalTime now = LocalTime.now();
            LocalTime selected = timePicker.getSelectedTime();

            assertThat(selected.getHour()).isEqualTo(now.getHour());
            assertThat(selected.getMinute()).isEqualTo(now.getMinute());
        }
    }

    @Nested
    @DisplayName("24-Hour View")
    class Hour24ViewTests {

        @Test
        @DisplayName("should switch to 24-hour view")
        void shouldSwitchTo24HourView() {
            timePicker.set24HourView(true);

            assertThat(timePicker.is24HourView()).isTrue();
        }

        @Test
        @DisplayName("should switch back to 12-hour view")
        void shouldSwitchTo12HourView() {
            timePicker.set24HourView(true);
            timePicker.set24HourView(false);

            assertThat(timePicker.is24HourView()).isFalse();
        }
    }

    @Nested
    @DisplayName("Language Settings")
    class LanguageTests {

        @Test
        @DisplayName("should switch to English language")
        void shouldSwitchToEnglish() {
            timePicker.setLanguage(PickerLanguage.ENGLISH);

            assertThat(timePicker.getLanguage()).isEqualTo(PickerLanguage.ENGLISH);
            assertThat(timePicker.getDisplayLocale()).isEqualTo(Locale.ENGLISH);
        }

        @Test
        @DisplayName("should switch to Thai language")
        void shouldSwitchToThai() {
            timePicker.setLanguage(PickerLanguage.ENGLISH);
            timePicker.setLanguage(PickerLanguage.THAI);

            assertThat(timePicker.getLanguage()).isEqualTo(PickerLanguage.THAI);
        }
    }

    @Nested
    @DisplayName("Time Format String")
    class TimeFormatTests {

        @Test
        @DisplayName("should format time in 24-hour format")
        void shouldFormatIn24Hour() {
            timePicker.set24HourView(true);
            timePicker.setSelectedTime(LocalTime.of(14, 30));

            String formatted = timePicker.getSelectedTimeAsString();

            assertThat(formatted).isEqualTo("14:30");
        }

        @Test
        @DisplayName("should format time in 12-hour format for English")
        void shouldFormatIn12HourEnglish() {
            timePicker.set24HourView(false);
            timePicker.setLanguage(PickerLanguage.ENGLISH);
            timePicker.setSelectedTime(LocalTime.of(14, 30));

            String formatted = timePicker.getSelectedTimeAsString();

            assertThat(formatted).containsIgnoringCase("PM");
        }
    }

    @Nested
    @DisplayName("Orientation")
    class OrientationTests {

        @Test
        @DisplayName("should set horizontal orientation")
        void shouldSetHorizontalOrientation() {
            timePicker.setOrientation(javax.swing.SwingConstants.HORIZONTAL);

            assertThat(timePicker.getOrientation())
                    .isEqualTo(javax.swing.SwingConstants.HORIZONTAL);
        }

        @Test
        @DisplayName("should set vertical orientation")
        void shouldSetVerticalOrientation() {
            timePicker.setOrientation(javax.swing.SwingConstants.VERTICAL);

            assertThat(timePicker.getOrientation())
                    .isEqualTo(javax.swing.SwingConstants.VERTICAL);
        }
    }
}
