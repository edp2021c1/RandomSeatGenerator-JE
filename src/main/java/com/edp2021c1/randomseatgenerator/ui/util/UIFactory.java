/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.ui.util;

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
import com.edp2021c1.randomseatgenerator.ui.control.ConfigPane;
import com.edp2021c1.randomseatgenerator.util.AppConfig;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Contains several useful methods for creating or initializing JavaFX controls.
 *
 * @author Calboot
 * @since 1.3.3
 */
public class UIFactory {
    /**
     * Default margin.
     */
    public static final Insets DEFAULT_MARGIN = new Insets(5);
    /**
     * Default padding.
     */
    public static final Insets DEFAULT_PADDING = new Insets(10);

    /**
     * Sets margin of elements.
     *
     * @param margin   of the elements.
     * @param elements where margin will be set to
     */
    public static void setMargins(final Insets margin, final Node... elements) {
        for (final Node n : elements) {
            HBox.setMargin(n, margin);
            VBox.setMargin(n, margin);
        }
    }

    /**
     * Sets {@code HGrow} and {@code VGrow} of elements.
     *
     * @param priority {@code HGrow} and {@code VGrow} value to be set to the elements
     * @param elements where priority will be set to
     */
    public static void setGrows(final Priority priority, final Node... elements) {
        for (final Node n : elements) {
            HBox.setHgrow(n, priority);
            VBox.setVgrow(n, priority);
        }
    }

    /**
     * Sets padding of elements.
     *
     * @param padding  to be set to the elements
     * @param elements where padding will be set to
     */
    public static void setPaddings(final Insets padding, final Region... elements) {
        for (final Region r : elements) {
            r.setPadding(padding);
        }
    }

    /**
     * Creates a {@code Button}.
     *
     * @param text   of the button
     * @param width  of the button
     * @param height of the button
     * @return the button created
     */
    public static Button createButton(final String text, final double width, final double height) {
        final Button btn = new Button(text);
        btn.setPrefSize(width, height);
        return btn;
    }

    /**
     * Creates a {@code VBox}.
     *
     * @param width    of the box
     * @param height   of the box
     * @param children of the box
     * @return the box created
     */
    public static VBox createVBox(final double width, final double height, final Node... children) {
        final VBox vBox = new VBox(children);
        vBox.setPrefSize(width, height);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * Creates a {@code HBox}.
     *
     * @param width    of the box
     * @param height   of the box
     * @param children of the box
     * @return the box created
     */
    public static HBox createHBox(final double width, final double height, final Node... children) {
        final HBox hBox = new HBox(children);
        hBox.setPrefSize(width, height);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    /**
     * Creates a {@code TextField}.
     *
     * @param promptText of the field
     * @return the field created
     */
    public static TextField createTextField(final String promptText) {
        final TextField t = new TextField();
        t.setPromptText(promptText);
        return t;
    }

    /**
     * Creates a {@code TextField}.
     *
     * @param promptText of the field
     * @param width      of the field
     * @param height     of the field
     * @return the field created
     */
    public static TextField createTextField(final String promptText, final double width, final double height) {
        final TextField t = createTextField(promptText);
        t.setPrefSize(width, height);
        return t;
    }

    /**
     * Creates a {@code TextArea}.
     *
     * @param promptText of the area
     * @param width      of the area
     * @param height     of the area
     * @return the area created
     */
    public static TextArea createTextArea(final String promptText, final double width, final double height) {
        final TextArea t = new TextArea();
        t.setPromptText(promptText);
        t.setPrefSize(width, height);
        t.setWrapText(true);
        return t;
    }

    /**
     * Creates a {@code Label}.
     *
     * @param text   of the label
     * @param width  of the label
     * @param height of the label
     * @return the label created
     */
    public static Label createLabel(final String text, final double width, final double height) {
        final Label label = new Label(text);
        label.setPrefSize(width, height);
        label.setWrapText(true);
        return label;
    }

    /**
     * Creates a {@code ImageView}.
     *
     * @param imageUrl  URL of the image of the view
     * @param fitWidth  of the view
     * @param fitHeight of the view
     * @return the view created
     */
    public static ImageView createImageView(final String imageUrl, final double fitWidth, final double fitHeight) {
        final ImageView i = new ImageView(new Image(imageUrl));
        i.setFitWidth(fitWidth);
        i.setFitHeight(fitHeight);
        i.setPickOnBounds(true);
        i.setPreserveRatio(true);
        return i;
    }

    /**
     * Creates a {@code ButtonBar}.
     *
     * @param width   of the bar
     * @param height  of the bar
     * @param buttons in the bar
     * @return the bar created
     */
    public static ButtonBar createButtonBar(final double width, final double height, final Button... buttons) {
        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPrefSize(width, height);
        buttonBar.getButtons().addAll(buttons);
        return buttonBar;
    }

    /**
     * Initialize a seat table view.
     *
     * @param seatTable to be initialized
     * @param config    used to initialize the table
     */
    public static void initSeatTable(final TableView<SeatRowData> seatTable, final SeatConfig config) {
        seatTable.setEditable(false);
        final int rowCount = config.getRowCount();
        final int columnCount = config.getColumnCount();
        TableColumn<SeatRowData, String> c;

        if (seatTable.getColumns().size() != columnCount) {
            seatTable.getColumns().clear();
            for (int i = 0; i < columnCount || i < 2; i++) {
                c = new TableColumn<>("C" + (i + 1)) {{
                    prefWidthProperty().bind(seatTable.widthProperty().divide(Math.max(columnCount, 2)));
                }};
                c.setCellValueFactory(new PropertyValueFactory<>("c" + (i + 1)));
                c.setSortable(false);
                seatTable.getColumns().add(c);
            }
        }

        seatTable.setItems(FXCollections.observableArrayList(SeatRowData.emptySeat(rowCount, columnCount)));
    }

    /**
     * Initialize a config input pane.
     *
     * @param config     to be filled in
     * @param configPane to be initialized
     */
    public static void initConfigPane(final AppConfig config, final ConfigPane configPane) {
        configPane.getRowCountInput().setText(config.row_count);
        configPane.getColumnCountInput().setText(config.column_count);
        configPane.getRbrInput().setText(config.random_between_rows);
        configPane.getDisabledLastRowPosInput().setText(config.last_row_pos_cannot_be_chosen);
        configPane.getNameListInput().setText(config.person_sort_by_height);
        configPane.getGroupLeaderListInput().setText(config.group_leader_list);
        configPane.getSeparateListInput().setText(config.separate_list);
        configPane.getLuckyOptionCheck().setSelected(config.lucky_option);
        configPane.getExportWritableCheck().setSelected(config.export_writable);
    }
}
