package com.edp2021c1.randomseatgenerator.ui.control;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import static com.edp2021c1.randomseatgenerator.ui.util.UIFactory.createHBox;

@Getter
public class SeatConfigPane extends VBox {
    private final TextField rowCountInput;

    private final TextField columnCountInput;

    private final TextField rbrInput;

    private final TextField disabledLastRowPosInput;

    private final TextField nameListInput;

    private final TextField groupLeaderListInput;

    private final TextArea separateListInput;

    private final CheckBox luckyOptionCheck;

    public SeatConfigPane(TextField rowCountInput, TextField columnCountInput, TextField rbrInput, TextField disabledLastRowPosInput, TextField nameListInput, TextField groupLeaderListInput, TextArea separateListInput, CheckBox luckyOptionCheck) {
        super();

        this.rowCountInput = rowCountInput;
        this.columnCountInput = columnCountInput;
        this.rbrInput = rbrInput;
        this.disabledLastRowPosInput = disabledLastRowPosInput;
        this.nameListInput = nameListInput;
        this.groupLeaderListInput = groupLeaderListInput;
        this.separateListInput = separateListInput;
        this.luckyOptionCheck = luckyOptionCheck;

        HBox box1 = createHBox(1212, 60, rowCountInput, columnCountInput, rbrInput, disabledLastRowPosInput);
        HBox box2 = createHBox(1212, 69, nameListInput, groupLeaderListInput, separateListInput, luckyOptionCheck);
        getChildren().addAll(box1, box2);
    }

}
