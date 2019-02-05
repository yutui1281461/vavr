/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */

package javaslang.collection;

import javaslang.*;
import javaslang.control.Option;

import java.util.*;
import java.util.function.*;

/**
 * @author Pap Lőrinc, Daniel Dietrich
 * @since 2.1.0
 */
abstract class AbstractsQueue<T, Q extends AbstractsQueue<T, Q>> implements Traversable<T> {
    /**
     * Removes an element from this Queue.
     *
     * @return a tuple containing the first element and the remaining elements of this Queue
     * @throws NoSuchElementException if this Queue is empty
     */
    public Tuple2<T, Q> dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("dequeue of empty " + getClass().getSimpleName());
        } else {
            return Tuple.of(head(), tail());
        }
    }

    /**
     * Removes an element from this Queue.
     *
     * @return {@code None} if this Queue is empty, otherwise {@code Some} {@code Tuple} containing the first element and the remaining elements of this Queue
     */
    public Option<Tuple2<T, Q>> dequeueOption() {
        return isEmpty() ? Option.none() : Option.some(dequeue());
    }

    /**
     * Enqueues a new element.
     *
     * @param element The new element
     * @return a new {@code Queue} instance, containing the new element
     */
    public abstract Q enqueue(T element);

    /**
     * Enqueues the given elements. A queue has FIFO order, i.e. the first of the given elements is
     * the first which will be retrieved.
     *
     * @param elements Elements, may be empty
     * @return a new {@code Queue} instance, containing the new elements
     * @throws NullPointerException if elements is null
     */
    @SuppressWarnings("unchecked")
    public Q enqueue(T... elements) {
        Objects.requireNonNull(elements, "elements is null");
        return enqueueAll(List.of(elements));
    }

    /**
     * Enqueues the given elements. A queue has FIFO order, i.e. the first of the given elements is
     * the first which will be retrieved.
     *
     * @param elements An Iterable of elements, may be empty
     * @return a new {@code Queue} instance, containing the new elements
     * @throws NullPointerException if elements is null
     */
    @SuppressWarnings("unchecked")
    public Q enqueueAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");

        return List.ofAll(elements).foldLeft((Q) this, AbstractsQueue<T, Q>::enqueue);
    }

    /**
     * Returns the first element without modifying it.
     *
     * @return the first element
     * @throws NoSuchElementException if this Queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("peek of empty " + getClass().getSimpleName());
        } else {
            return head();
        }
    }

    /**
     * Returns the first element without modifying the Queue.
     *
     * @return {@code None} if this Queue is empty, otherwise a {@code Some} containing the first element
     */
    public Option<T> peekOption() {
        return isEmpty() ? Option.none() : Option.some(peek());
    }

    /**
     * Dual of {@linkplain #tail()}, returning all elements except the last.
     *
     * @return a new instance containing all elements except the last.
     * @throws UnsupportedOperationException if this is empty
     */
    @Override
    public abstract Q init();

    /**
     * Dual of {@linkplain #tailOption()}, returning all elements except the last as {@code Option}.
     *
     * @return {@code Some(Q)} or {@code None} if this is empty.
     */
    @Override
    public Option<Q> initOption() {
        return isEmpty() ? Option.none() : Option.some(init());
    }

    /**
     * Drops the first element of a non-empty Traversable.
     *
     * @return A new instance of Traversable containing all elements except the first.
     * @throws UnsupportedOperationException if this is empty
     */
    @Override
    public abstract Q tail();

    @Override
    public Option<Q> tailOption() {
        return isEmpty() ? Option.none() : Option.some(tail());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Q retainAll(Iterable<? extends T> elements) {
        return Collections.retainAll((Q) this, elements);
    }

    @SuppressWarnings("unchecked")
    public Q removeAll(Iterable<? extends T> elements) {
        return Collections.removeAll((Q) this, elements);
    }

    @SuppressWarnings("unchecked")
    public Q removeAll(Predicate<? super T> predicate) {
        return Collections.removeAll((Q) this, predicate);
    }

    @Override
    public Q takeWhile(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return takeUntil(predicate.negate());
    }

    @Override
    public abstract Q takeUntil(Predicate<? super T> predicate);

    @SuppressWarnings("unchecked")
    @Override
    public Q peek(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (!isEmpty()) {
            action.accept(head());
        }
        return (Q) this;
    }

    @Override
    public int hashCode() {
        return Collections.hash(this);
    }

    @Override
    public String toString() {
        return mkString(stringPrefix() + "(", ", ", ")");
    }

}