package dev.VoxelTales.Assets.Dialogues;

import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;

public class RepeatedDialogues {
    public static final String CONTINUE_TEXT = "[Continue...]";
    public static final String ClOSE_TEXT = "[Close...]";
    public static final String OPEN_FORGE_TEXT = "[Forge a new sword...]";

    public static final DialogueController.DialogueResponse DEFAULT_CLOSE_RESPONSE = DialogueController.DialogueResponse.close(ClOSE_TEXT);
    public static final DialogueController.DialogueResponse DEFAULT_OPEN_FORGE_RESPONSE = DialogueController.DialogueResponse.callback(OPEN_FORGE_TEXT, (_, page) -> {
        page.close();

        WeaponForgerPage newPage = VoxelCacheRegistry.get("WeaponForgerPage", page.getPlayerRef(), WeaponForgerPage.class);
        newPage.open();
    });
}
