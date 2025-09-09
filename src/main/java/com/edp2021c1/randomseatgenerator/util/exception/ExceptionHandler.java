/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.util.exception;

import com.edp2021c1.randomseatgenerator.AppSettings;
import com.edp2021c1.randomseatgenerator.RandomSeatGenerator;
import com.edp2021c1.randomseatgenerator.ui.stage.CrashReporterDialog;
import com.edp2021c1.randomseatgenerator.ui.stage.MessageDialog;
import com.edp2021c1.randomseatgenerator.util.Notice;

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
