package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.block.BlockPlacement;
import com.github.littleemptydoll.lasthope.block.BlockShape;
import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.block.decoration.CardboardBoxBlock;
import com.github.littleemptydoll.lasthope.block.decoration.TestBlock;
import com.github.littleemptydoll.lasthope.block.decoration.TestStorageBlock;
import com.github.littleemptydoll.lasthope.registry.category.BlockCategory;
import com.github.littleemptydoll.lasthope.registry.definition.AssetFolder;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinitionRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.BlockRegistry;
import com.github.littleemptydoll.lasthope.registry.definition.ContainerSound;
import com.github.littleemptydoll.lasthope.registry.definition.settings.ContainerSettings;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayouts;
import com.github.littleemptydoll.lasthope.registry.tag.BlockTag;
import net.neoforged.bus.api.IEventBus;

import java.util.List;

public class ModBlocks {
    private ModBlocks() {}

    public static void register(IEventBus bus) {
        BlockRegistry.register(bus);
    }

    public static List<BlockDefinition> getBlockDefinitions() {
        return BlockRegistry.getBlockDefinitions();
    }

    public static final BlockDefinition TEST_BLOCK = BlockRegistry.register(
            "test_block",
            TestBlock::new,
            BlockDefinition.builder()
                    .category(BlockCategory.DECORATION)
                    .assetFolder(AssetFolder.DECORATION)
                    .properties(ModBlockProperties::decoration)
                    .build()
    );

    public static final BlockDefinition TEST_STORAGE_BLOCK = BlockRegistry.register(
            "test_storage_block",
            TestStorageBlock::new,
            BlockDefinition.builder()
                    .category(BlockCategory.STORAGE)
                    .assetFolder(AssetFolder.DECORATION)
                    .properties(ModBlockProperties::metalDecoration)
                    .placement(BlockPlacement.horizontal())
                    .shape(BlockShape.cube())
                    .container(
                            ContainerSettings.of(
                                    InventoryLayouts.SOME_BIG_BOX,
                                    ContainerSound.METAL
                            )
                    )
                    .tags(
                            BlockTag.FLAMMABLE,
                            BlockTag.BREAKABLE,
                            BlockTag.LOOTABLE
                    )
                    .build()
    );

    public static final BlockDefinition CARDBOARD_BOX = BlockRegistry.register(
            "cardboard_box",
            CardboardBoxBlock::new,
            BlockDefinition.builder()
                    .category(BlockCategory.STORAGE)
                    .assetFolder(AssetFolder.DECORATION)
                    .properties(ModBlockProperties::softDecoration)
                    .placement(BlockPlacement.horizontal())
                    .shape(BlockShape.box(3, 0, 1, 13, 8, 15))
                    .container(
                            ContainerSettings.of(
                                    InventoryLayouts.SMALL_BOX,
                                    ContainerSound.BOX
                            )
                    )
                    .tags(
                            BlockTag.FLAMMABLE,
                            BlockTag.BREAKABLE,
                            BlockTag.LOOTABLE
                    )
                    .build()
    );
}
