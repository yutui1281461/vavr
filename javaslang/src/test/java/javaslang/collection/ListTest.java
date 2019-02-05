/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Serializables;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Value;
import javaslang.control.Option;
import org.junit.Test;

import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;
import java.util.stream.Stream;

public class ListTest extends AbstractLinearSeqTest {

    // -- construction

    @Override
    protected <T> Collector<T, ArrayList<T>, List<T>> collector() {
        return List.collector();
    }

    @Override
    protected <T> List<T> empty() {
        return List.empty();
    }

    @Override
    protected <T> List<T> of(T element) {
        return List.of(element);
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    @Override
    protected final <T> List<T> of(T... elements) {
        return List.of(elements);
    }

    @Override
    protected <T> List<T> ofAll(Iterable<? extends T> elements) {
        return List.ofAll(elements);
    }

    @Override
    protected <T> List<T> ofJavaStream(Stream<? extends T> javaStream) {
        return List.ofAll(javaStream);
    }

    @Override
    protected List<Boolean> ofAll(boolean[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Byte> ofAll(byte[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Character> ofAll(char[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Double> ofAll(double[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Float> ofAll(float[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Integer> ofAll(int[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Long> ofAll(long[] array) {
        return List.ofAll(array);
    }

    @Override
    protected List<Short> ofAll(short[] array) {
        return List.ofAll(array);
    }

    @Override
    protected <T> List<T> tabulate(int n, Function<? super Integer, ? extends T> f) {
        return List.tabulate(n, f);
    }

    @Override
    protected <T> List<T> fill(int n, Supplier<? extends T> s) {
        return List.fill(n, s);
    }

    @Override
    protected List<Character> range(char from, char toExclusive) {
        return List.range(from, toExclusive);
    }

    @Override
    protected List<Character> rangeBy(char from, char toExclusive, int step) {
        return List.rangeBy(from, toExclusive, step);
    }

    @Override
    protected List<Double> rangeBy(double from, double toExclusive, double step) {
        return List.rangeBy(from, toExclusive, step);
    }

    @Override
    protected List<Integer> range(int from, int toExclusive) {
        return List.range(from, toExclusive);
    }

    @Override
    protected List<Integer> rangeBy(int from, int toExclusive, int step) {
        return List.rangeBy(from, toExclusive, step);
    }

    @Override
    protected List<Long> range(long from, long toExclusive) {
        return List.range(from, toExclusive);
    }

    @Override
    protected List<Long> rangeBy(long from, long toExclusive, long step) {
        return List.rangeBy(from, toExclusive, step);
    }

    @Override
    protected List<Character> rangeClosed(char from, char toInclusive) {
        return List.rangeClosed(from, toInclusive);
    }

    @Override
    protected List<Character> rangeClosedBy(char from, char toInclusive, int step) {
        return List.rangeClosedBy(from, toInclusive, step);
    }

    @Override
    protected List<Double> rangeClosedBy(double from, double toInclusive, double step) {
        return List.rangeClosedBy(from, toInclusive, step);
    }

    @Override
    protected List<Integer> rangeClosed(int from, int toInclusive) {
        return List.rangeClosed(from, toInclusive);
    }

    @Override
    protected List<Integer> rangeClosedBy(int from, int toInclusive, int step) {
        return List.rangeClosedBy(from, toInclusive, step);
    }

    @Override
    protected List<Long> rangeClosed(long from, long toInclusive) {
        return List.rangeClosed(from, toInclusive);
    }

    @Override
    protected List<Long> rangeClosedBy(long from, long toInclusive, long step) {
        return List.rangeClosedBy(from, toInclusive, step);
    }

    @Override
    protected int getPeekNonNilPerformingAnAction() {
        return 1;
    }

    // -- static narrow

    @Test
    public void shouldNarrowList() {
        final List<Double> doubles = of(1.0d);
        final List<Number> numbers = List.narrow(doubles);
        final int actual = numbers.append(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- ofAll(NavigableSet)

    @Test
    public void shouldAcceptNavigableSet() {
        java.util.TreeSet<Integer> javaSet = new java.util.TreeSet<>();
        javaSet.add(2);
        javaSet.add(1);
        assertThat(List.ofAll(javaSet)).isEqualTo(List.of(1, 2));
    }

    // -- peek

    @Test(expected = NoSuchElementException.class)
    public void shouldFailPeekOfNil() {
        empty().peek();
    }

    @Test
    public void shouldPeekOfNonNil() {
        assertThat(of(1).peek()).isEqualTo(1);
        assertThat(of(1, 2).peek()).isEqualTo(1);
    }

    // -- peekOption

    @Test
    public void shouldPeekOption() {
        assertThat(empty().peekOption()).isSameAs(Option.none());
        assertThat(of(1).peekOption()).isEqualTo(Option.of(1));
        assertThat(of(1, 2).peekOption()).isEqualTo(Option.of(1));
    }

    // -- pop

    @Test(expected = NoSuchElementException.class)
    public void shouldFailPopOfNil() {
        empty().pop();
    }

    @Test
    public void shouldPopOfNonNil() {
        assertThat(of(1).pop()).isSameAs(empty());
        assertThat(of(1, 2).pop()).isEqualTo(of(2));
    }

    // -- popOption

    @Test
    public void shouldPopOption() {
        assertThat(empty().popOption()).isSameAs(Option.none());
        assertThat(of(1).popOption()).isEqualTo(Option.of(empty()));
        assertThat(of(1, 2).popOption()).isEqualTo(Option.of(of(2)));
    }

    // -- pop2

    @Test(expected = NoSuchElementException.class)
    public void shouldFailPop2OfNil() {
        empty().pop2();
    }

    @Test
    public void shouldPop2OfNonNil() {
        assertThat(of(1).pop2()).isEqualTo(Tuple.of(1, empty()));
        assertThat(of(1, 2).pop2()).isEqualTo(Tuple.of(1, of(2)));
    }

    // -- pop2Option

    @Test
    public void shouldPop2Option() {
        assertThat(empty().pop2Option()).isSameAs(Option.none());
        assertThat(of(1).pop2Option()).isEqualTo(Option.of(Tuple.of(1, empty())));
        assertThat(of(1, 2).pop2Option()).isEqualTo(Option.of(Tuple.of(1, of(2))));
    }

    // -- push

    @Test
    public void shouldPushElements() {
        assertThat(empty().push(1)).isEqualTo(of(1));
        assertThat(empty().push(1, 2, 3)).isEqualTo(of(3, 2, 1));
        assertThat(empty().pushAll(of(1, 2, 3))).isEqualTo(of(3, 2, 1));
        assertThat(of(0).push(1)).isEqualTo(of(1, 0));
        assertThat(of(0).push(1, 2, 3)).isEqualTo(of(3, 2, 1, 0));
        assertThat(of(0).pushAll(of(1, 2, 3))).isEqualTo(of(3, 2, 1, 0));
    }

    // -- transform()

    @Test
    public void shouldTransform() {
        String transformed = of(42).transform(v -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    // -- toString

    @Test
    public void shouldStringifyNil() {
        assertThat(empty().toString()).isEqualTo("List()");
    }

    @Test
    public void shouldStringifyNonNil() {
        assertThat(of(1, 2, 3).toString()).isEqualTo("List(1, 2, 3)");
    }

    // -- unfold

    @Test
    public void shouldUnfoldRightToEmpty() {
        assertThat(List.unfoldRight(0, x -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldRightSimpleList() {
        assertThat(
            List.unfoldRight(10, x -> x == 0
                             ? Option.none()
                             : Option.of(new Tuple2<>(x, x-1))))
            .isEqualTo(of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }

    @Test
    public void shouldUnfoldLeftToEmpty() {
        assertThat(List.unfoldLeft(0, x -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldLeftSimpleList() {
        assertThat(
            List.unfoldLeft(10, x -> x == 0
                            ? Option.none()
                            : Option.of(new Tuple2<>(x-1, x))))
            .isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void shouldUnfoldToEmpty() {
        assertThat(List.unfold(0, x -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldSimpleList() {
        assertThat(
            List.unfold(10, x -> x == 0
                            ? Option.none()
                            : Option.of(new Tuple2<>(x-1, x))))
            .isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    // -- Cons test

    @Test(expected = InvalidObjectException.class)
    public void shouldNotSerializeEnclosingClass() throws Throwable {
        Serializables.callReadObject(List.of(1));
    }

    @Test(expected = InvalidObjectException.class)
    public void shouldNotDeserializeListWithSizeLessThanOne() throws Throwable {
        try {
            /*
             * This implementation is stable regarding jvm impl changes of object serialization. The index of the number
             * of List elements is gathered dynamically.
             */
            final byte[] listWithOneElement = Serializables.serialize(List.of(0));
            final byte[] listWithTwoElements = Serializables.serialize(List.of(0, 0));
            int index = -1;
            for (int i = 0; i < listWithOneElement.length && index == -1; i++) {
                final byte b1 = listWithOneElement[i];
                final byte b2 = listWithTwoElements[i];
                if (b1 != b2) {
                    if (b1 != 1 || b2 != 2) {
                        throw new IllegalStateException("Difference does not indicate number of elements.");
                    } else {
                        index = i;
                    }
                }
            }
            if (index == -1) {
                throw new IllegalStateException("Hack incomplete - index not found");
            }
            /*
             * Hack the serialized data and fake zero elements.
             */
            listWithOneElement[index] = 0;
            Serializables.deserialize(listWithOneElement);
        } catch (IllegalStateException x) {
            throw (x.getCause() != null) ? x.getCause() : x;
        }
    }

    @Override
    protected boolean useIsEqualToInsteadOfIsSameAs() {
        return false;
    }

    // -- toList

    @Test
    public void shouldReturnSelfOnConvertToList() {
        Value<Integer> value = of(1, 2, 3);
        assertThat(value.toList()).isSameAs(value);
    }
}
