package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.datagen.DataGenerators;
import net.neoforged.bus.api.IEventBus;

public class ModRegistries {
    public static void register(IEventBus eventBus) {
        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.register(eventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.register(eventBus);
        // Register the Deferred Register to the mod event bus so block entities get registered
        ModBlockEntities.register(eventBus);
        //
        MenuRegistry.register(eventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        //ModCreativeTabs.register(eventBus);
        // Регистрируем генераторы ресурсов мода
        DataGenerators.register(eventBus);
    }
}
