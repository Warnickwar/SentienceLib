package org.warnickwar.sentiencelib.api.core.context;

import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.Desire;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// QUESTION is it even possible to have less memory overhead with this?
public final class Context<O> {

    private final O owner;
    private final Set<IdentifiedData<Action>> actions;
    private final Set<IdentifiedData<Desire>> desires;

    Context(@NotNull O owner) {
        this.owner = owner;
        this.actions = ConcurrentHashMap.newKeySet();
        this.desires = ConcurrentHashMap.newKeySet();
    }

    public void addAction(Identity<Action> id, Action action) {
        addAction(IdentifiedData.of(id, action));
    }

    public void addAction(IdentifiedData<Action> data) {
        actions.add(data);
    }

    public void addDesire(Identity<Desire> id, Desire action) {
        addDesire(IdentifiedData.of(id, action));
    }

    public void addDesire(IdentifiedData<Desire> data) {
        desires.add(data);
    }

    public O getOwner() {
        return owner;
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
