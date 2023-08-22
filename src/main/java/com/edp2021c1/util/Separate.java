package com.edp2021c1.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Separate {
    public static final ArrayList<Integer> NOT_SEPARATED = new ArrayList<>(Arrays.asList(-1, -6, -7, -8, 1, 6, 7, 8));
    public final String a;
    public final String b;

    public Separate(String s) {
        String[] t = s.split(" ");
        a = t[0];
        b = t[1];
    }
}
