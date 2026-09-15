package com.github.littleemptydoll.lasthope.client.model;

import com.github.littleemptydoll.lasthope.block.BlockRotation;
import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.MultiBlockDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class BlockStateGenerator {
    private BlockStateGenerator() {}

    public static void register(
            BlockStateProvider provider,
            BlockDefinition definition
    ) {
        if (definition.block().get() instanceof AbstractDecorativeBlock) {
            registerDecorative(provider, definition);
            return;
        }

        switch (definition.modelType()) {
            case SIMPLE -> registerSimple(provider, definition);
        }
    }

    private static void registerSimple(
            BlockStateProvider provider,
            BlockDefinition definition
    ) {
        provider.simpleBlock(
                definition.block().get(),
                provider.models().getExistingFile(
                        provider.modLoc(
                                AssetPaths.getBlockModelPath(definition)
                        )
                )
        );
    }

    private static void registerDecorative(
            BlockStateProvider provider,
            BlockDefinition definition
    ) {
        var block = definition.block().get();
        MultiBlockDefinition multiBlock = definition.multiBlock();

        provider.getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(provider.models().getExistingFile(
                                provider.modLoc(getModelPath(definition, multiBlock, state))
                        ))
                        .rotationY(modelRotation(state, definition))
                        .build());
    }

    private static String getModelPath(
            BlockDefinition definition,
            MultiBlockDefinition multiBlock,
            BlockState state
    ) {
        if (multiBlock == null || !state.hasProperty(com.github.littleemptydoll.lasthope.block.multiblock.AbstractMultiBlockBlock.PART)) {
            return AssetPaths.getBlockModelPath(definition);
        }

        String suffix = multiBlock.partModel(
                state.getValue(com.github.littleemptydoll.lasthope.block.multiblock.AbstractMultiBlockBlock.PART)
        );

        return suffix == null
                ? AssetPaths.getBlockModelPath(definition)
                : AssetPaths.getBlockModelPath(definition, suffix);
    }

    private static int modelRotation(
            BlockState state,
            BlockDefinition definition
    ) {
        if (definition.placement().rotation() != BlockRotation.HORIZONTAL) {
            return 0;
        }

        return (int) state.getValue(AbstractDecorativeBlock.FACING).toYRot();
    }
}
