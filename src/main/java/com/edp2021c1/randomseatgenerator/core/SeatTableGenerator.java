package com.edp2021c1.randomseatgenerator.core;

import com.edp2021c1.randomseatgenerator.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.edp2021c1.randomseatgenerator.core.SeatTable.EMPTY_SEAT_PLACEHOLDER;
import static com.edp2021c1.randomseatgenerator.core.SeatTable.groupLeaderFormat;
import static com.edp2021c1.randomseatgenerator.util.CollectionUtils.*;
import static java.util.Collections.*;

/**
 * Seat table generator class.
 *
 * @author Calboot
 * @since 1.5.0
 */
@FunctionalInterface
public interface SeatTableGenerator {

    /**
     * Returns the default implementation of this class.
     *
     * @return the default implementation
     */
    static SeatTableGenerator getDefault() {
        return SeatTableGeneratorImpl.instance;
    }


    /**
     * Generates a seat table.
     *
     * @param config used to generate the seat table
     * @param seed   used to generate the seat table
     * @return an instance of {@code SeatTable}
     * @throws NullPointerException   if the config is null
     * @throws IllegalConfigException if the config has an illegal format
     */
    SeatTable generate(SeatConfig config, String seed)
            throws NullPointerException, IllegalConfigException;

    class SeatTableGeneratorImpl implements SeatTableGenerator {

        private static final SeatTableGenerator instance = new SeatTableGeneratorImpl();

        /**
         * Don't let anyone else instantiate this class.
         */
        private SeatTableGeneratorImpl() {
        }

        @Override
        public SeatTable generate(final SeatConfig config, String seed) throws NullPointerException, IllegalConfigException {
            if (config == null) {
                throw new IllegalConfigException("Config cannot be null");
            }

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
            final List<String> nameList = unmodifiableList(config.getNames());
            final List<String> groupLeaderList = unmodifiableList(config.getGroupLeaders());
            final boolean lucky = config.isLucky();

            // 防止lucky为true时数组越界
            final int minus = lucky ? 1 : 0;

            // 临时变量，提前声明以减少内存和计算操作
            final int peopleNum = nameList.size();

            // 防止行数过多引发无限递归
            rowCount = (int) Math.min(config.getRowCount(), Math.ceil((double) (peopleNum - minus) / columnCount));
            randomBetweenRows = Math.min(config.getRandomBetweenRows(), rowCount);

            // 临时变量，提前声明以减少内存和计算操作
            final int seatNum = rowCount * columnCount;
            final int randomPeopleCount = columnCount * randomBetweenRows;
            final int tPeopleNum = peopleNum - minus;
            final int peopleLeft = tPeopleNum > seatNum ? 0 : tPeopleNum % columnCount;

            final int forTimesMinusOne = (
                    peopleNum % randomPeopleCount > columnCount
                            ? seatNum / randomPeopleCount + 1
                            : seatNum / randomPeopleCount
            ) - 1;

            final List<String> emptyRow = Arrays.asList(new String[columnCount]);
            fill(emptyRow, EMPTY_SEAT_PLACEHOLDER);

            final List<Integer> availableLastRowPos;
            {
                final List<Integer> tmp = range(1, columnCount);
                tmp.removeAll(config.getDisabledLastRowPos());
                if (tmp.size() < peopleLeft) {
                    throw new IllegalConfigException("Available last row seat not enough");
                }
                availableLastRowPos = unmodifiableList(tmp);
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
                            peopleNum - randomPeopleCount - peopleLeft, peopleNum
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
                        Integer t = pickRandomly(tAvailableLastRowPos, rd);
                        seatTable.set(t + seatNum - columnCount - 1, tNameList.get(i));
                        tAvailableLastRowPos.remove(t);
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


        private boolean check(final List<String> seatTable, final SeatConfig config) throws IllegalConfigException {
            final List<String> gl = unmodifiableList(config.getGroupLeaders());
            final List<SeparatedPair> sp = unmodifiableList(config.getSeparatedPairs());
            boolean hasLeader = false;
            final int spNum = sp.size();
            final int rowCount;
            final int columnCount = config.getColumnCount();
            final int minus = config.isLucky() ? 1 : 0;
            rowCount = (int) Math.min(
                    config.getRowCount(),
                    Math.ceil((double) (config.getNames().size() - minus) / columnCount)
            );

            // 检查每列是否都有组长
            for (int i = 0; i < columnCount; i++) {
                for (int j = 0; j < rowCount; j++) {
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
            for (int i = 0; i < spNum; i++) {
                if (!sp.get(i).check(seatTable, columnCount)) {
                    return false;
                }
            }

            return true;
        }
    }

}
