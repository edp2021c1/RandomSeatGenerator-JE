package com.edp2021c1.randomseatgenerator.v2.util;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public final class CollectionUtils {

    @Contract(pure = true)
    public static <T> List<T> randomlyPick(@NotNull List<T> list, int count, @NotNull Random random) {
        List<T> result = Lists.newLinkedList(list);
        Collections.shuffle(result, random);
        return Lists.newLinkedList(result.subList(0, Math.min(count, result.size())));
    }

    @Contract(pure = true)
    public static <T> T randomlyPickOne(@NotNull List<T> list, @NotNull Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static List<Integer> range(int origin, int bound) {
        return IntStream.range(origin, bound).boxed().toList();
    }

}
