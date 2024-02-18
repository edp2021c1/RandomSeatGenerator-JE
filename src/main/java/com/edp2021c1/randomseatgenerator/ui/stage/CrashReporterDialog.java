package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.ui.UIFactory;
import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.Utils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Stage of FX crash reporter.
 *
 * @author Calboot
 * @since 1.4.1
 */
public class CrashReporterDialog extends Stage {

    private static String titleToBeShown;
    private static String messageToBeShown;

    private CrashReporterDialog(final String title, final String msg) {
        super();

        final Label preLabelBeforeLink = new Label("Something's wrong... Click");
        final Hyperlink here = new Hyperlink("here");
        final Label preLabelAfterLink = new Label("to copy the error message");
        preLabelBeforeLink.getStyleClass().add("err-pre-label");
        here.getStyleClass().add("err-pre-label");
        preLabelAfterLink.getStyleClass().add("err-pre-label");

        final HBox preBox = new HBox(preLabelBeforeLink, here, preLabelAfterLink);
        preBox.setAlignment(Pos.CENTER_LEFT);

        final TextArea mainText = new TextArea(msg);
        mainText.setEditable(false);
        mainText.getStyleClass().add("err-main-text");

        final Button confirmBtn = UIFactory.createButton("关闭", 80, 26);
        confirmBtn.setOnAction(event -> close());
        confirmBtn.setDefaultButton(true);

        final Button copyBtn = UIFactory.createButton("复制并关闭", 80, 26);

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(copyBtn, confirmBtn);
        buttonBar.setPrefHeight(66);
        buttonBar.getStyleClass().add("bottom");

        final VBox mainBox = new VBox(preBox, mainText, buttonBar);
        mainBox.getStyleClass().add("main");

        setScene(new Scene(mainBox));
        setMaxWidth(1440);
        setMaxHeight(810);
        setTitle(title);
        UIFactory.decorate(this, UIFactory.StageType.ERROR);

        here.setOnAction(event -> DesktopUtils.copyPlainText(mainText.getText()));

        copyBtn.setOnAction(event -> {
            DesktopUtils.copyPlainText(mainText.getText());
            close();
        });

        if (Utils.isMac()) {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> close();
                    case C -> DesktopUtils.copyPlainText(mainText.getText());
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                if (KeyCode.C == event.getCode()) {
                    DesktopUtils.copyPlainText(mainText.getText());
                }
            });
        }
    }

    /**
     * Shows a crash reporter dialog.
     *
     * @param title dialog title
     * @param msg   error message
     */
    public static void showCrashReporter(final String title, final String msg) {
        try {
            new CrashReporterDialog(title, msg).showAndWait();
        } catch (final IllegalStateException exception) {
            titleToBeShown = title;
            messageToBeShown = msg;
            Application.launch(CrashReporterApp.class);
        }
    }

    /**
     * JavaFX application used to launch {@code CrashReporterDialog}.
     */
    public static class CrashReporterApp extends Application {

        /**
         * Don't let anyone else instantiate this class.
         */
        private CrashReporterApp() {
        }

        @Override
        public void start(final Stage primaryStage) {
            new CrashReporterDialog(titleToBeShown, messageToBeShown).showAndWait();
        }
    }

}