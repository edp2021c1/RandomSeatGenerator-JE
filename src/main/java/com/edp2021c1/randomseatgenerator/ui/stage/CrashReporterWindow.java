package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.ui.util.UIFactory;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Windows of FX crash reporter.
 *
 * @author Calboot
 * @since 1.4.1
 */
public class CrashReporterWindow extends Stage {

    /**
     * Creates an instance showing the given error message.
     *
     * @param msg to be shown.
     */
    public CrashReporterWindow(final String msg) {
        final Label preLabel = new Label("Something's wrong... click here to copy the error message.\n");
        preLabel.getStyleClass().add("err-pre-label");

        final Label mainLabel = new Label(msg);

        final Button confirmBtn = UIFactory.createButton("关闭", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        final Button copyBtn = UIFactory.createButton("复制并关闭", 80, 26);
        copyBtn.setOnAction(event -> {
            copyText(mainLabel);
            close();
        });

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);
        buttonBar.setPrefHeight(66);
        buttonBar.getStyleClass().add("bottom");

        final ScrollPane scrollPane = new ScrollPane(mainLabel);
        scrollPane.setPrefSize(720, 480);
        scrollPane.getStyleClass().add("err-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final VBox mainBox = new VBox(preLabel, scrollPane, buttonBar);
        mainBox.getStyleClass().add("main");

        final Scene scene = new Scene(mainBox);

        setScene(scene);
        setTitle("出错啦");
        UIFactory.decorate(this, UIFactory.StageType.ERROR);

        preLabel.setOnMouseClicked(event -> copyText(mainLabel));

        if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> close();
                    case C -> copyText(mainLabel);
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                if (KeyCode.C.equals(event.getCode())) {
                    copyText(mainLabel);
                }
            });
        }
    }

    private void copyText(Label label) {
        final HashMap<DataFormat, Object> map = new HashMap<>();
        map.put(DataFormat.PLAIN_TEXT, label.getText());
        Clipboard.getSystemClipboard().setContent(map);
    }
}