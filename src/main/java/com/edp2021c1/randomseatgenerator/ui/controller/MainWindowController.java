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

package com.edp2021c1.randomseatgenerator.ui.controller;

import com.edp2021c1.randomseatgenerator.core.*;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import com.edp2021c1.randomseatgenerator.util.CrashReporter;
import com.edp2021c1.randomseatgenerator.util.MetaData;
import com.edp2021c1.randomseatgenerator.util.SeatUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller of {@code assets/fxml/MainWindow.fxml}
 *
 * @author Calboot
 * @since 1.0.0
 */
public class MainWindowController {

    /**
     * Decides whether the config is changed after opening {@code PreferencesDialog}.
     */
    public static boolean configIsChanged = false;
    private SeatTable seat;
    private long previousSeed = 0;
    private File exportDir;

    @FXML
    private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField seedInput;

    @FXML
    private TableView<SeatRowData> seatTable;

    @FXML
    private HBox box_1;

    @FXML
    private VBox box_2;

    @FXML
    private VBox box_3;

    @FXML
    private HBox box_4;

    @FXML
    void dateAsSeed(final ActionEvent event) {
        final Date t = new Date();
        seedInput.setText(String.format("%tY%tm%td%tH%tM%tS", t, t, t, t, t, t));
    }

    @FXML
    void exportSeatTable(final ActionEvent event) {
        try {
            if (seat == null) {
                generateSeatTable(null);
            }

            final FileChooser fc = new FileChooser();
            fc.setTitle("导出座位表");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));
            fc.setInitialDirectory(exportDir == null ? MetaData.USER_HOME.toFile() : exportDir);
            fc.setInitialFileName(String.format("%tF", new Date()));

            final File outputFile = fc.showSaveDialog(stage);
            if (outputFile == null) {
                return;
            }
            try {
                SeatUtils.exportToExcelDocument(seat, outputFile);
            } catch (final IOException e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(
                        Thread.currentThread(),
                        new RuntimeException(String.format("Failed to export seat table to %s.", outputFile.getAbsolutePath()), e)
                );
            }

            exportDir = outputFile.getParentFile();
        } catch (final Throwable e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
        }
    }

    @FXML
    void generateRandomSeed(final ActionEvent event) {
        seedInput.setText(Long.toString(new Random().nextLong()));
    }

    @FXML
    void generateSeatTable(final ActionEvent event) {
        try {
            final SeatConfig config = ConfigUtils.reloadConfig();
            initSeatTable(config);

            final long seed;
            try {
                if (Long.parseLong(seedInput.getText()) == previousSeed) {
                    generateRandomSeed(null);
                }
            } catch (final NumberFormatException e) {
                Logger.getGlobal().warning("Invalid seed.");
                generateRandomSeed(null);
            }

            seed = Long.parseLong(seedInput.getText());
            try {
                seat = new SeatGenerator().generate(config, seed);
            } catch (final IllegalConfigException e) {
                CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
                return;
            }
            seatTable.setItems(FXCollections.observableArrayList(SeatRowData.fromSeat(seat)));
            previousSeed = seed;
        } catch (final Throwable e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
        }
    }

    @FXML
    void openPreferencesDialog(final ActionEvent event) {
        try {
            final Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/PreferencesDialog.fxml")));
            s.initOwner(stage);
            s.showAndWait();
            if (configIsChanged) {
                initSeatTable(ConfigUtils.reloadConfig());
                configIsChanged = false;
            }
        } catch (final Throwable e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
        }
    }

    @FXML
    void initialize() {
        try {
            stage.getIcons().add(new Image(MetaData.ICON_URL));
            stage.setTitle("Random Seat Generator - 随机座位生成器");
            stage.getScene().getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);
            stage.setOnCloseRequest(event -> System.exit(0));

            initSeatTable(ConfigUtils.reloadConfig());
        } catch (final Throwable e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
        }
    }

    void initSeatTable(final SeatConfig conf) {
        final int rowCount = conf.getRowCount(), columnCount = conf.getColumnCount();
        TableColumn<SeatRowData, String> c;

        if (seatTable.getColumns().size() != columnCount) {
            seatTable.getColumns().clear();
            for (int i = 0; i < columnCount || i < 2; i++) {
                c = new TableColumn<>("C" + (i + 1)) {{
                    prefWidthProperty().bind(seatTable.widthProperty().divide(Math.max(columnCount, 2)));
                }};
                c.setCellValueFactory(new PropertyValueFactory<>("c" + (i + 1)));
                c.setSortable(false);
                seatTable.getColumns().add(c);
            }
        }

        seatTable.setItems(FXCollections.observableArrayList(SeatRowData.emptySeat(rowCount, columnCount)));
    }

}
