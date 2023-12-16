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

import com.edp2021c1.randomseatgenerator.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages the generation of seat tables.
 *
 * @author Calboot
 * @since 1.2.0
 */
public class SeatTableFactory {

    /**
     * Generates a seat table.
     *
     * @param config used to generate the seat table.
     * @param seed   used to generate the seat table.
     * @return an instance of {@code SeatTable}.
     * @throws NullPointerException   if the config is null.
     * @throws IllegalConfigException if the config has an illegal format.
     */
    private static SeatTable generate0(final SeatConfig config, String seed)
            throws NullPointerException, IllegalConfigException {
        if (config == null) {
            throw new NullPointerException("Config cannot be null");
        }
        config.checkFormat();

        long longSeed;
        try {
            longSeed = Long.parseLong(seed);
            seed += " (integer)";
        } catch (RuntimeException e) {
            longSeed = StringUtils.longHash(seed);
            seed += " (string)";
        }

        final Random rd = new Random(longSeed);

        // 获取配置
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int randomRowCount;
        final ArrayList<Integer> notAllowedLastRowPos = config.getNotAllowedLastRowPos();
        final ArrayList<String> nameList = config.getNameList();
        final ArrayList<String> groupLeaderList = config.getGroupLeaderList();
        final boolean lucky = config.lucky_option;

        // 防止lucky为true时数组越界
        final int minus = lucky ? 1 : 0;

        // 临时变量，提前声明以减少内存和计算操作
        final int peopleNum = nameList.size();

        // 防止行数过多引发无限递归
        rowCount = (int) Math.min(config.getRowCount(), Math.ceil((double) (peopleNum - minus) / columnCount));
        randomRowCount = Math.min(config.getRandomBetweenRows(), rowCount);

        // 临时变量，提前声明以减少内存和计算操作
        int t;
        final int seatNum = rowCount * columnCount;
        final int randomPeopleCount = columnCount * randomRowCount;
        final int peopleLeft = peopleNum % randomPeopleCount;

        final int forTimes = peopleLeft > columnCount ? seatNum / randomPeopleCount + 1 : seatNum / randomPeopleCount;

        final String[] emptyRow = new String[columnCount];
        Arrays.fill(emptyRow, SeatTable.EMPTY_SEAT_PLACEHOLDER);

        // 座位表数据
        ArrayList<String> seatTable = new ArrayList<>(seatNum);
        String luckyPerson = null;

        final int tPeopleNum = peopleNum - minus;
        ArrayList<String> tNameList;
        ArrayList<Integer> tLastRowPosChosenList;
        String tGroupLeader;

        do {
            seatTable.clear();
            tNameList = new ArrayList<>(nameList);

            if (lucky) {
                t = rd.nextInt(peopleNum - randomPeopleCount, peopleNum);
                luckyPerson = tNameList.get(t);
                tNameList.remove(t);
            }

            for (int i = 0; i < forTimes; i++) {
                if (i == forTimes - 1) {
                    t = tPeopleNum;
                } else {
                    t = (i + 1) * randomPeopleCount;
                }
                Collections.shuffle(tNameList.subList(i * randomPeopleCount, t), rd);
            }

            t = tPeopleNum > seatNum ? 0 : tPeopleNum % columnCount;
            if (t == 0) {
                seatTable.addAll(tNameList.subList(0, seatNum));
            } else {
                tLastRowPosChosenList = new ArrayList<>(t);
                seatTable.addAll(tNameList.subList(0, seatNum - columnCount));
                seatTable.addAll(Arrays.asList(emptyRow));
                for (int i = seatNum - columnCount; i < tPeopleNum; i++) {
                    do {
                        t = rd.nextInt(seatNum - columnCount, seatNum);
                    } while (notAllowedLastRowPos.contains(t - seatNum + columnCount + 1)
                            || tLastRowPosChosenList.contains(t));
                    seatTable.set(t, tNameList.get(i));
                    tLastRowPosChosenList.add(t);
                }
            }
        } while (!checkSeatTableFormat(seatTable, config));

        for (int i = 0; i < columnCount; i++) {
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, SeatTable.GROUP_LEADER_FORMAT.formatted(tGroupLeader));
        }

        return new SeatTable(seatTable, config, seed, luckyPerson);
    }

    /**
     * Generate a seat table using the specified config and the seed.
     *
     * @param config used to generate the seat table.
     * @param seed   used to generate the seat table.
     * @return an instance of {@code SeatTable}.
     * @throws NullPointerException   if the config is null.
     * @throws IllegalConfigException if the config has an illegal format, or if it
     *                                costs too much time to generate the seat table.
     */
    public static SeatTable generate(final SeatConfig config, final String seed) {
        final Future<SeatTable> future = Executors.newSingleThreadExecutor().submit(() -> generate0(config, seed));

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final TimeoutException e) {
            throw new IllegalConfigException(
                    "Unlucky or invalid config/seed, please check your config or use another seed."
            );
        }
    }

    /**
     * Generates an empty seat table.
     *
     * @param config used to generate the empty seat table
     * @return an empty seat table
     */
    public static SeatTable generateEmpty(final SeatConfig config) {
        List<String> seat = new ArrayList<>(Arrays.asList(new String[config.getRowCount() * config.getColumnCount()]));
        Collections.fill(seat, SeatTable.EMPTY_SEAT_PLACEHOLDER);
        return new SeatTable(seat, config, "", "null");
    }

    private static boolean checkSeatTableFormat(List<String> seatTable, SeatConfig config) throws IllegalConfigException {
        final List<String> gl = config.getGroupLeaderList();
        final List<SeparatedPair> sp = config.getSeparatedList();
        boolean hasLeader = false;
        int i, j;
        final int spNum = sp.size();
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int minus = config.lucky_option ? 1 : 0;
        rowCount = (int) Math.min(
                config.getRowCount(),
                Math.ceil((double) (config.getNameList().size() - minus) / columnCount));

        // 检查每列是否都有组长
        for (i = 0; i < columnCount; i++) {
            for (j = 0; j < rowCount; j++) {
                hasLeader = gl.contains(seatTable.get(j * columnCount + i));
                if (hasLeader) {
                    break;
                }
            }
            if (!hasLeader) {
                return false;
            }
            hasLeader = false;
        }
        // 检查是否分开
        for (i = 0; i < spNum; i++) {
            if (!sp.get(i).check(seatTable, columnCount)) {
                return false;
            }
        }

        return true;
    }

}
