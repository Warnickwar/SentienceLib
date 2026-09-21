package org.warnickwar.sentiencelib.api.core;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.actions.ActionPlan;
import org.warnickwar.sentiencelib.api.core.context.Context;
import org.warnickwar.sentiencelib.api.core.context.IContextProvider;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.planning.PlanBuilder;
import org.warnickwar.sentiencelib.api.core.planning.Planners;
import org.warnickwar.sentiencelib.api.core.sense.SenseManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Agent<O> {

    private static final Collection<IContextProvider> DEFAULT_EMPTY_PROVIDERS = ImmutableSet.of();

    // Cache to avoid constantly formatting
    // If the Modid changes at runtime, we have bigger issues tbh.
    private static final String profilerString$execute = Constants.MODID + ":executingAgentPlan";
    private static final String profilerString$request = Constants.MODID + ":requestingPlan";
    private static final String profilerString$context = Constants.MODID + ":collectingContext";

    // Cache, don't need to make new defaults
    private static final ContextHandler<?> DEFAULT_CTX = (owner) -> DEFAULT_EMPTY_PROVIDERS;

    private final SenseManager senses = new SenseManager();
    // Used solely to allow for concurrent planning on the entity
    private final Map<Identity<Belief>, IdentifiedData<Belief>> beliefs = new Object2ObjectOpenHashMap<>();
    private final Map<Identity<Desire>, IdentifiedData<Desire>> desires = new Object2ObjectOpenHashMap<>();
    private final Map<Identity<Action>, IdentifiedData<Action>> actions = new Object2ObjectOpenHashMap<>();

    private final O owner;

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

    Agent(O owner) {
        this.owner = owner;
    }

    // Generic Methods

    @SuppressWarnings("unused")
    public O getOwner() {
        return owner;
    }

    @SuppressWarnings("unused")
    public SenseManager getSenses() {
        return senses;
    }

    @SuppressWarnings("unused")
    public Set<Identity<Belief>> getBeliefIds() { return ImmutableSet.copyOf(beliefs.keySet()); }

    @SuppressWarnings("unused")
    public Set<Identity<Desire>> getDesireIds() {
        return ImmutableSet.copyOf(desires.keySet());
    }

    public Set<Identity<Action>> getActions() {
        return ImmutableSet.copyOf(actions.keySet());
    }

    // Processing Methods

    public void tick() {
        senses.tick();
        ProfilerFiller filler = profiler.get();
        if (currentAction == null) {
            // Await new Plan
            handlePlanRequest(filler);
        } else if (currentPlan != null) {
            // Execute current Plan

            if (filler != null) {
                filler.push(profilerString$execute);
            }

            Action val = currentAction.getValue();
            val.tick();

            if (val.isComplete()) {
                val.stop();
                boolean end = currentPlan.actions().isEmpty();
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
                    Constants.LOG.error("Failed to generate plan for Agent {}!\nException: {}", this.owner, e);
                }
            }
            planRequest = null;
        }
    }

    private void submitPlanRequest(@Nullable ProfilerFiller filler)  {
        // Create new Request
        if (filler != null) {
            // Log the amount of time to request new Plan
            filler.push(profilerString$request);
        }

        Set<IdentifiedData<Desire>> desires = new HashSet<>(this.desires.values());
        Set<IdentifiedData<Action>> actions = new HashSet<>(this.actions.values());

        ExecutorService pool = Util.backgroundExecutor();

        // Collect Context

        Collection<IContextProvider> availableProviders = contextHandler.getFilteredProviders(owner);

        // This is one of those things where I wish Java had Regions like C#...

        // NOTE SEQUENTIAL SECTION

        Context<O> ctx = Context.of(owner);

        if (filler != null) {
            filler.push(profilerString$context);
        }

        availableProviders.forEach(p -> p.injectContext(ctx));
        var results = Context.extract(ctx);

        if (filler != null) {
            filler.pop();
        }

        // END SEQUENTIAL SECTION

        // NOTE MULTITHREADED SECTION

//
//        Context<O> ctx = Context.of(owner);
//
//        List<Callable<Void>> contextCollectionTasks = availableProviders.stream()
//            .map(p -> (Callable<Void>) () -> {
//                p.injectContext(ctx);
//                return null;
//            })
//            .toList();
//
//        // Request Pool to handle tasks
//        // TODO: Test this to ensure this doesn't block the Server frequently,
//        //  if that's the case it might be better to revert back to the old sequential
//        //  system
//        //  This is very likely to be stupid, and deprecated.
//        try {
//            pool.invokeAll(contextCollectionTasks);
//        } catch (InterruptedException ignored) {}
//
//        if (filler != null) {
//            filler.push(profilerString$context);
//        }
//
//        var results = Context.extract(ctx);
//
//        if (filler != null) {
//            filler.pop();
//        }

        // END MULTITHREADED SECTION

        desires.addAll(results.first());
        actions.addAll(results.second());

        // Belief Evaluations

        // We want to collect this sequentially to prevent race conditions
        Map<IdentifiedData<Belief>, Boolean> beliefEvals = getBeliefEvaluations(desires, actions);

        planRequest = pool.submit(() ->
            this.planner.plan(
                Collections.unmodifiableSet(desires),
                Collections.unmodifiableSet(actions),
                beliefEvals,
                lastDesire)
        );

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
     * @return A new, unmodifiable Map holding all the default evaluations of Beliefs from every Action and Belief's {@link Action#getPreconditions()}.
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
        return Collections.unmodifiableMap(res);
    }

    // Debug Collection

    public List<Identity<Action>> getActionPlanIDs() {
        List<Identity<Action>> ids = new LinkedList<>();
        if  (currentPlan == null) {
            return ids;
        }
        currentPlan.actions().forEach(action -> ids.add(action.getIdentifier()));
        return ids;
    }

    public @NotNull Pair<Identity<Desire>, Double> getCurrentPlanInformation() {
        return currentPlan == null ? Pair.of(null, 0.0D) : Pair.of(currentPlan.desire().getIdentifier(), currentPlan.totalCost());
    }

    public static <O> Agent.Builder<O> start(O owner) {
        return new Agent.Builder<>(owner);
    }

    // TODO: Documentation of this entire fucking class
    public static class Builder<O> {

        private final Agent<O> instance;

        private Consumer<SenseManager> sensorSetup = (sens) -> {};
        private Consumer<BeliefFactory> beliefSetup = (sens) -> {};
        private Consumer<DesireFactory> desireSetup = (beliefs) -> {};
        private Consumer<ActionFactory> actionSetup = (m) -> {};

        Builder(O owner) {
            instance = new Agent<>(owner);
        }

        @SuppressWarnings("unused")
        public Builder<O> planBuilder(@NotNull PlanBuilder planBuilder) {
            instance.planner = planBuilder;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder<O> shouldAlwaysReplan(boolean shouldAlwaysReplan) {
            instance.shouldReplanAlways = shouldAlwaysReplan;
            return this;
        }

        /**
         * Used to allow the Agent to print Profiler Stacks to the Profiler of Minecraft.
         * Useful to track the processing time of custom Agents, primarily to see how long
         * Context Collection takes.
         * @param profiler The supplier which returns a valid, non-null ProfilerFiller
         * @return This Builder.
         */
        @SuppressWarnings("unused")
        public Builder<O> setProfiler(@Nullable Supplier<ProfilerFiller> profiler) {
            instance.profiler = profiler;
            return this;
        }

        @SuppressWarnings("unused")
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

        public Builder<O> desireSetup(@NotNull Consumer<DesireFactory> desireSetup) {
            this.desireSetup = desireSetup;
            return this;
        }

        public Builder<O> actionSetup(@NotNull Consumer<ActionFactory> actionSetup) {
            this.actionSetup = actionSetup;
            return this;
        }

        public Agent<O> build() {
            // Set up Senses, important to every part of building
            sensorSetup.accept(instance.senses);

            // Collect Factories
            var beliefFactory = new BeliefFactory(instance.senses);
            var desireFactory = new DesireFactory(instance.beliefs);
            var actionFactory = new ActionFactory(instance.beliefs);

            // Delegate Factories out
            beliefSetup.accept(beliefFactory);
            desireSetup.accept(desireFactory);
            actionSetup.accept(actionFactory);

            // Collect returned data and inject into Agent
            // QUESTION maybe figure out how to have it inject directly into the Agent?

            beliefFactory.closeFactory().forEach(this::addBelief);
            desireFactory.closeFactory().forEach(this::addDesire);
            actionFactory.closeFactory().forEach(this::addAction);

            return instance;
        }

        private void addBelief(IdentifiedData<Belief> data) {
            instance.beliefs.put(data.getIdentifier(), data);
        }

        private void addDesire(IdentifiedData<Desire> data) {
            instance.desires.put(data.getIdentifier(), data);
        }

        private void addAction(IdentifiedData<Action> data) {
            instance.actions.put(data.getIdentifier(), data);
        }

    }

    // TODO: Migrate Context to collect all Providers
    //  rather than having the function directly mutate a Context object
    public interface ContextHandler<A> {
        // Agent should make Context and pass into this function

        // TODO: Documentation

        /**
         */
        Collection<IContextProvider> getContextProviders(A owner);

        private Collection<IContextProvider> getFilteredProviders(A owner) {
            Collection<IContextProvider> results = getContextProviders(owner);
            results.removeIf(p -> !p.canSupply(owner));
            return results;
        }
    }

}
