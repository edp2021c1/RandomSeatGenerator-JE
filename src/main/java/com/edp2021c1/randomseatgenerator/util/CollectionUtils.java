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

import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.random.RandomGenerator;

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
     * Returns a range of integers from origin (inclusive) to bound (exclusive).
     *
     * @param origin the least value that can be returned
     * @param bound  the upper bound (exclusive)
     * @return a range of integer from origin (inclusive) to bound (exclusive)
     */
    public static List<Integer> range(final int origin, final int bound) {
        return new Range(origin, bound);
    }

    /**
     * Returns a modifiable range of integers from origin (inclusive) to bound (exclusive).
     *
     * @param origin the least value that can be returned
     * @param bound  the upper bound (exclusive)
     * @return a modifiable range of integer from origin (inclusive) to bound (exclusive)
     */
    public static List<Integer> modifiableRange(final int origin, final int bound) {
        return modifiableList(range(origin, bound));
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
        return CollectionUtils.<T>modifiableList(src).get(rd.nextInt(src.size()));
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
        val res = pickRandomly(src, rd);
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
    public static <T> List<T> modifiableList(final Collection<? extends T> src) {
        return new ArrayList<>(src);
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
        val res = new ArrayList<R>(input.size());
        input.forEach(t -> res.add(builder.apply(t)));
        return res;
    }

    /**
     * Returns a {@link IndexFilterList}.
     *
     * @param list           input list
     * @param indexPredicate filter of the index of elements
     * @param <T>            type of elements in the list
     * @return a {@link IndexFilterList}
     */
    public static <T> List<T> indexFilter(final List<T> list, final IntPredicate indexPredicate) {
        if (list instanceof RandomAccess) {
            return new RandomAccessIndexFilterList<>(list, indexPredicate);
        }
        return new IndexFilterList<>(list, indexPredicate);
    }

    /**
     * Returns a modify-free index filter list.
     *
     * @param list           input list
     * @param indexPredicate filter of the index of elements
     * @param <T>            type of elements in the list
     * @return a modify-free index filter list
     */
    public static <T> List<T> modifiableIndexFilter(final List<T> list, final IntPredicate indexPredicate) {
        return modifiableList(indexFilter(list, indexPredicate));
    }

    /**
     * Fast implementation of integer range.
     */
    private static class Range extends AbstractList<Integer> implements RandomAccess {

        private final int origin;
        private final int bound;
        private final int size;

        public Range(final int origin, final int bound) {
            if (origin > bound) {
                throw new IllegalArgumentException("Origin %d larger than bound %d".formatted(origin, bound));
            }

            this.origin = origin;
            this.bound = bound;
            this.size = bound - origin;
        }

        @Override
        public Integer get(final int index) {
            return origin + Objects.checkIndex(index, size);
        }

        @Override
        public int indexOf(final Object o) {
            if (!(o instanceof final Integer i) || !(origin <= i && i < bound)) {
                return -1;
            }
            return i - origin;
        }

        @Override
        public int lastIndexOf(final Object o) {
            return indexOf(o);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof final Integer i)) {
                return false;
            }
            return origin <= i && i < bound;
        }
    }

    private static class IndexFilterList<T> extends AbstractList<T> {
        private final Integer[] indexes;
        private final List<T> root;
        private final int size;
        private final int rootSize;

        public IndexFilterList(final List<T> root, final IntPredicate indexPredicate) {
            this.root = root;
            this.rootSize = root.size();

            val indexes = new ArrayList<Integer>();
            for (int i = 0, len = root.size(); i < len; i++) {
                if (indexPredicate.test(i)) {
                    indexes.add(i);
                }
            }
            this.indexes = indexes.toArray(new Integer[0]);
            this.size = this.indexes.length;
        }

        @Override
        public T get(final int index) {
            checkRootSize();
            return root.get(indexes[Objects.checkIndex(index, size)]);
        }

        @Override
        public T set(final int index, final T obj) {
            checkRootSize();
            return root.set(indexes[index], obj);
        }

        @Override
        public int indexOf(final Object o) {
            checkRootSize();
            return super.indexOf(o);
        }

        @Override
        public int lastIndexOf(final Object o) {
            checkRootSize();
            return super.lastIndexOf(o);
        }

        @Override
        public int size() {
            checkRootSize();
            return size;
        }

        @Override
        public boolean contains(Object o) {
            checkRootSize();
            return super.contains(o);
        }

        protected void checkRootSize() {
            if (rootSize != root.size()) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static class RandomAccessIndexFilterList<T> extends IndexFilterList<T> implements RandomAccess {

        public RandomAccessIndexFilterList(final List<T> root, final IntPredicate indexPredicate) {
            super(root, indexPredicate);

            if (!(root instanceof RandomAccess)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int indexOf(final Object o) {
            checkRootSize();
            var index = -1;
            for (var i = 0; i < size(); i++) {
                if (Objects.equals(get(i), o)) {
                    index = i;
                }
            }
            return index;
        }

        @Override
        public int lastIndexOf(final Object o) {
            checkRootSize();
            var lastIndex = -1;
            for (var i = size() - 1; i > -1; i--) {
                if (Objects.equals(get(i), o)) {
                    lastIndex = i;
                }
            }
            return lastIndex;
        }

        @Override
        public boolean contains(final Object o) {
            checkRootSize();
            for (val t : this) {
                if (Objects.equals(t, o)) {
                    return true;
                }
            }
            return false;
        }

    }

}
