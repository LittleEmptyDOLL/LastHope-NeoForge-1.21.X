package com.github.littleemptydoll.lasthope.registry.definition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.HashMap;
import java.util.Map;

public class BlockDefinitionRegistry {
    private static final Map<DeferredBlock<?>, BlockDefinition> DEFINITIONS = new HashMap<>();

    public static void register(
            Block block,
            BlockDefinition definition
    ) {
        DEFINITIONS.put(block, definition);
    }

    public static BlockDefinition get(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return DEFINITIONS.get(id);
    }

    public static BlockDefinition get(BlockState state) {
        return null;
    }

    public static BlockDefinition get(DeferredBlock<?> block) {
        return null;
    }
}
