/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Lazy;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.collection.IteratorModule.ConcatIterator;
import javaslang.collection.IteratorModule.DistinctIterator;
import javaslang.control.Option;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.math.RoundingMode.HALF_UP;
import static javaslang.collection.IteratorModule.BigDecimalHelper.areEqual;
import static javaslang.collection.IteratorModule.BigDecimalHelper.asDecimal;

/**
 * {@code javaslang.collection.Iterator} is a compositional replacement for {@code java.util.Iterator}
 * whose purpose is to iterate <em>once</em> over a sequence of elements.
 * <p>
 * It is recommended to create instances using {@link AbstractIterator} in favor of {@code Iterator}.
 * <p>
 * <strong>Note:</strong> Iterators encapsulate mutable state.
 * They are not meant to be used concurrently by different threads. Do not reuse Iterators, e.g. after passing to
 * {@linkplain List#ofAll(Iterable)}.
 * <p>
 * There are two abstract methods: {@code hasNext} for checking if there is a next element available,
 * and {@code next} which removes the next element from the iterator and returns it. They can be called
 * an arbitrary amount of times. If {@code hasNext} returns false, a call of {@code next} will throw
 * a {@code NoSuchElementException}.
 * <p>
 * <strong>Caution: Other methods than {@code hasNext} and {@code next} can be called only once (exclusively).
 * More specifically, after calling a method it cannot be guaranteed that the next call will succeed.</strong>
 * <p>
 * An Iterator that can be only used once because it is a traversal pointer into a collection, and not a collection
 * itself.
 *
 * @param <T> Component type
 * @author Daniel Dietrich
 * @since 2.0.0
 */
public interface Iterator<T> extends java.util.Iterator<T>, Traversable<T> {
    // DEV-NOTE: we prefer returning empty() over this if !hasNext() == true in order to free memory.

