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

import com.edp2021c1.randomseatgenerator.ui.stage.MainWindow;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.config.JSONAppConfigHolder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import lombok.val;

import java.util.Objects;

/**
 * Contains several useful methods for creating or initializing JavaFX controls.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class UIUtils {

    /**
     * Identifies the main window in the application.
     */
    public static final int MAIN_WINDOW = 0;

    /**
     * Identifies dialogs in the application.
     * <p>
     * Not resizable.
     * <p>
     * Always on the top of other windows of this app.
     */
    public static final int DIALOG = 1;

    /**
     * Identifies crash reporter windows in the application.
     * <p>
     * Icon set to the error icon.
     */
    public static final int CRASH_REPORTER = 2;

    /**
     * Dark stylesheets of the windows of the app.
     */
    private static final String[] STYLESHEETS_DARK = {"/assets/css/base.css", "/assets/css/dark.css"};
    /**
     * Light stylesheets of the windows of the app.
     */
    private static final String[] STYLESHEETS_LIGHT = {"/assets/css/base.css", "/assets/css/light.css"};

    private static final BooleanProperty globalDarkMode = new SimpleBooleanProperty(null, "globalDarkMode") {
        @Override
        protected void invalidated() {
            JSONAppConfigHolder.global().put("appearance.style.dark", get());
        }
    };
    @Getter
    private static final Image icon = new Image(Metadata.ICON_URL);

    /**
     * Don't let anyone else instantiate this class.
     */
    private UIUtils() {
    }

    /**
     * Returns the main window of the application.
     *
     * @return the app's main window
     */
    public static MainWindow getMainWindow() {
        return (MainWindow) RuntimeUtils.runtimeConfig.get("window.main");
    }

    /**
     * Sets the main window of the application if it has never been set.
     *
     * @param mainWindow to be set as the app's main window
     * @return whether the main window has never been set
     */
    public static boolean setMainWindow(final MainWindow mainWindow) {
        return RuntimeUtils.runtimeConfig.putIfAbsent("window.main", mainWindow) == null;
    }

    /**
     * Returns the global dark mode property.
     *
     * @return global dark mode property
     */
    public static BooleanProperty globalDarkModeProperty() {
        return globalDarkMode;
    }

    /**
     * Sets whether the app is shown in the dark mode.
     *
     * @param isDarkMode value
     */
    public static void setGlobalDarkMode(final Boolean isDarkMode) {
        globalDarkMode.set(Objects.requireNonNullElse(isDarkMode, true));
    }

    /**
     * Decorates the given stage by adding an icon related to
     * the window type of the stage and stylesheets.
     *
     * @param stage     to be decorated
     * @param stageType of the window
     */
    public static void decorate(final Stage stage, final int stageType) {
        switch (stageType) {
            case MAIN_WINDOW -> stage.getIcons().add(icon);
            case DIALOG -> {
                stage.getIcons().add(icon);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }
            case CRASH_REPORTER -> {
                stage.initOwner(getMainWindow());
                stage.getIcons().add(icon);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }
        }

        val stylesheets = stage.getScene().getStylesheets();
        globalDarkMode.subscribe(newValue -> {
            if (newValue) {
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

    /**
     * Sets margin of elements.
     *
     * @param margin   of the elements.
     * @param elements where margin will be set to
     */
    public static void setInsets(final Insets margin, final Node... elements) {
        for (val n : elements) {
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
     * @return the button created
     */
    public static Button createButton(final String text, final double width, final double height) {
        val btn = new Button(text);
        btn.setPrefSize(width, height);
        return btn;
    }

    /**
     * Creates a {@link VBox}.
     *
     * @param children of the box
     * @return the box created
     */
    public static VBox createVBox(final Node... children) {
        val vBox = new VBox(children);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Creates a {@link HBox}.
     *
     * @param children of the box
     * @return the box created
     */
    public static HBox createHBox(final Node... children) {
        val hBox = new HBox(children);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    /**
     * Creates a {@link TextField}.
     *
     * @param promptText prompt text of the field
     * @return the field created
     */
    public static TextField createEmptyTextField(final String promptText) {
        val t = new TextField();
        t.setPromptText(promptText);
        return t;
    }

    /**
     * Creates a {@link TextArea}.
     *
     * @param promptText of the area
     * @param width      of the area
     * @param height     of the area
     * @return the area created
     */
    public static TextArea createEmptyTextArea(final String promptText, final double width, final double height) {
        val t = new TextArea();
        t.setPromptText(promptText);
        t.setPrefSize(width, height);
        return t;
    }

}
