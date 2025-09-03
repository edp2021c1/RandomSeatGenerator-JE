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

package com.edp2021c1.randomseatgenerator.util;

import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utils of data processing.
 *
 * @author Calboot
 * @since 1.4.9
 */
public final class DataUtils {

    /**
     * Returns a randomly picked element of the source collection
     * and then remove it from the collection.
     *
     * @param src source collection
     * @param rd  random service used to choose the element
     * @param <T> type of the element
     *
     * @return a randomly picked and removed element of the source collection
     */
    public static <T> T pickRandomlyAndRemove(final Collection<T> src, final RandomGenerator rd) {
        if (src instanceof List<? extends T>) {
            return ((List<T>) src).remove(rd.nextInt(src.size()));
        }
        T res = pickRandomly(src, rd);
        src.remove(res);
        return res;
    }

    /**
     * Returns a randomly picked element of the source collection.
     *
     * @param src source collection
     * @param rd  random service used to choose the element
     * @param <T> type of the element
     *
     * @return a randomly picked element of the source collection
     */
    public static <T> T pickRandomly(final Collection<T> src, final RandomGenerator rd) {
        if (src instanceof List<? extends T>) {
            return ((List<T>) src).get(rd.nextInt(src.size()));
        }
        return new ArrayList<>(src).get(rd.nextInt(src.size()));
    }

    /**
     * Splits a list by the given size and returns a {@link Stream} of the sublists.
     * The few elements at the end of the source list will be contained in the last elements of the returned stream.
     *
     * @param list      source to split
     * @param chunkSize number of elements in each chunk
     * @param <T>       type of the elements in the source list
     *
     * @return a {@code Stream} of {@code List<T>}, each with a size of {@code chunkSize}
     */
    public static <T> Stream<List<T>> split(final List<T> list, final int chunkSize) {
        return IntStream
                .range(0, (int) Math.ceil((double) list.size() / chunkSize))
                .mapToObj(i -> list.subList(i * chunkSize, Math.min((i + 1) * chunkSize, list.size())));
    }

    /**
     * Don't let anyone else instantiate this class.
     */
    private DataUtils() {
    }

}
