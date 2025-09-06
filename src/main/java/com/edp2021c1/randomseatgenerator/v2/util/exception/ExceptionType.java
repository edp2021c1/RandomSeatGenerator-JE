package com.edp2021c1.randomseatgenerator.v2.util.exception;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public enum ExceptionType {

    IO("io", IOException.class),
    SEAT("seat", RuntimeException.class),
    COMMON("", Throwable.class);

    private static final String BASE = "randomseatgenerator.exception";

    public static ExceptionType of(@NotNull Throwable cause) {
        if (IO.isOf(cause)) {
            return IO;
        } else {
            return COMMON;
        }
    }

    public final String trKey;

    public final Class<? extends Throwable> clazz;

    ExceptionType(@NotNull String trKey, @NotNull Class<? extends Throwable> clazz) {
        this.trKey = trKey.isBlank() ? BASE : BASE + "." + trKey;
        this.clazz = clazz;
    }

    public boolean isOf(@NotNull Throwable cause) {
        return clazz.isAssignableFrom(cause.getClass());
    }

}
