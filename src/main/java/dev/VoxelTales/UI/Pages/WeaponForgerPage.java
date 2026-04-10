package dev.VoxelTales.UI.Pages;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.UI.Pages.Default.VoxelPageUI;

public class WeaponForgerPage extends VoxelPageUI {
    public WeaponForgerPage(PlayerRef playerRef) {
        super(playerRef);
    }

    public void update() {
        super.update("Pages/WeaponForger.html");
    }

    @Override
    public void open() {
        //Do custom stuff before opening!
        super.open();
    }
}
