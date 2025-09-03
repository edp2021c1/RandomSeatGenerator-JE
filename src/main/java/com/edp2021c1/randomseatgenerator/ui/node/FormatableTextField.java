/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.ui.node;

import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Text field class providing a convenient way to format inputted text.
 *
 * @author Calboot
 * @since 1.5.2
 */
public abstract class FormatableTextField extends TextField {

    /**
     * Creates an instance.
     *
     * @param formatter used for formatting the text, inputting the old and new value, and returning the formatted value
     *
     * @return the field created
     */
    public static FormatableTextField of(final BiFunction<String, String, String> formatter) {
        return new FormatableTextField() {
            @Override
            protected String format(final String oldValue, final String newValue) {
                return formatter.apply(oldValue, newValue);
            }
        };
    }

    /**
     * Constructor.
     */
    protected FormatableTextField() {
        textProperty().subscribe((oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            setText(format(oldValue, newValue));
        });
    }

    /**
     * Formats the value of the text.
     *
     * @param oldValue of the text
     * @param newValue of the text
     *
     * @return the formatted value of the text
     */
    protected abstract String format(String oldValue, String newValue);

}
