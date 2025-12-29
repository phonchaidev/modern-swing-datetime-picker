package dev.phonchai.datetime.picker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PickerLanguage}.
 */
@DisplayName("PickerLanguage")
class PickerLanguageTest {

    @Nested
    @DisplayName("Locale Mapping")
    class LocaleMappingTests {

        @Test
        @DisplayName("THAI should have Thai locale")
        void thaiShouldHaveThaiLocale() {
            assertThat(PickerLanguage.THAI.getLocale().getLanguage()).isEqualTo("th");
        }

        @Test
        @DisplayName("ENGLISH should have English locale")
        void englishShouldHaveEnglishLocale() {
            assertThat(PickerLanguage.ENGLISH.getLocale()).isEqualTo(Locale.ENGLISH);
        }
    }

    @Nested
    @DisplayName("fromLocale")
    class FromLocaleTests {

        @Test
        @DisplayName("should return THAI for Thai locale")
        void shouldReturnThaiForThaiLocale() {
            Locale thaiLocale = Locale.forLanguageTag("th-TH");
            assertThat(PickerLanguage.fromLocale(thaiLocale)).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should return ENGLISH for English locale")
        void shouldReturnEnglishForEnglishLocale() {
            assertThat(PickerLanguage.fromLocale(Locale.ENGLISH)).isEqualTo(PickerLanguage.ENGLISH);
        }

        @Test
        @DisplayName("should return null for null locale")
        void shouldReturnNullForNullLocale() {
            assertThat(PickerLanguage.fromLocale(null)).isNull();
        }
    }

    @Nested
    @DisplayName("fromString")
    class FromStringTests {

        @Test
        @DisplayName("should parse 'THAI'")
        void shouldParseThai() {
            assertThat(PickerLanguage.fromString("THAI")).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should parse 'th'")
        void shouldParseThLowercase() {
            assertThat(PickerLanguage.fromString("th")).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should parse 'ENGLISH'")
        void shouldParseEnglish() {
            assertThat(PickerLanguage.fromString("ENGLISH")).isEqualTo(PickerLanguage.ENGLISH);
        }

        @Test
        @DisplayName("should parse 'en'")
        void shouldParseEnLowercase() {
            assertThat(PickerLanguage.fromString("en")).isEqualTo(PickerLanguage.ENGLISH);
        }

        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNullInput() {
            assertThat(PickerLanguage.fromString(null)).isNull();
        }

        @Test
        @DisplayName("should return null for empty string")
        void shouldReturnNullForEmptyString() {
            assertThat(PickerLanguage.fromString("")).isNull();
        }
    }

    @Nested
    @DisplayName("fromValue")
    class FromValueTests {

        @Test
        @DisplayName("should return PickerLanguage as-is")
        void shouldReturnPickerLanguageAsIs() {
            assertThat(PickerLanguage.fromValue(PickerLanguage.THAI)).isEqualTo(PickerLanguage.THAI);
        }

        @Test
        @DisplayName("should convert Locale to PickerLanguage")
        void shouldConvertLocale() {
            assertThat(PickerLanguage.fromValue(Locale.ENGLISH)).isEqualTo(PickerLanguage.ENGLISH);
        }

        @Test
        @DisplayName("should convert String to PickerLanguage")
        void shouldConvertString() {
            assertThat(PickerLanguage.fromValue("THAI")).isEqualTo(PickerLanguage.THAI);
        }
    }

    @Nested
    @DisplayName("isThai")
    class IsThaiTests {

        @Test
        @DisplayName("THAI.isThai() should return true")
        void thaiIsThaiShouldReturnTrue() {
            assertThat(PickerLanguage.THAI.isThai()).isTrue();
        }

        @Test
        @DisplayName("ENGLISH.isThai() should return false")
        void englishIsThaiShouldReturnFalse() {
            assertThat(PickerLanguage.ENGLISH.isThai()).isFalse();
        }
    }

    @Nested
    @DisplayName("UI Key")
    class UIKeyTests {

        @Test
        @DisplayName("should have correct UI key prefix")
        void shouldHaveCorrectUIKeyPrefix() {
            assertThat(PickerLanguage.UI_KEY_LANGUAGE).startsWith("dev.phonchai.");
        }
    }
}
