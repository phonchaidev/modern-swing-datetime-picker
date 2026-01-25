/*
 * The MIT License
 *
 * Copyright 2025 dev.phonchai
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
 * Font utility for the DateTime Picker library.
 * <p>
 * <b>Design Principle:</b> This library uses the host application's fonts by
 * default,
 * obtained from {@link UIManager}. No configuration is required - the picker
 * will
 * automatically match your application's look and feel.
 * <p>
 * <b>Font Resolution Order:</b>
 * <ol>
 * <li>UIManager override key (if host app explicitly sets it)</li>
 * <li>UIManager "Label.font" (standard Swing font)</li>
 * <li>System default font (fallback)</li>
 * </ol>
 * <p>
 * <b>Optional Customization:</b>
 * 
 * <pre>{@code
 * // Only if you want different fonts than your app's default
 * AppFont.setBaseFont(new Font("Segoe UI", Font.PLAIN, 14));
 * }</pre>
 *
 * @author dev.phonchai
 * @version 1.0.0
 */
public final class AppFont {

    private AppFont() {
        // Prevent instantiation
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UIManager Keys (optional overrides)
    // ═══════════════════════════════════════════════════════════════════════════

    private static final String UI_KEY_PREFIX = "dev.phonchai.DateTimePicker.";

    /** UIManager key to override base font. */
    public static final String UI_KEY_BASE_FONT = UI_KEY_PREFIX + "baseFont";

    /** UIManager key to override header font (month/year display). */
    public static final String UI_KEY_HEADER_FONT = UI_KEY_PREFIX + "headerFont";

    // ═══════════════════════════════════════════════════════════════════════════
    // Optional Setters (for host app customization)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Sets the base font for all picker components.
     * <p>
     * Call this before creating any DatePicker/TimePicker instances.
     * If not called, the library uses the application's default font from
     * UIManager.
     *
     * @param font the font to use
     */
    public static void setBaseFont(Font font) {
        UIManager.put(UI_KEY_BASE_FONT, font);
    }

    /**
     * Sets a custom header font (month/year display).
     *
     * @param font the header font
     */
    public static void setHeaderFont(Font font) {
        UIManager.put(UI_KEY_HEADER_FONT, font);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Font Getters (used by picker components)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Gets the base font for picker components.
     * <p>
     * Resolution: Override → UIManager "Label.font" → System default
     *
     * @return the base font, never null
     */
    public static Font getBaseFont() {
        // 1. Check for explicit override
        Font override = UIManager.getFont(UI_KEY_BASE_FONT);
        if (override != null) {
            return override;
        }

        // 2. Use UIManager Label.font (matches host app)
        Font labelFont = UIManager.getFont("Label.font");
        if (labelFont != null) {
            return labelFont;
        }

        // 3. Fallback to system default
        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }

    /**
     * Gets the header font (bold, +2pt from base).
     *
     * @return the header font
     */
    public static Font getHeaderFont() {
        Font override = UIManager.getFont(UI_KEY_HEADER_FONT);
        if (override != null) {
            return override;
        }

        Font base = getBaseFont();
        return base.deriveFont(Font.BOLD, base.getSize2D() + 2f);
    }

    /**
     * Gets the day label font (bold, same size as base).
     *
     * @return the day label font
     */
    public static Font getDayLabelFont() {
        Font base = getBaseFont();
        return base.deriveFont(Font.BOLD);
    }

    /**
     * Gets the day button font (same as base).
     *
     * @return the day button font
     */
    public static Font getDayButtonFont() {
        return getBaseFont();
    }

    /**
     * Gets the month button font (+2pt from base).
     *
     * @return the month button font
     */
    public static Font getMonthButtonFont() {
        Font base = getBaseFont();
        return base.deriveFont(base.getSize2D() + 2f);
    }

    /**
     * Gets the year button font (same as base).
     *
     * @return the year button font
     */
    public static Font getYearButtonFont() {
        return getBaseFont();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Deprecated methods (for backward compatibility)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * @deprecated Use {@link #getBaseFont()} instead
     */
    @Deprecated
    public static Font resolveBaseFont(Component component) {
        return getBaseFont();
    }

    /**
     * @deprecated Use {@link #getHeaderFont()} instead
     */
    @Deprecated
    public static Font resolveHeaderFont(Component component) {
        return getHeaderFont();
    }

    /**
     * @deprecated Use {@link #getDayLabelFont()} instead
     */
    @Deprecated
    public static Font resolveDayLabelFont(Component component) {
        return getDayLabelFont();
    }

    /**
     * @deprecated Use {@link #getDayButtonFont()} instead
     */
    @Deprecated
    public static Font resolveDayButtonFont(Component component) {
        return getDayButtonFont();
    }

    /**
     * @deprecated Use {@link #getMonthButtonFont()} instead
     */
    @Deprecated
    public static Font resolveMonthButtonFont(Component component) {
        return getMonthButtonFont();
    }

    /**
     * @deprecated Use {@link #getYearButtonFont()} instead
     */
    @Deprecated
    public static Font resolveYearButtonFont(Component component) {
        return getYearButtonFont();
    }
}
