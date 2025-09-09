/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.i18n.I18N;
import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.function.Predicate;

@DefaultProperty("value")
public class IntegerField extends FormatableTextField {

    private final IntegerProperty value = new SimpleIntegerProperty(this, "value") {
        @Override
        protected void invalidated() {
            setText(String.valueOf(get()));
        }
    };

    private final Predicate<String> patternPredicate;

    public IntegerField(boolean unsigned, String promptTextKey) {
        super();
        setPromptText(I18N.tr(FXUtils.TR_TEXT_INPUT + promptTextKey));

        patternPredicate = unsigned ? Strings.unsignedIntegerPatternPredicate : Strings.integerPatternPredicate;

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, KP_UP -> value.set(value.get() + 1);
                case DOWN, KP_DOWN -> value.set(value.get() - 1);
            }
        });
    }

    @Override
    protected String format(final String oldValue, final String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            return newValue;
        }
        if (!patternPredicate.test(newValue)) {
            return oldValue;
        }
        value.set(Integer.parseInt(newValue));
        return newValue;
    }

    public IntegerProperty valueProperty() {
        return value;
    }

}
