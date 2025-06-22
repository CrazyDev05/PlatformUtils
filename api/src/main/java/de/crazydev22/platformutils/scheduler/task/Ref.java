/*
 * MIT License
 *
 * Copyright (c) 2025 Julian Krings
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.crazydev22.platformutils.scheduler.task;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A generic container class that holds a single mutable value. This class is primarily
 * intended for internal use and allows wrapping a value for indirect references within
 * contexts where objects need to hold mutable references.
 *
 * <p>Instances of this class primarily store their associated value in the {@code value} field.
 * The class provides overrides for {@code equals}, {@code hashCode}, and {@code toString} to
 * facilitate comparisons, obtain consistent hash-based behavior, and provide a meaningful string representation.
 *
 * @param <T> the type of the value being stored in this reference.
 */
@ApiStatus.Internal
public final class Ref<T> {
    private volatile Result<T> result;

    /**
     * Constructs an empty instance of {@code Ref}.
     * The initial state of the constructed object is such that the mutable {@link #result}
     * field will hold a default {@code null} reference until explicitly set.
     * <p>
     * This constructor provides a straightforward way to initialize a {@code Ref} object
     * for future updates to its {@code value} property.
     */
    public Ref() {
    }

    /**
     * Sets the provided value in the container and updates its internal state.
     *
     * @param value the value to be stored in the container
     * @return the input value passed to the method
     */
    public T set(T value) {
        this.result = new Result<>(value);
        return value;
    }

    /**
     * Retrieves the value stored in the container. If the value is not yet
     * available, the method will continuously poll until the value becomes available.
     * <p>
     * This method blocks indefinitely until the stored value is non-null.
     *
     * @return the value of type {@code T} stored in the container.
     */
    public T get() {
        for (;;) {
            if (result == null) continue;
            return result.value;
        }
    }

    /**
     * Creates a {@link Consumer} for the generic type {@code S} that allows processing tasks using a provided
     * {@link Consumer} for the underlying type {@code T}.
     * <p>
     * The returned Consumer retrieves the current value stored in the container using the {@code get} method
     * and passes it to the provided Consumer for processing.
     *
     * @param <S> the type of the argument for the returned Consumer
     * @param consumer the {@link Consumer} that processes the value of type {@code T} retrieved
     *                 from the container
     * @return a {@link Consumer} that accepts a value of type {@code S} and delegates processing to the provided Consumer.
     */
    public <S> Consumer<S> consume(Consumer<T> consumer) {
        return task -> consumer.accept(get());
    }

    /**
     * Creates a {@link Consumer} for the generic type {@code S} that takes an input of type {@code V}
     * along with an associated {@link BiConsumer} for processing the values of type {@code T} and {@code V}.
     * <p>
     * The returned {@link Consumer} uses the {@code get()} method to retrieve the current
     * value stored in the container and passes it, along with the given second value,
     * to the specified {@link BiConsumer}.
     *
     * @param <S> the type of the argument for the returned {@code Consumer}
     * @param <V> the type of the second input used by the {@link BiConsumer}
     * @param second the second input parameter to be used with the {@link BiConsumer}
     * @param consumer the {@link BiConsumer} that processes the value of type {@code T} retrieved
     *                 from the container along with the provided second value of type {@code V}
     * @return a {@link Consumer} that accepts a value of type {@code S}, retrieves the current container
     *         value, and applies the specified {@link BiConsumer} for processing
     */
    public <S, V> Consumer<S> consume(V second, BiConsumer<T, V> consumer) {
        return task -> consumer.accept(get(), second);
    }

    /**
     * Creates a {@link Runnable} that, when executed, retrieves the value stored in the container
     * and passes it to the provided {@link Consumer} for processing.
     *
     * @param consumer the {@link Consumer} that will process the value retrieved
     *                 from the container.
     * @return a {@link Runnable} that retrieves the container's value and delegates its
     *         processing to the provided {@link Consumer}.
     */
    public Runnable run(Consumer<T> consumer) {
        return () -> consumer.accept(get());
    }

    /**
     * Creates a {@link Runnable} that, when executed, retrieves the value stored in the container
     * and applies the specified {@link BiConsumer} using the retrieved value and the provided
     * second input parameter.
     *
     * @param <V> the type of the second input parameter used by the {@link BiConsumer}
     * @param second the second input parameter to be used with the {@link BiConsumer}
     * @param consumer the {@link BiConsumer} that processes the value retrieved from the container
     *                 along with the provided second input parameter
     * @return a {@link Runnable} that, when run, retrieves the container's value and invokes
     *         the specified {@link BiConsumer} with it and the given second input
     */
    public <V> Runnable run(V second, BiConsumer<T, V> consumer) {
        return () -> consumer.accept(get(), second);
    }

    @Override
    public String toString() {
        return "Ref[" + (result != null ? result.toString() : "null") + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ref<?> ref)) return false;
        return Objects.equals(result, ref.result);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(result);
    }

    private record Result<T>(T value) {}
}
