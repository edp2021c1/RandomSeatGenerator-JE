package com.edp2021c1.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class SeatManager {
    /**
     * The instance of {@code Random} used to generate random numbers.
     */
    private static final Random random = new Random();
    /**
     * Saves the config of an instance.
     *
     * @deprecated Use config instead.
     */
    @Deprecated
    public static SeatConfig_Old conf;
    /**
     * Saves the config of an instance.
     */
    public static SeatConfig config;
    /**
     * Buffers the seat table.
     */
    private static List<String> seat;

    /**
     * Generate a seat table using the pre-set config and the seed.
     *
     * @return an instance of {@code Seat_Old}.
     */
    public static Seat generate(long seed) throws Exception {
        random.setSeed(seed);
        if (config == null) {
            throw new NullPointerException("Config cannot be null.");
        }

        // 获取配置
        int rowCount = config.getRowCount();
        int columnCount = config.getColumnCount();
        int randomBetweenRows = config.getRandomBetweenRows();
        List<Integer> lastRowPos = config.getLastRowPos();
        List<String> nameList = config.getNameList();
        List<String> groupLeaderList = config.getGroupLeaderList();

        // 临时变量，提前声明以减少内存和计算操作
        int tmp_1;
        int tmp_2;
        int tmp_3;
        int tmp_4;
        int tmp_5;
        int tmp_6;
        int peopleNum = nameList.size();
        int seatNum=rowCount*columnCount;
        int rp = columnCount * randomBetweenRows;
        int peopleLeft = peopleNum % (rp);
        boolean tmp_7 = peopleLeft > 0 && seatNum > peopleNum;
        boolean tmp_8 = peopleLeft <= columnCount;
        List<Integer> tmp;

        // 座位表变量
        seat = Arrays.asList(new String[seatNum]);

        List<Boolean> sorted = Arrays.asList(new Boolean[nameList.size()]);

        do {
            // 座位表初始化
            // 只有最后一排会出现空位，因此只填入最后一排
            for (tmp_1 = (rowCount - 1) * columnCount, tmp_3 = seatNum; tmp_1 < tmp_3; tmp_1++) {
                seat.set(tmp_1, "-");
            }
            for (tmp_1 = 0, tmp_3 = nameList.size(); tmp_1 < tmp_3; tmp_1++) {
                sorted.set(tmp_1, false);
            }
            tmp = new ArrayList<>(peopleNum % columnCount);

            for (tmp_1 = 0, tmp_3 = peopleNum / (rp); tmp_1 < tmp_3; tmp_1++) {
                if (tmp_1 == tmp_3 - 1 && tmp_7 && tmp_8) {    // 如果余位不多于一排，则将最后一排归到前两排中轮换
                    for (tmp_2 = tmp_1 * rp, tmp_6 = (tmp_1 + 1) * rp; tmp_2 < tmp_6; tmp_2++) {
                        do {
                            tmp_5 = random.nextInt(tmp_1 * rp, peopleNum);
                        } while (sorted.get(tmp_5));
                        seat.set(tmp_2, nameList.get(tmp_5));
                        sorted.set(tmp_5, true);
                    }
                    for (tmp_2 = 0; tmp_2 < peopleLeft; tmp_2++) {
                        do {
                            tmp_5 = random.nextInt(tmp_1 * rp, peopleNum);
                        } while (sorted.get(tmp_5));
                        do {
                            tmp_6 = random.nextInt(seatNum-columnCount, seatNum);
                        } while (!lastRowPos.contains(tmp_6 - tmp_3 * rp + 1) || tmp.contains(tmp_6));
                        tmp.add(tmp_6);
                        seat.set(tmp_6, nameList.get(tmp_5));
                        sorted.set(tmp_5, true);
                    }
                    break;
                } else if (tmp_1 == tmp_3 - 1 && tmp_7 && !tmp_8) {   // 如果余位多于一排，则在余位中进行随机轮换
                    for (tmp_2 = 0, tmp_4 = peopleLeft - peopleNum % columnCount; tmp_2 < tmp_4; tmp_2++) {
                        do {
                            tmp_5 = random.nextInt(tmp_1 * rp, peopleNum);
                        } while (sorted.get(tmp_5));
                        seat.set(tmp_2 + tmp_4, nameList.get(tmp_5));
                        sorted.set(tmp_5, true);
                    }
                    for (tmp_2 = 0; tmp_2 < peopleLeft; tmp_2++) {
                        do {
                            tmp_5 = random.nextInt(tmp_1 * rp, peopleNum);
                        } while (sorted.get(tmp_5));
                        do {
                            tmp_6 = random.nextInt(seatNum-columnCount, seatNum);
                        } while (!lastRowPos.contains(tmp_6 - tmp_3 * rp + 1) || tmp.contains(tmp_6));
                        tmp.add(tmp_6);
                        seat.set(tmp_6, nameList.get(tmp_5));
                        sorted.set(tmp_5, true);
                    }
                    break;
                }
                for (tmp_2 = tmp_1 * rp, tmp_6 = (tmp_1 + 1) * rp; tmp_2 < tmp_6; tmp_2++) {
                    do {
                        tmp_5 = random.nextInt(tmp_1 * rp, (tmp_1 + 1) * rp);
                    } while (sorted.get(tmp_5));
                    if(!(tmp_2 <seatNum)) break;
                    seat.set(tmp_2, nameList.get(tmp_5));
                    sorted.set(tmp_5, true);
                }

            }

        } while (!check());

        //组长
        for (tmp_1 = 0; tmp_1 < columnCount; tmp_1++) {
            do {
                tmp_5 = random.nextInt(0, rowCount);
            } while (!groupLeaderList.contains(seat.get(tmp_5 * columnCount + tmp_1)));
            seat.set(tmp_5 * columnCount + tmp_1, "*" + seat.get(tmp_5 * columnCount + tmp_1) + "*");
        }

        return new Seat(seat, config, seed);
    }

    /**
     * Check if the seat table fits the config.
     *
     * @return {@code true} if the seat table fits the config and {@code false} if not.
     */
    private static boolean check() throws Exception {
        boolean hasLeader = false;
        boolean isSeparated = true;
        int i, j, len;
        List<String> gl = config.getGroupLeaderList();
        List<Separate> sp = config.getSeparatedList();
        // 检查每列是否都有组长
        for (i = 0; i < config.getColumnCount(); i++) {
            for (j = 0; j < config.getRowCount(); j++) {
                hasLeader = gl.contains(seat.get(j * config.getColumnCount() + i));
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
        for (i = 0, len = sp.size(); i < len; i++) {
            if (isSeparated) {
                isSeparated = sp.get(i).check(seat);
                continue;
            }
            return false;
        }

        return true;
    }

}
