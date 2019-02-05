/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.test;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Stream;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class GenTest {

    // equally distributed random number generator
    static final Random RANDOM = new Random();

    // number of tries to assert a property
    static final int TRIES = 1000;

    // -- of

    @Test
    public void shouldIntersperseMultipleGeneratos() throws Exception {
        Gen<Integer> gen = Gen.of(0).intersperse(Gen.of(1));
        assertThat(gen.apply(RANDOM)).isEqualTo(0);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
        assertThat(gen.apply(RANDOM)).isEqualTo(0);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
    }
    @Test
    public void shouldCreateConstantGenOfElement() {
        final Gen<Integer> gen = Gen.of(1);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
    }

    @Test
    public void shouldCreateGenOfSeedAndFunction() {
        final Gen<Integer> gen = Gen.of(1, i -> i + 1);
        assertThat(gen.apply(RANDOM)).isEqualTo(1);
        assertThat(gen.apply(RANDOM)).isEqualTo(2);
        assertThat(gen.apply(RANDOM)).isEqualTo(3);
    }

    // -- random number generator (rng)

    @Test
    public void shouldUseCustomRandomNumberGenerator() {
        final Random rng = new Random() {
            private static final long serialVersionUID = 1L;

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        final Gen<Integer> gen = Gen.choose(1, 2);
        final Number actual = Stream.continually(() -> gen.apply(rng)).take(10).sum();
        assertThat(actual).isEqualTo(10L);
    }

    // -- choose(int, int)

    @Test
    public void shouldChooseIntBetweenMinMax() {
        assertForAll(() -> Gen.choose(-1, 1).apply(RANDOM), i -> i >= -1 && i <= 1);
    }

    @Test
    public void shouldChooseIntWhenMinEqualsMax() {
        assertForAll(() -> Gen.choose(0, 0).apply(RANDOM), i -> i == 0);
    }

    // -- choose(long, long)

    @Test
    public void shouldChooseLongBetweenMinMax() {
        assertForAll(() -> Gen.choose(-1L, 1L).apply(RANDOM), l -> l >= -1L && l <= 1L);
    }

    @Test
    public void shouldChooseLongWhenMinEqualsMax() {
        assertForAll(() -> Gen.choose(0L, 0L).apply(RANDOM), l -> l == 0L);
    }

    // -- choose(double, double)

    @Test
    public void shouldChooseDoubleBetweenMinMax() {
        assertForAll(() -> Gen.choose(-1.0d, 1.0d).apply(RANDOM), d -> d >= -1.0d && d <= 1.0d);
    }

    @Test
    public void shouldChooseDoubleWhenMinEqualsMax() {
        assertForAll(() -> Gen.choose(0.0d, 0.0d).apply(RANDOM), d -> d == 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMinIsNegativeInfinite() {
        Gen.choose(Double.NEGATIVE_INFINITY, 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMinIsPositiveInfinite() {
        Gen.choose(Double.POSITIVE_INFINITY, 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMinIsNotANumber() {
        Gen.choose(Double.NaN, 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMaxIsNegativeInfinite() {
        Gen.choose(0.0d, Double.NEGATIVE_INFINITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMaxIsPositiveInfinite() {
        Gen.choose(0.0d, Double.POSITIVE_INFINITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenChooseDoubleAndMaxIsNotANumber() {
        Gen.choose(0.0d, Double.NaN);
    }

    // -- choose(char, char)

    @Test
    public void shouldChooseCharBetweenMinMax() {
        assertForAll(() -> Gen.choose('A', 'Z').apply(RANDOM), c -> c >= 'A' && c <= 'Z');
    }

    @Test
    public void shouldChooseCharWhenMinEqualsMax() {
        assertForAll(() -> Gen.choose('λ', 'λ').apply(RANDOM), c -> c == 'λ');
    }

    // -- choose(array)

    @Test
    public void shouldChooseFromASingleArray() throws Exception {
        Integer[] i = {1};
        assertForAll(() -> Gen.choose(i).apply(RANDOM), c -> c == 1);
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailOnEmptyArray() throws Exception {
        Integer[] i = {};
        Gen.choose(i).apply(RANDOM);
    }

    // -- Choose(enum)

    enum testEnum {
        value1
    }

    @Test
    public void shouldChooseFromEnum() throws Exception {
        assertForAll(() -> Gen.choose(testEnum.class).apply(RANDOM), e -> Arrays.asList(testEnum.values()).contains(e));
    }

    // -- Choose(iterable)
    @Test
    public void shouldChooseFromIterable() throws Exception {
        List<Integer> i = List.of(1);
        assertForAll(() -> Gen.choose(i).apply(RANDOM), c -> c == 1);
    }


    @Test(expected = RuntimeException.class)
    public void shouldFailOnEmptyIterable() throws Exception {
        List<Integer> i = List.empty();
        Gen.choose(i).apply(RANDOM);
    }

    // -- fail

    @Test(expected = RuntimeException.class)
    public void shouldFailAlwaysWhenCallingFailingGen() {
        Gen.fail().apply(RANDOM);
    }

    // -- frequency(VarArgs)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingFrequencyOfVarArgsAndArgIsNull() {
        Gen.frequency((Tuple2<Integer, Gen<Object>>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCallingFrequencyOfVarArgsAndArgIsEmpty() {
        @SuppressWarnings("unchecked")
        final Tuple2<Integer, Gen<Object>>[] empty = (Tuple2<Integer, Gen<Object>>[]) new Tuple2<?, ?>[0];
        Gen.frequency(empty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCallingFrequencyOfVarArgsAndFrequenceIsNegative() {
        final Gen<Integer> gen = Gen.frequency(Tuple.of(-1, Gen.of(-1)));
        gen.apply(RANDOM);
    }

    @Test
    public void shouldGenerateElementsAccordingToFrequencyGivenVarArgs() {
        final Gen<Integer> gen = Gen.frequency(Tuple.of(0, Gen.of(-1)), Tuple.of(1, Gen.of(1)));
        assertForAll(() -> gen.apply(RANDOM), i -> i != -1);
    }

    // -- frequency(Iterable)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingFrequencyOfIterableAndArgIsNull() {
        Gen.frequency((Iterable<Tuple2<Integer, Gen<Object>>>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCallingFrequencyOfIterableAndArgIsEmpty() {
        Gen.frequency(List.empty());
    }

    @Test
    public void shouldGenerateElementsAccordingToFrequencyGivenAnIterable() {
        final Gen<Integer> gen = Gen.frequency(List.of(Tuple.of(0, Gen.of(-1)), Tuple.of(1, Gen.of(1))));
        assertForAll(() -> gen.apply(RANDOM), i -> i != -1);
    }

    // -- oneOf(VarArgs)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingOneOfAndVarArgsIsNull() {
        Gen.oneOf((Gen<Object>[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCallingOneOfAndVarArgsIsEmpty() {
        @SuppressWarnings("unchecked")
        final Gen<Object>[] empty = (Gen<Object>[]) new Gen<?>[0];
        Gen.oneOf(empty);
    }

    @Test
    public void shouldReturnOneOfGivenVarArgs() {
        final Gen<Integer> gen = Gen.oneOf(Gen.of(1), Gen.of(2));
        assertForAll(() -> gen.apply(RANDOM), i -> i == 1 || i == 2);
    }

    // -- oneOf(Iterable)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingOneOfAndIterableIsNull() {
        Gen.oneOf((Iterable<Gen<Object>>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCallingOneOfAndIterableIsEmpty() {
        Gen.oneOf(List.empty());
    }

    @Test
    public void shouldReturnOneOfGivenIterable() {
        final Gen<Integer> gen = Gen.oneOf(List.of(Gen.of(1), Gen.of(2)));
        assertForAll(() -> gen.apply(RANDOM), i -> i == 1 || i == 2);
    }

    // -- arbitrary

    @Test
    public void shouldConvertGenToArbitrary() {
        assertThat(Gen.of(1).arbitrary()).isInstanceOf(Arbitrary.class);
    }

    // -- map

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingMapWithNullArg() {
        Gen.of(1).map(null);
    }

    @Test
    public void shouldMapGen() {
        final Gen<Integer> gen = Gen.of(1).map(i -> i * 2);
        assertForAll(() -> gen.apply(RANDOM), i -> i % 2 == 0);
    }

    // -- flatMap

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingFlatMapWithNullArg() {
        Gen.of(1).flatMap(null);
    }

    @Test
    public void shouldFlatMapGen() {
        final Gen<Integer> gen = Gen.of(1).flatMap(i -> Gen.of(i * 2));
        assertForAll(() -> gen.apply(RANDOM), i -> i % 2 == 0);
    }

    // -- filter

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCallingFilterWithNullArg() {
        Gen.of(1).filter(null);
    }

    @Test
    public void shouldFilterGenerator() {
        final Gen<Integer> gen = Gen.choose(1, 2).filter(i -> i % 2 == 0);
        assertForAll(() -> gen.apply(RANDOM), i -> i == 2);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldDetectEmptyFilter() {
        Gen.of(1).filter(ignored -> false).apply(RANDOM);
    }

    // -- peek

    @Test
    public void shouldPeekArbitrary() {
        final int[] actual = new int[] { -1 };
        final int expected = Gen.of(1).peek(i -> actual[0] = i).apply(new Random());
        assertThat(actual[0]).isEqualTo(expected);
    }

    // -- transform

    @Test
    public void shouldTransformGen() {
        final String s = Gen.of(1).transform(gen -> gen.apply(RANDOM).toString());
        assertThat(s).isEqualTo("1");
    }

    // helpers

    <T> void assertForAll(Supplier<T> supplier, Predicate<T> property) {
        for (int i = 0; i < TRIES; i++) {
            final T element = supplier.get();
            if (!property.test(element)) {
                throw new AssertionError("predicate did not hold for " + element);
            }
        }
    }
}
