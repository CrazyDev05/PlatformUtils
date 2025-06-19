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

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a task scheduled to a scheduler.
 */
@ApiStatus.NonExtendable
public interface Task {

    /**
     * Returns the plugin that scheduled this task.
     *
     * @return the plugin that scheduled this task.
     */
    @NotNull Plugin getOwner();

    /**
     * Determines whether the task is a repeating task.
     *
     * @return true if the task is scheduled to repeat execution periodically, false otherwise.
     */
    boolean isRepeating();

    /**
     * Determines whether the task is scheduled to be executed asynchronously.
     *
     * @return true if the task runs asynchronously, false if it runs synchronously.
     */
    boolean isAsync();

    /**
     * Attempts to cancel the task. If the task is a repeating task, this will halt future executions, but the
     * current execution might still be running. For non-repeating tasks, the result depends on the task's
     * current execution state.
     *
     * @return the result of the cancellation attempt, indicating whether the task was successfully cancelled,
     * was already cancelled, or could not be cancelled due to its current execution state.
     */
    @NotNull CancelledState cancel();

    /**
     * Retrieves the current execution state of the task.
     *
     * @return the current {@link ExecutionState} of the task, which may denote whether the task is idle, running,
     * finished, cancelled, or in a cancelled running state in the case of a repeating task.
     */
    @NotNull ExecutionState getExecutionState();

    /**
     * Checks whether the task has been cancelled. A task is considered cancelled if its execution state
     * is {@code CANCELLED} or {@code CANCELLED_RUNNING}.
     *
     * @return true if the task is cancelled, false otherwise.
     */
    default boolean isCancelled() {
        final ExecutionState state = getExecutionState();
        return state == ExecutionState.CANCELLED || state == ExecutionState.CANCELLED_RUNNING;
    }

    /**
     * Represents the result of attempting to cancel a task.
     */
    enum CancelledState {
        /**
         * The task (repeating or not) has been successfully cancelled by the caller thread. The task is not executing
         * currently, and it will not begin execution in the future.
         */
        CANCELLED_BY_CALLER,
        /**
         * The task (repeating or not) is already cancelled. The task is not executing currently, and it will not
         * begin execution in the future.
         */
        CANCELLED_ALREADY,

        /**
         * The task is not a repeating task, and could not be cancelled because the task is being executed.
         */
        RUNNING,
        /**
         * The task is not a repeating task, and could not be cancelled because the task has already finished execution.
         */
        ALREADY_EXECUTED,

        /**
         * The caller thread successfully stopped future executions of a repeating task, but the task is currently
         * being executed.
         */
        NEXT_RUNS_CANCELLED,

        /**
         * The repeating task's future executions are cancelled already, but the task is currently
         * being executed.
         */
        NEXT_RUNS_CANCELLED_ALREADY,
    }

    /**
     * Represents the current execution state of the task.
     */
    enum ExecutionState {
        /**
         * The task is currently not executing, but may begin execution in the future.
         */
        IDLE,

        /**
         * The task is currently executing.
         */
        RUNNING,

        /**
         * The task is not repeating, and the task finished executing.
         */
        FINISHED,

        /**
         * The task is not executing and will not begin execution in the future. If this task is not repeating, then
         * this task was never executed.
         */
        CANCELLED,

        /**
         * The task is repeating and currently executing, but future executions are cancelled and will not occur.
         */
        CANCELLED_RUNNING
    }
}
