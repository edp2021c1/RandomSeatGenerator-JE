package com.edp2021c1.randomseatgenerator.ui.stage;

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
