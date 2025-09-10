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

package com.edp2021c1.randomseatgenerator;

import com.edp2021c1.randomseatgenerator.util.exception.ExceptionHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public final class RandomSeatGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger("RandomSeatGenerator");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create();

    public static void main(String[] args) {
        Thread.currentThread().setName("main");

        try {
            javafx.application.Application.launch(AppLaunch.class, args);
        } catch (Exception e) {
            ExceptionHandler.INSTANCE.handleException(e);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(
                    null,
                    "JavaFX missing, you'll need to install it",
                    "JFX Missing",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
