package com.edp2021c1.randomseatgenerator.core;

import java.util.Arrays;
import java.util.List;

/**
 * This class packs the names of a pair of people separated when generating the seat and
 * provides a method to check if they are separated in a certain seat table.
 *
 * @author Calboot
 * @since 1.0.0
 */
public class Separate {
    private final String name_1;
    private final String name_2;

    /**
     * Create an instance from a {@code String}.
     * <p>
     * The format of the {@code String} should be "name_1 name_2".
     * <p>
     * Note that every character after the first space will be taken as the second name.
     *
     * @param s a {@code String} contains the names of the two people separated, divided by a {@code space}.
     * @throws IllegalSeatConfigException if the {@code String} contains only one name.
     */
    public Separate(String s) throws IllegalSeatConfigException {
        String[] t = s.split(" ", 2);
        if (t.length < 2) {
            throw new IllegalSeatConfigException(String.format("Invalid separate pair: \"%s\".", s));
        }
        name_1 = t[0];
        name_2 = t[1];
    }

    /**
     * Check if {@code name_1} and {@code name_2} are separated in the specified seat table.
     *
     * @param seat        the seat table checked.
     * @param columnCount count of columns of the seat table.
     * @return if {@code name_1} and {@code name_2} are separated in the seat table.
     */
    public boolean check(List<String> seat, int columnCount) {
        List<Integer> notSeparated = Arrays.asList(-columnCount - 1, -columnCount, -columnCount + 1, -1, 1, columnCount - 1, columnCount, columnCount + 1);
        return !notSeparated.contains(seat.indexOf(name_1) - seat.indexOf(name_2));
    }
}
