package com.edp2021c1.util;

import java.util.ArrayList;
import java.util.Arrays;

public class SeatConfig {
    public ArrayList<String> frontRows, middleRows, backRows, groupLeaders;
    public ArrayList<Separate> separated;

    public SeatConfig(String fr, String mr, String br, String gl, String sp) {
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
