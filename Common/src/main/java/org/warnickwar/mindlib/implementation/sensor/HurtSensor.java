package org.warnickwar.mindlib.implementation.sensor;

import org.warnickwar.mindlib.old.base.MindAgent;
import org.warnickwar.mindlib.old.base.MindSensor;
import net.minecraft.world.entity.LivingEntity;

public class HurtSensor extends MindSensor {

    private final MindAgent<LivingEntity> host;
    private float lastHurtTime;

    public HurtSensor(MindAgent<LivingEntity> host) {
        super(1);
        this.host = host;
        host.getSubscriber().register();
    }

    @Override
    protected void onTick() {

    }

}
