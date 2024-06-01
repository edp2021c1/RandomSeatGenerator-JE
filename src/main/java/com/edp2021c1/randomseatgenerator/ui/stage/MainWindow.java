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

import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.ui.UIUtils;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.*;
import com.edp2021c1.randomseatgenerator.util.config.AppPropertiesHolder;
import com.edp2021c1.randomseatgenerator.util.config.SeatConfigHolder;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.val;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.ui.UIUtils.*;

/**
 * Main window of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class MainWindow extends Stage {

    private static final LoggerWrapper LOGGER = LoggerWrapper.global();

    @Getter
    private static final MainWindow mainWindow = new MainWindow();

    private final SeatTableView seatTableView;

    private final SeatConfigHolder cfHolder;

    private final StringProperty seed;

    private final ObjectProperty<SeatTable> seatTable;

    private String previousSeed = null;

    private boolean generated;

    /**
     * Creates an instance.
     */
    private MainWindow() {
        super();
        if (!setMainWindow(this)) {
            throw new IllegalStateException("Main window can only be constructed once");
        }

        cfHolder = SeatConfigHolder.global();

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        val settingsBtn = createButton("设置", 80, 26);
        val generateBtn = createButton("生成", 80, 26);
        val exportBtn   = createButton("导出", 80, 26);
        val leftBox     = createVBox(settingsBtn, generateBtn, exportBtn);
        leftBox.getStyleClass().add("left");

        // 右上种子输入栏
        val seedInput     = createEmptyTextField("种子");
        val randomSeedBtn = createButton("随机种子", 80, 26);
        val dateAsSeedBtn = createButton("填入日期", 80, 26);

        seed = seedInput.textProperty();

        // 座位表
        val config = cfHolder.getClone();
        try {
            config.check();
        } catch (final IllegalConfigException e) {
            throw new IllegalConfigException(List.of(
                    new IllegalConfigException("Illegal config loaded from " + cfHolder.getConfigPath()),
                    e
            ));
        }
        seatTableView = new SeatTableView(config);

        seatTable = seatTableView.seatTableProperty();

        // 右侧主体
        val rightBox = createVBox(createHBox(seedInput, randomSeedBtn, dateAsSeedBtn), seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        val mainBox = createHBox(leftBox, new Separator(Orientation.VERTICAL), rightBox);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        VBox.setVgrow(seatTableView, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        setScene(new Scene(mainBox));
        setTitle(Metadata.TITLE);
        decorate(this, MAIN_WINDOW);

        val fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel 97-2004 工作薄", "*.xls"),
                new FileChooser.ExtensionFilter("CSV 逗号分隔", "*.csv")
        );

        fc.setInitialDirectory(new File(Objects.requireNonNullElse(
                AppPropertiesHolder.global().getProperty(KEY_EXPORT_DIR_PREVIOUS),
                SeatTable.DEFAULT_EXPORTING_DIR.toString()
        )));

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> SettingsDialog.getSettingsDialog().showAndWait());

        generateBtn.setOnAction(event -> {
            try {
                if (Objects.equals(previousSeed, seed.get())) {
                    randomSeedBtn.fire();
                }

                val seed1 = seed.get();
                seatTable.set(SeatTable.generate(cfHolder.getClone().checkAndReturn(), seed1));
                LOGGER.info(System.lineSeparator() + seatTable.get());
                previousSeed = seed1;
                generated = true;
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> {
            try {
                if (!generated) {
                    generateBtn.fire();
                }

                fc.setInitialFileName("%tF".formatted(new Date()));

                var tmp = fc.getInitialDirectory();
                if (tmp != null) {
                    while (!tmp.isDirectory()) {
                        tmp = tmp.getParentFile();
                    }
                    fc.setInitialDirectory(tmp);
                }

                val exportFile = fc.showSaveDialog(this);
                if (exportFile == null) {
                    return;
                }
                seatTable.get().exportToChart(exportFile.toPath(), UIUtils.exportWritableProperty().get());

                MessageDialog.showMessage(this, Notice.of("成功导出座位表到\n" + exportFile));

                fc.setInitialDirectory(exportFile.getParentFile());
                AppPropertiesHolder.global().setProperty(KEY_EXPORT_DIR_PREVIOUS, exportFile.getParentFile().toString());
            } catch (final Throwable e) {
                CrashReporter.report(e);
            }
        });

        seedInput.setOnAction(event -> generateBtn.fire());

        randomSeedBtn.setOnAction(event -> seed.set(Strings.randomString(30)));

        dateAsSeedBtn.setOnAction(event -> seed.set(Strings.nowStr()));

        if (OperatingSystem.MAC == OperatingSystem.getCurrent()) {
            setFullScreenExitHint("");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case Q -> System.exit(0);
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

        try {
            setHeight(AppPropertiesHolder.global().getDouble(KEY_MAIN_WINDOW_HEIGHT));
        } catch (final NumberFormatException | NullPointerException ignored) {
        }

        try {
            setWidth(AppPropertiesHolder.global().getDouble(KEY_MAIN_WINDOW_WIDTH));
        } catch (final NumberFormatException | NullPointerException ignored) {
        }

        try {
            setX(AppPropertiesHolder.global().getDouble(KEY_MAIN_WINDOW_X));
        } catch (final NumberFormatException | NullPointerException ignored) {
        }

        try {
            setY(AppPropertiesHolder.global().getDouble(KEY_MAIN_WINDOW_Y));
        } catch (final NumberFormatException | NullPointerException ignored) {
        }

        heightProperty().subscribe(newValue -> {
            if (!isFullScreen()) {
                AppPropertiesHolder.global().setProperty(KEY_MAIN_WINDOW_HEIGHT, newValue.doubleValue());
            }
        });
        widthProperty().subscribe(newValue -> {
            if (!isFullScreen()) {
                AppPropertiesHolder.global().setProperty(KEY_MAIN_WINDOW_WIDTH, newValue.doubleValue());
            }
        });
        xProperty().subscribe(newValue -> {
            if (!isFullScreen()) {
                AppPropertiesHolder.global().setProperty(KEY_MAIN_WINDOW_X, newValue.doubleValue());
            }
        });
        yProperty().subscribe(newValue -> {
            if (!isFullScreen()) {
                AppPropertiesHolder.global().setProperty(KEY_MAIN_WINDOW_Y, newValue.doubleValue());
            }
        });

        setOnCloseRequest(event -> close());
    }

    /**
     * Action to do if config is changed.
     */
    public void configChanged() {
        seatTableView.setEmptySeatTable(cfHolder.getClone());
        generated = false;
        previousSeed = null;
    }

}
