package com.edp2021c1.randomseatgenerator.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Stores config used to generate seat table.
 *
 * @author Calboot
 * @since 1.4.8
 */
@Getter
@Setter
public class SeatConfig {
    /**
     * Max count of column in a {@code SeatRowData}.
     */
    public static final int MAX_COLUMN_COUNT = 20;

    private int rowCount;
    private int columnCount;
    private int randomBetweenRows;
    private List<Integer> disabledLastRowPos;
    private List<String> names;
    private List<String> groupLeaders;
    private List<SeparatedPair> separatedPairs;
    private boolean lucky;
}
