/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.*;
import javaslang.control.Option;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * Tests all methods defined in {@link javaslang.collection.Seq}.
 */
public abstract class AbstractSeqTest extends AbstractTraversableRangeTest {

    // -- construction

    @Override
    abstract protected <T> Collector<T, ArrayList<T>, ? extends Seq<T>> collector();

    @Override
    abstract protected <T> Seq<T> empty();

    @Override
    abstract protected <T> Seq<T> of(T element);

    @SuppressWarnings("unchecked")
    @Override
    abstract protected <T> Seq<T> of(T... elements);

    @Override
    abstract protected <T> Seq<T> ofAll(Iterable<? extends T> elements);

    @Override
    abstract protected Seq<Boolean> ofAll(boolean[] array);

    @Override
    abstract protected Seq<Byte> ofAll(byte[] array);

    @Override
    abstract protected Seq<Character> ofAll(char[] array);

    @Override
    abstract protected Seq<Double> ofAll(double[] array);

    @Override
    abstract protected Seq<Float> ofAll(float[] array);

    @Override
    abstract protected Seq<Integer> ofAll(int[] array);

    @Override
    abstract protected Seq<Long> ofAll(long[] array);

    @Override
    abstract protected Seq<Short> ofAll(short[] array);

    @Override
    abstract protected Seq<Character> range(char from, char toExclusive);

    @Override
    abstract protected Seq<Character> rangeBy(char from, char toExclusive, int step);

    @Override
    abstract protected Seq<Double> rangeBy(double from, double toExclusive, double step);

    @Override
    abstract protected Seq<Integer> range(int from, int toExclusive);

    @Override
    abstract protected Seq<Integer> rangeBy(int from, int toExclusive, int step);

    @Override
    abstract protected Seq<Long> range(long from, long toExclusive);

    @Override
    abstract protected Seq<Long> rangeBy(long from, long toExclusive, long step);

    @Override
    abstract protected Seq<Character> rangeClosed(char from, char toInclusive);

    @Override
    abstract protected Seq<Character> rangeClosedBy(char from, char toInclusive, int step);

    @Override
    abstract protected Seq<Double> rangeClosedBy(double from, double toInclusive, double step);

    @Override
    abstract protected Seq<Integer> rangeClosed(int from, int toInclusive);

    @Override
    abstract protected Seq<Integer> rangeClosedBy(int from, int toInclusive, int step);

    @Override
    abstract protected Seq<Long> rangeClosed(long from, long toInclusive);

    @Override
    abstract protected Seq<Long> rangeClosedBy(long from, long toInclusive, long step);

    // -- static narrow

