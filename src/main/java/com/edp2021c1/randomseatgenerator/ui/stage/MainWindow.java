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

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.core.SeatTableFactory;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.ConfigHolder;
import com.edp2021c1.randomseatgenerator.util.config.RawAppConfig;
import com.edp2021c1.randomseatgenerator.util.logging.Logging;
import com.edp2021c1.randomseatgenerator.util.ui.UIFactory;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.util.ui.UIFactory.*;

/**
 * Main window of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class MainWindow extends Stage {
    private final SeatTableView seatTableView;
    private final SettingsDialog settingsDialog;
    private final ConfigHolder cfHolder;
    private File exportFile;
    private SeatTable seatTable = null;
    private RawAppConfig config;
    private RawAppConfig t;
    private String previousSeed = null;
    private File exportDir;

    /**
     * Creates an instance.
     */
    public MainWindow() {
        cfHolder = ConfigHolder.globalHolder();

        settingsDialog = new SettingsDialog(this);
        config = cfHolder.get();
        config.checkFormat();
        exportDir = new File(config.last_export_dir == null ? Metadata.USER_HOME : config.last_export_dir);

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        final Button settingsBtn = createButton("设置", 80, 26);
        final Button generateBtn = createButton("生成", 80, 26);
        final Button exportBtn = createButton("导出", 80, 26);
        final VBox leftBox = createVBox(settingsBtn, generateBtn, exportBtn);
        leftBox.getStyleClass().add("left");

        final Separator separator = new Separator(Orientation.VERTICAL);

        // 右上种子输入栏
        final TextField seedInput = createTextField("种子");
        final Button randomSeedBtn = createButton("随机种子", 80, 26);
        final Button dateAsSeedBtn = createButton("填入日期", 80, 26);
        final HBox topRightBox = createHBox(seedInput, randomSeedBtn, dateAsSeedBtn);

        // 座位表
        seatTableView = new SeatTableView(config.getContent());

        // 右侧主体
        final VBox rightBox = createVBox(topRightBox, seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        final HBox mainBox = createHBox(leftBox, separator, rightBox);
        mainBox.getStyleClass().add("main");

        final Scene scene = new Scene(mainBox);

        setMargins(new Insets(5), settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        setGrows(Priority.ALWAYS, seatTableView, rightBox);

        setScene(scene);
        setTitle(Metadata.TITLE);
        UIFactory.decorate(this, StageType.MAIN);
        setOnCloseRequest(event -> close());

        final StringProperty seed = seedInput.textProperty();

        final FileChooser fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> settingsDialog.show());

        generateBtn.setOnAction(event -> {
            try {
                if (Objects.equals(previousSeed, seed.get())) {
                    randomSeedBtn.fire();
                }

                config = cfHolder.get();
                if (config == null) {
                    throw new IllegalConfigException("Null config");
                }
                config.checkFormat();

                try {
                    seatTable = SeatTableFactory.generate(config.getContent(), seed.get());
                } catch (final IllegalConfigException e) {
                    CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                Logging.info("\n" + seatTable);
                seatTableView.setSeatTable(seatTable);
                previousSeed = seed.get();
            } catch (final Throwable e) {
                CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
            }
        });
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> {
            try {
                if (seatTable == null) {
                    generateBtn.fire();
                }

                fc.setInitialDirectory(exportDir);
                fc.setInitialFileName("%tF".formatted(new Date()));

                exportFile = fc.showSaveDialog(MainWindow.this);
                if (exportFile == null) {
                    return;
                }
                try {
                    SeatTables.exportToExcelDocument(seatTable, exportFile, cfHolder.get().export_writable);
                } catch (final IOException e) {
                    CrashReporter.fullCrashReporter.uncaughtException(
                            Thread.currentThread(),
                            new RuntimeException(
                                    "Failed to export seat table to " + exportFile.getAbsolutePath(),
                                    e
                            )
                    );
                }

                Logging.info("Successfully exported seat table to " + exportFile);

                exportDir = exportFile.getParentFile();
                t = new RawAppConfig();
                t.last_export_dir = exportDir.toString();
                cfHolder.set(t);
            } catch (final Throwable e) {
                CrashReporter.fullCrashReporter.uncaughtException(Thread.currentThread(), e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seedInput.setText(Strings.randomString(30)));

        dateAsSeedBtn.setOnAction(event -> seed.set(Strings.nowStr()));

        if (OperatingSystem.getCurrent().isMac()) {
            setFullScreenExitHint("按 Esc / Cmd+Shift+F 退出全屏");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> close();
                    case F -> setFullScreen(event.isControlDown() != isFullScreen());
                    case COMMA -> settingsBtn.fire();
                    case S -> exportBtn.fire();
                    case R -> randomSeedBtn.fire();
                    case D -> dateAsSeedBtn.fire();
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

        setOnShown(event -> {
            setMinHeight(getHeight());
            setMinWidth(getWidth());
        });
    }

    /**
     * Action to do if config is changed.
     */
    public void onConfigChanged() {
        seatTableView.setEmptySeatTable(cfHolder.get().getContent());
        previousSeed = null;
        Logging.debug("Seat table view reset");
    }

    @Override
    public void close() {
        super.close();
        System.exit(0);
    }

}
