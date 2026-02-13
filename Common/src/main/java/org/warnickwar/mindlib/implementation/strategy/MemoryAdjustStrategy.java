package org.warnickwar.mindlib.implementation.strategy;

import org.warnickwar.mindlib.base.IStrategy;
import org.warnickwar.mindlib.base.memories.MemoryManager;
import org.warnickwar.mindlib.old.base.memory.TemplateValue;

import java.util.HashSet;

public class MemoryAdjustStrategy implements IStrategy {

    private final MemoryManager manager;
    private final IStrategy strategy;

    private final HashSet<TemplateValue<?>> templateValues;

    public MemoryAdjustStrategy(MemoryManager manager, IStrategy actualStrategy) {
        this.manager = manager;
        this.strategy = actualStrategy;
        this.templateValues = new HashSet<>();
    }

    public MemoryAdjustStrategy addMemoryAdjustment(TemplateValue<?> valueToAdjust) {
        templateValues.add(valueToAdjust);
        return this;
    }

    @Override
    public void start() {
        strategy.start();
    }

    @Override
    public void tick() {
        strategy.tick();
    }

    @Override
    public void stop(boolean successful) {
        if (successful) templateValues.forEach(val -> val.applyToMemories(manager));
        strategy.stop(successful);
    }

    @Override
    public boolean canPerform() {
        return strategy.canPerform();
    }

    @Override
    public boolean isComplete() {
        return strategy.isComplete();
    }

}
