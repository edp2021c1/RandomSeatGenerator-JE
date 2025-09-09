/*
 * This file is part of the RandomSeatGenerator project, licensed under the
 * GNU General Public License v3.0
 *
 * Copyright (C) 2025  EDP2021C1 and contributors
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

package com.edp2021c1.randomseatgenerator.util;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public final class CollectionUtils {

    public static <T> List<T> randomlyPick(@NotNull List<T> list, int count, @NotNull Random random) {
        List<T> result = Lists.newLinkedList(list);
        Collections.shuffle(result, random);
        return Lists.newLinkedList(result.subList(0, Math.min(count, result.size())));
    }

    public static <T> T randomlyPickOne(@NotNull List<T> list, @NotNull Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static List<Integer> range(int origin, int bound) {
        return IntStream.range(origin, bound).boxed().toList();
    }

}
