/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.edp2021c1.randomseatgenerator.core;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SeatConfig {

    @SerializedName(value = "rows", alternate = "row_count")
    public int rowCount;

    @SerializedName(value = "columns", alternate = "column_count")
    public int columnCount;

    @SerializedName(value = "shuffledRows", alternate = "random_between_rows")
    public int shuffledRowCount;

    @SerializedName(value = "disabledLastRowPos", alternate = "last_row_pos_cannot_be_chosen")
    public String disabledLastRowPositions;

    @SerializedName(value = "names", alternate = "person_sort_by_height")
    public String nameList;

    @SerializedName(value = "leaders", alternate = "group_leader_list")
    public String leaderNameSet;

    @SerializedName(value = "separated", alternate = "separate_list")
    public String separatedPairs;

    @SerializedName(value = "findLucky", alternate = "lucky_option")
    public boolean findLucky;

    @SerializedName(value = "findLeaders")
    public boolean findLeaders;

    public SeatConfig copy() {
        SeatConfig seatConfig = new SeatConfig();
        seatConfig.rowCount = rowCount;
        seatConfig.columnCount = columnCount;
        seatConfig.shuffledRowCount = shuffledRowCount;
        seatConfig.disabledLastRowPositions = disabledLastRowPositions;
        seatConfig.nameList = nameList;
        seatConfig.leaderNameSet = leaderNameSet;
        seatConfig.separatedPairs = separatedPairs;
        seatConfig.findLucky = findLucky;
        seatConfig.findLeaders = findLeaders;
        return seatConfig;
    }

}
