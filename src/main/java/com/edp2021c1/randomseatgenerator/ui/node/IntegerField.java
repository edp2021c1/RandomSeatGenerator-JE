/*
 * RandomSeatGenerator
 * Copyright © 2023 EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.util.Strings;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.function.Predicate;

/**
 * Input for integer.
 *
 * @author Calboot
 * @since 1.5.0
 */
@DefaultProperty("value")
public class IntegerField extends FormatableTextField {

    private final IntegerProperty value = new SimpleIntegerProperty(this, "value") {
        @Override
        protected void invalidated() {
            setText(String.valueOf(get()));
        }
    };

    private final Predicate<String> patternPredicate;

    /**
     * Constructs an instance.
     *
     * @param unsigned   whether the value should be unsigned
     * @param promptText pre-set value of {@link #promptTextProperty()}
     */
    public IntegerField(final boolean unsigned, final String promptText) {
        super();
        setPromptText(promptText);

        patternPredicate = unsigned ? Strings.unsignedIntegerPatternPredicate : Strings.integerPatternPredicate;

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, KP_UP -> value.set(value.get() + 1);
                case DOWN, KP_DOWN -> value.set(value.get() - 1);
            }
        });
    }

    /**
     * Returns property of the integer value.
     *
     * @return {@link #value}
     */
    public IntegerProperty valueProperty() {
        return value;
    }

    @Override
    protected String format(final String oldValue, final String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            return "0";
        }
        if (!patternPredicate.test(newValue)) {
            return oldValue;
        }
        value.set(Integer.parseInt(newValue));
        return newValue;
    }

}
