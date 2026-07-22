package com.github.littleemptydoll.lasthope.registry.definition.settings;

import com.github.littleemptydoll.lasthope.registry.definition.ContainerSound;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ContainerSettings {
    private InventoryLayout layout;
    private boolean preserveInventory;
    private @Nullable ResourceLocation guiTexture;
    private ContainerSound sound;

    public ContainerSettings( InventoryLayout layout ) {
        this.layout = layout;
        this.sound = ContainerSound.BOX;
    }

    public static ContainerSettings of(InventoryLayout layout) {
        return new ContainerSettings(layout);
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
}
