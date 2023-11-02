package com.edp2021c1.randomseatgenerator.fx.controller;

import com.edp2021c1.randomseatgenerator.Main;
import com.edp2021c1.randomseatgenerator.core.Seat;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
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

import static java.lang.System.err;

/**
 * Controller of {@code assets/fxml/MainWindow.fxml}
 */
public class MainWindowController {
    /**
     * Decides whether the config is changed after opening {@code PreferencesDialog}.
     */
    public static boolean configIsChanged = false;
    private static Seat seat;
    private static long previousSeed = 0;

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
        fc.setInitialDirectory(new File("./"));
        fc.setInitialFileName(String.format("%tF", new Date()));

        File outputFile = fc.showSaveDialog(stage);
        if (outputFile == null) {
            return;
        }
        try {
            seat.exportToExcelDocument(outputFile);
        } catch (IOException e) {
            err.printf("ERROR: Failed to export seat table to %s.%n", outputFile.getAbsolutePath());
        }
    }

    @FXML
    void generateRandomSeed(ActionEvent event) {
        seedInput.setText(Long.toString(new Random().nextLong()));
    }

    @FXML
    void generateSeatTable(ActionEvent event) {
        SeatConfig config = Main.reloadConfig();
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
        seat = new SeatGenerator().generate(config, seed);
        seatTable.setItems(FXCollections.observableArrayList(SeatRowData.fromSeat(seat)));
        previousSeed = seed;
    }

    @FXML
    void openPreferencesDialog(ActionEvent event) {
        Stage s;
        try {
            s = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/dialog/PreferencesDialog.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        stage.getIcons().add(new Image("assets/img/logo.png"));

        initSeatTable();
    }

    void initSeatTable() {
        SeatConfig conf = Main.reloadConfig();
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
