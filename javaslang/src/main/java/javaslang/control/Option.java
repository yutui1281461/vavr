/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.control;

import javaslang.Value;
import javaslang.collection.Iterator;
import javaslang.collection.List;
import javaslang.collection.*;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

/**
 * Replacement for {@link java.util.Optional}.
 * <p>
 * Option is a <a href="http://stackoverflow.com/questions/13454347/monads-with-java-8">monadic</a> container type which
 * represents an optional value. Instances of Option are either an instance of {@link Some} or the
 * singleton {@link None}.
 * <p>
 * Most of the API is taken from {@link java.util.Optional}. A similar type can be found in <a
 * href="http://hackage.haskell.org/package/base-4.6.0.1/docs/Data-Maybe.html">Haskell</a> and <a
 * href="http://www.scala-lang.org/api/current/#scala.Option">Scala</a>.
 *
 * @param <T> The type of the optional value.
 * @author Daniel Dietrich
 * @since 1.0.0
 */
public interface Option<T> extends Value<T> {

    /**
     * Creates a new {@code Option} of a given value.
     *
     * @param value A value
     * @param <T>   type of the value
     * @return {@code Some(value)} if value is not {@code null}, {@code None} otherwise
     */
    static <T> Option<T> of(T value) {
        return (value == null) ? none() : some(value);
    }

    /**
     * Reduces many {@code Option}s into a single {@code Option} by transforming an
     * {@code Iterable<Option<? extends T>>} into a {@code Option<Seq<T>>}. If any of
     * the Options are {@link Option.None}, then this returns {@link Option.None}.
     *
     * @param values An {@code Iterable} of {@code Option}s
     * @param <T>    type of the Options
     * @return An {@code Option} of a {@link Seq} of results
     * @throws NullPointerException if {@code values} is null
     */
    static <T> Option<Seq<T>> sequence(Iterable<? extends Option<? extends T>> values) {
        Objects.requireNonNull(values, "values is null");
        List<T> list = List.empty();
        for (Option<? extends T> value : values) {
            if (value.isEmpty()) {
                return Option.none();
            }
            list = list.prepend(value.get());
        }
        return Option.some(list.reverse());
    }

    /**
     * Creates a new {@code Some} of a given value.
     * <p>
     * The only difference to {@link Option#of(Object)} is, when called with argument {@code null}.
     * <pre>
     * <code>
     * Option.of(null);   // = None
     * Option.some(null); // = Some(null)
     * </code>
     * </pre>
     *
     * @param value A value
     * @param <T>   type of the value
     * @return {@code Some(value)}
     */
    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    /**
     * Returns the single instance of {@code None}
     *
     * @param <T> component type
     * @return the single instance of {@code None}
     */
    static <T> Option<T> none() {
        @SuppressWarnings("unchecked")
        final None<T> none = (None<T>) None.INSTANCE;
        return none;
    }

    /**
     * Return the singleton instance of {@code Some<Void>}.
     *
     * @return {@code Some<Void>}
     */
    static Option<Void> nothing() {
        return Some.NOTHING;
    }

    /**
     * Narrows a widened {@code Option<? extends T>} to {@code Option<T>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * @param option A {@code Option}.
     * @param <T>    Component type of the {@code Option}.
     * @return the given {@code option} instance as narrowed type {@code Option<T>}.
     */
    @SuppressWarnings("unchecked")
    static <T> Option<T> narrow(Option<? extends T> option) {
        return (Option<T>) option;
    }

    /**
     * Creates {@code Some} of suppliers value if condition is true, or {@code None} in other case
     *
     * @param <T>       type of the optional value
     * @param condition A boolean value
     * @param supplier  An optional value supplier, may supply {@code null}
     * @return return {@code Some} of supplier's value if condition is true, or {@code None} in other case
     * @throws NullPointerException if the given {@code supplier} is null
     */
    static <T> Option<T> when(boolean condition, Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return condition ? some(supplier.get()) : none();
    }

    /**
     * Creates {@code Some} of value if condition is true, or {@code None} in other case
     *
     * @param <T>       type of the optional value
     * @param condition A boolean value
     * @param value     An optional value, may be {@code null}
     * @return return {@code Some} of value if condition is true, or {@code None} in other case
     */
    static <T> Option<T> when(boolean condition, T value) {
        return condition ? some(value) : none();
    }

    /**
     * Wraps a Java Optional to a new Option
     *
     * @param optional a given optional to wrap in {@code Option}
     * @param <T>      type of the value
     * @return {@code Some(optional.get())} if value is Java {@code Optional} is present, {@code None} otherwise
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Option<T> ofOptional(Optional<? extends T> optional) {
        Objects.requireNonNull(optional, "optional is null");
        return optional.isPresent() ? of(optional.get()) : none();
    }

    /**
     * Returns true, if this is {@code None}, otherwise false, if this is {@code Some}.
     *
     * @return true, if this {@code Option} is empty, false otherwise
     */
    @Override
    boolean isEmpty();

    /**
     * Returns true, if this is {@code Some}, otherwise false, if this is {@code None}.
     * <p>
     * Please note that it is possible to create {@code new Some(null)}, which is defined.
     *
     * @return true, if this {@code Option} has a defined value, false otherwise
     */
    default boolean isDefined() {
        return !isEmpty();
    }

    /**
     * An {@code Option} is single-valued.
     *
     * @return {@code true}
     */
    @Override
    default boolean isSingleValued() {
        return true;
    }

    @Override
    T get();

    @Override
    default Option<T> getOption() {
        return this;
    }

