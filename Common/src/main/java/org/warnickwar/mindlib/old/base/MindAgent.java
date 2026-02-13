package org.warnickwar.mindlib.old.base;

import org.warnickwar.mindlib.base.context.IContextProvider;
import org.warnickwar.mindlib.events.AIEvent;
import org.warnickwar.mindlib.events.AIEventListener;
import org.warnickwar.mindlib.old.base.kits.PlanContext;
import org.warnickwar.mindlib.old.base.plan.ActionPlan;
import org.warnickwar.mindlib.old.base.plan.BasicPlanner;
import org.warnickwar.mindlib.threading.JobManager;
import org.warnickwar.mindlib.utils.TypesafeKey;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public abstract class MindAgent<T> implements Tickable {

    private static Logger LOGGER = LogUtils.getLogger();

    @Nullable
    protected MindGoal lastGoal;
    @Nullable
    protected ActionPlan plan;
    @Nullable
    protected MindGoal currentGoal;
    @Nullable
    protected IAction currentAction;

    private final Map<Class<? extends AIEvent>, List<AIEventListener<? extends AIEvent>>> actionEvents;
    private final ListenerSubscriber cachedSubscriber;

    private final Map<TypesafeKey<? extends MindSensor>, ? extends MindSensor> sensors;
    private final Map<TypesafeKey<? extends MindBelief>, ? extends MindBelief> beliefs;
    // Not private so Planners alone can have access
    final Map<TypesafeKey<IAction>, IAction> actions;
    private final Map<TypesafeKey<MindGoal>, MindGoal> goals;

    protected final IPlanner planner;

    private final BeliefView cachedBeliefView;
    private final SensorView cachedSensorView;

    @Nullable
    private Future<ActionPlan> planFuture;
    private boolean isPlanning;

    public MindAgent() {
        this.sensors = new HashMap<>();
        this.cachedSensorView = new SensorView(this.sensors);
        this.beliefs = new HashMap<>();
        this.cachedBeliefView = new BeliefView(this.beliefs);
        this.actions = new HashMap<>();
        HashSet<IAction> tempActions = new HashSet<>();
        this.goals = new HashMap<>();
        HashSet<MindGoal> tempGoals = new HashSet<>();
        this.actionEvents = new HashMap<>();
        this.cachedSubscriber = new ListenerSubscriber(this);
        setupSensors(this.sensors);
        setupBeliefs(this.beliefs);
        setupActions(tempActions);
        tempActions.forEach(act -> this.actions.put(act.getName(), act));
        setupGoals(tempGoals);
        tempGoals.forEach(goal -> this.goals.put(goal.getName(), goal));
        this.planner = createPlanner();
        this.isPlanning = false;
    }

    public abstract T getOwner();

    public HashSet<IAction> getActions() {
        return new HashSet<>(this.actions.values());
    }

    // <---> TICK <--->

    protected void onPreTick() {}

    protected void onPlanningTick() {}

    protected void onPlanRunTick() {}

    public final void tick() {
        onPreTick();
        // Tick all sensors to work properly
        sensors.values().forEach(MindSensor::tick);
        if (currentAction == null) {
            onPlanningTick();
            calculatePlan();
            if (plan != null && !plan.getActions().isEmpty()) {

                this.currentGoal = plan.getGoal();
                this.currentAction = plan.getActions().pop();
                if (currentAction.getPreconditions().stream().allMatch(MindBelief::evaluate)) {
                    this.currentAction.start();
                } else {
                    // Finish Plan early; action cannot be run as expected
                    finishPlan();
                    this.onPlanFinish();
                }
            }
            onPlanningEndTick();
        }

        if (plan != null && currentAction != null) {
            onPlanRunTick();
            currentAction.tick();

            if (currentAction.isComplete()) {
                currentAction.stop(true);
                currentAction = null;

                if (plan.getActions().isEmpty()) {
                    lastGoal = currentGoal;
                    finishPlan();
                    this.onPlanFinish();
                }
            }
            onPlanRunEndTick();
        }
        onPostTick();
    }

    protected void onPlanningEndTick() {}

    protected void onPlanRunEndTick() {}

    protected void onPostTick() {}

    final void calculatePlan() {
        if (isPlanning) {
            assert planFuture != null;
            if (planFuture.isDone()) {
                try {
                    ActionPlan res = planFuture.get();
                    if (res != null) plan = res;
                } catch (ExecutionException | InterruptedException | CancellationException exception) {
                    // Failed or Interrupted Plan, ignore and continue. Remove PlanFuture when done
                    //  to try and re-request a new plan.
                } catch (Exception excep) {
                    // Catch and log any other errors resulting from the asynchronous operation
                    LOGGER.error("Failed to generate plan for Agent of {}!\nError Message: {}", this.getOwner(), excep);
                }
                this.isPlanning = false;
                planFuture = null;
            }
        } else {
            // Always be planning something, that will be useful if any goals are of more priority than something else
            //  (e.g. Living over Farming)
            double priorityLevel = currentGoal == null ? 0 :
                currentGoal.getPriority();

            HashSet<MindGoal> goalsToEvaluate;

            if (currentGoal == null) {
                goalsToEvaluate = goals.values().stream().filter(g -> g.getPriority() > priorityLevel).collect(Collectors.toCollection(HashSet::new));
            } else {
                goalsToEvaluate = new HashSet<>(goals.values());
            }

            planFuture = JobManager.submitJob(() -> this.planSupplier(this, goalsToEvaluate, lastGoal));
            this.isPlanning = true;
        }
    }

    protected ActionPlan planSupplier(MindAgent<T> agent, HashSet<MindGoal> goals, @Nullable MindGoal lastGoal) {
        return planner.plan(agent, goals, lastGoal);
    }

    public void finishPlan() {
        if (currentAction != null) {
            this.currentAction.stop(false);
            this.currentAction = null;
        }
        if (planFuture != null) {
            this.planFuture.cancel(true);
        }
        plan = null;
        lastGoal = currentGoal;
        currentGoal = null;
    }

    // <---> ABSTRACTS <--->

    public abstract Vec3 getLocation();

    public abstract Level getLevel();

    protected abstract void setupSensors(Map<String, MindSensor> sensors);

    protected abstract void setupBeliefs(Map<String, MindBelief> beliefs);

    protected abstract void setupActions(Set<IAction> actions);

    protected abstract void setupGoals(Set<MindGoal> goals);

    protected abstract void onPlanFinish();

    /**
     * <p>
     *     An optional method which can be implemented to
     *     designate how an Agent collect contextual actions and goals.
     * </p>
     * <p>
     *     This allows for the environment to affect how an agent should
     * </p>
     * An optional method which can be implemented to
     *  allow for the agent to collect actions outside the agent's default
     *  Action list.
     * @return a Collector of Actions which are combined when planning.
     * @see IContextProvider
     */
    @SuppressWarnings("unchecked")
    public PlanContext<MindAgent<T>> collectContext() { return (PlanContext<MindAgent<T>>) PlanContext.NONE; }

    // <---> GETTERS & ADDERS <--->

    @Nullable
    public IAction getCurrentAction() {
        return this.currentAction;
    }

    @Nullable
    public MindGoal getCurrentGoal() {
        return this.currentGoal;
    }

    @Nullable
    public ActionPlan getCurrentPlan() {
        return this.plan;
    }

    public void addSensor(String name, MindSensor sensor) {
        this.sensors.put(name, sensor);
    }

    @Nullable
    public MindSensor removeSensor(String name) {
        return this.sensors.remove(name);
    }

    public void addBelief(MindBelief belief) {
        this.beliefs.put(belief.getName(), belief);
    }

    @Nullable
    public MindBelief removeBelief(String name) {
        return this.beliefs.remove(name);
    }

    // This is only relevant to Beliefs because Beliefs are
    //  otherwise loaded by merit of being used by both Goals and
    //  Actions to evaluate true/false values. By removing the
    //  Belief, but not removing the associated action and goals,
    //  The belief will still be kept in memory as long as it is used
    //  by other actions and goals.
    // This function should be used when one does not care what Beliefs are
    //  removed, nor that they are still in memory.
    @Nullable
    public MindBelief removeBeliefAndUpdate(String name) {
        MindBelief res = removeBelief(name);
        if (res == null) return null;
        final AtomicBoolean shouldReplan = new AtomicBoolean(false);
        actions.values().removeIf(act -> {
            boolean temp = act.getPreconditions().contains(res) || act.getEffects().contains(res);
            if (temp) shouldReplan.set(true);
            return temp;
        });
        goals.values().removeIf(goal -> {
            boolean temp = goal.getEffects().contains(res);
            if (temp) shouldReplan.set(true);
            return temp;
        });
        if (shouldReplan.get()) finishPlan();
        return res;
    }

    public void addAction(IAction action) {
        this.actions.put(action.getName(), action);
        this.finishPlan();
    }

    @Nullable
    public IAction removeAction(String name) {
        IAction res = this.actions.remove(name);
        // Finish the plan as the current plan might be dependent on this action
        if (res != null) this.finishPlan();
        return res;
    }

    public void addGoal(MindGoal goal) {
        this.goals.put(goal.getName(), goal);
        this.finishPlan();
    }

    @Nullable
    public MindGoal removeGoal(String name) {
        MindGoal res = this.goals.remove(name);
        // Finish the plan as the current plan might be dependent on this goal, or influenced
        //  by it
        if (res != null) this.finishPlan();
        return res;
    }

    // <---> VIEWS & SUBSCRIBERS <--->

    @NotNull
    public ListenerSubscriber getSubscriber() {
        return this.cachedSubscriber;
    }

    @NotNull
    public BeliefView getBeliefView() { return this.cachedBeliefView; }

    @NotNull
    public SensorView getSensorView() { return this.cachedSensorView; }

    @NotNull
    protected IPlanner createPlanner() {
        return new BasicPlanner();
    }

    // <---> EVENTS <--->

    @SuppressWarnings("unchecked")
    public <E extends AIEvent> E emitEvent(@NotNull E event) {
        if (this.actionEvents.containsKey(event.getClass())) {
            ((Consumer<E>) this.actionEvents.get(event.getClass())).accept(event);
        }
        return event;
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class ListenerSubscriber {

        private final MindAgent<?> agent;

        ListenerSubscriber(MindAgent<?> agent) {
            this.agent = agent;
        }

        public <E extends AIEvent> void register(Class<E> eventClass, AIEventListener<E> cons) {
            agent.actionEvents.computeIfAbsent(eventClass, (k) -> new ArrayList<>()).add(cons);
        }

        public <E extends AIEvent> void remove(Class<E> eventClass, AIEventListener<E> cons) {
            agent.actionEvents.computeIfPresent(eventClass, (k, v) -> {
                v.remove(cons);
                return v.isEmpty() ? null : v;
            });
        }
    }

    // This seems redundant, and it is, but it's so that
    //  objects outside the agent cannot access the corresponding maps.
    //  Basically, shitty security.
    protected abstract static class View<T> {
        protected final Map<String, T> backedMap;

        View(Map<String, T> map) {
            this.backedMap = map;
        }

        public abstract T get(String key);

        public boolean has(String key) { return this.backedMap.containsKey(key); }
    }

    public static class SensorView extends View<MindSensor> {

        SensorView(Map<String, MindSensor> backed) {
            super(backed);
        }

        @Override
        public MindSensor get(String key) {
            return this.backedMap.get(key);
        }

    }

    public static class BeliefView extends View<MindBelief> {

        BeliefView(Map<String, MindBelief> backed) {
            super(backed);
        }

        public MindBelief get(String key) {
            return this.backedMap.get(key);
        }

    }
}
