package com.edp2021c1.core;

import java.util.ArrayList;

@Deprecated
public class Seat {
    public final ArrayList<String> seat;
    public final long seed;

    public Seat(ArrayList<String> st, long sd) {
        seat = st;
        seed = sd;
    }
}
