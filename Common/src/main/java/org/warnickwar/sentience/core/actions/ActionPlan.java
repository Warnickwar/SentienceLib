package org.warnickwar.sentience.core.actions;

import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentience.core.Desire;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import java.util.LinkedList;

public record ActionPlan(@NotNull IdentifiedData<Desire> desire, LinkedList<IdentifiedData<Action>> actions, double totalCost) {}
