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

package com.edp2021c1.randomseatgenerator.core;

import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.val;

import java.util.Arrays;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;

/**
 * Seat table generator class.
 *
 * @author Calboot
 * @since 1.5.0
 */
@FunctionalInterface
public interface SeatTableGenerator {

    /**
     * The default implementation of this class.
     */
    SeatTableGenerator defaultGenerator = SeatTableGeneratorAndCheckerImpl.instance;

    /**
     * Empty generator.
     */
    SeatTableGenerator emptyGenerator = (config, seed) -> {
        val seat = new String[config.getRowCount() * config.getColumnCount()];
        Arrays.fill(seat, EMPTY_SEAT_PLACEHOLDER);
        return new SeatTable(Arrays.asList(seat), config, EMPTY_SEAT_PLACEHOLDER, EMPTY_SEAT_PLACEHOLDER);
    };


    /**
     * Generates a seat table.
     *
     * @param config used to generate the seat table
     * @param seed   used to generate the seat table
     * @return an instance of {@code SeatTable}
     * @throws IllegalConfigException if config is null or is illegal
     */
    SeatTable generate(SeatConfig config, String seed)
            throws IllegalConfigException;

}
