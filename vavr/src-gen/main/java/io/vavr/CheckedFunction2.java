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

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
   G E N E R A T O R   C R A F T E D
\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a function with two arguments.
 *
 * @param <T1> argument 1 of the function
 * @param <T2> argument 2 of the function
 * @param <R> return type of the function
 * @author Daniel Dietrich
 */
@FunctionalInterface
public interface CheckedFunction2<T1, T2, R> extends Serializable {

    /**
     * The <a href="https://docs.oracle.com/javase/8/docs/api/index.html">serial version uid</a>.
     */
    long serialVersionUID = 1L;

    /**
     * Creates a {@code CheckedFunction2} based on
     * <ul>
     * <li><a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html">method reference</a></li>
     * <li><a href="https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#syntax">lambda expression</a></li>
     * </ul>
     *
     * Examples (w.l.o.g. referring to Function1):
     * <pre><code>// using a lambda expression
     * Function1&lt;Integer, Integer&gt; add1 = Function1.of(i -&gt; i + 1);
     *
     * // using a method reference (, e.g. Integer method(Integer i) { return i + 1; })
     * Function1&lt;Integer, Integer&gt; add2 = Function1.of(this::method);
     *
     * // using a lambda reference
     * Function1&lt;Integer, Integer&gt; add3 = Function1.of(add1::apply);
     * </code></pre>
     * <p>
     * <strong>Caution:</strong> Reflection loses type information of lambda references.
     * <pre><code>// type of a lambda expression
     * Type&lt;?, ?&gt; type1 = add1.getType(); // (Integer) -&gt; Integer
     *
     * // type of a method reference
     * Type&lt;?, ?&gt; type2 = add2.getType(); // (Integer) -&gt; Integer
     *
     * // type of a lambda reference
     * Type&lt;?, ?&gt; type3 = add3.getType(); // (Object) -&gt; Object
     * </code></pre>
     *
     * @param methodReference (typically) a method reference, e.g. {@code Type::method}
     * @param <R> return type
     * @param <T1> 1st argument
     * @param <T2> 2nd argument
     * @return a {@code CheckedFunction2}
     */
    static <T1, T2, R> CheckedFunction2<T1, T2, R> of(CheckedFunction2<T1, T2, R> methodReference) {
        return methodReference;
    }

    /**
     * Narrows the given {@code CheckedFunction2<? super T1, ? super T2, ? extends R>} to {@code CheckedFunction2<T1, T2, R>}
     *
     * @param f A {@code CheckedFunction2}
     * @param <R> return type
     * @param <T1> 1st argument
     * @param <T2> 2nd argument
     * @return the given {@code f} instance as narrowed type {@code CheckedFunction2<T1, T2, R>}
     */
    @SuppressWarnings("unchecked")
    static <T1, T2, R> CheckedFunction2<T1, T2, R> narrow(CheckedFunction2<? super T1, ? super T2, ? extends R> f) {
        return (CheckedFunction2<T1, T2, R>) f;
    }

    /**
     * Applies this function to two arguments and returns the result.
     *
     * @param t1 argument 1
     * @param t2 argument 2
     * @return the result of function application
     * @throws Exception if something goes wrong applying this function to the given arguments
     */
    R apply(T1 t1, T2 t2) throws Exception;

    /**
     * Applies this function partially to one argument.
     *
     * @param t1 argument 1
     * @return a partial application of this function
     */
    default CheckedFunction1<T2, R> apply(T1 t1) {
        return (T2 t2) -> apply(t1, t2);
    }

    /**
     * Returns a curried version of this function.
     *
     * @return a curried function equivalent to this.
     */
    default Function1<T1, CheckedFunction1<T2, R>> curried() {
        return t1 -> t2 -> apply(t1, t2);
    }

    /**
     * Returns a tupled version of this function.
     *
     * @return a tupled function equivalent to this.
     */
    default CheckedFunction1<Tuple2<T1, T2>, R> tupled() {
        return t -> apply(t._1, t._2);
    }

    /**
     * Returns a reversed version of this function. This may be useful in a recursive context.
     *
     * @return a reversed function equivalent to this.
     */
    default CheckedFunction2<T2, T1, R> reversed() {
        return (t2, t1) -> apply(t1, t2);
    }

    /**
     * Checks if this function is memoizing (= caching) computed values.
     *
     * @return true, if this function is memoizing, false otherwise
     */
    default boolean isMemoized() {
        return this instanceof Memoized;
    }

    default CheckedFunction2<T1, T2, R> memoized() {
        if (isMemoized()) {
            return this;
        } else {
            final Map<Tuple2<T1, T2>, R> cache = new HashMap<>();
            return (CheckedFunction2<T1, T2, R> & Memoized) (t1, t2) -> {
                final Tuple2<T1, T2> key = Tuple.of(t1, t2);
                synchronized (cache) {
                    if (cache.containsKey(key)) {
                        return cache.get(key);
                    } else {
                        final R value = tupled().apply(key);
                        cache.put(key, value);
                        return value;
                    }
                }
            };
        }
    }

    interface Memoized { /* zero abstract method (ZAM) interface */ }

    /**
     * Return a composed function that first applies this CheckedFunction2 to the given arguments and in case of throwable
     * try to get value from {@code recover} function with same arguments and throwable information.
     *
     * @param recover the function applied in case of throwable
     * @return a function composed of this and recover
     * @throws NullPointerException if recover is null
     */
    default Function2<T1, T2, R> recover(Function<? super Throwable, ? extends BiFunction<? super T1, ? super T2, ? extends R>> recover) {
        Objects.requireNonNull(recover, "recover is null");
        return (t1, t2) -> {
            try {
                return this.apply(t1, t2);
            } catch (Throwable throwable) {
                final BiFunction<? super T1, ? super T2, ? extends R> func = recover.apply(throwable);
                Objects.requireNonNull(func, () -> "recover return null for " + throwable.getClass() + ": " + throwable.getMessage());
                return func.apply(t1, t2);
            }
        };
    }

    /**
     * Returns a composed function that first applies this CheckedFunction2 to the given argument and then applies
     * {@linkplain CheckedFunction1} {@code after} to the result.
     *
     * @param <V> return type of after
     * @param after the function applied after this
     * @return a function composed of this and after
     * @throws NullPointerException if after is null
     */
    default <V> CheckedFunction2<T1, T2, V> andThen(CheckedFunction1<? super R, ? extends V> after) {
        Objects.requireNonNull(after, "after is null");
        return (t1, t2) -> after.apply(apply(t1, t2));
    }

}