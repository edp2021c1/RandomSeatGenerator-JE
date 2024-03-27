/*
 * RandomSeatGenerator
 * Copyright (C) 2023  EDP2021C1
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

import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.groupLeaderFormat;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.*;
import static java.util.Collections.fill;
import static java.util.Collections.shuffle;

/**
 * Default generator implementation class.
 *
 * @author Calboot
 * @since 1.5.1
 */
public class SeatTableGeneratorAndCheckerImpl implements SeatTableGeneratorAndChecker {

    static final SeatTableGenerator instance = new SeatTableGeneratorAndCheckerImpl();

    /**
     * Don't let anyone else instantiate this class.
     */
    private SeatTableGeneratorAndCheckerImpl() {
    }

    @Override
    public SeatTable generate(final SeatConfig config, String seed) throws IllegalConfigException {
        long longSeed;
        try {
            longSeed = Long.parseLong(seed);
            seed += " (integer)";
        } catch (final RuntimeException e) {
            if (seed == null || seed.isEmpty()) {
                longSeed = 0;
            } else {
                longSeed = Strings.longHashCode(seed);
                seed += " (string)";
            }
        }

        val rd = new Random(longSeed);

        // 获取配置
        val rowCount = config.getRowCount();
        val columnCount = config.getColumnCount();
        val nameList = config.getNames();
        val groupLeaderList = config.getGroupLeaders();
        val lucky = config.isLucky();

        // 防止lucky为true时数组越界
        val minus = lucky ? 1 : 0;

        // 临时变量，提前声明以减少内存和计算操作
        val peopleNum = nameList.size();

        // 防止行数过多引发死循环
        if (rowCount > Math.ceil((double) (peopleNum - minus) / columnCount)) {
            throw new IllegalConfigException("Too many seat with row count " + rowCount);
        }

        // 防止组长不足引发死循环
        if (groupLeaderList.size() < columnCount) {
            throw new IllegalConfigException("Not enough group leader for " + columnCount + " column(s)");
        }

        // 临时变量，提前声明以减少内存和计算操作
        val seatNum = rowCount * columnCount;

        if (seatNum < peopleNum) {
            throw new IllegalConfigException("Too many people and not enough seat");
        }

        val peopleInSeat = peopleNum - minus;
        val peopleLeft = peopleInSeat % columnCount;
        val noPeopleLeft = peopleLeft == 0;
        val randomPeopleCount = Math.min(columnCount * config.getRandomBetweenRows(), peopleInSeat - peopleLeft);

        val forTimesMinusOne = peopleNum % randomPeopleCount > columnCount
                ? seatNum / randomPeopleCount
                : seatNum / randomPeopleCount - 1;

        val emptyRow = Arrays.asList(new String[columnCount]);
        fill(emptyRow, EMPTY_SEAT_PLACEHOLDER);

        val availableLastRowPos = elementFilter(range(1, columnCount + 1), i -> !config.getDisabledLastRowPos().contains(i));
        if (availableLastRowPos.size() < peopleLeft) {
            throw new IllegalConfigException("Available last row seat not enough");
        }

        // 座位表数据
        val seatTable = new ArrayList<String>(seatNum);
        var luckyPerson = "";

        // 临时变量，提前声明以减少内存和计算操作
        val luckyPersonOriginIndex = peopleNum - randomPeopleCount - peopleLeft;
        val peopleNumShuffledInLoop = forTimesMinusOne * randomPeopleCount;
        val seatNumMinusColumnCount = seatNum - columnCount;

        do {
            seatTable.clear();

            val tNameList = modifiableList(nameList);
            val tAvailableLastRowPos = modifiableList(availableLastRowPos);

            if (lucky) {
                luckyPerson = pickRandomlyAndRemove(tNameList.subList(
                        luckyPersonOriginIndex,
                        peopleNum
                ), rd);
            }

            for (var i = 0; i < peopleNumShuffledInLoop; ) {
                shuffle(tNameList.subList(i, i += randomPeopleCount), rd);
            }
            shuffle(tNameList.subList(peopleNumShuffledInLoop, peopleInSeat), rd);

            if (noPeopleLeft) {
                seatTable.addAll(tNameList);
            } else {
                seatTable.addAll(tNameList.subList(0, seatNumMinusColumnCount));
                seatTable.addAll(emptyRow);
                for (var i = seatNumMinusColumnCount; i < peopleInSeat; i++) {
                    seatTable.set(pickRandomlyAndRemove(tAvailableLastRowPos, rd) + seatNumMinusColumnCount - 1, tNameList.get(i));
                }
            }
        } while (!check(seatTable, config));

        for (var i = 0; i < columnCount; i++) {
            int t;
            String tGroupLeader;
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, groupLeaderFormat.formatted(tGroupLeader));
        }

        return new SeatTable(seatTable, config, seed, luckyPerson);
    }

}