package com.edp2021c1.randomseatgenerator.core;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Default checker implementation class.
 */
@FunctionalInterface
public interface SeatTableGeneratorAndCheckerBase extends SeatTableGenerator, SeatTableChecker {

    default boolean check(final List<String> seatTable, final SeatConfig config) throws IllegalConfigException {
        final List<String> gl = unmodifiableList(config.getGroupLeaders());
        final List<SeparatedPair> sp = unmodifiableList(config.getSeparatedPairs());
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
                hasLeader = gl.contains(seatTable.get(j * columnCount + i));
                if (hasLeader) {
                    break;
                }
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
