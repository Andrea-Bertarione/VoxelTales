package dev.VoxelTales.Registries.RegistryEnums;

public enum ActionEnum {
    OPEN_DIALOGUE_ACTION("OpenDialogueAction");

    private final String name;
    ActionEnum(String name) {
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
