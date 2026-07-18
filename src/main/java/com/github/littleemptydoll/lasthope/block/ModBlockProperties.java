package com.github.littleemptydoll.lasthope.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlockProperties {
    private ModBlockProperties() {}

    public static BlockBehaviour.Properties cardboard() {
        return BlockBehaviour.Properties.of()
                .strength(0.3F);
    }

    public static BlockBehaviour.Properties metalContainer() {
        return BlockBehaviour.Properties.of()
                .strength(3.5F);
    }
}
