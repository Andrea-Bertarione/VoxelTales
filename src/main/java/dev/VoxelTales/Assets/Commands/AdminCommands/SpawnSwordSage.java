package dev.VoxelTales.Assets.Commands.AdminCommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Controllers.SwordSageController;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

public class SpawnSwordSage extends AbstractPlayerCommand {
    public SpawnSwordSage() {
        super("spawnSwordSage", "Spawns a sword sage");
    }

    private final OptionalArg<String> modelId = withOptionalArg("modelID", "The model to spawn the sword sage with", ArgTypes.STRING);
    private final OptionalArg<Vector3i> rotation = withOptionalArg("rotation", "The rotation of the sword sage", ArgTypes.VECTOR3I);
    private final OptionalArg<Vector3i> position = withOptionalArg("position", "The position to spawn the sword sage at", ArgTypes.VECTOR3I);
    private final OptionalArg<String> equipmentId = withOptionalArg("equipmentID", "The equipment ID to spawn the sword sage with", ArgTypes.STRING);
    private final OptionalArg<String> roleId = withOptionalArg("roleID", "The role ID to spawn the sword sage with", ArgTypes.STRING);
    private final OptionalArg<String> interactionId = withOptionalArg("interactionID", "The interaction ID to spawn the sword sage with", ArgTypes.STRING);

    @Override
    protected void execute(
            @NotNull CommandContext context,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        String modelId = this.modelId.get(context);
        Vector3i rotation = this.rotation.get(context);
        Vector3i position = this.position.get(context);
        String equipmentId = this.equipmentId.get(context);
        String roleId = this.roleId.get(context);
        String interactionId = this.interactionId.get(context);

        TransformComponent playerTransform = store.ensureAndGetComponent(ref, TransformComponent.getComponentType());
        Vector3d playerPosition = playerTransform.getPosition();

        Vector3f finalRotation = rotation != null ? new Vector3f(rotation.getX(), rotation.getY(), rotation.getZ()) : null;
        Vector3d finalPosition = position != null ? new Vector3d(position.getX(), position.getY(), position.getZ()) : playerPosition;

        SwordSageController.spawnSwordSage(world, finalPosition, finalRotation, modelId, equipmentId, roleId, interactionId);
    }
}
