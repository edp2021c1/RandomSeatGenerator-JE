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

import com.edp2021c1.randomseatgenerator.util.DataUtils;
import com.edp2021c1.randomseatgenerator.util.Strings;
import com.edp2021c1.randomseatgenerator.util.exception.IllegalConfigException;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.groupLeaderFormat;
import static com.edp2021c1.randomseatgenerator.util.DataUtils.pickRandomlyAndRemove;
import static java.util.Collections.fill;

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
        if (config == null) {
            throw new IllegalConfigException("Config cannot be null");
        }

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
        val rowCount        = config.rowCount();
        val columnCount     = config.columnCount();
        val nameList        = config.names();
        val groupLeaderList = config.groupLeaders();
        val lucky           = config.lucky();

        // 防止lucky为true时数组越界
        val minus = lucky ? 1 : 0;

        // 临时变量，提前声明以减少内存和计算操作
        val peopleNum = nameList.size();

        // 防止行数过多引发死循环
        if (rowCount > Math.ceil((double) (peopleNum - minus) / columnCount)) {
            throw new IllegalConfigException("Too many seat with row count " + rowCount);
        }

        // 防止组长数量不足引发死循环
        if (groupLeaderList.size() < columnCount) {
            throw new IllegalConfigException("Not enough group leader for " + columnCount + " column(s)");
        }

        // 临时变量，提前声明以减少内存和计算操作
        val seatNum = rowCount * columnCount;

        if (seatNum < peopleNum) {
            throw new IllegalConfigException("Too many people and not enough seat");
        }

        val peopleInSeat      = peopleNum - minus;
        val peopleLeft        = peopleInSeat % columnCount;
        val noPeopleLeft      = peopleLeft == 0;
        val randomPeopleCount = Math.min(columnCount * config.randomBetweenRows(), peopleInSeat - peopleLeft);

        val forTimesMinusOne = peopleNum % randomPeopleCount > columnCount
                ? seatNum / randomPeopleCount
                : seatNum / randomPeopleCount - 1;

        val emptyRow = Arrays.asList(new String[columnCount]);
        fill(emptyRow, EMPTY_SEAT_PLACEHOLDER);

        val availableLastRowPos = IntStream.range(1, columnCount + 1).boxed().filter(value -> !config.disabledLastRowPos().contains(value)).toList();
        if (availableLastRowPos.size() < peopleLeft) {
            throw new IllegalConfigException("Available last row seat not enough");
        }

        // 座位表数据
        val seatTable   = new ArrayList<String>(seatNum);
        var luckyPerson = "";

        // 临时变量，提前声明以减少内存和计算操作
        val luckyPersonOriginIndex  = peopleNum - randomPeopleCount - peopleLeft;
        val seatNumMinusColumnCount = seatNum - columnCount;

        do {
            seatTable.clear();

            val tNameList            = new ArrayList<>(nameList);
            val tAvailableLastRowPos = new ArrayList<>(availableLastRowPos);

            if (lucky) {
                luckyPerson = pickRandomlyAndRemove(tNameList.subList(
                        luckyPersonOriginIndex,
                        peopleNum
                ), rd);
            }

            DataUtils.split(tNameList, randomPeopleCount).forEach(list -> Collections.shuffle(list, rd));

            if (noPeopleLeft) {
                seatTable.addAll(tNameList);
            } else {
                seatTable.addAll(tNameList.subList(0, seatNumMinusColumnCount));
                seatTable.addAll(emptyRow);
                IntStream
                        .range(seatNumMinusColumnCount, peopleInSeat)
                        .forEach(i ->
                                seatTable.set(pickRandomlyAndRemove(tAvailableLastRowPos, rd) + seatNumMinusColumnCount - 1, tNameList.get(i))
                        );
            }
        } while (!check(seatTable, config));

        IntStream
                .range(0, columnCount)
                .mapToObj(i ->
                        DataUtils.pickRandomly(
                                IntStream
                                        .iterate(i, i1 -> i1 + columnCount)
                                        .limit(rowCount)
                                        .mapToObj(seatTable::get)
                                        .filter(groupLeaderList::contains)
                                        .toList()
                                , rd))
                .forEach(tGroupLeader -> {
                    val t = seatTable.indexOf(tGroupLeader);
                    seatTable.set(t, groupLeaderFormat.formatted(tGroupLeader));
                });

        return new SeatTable(seatTable, config, seed, luckyPerson);
    }

}