package com.edp2021c1.util;

import java.util.ArrayList;

public class Seat {
    private final ArrayList<String> seat;
    private final long seed;

    public Seat(ArrayList<String> st, long sd) {
        seat = st;
        seed = sd;
    }

    public ArrayList<String> getSeat() {
        return seat;
    }

    public long getSeed() {
        return seed;
    }
}
