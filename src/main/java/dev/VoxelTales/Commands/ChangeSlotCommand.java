package dev.VoxelTales.Commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Utils.InventoryUtils;
import dev.VoxelTales.Utils.SwordFactory;
import dev.VoxelTales.VoxelTalesPlugin;
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
        VoxelPlayerComponent configs = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getVoxelPlayerComponent());

        short formattedSlot = slot.get(context).shortValue();
        short newSlot = (short) (formattedSlot - 1);
        short itemSlot = configs.getWeaponSlot();

        if (itemSlot == newSlot) {
            context.sendMessage(Message.raw("Weapon slot is already " + formattedSlot));
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());

        player.getInventory().getHotbar().setItemStackForSlot(itemSlot, null);
        player.getInventory().getHotbar().setItemStackForSlot(newSlot, SwordFactory.generateItemStack(weaponHandlerComponent));

        InventoryUtils.syncHotbar(playerRef, ref, store);

        configs.setWeaponSlot(newSlot);
        VoxelTalesPlugin.get().getSlotCache().put(playerRef.getUuid(), newSlot);
        context.sendMessage(Message.raw("Weapon slot changed to " + formattedSlot));
    }
}
