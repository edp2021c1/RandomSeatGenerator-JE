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

package com.edp2021c1.randomseatgenerator.ui;

import com.edp2021c1.randomseatgenerator.AppSettings;
import com.edp2021c1.randomseatgenerator.ui.stage.PrimaryWindowManager;
import com.edp2021c1.randomseatgenerator.ui.stage.StageType;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.jetbrains.annotations.PropertyKey;

import java.io.IOException;
import java.net.URI;

import static com.edp2021c1.randomseatgenerator.util.i18n.I18N.i18n;

public final class FXUtils {

    private static final String[] STYLESHEETS_DARK = {"/assets/css/base.css", "/assets/css/dark.css"};

    private static final String[] STYLESHEETS_LIGHT = {"/assets/css/base.css", "/assets/css/light.css"};

    @Getter
    private static final Image icon = new Image(Metadata.ICON_URL);

    private static final BooleanProperty globalDarkMode = new SimpleBooleanProperty(null, "globalDarkMode", AppSettings.config.darkMode) {

        @Override
        protected void invalidated() {
            AppSettings.config.darkMode = get();
            try {
                AppSettings.saveConfig();
            } catch (IOException ignored) {
            }
        }
    };

    public static BooleanProperty globalDarkModeProperty() {
        return globalDarkMode;
    }

    public static void decorate(Stage stage, StageType stageType) {
        stage.getIcons().add(icon);
        if (stageType.level > 0) {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        }
        if (stageType.level > 1) {
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

    public static void setInsets(Insets margin, Node... elements) {
        for (Node n : elements) {
            HBox.setMargin(n, margin);
            VBox.setMargin(n, margin);
        }
    }

    public static Button createButton(@PropertyKey(resourceBundle = "assets.lang.I18N") String key, final double width, final double height) {
        Button btn = new Button(i18n(key));
        btn.setPrefSize(width, height);
        return btn;
    }

    public static VBox createVBox(Node... children) {
        VBox vBox = new VBox(children);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    public static HBox createHBox(Node... children) {
        HBox hBox = new HBox(children);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    public static TextField createEmptyTextField(@PropertyKey(resourceBundle = "assets.lang.I18N") String promptTextKey) {
        TextField t = new TextField();
        t.setPromptText(i18n(promptTextKey));
        return t;
    }

    public static TextArea createEmptyTextArea(@PropertyKey(resourceBundle = "assets.lang.I18N") String promptTextKey, double width, double height) {
        TextArea t = new TextArea();
        t.setPromptText(i18n(promptTextKey));
        t.setPrefSize(width, height);
        return t;
    }

    public static TextArea createEmptyTextArea(double width, double height) {
        TextArea t = new TextArea();
        t.setPrefSize(width, height);
        return t;
    }

    public static Tab createTab(@PropertyKey(resourceBundle = "assets.lang.I18N") String textKey, Node children) {
        return new Tab(i18n(textKey), children);
    }

    public static CheckBox createCheckBox(@PropertyKey(resourceBundle = "assets.lang.I18N") String textKey) {
        return new CheckBox(i18n(textKey));
    }

    public static Hyperlink createHyperlink(URI uri, @PropertyKey(resourceBundle = "assets.lang.I18N") String key, Object... args) {
        Hyperlink link = new Hyperlink(i18n(key, args));
        link.setOnAction(event -> DesktopUtils.browseIfSupported(uri));
        return link;
    }

    public static String translateTitle(@PropertyKey(resourceBundle = "assets.lang.I18N") String key) {
        return i18n(key);
    }

    public static FileChooser.ExtensionFilter extensionFilter(String extension) {
        return new FileChooser.ExtensionFilter(i18n("fileExtension." + extension), "*." + extension);
    }

}
