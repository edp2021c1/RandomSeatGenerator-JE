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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Manages the generation of seat tables.
 *
 * @author Calboot
 * @since 1.2.0
 */
public final class SeatGenerator {
    private static int ceil(double d) {
        return (int) Math.ceil(d);
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
    private SeatTable generateTask(SeatConfig config, long seed) throws NullPointerException, IllegalConfigException {
        if (config == null) {
            throw new NullPointerException("Config cannot be null");
        }
        config.checkFormat();

        Random random = new Random(seed);

        // 获取配置
        int rowCount = config.getRowCount();
        int columnCount = config.getColumnCount();
        int randomRowCount = config.getRandomBetweenRows();
        List<Integer> notAllowedLastRowPos = config.getNotAllowedLastRowPos();
        List<String> nameList = config.getNameList();
        List<String> groupLeaderList = config.getGroupLeaderList();

        // 临时变量，提前声明以减少内存和计算操作
        int peopleNum = nameList.size();

        // 防止行数过多引发无限递归
        while (rowCount * columnCount - peopleNum > columnCount) {
            rowCount--;
        }

        // 临时变量，提前声明以减少内存和计算操作
        int seatNum = rowCount * columnCount;
        int randomPeopleCount = columnCount * randomRowCount;
        int peopleLeft = peopleNum % randomPeopleCount;
        boolean luckyOption = config.lucky_option;
        int i;
        int j;
        int c;
        int d;
        int e;
        int f;
        boolean hasSeatLeft = peopleLeft > 0 && seatNum > peopleNum;
        boolean tmp_8 = peopleLeft <= columnCount;
        List<Integer> tmp;

        // 座位表变量
        List<String> seat = Arrays.asList(new String[seatNum]);
        List<Boolean> sorted = Arrays.asList(new Boolean[nameList.size()]);
        String luckyPerson = null;

        int forTimes = seatNum / (columnCount * Math.min(rowCount, randomRowCount));
        if (!hasSeatLeft) {
            forTimes = ceil((double) seatNum / (columnCount * Math.min(rowCount, randomRowCount)));
        }

        do {
            // 座位表初始化
            // 只有最后一排会出现空位，因此只填入最后一排
            for (i = (rowCount - 1) * columnCount, c = seatNum; i < c; i++) {
                seat.set(i, "-");
            }
            for (i = 0, c = nameList.size(); i < c; i++) {
                sorted.set(i, false);
            }
            tmp = new ArrayList<>(peopleNum % columnCount);

            for (i = 0; i < forTimes; i++) {
                if (i == forTimes - 1 && hasSeatLeft && tmp_8) {    // 如果余位不多于一排，则将最后一排归到前两排中轮换
                    for (j = i * randomPeopleCount, f = (i + 1) * randomPeopleCount; j < f; j++) {
                        do {
                            e = random.nextInt(i * randomPeopleCount, peopleNum);
                        } while (sorted.get(e));
                        seat.set(j, nameList.get(e));
                        sorted.set(e, true);
                    }
                    for (j = 0; j < peopleLeft; j++) {
                        do {
                            e = random.nextInt(i * randomPeopleCount, peopleNum);
                        } while (sorted.get(e));
                        do {
                            f = random.nextInt(seatNum - columnCount, seatNum);
                        } while (notAllowedLastRowPos.contains(f - forTimes * randomPeopleCount + 1) || tmp.contains(f));
                        tmp.add(f);
                        seat.set(f, nameList.get(e));
                        sorted.set(e, true);
                    }
                    break;
                } else if (i == forTimes - 1 && hasSeatLeft && !tmp_8) {   // 如果余位多于一排，则在余位中进行随机轮换
                    for (j = i * randomPeopleCount, d = seatNum - columnCount; j < d; j++) {
                        do {
                            e = random.nextInt(i * randomPeopleCount, peopleNum);
                        } while (sorted.get(e));
                        seat.set(j, nameList.get(e));
                        sorted.set(e, true);
                    }
                    for (j = 0, d = peopleLeft % columnCount; j < d; j++) {
                        do {
                            e = random.nextInt(i * randomPeopleCount, peopleNum);
                        } while (sorted.get(e));
                        do {
                            f = random.nextInt(seatNum - columnCount, seatNum);
                        } while (notAllowedLastRowPos.contains(f - seatNum + columnCount + 1) || tmp.contains(f));
                        tmp.add(f);
                        seat.set(f, nameList.get(e));
                        sorted.set(e, true);
                    }
                    break;
                }
                for (j = i * randomPeopleCount, f = (i + 1) * randomPeopleCount; j < f; j++) {
                    if (j >= seatNum) {
                        break;
                    }

                    do {
                        e = random.nextInt(i * randomPeopleCount, (i + 1) * randomPeopleCount);
                    } while (sorted.get(e));
                    seat.set(j, nameList.get(e));
                    sorted.set(e, true);
                }

            }

            if (luckyOption && seatNum >= peopleNum) {
                i = seatNum;
                while (i > 0) {
                    i--;
                    if (!"-".equals(seat.get(i))) {
                        luckyPerson = seat.set(i, "-");
                        break;
                    }
                }
            } else if (luckyOption) {
                for (i = 0; i < peopleNum; i++) {
                    if (!seat.contains(nameList.get(i))) {
                        luckyPerson = nameList.get(i);
                        break;
                    }
                }
            }

        } while (!checkSeatFormat(seat, config));

        //组长
        for (i = 0; i < columnCount; i++) {
            do {
                e = random.nextInt(0, rowCount);
            } while (!groupLeaderList.contains(seat.get(e * columnCount + i)));
            seat.set(e * columnCount + i, "*" + seat.get(e * columnCount + i) + "*");
        }

        return new SeatTable(seat, config, seed, luckyPerson);
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
    public SeatTable generate(SeatConfig config, long seed) {
        Callable<SeatTable> task = () -> generateTask(config, seed);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<SeatTable> future = service.submit(task);

        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new IllegalConfigException("Unlucky or invalid config/seed, please check your config or use another seed.");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkSeatFormat(List<String> seatTable, SeatConfig config) throws IllegalConfigException {
        List<String> gl = config.getGroupLeaderList();
        List<Separate> sp = config.getSeparatedList();
        boolean hasLeader = false;
        boolean isSeparated;
        int i, j;
        int spNum = sp.size();
        int seatNum = seatTable.size();
        int columnCount = config.getColumnCount();
        int rowCount = ceil((double) seatNum / columnCount);

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
