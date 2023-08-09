package com.edp2021c1.util;

public class LibData {
    private String lib;
    private String license;

    public LibData(String lib, String license) {
        this.lib = lib;
        this.license = license;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
