package com.edp2021c1.randomseatgenerator.ui.stage;

import javafx.stage.Stage;

import static com.edp2021c1.randomseatgenerator.ui.FXUtils.decorate;

public abstract class DecoratedStage extends Stage {

    protected DecoratedStage() {
        super();
        decorate(this);
    }

    public abstract StageType getStageStyle();

    public enum StageType {

        /**
         * Identifies the main window in the application.
         */
        MAIN_WINDOW(0),
        /**
         * Identifies dialogs in the application.
         * <p>
         * Not resizable.
         * <p>
         * Always on the top of other windows of this app.
         */
        DIALOG(1),
        /**
         * Identifies crash reporter windows in the application.
         * <p>
         * Icon set to the error icon.
         */
        CRASH_REPORTER(2);

        private final int mask;

        StageType(final int mask) {
            this.mask = mask;
        }

        public int mask() {
            return mask;
        }
    }

}
