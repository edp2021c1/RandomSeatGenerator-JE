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

import java.util.List;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.indexFilter;

/**
 * Default checker implementation class.
 *
 * @author Calboot
 * @since 1.5.1
 */
@FunctionalInterface
public interface SeatTableGeneratorAndChecker extends SeatTableGenerator {

    /**
     * Returns if the seat table is valid.
     *
     * @param seatTable to check
     * @param config    used to check
     * @return if the seat table is valid
     * @throws IllegalConfigException if config is invalid
     */
    default boolean check(final List<String> seatTable, final SeatConfig config) throws IllegalConfigException {
        val gl = config.getGroupLeaders();
        val columnCount = config.getColumnCount();

        // 检查每列是否都有组长
        for (var i = 0; i < columnCount; i++) {
            val iCopy = i;
            if (indexFilter(seatTable, index -> index % columnCount == iCopy).stream().noneMatch(gl::contains)) {
                return false;
            }
        }
        // 检查是否分开
        for (final SeparatedPair separatedPair : config.getSeparatedPairs()) {
            if (!separatedPair.check(seatTable, columnCount)) {
                return false;
            }
        }

        return true;
    }
}
