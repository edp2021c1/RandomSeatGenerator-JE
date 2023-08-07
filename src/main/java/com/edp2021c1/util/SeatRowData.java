package com.edp2021c1.util;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.ArrayList;

public class SeatRowData {
    @ExcelProperty("第七列")
    private String c1;
    @ExcelProperty("第六列")
    private String c2;
    @ExcelProperty("第五列")
    private String c3;
    @ExcelProperty("第四列")
    private String c4;
    @ExcelProperty("第三列")
    private String c5;
    @ExcelProperty("第二列")
    private String c6;
    @ExcelProperty("第一列")
    private String c7;

    public SeatRowData(String c1, String c2, String c3, String c4, String c5, String c6, String c7) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
        this.c5 = c5;
        this.c6 = c6;
        this.c7 = c7;
    }

    public static ArrayList<SeatRowData> fromSeat(Seat seat) {
        ArrayList<String> l = seat.getSeat();
        ArrayList<SeatRowData> s = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            s.add(new SeatRowData(l.get(i), l.get(i + 1), l.get(i + 2), l.get(i + 3), l.get(i + 4), l.get(i + 5), l.get(i + 6)));
        }
        s.add(new SeatRowData("Seed:", String.format("%d", seat.getSeed()), "", "", "", "", ""));

        return s;
    }

    public String getC1() {
        return c1;
    }

    public String getC2() {
        return c2;
    }

    public String getC3() {
        return c3;
    }

    public String getC4() {
        return c4;
    }

    public String getC5() {
        return c5;
    }

    public String getC6() {
        return c6;
    }

    public String getC7() {
        return c7;
    }
}
