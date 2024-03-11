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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Input for integer.
 *
 * @author Calboot
 * @since 1.5.0
 */
@DefaultProperty("value")
public class IntegerField extends TextField {

    /**
     * The pattern used for checking the input text.
     * <p>Note that value of this property should not be changed optionally.
     */
    private final ObjectProperty<Pattern> pattern = new SimpleObjectProperty<>(this, "pattern");

    private final IntegerProperty value = new SimpleIntegerProperty(this, "value") {
        @Override
        protected void invalidated() {
            setText(String.valueOf(get()));
        }
    };

    /**
     * Constructs an instance.
     *
     * @param unsigned   whether the value should be unsigned
     * @param promptText pre-set value of {@link #promptTextProperty()}
     */
    public IntegerField(final boolean unsigned, final String promptText) {
        super();
        setPromptText(promptText);

        pattern.set(unsigned ? Strings.unsignedIntegerPattern : Strings.integerPattern);

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            if (newValue == null || newValue.isEmpty()) {
                setValue(0);
                return;
            }
            if (!pattern.get().matcher(newValue).matches()) {
                setText(oldValue);
                return;
            }
            setValue(Integer.parseInt(getText()));
        });

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, KP_UP -> setText(Integer.toString(Integer.parseInt(getText()) + 1));
                case DOWN, KP_DOWN -> setText(Integer.toString(Integer.parseInt(getText()) - 1));
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

    /**
     * Sets the value of {@link #valueProperty()}
     *
     * @param val value
     */
    public void setValue(final Integer val) {
        value.setValue(val);
    }
}
