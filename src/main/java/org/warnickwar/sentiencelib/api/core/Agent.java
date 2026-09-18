package org.warnickwar.sentiencelib.api.core;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.actions.ActionPlan;
import org.warnickwar.sentiencelib.api.core.context.Context;
import org.warnickwar.sentiencelib.api.core.identifier.SenIdentifier;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.planning.PlanBuilder;
import org.warnickwar.sentiencelib.api.core.planning.Planners;
import org.warnickwar.sentiencelib.api.core.sense.SenseManager;
import org.warnickwar.sentiencelib.threading.JobManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Agent<O> {

    private static final Logger LOGGER = Constants.LOG;

    private static final ContextHandler<?> DEFAULT_CTX = (ctx) -> {};

    private final SenseManager senses = new SenseManager();
    // Used solely to allow for concurrent planning on the entity
    private final Map<SenIdentifier<Belief>, IdentifiedData<Belief>> beliefs = new ConcurrentHashMap<>();
    private final Map<SenIdentifier<Desire>, IdentifiedData<Desire>> desires = new ConcurrentHashMap<>();
    private final Map<SenIdentifier<Action>, IdentifiedData<Action>> actions = new ConcurrentHashMap<>();

    private final O owner;
    private final Supplier<Vec3> posSupplier;

    @SuppressWarnings("unchecked")
    private ContextHandler<O> contextHandler = (ContextHandler<O>) DEFAULT_CTX;

    private Supplier<ProfilerFiller> profiler = () -> null;

    // Planning System

    private boolean shouldReplanAlways = true;

    // Used by the Planner to consider different Desires over this one
    @Nullable
    private IdentifiedData<Desire> lastDesire = null;

    @Nullable
    private ActionPlan currentPlan = null;
    // Cached for direct lookup
    @Nullable
    private IdentifiedData<Action> currentAction = null;

    private PlanBuilder planner = Planners::DFSPlan;

    // Scheduled Plans
    /**
     * <p>
     *     The current Plan request sent to the Server for evaluation.
     * </p>
     * <p>
     *     This value should be considered to only exist on the {@code Logical Server}.
     * </p>
      */
    private Future<ActionPlan> planRequest = null;

    Agent(O owner, Supplier<Vec3> positionSupplier) {
        this.owner = owner;
        this.posSupplier = positionSupplier;
    }

    // Generic Methods

    public O getOwner() {
        return owner;
    }

    public Vec3 getPosition() {
        return posSupplier.get();
    }

    public SenseManager getSenses() {
        return senses;
    }

    public Set<SenIdentifier<Belief>> getBeliefIds() {
        return new HashSet<>(beliefs.keySet());
    }

    public Set<SenIdentifier<Desire>> getDesireIds() {
        return new HashSet<>(desires.keySet());
    }

    public Set<IdentifiedData<Action>> getActions() {
        return new HashSet<>(actions.values());
    }

    // Processing Methods

    // TODO: Make the Tick handle the Plan after Sensors all tick.
    public void tick() {
        senses.tick();
        ProfilerFiller filler = profiler.get();
        if (currentAction == null) {
            // Await new Plan
            handlePlanRequest(filler);
        } else if (currentPlan != null) {
            // Execute current Plan

            if (filler != null) {
                filler.push(String.format("%s:executingAgentPlan", Constants.MODID));
            }

            Action val = currentAction.getValue();
            val.tick();

            if (val.isComplete()) {
                val.stop();
                var end = currentPlan.actions().isEmpty();
                // If at the end, set Action to null
                currentAction = end ? null : currentPlan.actions().pop();

                if (end) {
                    stopPlan();
                } else {
                    // Swap to next Action in queue
                    assert currentAction != null;
                    if (currentAction.getValue().getPreconditions().stream().allMatch((b) -> b.getValue().evaluate())) {
                        currentAction.getValue().start();
                    } else {
                        stopPlan();
                    }
                }
            }

            if (filler != null) {
                filler.pop();
            }

            // Submit/Handle extra Plan request if it should always Plan
            if (shouldReplanAlways) {
                handlePlanRequest(filler);
            }
        }
    }

    private void handlePlanRequest(@Nullable ProfilerFiller filler) {
        if (planRequest == null) {
            submitPlanRequest(filler);
        } else {
            // Await Request finish
            if (planRequest.isDone()) {
                try {
                    ActionPlan res = planRequest.get();
                    if (res != null && !res.actions().isEmpty()) {
                        currentPlan = res;
                        // Stop in case of replanning during executing an already existing plan.
                        if (currentAction != null) {
                            currentAction.getValue().stop();
                        }
                        currentAction = currentPlan.actions().pop();
                        if (currentAction.getValue().getPreconditions().stream().allMatch(b -> b.getValue().evaluate())){
                            currentAction.getValue().start();
                        } else {
                            // Plan, somehow, is not valid to start despite just switching to it!
                            stopPlan();
                        }
                    }
                } catch (InterruptedException | CancellationException ignored) {
                    // Interrupted Plan, ignore and continue.
                } catch (ExecutionException e) {
                    LOGGER.error("Failed to generate plan for Agent {}!\nException: {}", this.owner, e);
                }
            }
            planRequest = null;
        }
    }

    private void submitPlanRequest(@Nullable ProfilerFiller filler) {
        // Create new Request
        if (filler != null) {
            // Log the amount of time to request new Plan
            filler.push(String.format("%s:requestingPlan", Constants.MODID));
        }

        // Collect Context
        HashSet<IdentifiedData<Desire>> desires = new HashSet<>(this.desires.values());
        HashSet<IdentifiedData<Action>> actions = new HashSet<>(this.actions.values());
        Context<O> ctx = Context.of(this);
        contextHandler.gatherContext(ctx);
        desires.addAll(ctx.getDesires());
        actions.addAll(ctx.getActions());

        planRequest = JobManager.submitJob(() -> this.planner.plan(desires, actions, getBeliefEvaluations(desires, actions), lastDesire));

        if (filler != null) {
            filler.pop();
        }
    }

    private void stopPlan() {
        // Cancel Plan, unable to continue
        if (currentPlan != null) {
            lastDesire = currentPlan.desire();
        }
        currentAction = null;
        if (planRequest != null) {
            planRequest.cancel(true);
            planRequest = null;
        }
        currentPlan = null;
    }

    /**
     * Collects all Belief Evaluations into a Map for the sake of thread-safe Planning.
     * @param desires The set of known desires, including those collected by {@link Context}.
     * @param actions The set of known actions, including those collected by {@link Context}.
     * @return A new Map holding all the default evaluations of Beliefs from every Action's {@link Action#getPreconditions()}.
     */
    // This is collected BEFORE submitting the request as to avoid non-thread-safe evaluations.
    private Map<IdentifiedData<Belief>, Boolean> getBeliefEvaluations(Set<IdentifiedData<Desire>> desires, Set<IdentifiedData<Action>> actions) {
        Map<IdentifiedData<Belief>, Boolean> res = new HashMap<>();
        desires.forEach(data -> {
            Desire d = data.getValue();
            d.getDesires().forEach(belief -> {
                if (!res.containsKey(belief)) {
                    res.put(belief, belief.getValue().evaluate());
                }
            });
        });
        actions.forEach(data -> {
            Action a = data.getValue();
            a.getPreconditions().forEach(belief -> {
                if (!res.containsKey(belief)) {
                    res.put(belief, belief.getValue().evaluate());
                }
            });
        });
        return res;
    }

    // Debug Collection

    public List<SenIdentifier<Action>> getActionPlanIDs() {
        List<SenIdentifier<Action>> ids = new LinkedList<>();
        if  (currentPlan == null) {
            return ids;
        }
        currentPlan.actions().forEach(action -> {
            ids.add(action.getIdentifier());
        });
        return ids;
    }

    public @NotNull Pair<SenIdentifier<Desire>, Double> getCurrentPlanInformation() {
        return currentPlan == null ? Pair.of(null, 0.0D) : Pair.of(currentPlan.desire().getIdentifier(), currentPlan.totalCost());
    }

    // TODO: Work on the Planning system

    public static class Builder<O> {

        private final Agent<O> instance;

        private Consumer<SenseManager> sensorSetup = (sens) -> {};
        private Consumer<BeliefFactory> beliefSetup = (sens) -> new HashSet<>();
        private Function<Map<SenIdentifier<Belief>, IdentifiedData<Belief>>, Set<IdentifiedData<Desire>>> desireSetup = (beliefs) -> new HashSet<>();
        private Function<Map<SenIdentifier<Desire>, IdentifiedData<Desire>>, Set<IdentifiedData<Action>>> actionSetup = (m) -> new HashSet<>();

        public Builder(O owner, Supplier<Vec3> positionSupplier) {
            instance = new Agent<>(owner, positionSupplier);
        }

        public Builder<O> planBuilder(@NotNull PlanBuilder planBuilder) {
            instance.planner = planBuilder;
            return this;
        }

        public Builder<O> shouldAlwaysReplan(boolean shouldAlwaysReplan) {
            instance.shouldReplanAlways = shouldAlwaysReplan;
            return this;
        }

        /**
         * Used to allow the Agent to print Profiler Stacks to the Profiler of Minecraft.
         * Useful to track the processing time of custom Agents.
         * @param profiler The supplier which returns a valid, non-null ProfilerFiller
         * @return This Builder.
         */
        public Builder<O> setProfiler(@Nullable Supplier<ProfilerFiller> profiler) {
            instance.profiler = profiler;
            return this;
        }

        public Builder<O> contextHandler(@NotNull ContextHandler<O> contextHandler) {
            instance.contextHandler = contextHandler;
            return this;
        }

        public Builder<O> sensorSetup(@NotNull Consumer<SenseManager> sensorSetup) {
            this.sensorSetup = sensorSetup;
            return this;
        }

        public Builder<O> beliefSetup(@NotNull Consumer<BeliefFactory> beliefSetup) {
            this.beliefSetup = beliefSetup;
            return this;
        }

        public Builder<O> desireSetup(@NotNull Function<Map<SenIdentifier<Belief>, IdentifiedData<Belief>>, Set<IdentifiedData<Desire>>> desireSetup) {
            this.desireSetup = desireSetup;
            return this;
        }

        public Builder<O> actionSetup(@NotNull Function<Map<SenIdentifier<Desire>, IdentifiedData<Desire>>, Set<IdentifiedData<Action>>> actionSetup) {
            this.actionSetup = actionSetup;
            return this;
        }

        public Agent<O> build() {
            sensorSetup.accept(instance.senses);
            var beliefs = new BeliefFactory(instance.senses);
            beliefSetup.accept(beliefs);
            beliefs.closeFactory().forEach((data) -> {
                instance.beliefs.put(data.getIdentifier(), data);
            });
            desireSetup.apply(Map.copyOf(instance.beliefs)).forEach(data -> {
                instance.desires.put(data.getIdentifier(), data);
            });
            actionSetup.apply(Map.copyOf(instance.desires)).forEach(data -> {
                instance.actions.put(data.getIdentifier(), data);
            });
            return instance;
        }

    }

    public interface ContextHandler<A> {
        // Agent should make Context and pass
        void gatherContext(Context<A> ctx);
    }

}
