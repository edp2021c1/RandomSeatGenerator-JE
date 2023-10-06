package com.edp2021c1.ui.controller;

import com.alibaba.excel.EasyExcel;
import com.edp2021c1.core.SeatConfig;
import com.edp2021c1.core.SeatConfig_Old;
import com.edp2021c1.core.SeatGenerator;
import com.edp2021c1.data.SeatRowData_Old;
import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Deprecated
public class MainWindowController_Old {

    private ArrayList<String> seat;
    private long seed;
    private SeatConfig defaultConfig;
    private SeatGenerator seatGenerator;

    @FXML
    private TableView<SeatRowData_Old> resultTable;

    @FXML
    private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem aboutMenu;

    @FXML
    private TextField backRowsInput;

    @FXML
    private MenuItem clearConfigMenu;

    @FXML
    private MenuItem exportToFileMenu;

    @FXML
    private Button fillDefaultBRBtn;

    @FXML
    private Button fillDefaultFRBtn;

    @FXML
    private Button fillDefaultGLBtn;

    @FXML
    private Button fillDefaultMRBtn;

    @FXML
    private Button fillDefaultSpBtn;

    @FXML
    private MenuItem fillInConfigMenu;

    @FXML
    private TextField frontRowsInput;

    @FXML
    private TextField groupLeadersInput;

    @FXML
    private MenuItem importConfigMenu;

    @FXML
    private MenuItem generateMenu;

    @FXML
    private HBox mainHBox;

    @FXML
    private VBox mainVBox;

    @FXML
    private TextField middleRowsInput;

    @FXML
    private Button rdSeed;

    @FXML
    private TextField seedInput;

    @FXML
    private TextArea separatedInput;

    @FXML
    private HBox subBox_1;

    @FXML
    private HBox subBox_2;

    @FXML
    private HBox subBox_3;

    @FXML
    private HBox subBox_4;

    @FXML
    private Button generateBtn;

    @FXML
    private Button exportBtn;

    @FXML
    void clearConfig(ActionEvent event) {
        frontRowsInput.clear();
        middleRowsInput.clear();
        backRowsInput.clear();
        groupLeadersInput.clear();
        seedInput.clear();
        separatedInput.clear();
    }

