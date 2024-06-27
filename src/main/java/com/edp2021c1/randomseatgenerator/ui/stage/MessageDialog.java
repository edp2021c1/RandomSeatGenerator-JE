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

import com.edp2021c1.randomseatgenerator.util.DesktopUtils;
import com.edp2021c1.randomseatgenerator.util.useroutput.Notice;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.stage.Window;
import lombok.val;

import static com.edp2021c1.randomseatgenerator.ui.FXUtils.*;

/**
 * Stage to show a simple message.
 *
 * @author Calboot
 * @since 1.5.0
 */
public final class MessageDialog extends DecoratedStage {

    private MessageDialog(final Notice msg) {
        super();

        val txt = new Label(msg.message());

        val button = createButton("确定", 80, 26);
        button.setDefaultButton(true);
        button.setOnAction(event -> close());

        val buttonBar = new ButtonBar();
        buttonBar.getButtons().add(button);

        val mainBox = createVBox(txt, buttonBar);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), txt, buttonBar);

        setScene(new Scene(mainBox));
        setTitle(msg.title());
        setMaxWidth(1280);
        setMaxHeight(720);
    }

    /**
     * Shows a message dialog.
     *
     * @param msg message to be shown
     */
    public static void showMessage(final Notice msg) {
        DesktopUtils.runOnFXThread(() -> new MessageDialog(msg).showAndWait());
    }

    /**
     * Shows a message dialog.
     *
     * @param owner the owner of the dialog, possibly null
     * @param msg   message to be shown
     */
    public static void showMessage(final Window owner, final Notice msg) {
        DesktopUtils.runOnFXThread(() -> {
            val dialog = new MessageDialog(msg);
            dialog.initOwner(owner);
            dialog.showAndWait();
        });
    }

    @Override
    public StageType getStageType() {
        return StageType.DIALOG;
    }

}
