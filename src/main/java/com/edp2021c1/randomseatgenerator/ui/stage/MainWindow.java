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
import com.edp2021c1.randomseatgenerator.ui.node.st.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.*;

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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private File exportFile;
    private SeatTable seatTable = null;
    private AppConfig config = ConfigUtils.reloadConfig();
    private AppConfig t;
    private String previousSeed = "";
    private File exportDir = Paths.get(MetaData.USER_HOME).toFile();

    /**
     * Creates an instance.
     */
    public MainWindow() {
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
        seedInput = createTextField("种子", 191, 26);
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
        scene.getStylesheets().addAll(MetaData.DEFAULT_STYLESHEETS);

        setMargins(DEFAULT_MARGIN, settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        setGrows(Priority.ALWAYS, seatTableView, rightBox);

        setScene(scene);
        getIcons().add(new Image(MetaData.ICON_URL));
        setTitle("Random Seat Generator - 随机座位生成器");
        setOnCloseRequest(event -> System.exit(0));

        String s = config.last_export_dir;
        if (s != null) {
            exportDir = new File(s);
        }

        seed = seedInput.textProperty();

        fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> new SettingsDialog(MainWindow.this).show());

        generateBtn.setOnAction(event -> {
            try {
                config = ConfigUtils.reloadConfig();
                seatTableView.setEmptySeatTable(config);
                if (previousSeed.equals(seed.get())) {
                    randomSeedBtn.fire();
                }

                try {
                    seatTable = SeatTableFactory.generate(config, seed.get());
                } catch (final IllegalConfigException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                seatTableView.setSeatTable(seatTable);
                previousSeed = seed.get();
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> {
            try {
                if (seatTable == null) {
                    generateBtn.fire();
                }

                fc.setInitialDirectory(exportDir);
                fc.setInitialFileName(String.format("%tF", new Date()));

                exportFile = fc.showSaveDialog(MainWindow.this);
                if (exportFile == null) {
                    return;
                }
                try {
                    SeatTableUtils.exportToExcelDocument(seatTable, exportFile, ConfigUtils.reloadConfig().export_writable);
                } catch (final IOException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(
                            Thread.currentThread(),
                            new RuntimeException(
                                    String.format("Failed to export seat table to %s.", exportFile.getAbsolutePath()),
                                    e
                            )
                    );
                }

                exportDir = exportFile.getParentFile();
                t = new AppConfig();
                t.last_export_dir = exportDir.toString();
                ConfigUtils.saveConfig(t);
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seedInput.setText(UUID.randomUUID().toString().replaceAll("-", "")));

        dateAsSeedBtn.setOnAction(event -> seed.set(dateFormat.format(new Date())));

        if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            setFullScreenExitHint("按 Esc / Cmd+Shift+F 退出全屏");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case F -> setFullScreen(event.isControlDown() != isFullScreen());
                    case W -> {
                        close();
                        System.exit(0);
                    }
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
        seatTableView.setEmptySeatTable(ConfigUtils.reloadConfig());
        previousSeed = "";
    }

}
