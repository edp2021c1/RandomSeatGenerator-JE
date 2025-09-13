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
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.exception.ExceptionHandler;
import com.edp2021c1.randomseatgenerator.util.i18n.TranslatableNotice;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;
import static com.edp2021c1.randomseatgenerator.ui.FXUtils.*;

public final class PrimaryWindowManager {

    @Getter
    private static Stage primaryStage = null;

    private static SeatTableView seatTableView;

    private static StringProperty seed;

    private static ObjectProperty<SeatTable> seatTable;

    private static SeatGenerator seatGenerator;

    private static FileChooser fileChooser;

    private static String previousSeed = null;

    private static boolean generated;

    private static void init0() {
        FXUtils.decorate(primaryStage, StageType.MAIN_WINDOW);

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        Button settingsBtn = createButton("settings", 80, 26);
        Button generateBtn = createButton("generate", 80, 26);
        Button exportBtn   = createButton("export", 80, 26);
        VBox   leftBox     = createVBox(settingsBtn, generateBtn, exportBtn);
        leftBox.getStyleClass().add("left");

        // 右上种子输入栏
        TextField seedInput     = createEmptyTextField("seedInput");
        Button    randomSeedBtn = createButton("randomSeed", 80, 26);
        Button    timeAsSeedBtn = createButton("fillInTime", 80, 26);

        seed = seedInput.textProperty();

        // 座位表
        seatTableView = new SeatTableView(AppSettings.config.seatConfig);
        seatGenerator = new SeatGenerator(AppSettings.config.seatConfig);
        seatTable = seatTableView.seatTableProperty();

        // 右侧主体
        VBox rightBox = createVBox(createHBox(seedInput, randomSeedBtn, timeAsSeedBtn), seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        HBox mainBox = createHBox(leftBox, new Separator(Orientation.VERTICAL), rightBox);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, timeAsSeedBtn);
        VBox.setVgrow(seatTableView, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        primaryStage.setScene(new Scene(mainBox));
        primaryStage.setTitle(Metadata.TITLE);

        fileChooser = new FileChooser();
        fileChooser.setTitle("exportSeatTable");
        fileChooser.getExtensionFilters().addAll(
                FXUtils.extensionFilter("xlsx"),
                FXUtils.extensionFilter("xls"),
                FXUtils.extensionFilter("csv")
        );

        fileChooser.setInitialDirectory(Metadata.DATA_DIR.toFile());

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> SettingsDialog.getSettingsDialog().showAndWait());

        generateBtn.setOnAction(event -> generateSeatTable());
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> exportSeatTable());

        seedInput.setOnAction(event -> generateSeatTable());

        randomSeedBtn.setOnAction(event -> generateRandomSeed());

        timeAsSeedBtn.setOnAction(event -> generateDateSeed());

        if (AppSettings.mac) {
            primaryStage.setFullScreenExitHint("");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> primaryStage.close();
                    case F -> primaryStage.setFullScreen(event.isControlDown() != primaryStage.isFullScreen());
                    case COMMA -> settingsBtn.fire();
                    case S -> exportBtn.fire();
                    case R -> randomSeedBtn.fire();
                    case D -> timeAsSeedBtn.fire();
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case S -> exportBtn.fire();
                    case R -> randomSeedBtn.fire();
                }
            });
        }

        primaryStage.setOnCloseRequest(event -> primaryStage.close());
    }

    private static void generateSeatTable() {
        try {
            if (Objects.equals(previousSeed, seed.get())) {
                generateRandomSeed();
            }

            String seed1 = seed.get();
            seatTable.set(seatGenerator.generate(seed1));
            LOGGER.info("{}{}", System.lineSeparator(), seatTable.get().toString());
            previousSeed = seed1;
            generated = true;
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    private static void generateRandomSeed() {
        seed.set(Strings.randomString(30));
    }

    private static void exportSeatTable() {
        try {
            if (!generated) {
                generateSeatTable();
            }

            fileChooser.setInitialDirectory(
                    IOUtils.getClosestDirectory(Objects.requireNonNullElseGet(fileChooser.getInitialDirectory(), Metadata.DATA_DIR::toFile))
            );

            fileChooser.setInitialFileName(Strings.nowStrShort());

            File exportFile = fileChooser.showSaveDialog(primaryStage);
            if (exportFile == null) {
                return;
            }
            LOGGER.debug("Exporting seat table to \"{}\"", exportFile);
            SeatUtils.export(seatTable.get(), exportFile.toPath());
            LOGGER.info("Successfully export seat table");

            MessageDialog.showMessage(primaryStage, TranslatableNotice.of("exportSuccess", System.lineSeparator(), exportFile));

            fileChooser.setInitialDirectory(exportFile.getParentFile());
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    private static void generateDateSeed() {
        seed.set(Strings.nowStr());
    }

    public static void init(Stage primaryStage) {
        if (PrimaryWindowManager.primaryStage != null) {
            throw new IllegalStateException("Primary stage already initialized");
        }
        PrimaryWindowManager.primaryStage = primaryStage;

        init0();
    }

    public static void configChanged() {
        seatTableView.setEmptySeatTable(AppSettings.config.seatConfig);
        seatGenerator = new SeatGenerator(AppSettings.config.seatConfig);
        generated = false;
        previousSeed = null;
    }

}
