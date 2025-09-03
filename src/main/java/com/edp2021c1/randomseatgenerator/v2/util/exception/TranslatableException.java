package com.edp2021c1.randomseatgenerator.v2.util.exception;

import com.edp2021c1.randomseatgenerator.v2.util.I18N;

import java.io.IOException;

public class TranslatableException extends RuntimeException {

    private static final String BASE = "randomseatgenerator.exception.";

    public static TranslatableException illegalArgument(String key, Object... args) {
        return new TranslatableException(ExceptionType.ILLEGAL_ARGUMENT, key, args);
    }

    public static TranslatableException common(String key, Object... args) {
        return new TranslatableException(key, args);
    }

    public static TranslatableException io(IOException e, String key, Object... args) {
        return new TranslatableException(e, key, args);
    }

    private final String key;

    private final Object[] args;

    private TranslatableException(String key, Object... args) {
        this(ExceptionType.COMMON, key, args);
    }

    private TranslatableException(ExceptionType type, String key, Object... args) {
        super(key);
        this.key = BASE + type.trKey + key;
        this.args = args;
    }

    private TranslatableException(Throwable cause, String key, Object... args) {
        super(key, cause);
        this.key = BASE + ExceptionType.of(cause) + key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return I18N.tr(this.key, args);
    }

}
