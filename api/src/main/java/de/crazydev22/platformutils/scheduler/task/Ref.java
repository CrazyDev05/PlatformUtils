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

    /**
     * The value stored within the reference container. This field represents
     * a mutable generic value of type {@code T}. It can be modified or accessed
     * by instances of the containing class.
     * <p>
     * The {@link Object#equals}, {@link Object#hashCode}, and {@link Object#toString}
     * methods interact with this field to support value-based comparisons, consistent
     * hash codes, and meaningful string representations, respectively.
     */
    public T value;

    /**
     * Constructs an empty instance of {@code Ref}.
     * The initial state of the constructed object is such that the mutable {@link #value}
     * field will hold a default {@code null} reference until explicitly set.
     * <p>
     * This constructor provides a straightforward way to initialize a {@code Ref} object
     * for future updates to its {@code value} property.
     */
    public Ref() {
    }

    @Override
    public String toString() {
        return "Ref[" + (value != null ? value.toString() : "null") + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ref<?> ref)) return false;
        return Objects.equals(value, ref.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
