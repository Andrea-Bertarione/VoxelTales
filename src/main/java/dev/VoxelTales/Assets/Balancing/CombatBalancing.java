package dev.VoxelTales.Assets.Balancing;

public class CombatBalancing {
    public static final float BASE_DAMAGE = 14.0f;
    public static final float LEVEL_DAMAGE_MULTIPLIER = 0.0125f;
    public static final float SPEED_MULTIPLIER = 1.5f;

    public static float getSpeedDamageMultiplier(float speed) {
        float speedMultiplier = 1.0f + ((1.0f - speed) * SPEED_MULTIPLIER);
        return Math.max(0.1f, speedMultiplier);
    }


}
