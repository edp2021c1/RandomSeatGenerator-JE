package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.util.Strings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

import java.util.Objects;

/**
 * Input for integer.
 */
public class IntegerField extends TextField {

    /**
     * The pattern used for checking the input text.
     * <p>Note that value of this property should not be changed optionally.
     */
    private final StringProperty pattern = new SimpleStringProperty(this, "pattern");

    private final IntegerProperty value = new SimpleIntegerProperty(this, "value") {
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
            if (newValue.isEmpty()) {
                setValue(0);
                return;
            }
            if (!newValue.matches(pattern.get())) {
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
