package com.github.littleemptydoll.lasthope.registry.definition;

import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.client.model.ModelType;
import com.github.littleemptydoll.lasthope.registry.category.BlockCategory;
import com.github.littleemptydoll.lasthope.registry.definition.settings.ContainerSettings;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayouts;
import com.github.littleemptydoll.lasthope.registry.tag.BlockTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collections;
import java.util.EnumSet;
import java.util.function.Supplier;

public final class BlockDefinition {
    // Block
    private DeferredBlock<? extends Block> block;
    // Категория блока
    private final BlockCategory category;
    // Поведение
    private final ModelType modelType;
    // Ресурсы
    private final AssetFolder assetFolder;
    // Параметры
    private final Supplier<BlockBehaviour.Properties> properties;
    // Tags
    private final EnumSet<BlockTag> tags;
    // Контейнер
    private final ContainerSettings containerSettings;

    //
    private BlockDefinition(Builder builder) {
        this.category = builder.category;
        this.modelType = builder.modelType;
        this.assetFolder = builder.assetFolder;
        this.properties = builder.properties;
        this.tags = EnumSet.copyOf(builder.tags);
        this.containerSettings = builder.containerSettings;
    }
    //
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        //
        private BlockCategory category;
        //
        private ModelType modelType = ModelType.SIMPLE;
        //
        private AssetFolder assetFolder = AssetFolder.DECORATION;
        //
        private Supplier<BlockBehaviour.Properties> properties = ModBlockProperties::decoration;
        //
        private final EnumSet<BlockTag> tags = EnumSet.noneOf(BlockTag.class);
        //
        private ContainerSettings containerSettings;

        //
        public BlockDefinition build() {
            if (category == null) {
                throw new IllegalStateException("Block category is required.");
            }
            return new BlockDefinition(this);
        }
        //Присвоение категории
        public Builder category(BlockCategory category) {
            this.category = category;
            return this;
        }
        //Присвоение типа модели
        public Builder model(ModelType modelType) {
            this.modelType = modelType;
            return this;
        }
        //Присвоение папки ресурсов
        public Builder assetFolder(AssetFolder assetFolder) {
            this.assetFolder = assetFolder;
            return this;
        }
        //Присвоение параметров
        public Builder properties(Supplier<BlockBehaviour.Properties> properties) {
            this.properties = properties;
            return this;
        }
        //Присвоение контейнера
        public Builder container(ContainerSettings containerSettings) {
            this.containerSettings = containerSettings;
            return this;
        }
        //Присвоение тега
        public Builder tag(BlockTag tag) {
            tags.add(tag);
            return this;
        }
        //Присвоение тегов
        public Builder tags(BlockTag... tags) {
            Collections.addAll(this.tags, tags);
            return this;
        }
    }

    // Вызывается только BlockRegistry после регистрации DeferredBlock
    public void setBlock(DeferredBlock<? extends Block> block) {
        this.block = block;
    }

    // Getter's
    // Block
    public DeferredBlock<? extends Block> block() {
        return block;
    }
    // Категория
    public BlockCategory category() {
        return category;
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
    // Теги
    public EnumSet<BlockTag> tags() {
        return EnumSet.copyOf(tags);
    }
    // Есть ли теги
    public boolean hasTag(BlockTag tag) {
        return tags.contains(tag);
    }
}
