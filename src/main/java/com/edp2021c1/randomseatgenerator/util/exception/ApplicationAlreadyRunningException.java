/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
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

import com.edp2021c1.randomseatgenerator.util.useroutput.ExecutableOnCaught;
import com.edp2021c1.randomseatgenerator.util.useroutput.Notice;

/**
 * Thrown if another instance of this application is detected.
 *
 * @author Calboot
 * @since 1.5.0
 */
public class ApplicationAlreadyRunningException extends RuntimeException implements Notice, ExecutableOnCaught {

    /**
     * Default constructor.
     */
    public ApplicationAlreadyRunningException() {
        super();
    }

    @Override
    public String message() {
        return "Another instance of the application is already running";
    }

    @Override
    public void exec() {
        System.exit(1);
    }

}
