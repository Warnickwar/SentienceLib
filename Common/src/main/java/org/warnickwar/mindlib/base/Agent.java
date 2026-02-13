package org.warnickwar.mindlib.base;

import net.minecraft.world.phys.Vec3;
import org.warnickwar.mindlib.base.context.Context;
import org.warnickwar.mindlib.base.identifier.AiIdentifier;
import org.warnickwar.mindlib.base.identifier.IdentifiedData;
import org.warnickwar.mindlib.base.identifier.IdentifiedSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Agent<O> {

    private static final ContextHandler<?> DEFAULT_CTX = (ctx) -> ctx;

    private final HashMap<AiIdentifier<Sensor>, IdentifiedData<Sensor>> sensors = new HashMap<>();
    private final HashMap<AiIdentifier<Belief>, IdentifiedData<Belief>> beliefs = new HashMap<>();
    private final HashMap<AiIdentifier<Desire>, IdentifiedData<Desire>> desires = new HashMap<>();
    private final ActionTree actions = new ActionTree();

    private final O owner;
    private final PositionSupplier posSupplier;

    @SuppressWarnings("unchecked")
    private ContextHandler<O> contextHandler = (ContextHandler<O>) DEFAULT_CTX;

    Agent(O owner, PositionSupplier positionSupplier) {
        this.owner = owner;
        this.posSupplier = positionSupplier;
    }

    public O getOwner() {
        return owner;
    }

    public Vec3 getPosition() {
        return posSupplier.get();
    }

    public static class Builder<O> {

        private final Agent<O> instance;

        private Supplier<Set<IdentifiedData<Sensor>>> sensorSetup = HashSet::new;
        private Supplier<Set<IdentifiedData<Belief>>> beliefSetup = HashSet::new;
        private Function<Set<IdentifiedData<Belief>>, Set<IdentifiedData<Desire>>> desireSetup = (beliefs) -> new IdentifiedSet<>();
        private Consumer<ActionTree.Twig> actionSetup = (t) -> {};

        public Builder(O owner, PositionSupplier positionSupplier) {
            instance = new Agent<>(owner, positionSupplier);
        }

        public Builder<O> contextHandler(ContextHandler<O> contextHandler) {
            instance.contextHandler = contextHandler;
            return this;
        }

        public Builder<O> sensorSetup(Supplier<Set<IdentifiedData<Sensor>>> sensorSetup) {
            this.sensorSetup = sensorSetup;
            return this;
        }

        public Builder<O> beliefSetup(Supplier<Set<IdentifiedData<Belief>>> beliefSetup) {
            this.beliefSetup = beliefSetup;
            return this;
        }

        public Builder<O> desireSetup(Function<Set<IdentifiedData<Belief>>, Set<IdentifiedData<Desire>>> desireSetup) {
            this.desireSetup = desireSetup;
            return this;
        }

        public Builder<O> actionSetup(Consumer<ActionTree.Twig> actionSetup) {
            this.actionSetup = actionSetup;
            return this;
        }

        public Agent<O> build() {
            sensorSetup.get().forEach(data -> {
                instance.sensors.put(data.getIdentifier(),  data);
            });
            var beliefSet = beliefSetup.get();
            beliefSet.forEach(data -> {
                instance.beliefs.put(data.getIdentifier(), data);
            });
            desireSetup.apply(beliefSet).forEach(data -> {
                instance.desires.put(data.getIdentifier(), data);
            });
            actionSetup.accept(instance.actions.getRoot().getValue());
            return instance;
        }

    }

    public interface PositionSupplier extends Supplier<Vec3> {
        Vec3 get();
    }

    public interface ContextHandler<A> {
        Context<A> gatherContext(Context<A> ctx);
    }

}
