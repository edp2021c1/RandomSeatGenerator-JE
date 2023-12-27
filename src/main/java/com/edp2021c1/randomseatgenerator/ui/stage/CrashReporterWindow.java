package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.RuntimeUtils;
import com.edp2021c1.randomseatgenerator.util.ui.UIFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        mainLabel.getStyleClass().add("err-main-label");

        final Button confirmBtn = UIFactory.createButton("关闭", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        final Button copyBtn = UIFactory.createButton("复制并关闭", 80, 26);
        copyBtn.setOnAction(event -> {
            DesktopUtils.copyText(mainLabel.getText());
            close();
        });

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);
        buttonBar.setPrefHeight(66);
        buttonBar.getStyleClass().add("bottom");

        final ScrollPane scrollPane = new ScrollPane(mainLabel);
        scrollPane.autosize();
        scrollPane.getStyleClass().add("err-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        final VBox mainBox = new VBox(preLabel, scrollPane, buttonBar);
        mainBox.getStyleClass().add("main");

        final Scene scene = new Scene(mainBox);

        setScene(scene);
        setMaxWidth(1440);
        setMaxHeight(810);
        setTitle("出错啦");
        UIFactory.decorate(this, UIFactory.StageType.ERROR);

        preLabel.setOnMouseClicked(event -> DesktopUtils.copyText(mainLabel.getText()));

        if (OperatingSystem.CURRENT == OperatingSystem.MAC) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case Q -> RuntimeUtils.exit();
                    case W -> close();
                    case C -> DesktopUtils.copyText(mainLabel.getText());
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                if (KeyCode.C.equals(event.getCode())) {
                    DesktopUtils.copyText(mainLabel.getText());
                }
            });
        }
    }

}