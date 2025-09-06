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

package com.edp2021c1.randomseatgenerator.ui;

import com.edp2021c1.randomseatgenerator.ui.stage.PrimaryWindowManager;
import com.edp2021c1.randomseatgenerator.ui.stage.StageType;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;
import com.edp2021c1.randomseatgenerator.v2.util.Metadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * Contains several useful methods for creating or initializing JavaFX controls.
 *
 * @author Calboot
 * @since 1.3.3
 */
public final class FXUtils {

    /**
     * Dark stylesheets of the windows of the app.
     */
    private static final String[] STYLESHEETS_DARK = {"/assets/css/base.css", "/assets/css/dark.css"};

    /**
     * Light stylesheets of the windows of the app.
     */
    private static final String[] STYLESHEETS_LIGHT = {"/assets/css/base.css", "/assets/css/light.css"};

    @Getter
    private static final Image icon = new Image(Metadata.ICON_URL);

    private static final BooleanProperty globalDarkMode = new SimpleBooleanProperty(null, "globalDarkMode", AppSettings.config.darkMode) {

        @Override
        protected void invalidated() {
            AppSettings.config.darkMode = get();
        }
    };

    /**
     * Key of the property of the height of the main window
     */
    @Deprecated
    public static final String KEY_MAIN_WINDOW_HEIGHT = "appearance.window.main.height";

    /**
     * Key of the property of the width of the main window
     */
    @Deprecated
    public static final String KEY_MAIN_WINDOW_WIDTH = "appearance.window.main.width";

    /**
     * Key of the property of the x position of the main window
     */
    @Deprecated
    public static final String KEY_MAIN_WINDOW_X = "appearance.window.main.x";

    /**
     * Key of the property of the y position of the main window
     */
    @Deprecated
    public static final String KEY_MAIN_WINDOW_Y = "appearance.window.main.y";

    /**
     * Returns the global dark mode property.
     *
     * @return global dark mode property
     */
    public static BooleanProperty globalDarkModeProperty() {
        return globalDarkMode;
    }

    /**
     * Decorates the given stage by adding an icon related to
     * the window type of the stage and stylesheets.
     *
     * @param stage to be decorated
     */
    public static void decorate(Stage stage, StageType stageType) {
        stage.getIcons().add(icon);
        if (stageType.level() > 0) {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        }
        if (stageType.level() > 1) {
            stage.initOwner(PrimaryWindowManager.getPrimaryStage());
        }

        stage.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> stylesheets = newValue.getStylesheets();
                globalDarkMode.subscribe(n -> {
                    if (n) {
                        stylesheets.setAll(STYLESHEETS_DARK);
                        return;
                    }
                    stylesheets.setAll(STYLESHEETS_LIGHT);
                });
                if (globalDarkMode.get()) {
                    stylesheets.setAll(STYLESHEETS_DARK);
                    return;
                }
                stylesheets.setAll(STYLESHEETS_LIGHT);
            }
        });
    }

    /**
     * Sets margin of elements.
     *
     * @param margin   of the elements.
     * @param elements where margin will be set to
     */
    public static void setInsets(final Insets margin, final Node... elements) {
        for (Node n : elements) {
            HBox.setMargin(n, margin);
            VBox.setMargin(n, margin);
        }
    }

    /**
     * Creates a {@link Button}.
     *
     * @param text   of the button
     * @param width  of the button
     * @param height of the button
     *
     * @return the button created
     */
    public static Button createButton(final String text, final double width, final double height) {
        Button btn = new Button(text);
        btn.setPrefSize(width, height);
        return btn;
    }

    /**
     * Creates a {@link VBox}.
     *
     * @param children of the box
     *
     * @return the box created
     */
    public static VBox createVBox(final Node... children) {
        VBox vBox = new VBox(children);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Creates a {@link HBox}.
     *
     * @param children of the box
     *
     * @return the box created
     */
    public static HBox createHBox(final Node... children) {
        HBox hBox = new HBox(children);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    /**
     * Creates a {@link TextField}.
     *
     * @param promptText prompt text of the field
     *
     * @return the field created
     */
    public static TextField createEmptyTextField(final String promptText) {
        TextField t = new TextField();
        t.setPromptText(promptText);
        return t;
    }

    /**
     * Creates a {@link TextArea}.
     *
     * @param promptText of the area
     * @param width      of the area
     * @param height     of the area
     *
     * @return the area created
     */
    public static TextArea createEmptyTextArea(final String promptText, final double width, final double height) {
        TextArea t = new TextArea();
        t.setPromptText(promptText);
        t.setPrefSize(width, height);
        return t;
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private FXUtils() {
    }

}
