package com.edp2021c1.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SeatGenerator {
    private final SeatConfig conf;
    private ArrayList<String> seat;

    public SeatGenerator(SeatConfig c) {
        this.conf = c;
    }

    public Seat next(long seed) {
        System.out.println("生成配置：");
        System.out.println("种子：" + seed);
        System.out.print("前两排名单：");
        for (int i = 0; i < this.conf.frontRows.size(); i++) {
            System.out.print(" " + this.conf.frontRows.get(i));
        }
        System.out.println();
        System.out.print("中两排名单：");
        for (int i = 0; i < this.conf.middleRows.size(); i++) {
            System.out.print(" " + this.conf.middleRows.get(i));
        }
        System.out.println();
        System.out.print("后两排名单：");
        for (int i = 0; i < this.conf.backRows.size(); i++) {
            System.out.print(" " + this.conf.backRows.get(i));
        }
        System.out.println();
        System.out.print("组长名单：");
        for (int i = 0; i < this.conf.groupLeaders.size(); i++) {
            System.out.print(" " + this.conf.groupLeaders.get(i));
        }
        System.out.println();
        System.out.println("拆分列表：");
        for (int i = 0; i < this.conf.separated.size(); i++) {
            System.out.println(this.conf.separated.get(i).toString());
        }
        System.out.println();

        Random rd = new Random(seed);
        this.seat = new ArrayList<>(Arrays.asList(new String[49]));
        ArrayList<Boolean> sorted = new ArrayList<>(Arrays.asList(new Boolean[44]));
        int t;
        int times = 0;
        do {
            for (int i = 0; i < 44; i++) {
                this.seat.set(i, "-");
                sorted.set(i, false);
            }
            for (int i = 44; i < 49; i++) {
                this.seat.set(i, "-");
            }
            // 第一、二排
            for (int i = 0; i < 14; i++) {
                do {
                    t = rd.nextInt(0, 14);
                } while (sorted.get(t));
                this.seat.set(i, this.conf.frontRows.get(t));
                sorted.set(t, true);
            }

            // 第三、四排
            for (int i = 14; i < 28; i++) {
                do {
                    t = rd.nextInt(0, 14);
                } while (sorted.get(t + 14));
                this.seat.set(i, this.conf.middleRows.get(t));
                sorted.set(t + 14, true);
            }

            // 第五、六排
            for (int i = 28; i < 42; i++) {
                do {
                    t = rd.nextInt(0, 16);
                } while (sorted.get(t + 28));
                this.seat.set(i, this.conf.backRows.get(t));
                sorted.set(t + 28, true);
            }

            // 第七排
            do {
                t = rd.nextInt(0, 16);
            } while (sorted.get(t + 28));
            int m = rd.nextInt(42, 49);
            this.seat.set(m, this.conf.backRows.get(t));
            sorted.set(t + 28, true);
            int n;
            do {
                t = rd.nextInt(0, 16);
                n = rd.nextInt(42, 49);
            } while (sorted.get(t + 28) || n == m);
            this.seat.set(n, this.conf.backRows.get(t));

            times++;
        } while (!check());

        //组长
        for (int i = 0; i < 7; i++) {
            do {
                t = rd.nextInt(0, 7);
            } while (!this.conf.groupLeaders.contains(this.seat.get(t * 7 + i)));
            this.seat.set(t * 7 + i, "*%s".formatted(this.seat.get(t * 7 + i)));
        }

        System.out.println(times + "次生成后成功");

        StringBuilder str = new StringBuilder("座位表：\n");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                str.append(this.seat.get(i * 7 + j)).append("\t");
            }
            str.append("\n");
        }
        System.out.print(str);

        return new Seat(this.seat, seed, times);
    }

    public boolean check() {
        boolean hasLeader = false;
        boolean isSeparated = true;
        // 检查每列是否都有组长
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                hasLeader = this.conf.groupLeaders.contains(this.seat.get(j * 7 + i));
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
        for (int i = 0; i < this.conf.separated.size(); i++) {
            if (isSeparated) {
                isSeparated = this.conf.separated.get(i).check(this.seat);
            } else {
                return false;
            }
        }

        return true;
    }
}
