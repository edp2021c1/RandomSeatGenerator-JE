package com.edp2021c1.randomseatgenerator.ui;

import com.edp2021c1.randomseatgenerator.core.SeatConfig;
import com.edp2021c1.randomseatgenerator.core.SeatRowData;
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

public class UIFactory {
    public static final Insets DEFAULT_MARGIN = new Insets(5);
    public static final Insets DEFAULT_PADDING = new Insets(10);

    public static void setMargins(final Insets margin, final Node... children) {
        for (final Node n : children) {
            HBox.setMargin(n, margin);
            VBox.setMargin(n, margin);
        }
    }

    public static void setGrows(final Priority priority, final Node... children) {
        for (final Node n : children) {
            HBox.setHgrow(n, priority);
            VBox.setVgrow(n, priority);
        }
    }

    public static void setPaddings(final Insets padding, final Region... children) {
        for (final Region r : children) {
            r.setPadding(padding);
        }
    }

    public static Button createButton(final String text, final double width, final double height) {
        final Button btn = new Button(text);
        btn.setPrefSize(width, height);
        return btn;
    }

    public static VBox createVBox(final double width, final double height, final Node... children) {
        final VBox vBox = new VBox(children);
        vBox.setPrefSize(width, height);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    public static HBox createHBox(final double width, final double height, final Node... children) {
        final HBox hBox = new HBox(children);
        hBox.setPrefSize(width, height);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    public static TextField createTextField(final String promptText) {
        final TextField t = new TextField();
        t.setPromptText(promptText);
        return t;
    }

    public static TextField createTextField(final String promptText, final double width, final double height) {
        final TextField t = createTextField(promptText);
        t.setPrefSize(width, height);
        return t;
    }

    public static TextArea createTextArea(final String promptText, final double width, final double height) {
        final TextArea t = new TextArea();
        t.setPromptText(promptText);
        t.setPrefSize(width, height);
        t.setWrapText(true);
        return t;
    }

    public static Label createLabel(final String text, final double width, final double height) {
        final Label label = new Label(text);
        label.setPrefSize(width, height);
        label.setWrapText(true);
        return label;
    }

    public static ImageView createImageView(final String imageUrl, final double fitWidth, final double fitHeight) {
        final ImageView i = new ImageView(new Image(imageUrl));
        i.setFitWidth(fitWidth);
        i.setFitHeight(fitHeight);
        i.setPickOnBounds(true);
        i.setPreserveRatio(true);
        return i;
    }

    public static ButtonBar createButtonBar(final double width, final double height, final Button... buttons) {
        final ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPrefSize(width, height);
        buttonBar.getButtons().addAll(buttons);
        return buttonBar;
    }

    public static void initSeatTable(final TableView<SeatRowData> seatTable, final SeatConfig conf) {
        seatTable.setEditable(false);
        final int rowCount = conf.getRowCount();
        final int columnCount = conf.getColumnCount();
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

    public static void initConfigPane(final SeatConfig seatConfig,
                                      final TextField rowCountInput,
                                      final TextField columnCountInput,
                                      final TextField rbrInput,
                                      final TextField disabledLastRowPosInput,
                                      final TextField nameListInput,
                                      final TextField groupLeaderListInput,
                                      final TextArea separateListInput,
                                      final CheckBox luckyOption) {
        rowCountInput.setText(seatConfig.row_count);
        columnCountInput.setText(seatConfig.column_count);
        rbrInput.setText(seatConfig.random_between_rows);
        disabledLastRowPosInput.setText(seatConfig.last_row_pos_cannot_be_chosen);
        nameListInput.setText(seatConfig.person_sort_by_height);
        groupLeaderListInput.setText(seatConfig.group_leader_list);
        separateListInput.setText(seatConfig.separate_list);
        luckyOption.setSelected(seatConfig.lucky_option);
    }
}
