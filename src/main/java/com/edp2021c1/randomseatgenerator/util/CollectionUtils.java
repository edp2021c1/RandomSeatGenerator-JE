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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;

/**
 * Utils of {@code arrays} and {@link Collection} and its subclasses.
 *
 * @author Calboot
 * @since 1.4.9
 */
public class CollectionUtils {

    /**
     * Don't let anyone else instantiate this class.
     */
    private CollectionUtils() {
    }

    /**
     * Returns a range of integers from origin (inclusive) to bound (exclusive).
     *
     * @param origin the least value that can be returned
     * @param bound  the upper bound (exclusive)
     * @return a range of integer from origin (inclusive) to bound (exclusive)
     */
    public static List<Integer> range(final int origin, final int bound) {
        if (origin > bound) {
            throw new IllegalArgumentException("Origin %d larger than bound %d".formatted(origin, bound));
        }
        final List<Integer> res = new ArrayList<>(Math.abs(bound - origin));
        for (int i = origin; i < bound; i++) {
            res.add(i);
        }
        return res;
    }

    /**
     * Returns a randomly picked element of the source collection.
     *
     * @param src source collection
     * @param rd  random service used to choose the element
     * @param <T> type of the element
     * @return a randomly picked element of the source collection
     */
    public static <T> T pickRandomly(final Collection<? extends T> src, final RandomGenerator rd) {
        return CollectionUtils.<T>modifiableListOf(src).get(rd.nextInt(src.size()));
    }

    /**
     * Returns a randomly picked element of the source collection
     * and then remove it from the collection.
     *
     * @param src source collection
     * @param rd  random service used to choose the element
     * @param <T> type of the element
     * @return a randomly picked and removed element of the source collection
     */
    public static <T> T pickRandomlyAndRemove(final Collection<? extends T> src, final RandomGenerator rd) {
        final T res = pickRandomly(src, rd);
        src.remove(res);
        return res;
    }

    /**
     * Returns a list containing elements in the source collection that is free to modify.
     * That means any change of the returned list will not affect the source collection.
     *
     * @param src source collection
     * @param <T> type of the
     * @return a modify-free list
     */
    public static <T> List<T> modifiableListOf(final Collection<? extends T> src) {
        return new ArrayList<>(src);
    }

    /**
     * Returns a list containing elements in the source array that is free to modify.
     * That means any change of the returned list will not affect the source collection.
     *
     * @param arr source array
     * @param <T> type of the
     * @return a modify-free list
     */
    @SafeVarargs
    public static <T> List<T> modifiableListOf(final T... arr) {
        return modifiableListOf(Arrays.asList(arr));
    }

    /**
     * Generates a list with a source list and a builder.
     *
     * @param input   source list
     * @param builder {@code Function} used to generate elements of the output list
     * @param <T>     type of input
     * @param <R>     type of output
     * @return list generated
     */
    public static <T, R> List<R> buildList(final List<? extends T> input, final Function<T, R> builder) {
        final List<R> res = new ArrayList<>(input.size());
        input.forEach(t -> res.add(builder.apply(t)));
        return res;
    }

}
