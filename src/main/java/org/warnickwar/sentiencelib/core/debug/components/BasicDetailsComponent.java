package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

public class BasicDetailsComponent extends DebugComponent {

    private static final String NO_DESIRE_ID = "{No Desire}";
    private static final String NO_ACTION_ID = "{No Action}";

    private String currentDesire = NO_DESIRE_ID;
    private final String[] currentActions = new String[5];

    public BasicDetailsComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setCurrentDesire(@NotNull String newDesire) {
        this.currentDesire = newDesire;
    }

    public void setQueuedAction(@NotNull String actionId, @Range(from = 0, to = 4) int queuedPosition) {
        // Done solely to prevent crashes via
        //  indices outside the Array size.
        //noinspection ConstantValue
        if (queuedPosition < 0 || queuedPosition > 4) {
            Constants.LOG.warn("Invalid queued position {} for Action ID {}!", queuedPosition, actionId);
            return;
        }
        currentActions[queuedPosition] = actionId;
    }

    public String getCurrentDesire() {
        return currentDesire;
    }

    public boolean hasDesire() {
        return NO_DESIRE_ID.equals(currentDesire);
    }

    public String[] getCurrentActions() {
        return currentActions;
    }

    @Override
    public void write(FriendlyByteBuf networkBuffer) {
        // Write current Desire first
        networkBuffer.writeUtf(currentDesire);

        // Write queued actions in order from 0 to 4 (newest to oldest).
        //  Will always have a Length of 5 due to it being a constant array.
        for (String currentAction : currentActions) {
            networkBuffer.writeUtf(currentAction == null ? NO_ACTION_ID : currentAction);
        }
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        // Read new Desire
        currentDesire = networkBuffer.readUtf();

        // Read all new Actions
        //  This is Guaranteed due to the 5 Action limit.
        for (int i = 0; i < currentActions.length; i++) {
            currentActions[i] = networkBuffer.readUtf();
        }
    }

}
