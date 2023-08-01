package com.edp2021c1.util;

import java.io.File;
import java.util.ArrayList;

public class Seat {
	public ArrayList<String> seat;
	public long seed;
	public int generation;

	public Seat(ArrayList<String> st, long sd, int gnrt) {
		this.seat = st;
		this.seed = sd;
		this.generation = gnrt;
	}

}
