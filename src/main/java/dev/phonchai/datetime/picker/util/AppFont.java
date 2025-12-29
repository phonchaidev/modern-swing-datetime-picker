/*
 * The MIT License
 *
 * Copyright 2025 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.phonchai.datetime.picker.util;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author user
 */
public class AppFont {

    private AppFont() {
    }

    public static final Font BASE = new Font("TH SarabunPSK", Font.PLAIN, 20);
    public static final Font HEADER = new Font("TH SarabunPSK", Font.BOLD, 22);
    public static final Font DAY_LABEL = new Font("TH SarabunPSK", Font.BOLD, 20);   // weekdays heading
    public static final Font DAY_BUTTON = new Font("TH SarabunPSK", Font.PLAIN, 20);  // วันที่แต่ละปุ่ม
    public static final Font MONTH_BUTTON = new Font("TH SarabunPSK", Font.PLAIN, 22);
    public static final Font YEAR_BUTTON = new Font("TH SarabunPSK", Font.PLAIN, 20);
    public static final Font EDITOR = new Font("TH SarabunPSK", Font.PLAIN, 18);       // ตัวหนังสือใน JFormattedTextFieldแ

    /**
     * UIManager keys to override fonts without modifying this library.
     * Set these before creating the components (recommended), or call
     * {@code SwingUtilities.updateComponentTreeUI(window)} after changing.
     */
    public static final String UI_KEY_PREFIX = "SFIS.DateTimePicker.";
    public static final String UI_KEY_BASE_FONT = UI_KEY_PREFIX + "baseFont";
    public static final String UI_KEY_HEADER_FONT = UI_KEY_PREFIX + "headerFont";
    public static final String UI_KEY_DAY_LABEL_FONT = UI_KEY_PREFIX + "dayLabelFont";
    public static final String UI_KEY_DAY_BUTTON_FONT = UI_KEY_PREFIX + "dayButtonFont";
    public static final String UI_KEY_MONTH_BUTTON_FONT = UI_KEY_PREFIX + "monthButtonFont";
    public static final String UI_KEY_YEAR_BUTTON_FONT = UI_KEY_PREFIX + "yearButtonFont";

    public static void setBaseFont(Font font) {
        UIManager.put(UI_KEY_BASE_FONT, font);
    }

    public static void setHeaderFont(Font font) {
        UIManager.put(UI_KEY_HEADER_FONT, font);
    }

    public static void setDayLabelFont(Font font) {
        UIManager.put(UI_KEY_DAY_LABEL_FONT, font);
    }

    public static void setDayButtonFont(Font font) {
        UIManager.put(UI_KEY_DAY_BUTTON_FONT, font);
    }

    public static void setMonthButtonFont(Font font) {
        UIManager.put(UI_KEY_MONTH_BUTTON_FONT, font);
    }

    public static void setYearButtonFont(Font font) {
        UIManager.put(UI_KEY_YEAR_BUTTON_FONT, font);
    }

    public static Font resolveBaseFont(Component component) {
        return resolveFont(component, UI_KEY_BASE_FONT, getDefaultUiFont(component, BASE));
    }

    public static Font resolveHeaderFont(Component component) {
        Font base = resolveBaseFont(component);
        Font derived = deriveFont(base, base.getStyle() | Font.BOLD, 2f);
        return resolveFont(component, UI_KEY_HEADER_FONT, derived != null ? derived : HEADER);
    }

    public static Font resolveDayLabelFont(Component component) {
        Font base = resolveBaseFont(component);
        Font derived = deriveFont(base, base.getStyle() | Font.BOLD, 0f);
        return resolveFont(component, UI_KEY_DAY_LABEL_FONT, derived != null ? derived : DAY_LABEL);
    }

    public static Font resolveDayButtonFont(Component component) {
        Font base = resolveBaseFont(component);
        return resolveFont(component, UI_KEY_DAY_BUTTON_FONT, base != null ? base : DAY_BUTTON);
    }

    public static Font resolveMonthButtonFont(Component component) {
        Font base = resolveBaseFont(component);
        Font derived = deriveFont(base, base.getStyle(), 2f);
        return resolveFont(component, UI_KEY_MONTH_BUTTON_FONT, derived != null ? derived : MONTH_BUTTON);
    }

    public static Font resolveYearButtonFont(Component component) {
        Font base = resolveBaseFont(component);
        return resolveFont(component, UI_KEY_YEAR_BUTTON_FONT, base != null ? base : YEAR_BUTTON);
    }

    private static Font resolveFont(Component component, String uiKey, Font fallback) {
        Font override = UIManager.getFont(uiKey);
        if (override != null) {
            return override;
        }
        return fallback;
    }

    private static Font getDefaultUiFont(Component component, Font ultimateFallback) {
        if (component != null) {
            Font componentFont = component.getFont();
            if (componentFont != null) {
                return componentFont;
            }
        }
        Font uiFont = UIManager.getFont("defaultFont");
        if (uiFont != null) {
            return uiFont;
        }
        uiFont = UIManager.getFont("Label.font");
        if (uiFont != null) {
            return uiFont;
        }
        return ultimateFallback;
    }

    private static Font deriveFont(Font base, int style, float addSize) {
        if (base == null) {
            return null;
        }
        float newSize = Math.max(1f, base.getSize2D() + addSize);
        int newStyle = style;
        if (base.getStyle() == newStyle && base.getSize2D() == newSize) {
            return base;
        }
        return base.deriveFont(newStyle, newSize);
    }
}
