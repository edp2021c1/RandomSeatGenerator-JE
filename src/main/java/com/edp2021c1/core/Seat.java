package com.edp2021c1.core;

import lombok.Getter;

import java.util.List;

@Getter
public class Seat {
    private final List<String> seat;
    private final SeatConfig config;
    private final long seed;
    private final String luckyPerson;

    public Seat(List<String> seat, SeatConfig config, long seed, String luckyPerson) {
        this.seat = seat;
        this.config = config;
        this.seed = seed;
        if (luckyPerson == null) luckyPerson = "";
        this.luckyPerson = luckyPerson;
    }
}
