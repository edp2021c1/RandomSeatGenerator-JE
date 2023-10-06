package com.edp2021c1.ui.controller;

import com.edp2021c1.Main;
import com.edp2021c1.core.Seat;
import com.edp2021c1.core.SeatManager;
import com.edp2021c1.data.SeatRowData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class MainWindowController {

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
    void dateAsSeed(ActionEvent event) {
        Date t = new Date();
        seedInput.setText(String.format("%tY%tm%td%tH%tM%tS", t, t, t, t, t, t));
    }

    @FXML
    void exportSeatTable(ActionEvent event) {

    }

    @FXML
    void generateRandomSeed(ActionEvent event) {
        seedInput.setText(Long.toString(new Random().nextLong()));
    }

    @FXML
    void generateSeatTable(ActionEvent event) {
        long seed;
        try {
            Long.parseLong(seedInput.getText());
        } catch (NumberFormatException e) {
            generateRandomSeed(null);
        }
        seed = Long.parseLong(seedInput.getText());
        SeatManager.config = Main.seatConfig;
        Seat seat = SeatManager.generate(seed);
        System.out.println(seat);
    }

    @FXML
    void openPreferencesDialog(ActionEvent event) throws IOException {
        Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/dialog/PreferencesDialog.fxml")));
        s.initOwner(stage);
        s.show();
    }

    @FXML
    void initialize() {
        initSeatTable();
    }

    void initSeatTable() {
        int rows = Main.seatConfig.getRows(), columns = Main.seatConfig.getColumns();
        TableColumn<SeatRowData, String> c;
        for (int i = 0; i < columns; i++) {
            c = new TableColumn<>("C" + (i + 1));
            c.setCellValueFactory(new PropertyValueFactory<>("c" + (i + 1)));
            c.setSortable(false);
            seatTable.getColumns().add(c);
        }
        seatTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        seatTable.setEditable(false);
    }

}
