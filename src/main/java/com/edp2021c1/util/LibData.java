package com.edp2021c1.util;

import lombok.Getter;

public class LibData {
    @Getter
    private final String lib;
    @Getter
    private final String license;

    public LibData(String lib, String license) {
        this.lib = lib;
        this.license = license;
    }
}
