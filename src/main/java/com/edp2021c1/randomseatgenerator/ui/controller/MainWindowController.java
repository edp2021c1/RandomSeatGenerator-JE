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
    private File export;

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
    void dateAsSeed(ActionEvent event) {
        Date t = new Date();
        seedInput.setText(String.format("%tY%tm%td%tH%tM%tS", t, t, t, t, t, t));
    }

    @FXML
    void exportSeatTable(ActionEvent event) {
        if (seat == null) {
            generateSeatTable(null);
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"));
        fc.setInitialDirectory(export == null ? new File("./") : export);
        fc.setInitialFileName(String.format("%tF", new Date()));

        File outputFile = fc.showSaveDialog(stage);
        if (outputFile == null) {
            return;
        }
        try {
            SeatUtils.exportToExcelDocument(seat, outputFile);
        } catch (IOException e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(
                    Thread.currentThread(),
                    new RuntimeException(String.format("Failed to export seat table to %s.", outputFile.getAbsolutePath()), e)
            );
        }

        export = outputFile.getParentFile();
    }

    @FXML
    void generateRandomSeed(ActionEvent event) {
        seedInput.setText(Long.toString(new Random().nextLong()));
    }

    @FXML
    void generateSeatTable(ActionEvent event) {
        SeatConfig config = ConfigUtils.reloadConfig();
        initSeatTable();

        long seed;
        try {
            if (Long.parseLong(seedInput.getText()) == previousSeed) {
                generateRandomSeed(null);
            }
        } catch (NumberFormatException e) {
            System.err.println("WARNING: Invalid seed.");
            generateRandomSeed(null);
        }

        seed = Long.parseLong(seedInput.getText());
        try {
            seat = new SeatGenerator().generate(config, seed);
        } catch (IllegalConfigException e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            return;
        }
        seatTable.setItems(FXCollections.observableArrayList(SeatRowData.fromSeat(seat)));
        previousSeed = seed;
    }

    @FXML
    void openPreferencesDialog(ActionEvent event) {
        Stage s = new Stage();
        try {
            s = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/PreferencesDialog.fxml")));
        } catch (IOException ignored) {
        }
        s.initOwner(stage);
        s.showAndWait();
        if (configIsChanged) {
            initSeatTable();
            configIsChanged = false;
        }
    }

    @FXML
    void initialize() {
        stage.getIcons().add(new Image(MetaData.ICON_URL));
        stage.setTitle("Random Seat Generator - 随机座位生成器");
        stage.getScene().getStylesheets().add(MetaData.DEFAULT_STYLESHEET_URL);

        initSeatTable();
    }

    void initSeatTable() {
        SeatConfig conf;
        try {
            conf = ConfigUtils.reloadConfig();
        } catch (IllegalConfigException e) {
            CrashReporter.DEFAULT_CRASH_REPORTER.uncaughtException(Thread.currentThread(), e);
            System.exit(0);
            return;
        }
        int rowCount = conf.getRowCount(), columnCount = conf.getColumnCount();
        TableColumn<SeatRowData, String> c;

        if (seatTable.getColumns().size() != columnCount) {
            seatTable.getColumns().clear();
            double d = 1.0 / (Math.max(columnCount, 2));
            for (int i = 0; i < columnCount || i < 2; i++) {
                c = new TableColumn<>("C" + (i + 1)) {{
                    prefWidthProperty().bind(seatTable.widthProperty().multiply(d));
                }};
                c.setCellValueFactory(new PropertyValueFactory<>("c" + (i + 1)));
                c.setSortable(false);
                seatTable.getColumns().add(c);
            }
        }

        seatTable.setItems(FXCollections.observableArrayList(SeatRowData.emptySeat(rowCount, columnCount)));
    }

}
