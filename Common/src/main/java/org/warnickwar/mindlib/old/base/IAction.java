package org.warnickwar.mindlib.old.base;

import java.util.HashSet;

public interface IAction {
    String getName();

    double getCost();

    double getUtility();

    double getAppeal();

    boolean isComplete();

    void start();

    void tick();

    void stop(boolean successful);

    HashSet<MindBelief> getPreconditions();

    HashSet<MindBelief> getEffects();
}
