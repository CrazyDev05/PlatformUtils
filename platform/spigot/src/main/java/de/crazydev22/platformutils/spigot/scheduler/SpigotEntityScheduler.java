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
package de.crazydev22.platformutils.spigot.scheduler;

import de.crazydev22.platformutils.scheduler.IEntityScheduler;
import de.crazydev22.platformutils.scheduler.IGlobalScheduler;
import de.crazydev22.platformutils.scheduler.task.CompletableTask;
import de.crazydev22.platformutils.scheduler.task.Task;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;
import java.util.function.Function;

import static de.crazydev22.platformutils.spigot.SpigotPlatform.isValid;


public class SpigotEntityScheduler implements IEntityScheduler {
    private final IGlobalScheduler scheduler;
    private final Entity entity;

    public SpigotEntityScheduler(IGlobalScheduler scheduler, Entity entity) {
        this.scheduler = scheduler;
        this.entity = entity;
    }

    @Override
    public @Nullable <R> CompletableTask<R> runDelayed(@NotNull Function<CompletableTask<R>, R> task, @Nullable Runnable retired, @Range(from = 1, to = Long.MAX_VALUE) long delayTicks) {
        if (!isValid(entity)) return null;
        return scheduler.runDelayed(t -> {
            if (!isValid(entity)) {
                t.cancel();
                if (retired != null)
                    retired.run();
                return null;
            }
            return task.apply(t);
        }, delayTicks);
    }

    @Override
    public @Nullable Task runAtFixedRate(@NotNull Consumer<Task> task, @Nullable Runnable retired, @Range(from = 1, to = Long.MAX_VALUE) long initialDelayTicks, @Range(from = 1, to = Long.MAX_VALUE) long periodTicks) {
        if (!isValid(entity)) return null;
        return scheduler.runAtFixedRate(t -> {
            if (!isValid(entity)) {
                t.cancel();
                if (retired != null)
                    retired.run();
                return;
            }
            task.accept(t);
        }, initialDelayTicks, periodTicks);
    }
}
