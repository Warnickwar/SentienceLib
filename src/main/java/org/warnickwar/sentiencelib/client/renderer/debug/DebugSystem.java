package org.warnickwar.sentiencelib.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.core.debug.DebugInformation;

import java.util.Collection;
import java.util.Set;

/**
 * <p>
 *      A modular system which is used by the Library to choose and dictate how
 *      to show the player Debug Information for development or debugging purposes.
 * </p>
 * <p>
 *     To implement a new System, developers must implement the {@link DebugSystem#render(Minecraft, DebugInformation, PoseStack, MultiBufferSource.BufferSource, double, double, double) render(...)}
 *     function, and define what {@link DebugComponentType Types} of information are required for the system to function
 *     through implementation of {@link DebugSystem#requiredComponents()}.
 * </p>
 * <p>
 *     Optionally, two extra functions are available for the sake of processing the information before rendering and after rendering,
 *     in the case that anything needs to be settled ahead of time and/or cleaned up after rendering.
 *     They may be found at {@link DebugSystem#preRender(Collection)} and {@link DebugSystem#postRender(Collection)}.
 * </p>
 * <p>
 *     Finally, implementors should be aware of the {@link DebugSystem#onSelected()} and {@link DebugSystem#onDeselect()}
 *     functions, which are functions used to set up and clear this system for Rendering. Due to the nature of the render system,
 *     only one Debug System can be rendering at a time, so these functions allow managing resources within the system that would no longer be used.
 * </p>
 * @see org.warnickwar.sentiencelib.client.debug.DebugManagement
 * @see DebugInformation
 * @author Warnickwar
 * @since 0.3
 */
public abstract class DebugSystem {

    /**
     * Passes all valid Render data for this System for pre-processing.
     *  This can be used to handle selections, for example.
     * @param validInformationComponents All valid DebugInformation currently in the Client Cache which holds the Archetype
     *                                   defined in {@link DebugSystem#requiredComponents()}.
     * @see DebugSystem#requiredComponents()
     * @author Warnickwar
     * @since 0.3
     */
    public void preRender(Collection<DebugInformation> validInformationComponents) {}

    /**
     * Handles rendering the DebugInformation currently passed into the system.
     * At minimum, the DebugInformation will have all the Components defined in {@link DebugSystem#requiredComponents()}.
     * @param client    The Minecraft Client of the Player
     * @param current   The current DebugInformation being passed into the system for rendering
     * @param poseStack The existing PoseStack for rendering
     * @param buffer    The renderer buffer currently active
     * @param camX      The Player Camera's X position
     * @param camY      The Player Camera's Y position
     * @param camZ      The Player Camera's Z position
     * @author Warnickwar
     * @see DebugSystem#requiredComponents()
     * @since 0.3
     */
    abstract public void render(Minecraft client, DebugInformation current, PoseStack poseStack, MultiBufferSource.BufferSource buffer, double camX, double camY, double camZ);

    /**
     * Handles cleanup from this system for rendering.
     * This is not required in most cases, but the option will still present itself.
     * @param validInformationComponents All valid DebugInformation currently in the Client Cache which holds the Archetype
     *                                   defined in {@link DebugSystem#requiredComponents()}.
     * @author Warnickwar
     * @since 0.3
     * @see DebugSystem#requiredComponents()
     */
    public void postRender(Collection<DebugInformation> validInformationComponents) {}

    /**
     * Handles cleanup of this System when deselected to render.
     */
    public void onDeselect() {}

    /**
     * Handles setup of this System when selected to render.
     */
    public void onSelected() {}

    /**
     * @return The Priority of Rendering for this system compared to other systems.
     *          Lower values get rendered first.
     */
    public int renderPriority() {
        return 0;
    }

    /**
     * Defines what {@link DebugComponentType Component Types} are required for this System to render.
     * All Debug Information passed into this system will have these Component Types at minimum.
     * @return A set of unique Component Types which are required at minimum.
     * @author Warnickwar
     * @since 0.3
     */
    abstract public Set<DebugComponentType<?>> requiredComponents();

    /**
     * <p>
     *      An optional function used to extract the current Partial Tick for rendering.
     * </p>
     * @param inst The Minecraft Client.
     * @return The current Partial Tick for Rendering.
     */
    protected final float getCurrentPartialTick(Minecraft inst) {
        return inst.getTimer().getGameTimeDeltaPartialTick(inst.isPaused());
    }
}
