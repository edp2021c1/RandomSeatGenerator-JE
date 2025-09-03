package com.edp2021c1.randomseatgenerator.v2.util.exception;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public enum ExceptionType {

    ILLEGAL_ARGUMENT("illegal_argument.", IllegalArgumentException.class),
    IO("io.", IOException.class),
    COMMON("", Throwable.class);

    public static ExceptionType of(@NotNull Throwable cause) {
        if (IO.isOf(cause)) {
            return IO;
        } else if (ILLEGAL_ARGUMENT.isOf(cause)) {
            return ILLEGAL_ARGUMENT;
        } else {
            return COMMON;
        }
    }

    public final String trKey;

    public final Class<? extends Throwable> clazz;

    ExceptionType(@NotNull String trKey, @NotNull Class<? extends Throwable> clazz) {
        this.trKey = trKey;
        this.clazz = clazz;
    }

    public boolean isOf(@NotNull Throwable cause) {
        return clazz.isAssignableFrom(cause.getClass());
    }

}
