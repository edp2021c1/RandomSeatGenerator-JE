package com.edp2021c1.util;

import java.util.ArrayList;

public class Seat {
    private final ArrayList<String> seat;
    private final long seed;
    public int generation;

    public Seat(ArrayList<String> st, long sd, int gnrt) {
        this.seat = st;
        this.seed = sd;
        this.generation = gnrt;
    }

    public ArrayList<String> getSeat() {
        return seat;
    }

    public long getSeed() {
        return seed;
    }
}
