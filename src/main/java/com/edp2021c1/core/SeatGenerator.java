package com.edp2021c1.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * An instance of this class generates a seat table in form of an {@link ArrayList}.
 * <p>
 * If same config and seed are set to two instances,
 * they will generate same seat tables.
 * <p>
 * If the seed of an instance is not set, then it will default to 0.
 *
 * @author Calboot
 * @since 1.0
 * @deprecated Now use {@link SeatManager} instead.
 */
@Deprecated
public final class SeatGenerator {
    /**
     * The instance of {@code Random} used to generate random numbers.
     */
    private final Random random;
    /**
     * Saves the config of an instance.
     */
    private SeatConfig_Old config;
    /**
     * Buffers the seat table.
     */
    private ArrayList<String> seat;
    /**
     * The seed used to generate seat table, default to 0.
     */
    private long seed;

    /**
     * Initialization.
     */
    public SeatGenerator() {
        random = new Random();
        seed = 0;
    }

    /**
     * Initialize the config of this instance.
     *
     * @param config the config of the instance.
     */
    public void setConfig(SeatConfig_Old config) {
        this.config = config;
    }

    /**
     * Set the default seed of this instance to a new seed.
     *
     * @param seed the seed of the instance.
     */
    public void setSeed(long seed) {
        random.setSeed(this.seed = seed);
    }

    /**
     * Generate a seat table with the config and the seed.
     *
     * @return an instance of {@code Seat_Old}.
     */
    public Seat_Old next() {
        if (config == null) {
            throw new NullPointerException("The config cannot be null.");
        }
        seat = new ArrayList<>(Arrays.asList(new String[49]));
        ArrayList<Boolean> sorted = new ArrayList<>(Arrays.asList(new Boolean[44]));
        int t, i, m, n;
        ArrayList<String> fr = config.frontRows;
        ArrayList<String> mr = config.middleRows;
        ArrayList<String> br = config.backRows;
        ArrayList<String> gl = config.groupLeaders;
        do {
            //初始化
            for (i = 42; i < 49; i++) {
                seat.set(i, "-");  //只有第七排会有空位，所以只填入第七排
            }
            for (i = 0; i < 44; i++) {
                sorted.set(i, false);
            }
            // 第一、二排
            for (i = 0; i < 14; i++) {
                do {
                    t = random.nextInt(0, 14);
                } while (sorted.get(t));
                seat.set(i, fr.get(t));
                sorted.set(t, true);
            }

            // 第三、四排
            for (i = 14; i < 28; i++) {
                do {
                    t = random.nextInt(14, 28);
                } while (sorted.get(t));
                seat.set(i, mr.get(t - 14));
                sorted.set(t, true);
            }

            // 第五、六排
            for (i = 28; i < 42; i++) {
                do {
                    t = random.nextInt(28, 44);
                } while (sorted.get(t));
                seat.set(i, br.get(t - 28));
                sorted.set(t, true);
            }

            // 第七排
            do {
                t = random.nextInt(28, 44);
            } while (sorted.get(t));
            m = random.nextInt(42, 49);
            seat.set(m, br.get(t - 28));
            sorted.set(t, true);
            do {
                t = random.nextInt(28, 44);
                n = random.nextInt(42, 49);
            } while (sorted.get(t) || n == m);
            seat.set(n, br.get(t - 28));

        } while (!check());

        //组长
        for (i = 0; i < 7; i++) {
            do {
                t = random.nextInt(0, 7);
            } while (!gl.contains(seat.get(t * 7 + i)));
            seat.set(t * 7 + i, "*" + seat.get(t * 7 + i) + "*");
        }

        return new Seat_Old(seat, seed);
    }

    /**
     * Check if the seat table fits the config.
     *
     * @return {@code true} if the seat table fits the config and {@code false} if not.
     */
    private boolean check() {
        boolean hasLeader = false;
        boolean isSeparated = true;
        int i, j, len;
        ArrayList<String> gl = config.groupLeaders;
        ArrayList<Separate> sp = config.separated;
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