    /**
     * The empty Iterator.
     */
    Iterator<Object> EMPTY = new AbstractIterator<Object>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object getNext() {
            return null;
        }
    };

    /**
     * Creates an Iterator which traverses along the concatenation of the given iterables.
     *
     * @param iterables The iterables
     * @param <T>       Component type.
     * @return A new {@code javaslang.collection.Iterator}
     */
    @SuppressWarnings("varargs")
    @SafeVarargs
    static <T> Iterator<T> concat(Iterable<? extends T>... iterables) {
        Objects.requireNonNull(iterables, "iterables is null");
        if (iterables.length == 0) {
            return empty();
        } else {
            return new ConcatIterator<>(Stream.of(iterables).map(Iterator::ofAll).iterator());
        }
    }

    /**
     * Creates an Iterator which traverses along the concatenation of the given iterables.
     *
     * @param iterables The iterable of iterables
     * @param <T>       Component type.
     * @return A new {@code javaslang.collection.Iterator}
     */
    static <T> Iterator<T> concat(Iterable<? extends Iterable<? extends T>> iterables) {
        Objects.requireNonNull(iterables, "iterables is null");
        if (!iterables.iterator().hasNext()) {
            return empty();
        } else {
            return new ConcatIterator<>(Stream.ofAll(iterables).map(Iterator::ofAll).iterator());
        }
    }

    /**
     * Returns the empty Iterator.
     *
     * @param <T> Component type
     * @return The empty Iterator
     */
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> empty() {
        return (Iterator<T>) EMPTY;
    }

    /**
     * Narrows a widened {@code Iterator<? extends T>} to {@code Iterator<T>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * @param iterator An {@code Iterator}.
     * @param <T>      Component type of the {@code Iterator}.
     * @return the given {@code iterator} instance as narrowed type {@code Iterator<T>}.
     */
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> narrow(Iterator<? extends T> iterator) {
        return (Iterator<T>) iterator;
    }

    /**
     * Creates an Iterator which traverses one element.
     *
     * @param element An element
     * @param <T>     Component type.
     * @return A new Iterator
     */
    static <T> Iterator<T> of(T element) {
        return new AbstractIterator<T>() {

            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T getNext() {
                hasNext = false;
                return element;
            }
        };
    }

    /**
     * Creates an Iterator which traverses the given elements.
     *
     * @param elements Zero or more elements
     * @param <T>      Component type
     * @return A new Iterator
     */
    @SafeVarargs
    static <T> Iterator<T> of(T... elements) {
        Objects.requireNonNull(elements, "elements is null");
        if (elements.length == 0) {
            return empty();
        } else {
            return new AbstractIterator<T>() {

                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < elements.length;
                }

                @Override
                public T getNext() {
                    return elements[index++];
                }
            };
        }
    }

    /**
     * Creates an Iterator based on the given Iterable. This is a convenience method for
     * {@code Iterator.ofAll(iterable.iterator()}.
     *
     * @param iterable A {@link Iterable}
     * @param <T>      Component type.
     * @return A new {@code javaslang.collection.Iterator}
     */
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> ofAll(Iterable<? extends T> iterable) {
        Objects.requireNonNull(iterable, "iterable is null");
        if (iterable instanceof Iterator) {
            return (Iterator<T>) iterable;
        } else {
            return ofAll(iterable.iterator());
        }
    }

    /**
     * Creates an Iterator based on the given Iterator by
     * delegating calls of {@code hasNext()} and {@code next()} to it.
     *
     * @param iterator A {@link java.util.Iterator}
     * @param <T>      Component type.
     * @return A new {@code javaslang.collection.Iterator}
     */
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> ofAll(java.util.Iterator<? extends T> iterator) {
        Objects.requireNonNull(iterator, "iterator is null");
        if (iterator instanceof Iterator) {
            return (Iterator<T>) iterator;
        } else {
            return new AbstractIterator<T>() {

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public T getNext() {
                    return iterator.next();
                }
            };
        }
    }

    /**
     * Creates an Iterator based on the elements of a boolean array.
     *
     * @param array a boolean array
     * @return A new Iterator of Boolean values
     */
    static Iterator<Boolean> ofAll(boolean[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Boolean>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Boolean getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of a byte array.
     *
     * @param array a byte array
     * @return A new Iterator of Byte values
     */
    static Iterator<Byte> ofAll(byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Byte>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Byte getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of a char array.
     *
     * @param array a char array
     * @return A new Iterator of Character values
     */
    static Iterator<Character> ofAll(char[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Character>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Character getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates ann Iterator based on the elements of a double array.
     *
     * @param array a double array
     * @return A new Iterator of Double values
     */
    static Iterator<Double> ofAll(double[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Double getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of a float array.
     *
     * @param array a float array
     * @return A new Iterator of Float values
     */
    static Iterator<Float> ofAll(float[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Float>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Float getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of an int array.
     *
     * @param array an int array
     * @return A new Iterator of Integer values
     */
    static Iterator<Integer> ofAll(int[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Integer>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Integer getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of a long array.
     *
     * @param array a long array
     * @return A new Iterator of Long values
     */
    static Iterator<Long> ofAll(long[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Long>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Long getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Creates an Iterator based on the elements of a short array.
     *
     * @param array a short array
     * @return A new Iterator of Short values
     */
    static Iterator<Short> ofAll(short[] array) {
        Objects.requireNonNull(array, "array is null");
        return new AbstractIterator<Short>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public Short getNext() {
                return array[i++];
            }
        };
    }

    /**
     * Returns an Iterator on a sequence of {@code n} values of a given Function {@code f}
     * over a range of integer values from 0 to {@code n - 1}.
     *
     * @param <T> Component type of the Iterator
     * @param n   The number of elements
     * @param f   The Function computing element values
     * @return An Iterator on a sequence of elements {@code f(0),f(1), ..., f(n - 1)}
     * @throws NullPointerException if {@code f} is null
     */
    static <T> Iterator<T> tabulate(int n, Function<? super Integer, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        return Collections.tabulate(n, f);
    }

    /**
     * Returns an Iterator on a sequence of {@code n} values supplied by a given Supplier {@code s}.
     *
     * @param <T> Component type of the Iterator
     * @param n   The number of elements
     * @param s   The Supplier computing element values
     * @return An iterator on a sequence of {@code n} elements, where each element contains the result supplied by {@code s}.
     * @throws NullPointerException if {@code s} is null
     */
    static <T> Iterator<T> fill(int n, Supplier<? extends T> s) {
        Objects.requireNonNull(s, "s is null");
        return Collections.fill(n, s);
    }

    /**
     * Creates an Iterator of characters starting from {@code from}, extending to {@code toExclusive - 1}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.range('a', 'c')  // = ('a', 'b')
     * Iterator.range('c', 'a')  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first character
     * @param toExclusive the successor of the last character
     * @return a range of characters as specified or the empty range if {@code from >= toExclusive}
     */
    static Iterator<Character> range(char from, char toExclusive) {
        return rangeBy(from, toExclusive, 1);
    }

    /**
     * Creates an Iterator of characters starting from {@code from}, extending to {@code toExclusive - 1},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeBy('a', 'c', 1)  // = ('a', 'b')
     * Iterator.rangeBy('a', 'd', 2)  // = ('a', 'c')
     * Iterator.rangeBy('d', 'a', -2) // = ('d', 'b')
     * Iterator.rangeBy('d', 'a', 2)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first character
     * @param toExclusive the successor of the last character if step &gt; 0, the predecessor of the last character if step &lt; 0
     * @param step        the step
     * @return a range of characters as specified or the empty range if {@code signum(step) == signum(from - toExclusive)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Character> rangeBy(char from, char toExclusive, int step) {
        return rangeBy((int) from, (int) toExclusive, step).map(i -> (char) i.shortValue());
    }

    @GwtIncompatible("BigDecimalHelper is GwtIncompatible")
    static Iterator<Double> rangeBy(double from, double toExclusive, double step) {
        final BigDecimal fromDecimal = asDecimal(from), toDecimal = asDecimal(toExclusive), stepDecimal = asDecimal(step);
        return rangeBy(fromDecimal, toDecimal, stepDecimal).map(BigDecimal::doubleValue);
    }

    static Iterator<BigDecimal> rangeBy(BigDecimal from, BigDecimal toExclusive, BigDecimal step) {
        if (step.signum() == 0) {
            throw new IllegalArgumentException("step cannot be 0");
        } else if (areEqual(from, toExclusive) || step.signum() == from.subtract(toExclusive).signum()) {
            return empty();
        } else {
            if (step.signum() > 0) {
                return new AbstractIterator<BigDecimal>() {
                    BigDecimal i = from;

                    @Override
                    public boolean hasNext() {
                        return i.compareTo(toExclusive) < 0;
                    }

                    @Override
                    public BigDecimal getNext() {
                        final BigDecimal next = this.i;
                        this.i = next.add(step);
                        return next;
                    }
                };
            } else {
                return new AbstractIterator<BigDecimal>() {
                    BigDecimal i = from;

                    @Override
                    public boolean hasNext() {
                        return i.compareTo(toExclusive) > 0;
                    }

                    @Override
                    public BigDecimal getNext() {
                        final BigDecimal next = this.i;
                        this.i = next.add(step);
                        return next;
                    }
                };
            }
        }
    }

    /**
     * Creates an Iterator of int numbers starting from {@code from}, extending to {@code toExclusive - 1}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.range(0, 0)  // = ()
     * Iterator.range(2, 0)  // = ()
     * Iterator.range(-2, 2) // = (-2, -1, 0, 1)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @return a range of int values as specified or the empty range if {@code from >= toExclusive}
     */
    static Iterator<Integer> range(int from, int toExclusive) {
        return rangeBy(from, toExclusive, 1);
    }

    /**
     * Creates an Iterator of int numbers starting from {@code from}, extending to {@code toExclusive - 1},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeBy(1, 3, 1)  // = (1, 2)
     * Iterator.rangeBy(1, 4, 2)  // = (1, 3)
     * Iterator.rangeBy(4, 1, -2) // = (4, 2)
     * Iterator.rangeBy(4, 1, 2)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1 if step &gt; 0, the last number - 1 if step &lt; 0
     * @param step        the step
     * @return a range of long values as specified or the empty range if {@code (from == toExclusive) || (step * (from - toExclusive) > 0)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Integer> rangeBy(int from, int toExclusive, int step) {
        final int toInclusive = toExclusive - (step > 0 ? 1 : -1);
        return rangeClosedBy(from, toInclusive, step);
    }

    /**
     * Creates an Iterator of long numbers starting from {@code from}, extending to {@code toExclusive - 1}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.range(0L, 0L)  // = ()
     * Iterator.range(2L, 0L)  // = ()
     * Iterator.range(-2L, 2L) // = (-2L, -1L, 0L, 1L)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @return a range of long values as specified or the empty range if {@code from >= toExclusive}
     */
    static Iterator<Long> range(long from, long toExclusive) {
        return rangeBy(from, toExclusive, 1);
    }

    /**
     * Creates an Iterator of long numbers starting from {@code from}, extending to {@code toExclusive - 1},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeBy(1L, 3L, 1L)  // = (1L, 2L)
     * Iterator.rangeBy(1L, 4L, 2L)  // = (1L, 3L)
     * Iterator.rangeBy(4L, 1L, -2L) // = (4L, 2L)
     * Iterator.rangeBy(4L, 1L, 2L)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1 if step &gt; 0, the last number - 1 if step &lt; 0
     * @param step        the step
     * @return a range of long values as specified or the empty range if {@code (from == toExclusive) || (step * (from - toExclusive) > 0)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Long> rangeBy(long from, long toExclusive, long step) {
        final long toInclusive = toExclusive - (step > 0 ? 1 : -1);
        return rangeClosedBy(from, toInclusive, step);
    }

    /**
     * Creates an Iterator of characters starting from {@code from}, extending to {@code toInclusive}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosed('a', 'c')  // = ('a', 'b', 'c')
     * Iterator.rangeClosed('c', 'a')  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first character
     * @param toInclusive the last character
     * @return a range of characters as specified or the empty range if {@code from > toInclusive}
     */
    static Iterator<Character> rangeClosed(char from, char toInclusive) {
        return rangeClosedBy(from, toInclusive, 1);
    }

    /**
     * Creates an Iterator of characters starting from {@code from}, extending to {@code toInclusive},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosedBy('a', 'c', 1)  // = ('a', 'b', 'c')
     * Iterator.rangeClosedBy('a', 'd', 2)  // = ('a', 'c')
     * Iterator.rangeClosedBy('d', 'a', -2) // = ('d', 'b')
     * Iterator.rangeClosedBy('d', 'a', 2)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first character
     * @param toInclusive the last character
     * @param step        the step
     * @return a range of characters as specified or the empty range if {@code signum(step) == signum(from - toInclusive)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Character> rangeClosedBy(char from, char toInclusive, int step) {
        return rangeClosedBy((int) from, (int) toInclusive, step).map(i -> (char) i.shortValue());
    }

    @GwtIncompatible
    static Iterator<Double> rangeClosedBy(double from, double toInclusive, double step) {
        if (from == toInclusive) {
            return of(from);
        }

        final double toExclusive = (step > 0) ? Math.nextUp(toInclusive) : Math.nextDown(toInclusive);
        return rangeBy(from, toExclusive, step);
    }

    /**
     * Creates an Iterator of int numbers starting from {@code from}, extending to {@code toInclusive}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosed(0, 0)  // = (0)
     * Iterator.rangeClosed(2, 0)  // = ()
     * Iterator.rangeClosed(-2, 2) // = (-2, -1, 0, 1, 2)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @return a range of int values as specified or the empty range if {@code from > toInclusive}
     */
    static Iterator<Integer> rangeClosed(int from, int toInclusive) {
        return rangeClosedBy(from, toInclusive, 1);
    }

    /**
     * Creates an Iterator of int numbers starting from {@code from}, extending to {@code toInclusive},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosedBy(1, 3, 1)  // = (1, 2, 3)
     * Iterator.rangeClosedBy(1, 4, 2)  // = (1, 3)
     * Iterator.rangeClosedBy(4, 1, -2) // = (4, 2)
     * Iterator.rangeClosedBy(4, 1, 2)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @param step        the step
     * @return a range of int values as specified or the empty range if {@code signum(step) == signum(from - toInclusive)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Integer> rangeClosedBy(int from, int toInclusive, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("step cannot be 0");
        } else if (from == toInclusive) {
            return of(from);
        } else if (Integer.signum(step) == Integer.signum(from - toInclusive)) {
            return empty();
        } else {
            final int end = toInclusive - step;
            if (step > 0) {
                return new AbstractIterator<Integer>() {
                    int i = from - step;

                    @Override
                    public boolean hasNext() {
                        return i <= end;
                    }

                    @Override
                    public Integer getNext() {
                        return i += step;
                    }
                };
            } else {
                return new AbstractIterator<Integer>() {
                    int i = from - step;

                    @Override
                    public boolean hasNext() {
                        return i >= end;
                    }

                    @Override
                    public Integer getNext() {
                        return i += step;
                    }
                };
            }
        }
    }

    /**
     * Creates an Iterator of long numbers starting from {@code from}, extending to {@code toInclusive}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosed(0L, 0L)  // = (0L)
     * Iterator.rangeClosed(2L, 0L)  // = ()
     * Iterator.rangeClosed(-2L, 2L) // = (-2L, -1L, 0L, 1L, 2L)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @return a range of long values as specified or the empty range if {@code from > toInclusive}
     */
    static Iterator<Long> rangeClosed(long from, long toInclusive) {
        return rangeClosedBy(from, toInclusive, 1L);
    }

    /**
     * Creates an Iterator of long numbers starting from {@code from}, extending to {@code toInclusive},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Iterator.rangeClosedBy(1L, 3L, 1L)  // = (1L, 2L, 3L)
     * Iterator.rangeClosedBy(1L, 4L, 2L)  // = (1L, 3L)
     * Iterator.rangeClosedBy(4L, 1L, -2L) // = (4L, 2L)
     * Iterator.rangeClosedBy(4L, 1L, 2L)  // = ()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @param step        the step
     * @return a range of int values as specified or the empty range if {@code signum(step) == signum(from - toInclusive)}.
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Iterator<Long> rangeClosedBy(long from, long toInclusive, long step) {
        if (step == 0) {
            throw new IllegalArgumentException("step cannot be 0");
        } else if (from == toInclusive) {
            return of(from);
        } else if (Long.signum(step) == Long.signum(from - toInclusive)) {
            return empty();
        } else {
            final long end = toInclusive - step;
            if (step > 0) {
                return new AbstractIterator<Long>() {
                    long i = from - step;

                    @Override
                    public boolean hasNext() {
                        return i <= end;
                    }

                    @Override
                    public Long getNext() {
                        return i += step;
                    }
                };
            } else {
                return new AbstractIterator<Long>() {
                    long i = from - step;

                    @Override
                    public boolean hasNext() {
                        return i >= end;
                    }

                    @Override
                    public Long getNext() {
                        return i += step;
                    }
                };

            }
        }
    }

    /**
     * Returns an infinite iterator of int values starting from {@code value}.
     * <p>
     * The {@code Iterator} extends to {@code Integer.MIN_VALUE} when passing {@code Integer.MAX_VALUE}.
     *
     * @param value a start int value
     * @return a new {@code Iterator} of int values starting from {@code from}
     */
    static Iterator<Integer> from(int value) {
        return new AbstractIterator<Integer>() {
            private int next = value;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer getNext() {
                return next++;
            }
        };
    }

    /**
     * Returns an infinite iterator of int values starting from {@code value} and spaced by {@code step}.
     * <p>
     * The {@code Iterator} extends to {@code Integer.MIN_VALUE} when passing {@code Integer.MAX_VALUE}.
     *
     * @param value a start int value
     * @param step  the step by which to advance on each iteration
     * @return a new {@code Iterator} of int values starting from {@code from}
     */
    static Iterator<Integer> from(int value, int step) {
        return new AbstractIterator<Integer>() {
            private int next = value;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer getNext() {
                final int result = next;
                next += step;
                return result;
            }
        };
    }

    /**
     * Returns an infinite iterator of long values starting from {@code value}.
     * <p>
     * The {@code Iterator} extends to {@code Long.MIN_VALUE} when passing {@code Long.MAX_VALUE}.
     *
     * @param value a start long value
     * @return a new {@code Iterator} of long values starting from {@code from}
     */
    static Iterator<Long> from(long value) {
        return new AbstractIterator<Long>() {
            private long next = value;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Long getNext() {
                return next++;
            }
        };
    }

    /**
     * Returns an infinite iterator of long values starting from {@code value} and spaced by {@code step}.
     * <p>
     * The {@code Iterator} extends to {@code Long.MIN_VALUE} when passing {@code Long.MAX_VALUE}.
     *
     * @param value a start long value
     * @param step  the step by which to advance on each iteration
     * @return a new {@code Iterator} of long values starting from {@code from}
     */
    static Iterator<Long> from(long value, long step) {
        return new AbstractIterator<Long>() {
            private long next = value;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Long getNext() {
                final long result = next;
                next += step;
                return result;
            }
        };
    }

    /**
     * Generates an infinite iterator using a value Supplier.
     *
     * @param supplier A Supplier of iterator values
     * @param <T>      value type
     * @return A new {@code Iterator}
     */
    static <T> Iterator<T> continually(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return new AbstractIterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T getNext() {
                return supplier.get();
            }
        };
    }

    /**
     * Generates an infinite iterator using a function to calculate the next value
     * based on the previous.
     *
     * @param seed The first value in the iterator
     * @param f    A function to calculate the next value based on the previous
     * @param <T>  value type
     * @return A new {@code Iterator}
     */
    static <T> Iterator<T> iterate(T seed, Function<? super T, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        return new AbstractIterator<T>() {
            Function<? super T, ? extends T> nextFunc = s -> {
                nextFunc = f;
                return seed;
            };
            T current = null;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T getNext() {
                current = nextFunc.apply(current);
                return current;
            }
        };
    }

    /**
     * Creates an infinite iterator returning the given element.
     *
     * @param t   An element
     * @param <T> Element type
     * @return A new Iterator containing infinite {@code t}'s.
     */
    static <T> Iterator<T> continually(T t) {
        return new AbstractIterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T getNext() {
                return t;
            }
        };
    }

    // -- Additional methods of Iterator

    // DEV-NOTE: cannot use arg Iterable, it would be ambiguous
    default Iterator<T> concat(java.util.Iterator<? extends T> that) {
        Objects.requireNonNull(that, "that is null");
        if (!that.hasNext()) {
            return this;
        } else if (!hasNext()) {
            return ofAll(that);
        } else {
            return concat(this, ofAll(that));
        }
    }

    /**
     * Inserts an element between all elements of this Iterator.
     *
     * @param element An element.
     * @return an interspersed version of this
     */
    default Iterator<T> intersperse(T element) {
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                boolean insertElement = false;

                @Override
                public boolean hasNext() {
                    return that.hasNext();
                }

                @Override
                public T getNext() {
                    if (insertElement) {
                        insertElement = false;
                        return element;
                    } else {
                        insertElement = true;
                        return that.next();
                    }
                }
            };
        }
    }

    /**
     * Transforms this {@code Iterator}.
     *
     * @param f   A transformation
     * @param <U> Type of transformation result
     * @return An instance of type {@code U}
     * @throws NullPointerException if {@code f} is null
     */
    default <U> U transform(Function<? super Iterator<T>, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(this);
    }

    @Override
    default <U> Iterator<Tuple2<T, U>> zip(Iterable<? extends U> that) {
        return zipWith(that, Tuple::of);
    }

    @Override
    default <U, R> Iterator<R> zipWith(Iterable<? extends U> that, BiFunction<? super T, ? super U, ? extends R> mapper) {
        Objects.requireNonNull(that, "that is null");
        Objects.requireNonNull(mapper, "mapper is null");
        if (isEmpty()) {
            return empty();
        } else {
            final Iterator<T> it1 = this;
            final java.util.Iterator<? extends U> it2 = that.iterator();
            return new AbstractIterator<R>() {
                @Override
                public boolean hasNext() {
                    return it1.hasNext() && it2.hasNext();
                }

                @Override
                public R getNext() {
                    return mapper.apply(it1.next(), it2.next());
                }
            };
        }
    }

    @Override
    default <U> Iterator<Tuple2<T, U>> zipAll(Iterable<? extends U> that, T thisElem, U thatElem) {
        Objects.requireNonNull(that, "that is null");
        final java.util.Iterator<? extends U> thatIt = that.iterator();
        if (isEmpty() && !thatIt.hasNext()) {
            return empty();
        } else {
            final Iterator<T> thisIt = this;
            return new AbstractIterator<Tuple2<T, U>>() {
                @Override
                public boolean hasNext() {
                    return thisIt.hasNext() || thatIt.hasNext();
                }

                @Override
                public Tuple2<T, U> getNext() {
                    final T v1 = thisIt.hasNext() ? thisIt.next() : thisElem;
                    final U v2 = thatIt.hasNext() ? thatIt.next() : thatElem;
                    return Tuple.of(v1, v2);
                }
            };
        }
    }

    @Override
    default Iterator<Tuple2<T, Integer>> zipWithIndex() {
        return zipWithIndex(Tuple::of);
    }

    @Override
    default <U> Iterator<U> zipWithIndex(BiFunction<? super T, ? super Integer, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isEmpty()) {
            return empty();
        } else {
            final Iterator<T> it1 = this;
            return new AbstractIterator<U>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return it1.hasNext();
                }

                @Override
                public U getNext() {
                    return mapper.apply(it1.next(), index++);
                }
            };
        }
    }

    @Override
    default <T1, T2> Tuple2<Iterator<T1>, Iterator<T2>> unzip(
            Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
        Objects.requireNonNull(unzipper, "unzipper is null");
        if (!hasNext()) {
            return Tuple.of(empty(), empty());
        } else {
            final Stream<Tuple2<? extends T1, ? extends T2>> source = Stream.ofAll(this.map(unzipper));
            return Tuple.of(source.map(t -> (T1) t._1).iterator(), source.map(t -> (T2) t._2).iterator());
        }
    }

    @Override
    default <T1, T2, T3> Tuple3<Iterator<T1>, Iterator<T2>, Iterator<T3>> unzip3(
            Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
        Objects.requireNonNull(unzipper, "unzipper is null");
        if (!hasNext()) {
            return Tuple.of(empty(), empty(), empty());
        } else {
            final Stream<Tuple3<? extends T1, ? extends T2, ? extends T3>> source = Stream.ofAll(this.map(unzipper));
            return Tuple.of(source.map(t -> (T1) t._1).iterator(), source.map(t -> (T2) t._2).iterator(), source.map(t -> (T3) t._3).iterator());
        }
    }

    /**
     * Creates an iterator from a seed value and a function.
     * The function takes the seed at first.
     * The function should return {@code None} when it's
     * done generating elements, otherwise {@code Some} {@code Tuple}
     * of the value to add to the resulting iterator and
     * the element for the next call.
     * <p>
     * Example:
     * <pre>
     * <code>
     * Iterator.unfold(10, x -&gt; x == 0
     *                 ? Option.none()
     *                 : Option.of(new Tuple2&lt;&gt;(x-1, x)));
     * // List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
     * </code>
     * </pre>
     *
     * @param <T>  type of seeds and unfolded values
     * @param seed the start value for the iteration
     * @param f    the function to get the next step of the iteration
     * @return a list with the values built up by the iteration
     * @throws NullPointerException if {@code f} is null
     */
    static <T> Iterator<T> unfold(T seed, Function<? super T, Option<Tuple2<? extends T, ? extends T>>> f) {
        return unfoldLeft(seed, f);
    }

    /**
     * Creates an iterator from a seed value and a function.
     * The function takes the seed at first.
     * The function should return {@code None} when it's
     * done generating elements, otherwise {@code Some} {@code Tuple}
     * of the value to add to the resulting iterator and
     * the element for the next call.
     * <p>
     * Example:
     * <pre>
     * <code>
     * Iterator.unfoldLeft(10, x -&gt; x == 0
     *                    ? Option.none()
     *                    : Option.of(new Tuple2&lt;&gt;(x-1, x)));
     * // List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
     * </code>
     * </pre>
     *
     * @param <T>  type of seeds
     * @param <U>  type of unfolded values
     * @param seed the start value for the iteration
     * @param f    the function to get the next step of the iteration
     * @return a list with the values built up by the iteration
     * @throws NullPointerException if {@code f} is null
     */
    static <T, U> Iterator<U> unfoldLeft(T seed, Function<? super T, Option<Tuple2<? extends T, ? extends U>>> f) {
        return Stream.<U> ofAll(
                unfoldRight(seed, f.andThen(tupleOpt -> tupleOpt.map(t -> Tuple.of(t._2, t._1)))))
                .reverse().iterator();
    }

    /**
     * Creates an iterator from a seed value and a function.
     * The function takes the seed at first.
     * The function should return {@code None} when it's
     * done generating elements, otherwise {@code Some} {@code Tuple}
     * of the element for the next call and the value to add to the
     * resulting iterator.
     * <p>
     * Example:
     * <pre>
     * <code>
     * Iterator.unfoldRight(10, x -&gt; x == 0
     *             ? Option.none()
     *             : Option.of(new Tuple2&lt;&gt;(x, x-1)));
     * // List(10, 9, 8, 7, 6, 5, 4, 3, 2, 1))
     * </code>
     * </pre>
     *
     * @param <T>  type of seeds
     * @param <U>  type of unfolded values
     * @param seed the start value for the iteration
     * @param f    the function to get the next step of the iteration
     * @return a list with the values built up by the iteration
     * @throws NullPointerException if {@code f} is null
     */
    static <T, U> Iterator<U> unfoldRight(T seed, Function<? super T, Option<Tuple2<? extends U, ? extends T>>> f) {
        Objects.requireNonNull(f, "the unfold iterating function is null");
        return new AbstractIterator<U>() {
            private Option<Tuple2<? extends U, ? extends T>> nextVal = f.apply(seed);

            @Override
            public boolean hasNext() {
                return nextVal.isDefined();
            }

            @Override
            public U getNext() {
                final U result = nextVal.get()._1;
                nextVal = f.apply(nextVal.get()._2);
                return result;
            }
        };
    }

    // -- Overridden methods of Traversable

    @Override
    default Iterator<T> distinct() {
        if (!hasNext()) {
            return empty();
        } else {
            return new DistinctIterator<>(this, HashSet.empty(), Function.identity());
        }
    }

    @Override
    default Iterator<T> distinctBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        if (!hasNext()) {
            return empty();
        } else {
            return new DistinctIterator<>(this, TreeSet.empty(comparator), Function.identity());
        }
    }

    @Override
    default <U> Iterator<T> distinctBy(Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "keyExtractor is null");
        if (!hasNext()) {
            return empty();
        } else {
            return new DistinctIterator<>(this, HashSet.empty(), keyExtractor);
        }
    }

    /**
     * Removes up to n elements from this iterator.
     *
     * @param n A number
     * @return The empty iterator, if {@code n <= 0} or this is empty, otherwise a new iterator without the first n elements.
     */
    @Override
    default Iterator<T> drop(int n) {
        if (n <= 0) {
            return this;
        } else if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                long count = n;

                @Override
                public boolean hasNext() {
                    while (count > 0 && that.hasNext()) {
                        that.next(); // discarded
                        count--;
                    }
                    return that.hasNext();
                }

                @Override
                public T getNext() {
                    return that.next();
                }
            };
        }
    }

    @Override
    default Iterator<T> dropRight(int n) {
        if (n <= 0) {
            return this;
        } else if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {
                private Queue<T> queue = Queue.empty();

                @Override
                public boolean hasNext() {
                    while (queue.length() < n && that.hasNext()) {
                        queue = queue.append(that.next());
                    }
                    return queue.length() == n && that.hasNext();
                }

                @Override
                public T getNext() {
                    final Tuple2<T, Queue<T>> t = queue.append(that.next()).dequeue();
                    queue = t._2;
                    return t._1;
                }
            };
        }
    }

    @Override
    default Iterator<T> dropUntil(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return dropWhile(predicate.negate());
    }

    @Override
    default Iterator<T> dropWhile(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                private T next = null;

                @Override
                public boolean hasNext() {
                    while (next == null && that.hasNext()) {
                        final T value = that.next();
                        if (!predicate.test(value)) {
                            next = value;
                        }
                    }
                    return next != null;
                }

                @Override
                public T getNext() {
                    final T result = next;
                    next = null;
                    return result;
                }
            };
        }
    }

    /**
     * Returns an Iterator that contains elements that satisfy the given {@code predicate}.
     *
     * @param predicate A predicate
     * @return A new Iterator
     */
    @Override
    default Iterator<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                Option<T> next = Option.none();

                @Override
                public boolean hasNext() {
                    while (next.isEmpty() && that.hasNext()) {
                        final T candidate = that.next();
                        if (predicate.test(candidate)) {
                            next = Option.some(candidate);
                        }
                    }
                    return next.isDefined();
                }

                @Override
                public T getNext() {
                    final T result = next.get();
                    next = Option.none();
                    return result;
                }
            };
        }
    }

    @Override
    default Option<T> findLast(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        T last = null;
        while (hasNext()) {
            final T elem = next();
            if (predicate.test(elem)) {
                last = elem;
            }
        }
        return Option.of(last);
    }

    /**
     * FlatMaps the elements of this Iterator to Iterables, which are iterated in the order of occurrence.
     *
     * @param mapper A mapper
     * @param <U>    Component type
     * @return A new Iterable
     */
    @Override
    default <U> Iterator<U> flatMap(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<U>() {

                final Iterator<? extends T> inputs = that;
                java.util.Iterator<? extends U> current = java.util.Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    boolean currentHasNext;
                    while (!(currentHasNext = current.hasNext()) && inputs.hasNext()) {
                        current = mapper.apply(inputs.next()).iterator();
                    }
                    return currentHasNext;
                }

                @Override
                public U getNext() {
                    return current.next();
                }
            };
        }
    }

    @Override
    default <U> U foldRight(U zero, BiFunction<? super T, ? super U, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return Stream.ofAll(this).foldRight(zero, f);
    }

    @Override
    default T get() {
        return head();
    }

    @Override
    default <C> Map<C, Iterator<T>> groupBy(Function<? super T, ? extends C> classifier) {
        return Collections.groupBy(this, classifier, Iterator::ofAll);
    }

    @Override
    default Iterator<Seq<T>> grouped(int size) {
        return sliding(size, size);
    }

    @Override
    default boolean hasDefiniteSize() {
        return false;
    }

    @Override
    default T head() {
        if (!hasNext()) {
            throw new NoSuchElementException("head() on empty iterator");
        }
        return next();
    }

    @Override
    default Iterator<T> init() {
        if (!hasNext()) {
            throw new UnsupportedOperationException();
        } else {
            return dropRight(1);
        }
    }

    @Override
    default Option<Iterator<T>> initOption() {
        return hasNext() ? Option.some(init()) : Option.none();
    }

    @Override
    default boolean isEmpty() {
        return !hasNext();
    }

    @Override
    default boolean isTraversableAgain() {
        return false;
    }

    @Override
    default Iterator<T> iterator() {
        return this;
    }

    @Override
    default int length() {
        return foldLeft(0, (n, ignored) -> n + 1);
    }

    /**
     * Maps the elements of this Iterator lazily using the given {@code mapper}.
     *
     * @param mapper A mapper.
     * @param <U>    Component type
     * @return A new Iterator
     */
    @Override
    default <U> Iterator<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<U>() {

                @Override
                public boolean hasNext() {
                    return that.hasNext();
                }

                @Override
                public U getNext() {
                    return mapper.apply(that.next());
                }
            };
        }
    }

    @Override
    default Tuple2<Iterator<T>, Iterator<T>> partition(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (!hasNext()) {
            return Tuple.of(empty(), empty());
        } else {
            final Stream<T> that = Stream.ofAll(this);
            final Iterator<T> first = that.iterator().filter(predicate);
            final Iterator<T> second = that.iterator().filter(predicate.negate());
            return Tuple.of(first, second);
        }
    }

    @Override
    default Iterator<T> peek(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {
                @Override
                public boolean hasNext() {
                    return that.hasNext();
                }

                @Override
                public T getNext() {
                    final T next = that.next();
                    action.accept(next);
                    return next;
                }
            };
        }
    }

    @Override
    default T reduceLeft(BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op, "op is null");
        if (isEmpty()) {
            throw new NoSuchElementException("reduceLeft on Nil");
        } else {
            final Stream<T> stream = Stream.ofAll(this);
            return stream.tail().foldLeft(stream.head(), op);
        }
    }

    @Override
    default T reduceRight(BiFunction<? super T, ? super T, ? extends T> op) {
        Objects.requireNonNull(op, "op is null");
        if (isEmpty()) {
            throw new NoSuchElementException("reduceRight on Nil");
        } else {
            final Stream<T> reversed = Stream.ofAll(this).reverse();
            return reversed.reduceLeft((xs, x) -> op.apply(x, xs));
        }
    }

    @Override
    default Iterator<T> replace(T currentElement, T newElement) {
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {
                boolean isFirst = true;

                @Override
                public boolean hasNext() {
                    return that.hasNext();
                }

                @Override
                public T getNext() {
                    final T elem = that.next();
                    if (isFirst && Objects.equals(currentElement, elem)) {
                        isFirst = false;
                        return newElement;
                    } else {
                        return elem;
                    }
                }
            };
        }
    }

    @Override
    default Iterator<T> replaceAll(T currentElement, T newElement) {
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                @Override
                public boolean hasNext() {
                    return that.hasNext();
                }

                @Override
                public T getNext() {
                    final T elem = that.next();
                    if (Objects.equals(currentElement, elem)) {
                        return newElement;
                    } else {
                        return elem;
                    }
                }
            };
        }
    }

    @Override
    default Iterator<T> retainAll(Iterable<? extends T> elements) {
        return Collections.retainAll(this, elements);
    }

    @Override
    default Traversable<T> scan(T zero, BiFunction<? super T, ? super T, ? extends T> operation) {
        return scanLeft(zero, operation);
    }

    @Override
    default <U> Iterator<U> scanLeft(U zero, BiFunction<? super U, ? super T, ? extends U> operation) {
        Objects.requireNonNull(operation, "operation is null");
        if (isEmpty()) {
            return of(zero);
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<U>() {

                boolean isFirst = true;
                U acc = zero;

                @Override
                public boolean hasNext() {
                    return isFirst || that.hasNext();
                }

                @Override
                public U getNext() {
                    if (isFirst) {
                        isFirst = false;
                        return acc;
                    } else {
                        acc = operation.apply(acc, that.next());
                        return acc;
                    }
                }
            };
        }
    }

    // not lazy!
    @Override
    default <U> Iterator<U> scanRight(U zero, BiFunction<? super T, ? super U, ? extends U> operation) {
        Objects.requireNonNull(operation, "operation is null");
        if (isEmpty()) {
            return of(zero);
        } else {
            return Collections.scanRight(this, zero, operation, Stream.empty(), Stream::prepend, Stream::iterator);
        }
    }

    @Override
    default Iterator<Seq<T>> sliding(int size) {
        return sliding(size, 1);
    }

    @Override
    default Iterator<Seq<T>> sliding(int size, int step) {
        if (size <= 0 || step <= 0) {
            throw new IllegalArgumentException("size: " + size + " or step: " + step + " not positive");
        }
        if (!hasNext()) {
            return empty();
        } else {
            final Stream<T> source = Stream.ofAll(this);
            return new AbstractIterator<Seq<T>>() {
                private Stream<T> that = source;
                private IndexedSeq<T> next = null;

                @Override
                public boolean hasNext() {
                    while (next == null && !that.isEmpty()) {
                        final Tuple2<Stream<T>, Stream<T>> split = that.splitAt(size);
                        next = split._1.toVector();
                        that = split._2.isEmpty() ? Stream.empty() : that.drop(step);
                    }
                    return next != null;
                }

                @Override
                public IndexedSeq<T> getNext() {
                    final IndexedSeq<T> result = next;
                    next = null;
                    return result;
                }
            };
        }
    }

    @Override
    default Tuple2<Iterator<T>, Iterator<T>> span(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (!hasNext()) {
            return Tuple.of(empty(), empty());
        } else {
            final Stream<T> that = Stream.ofAll(this);
            return Tuple.of(that.iterator().takeWhile(predicate), that.iterator().dropWhile(predicate));
        }
    }

    @Override
    default Spliterator<T> spliterator() {
        // the focus of the Stream API is on random-access collections of *known size*
        final Stream<T> stream = Stream.ofAll(this);
        return Spliterators.spliterator(stream.iterator(), stream.length(),
                                        Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @Override
    default String stringPrefix() {
        return "Iterator";
    }

    @Override
    default Iterator<T> tail() {
        if (!hasNext()) {
            throw new UnsupportedOperationException();
        } else {
            next(); // remove first element
            return this;
        }
    }

    @Override
    default Option<Iterator<T>> tailOption() {
        if (hasNext()) {
            next();
            return Option.some(this);
        } else {
            return Option.none();
        }
    }

    /**
     * Take the first n elements from this iterator.
     *
     * @param n A number
     * @return The empty iterator, if {@code n <= 0} or this is empty, otherwise a new iterator without the first n elements.
     */
    @Override
    default Iterator<T> take(int n) {
        if (n <= 0 || !hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                long count = n;

                @Override
                public boolean hasNext() {
                    return count > 0 && that.hasNext();
                }

                @Override
                public T getNext() {
                    count--;
                    return that.next();
                }
            };
        }
    }

    @Override
    default Iterator<T> takeRight(int n) {
        if (n <= 0) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {
                private Queue<T> queue = Queue.empty();

                @Override
                public boolean hasNext() {
                    while (that.hasNext()) {
                        queue = queue.enqueue(that.next());
                        if (queue.length() > n) {
                            queue = queue.dequeue()._2;
                        }
                    }
                    return queue.length() > 0;
                }

                @Override
                public T getNext() {
                    final Tuple2<T, Queue<T>> t = queue.dequeue();
                    queue = t._2;
                    return t._1;
                }
            };
        }
    }

    @Override
    default Iterator<T> takeUntil(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return takeWhile(predicate.negate());
    }

    @Override
    default Iterator<T> takeWhile(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (!hasNext()) {
            return empty();
        } else {
            final Iterator<T> that = this;
            return new AbstractIterator<T>() {

                private T next = null;
                private boolean finished = false;

                @Override
                public boolean hasNext() {
                    while (!finished && next == null && that.hasNext()) {
                        final T value = that.next();
                        if (predicate.test(value)) {
                            next = value;
                        } else {
                            finished = true;
                        }
                    }
                    return next != null;
                }

                @Override
                public T getNext() {
                    final T result = next;
                    next = null;
                    return result;
                }
            };
        }
    }
}

