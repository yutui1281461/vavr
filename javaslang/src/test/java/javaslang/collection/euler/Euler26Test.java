/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection.euler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javaslang.Function1;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.CharSeq;
import javaslang.collection.List;
import javaslang.collection.Stream;
import org.junit.Test;

import javaslang.control.Option;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <strong>Problem 26: Reciprocal cycles</strong>
 * <p>
 * A unit fraction contains 1 in the numerator. The decimal representation of
 * the unit fractions with denominators 2 to 10 are given:
 * <pre>
 * 1/2	= 0.5
 * 1/3	= 0.(3)
 * 1/4	= 0.25
 * 1/5	= 0.2
 * 1/6	= 0.1(6)
 * 1/7	= 0.(142857)
 * 1/8	= 0.125
 * 1/9	= 0.(1)
 * 1/10	= 0.1
 * </pre> Where 0.1(6) means 0.166666..., and has a 1-digit recurring cycle. It
 * can be seen that 1/7 has a 6-digit recurring cycle.
 * <p>
 * Find the value of d < 1000 for which 1/d contains the longest recurring cycle
 * in its decimal fraction part. <p>
 * See also
 * <a href="https://projecteuler.net/problem=26">projecteuler.net problem 26
 * </a>.
 */
public class Euler26Test {

    @Test
    public void shouldSolveProblem26() {
        assertThat(recurringCycleLengthForDivisionOf1(2)._2).isEqualTo(0);
        assertThat(recurringCycleLengthForDivisionOf1(3)._2).isEqualTo(1);
        assertThat(recurringCycleLengthForDivisionOf1(4)._2).isEqualTo(0);
        assertThat(recurringCycleLengthForDivisionOf1(5)._2).isEqualTo(0);
        assertThat(recurringCycleLengthForDivisionOf1(6)._2).isEqualTo(1);
        assertThat(recurringCycleLengthForDivisionOf1(7)._2).isEqualTo(6);
        assertThat(recurringCycleLengthForDivisionOf1(8)._2).isEqualTo(0);
        assertThat(recurringCycleLengthForDivisionOf1(9)._2).isEqualTo(1);
        assertThat(recurringCycleLengthForDivisionOf1(10)._2).isEqualTo(0);
        assertThat(deonominatorBelow1000WithTheLongetsRecurringCycleOfDecimalFractions()).isEqualTo(983);
    }

    private static int deonominatorBelow1000WithTheLongetsRecurringCycleOfDecimalFractions() {
        return List.range(2, 1000)
                .map(Euler26Test::recurringCycleLengthForDivisionOf1)
                .maxBy(Tuple2::_2)
                .get()._1;
    }

    private static Tuple2<Integer, Integer> recurringCycleLengthForDivisionOf1(int divisor) {
        return Tuple.of(
                divisor,
                recurringCycleLengthInDecimalFractionPart(
                        CharSeq.of(BigDecimal.ONE.divide(BigDecimal.valueOf(divisor), 2000, RoundingMode.UP).toString())
                        .transform(removeLeadingZeroAndDecimalPoint())
                        .transform(removeRoundingDigit())
                        .transform(removeTrailingZeroes())
                        .mkString()
                ));
    }

    private static int recurringCycleLengthInDecimalFractionPart(String decimalFractionPart) {
        return CharSeq.of(decimalFractionPart)
                .reverse()
                .toStream() // Stream is lazy evaluated which ensures the rest is only evaluated until the recurring cycle is found.
                .transform(createCandidateCycles())
                .transform(removeCandidatesLongerThanHalfTheFullString(decimalFractionPart))
                .transform(findFirstRecurringCycle(decimalFractionPart))
                .map(String::length)
                .getOrElse(0);
    }

    private static Function1<CharSeq, CharSeq> removeLeadingZeroAndDecimalPoint() {
        return seq -> seq.drop(2);
    }

    private static Function1<CharSeq, CharSeq> removeRoundingDigit() {
        return seq -> seq.dropRight(1);
    }

    private static Function1<CharSeq, CharSeq> removeTrailingZeroes() {
        return seq -> seq
                .reverse()
                .dropWhile(c -> c == '0') //Remove any trailing zeroes
                .reverse();
    }

    private static Function1<Stream<Character>, Stream<String>> createCandidateCycles() {
        return reversedDecimalFractionPart -> reversedDecimalFractionPart
                .map(String::valueOf)
                .scan("", String::concat)
                .drop(1); // Drop the first emtpy string created by scan
    }

    private static Function1<Stream<String>, Stream<String>> removeCandidatesLongerThanHalfTheFullString(String decimalFractionPart) {
        return candidateCycles -> candidateCycles.filter(candidate -> decimalFractionPart.length() >= candidate.length() * 2);
    }

    private static Function1<Stream<String>, Option<String>> findFirstRecurringCycle(String decimalFractionPart) {
        return reversedCandidateCycles -> reversedCandidateCycles
                .map(s -> CharSeq.of(s).reverse().mkString())
                .find(candidate -> candidate.equals(decimalFractionPart.substring(decimalFractionPart.length() - (candidate.length() * 2), decimalFractionPart.length() - candidate.length())));
    }
}
