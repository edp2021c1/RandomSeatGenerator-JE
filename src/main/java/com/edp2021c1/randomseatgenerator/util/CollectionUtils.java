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
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utils of {@code arrays} and {@link Collection} and its subclasses.
 *
 * @author Calboot
 * @since 1.4.9
 */
public final class CollectionUtils {

    /**
     * Don't let anyone else instantiate this class.
     */
    private CollectionUtils() {
    }

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
    public static <T> T pickRandomlyAndRemove(final Collection<? extends T> src, final RandomGenerator rd) {
        if (src instanceof final List<? extends T> list) {
            return list.remove(rd.nextInt(list.size()));
        }
        val res = pickRandomly(src, rd);
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
    public static <T> T pickRandomly(final Collection<? extends T> src, final RandomGenerator rd) {
        if (src instanceof final List<? extends T> list) {
            return list.get(rd.nextInt(list.size()));
        }
        return new ArrayList<T>(src).get(rd.nextInt(src.size()));
    }

    /**
     * Generates a list with a source list and a builder.
     *
     * @param input   source list
     * @param builder {@code Function} used to generate elements of the output list
     * @param <T>     type of input
     * @param <R>     type of output
     *
     * @return list generated
     */
    public static <T, R> List<R> buildList(final List<? extends T> input, final Function<T, R> builder) {
        return input.stream().map(builder).toList();
    }

    /**
     * Returns a list containing the elements whose index matches the predicate.
     *
     * @param list           input list
     * @param indexPredicate filter of the index of elements
     * @param <T>            type of elements in the list
     *
     * @return a list containing the elements whose index matches the predicate
     */
    public static <T> Stream<T> indexFilter(final List<T> list, final IntPredicate indexPredicate) {
        return IntStream.range(0, list.size()).filter(indexPredicate).mapToObj(list::get);
    }

}
