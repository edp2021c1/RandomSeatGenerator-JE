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
import com.edp2021c1.randomseatgenerator.ui.util.UIFactory;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.beans.property.StringProperty;
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

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.*;
import static com.edp2021c1.randomseatgenerator.util.StringUtils.DATE_FORMAT;

/**
 * Main window of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class MainWindow extends Stage {
    private final Button settingsBtn;
    private final Button generateBtn;
    private final Button exportBtn;
    private final Button randomSeedBtn;
    private final Button dateAsSeedBtn;
    private final SeatTableView seatTableView;
    private final TextField seedInput;
    private final StringProperty seed;
    private final FileChooser fc;
    private final SettingsDialog settingsDialog;
    private File exportFile;
    private SeatTable seatTable = null;
    private AppConfig config;
    private AppConfig t;
    private String previousSeed = null;
    private File exportDir;

    /**
     * Creates an instance.
     */
    public MainWindow() {
        settingsDialog = new SettingsDialog(this);
        config = ConfigUtils.getConfig();
        config.checkFormat();
        exportDir = new File(config.last_export_dir != null ? config.last_export_dir : MetaData.USER_HOME);

        final Scene scene;
        final HBox mainBox;
        final VBox leftBox;
        final Separator separator;
        final VBox rightBox;
        final HBox topRightBox;


        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        settingsBtn = createButton("设置", 80, 26);
        generateBtn = createButton("生成", 80, 26);
        exportBtn = createButton("导出", 80, 26);
        leftBox = createVBox(121, 711, settingsBtn, generateBtn, exportBtn);
        leftBox.getStyleClass().add("left");

        separator = new Separator(Orientation.VERTICAL);

        // 右上种子输入栏
        seedInput = createTextField("种子");
        randomSeedBtn = createButton("随机种子", 80, 26);
        dateAsSeedBtn = createButton("填入日期", 80, 26);
        topRightBox = createHBox(998, 60, seedInput, randomSeedBtn, dateAsSeedBtn);

        // 座位表
        seatTableView = new SeatTableView(config);

        // 右侧主体
        rightBox = createVBox(1003, 698, topRightBox, seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        mainBox = createHBox(1130, 634, leftBox, separator, rightBox);
        mainBox.getStyleClass().add("main");

        scene = new Scene(mainBox);

        setMargins(DEFAULT_MARGIN, settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        setGrows(Priority.ALWAYS, seatTableView, rightBox);

        setScene(scene);
        setTitle(MetaData.TITLE);
        UIFactory.decorate(this, StageType.MAIN);
        setOnCloseRequest(event -> close());

        seed = seedInput.textProperty();

        fc = new FileChooser();
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
                config = ConfigUtils.getConfig();
                if (config == null) {
                    throw new IllegalConfigException("Null config");
                }
                config.checkFormat();
                if (Objects.equals(previousSeed, seed.get())) {
                    randomSeedBtn.fire();
                }

                try {
                    seatTable = SeatTableFactory.generate(config, seed.get());
                } catch (final IllegalConfigException e) {
                    CrashReporter.CRASH_REPORTER_FULL.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                Logging.info("\n" + seatTable);
                seatTableView.setSeatTable(seatTable);
                previousSeed = seed.get();
            } catch (final Throwable e) {
                CrashReporter.CRASH_REPORTER_FULL.uncaughtException(Thread.currentThread(), e);
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
                    SeatTableUtils.exportToExcelDocument(seatTable, exportFile, ConfigUtils.getConfig().export_writable);
                } catch (final IOException e) {
                    CrashReporter.CRASH_REPORTER_FULL.uncaughtException(
                            Thread.currentThread(),
                            new RuntimeException(
                                    "Failed to export seat table to " + exportFile.getAbsolutePath(),
                                    e
                            )
                    );
                }

                Logging.info("Successfully exported seat table to " + exportFile);

                exportDir = exportFile.getParentFile();
                t = new AppConfig();
                t.last_export_dir = exportDir.toString();
                ConfigUtils.saveConfig(t);
            } catch (final Throwable e) {
                CrashReporter.CRASH_REPORTER_FULL.uncaughtException(Thread.currentThread(), e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seedInput.setText(StringUtils.randomString(30)));

        dateAsSeedBtn.setOnAction(event -> seed.set(DATE_FORMAT.format(new Date())));

        if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            setFullScreenExitHint("按 Esc / Cmd+Shift+F 退出全屏");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case Q -> RuntimeUtils.exit();
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
    }

    /**
     * Action to do if config is changed.
     */
    public void onConfigChanged() {
        seatTableView.setEmptySeatTable(ConfigUtils.getConfig());
        previousSeed = null;
        Logging.debug("Seat table view reset");
    }

    @Override
    public void close() {
        super.close();
        RuntimeUtils.exit();
    }

}
