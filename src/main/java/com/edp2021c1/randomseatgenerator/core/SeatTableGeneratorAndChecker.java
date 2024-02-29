package com.edp2021c1.randomseatgenerator.core;

import lombok.NonNull;

import java.util.List;

/**
 * Default checker implementation class.
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
    default boolean check(@NonNull final List<String> seatTable, @NonNull final SeatConfig config) throws IllegalConfigException {
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
