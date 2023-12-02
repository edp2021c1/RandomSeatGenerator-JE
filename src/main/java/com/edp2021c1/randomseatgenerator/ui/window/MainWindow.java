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

package com.edp2021c1.randomseatgenerator.ui.window;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.util.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    private final TableView<SeatRowData> seatTableView = new TableView<>();
    private SeatTable seatTable = null;
    private String previousSeed = "";
    private File exportDir = null;

    /**
     * Creates an instance.
     */
    public MainWindow() {
        String s = ConfigUtils.reloadConfig().lastExportDir;
        if (s != null) {
            exportDir = new File(s);
        }

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

        // 右上种子输入栏
        seedInput = createTextField("种子", 191, 26);
        randomSeedBtn = createButton("随机种子", 80, 26);
        dateAsSeedBtn = createButton("填入日期", 80, 26);
        topRightBox = createHBox(998, 60, seedInput, randomSeedBtn, dateAsSeedBtn);

        // 座位表
        initSeatTable(seatTableView, ConfigUtils.reloadConfig());

        // 右侧主体
        rightBox = createVBox(1003, 698, topRightBox, seatTableView);
        rightBox.setPadding(new Insets(0, 10, 10, 0));

        // 整体
        mainBox = createHBox(1100, 634, leftBox, rightBox);

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
                final AppConfig config = ConfigUtils.reloadConfig();
                initSeatTable(seatTableView, config);

                String seed = seedInput.getText();
                if (previousSeed.equals(seed)) {
                    randomSeedBtn.fire();
                }
                seed = seedInput.getText();

                try {
                    seatTable = new SeatGenerator().generate(config, seed);
                } catch (final IllegalConfigException e) {
                    CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                    return;
                }
                seatTableView.setItems(FXCollections.observableArrayList(SeatRowData.fromSeat(seatTable)));
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
                fc.setInitialDirectory(exportDir == null ? MetaData.USER_HOME.toFile() : exportDir);
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
                t.lastExportDir = exportDir.toString();
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
    }

    /**
     * Action to do if config is changed.
     */
    public void onConfigChanged() {
        initSeatTable(seatTableView, ConfigUtils.reloadConfig());
        previousSeed = "";
    }

}
