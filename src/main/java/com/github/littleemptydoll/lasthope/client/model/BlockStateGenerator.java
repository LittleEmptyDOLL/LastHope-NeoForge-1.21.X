package com.github.littleemptydoll.lasthope.client.model;

import com.github.littleemptydoll.lasthope.block.BlockRotation;
import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
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
        var model = provider.models().getExistingFile(
                provider.modLoc(AssetPaths.getBlockModelPath(definition))
        );

        provider.getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationY(modelRotation(state, definition))
                        .build());
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
