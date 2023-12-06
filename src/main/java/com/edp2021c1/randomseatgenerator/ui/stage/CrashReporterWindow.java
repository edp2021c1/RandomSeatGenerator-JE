package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.ui.util.UIFactory;
import com.edp2021c1.randomseatgenerator.util.MetaData;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Windows of FX crash reporter.
 */
public class CrashReporterWindow extends Stage {

    public CrashReporterWindow(final String message) {
        final Label preLabel = new Label("Something's wrong... click here to copy the error message.\n");
        preLabel.getStyleClass().add("error-pre-label");

        final Label mainLabel = new Label(message);
        mainLabel.setWrapText(true);

        final Button confirmBtn = UIFactory.createButton("确定", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        final Button copyBtn = UIFactory.createButton("复制并关闭", 80, 26);
        copyBtn.setOnAction(event -> {
            HashMap<DataFormat, Object> map = new HashMap<>();
            map.put(DataFormat.PLAIN_TEXT, mainLabel.getText());
            Clipboard.getSystemClipboard().setContent(map);
            close();
        });

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);

        final ScrollPane scrollPane = new ScrollPane(mainLabel);

        final VBox mainBox = new VBox(preLabel, scrollPane, buttonBar);
        mainBox.getStyleClass().add("main");
        mainBox.setSpacing(10);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final Scene scene = new Scene(mainBox);
        scene.getStylesheets().addAll(MetaData.DEFAULT_STYLESHEETS);

        setScene(scene);
        setTitle("出错啦");
        setWidth(720);
        setHeight(480);
        getIcons().add(new Image(MetaData.ERROR_ICON_URL));

        preLabel.setOnMouseClicked(event -> {
            HashMap<DataFormat, Object> map = new HashMap<>();
            map.put(DataFormat.PLAIN_TEXT, mainLabel.getText());
            Clipboard.getSystemClipboard().setContent(map);
        });

        mainBox.setOnKeyPressed(event -> {
            if (OperatingSystem.CURRENT != OperatingSystem.MAC || !event.isMetaDown()) {
                return;
            }
            if (KeyCode.W.equals(event.getCode())) {
                close();
            }
        });
    }
}