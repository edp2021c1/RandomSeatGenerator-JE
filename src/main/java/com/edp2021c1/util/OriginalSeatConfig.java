package com.edp2021c1.util;

public class OriginalSeatConfig {
    private final String ot;
    private final String tf;
    private final String fs;
    private final String zz;
    private final String separate;

    public OriginalSeatConfig(String ot, String tf, String fs, String zz, String separate) {
        this.ot = ot;
        this.tf = tf;
        this.fs = fs;
        this.zz = zz;
        this.separate = separate;
    }

    public String getOt() {
        return ot;
    }

    public String getTf() {
        return tf;
    }

    public String getFs() {
        return fs;
    }

    public String getZz() {
        return zz;
    }

    public String getSeparate() {
        return separate;
    }
}
