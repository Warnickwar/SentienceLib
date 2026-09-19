package org.warnickwar.sentiencelib.api.core.context;

import it.unimi.dsi.fastutil.Pair;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.Agent;
import org.warnickwar.sentiencelib.api.core.Desire;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Context<O> {

    private final O owner;
    private final Set<IdentifiedData<Action>> actions;
    private final Set<IdentifiedData<Desire>> desires;

    Context(@NotNull O owner) {
        this.owner = owner;
        this.actions = ConcurrentHashMap.newKeySet();
        this.desires = ConcurrentHashMap.newKeySet();
    }

    public O getOwner() {
        return owner;
    }

    public Context<O> merge(Context<O> other) {
        // Only check memory equality; Any other equality doesn't guarantee
        //  that the objects are truly the same.
        if (this.owner != other.owner) {
            throw new IllegalArgumentException(String.format("Merging Context of Owner %s to Owner %s is not allowed;\n" +
                "Owners MUST be equal!", this.owner, other.owner));
        }

        Context<O> ctx = new Context<>(owner);

        ctx.actions.addAll(this.actions);
        ctx.desires.addAll(this.desires);

        ctx.actions.addAll(other.actions);
        ctx.desires.addAll(other.desires);
        return ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Context<?> context = (Context<?>) o;
        return Objects.equals(owner, context.owner) &&
            Objects.equals(actions, context.actions) &&
            Objects.equals(desires, context.desires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, actions, desires);
    }

    public static <A> Context<A> of(A owner) {
        return new Context<>(owner);
    }

    public static Pair<Set<IdentifiedData<Desire>>, Set<IdentifiedData<Action>>> extract(Context<?> ctx) {
        return Pair.of(
            ctx.desires,
            ctx.actions
        );
    }

}
