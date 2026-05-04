package dev.VoxelTales.Assets.Dialogues;

import com.hypixel.hytale.server.core.Message;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;

public class DefaultDialogues {
    public static final String CONTINUE_TEXT = "[Continue...]";
    public static final String ClOSE_TEXT = "[Close...]";
    public static final String OPEN_FORGE_TEXT = "[Forge a new sword...]";
    public static final String ACCEPT_QUEST_TEXT = "[Accept]";

    public static DialogueController.DialogueResponse defaultCloseResponse() {
        return DialogueController.DialogueResponse.close(ClOSE_TEXT);
    }
    public static DialogueController.DialogueResponse defaultOpenForgeResponse() {
        return DialogueController.DialogueResponse.custom(OPEN_FORGE_TEXT).withCallback((_, page) -> {
            //page.close();

            WeaponForgerPage newPage = VoxelCacheRegistry.staticGet(CacheEnum.WEAPON_FORGER_PAGE, page.getPlayerRef(), WeaponForgerPage.class);

            if (newPage != null) {
                newPage.open();
            } else {
                page.getPlayerRef().sendMessage(Message.parse("Failed to open UI: " + CacheEnum.WEAPON_FORGER_PAGE.name()));
            }
        });
    }
}