    /**
     * Returns the value if this is a {@code Some} or the {@code other} value if this is a {@code None}.
     * <p>
     * Please note, that the other value is eagerly evaluated.
     *
     * @param other An alternative value
     * @return This value, if this Option is defined or the {@code other} value, if this Option is empty.
     */
    @Override
    default T getOrElse(T other) {
        return isEmpty() ? other : get();
    }

    /**
     * Returns this {@code Option} if it is nonempty, otherwise return the alternative.
     *
     * @param other An alternative {@code Option}
     * @return this {@code Option} if it is nonempty, otherwise return the alternative.
     */
    @SuppressWarnings("unchecked")
    default Option<T> orElse(Option<? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        return isEmpty() ? (Option<T>) other : this;
    }

    /**
     * Returns this {@code Option} if it is nonempty, otherwise return the result of evaluating supplier.
     *
     * @param supplier An alternative {@code Option} supplier
     * @return this {@code Option} if it is nonempty, otherwise return the result of evaluating supplier.
     */
    @SuppressWarnings("unchecked")
    default Option<T> orElse(Supplier<? extends Option<? extends T>> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isEmpty() ? (Option<T>) supplier.get() : this;
    }

    /**
     * Returns the value if this is a {@code Some}, otherwise the {@code other} value is returned,
     * if this is a {@code None}.
     * <p>
     * Please note, that the other value is lazily evaluated.
     *
     * @param supplier An alternative value supplier
     * @return This value, if this Option is defined or the {@code other} value, if this Option is empty.
     */
    @Override
    default T getOrElse(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return isEmpty() ? supplier.get() : get();
    }

    /**
     * Returns the value if this is a {@code Some}, otherwise throws an exception.
     *
     * @param exceptionSupplier An exception supplier
     * @param <X>               A throwable
     * @return This value, if this Option is defined, otherwise throws X
     * @throws X a throwable
     */
    @Override
    default <X extends Throwable> T getOrElseThrow(Supplier<X> exceptionSupplier) throws X {
        Objects.requireNonNull(exceptionSupplier, "exceptionSupplier is null");
        if (isEmpty()) {
            throw exceptionSupplier.get();
        } else {
            return get();
        }
    }

    /**
     * Returns {@code Some(value)} if this is a {@code Some} and the value satisfies the given predicate.
     * Otherwise {@code None} is returned.
     *
     * @param predicate A predicate which is used to test an optional value
     * @return {@code Some(value)} or {@code None} as specified
     */
    default Option<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return isEmpty() || predicate.test(get()) ? this : none();
    }

    /**
     * Maps the value to a new {@code Option} if this is a {@code Some}, otherwise returns {@code None}.
     *
     * @param mapper A mapper
     * @param <U>    Component type of the resulting Option
     * @return a new {@code Option}
     */
    @SuppressWarnings("unchecked")
    default <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? none() : (Option<U>) mapper.apply(get());
    }

    /**
     * Maps the value and wraps it in a new {@code Some} if this is a {@code Some}, returns {@code None}.
     *
     * @param mapper A value mapper
     * @param <U>    The new value type
     * @return a new {@code Some} containing the mapped value if this Option is defined, otherwise {@code None}, if this is empty.
     */
    @Override
    default <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? none() : some(mapper.apply(get()));
    }

    /**
     * Applies an action to this value, if this option is defined, otherwise does nothing.
     *
     * @param action An action which can be applied to an optional value
     * @return this {@code Option}
     */
    @Override
    default Option<T> peek(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isDefined()) {
            action.accept(get());
        }
        return this;
    }

    /**
     * Transforms this {@code Option}.
     *
     * @param f   A transformation
     * @param <U> Type of transformation result
     * @return An instance of type {@code U}
     * @throws NullPointerException if {@code f} is null
     */
    default <U> U transform(Function<? super Option<T>, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(this);
    }

    @Override
    default Iterator<T> iterator() {
        return isEmpty() ? Iterator.empty() : Iterator.of(get());
    }

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    /**
     * Some represents a defined {@link Option}. It contains a value which may be null. However, to
     * create an Option containing null, {@code new Some(null)} has to be called. In all other cases
     * {@link Option#of(Object)} is sufficient.
     *
     * @param <T> The type of the optional value.
     * @author Daniel Dietrich
     * @since 1.0.0
     */
    final class Some<T> implements Option<T>, Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The singleton instance of {@code Some<Void>}.
         */
        private static final Some<Void> NOTHING = new Some<>(null);

        private final T value;

        /**
         * Creates a new Some containing the given value.
         *
         * @param value A value, may be null
         */
        private Some(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj == this) || (obj instanceof Some && Objects.equals(value, ((Some<?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String stringPrefix() {
            return "Some";
        }

        @Override
        public String toString() {
            return stringPrefix() + "(" + value + ")";
        }
    }

    /**
     * None is a singleton representation of the undefined {@link Option}.
     *
     * @param <T> The type of the optional value.
     * @author Daniel Dietrich
     * @since 1.0.0
     */
    final class None<T> implements Option<T>, Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The singleton instance of None.
         */
        private static final None<?> INSTANCE = new None<>();

        /**
         * Hidden constructor.
         */
        private None() {
        }

        @Override
        public T get() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String stringPrefix() {
            return "None";
        }

        @Override
        public String toString() {
            return stringPrefix();
        }

        // -- Serializable implementation

        /**
         * Instance control for object serialization.
         *
         * @return The singleton instance of None.
         * @see Serializable
         */
        private Object readResolve() {
            return INSTANCE;
        }
    }
}
