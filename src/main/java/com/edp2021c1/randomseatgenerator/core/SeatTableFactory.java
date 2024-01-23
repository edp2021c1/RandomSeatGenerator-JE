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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.*;

/**
 * Manages the generation of seat tables.
 *
 * @author Calboot
 * @since 1.2.0
 */
public class SeatTableFactory {

    /**
     * Don't let anyone else instantiate this class.
     */
    private SeatTableFactory() {
    }

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
            throw new IllegalConfigException("Config cannot be null");
        }

        long longSeed;
        try {
            longSeed = Long.parseLong(seed);
            seed += " (integer)";
        } catch (RuntimeException e) {
            longSeed = Strings.longHashOf(seed);
            seed += " (string)";
        }

        final Random rd = new Random(longSeed);

        // 获取配置
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int randomRowCount;
        final List<Integer> notAllowedLastRowPos = modifiableListOf(config.getDisabledLastRowPos());
        final List<String> nameList = modifiableListOf(config.getNames());
        final List<String> groupLeaderList = modifiableListOf(config.getGroupLeaders());
        final boolean lucky = config.isLucky();

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

        final int forTimes = (peopleLeft > columnCount ? seatNum / randomPeopleCount + 1 : seatNum / randomPeopleCount) - 1;

        final List<String> emptyRow = modifiableListOf(new String[columnCount]);
        Collections.fill(emptyRow, SeatTable.emptySeatPlaceholder);

        final List<Integer> availableLastRowPos = range(1, columnCount + 1);
        availableLastRowPos.removeAll(notAllowedLastRowPos);

        // 座位表数据
        final List<String> seatTable = new ArrayList<>(seatNum);
        String luckyPerson = null;

        final int tPeopleNum = peopleNum - minus;
        List<String> tNameList;
        List<Integer> tLastRowPosChosenList;
        String tGroupLeader;

        do {
            seatTable.clear();
            tNameList = modifiableListOf(nameList);

            if (lucky) {
                luckyPerson = pickRandomlyAndRemove(tNameList.subList(peopleNum - randomPeopleCount, peopleNum), rd);
            }

            for (int i = 0; i < forTimes; i++) {
                Collections.shuffle(tNameList.subList(i * randomPeopleCount, (i + 1) * randomPeopleCount), rd);
            }
            Collections.shuffle(tNameList.subList(forTimes * randomPeopleCount, tPeopleNum), rd);

            t = tPeopleNum > seatNum ? 0 : tPeopleNum % columnCount;
            if (t == 0) {
                seatTable.addAll(tNameList.subList(0, seatNum));
            } else {
                tLastRowPosChosenList = new ArrayList<>(t);
                seatTable.addAll(tNameList.subList(0, seatNum - columnCount));
                seatTable.addAll(emptyRow);
                for (int i = seatNum - columnCount; i < tPeopleNum; i++) {
                    do {
                        t = pickRandomly(availableLastRowPos, rd);
                    } while (tLastRowPosChosenList.contains(t));
                    seatTable.set(t + seatNum - columnCount - 1, tNameList.get(i));
                    tLastRowPosChosenList.add(t);
                }
            }
        } while (!checkSeatTableFormat(seatTable, config));

        for (int i = 0; i < columnCount; i++) {
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, SeatTable.groupLeaderFormat.formatted(tGroupLeader));
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
        final ExecutorService exe = Executors.newSingleThreadExecutor(r -> new Thread(r, "Seat Table Factory Thread"));
        final Future<SeatTable> future = exe.submit(() -> generate0(config, seed));
        exe.close();

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            final Throwable ex = e.getCause();
            if (ex instanceof IllegalConfigException) {
                throw (IllegalConfigException) ex;
            }
            throw (RuntimeException) ex;
        } catch (final TimeoutException e) {
            throw new IllegalConfigException(
                    "Seat table generating timeout, please check your config or use another seed"
            );
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates an empty seat table.
     *
     * @param config used to generate the empty seat table
     * @return an empty seat table
     */
    public static SeatTable generateEmpty(final SeatConfig config) {
        List<String> seat = modifiableListOf(new String[config.getRowCount() * config.getColumnCount()]);
        Collections.fill(seat, SeatTable.emptySeatPlaceholder);
        return new SeatTable(seat, config, "", "null");
    }

    private static boolean checkSeatTableFormat(List<String> seatTable, SeatConfig config) throws IllegalConfigException {
        final List<String> gl = modifiableListOf(config.getGroupLeaders());
        final List<SeparatedPair> sp = modifiableListOf(config.getSeparatedPairs());
        boolean hasLeader = false;
        int i, j;
        final int spNum = sp.size();
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int minus = config.isLucky() ? 1 : 0;
        rowCount = (int) Math.min(
                config.getRowCount(),
                Math.ceil((double) (config.getNames().size() - minus) / columnCount));

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
