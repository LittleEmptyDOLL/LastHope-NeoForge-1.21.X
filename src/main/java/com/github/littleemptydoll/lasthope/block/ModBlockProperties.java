package com.github.littleemptydoll.lasthope.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlockProperties {
    private ModBlockProperties() {}

    public static BlockBehaviour.Properties decoration() {
        return BlockBehaviour.Properties.of()
                .strength(1.0F);
    }

    public static BlockBehaviour.Properties softDecoration() {
        return BlockBehaviour.Properties.of()
                .strength(0.3F)
                .sound(SoundType.WOOL)
                .noOcclusion();
    }

    public static BlockBehaviour.Properties woodDecoration() {
        return BlockBehaviour.Properties.of()
                .strength(1.5F)
                .sound(SoundType.WOOD);
    }

    public static BlockBehaviour.Properties metalDecoration() {
        return BlockBehaviour.Properties.of()
                .strength(3.5F)
                .sound(SoundType.METAL);
    }

    public static BlockBehaviour.Properties glassDecoration() {
        return BlockBehaviour.Properties.of()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion();
    }
}
