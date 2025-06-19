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
package de.crazydev22.platformutils.spigot;

import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpigotTask implements Task {
    protected final BukkitTask task;
    protected final boolean repeating;
    private final AtomicReference<ExecutionState> state = new AtomicReference<>(ExecutionState.IDLE);

    public SpigotTask(@NotNull BukkitTask task, boolean repeating) {
        this.task = task;
        this.repeating = repeating;
    }

    @Override
    public @NotNull Plugin getOwner() {
        return task.getOwner();
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }

    @Override
    public boolean isAsync() {
        return !task.isSync();
    }

    @Override
    public @NotNull CancelledState cancel() {
        for (var curr = this.state.get();;) {
            switch (curr) {
                case IDLE -> {
                    if (ExecutionState.IDLE != (curr = state.compareAndExchange(ExecutionState.IDLE, ExecutionState.CANCELLED)))
                        continue;
                    task.cancel();
                    return CancelledState.CANCELLED_BY_CALLER;
                }
                case RUNNING -> {
                    if (!repeating) return CancelledState.RUNNING;
                    if (ExecutionState.RUNNING != (curr = state.compareAndExchange(ExecutionState.RUNNING, ExecutionState.CANCELLED_RUNNING)))
                        continue;
                    task.cancel();
                    return CancelledState.NEXT_RUNS_CANCELLED;
                }
                case CANCELLED_RUNNING -> {
                    return CancelledState.NEXT_RUNS_CANCELLED_ALREADY;
                }
                case FINISHED -> {
                    return CancelledState.ALREADY_EXECUTED;
                }
                case CANCELLED -> {
                    return CancelledState.CANCELLED_ALREADY;
                }
            };
        }
    }

    @Override
    public @NotNull ExecutionState getExecutionState() {
        return state.get();
    }

    public void run(Consumer<Task> action) {
        if (ExecutionState.IDLE != state.compareAndExchange(ExecutionState.IDLE, ExecutionState.RUNNING))
            return;

        try {
            action.accept(this);
        } finally {
            if (!repeating) state.set(ExecutionState.FINISHED);
            else state.compareAndSet(ExecutionState.RUNNING, ExecutionState.IDLE);
        }
    }

    public static class Completable<T> extends SpigotTask implements CompletableTask<T> {
        private final CompletableFuture<T> result = new CompletableFuture<>();

        public Completable(@NotNull BukkitTask task) {
            super(task, false);
        }

        @Override
        public @NotNull CompletableFuture<T> getResult() {
            return result;
        }

        @Override
        public void complete(@NotNull Function<CompletableTask<T>, T> function) {
            run(t -> {
                if (result.isDone()) return;
                try {
                    result.complete(function.apply(this));
                } catch (Throwable e) {
                    result.completeExceptionally(e);
                }
            });
        }

        @Override
        public @NotNull CancelledState cancel() {
            final CancelledState state = super.cancel();
            if (state == CancelledState.CANCELLED_BY_CALLER || state == CancelledState.CANCELLED_ALREADY) {
                result.cancel(false);
            }
            return state;
        }
    }
}
