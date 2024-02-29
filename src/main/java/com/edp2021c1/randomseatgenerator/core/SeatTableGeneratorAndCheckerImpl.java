package com.edp2021c1.randomseatgenerator.core;

import com.edp2021c1.randomseatgenerator.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.groupLeaderFormat;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.pickRandomlyAndRemove;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.range;
import static java.util.Collections.fill;
import static java.util.Collections.shuffle;

/**
 * Default generator implementation class.
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
                longSeed = Strings.longHashOf(seed);
                seed += " (string)";
            }
        }

        final Random rd = new Random(longSeed);

        // 获取配置
        final int rowCount;
        final int columnCount = config.getColumnCount();
        final int randomBetweenRows;
        final List<String> nameList = config.getNames();
        final List<String> groupLeaderList = config.getGroupLeaders();
        final boolean lucky = config.isLucky();

        // 防止lucky为true时数组越界
        final int minus = lucky ? 1 : 0;

        // 临时变量，提前声明以减少内存和计算操作
        final int peopleNum = nameList.size();

        // 防止行数过多引发无限递归
        rowCount = Math.min(config.getRowCount(), (int) Math.ceil((double) (peopleNum - minus) / columnCount));
        randomBetweenRows = Math.min(config.getRandomBetweenRows(), rowCount);

        // 临时变量，提前声明以减少内存和计算操作
        final int seatNum = rowCount * columnCount;

        if (seatNum < peopleNum) {
            throw new IllegalConfigException("Too many people and not enough seat");
        }

        final int tPeopleNum = peopleNum - minus;
        final int peopleLeft = tPeopleNum % columnCount;
        final int randomPeopleCount = Math.min(columnCount * randomBetweenRows, tPeopleNum - peopleLeft);

        final int forTimesMinusOne = (
                peopleNum % randomPeopleCount > columnCount
                        ? seatNum / randomPeopleCount + 1
                        : seatNum / randomPeopleCount
        ) - 1;

        final List<String> emptyRow = Arrays.asList(new String[columnCount]);
        fill(emptyRow, EMPTY_SEAT_PLACEHOLDER);

        final List<Integer> availableLastRowPos = range(1, columnCount);
        availableLastRowPos.removeAll(config.getDisabledLastRowPos());
        if (availableLastRowPos.size() < peopleLeft) {
            throw new IllegalConfigException("Available last row seat not enough");
        }

        final List<String> tNameList = new ArrayList<>(tPeopleNum);
        final List<Integer> tAvailableLastRowPos = new ArrayList<>(availableLastRowPos.size());

        String tGroupLeader;

        // 座位表数据
        final List<String> seatTable = new ArrayList<>(seatNum);
        String luckyPerson = null;

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

            for (int i = 0; i < forTimesMinusOne; i++) {
                shuffle(tNameList.subList(i * randomPeopleCount, (i + 1) * randomPeopleCount), rd);
            }
            shuffle(tNameList.subList(forTimesMinusOne * randomPeopleCount, tPeopleNum), rd);

            if (peopleLeft == 0) {
                seatTable.addAll(tNameList.subList(0, seatNum));
            } else {
                seatTable.addAll(tNameList.subList(0, seatNum - columnCount));
                seatTable.addAll(emptyRow);
                for (int i = seatNum - columnCount; i < tPeopleNum; i++) {
                    seatTable.set(pickRandomlyAndRemove(tAvailableLastRowPos, rd) + seatNum - columnCount - 1, tNameList.get(i));
                }
            }
        } while (!check(seatTable, config));

        for (int i = 0; i < columnCount; i++) {
            int t;
            do {
                t = rd.nextInt(rowCount) * columnCount + i;
            } while (!groupLeaderList.contains((tGroupLeader = seatTable.get(t))));
            seatTable.set(t, groupLeaderFormat.formatted(tGroupLeader));
        }

        return new SeatTable(seatTable, config, seed, luckyPerson);
    }

}