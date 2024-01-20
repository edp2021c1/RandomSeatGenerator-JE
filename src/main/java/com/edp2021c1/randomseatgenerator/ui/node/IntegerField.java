package com.edp2021c1.randomseatgenerator.ui.node;

import com.edp2021c1.randomseatgenerator.util.Strings;
import javafx.beans.property.*;
import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.regex.Pattern;

public class IntegerField extends TextField {
    private final ObjectProperty<Pattern> pattern = new SimpleObjectProperty<>(this, "pattern");

    private final IntegerProperty integerValue = new SimpleIntegerProperty(this, "integerValue") {
        protected void invalidated() {
            textProperty().set(getValue().toString());
        }
    };

    private final BooleanProperty unsigned = new SimpleBooleanProperty(this, "unsigned") {
        protected void invalidated() {
            pattern.set(get() ? Strings.unsignedIntegerPattern : Strings.integerPattern);
        }
    };

    public IntegerField(final boolean unsigned, final String promptText) {
        super();
        promptTextProperty().set(promptText);

        unsignedProperty().set(unsigned);

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(oldValue, newValue)) {
                return;
            }
            if (newValue.isEmpty()) {
                integerValueProperty().set(0);
                return;
            }
            if (!patternProperty().get().matcher(newValue).matches()) {
                textProperty().set(oldValue);
            }
            integerValueProperty().set(Integer.parseInt(textProperty().get()));
        });

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, KP_UP -> textProperty().set(Integer.toString(Integer.parseInt(textProperty().get()) + 1));
                case DOWN, KP_DOWN -> textProperty().set(Integer.toString(Integer.parseInt(textProperty().get()) - 1));
            }
        });
    }

    protected ObjectProperty<Pattern> patternProperty() {
        return pattern;
    }

    public BooleanProperty unsignedProperty() {
        return unsigned;
    }

    public IntegerProperty integerValueProperty() {
        return integerValue;
    }
}
