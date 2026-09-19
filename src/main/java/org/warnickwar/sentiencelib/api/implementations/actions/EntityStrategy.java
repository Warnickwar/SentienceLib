package org.warnickwar.sentiencelib.api.implementations.actions;

import net.minecraft.world.entity.LivingEntity;
import org.warnickwar.sentiencelib.api.core.actions.IStrategy;

public abstract class EntityStrategy<T extends LivingEntity> implements IStrategy {

    protected final T entity;

    protected EntityStrategy(T entity) {
        this.entity = entity;
    }


}
