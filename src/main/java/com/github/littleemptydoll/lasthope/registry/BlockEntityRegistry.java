package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.blockentity.GenericContainerBlockEntity;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockEntityRegistry {
    private BlockEntityRegistry() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    LastHope.MODID
            );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<GenericContainerBlockEntity>
            > GENERIC_CONTAINER = BLOCK_ENTITIES.register(
                    "generic_container",
            () -> BlockEntityType.Builder.of(
                    GenericContainerBlockEntity::new,
                    BlockDefinitionRegistry.getContainerBlocks().toArray(Block[]::new)
            ).build(null)
    );
}
