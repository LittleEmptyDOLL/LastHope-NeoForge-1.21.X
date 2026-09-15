package com.github.littleemptydoll.lasthope.registry.definition;

import com.github.littleemptydoll.lasthope.block.BlockPlacement;
import com.github.littleemptydoll.lasthope.block.BlockShape;
import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.client.model.ModelType;
import com.github.littleemptydoll.lasthope.registry.category.BlockCategory;
import com.github.littleemptydoll.lasthope.registry.definition.settings.ContainerSettings;
import com.github.littleemptydoll.lasthope.registry.tag.BlockTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collections;
import java.util.EnumSet;
import java.util.function.Supplier;

public final class BlockDefinition {
    private DeferredBlock<? extends Block> block;
    private final BlockCategory category;
    private final ModelType modelType;
    private final AssetFolder assetFolder;
    private final Supplier<BlockBehaviour.Properties> properties;
    private final EnumSet<BlockTag> tags;
    private final ContainerSettings containerSettings;
    private final BlockPlacement placement;
    private final BlockShape shape;
    private final MultiBlockDefinition multiBlock;

    private BlockDefinition(Builder builder) {
        this.category = builder.category;
        this.modelType = builder.modelType;
        this.assetFolder = builder.assetFolder;
        this.properties = builder.properties;
        this.tags = EnumSet.copyOf(builder.tags);
        this.containerSettings = builder.containerSettings;
        this.placement = builder.placement;
        this.shape = builder.shape;
        this.multiBlock = builder.multiBlock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BlockCategory category;
        private ModelType modelType = ModelType.SIMPLE;
        private AssetFolder assetFolder = AssetFolder.DECORATION;
        private Supplier<BlockBehaviour.Properties> properties = ModBlockProperties::decoration;
        private final EnumSet<BlockTag> tags = EnumSet.noneOf(BlockTag.class);
        private ContainerSettings containerSettings;
        private BlockPlacement placement = BlockPlacement.none();
        private BlockShape shape;
        private MultiBlockDefinition multiBlock;

        public BlockDefinition build() {
            if (category == null) {
                throw new IllegalStateException("Block category is required.");
            }
            if (multiBlock != null && !(multiBlock.parts() > 1)) {
                throw new IllegalStateException("Multiblock definition must contain more than one part.");
            }
            return new BlockDefinition(this);
        }

        public Builder category(BlockCategory category) {
            this.category = category;
            return this;
        }

        public Builder model(ModelType modelType) {
            this.modelType = modelType;
            return this;
        }

        public Builder assetFolder(AssetFolder assetFolder) {
            this.assetFolder = assetFolder;
            return this;
        }

        public Builder properties(Supplier<BlockBehaviour.Properties> properties) {
            this.properties = properties;
            return this;
        }

        public Builder container(ContainerSettings containerSettings) {
            this.containerSettings = containerSettings;
            return this;
        }

        public Builder placement(BlockPlacement placement) {
            this.placement = placement;
            return this;
        }

        public Builder shape(BlockShape shape) {
            this.shape = shape;
            return this;
        }

        public Builder multiBlock(MultiBlockDefinition multiBlock) {
            this.multiBlock = multiBlock;
            return this;
        }

        public Builder tag(BlockTag tag) {
            tags.add(tag);
            return this;
        }

        public Builder tags(BlockTag... tags) {
            Collections.addAll(this.tags, tags);
            return this;
        }
    }

    public void setBlock(DeferredBlock<? extends Block> block) {
        this.block = block;
    }

    public DeferredBlock<? extends Block> block() {
        return block;
    }

    public BlockCategory category() {
        return category;
    }

    public ModelType modelType() {
        return modelType;
    }

    public AssetFolder assetFolder() {
        return assetFolder;
    }

    public Supplier<BlockBehaviour.Properties> properties() {
        return properties;
    }

    public ContainerSettings containerSettings() {
        return containerSettings;
    }

    public BlockPlacement placement() {
        return placement;
    }

    public BlockShape shape() {
        return shape;
    }

    public MultiBlockDefinition multiBlock() {
        return multiBlock;
    }

    public EnumSet<BlockTag> tags() {
        return EnumSet.copyOf(tags);
    }

    public boolean hasTag(BlockTag tag) {
        return tags.contains(tag);
    }
}
