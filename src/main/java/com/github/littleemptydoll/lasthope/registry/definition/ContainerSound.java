package com.github.littleemptydoll.lasthope.registry.definition;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum ContainerSound {
    BOX(SoundEvents.BARREL_OPEN, SoundEvents.BARREL_CLOSE),
    WOOD(SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE),
    METAL(SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE),
    SAFE(SoundEvents.SHULKER_BOX_OPEN, SoundEvents.SHULKER_BOX_CLOSE),
    FRIDGE(SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE);

    private final SoundEvent open;
    private final SoundEvent close;

    ContainerSound(SoundEvent open, SoundEvent close) {
        this.open = open;
        this.close = close;
    }

    public SoundEvent open() {
        return open;
    }

    public SoundEvent close() {
        return close;
    }
}
