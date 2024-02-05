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

    private int rowCount;
    private int columnCount;
    private int randomBetweenRows;
    private List<Integer> disabledLastRowPos;
    private List<String> names;
    private List<String> groupLeaders;
    private List<SeparatedPair> separatedPairs;
    private boolean lucky;

    /**
     * Constructs an instance.
     */
    public SeatConfig() {
    }
}
