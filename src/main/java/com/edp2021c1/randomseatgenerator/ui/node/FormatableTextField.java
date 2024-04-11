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
     * Creates an instance.
     *
     * @param formatter used for formatting the text, inputting the old and new value, and returning the formatted value
     * @return the field created
     */
    public static FormatableTextField of(final BiFunction<String, String, String> formatter) {
        return new FormatableTextField() {
            @Override
            protected String format(String oldValue, String newValue) {
                return formatter.apply(oldValue, newValue);
            }
        };
    }

    protected abstract String format(String oldValue, String newValue);

}
