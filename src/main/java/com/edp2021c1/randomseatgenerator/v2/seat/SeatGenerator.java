package com.edp2021c1.randomseatgenerator.v2.seat;

import com.edp2021c1.randomseatgenerator.v2.util.CollectionUtils;
import com.edp2021c1.randomseatgenerator.v2.util.I18N;
import com.edp2021c1.randomseatgenerator.v2.util.Pair;
import com.edp2021c1.randomseatgenerator.v2.util.Table;
import com.edp2021c1.randomseatgenerator.v2.util.exception.TranslatableException;
import com.google.common.collect.Lists;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SeatGenerator {

    private final int rowCount, columnCount, shuffledRowCount;

    private final Set<Integer> disabledLastRowIndexes;

    private final List<String> nameList;

    private final Set<String> leaderNameSet;

    private final ImmutableGraph<@NotNull String> seperatedGraph;

    private static final int MAX_GENERATIONS = 65536;

    private final boolean findLucky, findLeaders;

    public SeatGenerator(SeatConfig config) {
        this.rowCount = config.rowCount;
        this.columnCount = config.columnCount;
        this.shuffledRowCount = config.shuffledRowCount;
        this.disabledLastRowIndexes = Arrays.stream(config.disabledLastRowPositions.split(" "))
                .map(s -> Integer.parseInt(s) - 1).filter(i -> i < this.columnCount).collect(Collectors.toSet());
        this.nameList = Arrays.asList(config.nameList.split(" "));
        this.leaderNameSet = new HashSet<>(Arrays.asList(config.leaderNameSet.split(" ")));

        MutableGraph<@NotNull String> s = GraphBuilder.undirected().build();
        nameList.forEach(s::addNode);
        config.separatedPairs.lines().forEach(l -> {
            String[] split = l.split(" ", 2);
            if (split.length >= 2 && !Objects.equals(split[0], split[1])) {
                s.putEdge(split[0], split[1]);
            }
        });
        this.seperatedGraph = ImmutableGraph.copyOf(s);

        this.findLucky = config.findLucky;
        this.findLeaders = config.findLeaders;
    }

    private boolean checkAndFindLeaders(SeatTable seatTable, Random random) {
        // Check seperated
        String[] data = seatTable.getData();
        for (int i = 0; i < data.length; i++) {
            String s = data[i];
            if (s == null) {
                continue;
            }
            if (!Collections.disjoint(
                    seatTable.getNeighbours(i / columnCount, i % columnCount),
                    seperatedGraph.adjacentNodes(s)
            )) {
                return false;
            }
        }

        // Check and find leaders
        if (seatTable.hasLeaders()) {
            for (int i = 0; i < columnCount; i++) {
                Table.Column<String> c = seatTable.getColumn(i);
                List<Integer> indexes = IntStream.range(0, rowCount).filter(j -> leaderNameSet.contains(c.get(j))).boxed().toList();
                if (indexes.isEmpty()) {
                    return false;
                }
                seatTable.getLeaders()[i] = CollectionUtils.randomlyPickOne(indexes, random);
            }
        }

        return true;
    }

    @Contract(pure = true)
    public SeatTable generate(String seed) {
        // Check config
        if (rowCount <= 0) {
            throw TranslatableException.illegalArgument("must_be_positive", rowCount);
        }
        if (columnCount <= 0) {
            throw TranslatableException.illegalArgument("must_be_positive", columnCount);
        }
        if (shuffledRowCount <= 0) {
            throw TranslatableException.illegalArgument("must_be_positive", shuffledRowCount);
        }

        int seatCount         = rowCount * columnCount - disabledLastRowIndexes.size();
        int peopleInSeatCount = nameList.size() - (findLucky ? 1 : 0);
        if (seatCount < peopleInSeatCount) {
            throw TranslatableException.illegalArgument("not_enough", I18N.constant("seats"), seatCount, peopleInSeatCount);
        }

        if (findLeaders && leaderNameSet.size() < columnCount) {
            throw TranslatableException.illegalArgument("not_enough_leaders", I18N.constant("leaders"), leaderNameSet.size(), columnCount);
        }

        int block = shuffledRowCount * columnCount;

        List<Pair<Integer, Integer>> rangesToBeShuffled = Lists.newLinkedList();
        {
            int i = peopleInSeatCount;
            while (i > block) {
                rangesToBeShuffled.add(new Pair<>(peopleInSeatCount - i, peopleInSeatCount - i + block));
                i -= block;
            }
            if (i < columnCount) {
                rangesToBeShuffled.set(rangesToBeShuffled.size() - 1, new Pair<>(peopleInSeatCount - i - block, peopleInSeatCount));
            } else {
                rangesToBeShuffled.add(new Pair<>(peopleInSeatCount - i, peopleInSeatCount));
            }
        }

        Random random = new Random(seed == null ? 0L : seed.hashCode());

        int loopTimes = 0;

        SeatTable    seatTable = new SeatTable(rowCount, columnCount, seed, findLeaders);
        List<String> names     = Lists.newArrayListWithExpectedSize(nameList.size());
        do {
            if (loopTimes >= MAX_GENERATIONS) {
                throw TranslatableException.common("seat.too_many_generations", MAX_GENERATIONS);
            }

            Arrays.fill(seatTable.getData(), null);

            names.clear();
            names.addAll(nameList);
            if (findLucky) {
                seatTable.setLuckyPerson(names.remove(random.nextInt(nameList.size())));
            }

            for (Pair<Integer, Integer> range : rangesToBeShuffled) {
                Collections.shuffle(names.subList(range.first, range.second), random);
            }

            int i = names.size() / columnCount;
            int j = i * columnCount;
            Collections.copy(seatTable.getDataAsList(), names.subList(0, j));
            List<Integer> indexes = Lists.newLinkedList(IntStream.range(0, columnCount).boxed().toList());
            if (i == rowCount - 1) {
                indexes.removeAll(disabledLastRowIndexes);
            }
            indexes = CollectionUtils.randomlyPick(indexes, names.size() - j, random);
            int k = 0;
            for (int index : indexes) {
                seatTable.getData()[j + index] = names.get(j + k++);
            }
            loopTimes++;
        } while (!checkAndFindLeaders(seatTable, random));

        seatTable.fillEmpty();

        return seatTable;
    }

}
