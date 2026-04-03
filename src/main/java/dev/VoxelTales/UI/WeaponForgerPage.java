package dev.VoxelTales.UI;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.UI.Default.VoxelPageUI;

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
