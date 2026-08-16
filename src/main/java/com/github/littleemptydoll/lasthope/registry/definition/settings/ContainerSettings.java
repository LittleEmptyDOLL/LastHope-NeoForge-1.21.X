package com.github.littleemptydoll.lasthope.registry.definition.settings;

import com.github.littleemptydoll.lasthope.registry.definition.ContainerSound;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ContainerSettings {
    private final InventoryLayout layout;
    private boolean preserveInventory;
    private @Nullable ResourceLocation guiTexture;
    private ContainerSound sound;

    public ContainerSettings(
            InventoryLayout layout,
            ContainerSound sound
    ) {
        this.layout = layout;
        this.sound = sound;
    }

    public static ContainerSettings of(
            InventoryLayout layout,
            ContainerSound sound
    ) {
        return new ContainerSettings(layout, sound);
    }

    public ContainerSettings preserveInventory(boolean preserve) {
        this.preserveInventory = preserve;
        return this;
    }

    public ContainerSettings gui(ResourceLocation texture) {
        this.guiTexture = texture;
        return this;
    }

    public ContainerSettings sound(ContainerSound sound) {
        this.sound = sound;
        return this;
    }

    public InventoryLayout layout() {
        return layout;
    }

    public boolean preserveInventory() {
        return preserveInventory;
    }

    public @Nullable ResourceLocation guiTexture() {
        return guiTexture;
    }

    public ContainerSound sound() {
        return sound;
    }
}
