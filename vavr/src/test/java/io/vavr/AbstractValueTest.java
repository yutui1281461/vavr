/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2018 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr;

import io.vavr.collection.*;
import io.vavr.control.Either;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import io.vavr.collection.Array;
import io.vavr.collection.CharSeq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import org.assertj.core.api.*;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.vavr.API.*;
import static io.vavr.Predicates.anyOf;
import static io.vavr.Predicates.instanceOf;

public abstract class AbstractValueTest {

    protected Random getRandom(int seed) {
        if (seed >= 0) {
            return new Random(seed);
        } else {
            final Random random = new Random();
            seed = random.nextInt();
            System.out.println("using seed: " + seed);
            random.setSeed(seed);
            return random;
        }
    }

    protected <T> IterableAssert<T> assertThat(Iterable<T> actual) {
        return new IterableAssert<T>(actual) {};
    }

    protected <T> ObjectAssert<T> assertThat(T actual) {
        return new ObjectAssert<T>(actual) {};
    }

    protected <T> ObjectArrayAssert<T> assertThat(T[] actual) {
        return new ObjectArrayAssert<T>(actual) {};
    }

    protected BooleanAssert assertThat(Boolean actual) {
        return new BooleanAssert(actual) {};
    }

    protected DoubleAssert assertThat(Double actual) {
        return new DoubleAssert(actual) {};
    }

    protected IntegerAssert assertThat(Integer actual) {
        return new IntegerAssert(actual) {};
    }

    protected LongAssert assertThat(Long actual) {
        return new LongAssert(actual) {};
    }

    protected StringAssert assertThat(String actual) {
        return new StringAssert(actual) {};
    }

    abstract protected <T> Value<T> empty();

    abstract protected <T> Value<T> of(T element);

    @SuppressWarnings("unchecked")
    abstract protected <T> Value<T> of(T... elements);

    // TODO: Eliminate this method. Switching the behavior of unit tests is evil. Tests should not contain additional logic. Also it seems currently to be used in different sematic contexts.
    abstract protected boolean useIsEqualToInsteadOfIsSameAs();

    // returns the peek result of the specific Traversable implementation
    abstract protected int getPeekNonNilPerformingAnAction();

    // -- get()

    @Test(expected = RuntimeException.class)
    public void shouldGetEmpty() {
        empty().get();
    }

    @Test
    public void shouldGetNonEmpty() {
        assertThat(of(1).get()).isEqualTo(1);
    }
    
    // -- getOrElse(T)

    @Test
    public void shouldCalculateGetOrElseWithNull() {
        assertThat(this.<Integer> empty().getOrElse((Integer) null)).isEqualTo(null);
        assertThat(of(1).getOrElse((Integer) null)).isEqualTo(1);
    }

    @Test
    public void shouldCalculateGetOrElseWithNonNull() {
        assertThat(empty().getOrElse(1)).isEqualTo(1);
        assertThat(of(1).getOrElse(2)).isEqualTo(1);
    }

    // -- getOrElse(Supplier)

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnGetOrElseWithNullSupplier() {
        final Supplier<?> supplier = null;
        empty().getOrElse(supplier);
    }

    @Test
    public void shouldCalculateGetOrElseWithSupplier() {
        assertThat(empty().getOrElse(() -> 1)).isEqualTo(1);
        assertThat(of(1).getOrElse(() -> 2)).isEqualTo(1);
    }

    // -- getOrElseThrow

    @Test(expected = ArithmeticException.class)
    public void shouldThrowOnGetOrElseThrowIfEmpty() {
        empty().getOrElseThrow(ArithmeticException::new);
    }

    @Test
    public void shouldNotThrowOnGetOrElseThrowIfNonEmpty() {
        assertThat(of(1).getOrElseThrow(ArithmeticException::new)).isEqualTo(1);
    }

    // -- getOrElseTry

