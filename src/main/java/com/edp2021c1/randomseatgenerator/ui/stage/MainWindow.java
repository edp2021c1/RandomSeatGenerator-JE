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
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.core.SeatTableFactory;
import com.edp2021c1.randomseatgenerator.ui.node.st.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private final SeatTableView seatTableView;
    private SeatTable seatTable = null;
    private String previousSeed = "";
    private File exportDir = Paths.get(MetaData.USER_HOME).toFile();

    /**
     * Creates an instance.
     */
    public MainWindow() {
        String s = ConfigUtils.reloadConfig().last_export_dir;
        if (s != null) {
            exportDir = new File(s);
        }

        final SeatConfig initialConfig = ConfigUtils.reloadConfig();

        final Scene scene;
        final HBox mainBox;
        final VBox leftBox;
        final Button settingsBtn;
        final Button generateBtn;
        final Button exportBtn;
        final VBox rightBox;
        final HBox topRightBox;
        final TextField seedInput;
        final Button randomSeedBtn;
        final Button dateAsSeedBtn;


        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        settingsBtn = createButton("设置", 70, 26);
        generateBtn = createButton("生成", 70, 26);
        exportBtn = createButton("导出", 70, 26);
        leftBox = createVBox(91, 711, settingsBtn, generateBtn, exportBtn);
        leftBox.setPadding(new Insets(0, 0, 0, 10));

        // 右上种子输入栏
        seedInput = createTextField("种子", 191, 26);
        randomSeedBtn = createButton("随机种子", 80, 26);
        dateAsSeedBtn = createButton("填入日期", 80, 26);
        topRightBox = createHBox(998, 60, seedInput, randomSeedBtn, dateAsSeedBtn);

        // 座位表
        seatTableView = new SeatTableView(initialConfig);

        // 右侧主体
        rightBox = createVBox(1003, 698, topRightBox, seatTableView);
        rightBox.setPadding(new Insets(0, 10, 10, 0));

        // 整体
        mainBox = createHBox(1100, 634, leftBox, rightBox);
        mainBox.setPadding(DEFAULT_PADDING);

        scene = new Scene(mainBox);
        scene.getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);

        setMargins(DEFAULT_MARGIN, settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        setGrows(Priority.ALWAYS, seatTableView, rightBox);

        setScene(scene);
        getIcons().add(new Image(MetaData.ICON_URL));
        setTitle("Random Seat Generator - 随机座位生成器");
        setOnCloseRequest(event -> System.exit(0));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> new SettingsDialog(MainWindow.this).show());

        generateBtn.setOnAction(event -> {
            try {
                final SeatConfig config = ConfigUtils.reloadConfig();
                seatTableView.setEmptySeatTable(config);
                String seed = seedInput.getText();
                if (previousSeed.equals(seed)) {
                    randomSeedBtn.fire();
                }
                seed = seedInput.getText();

                try {
                    seatTable = SeatTableFactory.generate(config, seed);
                } catch (final IllegalConfigException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                seatTableView.setSeatTable(seatTable);
                previousSeed = seed;
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

                final FileChooser fc = new FileChooser();
                fc.setTitle("导出座位表");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));
                fc.setInitialDirectory(exportDir);
                fc.setInitialFileName(String.format("%tF", new Date()));

                final File outputFile = fc.showSaveDialog(MainWindow.this);
                if (outputFile == null) {
                    return;
                }
                try {
                    SeatTableUtils.exportToExcelDocument(seatTable, outputFile, ConfigUtils.reloadConfig().export_writable);
                } catch (final IOException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(
                            Thread.currentThread(),
                            new RuntimeException(
                                    String.format("Failed to export seat table to %s.", outputFile.getAbsolutePath()),
                                    e
                            )
                    );
                }

                exportDir = outputFile.getParentFile();
                AppConfig t = new AppConfig();
                t.last_export_dir = exportDir.toString();
                ConfigUtils.saveConfig(t);
            } catch (final Throwable e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seedInput.setText(UUID.randomUUID().toString().replaceAll("-", "")));

        dateAsSeedBtn.setOnAction(event -> {
            final Date t = new Date();
            final SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
            seedInput.setText(d.format(t));
        });

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
