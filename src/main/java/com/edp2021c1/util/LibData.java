package com.edp2021c1.util;

public class LibData {
    private final String lib;
    private final String license;

    public LibData(String lib, String license) {
        this.lib = lib;
        this.license = license;
    }

    public String getLib() {
        return lib;
    }

    public String getLicense() {
        return license;
    }
}
