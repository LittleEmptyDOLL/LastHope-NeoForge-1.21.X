package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.block.decoration.CardboardBoxBlock;
import com.github.littleemptydoll.lasthope.block.decoration.TestBlock;
import com.github.littleemptydoll.lasthope.client.model.ModelType;
import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import com.github.littleemptydoll.lasthope.registry.definition.AssetFolder;
import net.neoforged.bus.api.IEventBus;

import java.util.List;

public class ModBlocks {
    // Реестр всех блоков нашего мода.
    // Всё, что будет зарегистрированно здесь, автоматически получит id
    // вида "lasthope:<имя_блока>"
    private ModBlocks() {}

    public static void register(IEventBus bus){
        BlockRegistry.register(bus);
    }

    public static List<BlockDefinition> getBlockDefinitions() {
        return BlockRegistry.getBlockDefinitions();
    }

    public static final BlockDefinition TEST_BLOCK = BlockRegistry.register(
            "test_block",
            TestBlock::new,
            ModelType.SIMPLE,
            AssetFolder.DECORATION,
            ModBlockProperties::decoration
    );

    public static final BlockDefinition CARDBOARD_BOX = BlockRegistry.register(
            "cardboard_box",
            CardboardBoxBlock::new,
            ModelType.SIMPLE,
            AssetFolder.DECORATION,
            ModBlockProperties::softDecoration
    );
}
