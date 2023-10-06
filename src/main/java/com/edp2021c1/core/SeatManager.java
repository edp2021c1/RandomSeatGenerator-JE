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
    public static Seat generate(long seed) {
        random.setSeed(seed);
        if (config == null) {
            throw new NullPointerException("Config cannot be null.");
        }

        // 获取配置
        int r = config.getRowCount();
        int c = config.getColumnCount();
        int rdr = config.getRandomBetweenRows();
        List<Integer> last = config.getLastRowPosCanBeChoosed();
        List<String> nameList = config.getNameList();
        List<String> gl = config.getGroupLeaderList();

        // 座位表变量
        seat = Arrays.asList(new String[r * c]);

        List<Boolean> sorted = Arrays.asList(new Boolean[nameList.size()]);

        // 临时变量，提前声明以减少内存操作
        int i, j, x, y, t, m, peopleNum = nameList.size(), rp = c * rdr, peopleLeft = peopleNum % (rp);
        boolean b = peopleLeft <= c && peopleLeft != 0;
        List<Integer> tmp;

        do {
            // 座位表初始化
            // 只有最后一排会出现空位，因此只填入最后一排
            for (i = (r - 1) * c, x = r * c; i < x; i++) {
                seat.set(i, "-");
            }
            for (i = 0, x = nameList.size(); i < x; i++) {
                sorted.set(i, false);
            }
            tmp = new ArrayList<>(peopleNum % c);

            for (i = 0, x = peopleNum / (rp); i < x; i++) {
                if (i == x - 1 && b) {    // 如果余位不多于一排，则将最后一排归到前两排中轮换
                    for (j = i * rp, m = (i + 1) * rp; j < m; j++) {
                        do {
                            t = random.nextInt(i * rp, peopleNum);
                        } while (sorted.get(t));
                        seat.set(j, nameList.get(t));
                        sorted.set(t, true);
                    }
                    for (j = 0; j < peopleLeft; j++) {
                        do {
                            t = random.nextInt(i * rp, peopleNum);
                        } while (sorted.get(t));
                        do {
                            m = random.nextInt((r - 1) * c, r * c);
                        } while (!last.contains(m - x * rp + 1) || tmp.contains(m));
                        tmp.add(m);
                        seat.set(m, nameList.get(t));
                        sorted.set(t, true);
                    }
                    break;
                } else if (i == x - 1 && !b) {   // 如果余位多于一排，则在余位中进行随机轮换
                    for (j = 0, y = peopleLeft - peopleNum % c; j < y; j++) {
                        do {
                            t = random.nextInt(i * rp, peopleNum);
                        } while (sorted.get(t));
                        seat.set(j + y, nameList.get(t));
                        sorted.set(t, true);
                    }
                    for (j = 0; j < peopleLeft; j++) {
                        do {
                            t = random.nextInt(i * rp, peopleNum);
                        } while (sorted.get(t));
                        do {
                            m = random.nextInt((r - 1) * c, r * c);
                        } while (!last.contains(m - x * rp + 1) || tmp.contains(m));
                        tmp.add(m);
                        seat.set(m, nameList.get(t));
                        sorted.set(t, true);
                    }
                    break;
                }
                for (j = i * rp, m = (i + 1) * rp; j < m; j++) {
                    do {
                        t = random.nextInt(i * rp, (i + 1) * rp);
                    } while (sorted.get(t));
                    seat.set(j, nameList.get(t));
                    sorted.set(t, true);
                }

            }

        } while (!check());

        //组长
        for (i = 0; i < c; i++) {
            do {
                t = random.nextInt(0, 7);
            } while (!gl.contains(seat.get(t * c + i)));
            seat.set(t * c + i, "*" + seat.get(t * c + i) + "*");
        }

        return new Seat(seat, config, seed);
    }

    /**
     * Check if the seat table fits the config.
     *
     * @return {@code true} if the seat table fits the config and {@code false} if not.
     */
    private static boolean check() {
        boolean hasLeader = false;
        boolean isSeparated = true;
        int i, j, len;
        List<String> gl = config.getGroupLeaderList();
        List<Separate> sp = config.getSeparatedList();
        // 检查每列是否都有组长
        for (i = 0; i < 7; i++) {
            for (j = 0; j < 7; j++) {
                hasLeader = gl.contains(seat.get(j * 7 + i));
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
