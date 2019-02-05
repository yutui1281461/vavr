/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection.euler;

import javaslang.Function1;
import javaslang.collection.Stream;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Euler14Test {

    /**
     * <strong>Problem 14: Longest Collatz sequence</strong>
     * <p>
     * The following iterative sequence is defined for the set of positive integers:
     * <pre>
     * <code>
     * n → n/2 (n is even)
     * n → 3n + 1 (n is odd)
     * </code>
     * </pre>
     * Using the rule above and starting with 13, we generate the following sequence:
     * <pre>
     * <code>
     * 13 → 40 → 20 → 10 → 5 → 16 → 8 → 4 → 2 → 1
     * </code>
     * </pre>
     * It can be seen that this sequence (starting at 13 and finishing at 1) contains 10 terms.
     * Although it has not been proved yet (Collatz Problem), it is thought that all starting numbers finish at 1.
     * <p>
     * Which starting number, under one million, produces the longest chain?
     * <p>
     * NOTE: Once the chain starts the terms are allowed to go above one million.
     * <p>
     * See also <a href="https://projecteuler.net/problem=14">projecteuler.net problem 14</a>.
     */
    @Test
    public void shouldSolveProblem14() {
        // equivalent to from(1L).take(1_000_000)
        assertThat(Stream.from(500_000L)
                .take(500_000)
                .maxBy(collatzSequenceLength)
                .get()).isEqualTo(837799);
    }

    private final static Function1<Long, Long> collatzRecursive = n -> {
        if (n == 1) {
            return 1L;
        } else {
            if (n % 2 == 0) {
                return Euler14Test.collatzRecursive.apply(n / 2) + 1;
            } else {
                return Euler14Test.collatzRecursive.apply(3 * n + 1) + 1;
            }
        }
    };

    private final static Function1<Long, Long> collatzSequenceLength = collatzRecursive.memoized();
}
