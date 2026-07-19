package com.github.littleemptydoll.lasthope.registry.definition;

import com.github.littleemptydoll.lasthope.client.model.ModelType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;

public record BlockDefinition(
        DeferredBlock<? extends Block> block,
        // Поведение
        ModelType modelType,
        // Ресурсы
        AssetFolder assetFolder,
        // Параметры
        Supplier<BlockBehaviour.Properties> properties
) {
}
