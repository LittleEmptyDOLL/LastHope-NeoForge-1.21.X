package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.client.model.ModelType;
import com.github.littleemptydoll.lasthope.registry.definition.AssetFolder;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    private BlockRegistry() {}

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LastHope.MODID);

    // Вспомогательный метод для регистрации блока
    private static <T extends Block> DeferredBlock<T> registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            Supplier<BlockBehaviour.Properties> properties)
    {
        DeferredBlock<T> block = BLOCKS.register(name, () -> factory.apply(properties.get()));
        ModItems.registerBlockItem(name, block);

        return block;
    }

    // Используется генераторами данных
    public static BlockDefinition register(
            String name,
            Function<BlockBehaviour.Properties, ? extends Block> factory,
            ModelType modelType,
            AssetFolder assetFolder,
            Supplier<BlockBehaviour.Properties> properties
    ) {
        DeferredBlock<? extends  Block> block = registerBlock(
                name,
                factory,
                properties
        );
        BlockDefinition definition = BlockDefinition.of(
                block,
                modelType,
                assetFolder,
                properties
        );
        BLOCK_DEFINITIONS.add(definition);

        return definition;
    }

    private static final List<BlockDefinition> BLOCK_DEFINITIONS = new ArrayList<>();

    public static List<BlockDefinition> getBlockDefinitions() {
        return  List.copyOf(BLOCK_DEFINITIONS);
    }
}
