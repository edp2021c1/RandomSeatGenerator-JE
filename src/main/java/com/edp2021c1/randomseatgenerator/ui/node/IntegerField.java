package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.util.Strings;
import javafx.beans.property.*;
import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Input for integer.
 */
public class IntegerField extends TextField {
    private final ObjectProperty<Pattern> pattern = new SimpleObjectProperty<>(this, "pattern");

    private final IntegerProperty value = new SimpleIntegerProperty(this, "integerValue") {
        protected void invalidated() {
            textProperty().set(getValue().toString());
        }
    };

    private final BooleanProperty unsigned = new SimpleBooleanProperty(this, "unsigned") {
        protected void invalidated() {
            pattern.set(get() ? Strings.unsignedIntegerPattern : Strings.integerPattern);
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
        promptTextProperty().set(promptText);

        unsignedProperty().set(unsigned);

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            if (newValue.isEmpty()) {
                valueProperty().set(0);
                return;
            }
            if (!patternProperty().get().matcher(newValue).matches()) {
                textProperty().set(oldValue);
            }
            valueProperty().set(Integer.parseInt(textProperty().get()));
        });

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, KP_UP -> textProperty().set(Integer.toString(Integer.parseInt(textProperty().get()) + 1));
                case DOWN, KP_DOWN -> textProperty().set(Integer.toString(Integer.parseInt(textProperty().get()) - 1));
            }
        });
    }

    /**
     * Returns the pattern used for checking the input text.
     * <p>Note that value of this property should not be changed optionally.
     *
     * @return {@link #pattern}
     */
    protected ObjectProperty<Pattern> patternProperty() {
        return pattern;
    }

    /**
     * Returns property of whether the value must be unsigned.
     *
     * @return {@link #unsigned}
     */
    public BooleanProperty unsignedProperty() {
        return unsigned;
    }

    /**
     * Returns property of the integer value.
     *
     * @return {@link #value}
     */
    public IntegerProperty valueProperty() {
        return value;
    }
}
