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

import lombok.Getter;
import lombok.val;

import java.util.List;

/**
 * Thrown if config has an illegal format.
 *
 * @author Calboot
 * @since 1.2.6
 */
@Getter
public class IllegalConfigException extends RuntimeException {

    /**
     * Localized message.
     */
    private final String localizedMessage;

    /**
     * If the exception is single.
     * True means one cause only, false otherwise.
     */
    private final boolean single;

    /**
     * Constructs a single {@code IllegalConfigException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public IllegalConfigException(final String message) {
        super(message);
        single = true;
        localizedMessage = message;
    }

    /**
     * Constructs a multiple exception with the given causes.
     *
     * @param causes of the exception
     */
    public IllegalConfigException(final List<IllegalConfigException> causes) {
        super();
        if (causes == null) {
            single = true;
            localizedMessage = null;
            return;
        }
        if (causes.size() == 1) {
            single = true;
            localizedMessage = causes.getFirst().getLocalizedMessage();
            return;
        }
        single = false;
        val str = new StringBuilder();
        causes.forEach(e -> {
            if (e.isSingle()) {
                str.append("\n");
            }
            str.append(e.getLocalizedMessage());
        });
        localizedMessage = str.toString();
    }

}
