package com.github.littleemptydoll.lasthope.datagen.provider;

import com.github.littleemptydoll.lasthope.client.model.BlockStateGenerator;
import com.github.littleemptydoll.lasthope.datagen.DatagenConstants;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DatagenConstants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (BlockDefinition definition : BlockDefinitionRegistry.getDefinitions()) {
            BlockStateGenerator.register(this, definition);
        }
    }
}
