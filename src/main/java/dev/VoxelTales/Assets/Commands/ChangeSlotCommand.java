package dev.VoxelTales.Assets.Commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Utils.VoxelInventoryHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

public class ChangeSlotCommand extends AbstractPlayerCommand {
    public ChangeSlotCommand() {
        super("ChangeSlot", "Command to change the weapon slot");
    }

    private final RequiredArg<Integer> slot =
            withRequiredArg("slot", "new slot to assign", ArgTypes.INTEGER)
                    .addValidator(Validators.min(1))
                    .addValidator(Validators.max(9));

    @Override
    protected void execute(
            @NotNull CommandContext context,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        short formattedSlot = slot.get(context).shortValue();
        short newSlot = (short) (formattedSlot - 1);
        short itemSlot = VoxelCacheRegistry.get(CacheEnum.SLOT_CACHE, playerRef, Short.class);

        if (itemSlot == newSlot) {
            context.sendMessage(Message.raw("Weapon slot is already " + formattedSlot));
            return;
        }

        VoxelInventoryHelper.changeVoxelWeaponSlot(playerRef, store, ref, itemSlot, newSlot);
        context.sendMessage(Message.raw("Weapon slot changed to " + formattedSlot));
    }
}
