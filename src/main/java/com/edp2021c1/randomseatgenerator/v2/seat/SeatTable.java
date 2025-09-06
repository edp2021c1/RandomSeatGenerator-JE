package com.edp2021c1.randomseatgenerator.v2.seat;

import com.edp2021c1.randomseatgenerator.v2.util.Table;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class SeatTable extends Table<String> {

    @Getter
    @Nullable
    private final String seed;

    @Getter
    private final int[] leaders;

    @Getter
    @Setter
    @Nullable
    private String luckyPerson;

    public SeatTable(int rowCount, int columnCount, boolean hasLeaders) throws IllegalArgumentException {
        this(rowCount, columnCount, hasLeaders, null);
    }

    public SeatTable(int rowCount, int columnCount, boolean hasLeaders, @Nullable String seed) throws IllegalArgumentException {
        super(rowCount, columnCount, new String[0]);
        if (rowCount == 0 || columnCount == 0) {
            throw new IllegalArgumentException();
        }
        this.seed = seed;
        this.luckyPerson = null;
        this.leaders = hasLeaders ? new int[columnCount] : new int[0];
    }

    @Contract(pure = true)
    public boolean hasLeaders() {
        return leaders.length > 0;
    }

    @Contract(pure = true)
    public boolean hasLuckyPerson() {
        return luckyPerson != null;
    }

    @Contract(pure = true)
    public Set<Integer> getLeadersOfRow(int row) {
        if (!hasLeaders()) {
            return Sets.newHashSet();
        }
        Set<Integer> l = Sets.newHashSetWithExpectedSize(leaders.length);
        for (int i = 0; i < leaders.length; i++) {
            if (isLeader(row, i)) {
                l.add(i);
            }
        }
        return l;
    }

    @Contract(pure = true)
    public boolean isLeader(int row, int column) {
        return leaders[column] == row;
    }

    public void fillEmpty() {
        for (int i = 0; i < this.size; i++) {
            if (data[i] == null) {
                data[i] = "-";
            }
        }
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            s.append("[");

            for (int j = 0; j < columnCount - 1; j++) {
                if (hasLeaders() && leaders[j] == i) {
                    s.append("{").append(get(i, j)).append("}");
                } else {
                    s.append(get(i, j));
                }
                s.append(", ");
            }

            if (hasLeaders() && leaders[columnCount - 1] == i) {
                s.append("{").append(get(i, columnCount - 1)).append("}");
            } else {
                s.append(get(i, columnCount - 1));
            }

            s.append("]").append(System.lineSeparator());
        }
        if (luckyPerson != null) {
            s.append(luckyPerson);
        }
        return s.toString();
    }

}
