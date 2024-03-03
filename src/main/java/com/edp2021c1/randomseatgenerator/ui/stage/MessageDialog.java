/*
 * RandomSeatGenerator
 * Copyright © 2023 EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.ui.stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import static com.edp2021c1.randomseatgenerator.ui.UIFactory.*;

/**
 * Stage to show a simple message.
 *
 * @author Calboot
 * @since 1.5.0
 */
public class MessageDialog extends Stage {
    private static String messageToBeShown;

    private MessageDialog(final String msg) {
        super();

        final Label txt = new Label(msg);

        final Button button = createButton("确定", 80, 26);
        button.setDefaultButton(true);
        button.setOnAction(event -> close());

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().add(button);

        final VBox mainBox = createVBox(txt, buttonBar);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), txt, buttonBar);

        setScene(new Scene(mainBox));
        setTitle("消息");
        setMaxWidth(1280);
        setMaxHeight(720);
        decorate(this, StageType.DIALOG);
    }

    /**
     * Shows a message dialog.
     *
     * @param msg message to be shown
     */
    public static void showMessage(final String msg) {
        try {
            new MessageDialog(msg).showAndWait();
        } catch (final Throwable e) {
            messageToBeShown = msg;
            Application.launch(MessageDialogApp.class);
        }
    }

    /**
     * Shows a message dialog.
     *
     * @param owner the owner of the dialog, possibly null
     * @param msg   message to be shown
     */
    public static void showMessage(final Window owner, final String msg) {
        try {
            final MessageDialog dialog = new MessageDialog(msg);
            dialog.initOwner(owner);
            dialog.showAndWait();
        } catch (final IllegalStateException e) {
            messageToBeShown = msg;
            Application.launch(MessageDialogApp.class);
        }
    }

    /**
     * JavaFX application used to launch {@code MessageDialog}.
     */
    public static class MessageDialogApp extends Application {

        /**
         * Don't let anyone else instantiate this class.
         */
        private MessageDialogApp() {
        }

        @Override
        public void start(Stage primaryStage) {
            new MessageDialog(messageToBeShown).showAndWait();
        }
    }
}
