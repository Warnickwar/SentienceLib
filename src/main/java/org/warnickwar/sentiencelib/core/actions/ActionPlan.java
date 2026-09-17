package org.warnickwar.sentiencelib.core.actions;

import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.core.Desire;
import org.warnickwar.sentiencelib.core.identifier.IdentifiedData;

import java.util.LinkedList;

public record ActionPlan(@NotNull IdentifiedData<Desire> desire, LinkedList<IdentifiedData<Action>> actions, double totalCost) {}
