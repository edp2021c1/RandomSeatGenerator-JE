package com.edp2021c1.data;

import lombok.Getter;

@Getter
public class LibData {
    private final String lib;
    private final String license;

    public LibData(String lib, String license) {
        this.lib = lib;
        this.license = license;
    }
}
