package dev.VoxelTales.Assets.Gameplay;

import java.util.Map;

public enum WeaponType {
    DAGGER      ("Daggers"),
    SHORTBOW    ("Shortbow"),
    SPEAR       ("Spear"),
    SHORTSWORD  ("Shortsword"),
    SWORD       ("Sword"),
    BATTLEAXE   ("Battleaxe"),
    CROSSBOW    ("Crossbow"),
    LONGSWORD   ("Longsword"),
    MACE        ("Mace");

    public final String name;

    WeaponType(String name) {
        this.name = name;
    }

    private static final Map<String, WeaponType> MATRIX = Map.of(
            "Small_Small",   DAGGER,
            "Small_Medium",  SHORTBOW,
            "Small_Heavy",   SPEAR,
            "Medium_Small",  SHORTSWORD,
            "Medium_Medium", SWORD,
            "Medium_Heavy",  BATTLEAXE,
            "Heavy_Small",   CROSSBOW,
            "Heavy_Medium",  LONGSWORD,
            "Heavy_Heavy",   MACE
    );

    public static WeaponType resolve(WeaponComponentWeight bladeWeight, WeaponComponentWeight handleWeight) {
        return MATRIX.get(bladeWeight.getConfigName() + "_" + handleWeight.getConfigName());
    }
}
