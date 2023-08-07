package com.edp2021c1.ui;

import javafx.beans.property.SimpleStringProperty;

public class LibData {
    private final SimpleStringProperty lib;
    private final SimpleStringProperty license;

    public LibData(String lib, String license) {
        this.lib = new SimpleStringProperty(lib);
        this.license = new SimpleStringProperty(license);
    }

    public String getLib() {
        return lib.get();
    }

    public void setLib(String lib) {
        this.lib.set(lib);
    }

    public String getLicense() {
        return license.get();
    }

    public void setLicense(String license) {
        this.license.set(license);
    }
}
