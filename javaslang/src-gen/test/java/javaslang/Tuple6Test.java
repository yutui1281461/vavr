/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang;

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
   G E N E R A T O R   C R A F T E D
\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.Objects;
import javaslang.collection.List;
import javaslang.collection.Seq;
import org.junit.Test;

public class Tuple6Test {

    @Test
    public void shouldCreateTuple() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        assertThat(tuple).isNotNull();
    }

    @Test
    public void shouldGetArity() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        assertThat(tuple.arity()).isEqualTo(6);
    }

    @Test
    public void shouldReturnElements() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6);
        assertThat(tuple._1).isEqualTo(1);
        assertThat(tuple._2).isEqualTo(2);
        assertThat(tuple._3).isEqualTo(3);
        assertThat(tuple._4).isEqualTo(4);
        assertThat(tuple._5).isEqualTo(5);
        assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate1() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update1(42);
      assertThat(tuple._1).isEqualTo(42);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate2() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update2(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(42);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate3() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update3(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(42);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate4() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update4(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(42);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate5() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update5(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(42);
      assertThat(tuple._6).isEqualTo(6);
    }

    @Test
    public void shouldUpdate6() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6).update6(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(42);
    }

    @Test
    public void shouldConvertToSeq() {
        final Seq<?> actual = createIntTuple(1, 0, 0, 0, 0, 0).toSeq();
        assertThat(actual).isEqualTo(List.of(1, 0, 0, 0, 0, 0));
    }

    @Test
    public void shouldCompareEqual() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t0)).isZero();
        assertThat(intTupleComparator.compare(t0, t0)).isZero();
    }

    @Test
    public void shouldCompare1stArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t1 = createIntTuple(1, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t1)).isNegative();
        assertThat(t1.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t1)).isNegative();
        assertThat(intTupleComparator.compare(t1, t0)).isPositive();
    }

    @Test
    public void shouldCompare2ndArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t2 = createIntTuple(0, 1, 0, 0, 0, 0);
        assertThat(t0.compareTo(t2)).isNegative();
        assertThat(t2.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t2)).isNegative();
        assertThat(intTupleComparator.compare(t2, t0)).isPositive();
    }

    @Test
    public void shouldCompare3rdArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t3 = createIntTuple(0, 0, 1, 0, 0, 0);
        assertThat(t0.compareTo(t3)).isNegative();
        assertThat(t3.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t3)).isNegative();
        assertThat(intTupleComparator.compare(t3, t0)).isPositive();
    }

    @Test
    public void shouldCompare4thArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t4 = createIntTuple(0, 0, 0, 1, 0, 0);
        assertThat(t0.compareTo(t4)).isNegative();
        assertThat(t4.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t4)).isNegative();
        assertThat(intTupleComparator.compare(t4, t0)).isPositive();
    }

    @Test
    public void shouldCompare5thArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t5 = createIntTuple(0, 0, 0, 0, 1, 0);
        assertThat(t0.compareTo(t5)).isNegative();
        assertThat(t5.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t5)).isNegative();
        assertThat(intTupleComparator.compare(t5, t0)).isPositive();
    }

    @Test
    public void shouldCompare6thArg() {
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0);
        final Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t6 = createIntTuple(0, 0, 0, 0, 0, 1);
        assertThat(t0.compareTo(t6)).isNegative();
        assertThat(t6.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t6)).isNegative();
        assertThat(intTupleComparator.compare(t6, t0)).isPositive();
    }

    @Test
    public void shouldMap() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple6<Object, Object, Object, Object, Object, Object> actual = tuple.map((o1, o2, o3, o4, o5, o6) -> tuple);
        assertThat(actual).isEqualTo(tuple);
    }

    @Test
    public void shouldMapComponents() {
      final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
      final Function1<Object, Object> f1 = Function1.identity();
      final Function1<Object, Object> f2 = Function1.identity();
      final Function1<Object, Object> f3 = Function1.identity();
      final Function1<Object, Object> f4 = Function1.identity();
      final Function1<Object, Object> f5 = Function1.identity();
      final Function1<Object, Object> f6 = Function1.identity();
      final Tuple6<Object, Object, Object, Object, Object, Object> actual = tuple.map(f1, f2, f3, f4, f5, f6);
      assertThat(actual).isEqualTo(tuple);
    }

    @Test
    public void shouldMap1stComponent() {
      final Tuple6<String, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1).map1(i -> "X");
      final Tuple6<String, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of("X", 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap2ndComponent() {
      final Tuple6<Integer, String, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1).map2(i -> "X");
      final Tuple6<Integer, String, Integer, Integer, Integer, Integer> expected = Tuple.of(1, "X", 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap3rdComponent() {
      final Tuple6<Integer, Integer, String, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1).map3(i -> "X");
      final Tuple6<Integer, Integer, String, Integer, Integer, Integer> expected = Tuple.of(1, 1, "X", 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap4thComponent() {
      final Tuple6<Integer, Integer, Integer, String, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1).map4(i -> "X");
      final Tuple6<Integer, Integer, Integer, String, Integer, Integer> expected = Tuple.of(1, 1, 1, "X", 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap5thComponent() {
      final Tuple6<Integer, Integer, Integer, Integer, String, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1).map5(i -> "X");
      final Tuple6<Integer, Integer, Integer, Integer, String, Integer> expected = Tuple.of(1, 1, 1, 1, "X", 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap6thComponent() {
      final Tuple6<Integer, Integer, Integer, Integer, Integer, String> actual = Tuple.of(1, 1, 1, 1, 1, 1).map6(i -> "X");
      final Tuple6<Integer, Integer, Integer, Integer, Integer, String> expected = Tuple.of(1, 1, 1, 1, 1, "X");
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldApplyTuple() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple0 actual = tuple.apply((o1, o2, o3, o4, o5, o6) -> Tuple0.instance());
        assertThat(actual).isEqualTo(Tuple0.instance());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldTransformTuple() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple0 actual = tuple.transform((o1, o2, o3, o4, o5, o6) -> Tuple0.instance());
        assertThat(actual).isEqualTo(Tuple0.instance());
    }

    @Test
    public void shouldRecognizeEquality() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple1 = createTuple();
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple2 = createTuple();
        assertThat((Object) tuple1).isEqualTo(tuple2);
    }

    @Test
    public void shouldRecognizeNonEquality() {
        final Tuple6<Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Object other = new Object();
        assertThat(tuple).isNotEqualTo(other);
    }

    @Test
    public void shouldRecognizeNonEqualityPerComponent() {
        final Tuple6<String, String, String, String, String, String> tuple = Tuple.of("1", "2", "3", "4", "5", "6");
        assertThat(tuple.equals(Tuple.of("X", "2", "3", "4", "5", "6"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "X", "3", "4", "5", "6"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "X", "4", "5", "6"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "X", "5", "6"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "X", "6"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "X"))).isFalse();
    }

    @Test
    public void shouldComputeCorrectHashCode() {
        final int actual = createTuple().hashCode();
        final int expected = Objects.hash(null, null, null, null, null, null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldImplementToString() {
        final String actual = createTuple().toString();
        final String expected = "(null, null, null, null, null, null)";
        assertThat(actual).isEqualTo(expected);
    }

    private Comparator<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>> intTupleComparator = Tuple6.comparator(Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare);

    private Tuple6<Object, Object, Object, Object, Object, Object> createTuple() {
        return new Tuple6<>(null, null, null, null, null, null);
    }

    private Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> createIntTuple(Integer i1, Integer i2, Integer i3, Integer i4, Integer i5, Integer i6) {
        return new Tuple6<>(i1, i2, i3, i4, i5, i6);
    }
}