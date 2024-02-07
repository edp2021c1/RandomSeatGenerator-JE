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
