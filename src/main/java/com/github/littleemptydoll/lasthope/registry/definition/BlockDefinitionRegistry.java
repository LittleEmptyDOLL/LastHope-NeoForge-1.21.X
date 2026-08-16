package com.github.littleemptydoll.lasthope.registry.definition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;

public class BlockDefinitionRegistry {
    private static final Map<ResourceLocation, BlockDefinition> DEFINITIONS = new LinkedHashMap<>();

    private BlockDefinitionRegistry() {}

    // Регистрируем описание блока
    public static void register(BlockDefinition definition) {
        DEFINITIONS.put(
                definition.block().getId(),
                definition
        );
    }

    // Получить описание по DeferredBlock
    public static BlockDefinition get(DeferredBlock<?> block) {
        return DEFINITIONS.get(block.getId());
    }

    // Получить описание по экземпляру блока
    public static BlockDefinition get(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return DEFINITIONS.get(id);
    }

    // Получить описание по BlockState
    public static BlockDefinition get(BlockState state) {
        return get(state.getBlock());
    }

    // Все зарегистрированные определения
    public static Collection<BlockDefinition> getDefinitions() {
        return Collections.unmodifiableCollection(
                DEFINITIONS.values()
        );
    }

    //
    public static List<BlockDefinition> getContainerDefinitions() {
        return DEFINITIONS.values().stream()
                .filter(definition -> definition.containerSettings() != null)
                .toList();
    }

    //
    public static List<? extends Block> getContainerBlocks() {
        return getContainerDefinitions().stream()
                .map(definition -> definition.block().get())
                .toList();
    }
}
