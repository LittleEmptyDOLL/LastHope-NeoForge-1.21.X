package com.github.littleemptydoll.lasthope.registry.definition;

import com.github.littleemptydoll.lasthope.client.model.ModelType;
import com.github.littleemptydoll.lasthope.registry.definition.settings.ContainerSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;

public class BlockDefinition {
    private BlockDefinition(
            DeferredBlock<? extends Block> block,
            ModelType modelType,
            AssetFolder assetFolder,
            Supplier<BlockBehaviour.Properties> properties
    ) {
        this.block = block;
        this.modelType = modelType;
        this.assetFolder = assetFolder;
        this.properties = properties;
    }

    public static BlockDefinition of(
            DeferredBlock<? extends Block> block,
            ModelType modelType,
            AssetFolder assetFolder,
            Supplier<BlockBehaviour.Properties> properties
    ) {
        return new BlockDefinition(
                block,
                modelType,
                assetFolder,
                properties
        );
    }

    private final DeferredBlock<? extends Block> block;
    // Поведение
    private final ModelType modelType;
    // Ресурсы
    private final AssetFolder assetFolder;
    // Параметры
    private final Supplier<BlockBehaviour.Properties> properties;
    // Контейнер
    private ContainerSettings containerSettings;

    // Добавить контейнер
    public BlockDefinition withContainer(ContainerSettings settings) {
        this.containerSettings = settings;
        return this;
    }

    // Getter's
    // Block
    public DeferredBlock<? extends Block> block() {
        return block;
    }
    // Поведение
    public ModelType modelType() {
        return modelType;
    }
    // Ресурсы
    public AssetFolder assetFolder() {
        return assetFolder;
    }
    // Параметры
    public Supplier<BlockBehaviour.Properties> properties() {
        return properties;
    }
    // Контейнер
    public ContainerSettings containerSettings() {
        return containerSettings;
    }
}
