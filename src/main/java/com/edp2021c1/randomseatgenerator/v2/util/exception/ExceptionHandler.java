package com.edp2021c1.randomseatgenerator.v2.util.exception;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final ExceptionHandler INSTANCE = new ExceptionHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handleException(t, e);
    }

    public void handleException(Thread t, Throwable e) {
        //TODO complete it here
    }

    public void handleException(Throwable e) {
        handleException(Thread.currentThread(), e);
    }

}
