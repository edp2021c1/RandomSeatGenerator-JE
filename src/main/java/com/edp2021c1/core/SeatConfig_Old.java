package com.edp2021c1.core;

import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class SeatConfig_Old {
    public final ArrayList<String> frontRows;
    public final ArrayList<String> middleRows;
    public final ArrayList<String> backRows;
    public final ArrayList<String> groupLeaders;
    public final ArrayList<Separate> separated;

    public SeatConfig_Old(String fr, String mr, String br, String gl, String sp) {
        frontRows = new ArrayList<>(Arrays.asList(fr.split(" ")));
        middleRows = new ArrayList<>(Arrays.asList(mr.split(" ")));
        backRows = new ArrayList<>(Arrays.asList(br.split(" ")));
        groupLeaders = new ArrayList<>(Arrays.asList(gl.split(" ")));

        separated = new ArrayList<>();
        String[] t = sp.split("\n");
        for (String i : t) {
            if (!i.isEmpty()) {
                separated.add(new Separate(i));
            }
        }
    }
}
