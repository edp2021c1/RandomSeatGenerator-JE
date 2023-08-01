package com.edp2021c1.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Separate {
    public static final ArrayList<Integer> notSeparated = new ArrayList<>(Arrays.asList(-1, -6, -7, -8, 1, 6, 7, 8));
    private final String a;
    private final String b;

    public Separate(String s) {
        String[] t = s.split(" ");
        if (t.length > 2) {
            new Exception("Invalid separate info").printStackTrace();
        }
        a = t[0];
        b = t[1];
    }

    public boolean check(ArrayList<String> seat) {
        Integer t = seat.indexOf(a) - seat.indexOf(b);
        return !notSeparated.contains(t);
    }

    public String toString(){
        return this.a+" "+this.b;
    }
}
