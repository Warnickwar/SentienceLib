package org.warnickwar.sentience.test.senses;

import org.warnickwar.sentience.core.sense.SenseType;
import org.warnickwar.sentience.core.identifier.AiIdentifier;

public final class Senses {

    public static final SenseType<HurtSense> HURT = new SenseType<>(new AiIdentifier<>("hurtsense"));

}
