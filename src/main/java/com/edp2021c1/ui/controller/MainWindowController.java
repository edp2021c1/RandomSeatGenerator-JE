package com.edp2021c1.ui.controller;

import com.alibaba.excel.EasyExcel;
import com.edp2021c1.util.*;
import com.google.gson.Gson;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainWindowController {

    private JTable resultTable;

    private Seat seat;
    private SeatConfig defaultConfig;
    private OriginalSeatConfig defaultConfigJson;

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
    private SwingNode tableNode;

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
            Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/fxml/dialog/RemindGenerateSeatDialog.fxml")));
            s.show();
        } else {
            FileChooser fc = new FileChooser();
            fc.setTitle("导出座位表");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有文件", "*"),
                    new FileChooser.ExtensionFilter("CSV文件", "*.csv"),
                    new FileChooser.ExtensionFilter("HTML文档", "*.htm", "*.html"),
                    new FileChooser.ExtensionFilter("Excel工作薄", "*.xlsx"));
            File f = fc.showSaveDialog(stage);
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
                String name = f.getName();
                Date date = new Date();
                StringBuilder str = new StringBuilder();
                if (name.endsWith(".htm") || name.endsWith(".html")) {
                    str.append(String.format("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>座位表-%tF-%tT</title><style>table{font-family: 'Microsoft Yahei';font-size: 28px;}table,tr,th{border: 2px solid;}</style></head><body><center><table>", date, date));
                    str.append("<tr><th>第七列</th><th>第六列</th><th>第五列</th><th>第四列</th><th>第三列</th><th>第二列</th><th>第一列</th></tr>");
                    for (int i = 0; i < 7; i++) {
                        str.append("<tr>");
                        for (int j = 0; j < 7; j++) {
                            str.append("<th>");
                            str.append(seat.getSeat().get(i * 7 + j));
                            str.append("</th>");
                        }
                        str.append("</tr>");
                    }
                    str.append(String.format("</table></center><p>Seed:%d</body></html>", seat.getSeed()));

                    FileWriter fr = new FileWriter(f);
                    fr.write(str.toString());
                    fr.close();
                } else if (f.getName().endsWith(".xlsx")) {
                    EasyExcel.write(f, SeatExcelRowData.class).sheet(String.format("座位表-%tF", date)).doWrite(SeatExcelRowData.fromSeat(this.seat));
                } else {
                    str.append(String.format(",,,座位表-%tF-%tT,,,\n", date, date));
                    str.append("第七列,第六列,第五列,第四列,第三列,第二列,第一列\n");
                    for (int i = 0; i < 7; i++) {
                        for (int j = 0; j < 7; j++) {
                            str.append(this.seat.getSeat().get(i * 7 + j));
                            str.append(",");
                        }
                        str.setCharAt(str.length() - 1, '\n');
                    }
                    str.append("Seed,");
                    str.append(this.seat.getSeed());
                    str.append(",,,,,");

                    FileWriter fr = new FileWriter(f);
                    fr.write(str.toString());
                    fr.close();
                }
            }
        }
    }

    @FXML
    void fillBR(ActionEvent event) {
        backRowsInput.setText(defaultConfigJson.getFs());
    }

    @FXML
    void fillFR(ActionEvent event) {
        frontRowsInput.setText(defaultConfigJson.getOt());
    }

    @FXML
    void fillGL(ActionEvent event) {
        groupLeadersInput.setText(defaultConfigJson.getZz());
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
        middleRowsInput.setText(defaultConfigJson.getTf());
    }

    @FXML
    void fillRdSeed(ActionEvent event) {
        seedInput.setText(String.format("%d", new Random().nextLong()));
    }

    @FXML
    void fillSP(ActionEvent event) {
        separatedInput.setText(defaultConfigJson.getSeparate());
    }

    @FXML
    void importConfig(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setTitle("导入配置文件");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json文件", "*.json"));
        File f = fc.showOpenDialog(stage);
        if (f != null && f.isFile()) {
            FileInputStream inputStream = new FileInputStream(f);
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            inputStream.close();
            String str = new String(bytes, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            defaultConfigJson = gson.fromJson(str, OriginalSeatConfig.class);
            defaultConfig = new SeatConfig(defaultConfigJson);
        }
    }

    @FXML
    void generate(ActionEvent event) throws IOException {
        if (frontRowsInput.getText().isEmpty() || middleRowsInput.getText().isEmpty() || backRowsInput.getText().isEmpty() || groupLeadersInput.getText().isEmpty()) {
            Stage s = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/fxml/dialog/RemindFillInConfigDialog.fxml")));
            s.show();
        }
        SeatConfig conf = new SeatConfig(frontRowsInput.getText(), middleRowsInput.getText(), backRowsInput.getText(), groupLeadersInput.getText(), separatedInput.getText());
        SeatGenerator sg = new SeatGenerator(conf);
        seat = sg.next(Long.parseLong(seedInput.getText()));
        ArrayList<String> l = seat.getSeat();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                resultTable.setValueAt(l.get(i * 7 + j), i, j);
            }
        }
    }

    @FXML
    void showAboutDialog(ActionEvent event) throws IOException {
        Stage aboutDialog = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/assets/fxml/dialog/AboutDialog.fxml")));

        aboutDialog.show();
    }

    @FXML
    void initialize() throws IOException {
        assert aboutMenu != null : "fx:id=\"aboutMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert backRowsInput != null : "fx:id=\"backRowsInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert clearConfigMenu != null : "fx:id=\"clearConfigMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert exportToFileMenu != null : "fx:id=\"exportToFileMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillDefaultBRBtn != null : "fx:id=\"fillDefaultBRBtn\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillDefaultFRBtn != null : "fx:id=\"fillDefaultFRBtn\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillDefaultGLBtn != null : "fx:id=\"fillDefaultGLBtn\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillDefaultMRBtn != null : "fx:id=\"fillDefaultMRBtn\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillDefaultSpBtn != null : "fx:id=\"fillDefaultSpBtn\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert fillInConfigMenu != null : "fx:id=\"fillInConfigMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert frontRowsInput != null : "fx:id=\"frontRowsInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert groupLeadersInput != null : "fx:id=\"groupLeadersInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert importConfigMenu != null : "fx:id=\"importConfigMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert mainHBox != null : "fx:id=\"mainHBox\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert mainVBox != null : "fx:id=\"mainVBox\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert middleRowsInput != null : "fx:id=\"middleRowsInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert rdSeed != null : "fx:id=\"rdSeed\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert seedInput != null : "fx:id=\"seedInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert separatedInput != null : "fx:id=\"separatedInput\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert stage != null : "fx:id=\"stage\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert subBox_1 != null : "fx:id=\"subBox_1\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert subBox_2 != null : "fx:id=\"subBox_2\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert subBox_3 != null : "fx:id=\"subBox_3\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert tableNode != null : "fx:id=\"tableNode\" was not injected: check your FXML file 'MainWindow.fxml'.";

        stage.getIcons().add(new Image("assets/img/icon.png"));

        resultTable = new JTable(7, 7);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                resultTable.setValueAt("-", i, j);
            }
        }
        resultTable.setRowHeight(40);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        resultTable.setGridColor(new Color(223, 225, 229));
        resultTable.setCellSelectionEnabled(false);
        resultTable.setFont(new Font("Microsoft Yahei", Font.PLAIN, 28));
        resultTable.setBorder(null);
        tableNode.setContent(resultTable);

        HBox.setHgrow(mainHBox, Priority.ALWAYS);
        VBox.setVgrow(mainVBox, Priority.ALWAYS);
        HBox.setHgrow(subBox_1, Priority.ALWAYS);
        HBox.setHgrow(subBox_2, Priority.ALWAYS);
        HBox.setHgrow(subBox_3, Priority.ALWAYS);
        HBox.setHgrow(subBox_4, Priority.ALWAYS);

        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("assets/json/defaultConfig.json"))));
        StringBuilder buffer = new StringBuilder();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        String str = buffer.toString();
        Gson gson = new Gson();
        defaultConfigJson = gson.fromJson(str, OriginalSeatConfig.class);
        defaultConfig = new SeatConfig(defaultConfigJson);
    }

}
