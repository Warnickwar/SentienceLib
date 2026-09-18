package org.warnickwar.sentiencelib.api.core.actions;

import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.Desire;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;

import java.util.LinkedList;

public record ActionPlan(@NotNull IdentifiedData<Desire> desire, LinkedList<IdentifiedData<Action>> actions, double totalCost) {}
