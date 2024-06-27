package com.edp2021c1.randomseatgenerator.ui.stage;

import com.edp2021c1.randomseatgenerator.ui.FXUtils;
import javafx.stage.Stage;

import static com.edp2021c1.randomseatgenerator.ui.FXUtils.decorate;

/**
 * A stage that has a {@link StageType} and can be resolved and decorated by {@link FXUtils#decorate(DecoratedStage)}
 *
 * @author Calboot
 * @since 1.6.0
 */
public abstract class DecoratedStage extends Stage {

    /**
     * Constructs and decorates an instance.
     */
    protected DecoratedStage() {
        super();
        decorate(this);
    }

    /**
     * Returns the type of the stage.
     *
     * @return the type of the stage
     *
     * @implSpec subclasses that implements this method should make sure the returned value
     * is always the same for each time the method is called and for every instance.
     */
    public abstract StageType getStageType();

    /**
     * Type of the stage.
     */
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

        private final int level;

        StageType(final int level) {
            this.level = level;
        }

        /**
         * Returns the level of the type. The higher it is, the more customized the stage is.
         *
         * @return the level of the type
         */
        public int level() {
            return level;
        }
    }

}
