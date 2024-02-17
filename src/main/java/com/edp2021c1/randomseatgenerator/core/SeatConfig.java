package com.edp2021c1.randomseatgenerator.core;

import java.util.List;

/**
 * Stores config used to generate seat table.
 *
 * @author Calboot
 * @since 1.5.0
 */
public interface SeatConfig {
    int getRowCount();

    int getColumnCount();

    int getRandomBetweenRows();

    List<Integer> getDisabledLastRowPos();

    List<String> getNames();

    List<String> getGroupLeaders();

    List<SeparatedPair> getSeparatedPairs();

    Boolean isLucky();
}
