package com.edp2021c1.data;

import java.util.Arrays;

public class AppConfig {
    public String style;

    public AppConfig(){
        style="light";
    }

    public AppStyle getStyle(){
        AppStyle s;
        try {
            s=AppStyle.valueOf(style);
        }catch (IllegalArgumentException e){
            style="light";
            s=AppStyle.valueOf("light");
        }
        return s;
    }
}
