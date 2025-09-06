package com.edp2021c1.randomseatgenerator.v2.util.exception;

import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.ui.stage.CrashReporterDialog;
import com.edp2021c1.randomseatgenerator.ui.stage.MessageDialog;
import com.edp2021c1.randomseatgenerator.util.Notice;
import com.edp2021c1.randomseatgenerator.v2.AppSettings;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final ExceptionHandler INSTANCE = new ExceptionHandler();

    private void handleException(Thread t, Throwable e, boolean uncaught) {
        if (uncaught) {
            RandomSeatGenerator.LOGGER.error("Uncaught exception in thread [{}]", t.getName(), e);
        } else {
            RandomSeatGenerator.LOGGER.error("Exception in thread [{}]", t.getName(), e);
        }
        if (AppSettings.withGUI && AppSettings.initializingDone) {
            if (e instanceof Notice n) {
                MessageDialog.showMessage(n);
            } else {
                CrashReporterDialog.showCrashReporter(Notice.of(t, e));
            }
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handleException(t, e, true);
    }

    public void handleException(Throwable e) {
        handleException(Thread.currentThread(), e, false);
    }

}
