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

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages the generation of seat tables.
 *
 * @author Calboot
 * @since 1.2.0
 */
public final class SeatGenerator {

    /**
     * Generates a seat table.
     *
     * @param config used to generate the seat table.
     * @param seed   used to generate the seat table.
     * @return an instance of {@code SeatTable}.
     * @throws NullPointerException   if the config is null.
     * @throws IllegalConfigException if the config has an illegal format.
     */
    private SeatTable generateTask(final SeatConfig config, final long seed) throws NullPointerException, IllegalConfigException {
        if (config == null) {
            throw new NullPointerException("Config cannot be null");
        }
        config.checkFormat();

        Random rd = new Random(seed);

        // 获取配置
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int randomRowCount;
        final List<Integer> notAllowedLastRowPos = config.getNotAllowedLastRowPos();
        final List<String> nameList = config.getNameList();
        final List<String> groupLeaderList = config.getGroupLeaderList();
        final boolean lucky = config.lucky_option;

        // 防止lucky为true时数组越界
        final int minus;
        if (lucky) {
            minus = 1;
        } else {
            minus = 0;
        }

        // 临时变量，提前声明以减少内存和计算操作
        final int peopleNum = nameList.size();

        // 防止行数过多引发无限递归
        rowCount = (int) Math.min(config.getRowCount(), Math.ceil((double) (peopleNum - minus) / columnCount));
        randomRowCount = Math.min(config.getRandomBetweenRows(), rowCount);

        // 座位表数据
        List<String> seatTable;
        String luckyPerson = null;

        // 临时变量，提前声明以减少内存和计算操作
        int t, u;
        final int seatNum = rowCount * columnCount;
        final int randomPeopleCount = columnCount * randomRowCount;
        final int peopleLeft = peopleNum % randomPeopleCount;

        final int forTimes;
        if (peopleLeft > columnCount) {
            forTimes = seatNum / randomPeopleCount + 1;
        } else {
            forTimes = seatNum / randomPeopleCount;
        }

        final String[] emptyRow = new String[columnCount];
        Arrays.fill(emptyRow, "-");

        List<String> tNameList;
        List<String> tResult;
        List<String> tSubNameList;
        int tPeopleNum;
        String tGroupLeader;

        do {
            seatTable = new ArrayList<>(seatNum);
            tNameList = new ArrayList<>(nameList);
            tPeopleNum = peopleNum;

            if (lucky) {
                t = rd.nextInt(tPeopleNum - randomPeopleCount, tPeopleNum);
                luckyPerson = tNameList.get(t);
                tNameList.remove(t);
                tPeopleNum--;
            }

            tResult = new ArrayList<>(tPeopleNum);
            for (int i = 0; i < forTimes; i++) {
                if (i == forTimes - 1) {
                    tSubNameList = tNameList.subList(i * randomPeopleCount, peopleNum - 1);
                } else {
                    tSubNameList = tNameList.subList(i * randomPeopleCount, (i + 1) * randomPeopleCount);
                }
                Collections.shuffle(tSubNameList);
                tResult.addAll(tSubNameList);
            }

            t = tPeopleNum > seatNum ? 0 : tPeopleNum % columnCount;
            if (t == 0) {
                seatTable.addAll(tResult.subList(0, seatNum));
            } else {
                seatTable.addAll(tResult.subList(0, seatNum - columnCount));
                seatTable.addAll(Arrays.asList(emptyRow));
                for (int i = seatNum - columnCount; i < tPeopleNum; i++) {
                    do {
                        u = rd.nextInt(seatNum - columnCount, seatNum);
                    } while (notAllowedLastRowPos.contains(u - seatNum + columnCount + 1));
                    seatTable.set(u, tResult.get(i));
                }
            }
        } while (!checkSeatTableFormat(seatTable, config));

        for (int i = 0; i < columnCount; i++) {
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, String.format("*%s*", tGroupLeader));
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
     * @throws IllegalConfigException if the config has an illegal format, or if it costs too much time to generate the seat table.
     */
    public SeatTable generate(final SeatConfig config, final long seed) {
        final Future<SeatTable> future = Executors.newSingleThreadExecutor().submit(() -> generateTask(config, seed));

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new IllegalConfigException("Unlucky or invalid config/seed, please check your config or use another seed.");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkSeatTableFormat(List<String> seatTable, SeatConfig config) throws IllegalConfigException {
        List<String> gl = config.getGroupLeaderList();
        List<Separate> sp = config.getSeparatedList();
        boolean hasLeader = false;
        boolean isSeparated;
        int i, j;
        int spNum = sp.size();
        int seatNum = seatTable.size();
        int columnCount = config.getColumnCount();
        int rowCount = (int) Math.ceil((double) seatNum / columnCount);

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
            isSeparated = sp.get(i).check(seatTable, columnCount);
            if (isSeparated) {
                continue;
            }
            return false;
        }

        return true;
    }

}
