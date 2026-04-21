package dev.VoxelTales.Assets.Actions.Builders;

import dev.VoxelTales.Assets.Actions.OpenDialogueAction;
import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * <p>Builder for {@link OpenDialogueAction}.</p>
 * <p>TODO: Add JavaDoc documentation here.</p>
 */
public class BuilderOpenDialogueAction extends BuilderActionBase {
    /* TODO: Add private data variables to store for OpenDialogueAction.*/
    @NullableDecl
    @Override
    public String getShortDescription() {
        return "Short description for OpenDialogueAction.";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "Long description for OpenDialogueAction.";
    }

    @NullableDecl
    @Override
    public Action build(BuilderSupport builderSupport) {
        return new OpenDialogueAction(this, builderSupport);
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    public BuilderOpenDialogueAction readConfig(JsonElement data) {
        return this;
    }

    /* TODO: Add getters for private data variables for OpenDialogueAction to access.*/
}