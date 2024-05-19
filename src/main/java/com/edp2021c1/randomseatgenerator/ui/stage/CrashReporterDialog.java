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

package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Notice;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.val;

import static com.edp2021c1.randomseatgenerator.ui.UIUtils.*;

/**
 * Stage of FX crash reporter.
 *
 * @author Calboot
 * @since 1.4.1
 */
public class CrashReporterDialog extends Stage {

    private static Notice messageToBeShown;

    private CrashReporterDialog(final Notice msg) {
        super();

        val preLabelBeforeLink = new Label("Something's wrong... Click");
        val here               = new Hyperlink("here");
        val preLabelAfterLink  = new Label("to copy the error message");
        preLabelBeforeLink.getStyleClass().add("err-pre-label");
        here.getStyleClass().add("err-pre-label");
        preLabelAfterLink.getStyleClass().add("err-pre-label");

        val preBox = new HBox(preLabelBeforeLink, here, preLabelAfterLink);
        preBox.setAlignment(Pos.CENTER_LEFT);

        val mainText = new TextArea(msg.message());
        mainText.setEditable(false);
        mainText.getStyleClass().add("err-main-text");

        val confirmBtn = createButton("关闭", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        val copyBtn = createButton("复制并关闭", 80, 26);

        val buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);
        buttonBar.setPrefHeight(66);
        buttonBar.getStyleClass().add("bottom");

        val mainBox = new VBox(preBox, mainText, buttonBar);
        mainBox.getStyleClass().add("main");

        setScene(new Scene(mainBox));
        setMaxWidth(1440);
        setMaxHeight(810);
        setTitle(msg.title());
        decorate(this, CRASH_REPORTER);

        here.setOnAction(event -> DesktopUtils.copyPlainText(mainText.getText()));

        copyBtn.setOnAction(event -> {
            DesktopUtils.copyPlainText(mainText.getText());
            close();
        });

        if (OperatingSystem.MAC == OperatingSystem.getCurrent()) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> close();
                    case C -> DesktopUtils.copyPlainText(mainText.getText());
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (event.isControlDown() && KeyCode.C == event.getCode()) {
                    DesktopUtils.copyPlainText(mainText.getText());
                }
            });
        }
    }

    /**
     * Shows a crash reporter dialog.
     *
     * @param msg error message
     */
    public static void showCrashReporter(final Notice msg) {
        try {
            messageToBeShown = msg;
            Application.launch(CrashReporterApp.class);
        } catch (final IllegalStateException e) {
            new CrashReporterDialog(msg).showAndWait();
        }
    }

    /**
     * JavaFX application used to launch {@code CrashReporterDialog}.
     */
    public static class CrashReporterApp extends Application {

        /**
         * Default constructor.
         */
        public CrashReporterApp() {
        }

        @Override
        public void start(final Stage primaryStage) {
            new CrashReporterDialog(messageToBeShown).showAndWait();
        }

    }

}