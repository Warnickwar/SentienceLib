package org.warnickwar.sentiencelib.api.implementations;

import org.warnickwar.sentiencelib.api.core.sense.SenseType;
import org.warnickwar.sentiencelib.api.core.identifier.SenIdentifier;
import org.warnickwar.sentiencelib.api.implementations.senses.HurtSense;

/**
 * <p>
 *      Multiple Keys can be associated with one type of Sense, which allows for special
 *      handling of different tiers of Senses, even if they may do the same thing.
 * </p>
 * <p>
 *     A good example of this would be if an entity has Sight and Smell (take the Warden).
 *     A Sense can be used to detect what entities are close enough to be Smelt, and another Sense
 *     can detect entities in the general area around the entity.
 * </p>
 * <p>
 *      Ideally, Senses shouldn't be serialized directly, so it is safe to discard them after
 *      using them in memory.
 * </p>
 */
public final class SenseKeys {

    public static final SenseType<HurtSense> HURT = new SenseType<>(new SenIdentifier<>("hurtsense"));

}
