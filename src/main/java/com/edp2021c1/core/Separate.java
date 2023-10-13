package com.edp2021c1.core;

import java.util.Arrays;
import java.util.List;

/**
 * This class packs a pair of people which should be separated when generating the seat and
 * provides a method to check if they are separated in a certain seat table.
 *
 * @author Calboot
 * @since 1.0
 */
public class Separate {
    /**
     * If the difference between the index of {@code a} and {@code b} in a seat table is contained,
     * then it means that they are next to each other.
     */
    private static final List<Integer> NOT_SEPARATED = Arrays.asList(-1, -6, -7, -8, 1, 6, 7, 8);
    /**
     * The first person.
     */
    private final String a;
    /**
     * The second person.
     */
    private final String b;

    /**
     * @param s a {@code String} contains the names of the two people separated, divided by a {@code space}.
     */
    public Separate(String s) throws Exception {
        String[] t = s.split(" ");
        if (t.length < 2) throw new Exception("Invalid separate data");
        a = t[0];
        b = t[1];
    }

    /**
     * @param seat the seat table checked.
     * @return true if {@code a} and {@code b} are separated in the seat table, and false if not.
     * @see Separate#NOT_SEPARATED
     */
    public boolean check(List<String> seat) {
        return !NOT_SEPARATED.contains(seat.indexOf(a) - seat.indexOf(b));
    }
}
