package com.edp2021c1.randomseatgenerator.util;

/**
 * Logging levels.
 */
class Level extends java.util.logging.Level {
    /**
     * Indicates debug messages.
     */
    public static final Level DEBUG = new Level("DEBUG", 200);
    /**
     * @see java.util.logging.Level#SEVERE
     */
    public static final Level ERROR = new Level(SEVERE);

    protected Level(String name, int value) {
        super(name, value);
    }

    private Level(java.util.logging.Level level) {
        super(level.getName(), level.intValue());
    }
}
