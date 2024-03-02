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

import java.util.List;

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
        final List<String> gl = config.getGroupLeaders();
        final List<SeparatedPair> sp = config.getSeparatedPairs();
        boolean hasLeader = false;
        final int spNum = sp.size();
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int minus = config.isLucky() ? 1 : 0;
        rowCount = (int) Math.min(
                config.getRowCount(),
                Math.ceil((double) (config.getNames().size() - minus) / columnCount)
        );

        // 检查每列是否都有组长
        for (int i = 0; i < columnCount; i++) {
            for (int j = 0; j < rowCount; j++) {
                hasLeader = gl.contains(seatTable.get(j * columnCount + i)) || hasLeader;
            }
            if (!hasLeader) {
                return false;
            }
            hasLeader = false;
        }
        // 检查是否分开
        for (int i = 0; i < spNum; i++) {
            if (!sp.get(i).check(seatTable, columnCount)) {
                return false;
            }
        }

        return true;
    }
}
