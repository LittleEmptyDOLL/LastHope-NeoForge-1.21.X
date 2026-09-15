package com.github.littleemptydoll.lasthope.client.model;

import com.github.littleemptydoll.lasthope.block.BlockRotation;
import com.github.littleemptydoll.lasthope.block.decoration.AbstractDecorativeBlock;
import com.github.littleemptydoll.lasthope.block.multiblock.AbstractMultiBlockBlock;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.MultiBlockDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;

import java.util.Map;

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
        Map<Integer, ModelFile> generatedParts = multiBlock == null
                ? Map.of()
                : MultiBlockModelGenerator.generate(provider, definition);

        provider.getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(getModel(provider, definition, multiBlock, generatedParts, state))
                        .rotationY(modelRotation(state, definition))
                        .build());
    }

    private static ModelFile getModel(
            BlockStateProvider provider,
            BlockDefinition definition,
            MultiBlockDefinition multiBlock,
            Map<Integer, ModelFile> generatedParts,
            BlockState state
    ) {
        if (multiBlock != null && state.hasProperty(AbstractMultiBlockBlock.PART)) {
            int part = state.getValue(AbstractMultiBlockBlock.PART);
            ModelFile model = generatedParts.get(part);
            if (model == null) {
                throw new IllegalStateException(
                        "No generated model for multiblock part " + part +
                                " of " + definition.block().getId()
                );
            }
            return model;
        }

        return provider.models().getExistingFile(
                provider.modLoc(AssetPaths.getBlockModelPath(definition))
        );
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
