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

package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.AppSettings;
import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Notice;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.edp2021c1.randomseatgenerator.ui.FXUtils.createButton;

public final class CrashReporterDialog extends Stage {

    public static void showCrashReporter(final Notice msg) {
        DesktopUtils.runOnFXThread(() -> new CrashReporterDialog(msg).showAndWait());
    }

    private CrashReporterDialog(final Notice msg) {
        super();
        FXUtils.decorate(this, StageType.CRASH_REPORTER);

        Label     preLabelBeforeLink = new Label("Something's wrong... Click");
        Hyperlink here               = new Hyperlink("here");
        Label     preLabelAfterLink  = new Label("to copy the error message");
        preLabelBeforeLink.getStyleClass().add("err-pre-label");
        here.getStyleClass().add("err-pre-label");
        preLabelAfterLink.getStyleClass().add("err-pre-label");

        TextArea mainText = new TextArea(msg.message());
        mainText.setEditable(false);
        mainText.getStyleClass().add("err-main-text");

        Button confirmBtn = createButton("close", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        Button copyBtn = createButton("copyAndClose", 80, 26);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);
        buttonBar.setPrefHeight(66);
        buttonBar.getStyleClass().add("bottom");

        VBox mainBox = new VBox(new HBox(preLabelBeforeLink, here, preLabelAfterLink), mainText, buttonBar);
        mainBox.getStyleClass().add("main");

        setScene(new Scene(mainBox));
        setMaxWidth(1440);
        setMaxHeight(810);
        setTitle(msg.title());

        here.setOnAction(event -> DesktopUtils.copyPlainText(mainText.getText()));

        copyBtn.setOnAction(event -> {
            DesktopUtils.copyPlainText(mainText.getText());
            close();
        });

        if (AppSettings.mac) {
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

}