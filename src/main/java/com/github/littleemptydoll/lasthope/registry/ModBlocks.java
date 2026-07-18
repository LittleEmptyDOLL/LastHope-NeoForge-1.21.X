package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.block.ModBlockProperties;
import com.github.littleemptydoll.lasthope.block.decoration.CardboardBoxBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    // Реестр всех блоков нашего мода.
    // Всё, что будет зарегистрированно здесь, автоматически получит id
    // вида "lasthope:<имя_блока>"
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LastHope.MODID);

    private ModBlocks() {
    }

    // Вспомогательный метод для регистрации блока
    public static <T extends Block> DeferredBlock<T> registerBlock(
            String name,
            Supplier<T> supplier )
    {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.registerBlockItem(name, block);

        return block;
    }

    public static void register(IEventBus bus){
        BLOCKS.register(bus);
    }

    public static final DeferredBlock<CardboardBoxBlock> CARDBOARD_BOX =
            registerBlock(
                    "cardboard_box",
                    () -> new CardboardBoxBlock(
                            ModBlockProperties.cardboard()
                    ));
}