    @FXML
    void exportToFile(ActionEvent event) throws IOException {
        if (seat == null) {
            generate(event);
        }
        var fc = new FileChooser();
        fc.setTitle("导出座位表");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有文件", "*"),
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"),
                new FileChooser.ExtensionFilter("HTML文档", "*.htm", "*.html"),
                new FileChooser.ExtensionFilter("Excel工作薄", "*.xlsx"));
        File f = fc.showSaveDialog(stage);
        if (f == null) {
            return;
        }
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        String name = f.getName();
        Date date = new Date();
        StringBuilder str = new StringBuilder();
        if (name.endsWith(".htm") || name.endsWith(".html")) {
            str.append(String.format("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>座位表-%tF-%tT</title><style>table{font-family: 'Microsoft Yahei';font-size: 28px;}table,tr,th{border: 2px solid;}</style></head><body><center><table>", date, date));
            str.append("<tr><th>&nbsp;&nbsp;G7&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G6&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G5&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G4&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G3&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G2&nbsp;&nbsp;</th><th>&nbsp;&nbsp;G1&nbsp;&nbsp;</th></tr>");
            for (int i = 0; i < 7; i++) {
                str.append("<tr>");
                for (int j = 0; j < 7; j++) {
                    str.append("<th>");
                    str.append(seat.get(i * 7 + j));
                    str.append("</th>");
                }
                str.append("</tr>");
            }
            str.append(String.format("</table></center><p>Seed:%d</body></html>", seed));
            FileWriter fr = new FileWriter(f);
            fr.write(str.toString());
            fr.close();
        } else if (f.getName().endsWith(".xlsx")) {
            EasyExcel.write(f, SeatRowData_Old.class).sheet(String.format("座位表-%tF", date)).doWrite(SeatRowData_Old.fromSeat(seat, seed));
        } else {
            str.append(String.format(",,,Seat_Old Table-%tF-%tT\n", date, date));
            str.append("G7,G6,G5,G4,G3,G2,G1\n");
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 6; j++) {
                    str.append(seat.get(i * 7 + j));
                    str.append(",");
                }
                str.append(seat.get(i * 7 + 6));
                str.append("\n");
            }
            str.append("Seed,");
            str.append(seed);

            FileOutputStream fr = new FileOutputStream(f);
            fr.write(str.toString().getBytes());
            fr.close();
        }
    }

    @FXML
    void fillBR(ActionEvent event) {
        backRowsInput.setText(defaultConfig.fs);
    }

    @FXML
    void fillFR(ActionEvent event) {
        frontRowsInput.setText(defaultConfig.ot);
    }

    @FXML
    void fillGL(ActionEvent event) {
        groupLeadersInput.setText(defaultConfig.getZz());
    }

    @FXML
    void fillInConfig(ActionEvent event) {
        fillFR(event);
        fillMR(event);
        fillBR(event);
        fillGL(event);
        fillRdSeed(event);
        fillSP(event);
    }

    @FXML
    void fillMR(ActionEvent event) {
        middleRowsInput.setText(defaultConfig.tf);
    }

    @FXML
    void fillRdSeed(ActionEvent event) {
        seedInput.setText(Long.toString(new Random().nextLong()));
    }

    @FXML
    void fillSP(ActionEvent event) {
        separatedInput.setText(defaultConfig.getSeparate());
    }

    @FXML
    void importConfig(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("导入配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));
        File f = fc.showOpenDialog(stage);
        if (f == null) {
            return;
        }
        FileInputStream inputStream = new FileInputStream(f);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String str = new String(bytes, StandardCharsets.UTF_8);
        defaultConfig = new Gson().fromJson(str, SeatConfig.class);
    }

    @FXML
    void generate(ActionEvent event) {
        if (frontRowsInput.getText().isBlank()) {
            fillFR(event);
        }
        if (middleRowsInput.getText().isBlank()) {
            fillMR(event);
        }
        if (backRowsInput.getText().isBlank()) {
            fillBR(event);
        }
        if (seedInput.getText().isBlank()) {
            fillRdSeed(event);
        }
        if (groupLeadersInput.getText().isBlank()) {
            fillGL(event);
        }
        SeatConfig_Old conf = new SeatConfig_Old(frontRowsInput.getText(), middleRowsInput.getText(), backRowsInput.getText(), groupLeadersInput.getText(), separatedInput.getText());
        seed = Long.parseLong(seedInput.getText());
        seatGenerator.setConfig(conf);
        seatGenerator.setSeed(seed);
        seat = seatGenerator.next().seat;
        resultTable.setItems(FXCollections.observableArrayList(SeatRowData_Old.fromSeat(seat, seed)));
    }

    @FXML
    void showAboutDialog(ActionEvent event) throws IOException {
        Stage aboutDialog = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/dialog/AboutDialog.fxml")));
        aboutDialog.initOwner(stage);
        aboutDialog.show();
    }

    @FXML
    void initialize() throws IOException {
        stage.getIcons().add(new Image("assets/img/icon.png"));

        TableColumn<SeatRowData_Old, String> c1 = new TableColumn<>("G7");
        c1.setCellValueFactory(new PropertyValueFactory<>("c1"));
        c1.setSortable(false);
        TableColumn<SeatRowData_Old, String> c2 = new TableColumn<>("G6");
        c2.setCellValueFactory(new PropertyValueFactory<>("c2"));
        c2.setSortable(false);
        TableColumn<SeatRowData_Old, String> c3 = new TableColumn<>("G5");
        c3.setCellValueFactory(new PropertyValueFactory<>("c3"));
        c3.setSortable(false);
        TableColumn<SeatRowData_Old, String> c4 = new TableColumn<>("G4");
        c4.setCellValueFactory(new PropertyValueFactory<>("c4"));
        c4.setSortable(false);
        TableColumn<SeatRowData_Old, String> c5 = new TableColumn<>("G3");
        c5.setCellValueFactory(new PropertyValueFactory<>("c5"));
        c5.setSortable(false);
        TableColumn<SeatRowData_Old, String> c6 = new TableColumn<>("G2");
        c6.setCellValueFactory(new PropertyValueFactory<>("c6"));
        c6.setSortable(false);
        TableColumn<SeatRowData_Old, String> c7 = new TableColumn<>("G1");
        c7.setCellValueFactory(new PropertyValueFactory<>("c7"));
        c7.setSortable(false);

        ObservableList<SeatRowData_Old> data = FXCollections.observableArrayList(SeatRowData_Old.emptySeat);
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        resultTable.setEditable(false);
        resultTable.setItems(data);
        resultTable.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7);

        HBox.setHgrow(mainHBox, Priority.ALWAYS);
        VBox.setVgrow(mainVBox, Priority.ALWAYS);
        HBox.setHgrow(subBox_1, Priority.ALWAYS);
        HBox.setHgrow(subBox_2, Priority.ALWAYS);
        HBox.setHgrow(subBox_3, Priority.ALWAYS);
        HBox.setHgrow(subBox_4, Priority.ALWAYS);

        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/assets/conf/seat_config_old.json"))));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        defaultConfig = new Gson().fromJson(buffer.toString(), SeatConfig.class);

        seatGenerator = new SeatGenerator();
    }

}
