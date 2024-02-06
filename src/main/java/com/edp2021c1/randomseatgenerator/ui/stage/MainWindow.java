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
    private final StringProperty seed;
    private final ObjectProperty<File> exportDir;
    private File exportFile;
    private SeatTable seatTable = null;
    private RawAppConfig t;
    private String previousSeed = null;

    /**
     * Creates an instance.
     */
    public MainWindow() {
        cfHolder = ConfigHolder.globalHolder();

        settingsDialog = new SettingsDialog(this);
        t = cfHolder.get();

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

        seed = seedInput.textProperty();

        // 座位表
        seatTableView = new SeatTableView(t.getContent());

        // 右侧主体
        final VBox rightBox = createVBox(topRightBox, seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        final HBox mainBox = createHBox(leftBox, separator, rightBox);
        mainBox.getStyleClass().add("main");

        final Scene scene = new Scene(mainBox);

        setInsets(new Insets(5), settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        VBox.setVgrow(seatTableView, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        setScene(scene);
        setTitle(Metadata.TITLE);
        UIFactory.decorate(this, StageType.MAIN);
        setOnCloseRequest(event -> close());

        final FileChooser fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));

        exportDir = fc.initialDirectoryProperty();
        exportDir.addListener((observable, oldValue, newValue) -> {
            try {
                IOUtils.replaceWithDirectory(newValue.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        exportDir.set(t.last_export_dir == null ? SeatTable.DEFAULT_EXPORTING_DIR.toFile() : new File(t.last_export_dir));

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

                t = cfHolder.get();
                if (t == null) {
                    throw new IllegalConfigException("Null config");
                }
                t.checkFormat();

                seatTable = SeatTableFactory.generate(t.getContent(), seed.get());
                Logging.info("\n" + seatTable);
                seatTableView.setSeatTable(seatTable);
                previousSeed = seed.get();
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> {
            try {
                if (seatTable == null) {
                    generateBtn.fire();
                }

                fc.setInitialFileName("%tF".formatted(new Date()));

                exportFile = fc.showSaveDialog(MainWindow.this);
                if (exportFile == null) {
                    return;
                }
                seatTable.exportToExcelDocument(exportFile.toPath(), cfHolder.get().export_writable);

                Logging.info("Successfully exported seat table to " + exportFile);

                exportDir.set(exportFile.getParentFile());
                t = new RawAppConfig();
                t.last_export_dir = exportDir.get().toString();
                cfHolder.set(t);
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seed.set(Strings.randomString(30)));

        dateAsSeedBtn.setOnAction(event -> seed.set(Strings.nowStr()));

        if (Utils.isMac()) {
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
