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

package com.edp2021c1.randomseatgenerator.ui.util;

import com.edp2021c1.randomseatgenerator.util.MetaData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Contains several useful methods for creating or initializing JavaFX controls.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class UIFactory {
    /**
     * Default margin.
     */
    public static final Insets DEFAULT_MARGIN = new Insets(5);

    /**
     * Decorates the given stage by adding an icon and
     * style sheets related to the window type to the stage.
     *
     * @param stage to be decorated
     * @param type  of the window
     * @see StageType
     */
    public static void decorate(final Stage stage, final StageType type) {
        stage.getScene().getStylesheets().addAll(MetaData.DEFAULT_STYLESHEETS);
        switch (type) {
            case ERROR -> {
                stage.getIcons().add(new Image(MetaData.ERROR_ICON_URL));
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            case MAIN -> {
                stage.getIcons().add(new Image(MetaData.ICON_URL));
                stage.setOnCloseRequest(event -> System.exit(0));
            }
            default -> {
                stage.getIcons().add(new Image(MetaData.ICON_URL));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }
        }
    }

    /**
     * Sets margin of elements.
     *
     * @param margin   of the elements.
     * @param elements where margin will be set to
     */
    public static void setMargins(final Insets margin, final Node... elements) {
        for (final Node n : elements) {
            HBox.setMargin(n, margin);
            VBox.setMargin(n, margin);
        }
    }

    /**
     * Sets {@code HGrow} and {@code VGrow} of elements.
     *
     * @param priority {@code HGrow} and {@code VGrow} value to be set to the elements
     * @param elements where priority will be set to
     */
    public static void setGrows(final Priority priority, final Node... elements) {
        for (final Node n : elements) {
            HBox.setHgrow(n, priority);
            VBox.setVgrow(n, priority);
        }
    }

    /**
     * Creates a {@code Button}.
     *
     * @param text   of the button
     * @param width  of the button
     * @param height of the button
     * @return the button created
     */
    public static Button createButton(final String text, final double width, final double height) {
        final Button btn = new Button(text);
        btn.setPrefSize(width, height);
        return btn;
    }

    /**
     * Creates a {@code VBox}.
     *
     * @param width    of the box
     * @param height   of the box
     * @param children of the box
     * @return the box created
     */
    public static VBox createVBox(final double width, final double height, final Node... children) {
        final VBox vBox = new VBox(children);
        vBox.setPrefSize(width, height);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Creates a {@code HBox}.
     *
     * @param width    of the box
     * @param height   of the box
     * @param children of the box
     * @return the box created
     */
    public static HBox createHBox(final double width, final double height, final Node... children) {
        final HBox hBox = new HBox(children);
        hBox.setPrefSize(width, height);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    /**
     * Creates a {@code TextField}.
     *
     * @param promptText of the field
     * @return the field created
     */
    public static TextField createTextField(final String promptText) {
        final TextField t = new TextField();
        t.setPromptText(promptText);
        return t;
    }

    /**
     * Creates a {@code TextArea}.
     *
     * @param promptText of the area
     * @param width      of the area
     * @param height     of the area
     * @return the area created
     */
    public static TextArea createTextArea(final String promptText, final double width, final double height) {
        final TextArea t = new TextArea();
        t.setPromptText(promptText);
        t.setPrefSize(width, height);
        return t;
    }

    /**
     * Creates a {@code ImageView}.
     *
     * @param imageUrl  URL of the image of the view
     * @param fitWidth  of the view
     * @param fitHeight of the view
     * @return the view created
     */
    public static ImageView createImageView(final String imageUrl, final double fitWidth, final double fitHeight) {
        final ImageView i = new ImageView(new Image(imageUrl));
        i.setFitWidth(fitWidth);
        i.setFitHeight(fitHeight);
        i.setPickOnBounds(true);
        i.setPreserveRatio(true);
        return i;
    }

    /**
     * Type of stage decorated.
     */
    public enum StageType {
        /**
         * Identifies the main window in the application.
         */
        MAIN,
        /**
         * Identifies dialogs in the application.
         * <p>
         * Not resizable.
         * <p>
         * Always on the top of other windows of this app.
         */
        DIALOG,
        /**
         * Identifies crash reporter windows in the application.
         * <p>
         * Icon set to the error icon.
         */
        ERROR
    }
}