interface IteratorModule {
    final class ConcatIterator<T> extends AbstractIterator<T> {
        private final Iterator<? extends Iterator<? extends T>> iterators;
        private Iterator<? extends T> current;

        ConcatIterator(Iterator<? extends Iterator<? extends T>> iterators) {
            this.current = Iterator.empty();
            this.iterators = iterators;
        }

        @Override
        public boolean hasNext() {
            while (!current.hasNext() && !iterators.isEmpty()) {
                current = iterators.next();
            }
            return current.hasNext();
        }

        @Override
        public T getNext() {
            return current.next();
        }
    }

    final class DistinctIterator<T, U> extends AbstractIterator<T> {
        private final Iterator<? extends T> that;
        private Set<U> known;
        private final Function<? super T, ? extends U> keyExtractor;
        private T next = null;

        DistinctIterator(Iterator<? extends T> that, Set<U> set, Function<? super T, ? extends U> keyExtractor) {
            this.that = that;
            this.known = set;
            this.keyExtractor = keyExtractor;
        }

        @Override
        public boolean hasNext() {
            while (next == null && that.hasNext()) {
                final T elem = that.next();
                final U key = keyExtractor.apply(elem);
                if (!known.contains(key)) {
                    known = known.add(key);
                    next = elem;
                }
            }
            return next != null;
        }

