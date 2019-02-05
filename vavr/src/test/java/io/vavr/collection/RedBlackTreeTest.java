/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2017 Vavr, http://vavr.io
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
package io.vavr.collection;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class RedBlackTreeTest {

    private static <T> RedBlackTree<T> empty() {
        return RedBlackTree.empty(Comparators.naturalComparator());
    }

    private static <T> RedBlackTree<T> of(T value) {
        return RedBlackTree.of(Comparators.naturalComparator(), value);
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    private static <T> RedBlackTree<T> of(T... values) {
        return RedBlackTree.<T> of(Comparators.naturalComparator(), values);
    }

    // Rudimentary tests

    // empty tree

    @Test
    public void shouldCreateEmptyTree() {
        final RedBlackTree<Integer> tree = empty();
        assertThat(tree.isEmpty()).isTrue();
        assertThat(tree.size()).isEqualTo(0);
        assertThat(tree.color()).isEqualTo(RedBlackTree.Color.BLACK);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailLeftOfEmpty() {
        empty().left();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailRightOfEmpty() {
        empty().right();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFailValueOfEmpty() {
        empty().value();
    }

    // isEmpty

    @Test
    public void shouldRecognizeEmptyTree() {
        assertThat(empty().isEmpty()).isTrue();
    }

    @Test
    public void shouldRecognizeNonEmptyTree() {
        assertThat(of(1).isEmpty()).isFalse();
    }

    // contains

    @Test
    public void shouldRecognizeContainedElement() {
        assertThat(of(1, 2, 3).contains(2)).isTrue();
    }

    @Test
    public void shouldRecognizeNonContainedElementOfEmptyTree() {
        assertThat(RedBlackTreeTest.<Integer> empty().contains(1)).isFalse();
    }

    @Test
    public void shouldRecognizeNonContainedElementOfNonEmptyTree() {
        assertThat(of(1, 2, 3).contains(0)).isFalse();
    }

    // insert

    @Test
    public void shouldInsert_2_1_4_5_9_3_6_7() {

        RedBlackTree<Integer> tree = empty();
        assertThat(tree.toString()).isEqualTo("()");
        assertThat(tree.size()).isEqualTo(0);

        tree = tree.insert(2);
        assertThat(tree.toString()).isEqualTo("(B:2)");
        assertThat(tree.size()).isEqualTo(1);

        tree = tree.insert(1);
        assertThat(tree.toString()).isEqualTo("(B:2 R:1)");
        assertThat(tree.size()).isEqualTo(2);

        tree = tree.insert(4);
        assertThat(tree.toString()).isEqualTo("(B:2 R:1 R:4)");
        assertThat(tree.size()).isEqualTo(3);

        tree = tree.insert(5);
        assertThat(tree.toString()).isEqualTo("(B:4 (B:2 R:1) B:5)");
        assertThat(tree.size()).isEqualTo(4);

        tree = tree.insert(9);
        assertThat(tree.toString()).isEqualTo("(B:4 (B:2 R:1) (B:5 R:9))");
        assertThat(tree.size()).isEqualTo(5);

        tree = tree.insert(3);
        assertThat(tree.toString()).isEqualTo("(B:4 (B:2 R:1 R:3) (B:5 R:9))");
        assertThat(tree.size()).isEqualTo(6);

        tree = tree.insert(6);
        assertThat(tree.toString()).isEqualTo("(B:4 (B:2 R:1 R:3) (R:6 B:5 B:9))");
        assertThat(tree.size()).isEqualTo(7);

        tree = tree.insert(7);
        assertThat(tree.toString()).isEqualTo("(B:4 (B:2 R:1 R:3) (R:6 B:5 (B:9 R:7)))");
        assertThat(tree.size()).isEqualTo(8);
    }

    @Test
    public void shouldInsertNullIntoEmptyTreeBecauseComparatorNotCalled() {
        final RedBlackTree<Integer> actual = RedBlackTreeTest.<Integer> empty().insert(null);
        final RedBlackTree<Integer> expected = of((Integer) null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotInsertNullTwoTimesIntoEmptyTreeBecauseComparatorCalled() {
        RedBlackTreeTest.<Integer> empty().insert(null).insert(null);
    }

    @Test
    public void shouldInsertNonNullIntoEmptyTree() {
        final RedBlackTree<Integer> actual = RedBlackTreeTest.<Integer> empty().insert(2);
        final RedBlackTree<Integer> expected = of(2);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnTheSameInstanceWhenInsertingAnAlreadyContainedElement() {
        final RedBlackTree<Integer> testee = of(1, 2, 3);
        final RedBlackTree<Integer> actual = testee.insert(2);
        assertThat(actual).isEqualTo(testee);
    }

    // delete

    @Test
    public void shouldDelete_2_from_2_1_4_5_9_3_6_7() {
        final RedBlackTree<Integer> testee = of(2, 1, 4, 5, 9, 3, 6, 7);
        final RedBlackTree<Integer> actual = testee.delete(2);
        assertThat(actual.toString()).isEqualTo("(B:4 (B:3 R:1) (R:6 B:5 (B:9 R:7)))");
        assertThat(actual.size()).isEqualTo(7);
    }

    // difference()

    @Test
    public void shouldSubtractEmptyFromNonEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = empty();
        final RedBlackTree<Integer> actual = t1.difference(t2);
        assertThat(actual).isEqualTo(t1);
    }

    @Test
    public void shouldSubtractNonEmptyFromEmpty() {
        final RedBlackTree<Integer> t1 = empty();
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.difference(t2);
        assertThat(actual).isEqualTo(t1);
    }

    @Test
    public void shouldSubtractNonEmptyFromNonEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.difference(t2);
        final RedBlackTree<Integer> expected = of(3);
        assertThat(actual).isEqualTo(expected);
    }

    // intersection()

    @Test
    public void shouldIntersectOnNonEmptyGivenEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = empty();
        final RedBlackTree<Integer> actual = t1.intersection(t2);
        final RedBlackTree<Integer> expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldIntersectOnEmptyGivenNonEmpty() {
        final RedBlackTree<Integer> t1 = empty();
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.intersection(t2);
        final RedBlackTree<Integer> expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldIntersectOnNonEmptyGivenNonEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.intersection(t2);
        final RedBlackTree<Integer> expected = of(5);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldIntersectOnNonEmptyGivenNonEmptyUnbalancedHeightLeft() {
        // Node::mergeGT
        //
        // Trees have
        // - different values
        // - similar to each other left children
        // - and unlike each other right children
        final RedBlackTree<Integer> t1 = of(1, 2, 3, 4, 5, 6, 7, 8, 60, 66, 67);
        final RedBlackTree<Integer> t2 = of(1, 2, 3, 10, 11, 12, 13, 14, 60, 76, 77);
        final RedBlackTree<Integer> actual = t1.intersection(t2);
        final RedBlackTree<Integer> expected = of(1, 2, 3, 60);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldIntersectOnNonEmptyGivenNonEmptyUnbalancedHeightRight() {
        // Node::mergeLT
        //
        // Trees have
        // - different values
        // - unlike each other left children
        // - and similar to each other right children
        final RedBlackTree<Integer> t1 = of(1, 2, 3, 4, 40, 61, 62, 63, 64, 65);
        final RedBlackTree<Integer> t2 = of(2, 7, 8, 9, 50, 61, 62, 63, 64, 65);
        final RedBlackTree<Integer> actual = t1.intersection(t2);
        final RedBlackTree<Integer> expected = of(2, 61, 62, 63, 64, 65);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldIntersectOnNonEmptyGivenNonEmptyBalancedHeightRight() {
        // Node::mergeEQ && isRed(n1.right)
        //
        final RedBlackTree<Integer> t1 = of(-10, -20, -30, -40, -50, 1, 10, 20, 30);
        final RedBlackTree<Integer> t2 = of(-10, -20, -30, -40, -50, 2, 10, 20, 30);
        assertThat(t1.intersection(t2)).isEqualTo(t1.delete(1));
    }

    // union()

    @Test
    public void shouldUnionOnNonEmptyGivenEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = empty();
        final RedBlackTree<Integer> actual = t1.union(t2);
        final RedBlackTree<Integer> expected = of(3, 5);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldUnionOnEmptyGivenNonEmpty() {
        final RedBlackTree<Integer> t1 = empty();
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.union(t2);
        final RedBlackTree<Integer> expected = of(5, 7);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldUnionOnNonEmptyGivenNonEmpty() {
        final RedBlackTree<Integer> t1 = of(3, 5);
        final RedBlackTree<Integer> t2 = of(5, 7);
        final RedBlackTree<Integer> actual = t1.union(t2);
        final RedBlackTree<Integer> expected = of(3, 5, 7);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldComputeUnionAndEqualTreesOfDifferentShapeButSameElements() {
        final RedBlackTree<Integer> t1 = of(-1, -1, 0, 1);
        final RedBlackTree<Integer> t2 = of(-2, -1, 0, 1);
        final RedBlackTree<Integer> actual = t1.union(t2);
        final RedBlackTree<Integer> expected = of(-2, -1, 0, 1);
        assertThat(actual).isEqualTo(expected);
    }

    // iterator()

    @Test
    public void shouldIterateEmptyTree() {
        assertThat(empty().iterator().hasNext()).isFalse();
    }

    @Test
    public void shouldIterateNonEmptyTree() {
        final RedBlackTree<Integer> testee = of(7, 1, 6, 2, 5, 3, 4);
        final List<Integer> actual = testee.iterator().toList();
        assertThat(actual.toString()).isEqualTo("List(1, 2, 3, 4, 5, 6, 7)");
    }

}