    @Test
    public void shouldNarrowSeq() {
        final Seq<Double> doubles = of(1.0d);
        final Seq<Number> numbers = Seq.narrow(doubles);
        final int actual = numbers.append(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- append

    @Test
    public void shouldAppendElementToNil() {
        final Seq<Integer> actual = this.<Integer> empty().append(1);
        final Seq<Integer> expected = of(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendNullElementToNil() {
        final Seq<Integer> actual = this.<Integer> empty().append(null);
        final Seq<Integer> expected = this.of((Integer) null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendElementToNonNil() {
        final Seq<Integer> actual = of(1, 2).append(3);
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMixAppendAndPrepend() {
        assertThat(of(1).append(2).prepend(0).prepend(-1).append(3).append(4)).isEqualTo(of(-1, 0, 1, 2, 3, 4));
    }

    // -- appendAll

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnAppendAllOfNull() {
        empty().appendAll(null);
    }

    @Test
    public void shouldAppendAllNilToNil() {
        final Seq<Object> actual = empty().appendAll(empty());
        final Seq<Object> expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNonNilToNil() {
        final Seq<Integer> actual = this.<Integer> empty().appendAll(of(1, 2, 3));
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNilToNonNil() {
        final Seq<Integer> actual = of(1, 2, 3).appendAll(empty());
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNonNilToNonNil() {
        final Seq<Integer> actual = of(1, 2, 3).appendAll(of(4, 5, 6));
        final Seq<Integer> expected = of(1, 2, 3, 4, 5, 6);
        assertThat(actual).isEqualTo(expected);
    }

    // -- apply

    @Test
    public void shouldUseSeqAsPartialFunction() {
        assertThat(of(1, 2, 3).apply(1)).isEqualTo(2);
    }

    // -- combinations

    @Test
    public void shouldComputeCombinationsOfEmptyList() {
        assertThat(empty().combinations()).isEqualTo(of(empty()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldComputeCombinationsOfNonEmptyList() {
        assertThat(of(1, 2, 3).combinations())
                .isEqualTo(of(empty(), of(1), of(2), of(3), of(1, 2), of(1, 3), of(2, 3), of(1, 2, 3)));
    }

    // -- combinations(k)

    @Test
    public void shouldComputeKCombinationsOfEmptyList() {
        assertThat(empty().combinations(1)).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldComputeKCombinationsOfNonEmptyList() {
        assertThat(of(1, 2, 3).combinations(2)).isEqualTo(of(of(1, 2), of(1, 3), of(2, 3)));
    }

    @Test
    public void shouldComputeKCombinationsOfNegativeK() {
        assertThat(of(1).combinations(-1)).isEqualTo(of(empty()));
    }

    // -- containsSlice

    @Test
    public void shouldRecognizeNilNotContainsSlice() {
        final boolean actual = empty().containsSlice(of(1, 2, 3));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesContainSlice() {
        final boolean actual = of(1, 2, 3, 4, 5).containsSlice(of(2, 3));
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldRecognizeNonNilDoesNotContainSlice() {
        final boolean actual = of(1, 2, 3, 4, 5).containsSlice(of(2, 1, 4));
        assertThat(actual).isFalse();
    }

    // -- crossProduct()

    @Test
    public void shouldCalculateCrossProductOfNil() {
        final Iterator<Tuple2<Object, Object>> actual = empty().crossProduct();
        assertThat(actual).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCalculateCrossProductOfNonNil() {
        final List<Tuple2<Integer, Integer>> actual = of(1, 2, 3).crossProduct().toList();
        final List<Tuple2<Integer, Integer>> expected = List.of(Tuple.of(1, 1), Tuple.of(1, 2), Tuple.of(1, 3),
                Tuple.of(2, 1), Tuple.of(2, 2), Tuple.of(2, 3), Tuple.of(3, 1), Tuple.of(3, 2), Tuple.of(3, 3));
        assertThat(actual).isEqualTo(expected);
    }

    // -- crossProduct(int)

    @Test
    public void shouldCalculateCrossProductPower() {
        assertThat(of(1, 2).crossProduct(0).toList()).isEqualTo(List.of(empty()));
        assertThat(of(1, 2).crossProduct(1).toList()).isEqualTo(List.of(of(1), of(2)));
        assertThat(of(1, 2).crossProduct(2).toList()).isEqualTo(List.of(of(1, 1), of(1, 2), of(2, 1), of(2, 2)));
    }

    @Test
    public void shouldCrossProductPowerBeLazy() {
        assertThat(range(0, 10).crossProduct(100).take(1).get()).isEqualTo(tabulate(100, i -> 0));
    }

    // -- crossProduct(Iterable)

    @Test
    public void shouldCalculateCrossProductOfNilAndNil() {
        final Iterator<Tuple2<Object, Object>> actual = empty().crossProduct(empty());
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldCalculateCrossProductOfNilAndNonNil() {
        final Iterator<Tuple2<Object, Object>> actual = empty().crossProduct(of(1, 2, 3));
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldCalculateCrossProductOfNonNilAndNil() {
        final Iterator<Tuple2<Integer, Integer>> actual = of(1, 2, 3).crossProduct(empty());
        assertThat(actual).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCalculateCrossProductOfNonNilAndNonNil() {
        final List<Tuple2<Integer, Character>> actual = of(1, 2, 3).crossProduct(of('a', 'b')).toList();
        final List<Tuple2<Integer, Character>> expected = of(Tuple.of(1, 'a'), Tuple.of(1, 'b'),
                Tuple.of(2, 'a'), Tuple.of(2, 'b'), Tuple.of(3, 'a'), Tuple.of(3, 'b')).toList();
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCalculatingCrossProductAndThatIsNull() {
        empty().crossProduct(null);
    }

    // -- get

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithNegativeIndexOnNil() {
        empty().get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithNegativeIndexOnNonNil() {
        of(1).get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetOnNil() {
        empty().get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithTooBigIndexOnNonNil() {
        of(1).get(1);
    }

    @Test
    public void shouldGetFirstElement() {
        assertThat(of(1, 2, 3).get(0)).isEqualTo(1);
    }

    @Test
    public void shouldGetLastElement() {
        assertThat(of(1, 2, 3).get(2)).isEqualTo(3);
    }

    // -- indexOf

    @Test
    public void shouldNotFindIndexOfElementWhenSeqIsEmpty() {
        assertThat(empty().indexOf(1)).isEqualTo(-1);

        assertThat(empty().indexOfOption(1)).isEqualTo(Option.none());
    }

    @Test
    public void shouldNotFindIndexOfElementWhenStartIsGreater() {
        assertThat(of(1, 2, 3, 4).indexOf(2, 2)).isEqualTo(-1);

        assertThat(of(1, 2, 3, 4).indexOfOption(2, 2)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindIndexOfFirstElement() {
        assertThat(of(1, 2, 3).indexOf(1)).isEqualTo(0);

        assertThat(of(1, 2, 3).indexOfOption(1)).isEqualTo(Option.some(0));
    }

    @Test
    public void shouldFindIndexOfInnerElement() {
        assertThat(of(1, 2, 3).indexOf(2)).isEqualTo(1);

        assertThat(of(1, 2, 3).indexOfOption(2)).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldFindIndexOfLastElement() {
        assertThat(of(1, 2, 3).indexOf(3)).isEqualTo(2);

        assertThat(of(1, 2, 3).indexOfOption(3)).isEqualTo(Option.some(2));
    }

    // -- indexOfSlice

    @Test
    public void shouldNotFindIndexOfSliceWhenSeqIsEmpty() {
        assertThat(empty().indexOfSlice(of(2, 3))).isEqualTo(-1);

        assertThat(empty().indexOfSliceOption(of(2, 3))).isEqualTo(Option.none());
    }

    @Test
    public void shouldNotFindIndexOfSliceWhenStartIsGreater() {
        assertThat(of(1, 2, 3, 4).indexOfSlice(of(2, 3), 2)).isEqualTo(-1);

        assertThat(of(1, 2, 3, 4).indexOfSliceOption(of(2, 3), 2)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindIndexOfFirstSlice() {
        assertThat(of(1, 2, 3, 4).indexOfSlice(of(1, 2))).isEqualTo(0);

        assertThat(of(1, 2, 3, 4).indexOfSliceOption(of(1, 2))).isEqualTo(Option.some(0));
    }

    @Test
    public void shouldFindIndexOfInnerSlice() {
        assertThat(of(1, 2, 3, 4).indexOfSlice(of(2, 3))).isEqualTo(1);

        assertThat(of(1, 2, 3, 4).indexOfSliceOption(of(2, 3))).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldFindIndexOfLastSlice() {
        assertThat(of(1, 2, 3).indexOfSlice(of(2, 3))).isEqualTo(1);

        assertThat(of(1, 2, 3).indexOfSliceOption(of(2, 3))).isEqualTo(Option.some(1));
    }

    // -- lastIndexOf

    @Test
    public void shouldNotFindLastIndexOfElementWhenSeqIsEmpty() {
        assertThat(empty().lastIndexOf(1)).isEqualTo(-1);

        assertThat(empty().lastIndexOfOption(1)).isEqualTo(Option.none());
    }

    @Test
    public void shouldNotFindLastIndexOfElementWhenEndIdLess() {
        assertThat(of(1, 2, 3, 4).lastIndexOf(3, 1)).isEqualTo(-1);

        assertThat(of(1, 2, 3, 4).lastIndexOfOption(3, 1)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindLastIndexOfElement() {
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOf(1)).isEqualTo(3);

        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfOption(1)).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldFindLastIndexOfElementWithEnd() {
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOf(1, 1)).isEqualTo(0);

        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfOption(1, 1)).isEqualTo(Option.some(0));
    }

    // -- lastIndexOfSlice

    @Test
    public void shouldNotFindLastIndexOfSliceWhenSeqIsEmpty() {
        assertThat(empty().lastIndexOfSlice(of(2, 3))).isEqualTo(-1);

        assertThat(empty().lastIndexOfSliceOption(of(2, 3))).isEqualTo(Option.none());
    }

    @Test
    public void shouldNotFindLastIndexOfSliceWhenEndIdLess() {
        assertThat(of(1, 2, 3, 4, 5).lastIndexOfSlice(of(3, 4), 1)).isEqualTo(-1);

        assertThat(of(1, 2, 3, 4, 5).lastIndexOfSliceOption(of(3, 4), 1)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindLastIndexOfSlice() {
        assertThat(of(1, 2, 3, 1, 2).lastIndexOfSlice(empty())).isEqualTo(5);
        assertThat(of(1, 2, 3, 1, 2).lastIndexOfSlice(of(2))).isEqualTo(4);
        assertThat(of(1, 2, 3, 1, 2, 3, 4).lastIndexOfSlice(of(2, 3))).isEqualTo(4);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(of(1, 2, 3))).isEqualTo(3);

        assertThat(of(1, 2, 3, 1, 2).lastIndexOfSliceOption(empty())).isEqualTo(Option.some(5));
        assertThat(of(1, 2, 3, 1, 2).lastIndexOfSliceOption(of(2))).isEqualTo(Option.some(4));
        assertThat(of(1, 2, 3, 1, 2, 3, 4).lastIndexOfSliceOption(of(2, 3))).isEqualTo(Option.some(4));
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(of(1, 2, 3))).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldFindLastIndexOfSliceWithEnd() {
        assertThat(empty().lastIndexOfSlice(empty(), -1)).isEqualTo(-1);
        assertThat(empty().lastIndexOfSlice(empty(), 0)).isEqualTo(0);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(empty(), -1)).isEqualTo(-1);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(empty(), 2)).isEqualTo(2);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(of(2), -1)).isEqualTo(-1);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(of(2), 2)).isEqualTo(1);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(of(2, 3), 2)).isEqualTo(1);
        assertThat(of(1, 2, 3, 1, 2, 3, 4).lastIndexOfSlice(of(2, 3), 2)).isEqualTo(1);
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSlice(of(1, 2, 3), 2)).isEqualTo(0);

        assertThat(empty().lastIndexOfSliceOption(empty(), -1)).isEqualTo(Option.none());
        assertThat(empty().lastIndexOfSliceOption(empty(), 0)).isEqualTo(Option.some(0));
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(empty(), -1)).isEqualTo(Option.none());
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(empty(), 2)).isEqualTo(Option.some(2));
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(of(2), -1)).isEqualTo(Option.none());
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(of(2), 2)).isEqualTo(Option.some(1));
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(of(2, 3), 2)).isEqualTo(Option.some(1));
        assertThat(of(1, 2, 3, 1, 2, 3, 4).lastIndexOfSliceOption(of(2, 3), 2)).isEqualTo(Option.some(1));
        assertThat(of(1, 2, 3, 1, 2, 3).lastIndexOfSliceOption(of(1, 2, 3), 2)).isEqualTo(Option.some(0));
    }

    // -- indexWhere

    @Test
    public void shouldCalculateIndexWhere() {
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 0)).isEqualTo(0);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 1)).isEqualTo(1);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 2)).isEqualTo(2);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 8)).isEqualTo(-1);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 0, 3)).isEqualTo(4);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 1, 3)).isEqualTo(5);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 2, 3)).isEqualTo(6);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhere(i -> i == 8, 3)).isEqualTo(-1);

        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 0)).isEqualTo(Option.some(0));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 1)).isEqualTo(Option.some(1));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 2)).isEqualTo(Option.some(2));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 8)).isEqualTo(Option.none());
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 0, 3)).isEqualTo(Option.some(4));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 1, 3)).isEqualTo(Option.some(5));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 2, 3)).isEqualTo(Option.some(6));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).indexWhereOption(i -> i == 8, 3)).isEqualTo(Option.none());
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailIndexWhereNullPredicate() {
        of(1).indexWhere(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailIndexWhereNullPredicateFrom() {
        of(1).indexWhere(null, 0);
    }

    // -- lastIndexWhere

    @Test
    public void shouldCalculateLastIndexWhere() {
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 0)).isEqualTo(4);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 1)).isEqualTo(5);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 2)).isEqualTo(6);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 8)).isEqualTo(-1);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 0, 3)).isEqualTo(0);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 1, 3)).isEqualTo(1);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 2, 3)).isEqualTo(2);
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhere(i -> i == 8, 3)).isEqualTo(-1);

        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 0)).isEqualTo(Option.some(4));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 1)).isEqualTo(Option.some(5));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 2)).isEqualTo(Option.some(6));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 8)).isEqualTo(Option.none());
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 0, 3)).isEqualTo(Option.some(0));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 1, 3)).isEqualTo(Option.some(1));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 2, 3)).isEqualTo(Option.some(2));
        assertThat(of(0, 1, 2, -1, 0, 1, 2).lastIndexWhereOption(i -> i == 8, 3)).isEqualTo(Option.none());
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailLastIndexWhereNullPredicate() {
        of(1).lastIndexWhere(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailLastIndexWhereNullPredicateFrom() {
        of(1).lastIndexWhere(null, 0);
    }

    // -- endsWith

    @Test
    public void shouldTestEndsWith() {
        assertThat(empty().endsWith(empty())).isTrue();
        assertThat(empty().endsWith(of(1))).isFalse();
        assertThat(of(1, 2, 3, 4).endsWith(empty())).isTrue();
        assertThat(of(1, 2, 3, 4).endsWith(of(4))).isTrue();
        assertThat(of(1, 2, 3, 4).endsWith(of(3, 4))).isTrue();
        assertThat(of(1, 2, 3, 4).endsWith(of(1, 2, 3, 4))).isTrue();
        assertThat(of(1, 2, 3, 4).endsWith(of(0, 1, 2, 3, 4))).isFalse();
        assertThat(of(1, 2, 3, 4).endsWith(of(2, 3, 5))).isFalse();
    }

    // -- insert

    @Test
    public void shouldInsertIntoNil() {
        final Seq<Integer> actual = this.<Integer> empty().insert(0, 1);
        final Seq<Integer> expected = of(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertInFrontOfElement() {
        final Seq<Integer> actual = of(4).insert(0, 1);
        final Seq<Integer> expected = of(1, 4);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertBehindOfElement() {
        final Seq<Integer> actual = of(4).insert(1, 1);
        final Seq<Integer> expected = of(4, 1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertIntoSeq() {
        final Seq<Integer> actual = of(1, 2, 3).insert(2, 4);
        final Seq<Integer> expected = of(1, 2, 4, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNonNilWithNegativeIndex() {
        of(1).insert(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNilWithNegativeIndex() {
        empty().insert(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnInsertWhenExceedingUpperBound() {
        empty().insert(1, null);
    }

    // -- insertAll

    @Test
    public void shouldInserAlltIntoNil() {
        final Seq<Integer> actual = this.<Integer> empty().insertAll(0, of(1, 2, 3));
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllInFrontOfElement() {
        final Seq<Integer> actual = of(4).insertAll(0, of(1, 2, 3));
        final Seq<Integer> expected = of(1, 2, 3, 4);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllBehindOfElement() {
        final Seq<Integer> actual = of(4).insertAll(1, of(1, 2, 3));
        final Seq<Integer> expected = of(4, 1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllIntoSeq() {
        final Seq<Integer> actual = of(1, 2, 3).insertAll(2, of(4, 5));
        final Seq<Integer> expected = of(1, 2, 4, 5, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnInsertAllWithNil() {
        empty().insertAll(0, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNonNilAllWithNegativeIndex() {
        of(1).insertAll(-1, empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNilAllWithNegativeIndex() {
        empty().insertAll(-1, empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnInsertAllWhenExceedingUpperBound() {
        empty().insertAll(1, empty());
    }

    // -- intersperse

    @Test
    public void shouldIntersperseNil() {
        assertThat(this.<Character> empty().intersperse(',')).isEmpty();
    }

    @Test
    public void shouldIntersperseSingleton() {
        assertThat(of('a').intersperse(',')).isEqualTo(of('a'));
    }

    @Test
    public void shouldIntersperseMultipleElements() {
        assertThat(of('a', 'b').intersperse(',')).isEqualTo(of('a', ',', 'b'));
    }

    // -- iterator(int)

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenNilIteratorStartingAtIndex() {
        empty().iterator(1);
    }

    @Test
    public void shouldIterateFirstElementOfNonNilStartingAtIndex() {
        assertThat(of(1, 2, 3).iterator(1).next()).isEqualTo(2);
    }

    @Test
    public void shouldFullyIterateNonNilStartingAtIndex() {
        int actual = -1;
        for (final Iterator<Integer> iter = of(1, 2, 3).iterator(1); iter.hasNext(); ) {
            actual = iter.next();
        }
        assertThat(actual).isEqualTo(3);
    }

    // -- padTo

    @Test
    public void shouldPadEmptyToEmpty() {
        assertThat(empty().padTo(0, 1)).isSameAs(empty());
    }

    @Test
    public void shouldPadEmptyToNonEmpty() {
        assertThat(empty().padTo(2, 1)).isEqualTo(of(1, 1));
    }

    @Test
    public void shouldPadNonEmptyZeroLen() {
        final Seq<Integer> seq = of(1);
        assertThat(seq.padTo(0, 2)).isSameAs(seq);
    }

    @Test
    public void shouldPadNonEmpty() {
        assertThat(of(1).padTo(2, 1)).isEqualTo(of(1, 1));
        assertThat(of(1).padTo(2, 2)).isEqualTo(of(1, 2));
        assertThat(of(1).padTo(3, 2)).isEqualTo(of(1, 2, 2));
    }

    // -- leftPadTo

    @Test
    public void shouldLeftPadEmptyToEmpty() {
        assertThat(empty().leftPadTo(0, 1)).isSameAs(empty());
    }

    @Test
    public void shouldLeftPadEmptyToNonEmpty() {
        assertThat(empty().leftPadTo(2, 1)).isEqualTo(of(1, 1));
    }

    @Test
    public void shouldLeftPadNonEmptyZeroLen() {
        final Seq<Integer> seq = of(1);
        assertThat(seq.leftPadTo(0, 2)).isSameAs(seq);
    }

    @Test
    public void shouldLeftPadNonEmpty() {
        assertThat(of(1).leftPadTo(2, 1)).isEqualTo(of(1, 1));
        assertThat(of(1).leftPadTo(2, 2)).isEqualTo(of(2, 1));
        assertThat(of(1).leftPadTo(3, 2)).isEqualTo(of(2, 2, 1));
    }

    // -- patch

    @Test
    public void shouldPatchEmptyByEmpty() {
        assertThat(empty().patch(0, empty(), 0)).isEmpty();
        assertThat(empty().patch(-1, empty(), -1)).isEmpty();
        assertThat(empty().patch(-1, empty(), 1)).isEmpty();
        assertThat(empty().patch(1, empty(), -1)).isEmpty();
        assertThat(empty().patch(1, empty(), 1)).isEmpty();
    }

    @Test
    public void shouldPatchEmptyByNonEmpty() {
        final Seq<Character> s = of('1', '2', '3');
        assertThat(empty().patch(0, s, 0)).isEqualTo(s);
        assertThat(empty().patch(-1, s, -1)).isEqualTo(s);
        assertThat(empty().patch(-1, s, 1)).isEqualTo(s);
        assertThat(empty().patch(1, s, -1)).isEqualTo(s);
        assertThat(empty().patch(1, s, 1)).isEqualTo(s);
    }

    @Test
    public void shouldPatchNonEmptyByEmpty() {
        final Seq<Character> s = of('1', '2', '3');
        assertThat(s.patch(-1, empty(), -1)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(-1, empty(), 0)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(-1, empty(), 1)).isEqualTo(of('2', '3'));
        assertThat(s.patch(-1, empty(), 3)).isEmpty();
        assertThat(s.patch(0, empty(), -1)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(0, empty(), 0)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(0, empty(), 1)).isEqualTo(of('2', '3'));
        assertThat(s.patch(0, empty(), 3)).isEmpty();
        assertThat(s.patch(1, empty(), -1)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(1, empty(), 0)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(1, empty(), 1)).isEqualTo(of('1', '3'));
        assertThat(s.patch(1, empty(), 3)).isEqualTo(of('1'));
        assertThat(s.patch(4, empty(), -1)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(4, empty(), 0)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(4, empty(), 1)).isEqualTo(of('1', '2', '3'));
        assertThat(s.patch(4, empty(), 3)).isEqualTo(of('1', '2', '3'));
    }

    @Test
    public void shouldPatchNonEmptyByNonEmpty() {
        final Seq<Character> s = of('1', '2', '3');
        final Seq<Character> d = of('4', '5', '6');
        assertThat(s.patch(-1, d, -1)).isEqualTo(of('4', '5', '6', '1', '2', '3'));
        assertThat(s.patch(-1, d, 0)).isEqualTo(of('4', '5', '6', '1', '2', '3'));
        assertThat(s.patch(-1, d, 1)).isEqualTo(of('4', '5', '6', '2', '3'));
        assertThat(s.patch(-1, d, 3)).isEqualTo(of('4', '5', '6'));
        assertThat(s.patch(0, d, -1)).isEqualTo(of('4', '5', '6', '1', '2', '3'));
        assertThat(s.patch(0, d, 0)).isEqualTo(of('4', '5', '6', '1', '2', '3'));
        assertThat(s.patch(0, d, 1)).isEqualTo(of('4', '5', '6', '2', '3'));
        assertThat(s.patch(0, d, 3)).isEqualTo(of('4', '5', '6'));
        assertThat(s.patch(1, d, -1)).isEqualTo(of('1', '4', '5', '6', '2', '3'));
        assertThat(s.patch(1, d, 0)).isEqualTo(of('1', '4', '5', '6', '2', '3'));
        assertThat(s.patch(1, d, 1)).isEqualTo(of('1', '4', '5', '6', '3'));
        assertThat(s.patch(1, d, 3)).isEqualTo(of('1', '4', '5', '6'));
        assertThat(s.patch(4, d, -1)).isEqualTo(of('1', '2', '3', '4', '5', '6'));
        assertThat(s.patch(4, d, 0)).isEqualTo(of('1', '2', '3', '4', '5', '6'));
        assertThat(s.patch(4, d, 1)).isEqualTo(of('1', '2', '3', '4', '5', '6'));
        assertThat(s.patch(4, d, 3)).isEqualTo(of('1', '2', '3', '4', '5', '6'));
    }

    // -- permutations

    @Test
    public void shouldComputePermutationsOfEmptySeq() {
        assertThat(empty().permutations()).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldComputePermutationsOfNonEmptySeq() {
        assertThat(of(1, 2, 3).permutations())
                .isEqualTo(ofAll(of(of(1, 2, 3), of(1, 3, 2), of(2, 1, 3), of(2, 3, 1), of(3, 1, 2), of(3, 2, 1))));
    }

    // -- map

    @Test
    public void shouldMapTransformedSeq() {
        final Function<Integer, Integer> mapper = o -> o + 1;
        assertThat(this.<Integer> empty().map(mapper)).isEmpty();
        assertThat(of(3, 1, 4, 1, 5).map(mapper)).isEqualTo(of(4, 2, 5, 2, 6));
        assertThat(of(3, 1, 4, 1, 5, 9, 2).sorted().distinct().drop(1).init().remove(5).map(mapper).tail()).isEqualTo(of(4, 5));
    }

    // -- prefixLength

    @Test
    public void shouldCalculatePrefixLength() {
        assertThat(of(1, 3, 5, 6).prefixLength(i -> (i & 1) > 0)).isEqualTo(3);
        assertThat(of(1, 3, 5).prefixLength(i -> (i & 1) > 0)).isEqualTo(3);
        assertThat(of(2).prefixLength(i -> (i & 1) > 0)).isEqualTo(0);
        assertThat(empty().prefixLength(i -> true)).isEqualTo(0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowPrefixLengthNullPredicate() {
        of(1).prefixLength(null);
    }

    // -- segmentLength

    @Test
    public void shouldCalculateSegmentLength() {
        assertThat(of(1, 3, 5, 6).segmentLength(i -> (i & 1) > 0, 1)).isEqualTo(2);
        assertThat(of(1, 3, 5).segmentLength(i -> (i & 1) > 0, 1)).isEqualTo(2);
        assertThat(of(2, 2).segmentLength(i -> (i & 1) > 0, 1)).isEqualTo(0);
        assertThat(of(2).segmentLength(i -> (i & 1) > 0, 1)).isEqualTo(0);
        assertThat(empty().segmentLength(i -> true, 1)).isEqualTo(0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowSegmentLengthNullPredicate() {
        of(1).segmentLength(null, 0);
    }

    // -- prepend

    @Test
    public void shouldPrependElementToNil() {
        final Seq<Integer> actual = this.<Integer> empty().prepend(1);
        final Seq<Integer> expected = of(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependElementToNonNil() {
        final Seq<Integer> actual = of(2, 3).prepend(1);
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- prependAll

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnPrependAllOfNull() {
        empty().prependAll(null);
    }

    @Test
    public void shouldPrependAllNilToNil() {
        final Seq<Integer> actual = this.<Integer> empty().prependAll(empty());
        final Seq<Integer> expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNilToNonNil() {
        final Seq<Integer> actual = of(1, 2, 3).prependAll(empty());
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNonNilToNil() {
        final Seq<Integer> actual = this.<Integer> empty().prependAll(of(1, 2, 3));
        final Seq<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNonNilToNonNil() {
        final Seq<Integer> actual = of(4, 5, 6).prependAll(of(1, 2, 3));
        final Seq<Integer> expected = of(1, 2, 3, 4, 5, 6);
        assertThat(actual).isEqualTo(expected);
    }

    // -- remove

    @Test
    public void shouldRemoveElementFromNil() {
        assertThat(empty().remove(null)).isEmpty();
    }

    @Test
    public void shouldRemoveFirstElement() {
        assertThat(of(1, 2, 3).remove(1)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldRemoveLastElement() {
        assertThat(of(1, 2, 3).remove(3)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldRemoveInnerElement() {
        assertThat(of(1, 2, 3).remove(2)).isEqualTo(of(1, 3));
    }

    @Test
    public void shouldNotRemoveDuplicateElement() {
        assertThat(of(1, 2, 3, 1, 2).remove(1).remove(3)).isEqualTo(of(2, 1, 2));
    }

    @Test
    public void shouldRemoveNonExistingElement() {
        final Seq<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.remove(4)).isEqualTo(t);
        } else {
            assertThat(t.remove(4)).isSameAs(t);
        }
    }

    // -- removeFirst(Predicate)

    @Test
    public void shouldRemoveFirstElementByPredicateFromNil() {
        assertThat(empty().removeFirst(v -> true)).isEmpty();
    }

    @Test
    public void shouldRemoveFirstElementByPredicateBegin() {
        assertThat(of(1, 2, 3).removeFirst(v -> v == 1)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateBeginM() {
        assertThat(of(1, 2, 1, 3).removeFirst(v -> v == 1)).isEqualTo(of(2, 1, 3));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateEnd() {
        assertThat(of(1, 2, 3).removeFirst(v -> v == 3)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateInner() {
        assertThat(of(1, 2, 3, 4, 5).removeFirst(v -> v == 3)).isEqualTo(of(1, 2, 4, 5));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateInnerM() {
        assertThat(of(1, 2, 3, 2, 5).removeFirst(v -> v == 2)).isEqualTo(of(1, 3, 2, 5));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateNonExisting() {
        final Seq<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.removeFirst(v -> v == 4)).isEqualTo(t);
        } else {
            assertThat(t.removeFirst(v -> v == 4)).isSameAs(t);
        }
    }

    // -- removeLast(Predicate)

    @Test
    public void shouldRemoveLastElementByPredicateFromNil() {
        assertThat(empty().removeLast(v -> true)).isEmpty();
    }

    @Test
    public void shouldRemoveLastElementByPredicateBegin() {
        assertThat(of(1, 2, 3).removeLast(v -> v == 1)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldRemoveLastElementByPredicateEnd() {
        assertThat(of(1, 2, 3).removeLast(v -> v == 3)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldRemoveLastElementByPredicateEndM() {
        assertThat(of(1, 3, 2, 3).removeLast(v -> v == 3)).isEqualTo(of(1, 3, 2));
    }

    @Test
    public void shouldRemoveLastElementByPredicateInner() {
        assertThat(of(1, 2, 3, 4, 5).removeLast(v -> v == 3)).isEqualTo(of(1, 2, 4, 5));
    }

    @Test
    public void shouldRemoveLastElementByPredicateInnerM() {
        assertThat(of(1, 2, 3, 2, 5).removeLast(v -> v == 2)).isEqualTo(of(1, 2, 3, 5));
    }

    @Test
    public void shouldRemoveLastElementByPredicateNonExisting() {
        final Seq<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.removeLast(v -> v == 4)).isEqualTo(t);
        } else {
            assertThat(t.removeLast(v -> v == 4)).isSameAs(t);
        }
    }

    // -- removeAll(Iterable)

    @Test
    public void shouldRemoveAllElementsFromNil() {
        assertThat(empty().removeAll(of(1, 2, 3))).isEmpty();
    }

    @Test
    public void shouldRemoveAllExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).removeAll(of(1, 2))).isEqualTo(of(3, 3));
    }

    @Test
    public void shouldNotRemoveAllNonExistingElementsFromNonNil() {
        final Seq<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.removeAll(of(4, 5))).isEqualTo(t);
        } else {
            assertThat(t.removeAll(of(4, 5))).isSameAs(t);
        }
    }

    // -- removeAll(Predicate)

    @Test
    public void shouldRemoveExistingElements() {
        assertThat(of(1, 2, 3).removeAll(i -> i == 1)).isEqualTo(of(2, 3));
        assertThat(of(1, 2, 3).removeAll(i -> i == 2)).isEqualTo(of(1, 3));
        assertThat(of(1, 2, 3).removeAll(i -> i == 3)).isEqualTo(of(1, 2));
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).removeAll(ignore -> true)).isEmpty();
            assertThat(of(1, 2, 3).removeAll(ignore -> false)).isEqualTo(of(1, 2, 3));
        } else {
            Seq<Integer> seq = of(1, 2, 3);
            assertThat(seq.removeAll(ignore -> false)).isSameAs(seq);
        }
    }

    @Test
    public void shouldRemoveNonExistingElements() {
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(this.<Integer> empty().removeAll(i -> i == 0)).isEqualTo(empty());
            assertThat(of(1, 2, 3).removeAll(i -> i != 0)).isEqualTo(empty());
        } else {
            assertThat(this.<Integer> empty().removeAll(i -> i == 0)).isSameAs(empty());
            assertThat(of(1, 2, 3).removeAll(i -> i != 0)).isSameAs(empty());
        }
    }

    @Test
    public void shouldRemoveAllElementsByPredicateFromNil() {
        assertThat(empty().removeAll(o -> true)).isEmpty();
    }

    @Test
    public void shouldRemoveAllExistingElements() {
        assertThat(of(1, 2, 3, 4, 5, 6).removeAll(ignored -> true)).isEmpty();
    }

    @Test
    public void shouldRemoveAllMatchedElementsFromNonNil() {
        assertThat(of(1, 2, 3, 4, 5, 6).removeAll(i -> i % 2 == 0)).isEqualTo(of(1, 3, 5));
    }

    @Test
    public void shouldNotRemoveAllNonMatchedElementsFromNonNil() {
        final Seq<Integer> t = of(1, 2, 3);
        final Predicate<Integer> isTooBig = i -> i >= 4;
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.removeAll(isTooBig)).isEqualTo(t);
        } else {
            assertThat(t.removeAll(isTooBig)).isSameAs(t);
        }
    }

    // -- removeAll(Object)

    @Test
    public void shouldRemoveAllObjectsFromNil() {
        assertThat(empty().removeAll(1)).isEmpty();
    }

    @Test
    public void shouldRemoveAllExistingObjectsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).removeAll(1)).isEqualTo(of(2, 3, 2, 3));
    }

    @Test
    public void shouldNotRemoveAllNonObjectsElementsFromNonNil() {
        final Seq<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(t.removeAll(4)).isEqualTo(t);
        } else {
            assertThat(t.removeAll(4)).isSameAs(t);
        }
    }

    // -- removeAt(index)

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndexAtNil() {
        assertThat(empty().removeAt(1)).isEmpty();
    }

    @Test
    public void shouldRemoveIndexAtNonNil() {
        assertThat(of(1, 2, 3).removeAt(1)).isEqualTo(of(1, 3));
    }

    @Test
    public void shouldRemoveIndexAtBegin() {
        assertThat(of(1, 2, 3).removeAt(0)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldRemoveIndexAtEnd() {
        assertThat(of(1, 2, 3).removeAt(2)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldRemoveMultipleTimes() {
        assertThat(of(3, 1, 4, 1, 5, 9, 2).removeAt(0).removeAt(0).removeAt(4).removeAt(3).removeAt(1)).isEqualTo(of(4, 5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndexOutOfBoundsLeft() {
        assertThat(of(1, 2, 3).removeAt(-1)).isEqualTo(of(1, 2, 3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndexOutOfBoundsRight() {
        assertThat(of(1, 2, 3).removeAt(5)).isEqualTo(of(1, 2, 3));
    }

    // -- reverse

    @Test
    public void shouldReverseNil() {
        assertThat(empty().reverse()).isEmpty();
    }

    @Test
    public void shouldReverseNonNil() {
        assertThat(of(1, 2, 3).reverse()).isEqualTo(of(3, 2, 1));
    }

    // -- reverseIterator

    @Test
    public void shouldCreateReverseIteratorOfEmpty() {
        assertThat(ofAll(empty()).reverseIterator()).isEmpty();
    }

    @Test
    public void shouldCreateReverseIteratorOfSingle() {
        assertThat(ofAll(this.of("a")).reverseIterator().toList()).isEqualTo(Iterator.of("a").toList());
    }

    @Test
    public void shouldCreateReverseIteratorOfNonEmpty() {
        assertThat(ofAll(of("a", "b", "c")).reverseIterator().toList()).isEqualTo(Iterator.of("c", "b", "a").toList());
    }

    // -- set

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithNegativeIndexOnNil() {
        empty().update(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithNegativeIndexOnNonNil() {
        of(1).update(-1, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetOnNil() {
        empty().update(0, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithIndexExceedingByOneOnNonNil() {
        of(1).update(1, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithIndexExceedingByTwoOnNonNil() {
        of(1).update(2, 2);
    }

    @Test
    public void shouldSetFirstElement() {
        assertThat(of(1, 2, 3).update(0, 4)).isEqualTo(of(4, 2, 3));
    }

    @Test
    public void shouldSetLastElement() {
        assertThat(of(1, 2, 3).update(2, 4)).isEqualTo(of(1, 2, 4));
    }

    // -- slice(beginIndex, endIndex)

    @Test
    public void shouldReturnNilWhenSliceFrom0To0OnNil() {
        final Seq<Integer> actual = this.<Integer> empty().slice(0, 0);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnNilWhenSliceFrom0To0OnNonNil() {
        final Seq<Integer> actual = of(1).slice(0, 0);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnSeqWithFirstElementWhenSliceFrom0To1OnNonNil() {
        final Seq<Integer> actual = of(1).slice(0, 1);
        assertThat(actual).isEqualTo(of(1));
    }

    @Test
    public void shouldReturnNilWhenSliceFrom1To1OnNonNil() {
        final Seq<Integer> actual = of(1).slice(1, 1);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnSliceWhenIndicesAreWithinRange() {
        final Seq<Integer> actual = of(1, 2, 3).slice(1, 3);
        assertThat(actual).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldReturnNilOnSliceWhenIndicesBothAreUpperBound() {
        final Seq<Integer> actual = of(1, 2, 3).slice(3, 3);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldComputeSliceOnNonNilWhenBeginIndexIsGreaterThanEndIndex() {
        assertThat(of(1, 2, 3).slice(1, 0)).isEmpty();
    }

    @Test
    public void shouldComputeSliceOnNilWhenBeginIndexIsGreaterThanEndIndex() {
        assertThat(empty().slice(1, 0)).isEmpty();
    }

    @Test
    public void shouldComputeSliceOnNonNilWhenBeginIndexExceedsLowerBound() {
        assertThat(of(1, 2, 3).slice(-1, 2)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldComputeSliceOnNilWhenBeginIndexExceedsLowerBound() {
        assertThat(empty().slice(-1, 2)).isEmpty();
    }

    @Test
    public void shouldThrowWhenSlice2OnNil() {
        assertThat(empty().slice(0, 1)).isEmpty();
    }

    @Test
    public void shouldComputeSliceWhenEndIndexExceedsUpperBound() {
        assertThat(of(1, 2, 3).slice(1, 4)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldComputeSliceWhenBeginIndexIsGreaterThanEndIndex() {
        assertThat(of(1, 2, 3).slice(2, 1)).isEmpty();
    }

    @Test
    public void shouldComputeSliceWhenBeginIndexAndEndIndexAreBothOutOfBounds() {
        assertThat(of(1, 2, 3).slice(-10, 10)).isEqualTo(of(1, 2, 3));
    }

    // -- sorted()

    @Test
    public void shouldSortNil() {
        assertThat(empty().sorted()).isEmpty();
    }

    @Test
    public void shouldSortNonNil() {
        assertThat(of(3, 4, 1, 2).sorted()).isEqualTo(of(1, 2, 3, 4));
    }

    // -- sorted(Comparator)

    @Test
    public void shouldSortNilUsingComparator() {
        assertThat(this.<Integer> empty().sorted((i, j) -> j - i)).isEmpty();
    }

    @Test
    public void shouldSortNonNilUsingComparator() {
        assertThat(of(3, 4, 1, 2).sorted((i, j) -> j - i)).isEqualTo(of(4, 3, 2, 1));
    }

    // -- sortBy(Function)

    @Test
    public void shouldSortByNilUsingFunction() {
        assertThat(this.<String> empty().sortBy(String::length)).isEmpty();
    }

    @Test
    public void shouldSortByNonNilUsingFunction() {
        final Seq<String> testee = of("aaa", "b", "cc");
        final Seq<String> actual = testee.sortBy(String::length);
        final Seq<String> expected = of("b", "cc", "aaa");
        assertThat(actual).isEqualTo(expected);
    }

    // -- sortBy(Comparator, Function)

    @Test
    public void shouldSortByNilUsingComparatorAndFunction() {
        assertThat(this.<String> empty().sortBy(String::length)).isEmpty();
    }

    @Test
    public void shouldSortByNonNilUsingComparatorAndFunction() {
        final Seq<String> testee = of("aaa", "b", "cc");
        final Seq<String> actual = testee.sortBy((i1, i2) -> i2 - i1, String::length);
        final Seq<String> expected = of("aaa", "cc", "b");
        assertThat(actual).isEqualTo(expected);
    }

    // -- splitAt(index)

    @Test
    public void shouldSplitAtNil() {
        assertThat(empty().splitAt(1)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitAtNonNil() {
        assertThat(of(1, 2, 3).splitAt(1)).isEqualTo(Tuple.of(of(1), of(2, 3)));
    }

    @Test
    public void shouldSplitAtBegin() {
        assertThat(of(1, 2, 3).splitAt(0)).isEqualTo(Tuple.of(empty(), of(1, 2, 3)));
    }

    @Test
    public void shouldSplitAtEnd() {
        assertThat(of(1, 2, 3).splitAt(3)).isEqualTo(Tuple.of(of(1, 2, 3), empty()));
    }

    @Test
    public void shouldSplitAtOutOfBounds() {
        assertThat(of(1, 2, 3).splitAt(5)).isEqualTo(Tuple.of(of(1, 2, 3), empty()));
        assertThat(of(1, 2, 3).splitAt(-1)).isEqualTo(Tuple.of(empty(), of(1, 2, 3)));
    }

    // -- splitAt(predicate)

    @Test
    public void shouldSplitPredicateAtNil() {
        assertThat(empty().splitAt(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitPredicateAtNonNil() {
        assertThat(of(1, 2, 3).splitAt(e -> e == 2)).isEqualTo(Tuple.of(of(1), of(2, 3)));
    }

    @Test
    public void shouldSplitAtPredicateBegin() {
        assertThat(of(1, 2, 3).splitAt(e -> e == 1)).isEqualTo(Tuple.of(empty(), of(1, 2, 3)));
    }

    @Test
    public void shouldSplitAtPredicateEnd() {
        assertThat(of(1, 2, 3).splitAt(e -> e == 3)).isEqualTo(Tuple.of(of(1, 2), of(3)));
    }

    @Test
    public void shouldSplitAtPredicateNotFound() {
        assertThat(of(1, 2, 3).splitAt(e -> e == 5)).isEqualTo(Tuple.of(of(1, 2, 3), empty()));
    }

    // -- splitAtInclusive(predicate)

    @Test
    public void shouldSplitInclusivePredicateAtNil() {
        assertThat(empty().splitAtInclusive(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitInclusivePredicateAtNonNil() {
        assertThat(of(1, 2, 3).splitAtInclusive(e -> e == 2)).isEqualTo(Tuple.of(of(1, 2), of(3)));
    }

    @Test
    public void shouldSplitAtInclusivePredicateBegin() {
        assertThat(of(1, 2, 3).splitAtInclusive(e -> e == 1)).isEqualTo(Tuple.of(of(1), of(2, 3)));
    }

    @Test
    public void shouldSplitAtInclusivePredicateEnd() {
        assertThat(of(1, 2, 3).splitAtInclusive(e -> e == 3)).isEqualTo(Tuple.of(of(1, 2, 3), empty()));
    }

    @Test
    public void shouldSplitAtInclusivePredicateNotFound() {
        assertThat(of(1, 2, 3).splitAtInclusive(e -> e == 5)).isEqualTo(Tuple.of(of(1, 2, 3), empty()));
    }

    // -- startsWith

    @Test
    public void shouldStartsNilOfNilCalculate() {
        assertThat(empty().startsWith(empty())).isTrue();
    }

    @Test
    public void shouldStartsNilOfNonNilCalculate() {
        assertThat(empty().startsWith(of(1))).isFalse();
    }

    @Test
    public void shouldStartsNilOfNilWithOffsetCalculate() {
        assertThat(empty().startsWith(empty(), 1)).isTrue();
    }

    @Test
    public void shouldStartsNilOfNonNilWithOffsetCalculate() {
        assertThat(empty().startsWith(of(1), 1)).isFalse();
    }

    @Test
    public void shouldStartsNonNilOfNilCalculate() {
        assertThat(of(1, 2, 3).startsWith(empty())).isTrue();
    }

    @Test
    public void shouldStartsNonNilOfNonNilCalculate() {
        assertThat(of(1, 2, 3).startsWith(of(1, 2))).isTrue();
        assertThat(of(1, 2, 3).startsWith(of(1, 2, 3))).isTrue();
        assertThat(of(1, 2, 3).startsWith(of(1, 2, 3, 4))).isFalse();
        assertThat(of(1, 2, 3).startsWith(of(1, 3))).isFalse();
    }

    @Test
    public void shouldStartsNonNilOfNilWithOffsetCalculate() {
        assertThat(of(1, 2, 3).startsWith(empty(), 1)).isTrue();
    }

    @Test
    public void shouldNotStartsNonNilOfNonNilWithNegativeOffsetCalculate() {
        assertThat(of(1, 2, 3).startsWith(of(1), -1)).isFalse();
    }

    @Test
    public void shouldNotStartsNonNilOfNonNilWithOffsetEqualLengthCalculate() {
        assertThat(of(1, 2, 3).startsWith(of(3), 3)).isFalse();
    }

    @Test
    public void shouldNotStartsNonNilOfNonNilWithOffsetEndCalculate() {
        assertThat(of(1, 2, 3).startsWith(of(3), 2)).isTrue();
    }

    @Test
    public void shouldStartsNonNilOfNonNilWithOffsetAtStartCalculate() {
        assertThat(of(1, 2, 3).startsWith(of(1), 0)).isTrue();
    }

    @Test
    public void shouldStartsNonNilOfNonNilWithOffsetCalculate1() {
        assertThat(of(1, 2, 3).startsWith(of(2, 3), 1)).isTrue();
    }

    @Test
    public void shouldStartsNonNilOfNonNilWithOffsetCalculate2() {
        assertThat(of(1, 2, 3).startsWith(of(2, 3, 4), 1)).isFalse();
    }

    @Test
    public void shouldStartsNonNilOfNonNilWithOffsetCalculate3() {
        assertThat(of(1, 2, 3).startsWith(of(2, 4), 1)).isFalse();
    }

    // -- subSequence(beginIndex)

    @Test
    public void shouldReturnNilWhenSubSequenceFrom0OnNil() {
        final Seq<Integer> actual = this.<Integer> empty().subSequence(0);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnIdentityWhenSubSequenceFrom0OnNonNil() {
        final Seq<Integer> actual = of(1).subSequence(0);
        assertThat(actual).isEqualTo(of(1));
    }

    @Test
    public void shouldReturnNilWhenSubSequenceFrom1OnSeqOf1() {
        final Seq<Integer> actual = of(1).subSequence(1);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnSubSequenceWhenIndexIsWithinRange() {
        final Seq<Integer> actual = of(1, 2, 3).subSequence(1);
        assertThat(actual).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldReturnNilWhenSubSequenceBeginningWithSize() {
        final Seq<Integer> actual = of(1, 2, 3).subSequence(3);
        assertThat(actual).isEmpty();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubSequenceOnNil() {
        empty().subSequence(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubSequenceWithOutOfLowerBound() {
        of(1, 2, 3).subSequence(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubSequenceWithOutOfUpperBound() {
        of(1, 2, 3).subSequence(4);
    }

    // -- subSequence(beginIndex, endIndex)

    @Test
    public void shouldReturnNilWhenSubSequenceFrom0To0OnNil() {
        final Seq<Integer> actual = this.<Integer> empty().subSequence(0, 0);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnNilWhenSubSequenceFrom0To0OnNonNil() {
        final Seq<Integer> actual = of(1).subSequence(0, 0);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnSeqWithFirstElementWhenSubSequenceFrom0To1OnNonNil() {
        final Seq<Integer> actual = of(1).subSequence(0, 1);
        assertThat(actual).isEqualTo(of(1));
    }

    @Test
    public void shouldReturnNilWhenSubSequenceFrom1To1OnNonNil() {
        final Seq<Integer> actual = of(1).subSequence(1, 1);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnSubSequenceWhenIndicesAreWithinRange() {
        final Seq<Integer> actual = of(1, 2, 3).subSequence(1, 3);
        assertThat(actual).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldReturnNilWhenOnSubSequenceIndicesBothAreUpperBound() {
        final Seq<Integer> actual = of(1, 2, 3).subSequence(3, 3);
        assertThat(actual).isEmpty();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceOnNonNilWhenBeginIndexIsGreaterThanEndIndex() {
        of(1, 2, 3).subSequence(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceOnNilWhenBeginIndexIsGreaterThanEndIndex() {
        empty().subSequence(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceOnNonNilWhenBeginIndexExceedsLowerBound() {
        of(1, 2, 3).subSequence(-1, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceOnNilWhenBeginIndexExceedsLowerBound() {
        empty().subSequence(-1, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubSequence2OnNil() {
        empty().subSequence(0, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceWhenEndIndexExceedsUpperBound() {
        of(1, 2, 3).subSequence(1, 4).mkString(); // force computation of last element, e.g. because Stream is lazy
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubSequenceWhenBeginIndexIsGreaterThanEndIndex() {
        of(1, 2, 3).subSequence(2, 1).mkString(); // force computation of last element, e.g. because Stream is lazy
    }

    // -- search(element)

    @Test
    public void shouldSearchIndexForPresentElements() {
        assertThat(of(1, 2, 3, 4, 5, 6).search(3)).isEqualTo(2);
    }

    @Test
    public void shouldSearchNegatedInsertionPointMinusOneForAbsentElements() {
        assertThat(empty().search(42)).isEqualTo(-1);
        assertThat(of(10, 20, 30).search(25)).isEqualTo(-3);
    }

    // -- search(element,comparator)

    @Test
    public void shouldSearchIndexForPresentElementsUsingComparator() {
        assertThat(of(1, 2, 3, 4, 5, 6).search(3, Integer::compareTo)).isEqualTo(2);
    }

    @Test
    public void shouldSearchNegatedInsertionPointMinusOneForAbsentElementsUsingComparator() {
        assertThat(this.<Integer> empty().search(42, Integer::compareTo)).isEqualTo(-1);
        assertThat(of(10, 20, 30).search(25, Integer::compareTo)).isEqualTo(-3);
    }

    // -- IndexedSeq special cases

    @Test
    public void shouldTestIndexedSeqStartsWithNonIndexedSeq() {
        assertThat(of(1, 3, 4).startsWith(Stream.of(1, 3))).isTrue();
        assertThat(of(1, 2, 3, 4).startsWith(Stream.of(1, 2, 4))).isFalse();
        assertThat(of(1, 2).startsWith(Stream.of(1, 2, 4))).isFalse();
    }

    @Test
    public void shouldTestIndexedSeqEndsWithNonIndexedSeq() {
        assertThat(of(1, 3, 4).endsWith(Stream.of(3, 4))).isTrue();
        assertThat(of(1, 2, 3, 4).endsWith(Stream.of(2, 3, 5))).isFalse();
    }

    @Test
    public void lift() {
        final Function1<Integer, Option<String>> lifted = of("a", "b", "c").lift();
        assertThat(lifted.apply(1).get()).isEqualTo("b");
        assertThat(lifted.apply(-1).isEmpty()).isTrue();
        assertThat(lifted.apply(3).isEmpty()).isTrue();
    }

    @Test
    public void withDefaultValue() {
        final Function1<Integer, String> withDef = of("a", "b", "c").withDefaultValue("z");
        assertThat(withDef.apply(2)).isEqualTo("c");
        assertThat(withDef.apply(-1)).isEqualTo("z");
        assertThat(withDef.apply(3)).isEqualTo("z");
    }

    @Test
    public void withDefault() {
        final Function1<Integer, String> withDef = of("a", "b", "c").withDefault(Object::toString);
        assertThat(withDef.apply(2)).isEqualTo("c");
        assertThat(withDef.apply(-1)).isEqualTo("-1");
        assertThat(withDef.apply(3)).isEqualTo("3");
    }
}