        @Override
        public T getNext() {
            final T result = next;
            next = null;
            return result;
        }
    }

    final class BigDecimalHelper {
        @GwtIncompatible("Math::nextDown is not implemented")
        private static final Lazy<BigDecimal> INFINITY_DISTANCE = Lazy.of(() -> {
            final BigDecimal two = BigDecimal.valueOf(2);
            final BigDecimal supremum = BigDecimal.valueOf(Math.nextDown(Double.POSITIVE_INFINITY));
            BigDecimal lowerBound = supremum;
            BigDecimal upperBound = two.pow(Double.MAX_EXPONENT + 1);
            while (true) {
                final BigDecimal magicValue = lowerBound.add(upperBound).divide(two, HALF_UP);
                if (Double.isInfinite(magicValue.doubleValue())) {
                    if (areEqual(magicValue, upperBound)) {
                        return magicValue.subtract(supremum);
                    }
                    upperBound = magicValue;
                } else {
                    lowerBound = magicValue;
                }
            }
        });

        /* scale-independent equality */
        static boolean areEqual(BigDecimal from, BigDecimal toExclusive) {
            return from.compareTo(toExclusive) == 0;
        }

        /* parse infinite values also */
        @GwtIncompatible("Math::nextUp is not implemented")
        static BigDecimal asDecimal(double number) {
            if (number == NEGATIVE_INFINITY) {
                final BigDecimal result = BigDecimal.valueOf(Math.nextUp(NEGATIVE_INFINITY));
                return result.subtract(INFINITY_DISTANCE.get());
            } else if (number == POSITIVE_INFINITY) {
                final BigDecimal result = BigDecimal.valueOf(Math.nextDown(POSITIVE_INFINITY));
                return result.add(INFINITY_DISTANCE.get());
            } else {
                return BigDecimal.valueOf(number);
            }
        }
    }
}
