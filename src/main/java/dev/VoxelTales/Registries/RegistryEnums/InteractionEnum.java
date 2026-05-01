package dev.VoxelTales.Registries.RegistryEnums;

public enum InteractionEnum {
    ROUTER_SIGNATURE_INTERACTION("RouterSignatureInteraction"),
    ROUTER_SKILL_INTERACTION("RouterSkillInteraction"),
    DAMAGE_ENTITY("DamageEntity");

    private final String name;
    InteractionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
