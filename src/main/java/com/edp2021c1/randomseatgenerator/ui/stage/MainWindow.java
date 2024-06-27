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
import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.Metadata;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.PathWrapper;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.config.AppPropertiesHolder;
import com.edp2021c1.randomseatgenerator.util.config.CachedMapSeatConfig;
import com.edp2021c1.randomseatgenerator.util.config.SeatConfigHolder;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.util.useroutput.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.useroutput.Notice;
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
import lombok.Getter;
import lombok.val;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.ui.FXUtils.*;
import static com.edp2021c1.randomseatgenerator.util.Log.LOG;

/**
 * Main window of the application.
 *
 * @author Calboot
 * @since 1.3.3
 */
public final class MainWindow extends DecoratedStage {

    @Getter
    private static final MainWindow mainWindow = new MainWindow();

    private final SeatTableView seatTableView;

    private final SeatConfigHolder cfHolder;

    private final StringProperty seed;

    private final ObjectProperty<SeatTable> seatTable;

    private final CachedMapSeatConfig config;

    private final FileChooser fileChooser;

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
        config = cfHolder.getClone();

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

        fileChooser = new FileChooser();
        fileChooser.setTitle("导出座位表");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel 97-2004 工作薄", "*.xls"),
                new FileChooser.ExtensionFilter("CSV 逗号分隔", "*.csv")
        );

        fileChooser.setInitialDirectory(new File(Objects.requireNonNullElseGet(
                AppPropertiesHolder.global().getProperty(KEY_EXPORT_DIR_PREVIOUS),
                SeatTable.DEFAULT_EXPORTING_DIR::toString
        )));

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

        dateAsSeedBtn.setOnAction(event -> generateDateSeed());

        if (OperatingSystem.MAC == OperatingSystem.getCurrent()) {
            setFullScreenExitHint("");
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

    private void generateSeatTable() {
        try {
            if (Objects.equals(previousSeed, seed.get())) {
                generateRandomSeed();
            }

            val seed1 = seed.get();
            seatTable.set(SeatTable.generate(cfHolder.getClone().checkAndReturn(), seed1));
            LOG.info(System.lineSeparator() + seatTable.get());
            previousSeed = seed1;
            generated = true;
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    private void generateRandomSeed() {
        seed.set(Strings.randomString(30));
    }

    private void exportSeatTable() {
        try {
            if (!generated) {
                generateSeatTable();
            }

            fileChooser.setInitialFileName(Strings.nowStr());

            val tmp = fileChooser.getInitialDirectory();
            val p   = tmp == null ? SeatTable.DEFAULT_EXPORTING_DIR : PathWrapper.wrap(tmp);
            fileChooser.setInitialDirectory(p.getDirParent().toFile());

            val exportFile = fileChooser.showSaveDialog(this);
            if (exportFile == null) {
                return;
            }
            LOG.debug("Exporting seat table to \"%s\"".formatted(exportFile));
            seatTable.get().exportToChart(exportFile.toPath(), FXUtils.exportWritableProperty().get());
            LOG.info("Successfully export seat table");

            MessageDialog.showMessage(this, Notice.of("成功导出座位表到\n" + exportFile));

            fileChooser.setInitialDirectory(exportFile.getParentFile());
            AppPropertiesHolder.global().setProperty(KEY_EXPORT_DIR_PREVIOUS, exportFile.getParentFile().toString());
        } catch (final Throwable e) {
            CrashReporter.report(e);
        }
    }

    private void generateDateSeed() {
        seed.set(Strings.nowStr());
    }

    @Override
    public StageType getStageType() {
        return StageType.MAIN_WINDOW;
    }

    /**
     * Action to do if config is changed.
     */
    public void configChanged() {
        seatTableView.setEmptySeatTable(config.putAllAndReturn(cfHolder.getClone()).refresh());
        generated = false;
        previousSeed = null;
    }

}
