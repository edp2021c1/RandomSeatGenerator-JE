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
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.modifiableRange;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.pickRandomlyAndRemove;
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
    public SeatTable generate(final SeatConfig config, String seed) throws NullPointerException, IllegalConfigException {
        long longSeed;
        try {
            longSeed = Long.parseLong(seed);
            seed += " (integer)";
        } catch (RuntimeException e) {
            if (seed == null || seed.isEmpty()) {
                longSeed = 0;
                seed = "empty_string";
            } else {
                longSeed = Strings.longHashCode(seed);
                seed += " (string)";
            }
        }

        val rd = new Random(longSeed);

        // 获取配置
        final int rowCount;
        val columnCount = config.getColumnCount();
        final int randomBetweenRows;
        val nameList = config.getNames();
        val groupLeaderList = config.getGroupLeaders();
        val lucky = config.isLucky();

        // 防止lucky为true时数组越界
        val minus = lucky ? 1 : 0;

        // 临时变量，提前声明以减少内存和计算操作
        val peopleNum = nameList.size();

        // 防止行数过多引发无限递归
        rowCount = Math.min(config.getRowCount(), (int) Math.ceil((double) (peopleNum - minus) / columnCount));
        randomBetweenRows = Math.min(config.getRandomBetweenRows(), rowCount);

        // 临时变量，提前声明以减少内存和计算操作
        val seatNum = rowCount * columnCount;

        if (seatNum < peopleNum) {
            throw new IllegalConfigException("Too many people and not enough seat");
        }

        val tPeopleNum = peopleNum - minus;
        val peopleLeft = tPeopleNum % columnCount;
        val randomPeopleCount = Math.min(columnCount * randomBetweenRows, tPeopleNum - peopleLeft);

        val forTimesMinusOne = (
                peopleNum % randomPeopleCount > columnCount
                        ? seatNum / randomPeopleCount + 1
                        : seatNum / randomPeopleCount
        ) - 1;

        val emptyRow = Arrays.asList(new String[columnCount]);
        fill(emptyRow, EMPTY_SEAT_PLACEHOLDER);

        val availableLastRowPos = modifiableRange(1, columnCount);
        availableLastRowPos.removeAll(config.getDisabledLastRowPos());
        if (availableLastRowPos.size() < peopleLeft) {
            throw new IllegalConfigException("Available last row seat not enough");
        }

        val tNameList = new ArrayList<String>(tPeopleNum);
        val tAvailableLastRowPos = new ArrayList<Integer>(availableLastRowPos.size());

        String tGroupLeader;

        // 座位表数据
        val seatTable = new ArrayList<String>(seatNum);
        var luckyPerson = "";

        do {
            seatTable.clear();
            tNameList.clear();
            tNameList.addAll(nameList);
            tAvailableLastRowPos.clear();
            tAvailableLastRowPos.addAll(availableLastRowPos);

            if (lucky) {
                luckyPerson = pickRandomlyAndRemove(tNameList.subList(
                        peopleNum - randomPeopleCount - peopleLeft,
                        peopleNum
                ), rd);
            }

            for (var i = 0; i < forTimesMinusOne; i++) {
                shuffle(tNameList.subList(i * randomPeopleCount, (i + 1) * randomPeopleCount), rd);
            }
            shuffle(tNameList.subList(forTimesMinusOne * randomPeopleCount, tPeopleNum), rd);

            if (peopleLeft == 0) {
                seatTable.addAll(tNameList.subList(0, seatNum));
            } else {
                seatTable.addAll(tNameList.subList(0, seatNum - columnCount));
                seatTable.addAll(emptyRow);
                for (var i = seatNum - columnCount; i < tPeopleNum; i++) {
                    seatTable.set(pickRandomlyAndRemove(tAvailableLastRowPos, rd) + seatNum - columnCount - 1, tNameList.get(i));
                }
            }
        } while (!check(seatTable, config));

        for (var i = 0; i < columnCount; i++) {
            int t;
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, groupLeaderFormat.formatted(tGroupLeader));
        }

        return new SeatTable(seatTable, config, seed, luckyPerson);
    }

}