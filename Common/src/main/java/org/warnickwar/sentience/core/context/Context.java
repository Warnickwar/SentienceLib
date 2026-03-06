package org.warnickwar.sentience.core.context;

import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentience.core.actions.Action;
import org.warnickwar.sentience.core.Agent;
import org.warnickwar.sentience.core.Desire;
import org.warnickwar.sentience.core.identifier.AiIdentifier;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Context<A> {

    private final Agent<A> agent;
    private final Set<IdentifiedData<Action>> actions;
    private final Set<IdentifiedData<Desire>> desires;

    Context(@NotNull Agent<A> agent) {
        this.agent = agent;
        this.actions = new HashSet<>();
        this.desires = new HashSet<>();
    }

    @NotNull
    public Agent<A> getAgent() {
        return agent;
    }

    public void inject(AiIdentifier<Action> id, Action newAction) {
        actions.add(IdentifiedData.of(id, newAction));
    }

    public void inject(AiIdentifier<Desire> id, Desire newDesire) {
        desires.add(IdentifiedData.of(id, newDesire));
    }

    public Set<IdentifiedData<Action>> getActions() {
        return new HashSet<>(actions);
    }

    public Set<IdentifiedData<Desire>> getDesires() {
        return new HashSet<>(desires);
    }

    public Context<A> merge(Context<A> other) {
        // Only check memory equality; Any other equality doesn't guarantee
        //  that the objects are truly the same.
        if (!(this.agent == other.agent)) {
            throw new IllegalArgumentException(String.format("Merging context of Agent %s to Agent %s is not allowed;\n" +
                "Agents MUST be equal!", this.agent, other.agent));
        }
        var ctx = new Context<>(agent);
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
        return Objects.equals(agent, context.agent) && Objects.equals(actions, context.actions) && Objects.equals(desires, context.desires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent, actions, desires);
    }

    public static <A> Context<A> of(Agent<A> agent) {
        return new Context<>(agent);
    }

}
