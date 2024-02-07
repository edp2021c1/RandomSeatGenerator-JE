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
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.buildList;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.getMax;
import static com.edp2021c1.randomseatgenerator.util.ui.UIFactory.*;

/**
 * Stage to show a simple message.
 *
 * @author Calboot
 * @since 1.5.0
 */
public class MessageDialog extends Stage {

    private MessageDialog(final String title, final String msg) {
        super();

        final TextArea txt = new TextArea(msg);
        txt.setEditable(false);
        txt.setPrefColumnCount(Math.min(
                75,
                getMax(buildList(txt.getParagraphs(), charSequence -> charSequence.length() / 2))
        ));

        final Button button = createButton("确定", 80, 26);
        button.setDefaultButton(true);
        button.setOnAction(event -> close());

        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().add(button);

        final VBox mainBox = createVBox(txt, buttonBar);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), txt, buttonBar);

        setScene(new Scene(mainBox));
        setTitle(title);
        decorate(this, StageType.DIALOG);
    }

    /**
     * Shows a message dialog.
     *
     * @param title title of the dialog
     * @param msg   message to be shown
     */
    public static void showMessage(final String title, final String msg) {
        try {
            new MessageDialog(title, msg).showAndWait();
        } catch (final IllegalStateException e) {
            Application.launch(MessageDialogApp.class, title, msg);
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
            new MessageDialog(
                    getParameters().getRaw().getFirst(),
                    getParameters().getRaw().getLast()
            ).showAndWait();
        }
    }
}