    @Test
    public void shouldReturnUnderlyingValueWhenCallingGetOrElseTryOnNonEmptyValue() {
        assertThat(of(1).getOrElseTry(() -> 2)).isEqualTo(1);
    }

    @Test
    public void shouldReturnAlternateValueWhenCallingGetOrElseTryOnEmptyValue() {
        assertThat(empty().getOrElseTry(() -> 2)).isEqualTo(2);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowWhenCallingGetOrElseTryOnEmptyValueAndTryIsAFailure() {
        empty().getOrElseTry(() -> {
            throw new Error();
        });
    }

    // -- getOrNull

    @Test
    public void shouldReturnNullWhenGetOrNullOfEmpty() {
        assertThat(empty().getOrNull()).isEqualTo(null);
    }

    @Test
    public void shouldReturnValueWhenGetOrNullOfNonEmpty() {
        assertThat(of(1).getOrNull()).isEqualTo(1);
    }

    // -- forEach

    @Test
    public void shouldPerformsActionOnEachElement() {
        final int[] consumer = new int[1];
        final Value<Integer> value = of(1, 2, 3);
        value.forEach(i -> consumer[0] += i);
        assertThat(consumer[0]).isEqualTo(value.isSingleValued() ? 1 : 6);
    }

    // -- isAsync

    @Test
    public void shouldVerifyAsyncProperty() {
        assertThat(empty().isAsync()).isFalse();
        assertThat(of(1).isAsync()).isFalse();
    }

    // -- isEmpty

    @Test
    public void shouldCalculateIsEmpty() {
        assertThat(empty().isEmpty()).isTrue();
        assertThat(of(1).isEmpty()).isFalse();
    }

    // -- isLazy

    @Test
    public void shouldVerifyLazyProperty() {
        assertThat(empty().isLazy()).isFalse();
        assertThat(of(1).isLazy()).isFalse();
    }

    // -- Conversions toXxx()

    @Test
    public void shouldConvertToArray() {
        final Value<Integer> value = of(1, 2, 3);
        final Array<Integer> array = value.toArray();
        if (value.isSingleValued()) {
            assertThat(array).isEqualTo(Array.of(1));
        } else {
            assertThat(array).isEqualTo(Array.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToCharSeq() {
        final Value<Integer> value = of(1, 2, 3);
        final CharSeq charSeq = value.toCharSeq();
        final CharSeq expected = CharSeq.of(of(1, 2, 3).iterator().mkString());
        assertThat(charSeq).isEqualTo(expected);
    }

    @Test
    public void shouldConvertToList() {
        final Value<Integer> value = of(1, 2, 3);
        final io.vavr.collection.List<Integer> list = value.toList();
        if (value.isSingleValued()) {
            assertThat(list).isEqualTo(io.vavr.collection.List.of(1));
        } else {
            assertThat(list).isEqualTo(io.vavr.collection.List.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToHashMap() {
        final Value<Integer> value = of(9, 5, 1);
        final io.vavr.collection.Map<Integer, Integer> map = value.toMap(i -> Tuple.of(i, i));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.HashMap.of(9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.HashMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToHashMapTwoFunctions() {
        final Value<Integer> value = of(9, 5, 1);
        final io.vavr.collection.Map<Integer, Integer> map = value.toMap(Function.identity(), Function.identity());
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.HashMap.of(9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.HashMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToLinkedMap() {
        final Value<Integer> value = of(1, 5, 9);
        final io.vavr.collection.Map<Integer, Integer> map = value.toLinkedMap(i -> Tuple.of(i, i));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.LinkedHashMap.of(1, 1));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.LinkedHashMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToLinkedMapTwoFunctions() {
        final Value<Integer> value = of(1, 5, 9);
        final io.vavr.collection.Map<Integer, Integer> map = value.toLinkedMap(Function.identity(), Function.identity());
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.LinkedHashMap.of(1, 1));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.LinkedHashMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToSortedMap() {
        final Value<Integer> value = of(9, 5, 1);
        final io.vavr.collection.SortedMap<Integer, Integer> map = value.toSortedMap(i -> Tuple.of(i, i));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToSortedMapTwoFunctions() {
        final Value<Integer> value = of(9, 5, 1);
        final io.vavr.collection.SortedMap<Integer, Integer> map = value.toSortedMap(Function.identity(), Function.identity());
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(1, 1, 5, 5, 9, 9));
        }
    }

    @Test
    public void shouldConvertToSortedMapWithComparator() {
        final Value<Integer> value = of(9, 5, 1);
        final Comparator<Integer> comparator = ((Comparator<Integer>) Integer::compareTo).reversed();
        final io.vavr.collection.SortedMap<Integer, Integer> map = value.toSortedMap(comparator, i -> Tuple.of(i, i));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(comparator, 9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(comparator, 9, 9, 5, 5, 1, 1));
        }
    }

    @Test
    public void shouldConvertToSortedMapTwoFunctionsWithComparator() {
        final Value<Integer> value = of(9, 5, 1);
        final Comparator<Integer> comparator = ((Comparator<Integer>) Integer::compareTo).reversed();
        final io.vavr.collection.SortedMap<Integer, Integer> map = value.toSortedMap(comparator, Function.identity(), Function.identity());
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(comparator, 9, 9));
        } else {
            assertThat(map).isEqualTo(io.vavr.collection.TreeMap.of(comparator, 9, 9, 5, 5, 1, 1));
        }
    }

    @Test
    public void shouldConvertToQueue() {
        final Value<Integer> value = of(1, 2, 3);
        final io.vavr.collection.Queue<Integer> queue = value.toQueue();
        if (value.isSingleValued()) {
            assertThat(queue).isEqualTo(io.vavr.collection.Queue.of(1));
        } else {
            assertThat(queue).isEqualTo(io.vavr.collection.Queue.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToPriorityQueueUsingImplicitComparator() {
        final Value<Integer> value = of(1, 3, 2);
        final io.vavr.collection.PriorityQueue<Integer> queue = value.toPriorityQueue();
        if (value.isSingleValued()) {
            assertThat(queue).isEqualTo(io.vavr.collection.PriorityQueue.of(1));
        } else {
            assertThat(queue).isEqualTo(io.vavr.collection.PriorityQueue.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToPriorityQueueUsingExplicitComparator() {
        final Comparator<Integer> comparator = Comparator.naturalOrder();
        final Value<Integer> value = of(1, 3, 2);
        final io.vavr.collection.PriorityQueue<Integer> queue = value.toPriorityQueue(comparator);
        if (value.isSingleValued()) {
            assertThat(queue).isEqualTo(io.vavr.collection.PriorityQueue.of(comparator, 1));
        } else {
            assertThat(queue).isEqualTo(io.vavr.collection.PriorityQueue.of(comparator, 1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToPriorityQueueUsingSerializableComparator() {
        final Value<Integer> value = of(1, 3, 2);
        final io.vavr.collection.PriorityQueue<Integer> queue = value.toPriorityQueue();
        final io.vavr.collection.PriorityQueue<Integer> actual = Serializables.deserialize(Serializables.serialize(queue));
        assertThat(actual).isEqualTo(queue);
    }

    @Test
    public void shouldConvertToSet() {
        final Value<Integer> value = of(1, 2, 3);
        final io.vavr.collection.Set<Integer> set = value.toSet();
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(io.vavr.collection.HashSet.of(1));
        } else {
            assertThat(set).isEqualTo(io.vavr.collection.HashSet.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToLinkedSet() {
        final Value<Integer> value = of(3, 7, 1, 15, 0);
        final io.vavr.collection.Set<Integer> set = value.toLinkedSet();
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(io.vavr.collection.LinkedHashSet.of(3));
        } else {
            final io.vavr.collection.List<Integer> itemsInOrder;
            if (value instanceof Traversable && !((Traversable) value).isTraversableAgain()) {
                itemsInOrder = io.vavr.collection.List.of(3, 7, 1, 15, 0);
            } else {
                itemsInOrder = value.toList();
            }
            assertThat(set).isEqualTo(itemsInOrder.foldLeft(io.vavr.collection.LinkedHashSet.empty(), io.vavr.collection.LinkedHashSet::add));
        }
    }

    @Test
    public void shouldConvertToSortedSetWithoutComparatorOnComparable() {
        final Value<Integer> value = of(3, 7, 1, 15, 0);
        final io.vavr.collection.SortedSet<Integer> set = value.toSortedSet();
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(io.vavr.collection.TreeSet.of(3));
        } else {
            assertThat(set).isEqualTo(io.vavr.collection.TreeSet.of(0, 1, 3, 7, 15));
        }
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowOnConvertToSortedSetWithoutComparatorOnNonComparable() {
        class NonComparable {
            final String s;
            NonComparable(String s) {
                this.s = s;
            }
        }
        final Value<NonComparable> value = of(new NonComparable("3"), new NonComparable("7"), new NonComparable("1"), new NonComparable("15"), new NonComparable("0"));
        final io.vavr.collection.SortedSet<NonComparable> set = value.toSortedSet();
        if (value.isSingleValued()) {
            //Comparator is not used yet
            set.add(new NonComparable("7"));
        }
    }

    @Test
    public void shouldConvertToSortedSet() {
        final Value<Integer> value = of(3, 7, 1, 15, 0);
        final Comparator<Integer> comparator = Comparator.comparingInt(Integer::bitCount);
        final io.vavr.collection.SortedSet<Integer> set = value.toSortedSet(comparator.reversed());
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(io.vavr.collection.TreeSet.of(3));
        } else {
            assertThat(set).isEqualTo(io.vavr.collection.TreeSet.of(comparator.reversed(), 0, 1, 3, 7, 15));
        }
    }

    @Test
    public void shouldConvertToSortedSetUsingSerializableComparator() {
        final Value<Integer> value = of(1, 3, 2);
        final io.vavr.collection.SortedSet<Integer> set = value.toSortedSet();
        final io.vavr.collection.SortedSet<Integer> actual = Serializables.deserialize(Serializables.serialize(set));
        assertThat(actual).isEqualTo(set);
    }

    @Test
    public void shouldConvertToStream() {
        final Value<Integer> value = of(1, 2, 3);
        final Stream<Integer> stream = value.toStream();
        if (value.isSingleValued()) {
            assertThat(stream).isEqualTo(Stream.of(1));
        } else {
            assertThat(stream).isEqualTo(Stream.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToVector() {
        final Value<Integer> value = of(1, 2, 3);
        final io.vavr.collection.Vector<Integer> vector = value.toVector();
        if (value.isSingleValued()) {
            assertThat(vector).isEqualTo(io.vavr.collection.Vector.of(1));
        } else {
            assertThat(vector).isEqualTo(io.vavr.collection.Vector.of(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaArray() {
        final Value<Integer> value = of(1, 2, 3);
        final Object[] ints = value.toJavaArray();
        if (value.isSingleValued()) {
            assertThat(ints).isEqualTo(new int[] { 1 });
        } else {
            assertThat(ints).isEqualTo(new int[] { 1, 2, 3 });
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithFactory() {
        final Value<Integer> value = of(1, 2, 3);
        final Integer[] ints = value.toJavaArray(Integer[]::new);
        if (value.isSingleValued()) {
            assertThat(ints).containsOnly(1);
        } else {
            assertThat(ints).containsOnly(1, 2, 3);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHint() {
        final Value<Integer> value = of(1, 2, 3);
        @SuppressWarnings("deprecation")
        final Integer[] ints = value.toJavaArray(Integer.class);
        if (value.isSingleValued()) {
            assertThat(ints).containsOnly(1);
        } else {
            assertThat(ints).containsOnly(1, 2, 3);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveBoolean() {
        final Value<Boolean> value = of(true, false);
        @SuppressWarnings("deprecation")
        final Boolean[] array = value.toJavaArray(boolean.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly(true);
        } else {
            assertThat(array).containsOnly(true, false);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveByte() {
        final Value<Byte> value = of((byte) 1, (byte) 2);
        @SuppressWarnings("deprecation")
        final Byte[] array = value.toJavaArray(byte.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly((byte) 1);
        } else {
            assertThat(array).containsOnly((byte) 1, (byte) 2);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveChar() {
        final Value<Character> value = of('a', 'b');
        @SuppressWarnings("deprecation")
        final Character[] array = value.toJavaArray(char.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly('a');
        } else {
            assertThat(array).containsOnly('a', 'b');
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveDouble() {
        final Value<Double> value = of(.1, .2);
        @SuppressWarnings("deprecation")
        final Double[] array = value.toJavaArray(double.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly(.1);
        } else {
            assertThat(array).containsOnly(.1, .2);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveFloat() {
        final Value<Float> value = of(.1f, .2f);
        @SuppressWarnings("deprecation")
        final Float[] array = value.toJavaArray(float.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly(.1f);
        } else {
            assertThat(array).containsOnly(.1f, .2f);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveInt() {
        final Value<Integer> value = of(1, 2);
        @SuppressWarnings("deprecation")
        final Integer[] array = value.toJavaArray(int.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly(1);
        } else {
            assertThat(array).containsOnly(1, 2);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveLong() {
        final Value<Long> value = of(1L, 2L);
        @SuppressWarnings("deprecation")
        final Long[] array = value.toJavaArray(long.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly(1L);
        } else {
            assertThat(array).containsOnly(1L, 2L);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveShort() {
        final Value<Short> value = of((short) 1, (short) 2);
        @SuppressWarnings("deprecation")
        final Short[] array = value.toJavaArray(short.class);
        if (value.isSingleValued()) {
            assertThat(array).containsOnly((short) 1);
        } else {
            assertThat(array).containsOnly((short) 1, (short) 2);
        }
    }

    @Test
    public void shouldConvertToJavaArrayWithTypeHintPrimitiveVoid() {
        final Value<Void> value = of((Void) null);
        @SuppressWarnings("deprecation")
        final Void[] array = value.toJavaArray(void.class);
        assertThat(array).containsOnly((Void) null);
    }

    @Test
    public void shouldConvertToJavaCollectionUsingSupplier() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.List<Integer> ints = value.toJavaCollection(ArrayList::new);
        if (value.isSingleValued()) {
            assertThat(ints).isEqualTo(Collections.singletonList(1));
        } else {
            assertThat(ints).isEqualTo(Arrays.asList(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaList() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.List<Integer> list = value.toJavaList();
        if (value.isSingleValued()) {
            assertThat(list).isEqualTo(Collections.singletonList(1));
        } else {
            assertThat(list).isEqualTo(Arrays.asList(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaListUsingSupplier() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.List<Integer> ints = value.toJavaList(ArrayList::new);
        if (value.isSingleValued()) {
            assertThat(ints).isEqualTo(Collections.singletonList(1));
        } else {
            assertThat(ints).isEqualTo(Arrays.asList(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaMapUsingFunction() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.Map<Integer, Integer> map = value.toJavaMap(v -> Tuple.of(v, v));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, 1));
        } else {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, 1, 2, 2, 3, 3));
        }
    }

    @Test
    public void shouldConvertToJavaMapUsingSupplierAndFunction() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.Map<Integer, Integer> map = value.toJavaMap(java.util.HashMap::new, i -> Tuple.of(i, i));
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, 1));
        } else {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, 1, 2, 2, 3, 3));
        }
    }

    @Test
    public void shouldConvertToJavaMapUsingSupplierAndTwoFunction() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.Map<Integer, String> map = value.toJavaMap(java.util.HashMap::new, Function.identity(), String::valueOf);
        if (value.isSingleValued()) {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, "1"));
        } else {
            assertThat(map).isEqualTo(JavaCollections.javaMap(1, "1", 2, "2", 3, "3"));
        }
    }

    @Test
    public void shouldConvertToJavaSet() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.Set<Integer> set = value.toJavaSet();
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(JavaCollections.javaSet(1));
        } else {
            assertThat(set).isEqualTo(JavaCollections.javaSet(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaSetUsingSupplier() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.Set<Integer> set = value.toJavaSet(java.util.HashSet::new);
        if (value.isSingleValued()) {
            assertThat(set).isEqualTo(JavaCollections.javaSet(1));
        } else {
            assertThat(set).isEqualTo(JavaCollections.javaSet(1, 2, 3));
        }
    }

    @Test
    public void shouldConvertToJavaStream() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.stream.Stream<Integer> s1 = value.toJavaStream();
        //noinspection Duplicates
        if (value.isSingleValued()) {
            final java.util.stream.Stream<Integer> s2 = java.util.stream.Stream.of(1);
            assertThat(io.vavr.collection.List.ofAll(s1::iterator)).isEqualTo(io.vavr.collection.List.ofAll(s2::iterator));
        } else {
            final java.util.stream.Stream<Integer> s2 = java.util.stream.Stream.of(1, 2, 3);
            assertThat(io.vavr.collection.List.ofAll(s1::iterator)).isEqualTo(io.vavr.collection.List.ofAll(s2::iterator));
        }
    }

    @Test
    public void shouldConvertToJavaParallelStream() {
        final Value<Integer> value = of(1, 2, 3);
        final java.util.stream.Stream<Integer> s1 = value.toJavaParallelStream();
        assertThat(s1.isParallel()).isTrue();
        //noinspection Duplicates
        if (value.isSingleValued()) {
            final java.util.stream.Stream<Integer> s2 = java.util.stream.Stream.of(1);
            assertThat(io.vavr.collection.List.ofAll(s1::iterator)).isEqualTo(io.vavr.collection.List.ofAll(s2::iterator));
        } else {
            final java.util.stream.Stream<Integer> s2 = java.util.stream.Stream.of(1, 2, 3);
            assertThat(io.vavr.collection.List.ofAll(s1::iterator)).isEqualTo(io.vavr.collection.List.ofAll(s2::iterator));
        }
    }

    // -- exists

    @Test
    public void shouldBeAwareOfExistingElement() {
        final Value<Integer> value = of(1, 2);
        if (value.isSingleValued()) {
            assertThat(value.exists(i -> i == 1)).isTrue();
        } else {
            assertThat(value.exists(i -> i == 2)).isTrue();
        }

    }

    @Test
    public void shouldBeAwareOfNonExistingElement() {
        assertThat(this.<Integer> empty().exists(i -> i == 1)).isFalse();
    }

    // -- forAll

    @Test
    public void shouldBeAwareOfPropertyThatHoldsForAll() {
        assertThat(of(2, 4).forAll(i -> i % 2 == 0)).isTrue();
    }

    @Test
    public void shouldBeAwareOfPropertyThatNotHoldsForAll() {
        assertThat(of(1, 2).forAll(i -> i % 2 == 0)).isFalse();
    }

    // ### ValueModule.Iterable ###

    // -- corresponds

    @Test
    public void shouldntCorrespondsNilNil() {
        assertThat(empty().corresponds(empty(), (o1, o2) -> true)).isTrue();
    }

    @Test
    public void shouldntCorrespondsNilNonNil() {
        assertThat(empty().corresponds(of(1), (o1, i2) -> true)).isFalse();
    }

    @Test
    public void shouldntCorrespondsNonNilNil() {
        assertThat(of(1).corresponds(empty(), (i1, o2) -> true)).isFalse();
    }

    @Test
    public void shouldntCorrespondsDifferentLengths() {
        if (!empty().isSingleValued()) {
            assertThat(of(1, 2, 3).corresponds(of(1, 2), (i1, i2) -> true)).isFalse();
            assertThat(of(1, 2).corresponds(of(1, 2, 3), (i1, i2) -> true)).isFalse();
        }
    }

    @Test
    public void shouldCorresponds() {
        assertThat(of(1, 2, 3).corresponds(of(3, 4, 5), (i1, i2) -> i1 == i2 - 2)).isTrue();
        assertThat(of(1, 2, 3).corresponds(of(1, 2, 3), (i1, i2) -> i1 == i2 + 1)).isFalse();
    }

    @Test
    public void shouldHaveAReasonableToString() {
        final Value<Integer> value = of(1, 2);
        value.toList(); // evaluate all elements (e.g. for Stream)
        final String actual = value.toString();

        if (value.isSingleValued()) {
            assertThat(actual).contains("1");
        } else {
            assertThat(actual).contains("1", "2");
        }
    }

    // -- Serialization

    /**
     * States whether the specific Value implementation is Serializable.
     * <p>
     * Test classes override this method to return false if needed.
     *
     * @return true (by default), if the Value is Serializable, false otherwise
     */
    private boolean isSerializable() {
        final Object nonEmpty = of(1);
        if (empty() instanceof Serializable != nonEmpty instanceof Serializable) {
            throw new Error("empty and non-empty do not consistently implement Serializable");
        }
        final boolean actual = nonEmpty instanceof Serializable;
        final boolean expected = Match(nonEmpty).of(
                Case($(anyOf(
                        instanceOf(Future.class),
                        instanceOf(io.vavr.collection.Iterator.class)
                )), false),
                Case($(anyOf(
                        instanceOf(Either.class),
                        instanceOf(Lazy.class),
                        instanceOf(Option.class),
                        instanceOf(Try.class),
                        instanceOf(Traversable.class),
                        instanceOf(Validation.class)
                )), true)
        );
        assertThat(actual).isEqualTo(expected);
        return actual;
    }

    @Test
    public void shouldSerializeDeserializeEmpty() {
        if (isSerializable()) {
            final Value<?> testee = empty();
            final Value<?> actual = Serializables.deserialize(Serializables.serialize(testee));
            assertThat(actual).isEqualTo(testee);
        }
    }

    @Test
    public void shouldSerializeDeserializeSingleValued() {
        if (isSerializable()) {
            final Value<?> testee = of(1);
            final Value<?> actual = Serializables.deserialize(Serializables.serialize(testee));
            assertThat(actual).isEqualTo(testee);
        }
    }

    @Test
    public void shouldSerializeDeserializeMultiValued() {
        if (isSerializable()) {
            final Value<?> testee = of(1, 2, 3);
            final Value<?> actual = Serializables.deserialize(Serializables.serialize(testee));
            assertThat(actual).isEqualTo(testee);
        }
    }

    @Test
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        if (isSerializable() && !useIsEqualToInsteadOfIsSameAs()) {
            final Value<?> empty = empty();
            final Value<?> actual = Serializables.deserialize(Serializables.serialize(empty));
            assertThat(actual).isSameAs(empty);
        }
    }

    // -- equals

    @Test
    public void shouldRecognizeSameObject() {
        final Value<Integer> v = of(1);
        //noinspection EqualsWithItself
        assertThat(v.equals(v)).isTrue();
    }

    @Test
    public void shouldRecognizeEqualObjects() {
        final Value<Integer> v1 = of(1);
        final Value<Integer> v2 = of(1);
        assertThat(v1.equals(v2)).isTrue();
    }

    @Test
    public void shouldRecognizeUnequalObjects() {
        final Value<Integer> v1 = of(1);
        final Value<Integer> v2 = of(2);
        assertThat(v1.equals(v2)).isFalse();
    }

}
