package com.edp2021c1.randomseatgenerator.v2.util.exception;

import com.edp2021c1.randomseatgenerator.util.Notice;
import com.edp2021c1.randomseatgenerator.v2.util.I18N;

import java.io.IOException;

public class TranslatableException extends RuntimeException implements Notice {

    public static TranslatableException common(String key, Object... args) {
        return new TranslatableException(key, args);
    }

    public static TranslatableException io(IOException e, String key, Object... args) {
        return new TranslatableException(e, key, args);
    }

    public static TranslatableException seat(String key, Object... args) {
        return new TranslatableException(ExceptionType.SEAT, key, args);
    }

    private final String key;

    private final Object[] args;

    private final ExceptionType type;

    private TranslatableException(String key, Object... args) {
        this(ExceptionType.COMMON, key, args);
    }

    private TranslatableException(ExceptionType type, String key, Object... args) {
        super(key);
        this.key = type.trKey + "." + key;
        this.args = args;
        this.type = type;
    }

    private TranslatableException(Throwable cause, String key, Object... args) {
        super(key, cause);
        this.type = ExceptionType.of(cause);
        this.key = this.type.trKey + "." + key;
        this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
        return message();
    }

    @Override
    public String title() {
        return I18N.tr(this.type.trKey);
    }

    @Override
    public String message() {
        return I18N.tr(this.key, args);
    }

}
