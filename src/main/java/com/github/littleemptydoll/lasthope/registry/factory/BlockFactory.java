package com.github.littleemptydoll.lasthope.registry.factory;

import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@FunctionalInterface
public interface BlockFactory<T extends Block> {
    T create(
            BlockDefinition definition,
            BlockBehaviour.Properties properties
    );
}
