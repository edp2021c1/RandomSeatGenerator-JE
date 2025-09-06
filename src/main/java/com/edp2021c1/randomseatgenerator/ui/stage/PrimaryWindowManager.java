package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.Notice;
import com.edp2021c1.randomseatgenerator.util.OperatingSystem;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;
import com.edp2021c1.randomseatgenerator.v2.seat.SeatGenerator;
import com.edp2021c1.randomseatgenerator.v2.util.IOUtils;
import com.edp2021c1.randomseatgenerator.v2.util.Metadata;
import com.edp2021c1.randomseatgenerator.v2.util.SeatUtils;
import com.edp2021c1.randomseatgenerator.v2.util.exception.ExceptionHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static com.edp2021c1.randomseatgenerator.RandomSeatGenerator.LOGGER;
import static com.edp2021c1.randomseatgenerator.ui.FXUtils.*;

public final class PrimaryWindowManager {

    @Getter
    private static Stage primaryStage = null;

    private static SeatTableView seatTableView;

    private static StringProperty seed;

    private static ObjectProperty<com.edp2021c1.randomseatgenerator.v2.seat.SeatTable> seatTable;

    private static SeatGenerator seatGenerator;

    private static FileChooser fileChooser;

    private static String previousSeed = null;

    private static boolean generated;

    /**
     * Creates an instance.
     */
    private static void init0() {
        FXUtils.decorate(primaryStage, StageType.MAIN_WINDOW);

        /* *************************************************************************
         *                                                                         *
         * Init Controls                                                           *
         *                                                                         *
         **************************************************************************/

        // 左侧按钮栏
        Button settingsBtn = createButton("设置", 80, 26);
        Button generateBtn = createButton("生成", 80, 26);
        Button exportBtn   = createButton("导出", 80, 26);
        VBox   leftBox     = createVBox(settingsBtn, generateBtn, exportBtn);
        leftBox.getStyleClass().add("left");

        // 右上种子输入栏
        TextField seedInput     = createEmptyTextField("种子");
        Button    randomSeedBtn = createButton("随机种子", 80, 26);
        Button    dateAsSeedBtn = createButton("填入日期", 80, 26);

        seed = seedInput.textProperty();

        // 座位表
        seatTableView = new SeatTableView(AppSettings.config.seatConfig);
        seatGenerator = new SeatGenerator(AppSettings.config.seatConfig);
        seatTable = seatTableView.seatTableProperty();

        // 右侧主体
        VBox rightBox = createVBox(createHBox(seedInput, randomSeedBtn, dateAsSeedBtn), seatTableView);
        rightBox.getStyleClass().add("right");

        // 整体
        HBox mainBox = createHBox(leftBox, new Separator(Orientation.VERTICAL), rightBox);
        mainBox.getStyleClass().add("main");

        setInsets(new Insets(5), settingsBtn, generateBtn, exportBtn, seedInput, randomSeedBtn, dateAsSeedBtn);
        VBox.setVgrow(seatTableView, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        primaryStage.setScene(new Scene(mainBox));
        primaryStage.setTitle(Metadata.TITLE);

        fileChooser = new FileChooser();
        fileChooser.setTitle("导出座位表");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 工作薄", "*.xlsx"),
                new FileChooser.ExtensionFilter("Excel 97-2004 工作薄", "*.xls")
                //,
                //new FileChooser.ExtensionFilter("CSV 逗号分隔", "*.csv")
        );

        fileChooser.setInitialDirectory(Metadata.DATA_DIR.toFile());

        /* *************************************************************************
         *                                                                         *
         * Init Control Actions                                                    *
         *                                                                         *
         **************************************************************************/

        settingsBtn.setOnAction(event -> SettingsDialog.getSettingsDialog().showAndWait());

        generateBtn.setOnAction(event -> generateSeatTable());
        generateBtn.setDefaultButton(true);

        exportBtn.setOnAction(event -> exportSeatTable());

        seedInput.setOnAction(event -> generateSeatTable());

        randomSeedBtn.setOnAction(event -> generateRandomSeed());

        dateAsSeedBtn.setOnAction(event -> generateDateSeed());

        if (OperatingSystem.MAC == OperatingSystem.getCurrent()) {
            primaryStage.setFullScreenExitHint("");
            mainBox.setOnKeyPressed(event -> {
                if (!event.isMetaDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case W -> primaryStage.close();
                    case F -> primaryStage.setFullScreen(event.isControlDown() != primaryStage.isFullScreen());
                    case COMMA -> settingsBtn.fire();
                    case S -> exportBtn.fire();
                    case R -> randomSeedBtn.fire();
                    case D -> dateAsSeedBtn.fire();
                }
            });
        } else {
            mainBox.setOnKeyPressed(event -> {
                if (!event.isControlDown()) {
                    return;
                }
                switch (event.getCode()) {
                    case S -> exportBtn.fire();
                    case R -> randomSeedBtn.fire();
                }
            });
        }

        primaryStage.setOnCloseRequest(event -> primaryStage.close());
    }

    private static void generateSeatTable() {
        try {
            if (Objects.equals(previousSeed, seed.get())) {
                generateRandomSeed();
            }

            String seed1 = seed.get();
            seatTable.set(seatGenerator.generate(seed1));
            LOGGER.info("{}{}", System.lineSeparator(), seatTable.get());
            previousSeed = seed1;
            generated = true;
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    private static void generateRandomSeed() {
        seed.set(Strings.randomString(30));
    }

    private static void exportSeatTable() {
        try {
            if (!generated) {
                generateSeatTable();
            }

            fileChooser.setInitialDirectory(
                    IOUtils.getClosestDirectory(Objects.requireNonNullElseGet(fileChooser.getInitialDirectory(), Metadata.DATA_DIR::toFile))
            );

            fileChooser.setInitialFileName(Strings.nowStrShort());

            File exportFile = fileChooser.showSaveDialog(primaryStage);
            if (exportFile == null) {
                return;
            }
            LOGGER.debug("Exporting seat table to \"{}\"", exportFile);
            Path   p = exportFile.toPath();
            String s = p.toString();
            if (s.endsWith(".xlsx")) {
                SeatUtils.exportToXlsx(seatTable.get(), p);
            } else if (s.endsWith(".xls")) {
                SeatUtils.exportToXls(seatTable.get(), p);
            }
            LOGGER.info("Successfully export seat table");

            MessageDialog.showMessage(primaryStage, Notice.of("成功导出座位表到" + System.lineSeparator() + exportFile));

            fileChooser.setInitialDirectory(exportFile.getParentFile());
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        }
    }

    private static void generateDateSeed() {
        seed.set(Strings.nowStr());
    }

    public static void init(Stage primaryStage) {
        if (PrimaryWindowManager.primaryStage != null) {
            throw new IllegalStateException("Primary stage already initialized");
        }
        PrimaryWindowManager.primaryStage = primaryStage;

        init0();
    }

    /**
     * Action to do if config is changed.
     */
    public static void configChanged() {
        seatTableView.setEmptySeatTable(AppSettings.config.seatConfig);
        seatGenerator = new SeatGenerator(AppSettings.config.seatConfig);
        generated = false;
        previousSeed = null;
    }

}
