package com.github.littleemptydoll.lasthope.registry;

import net.neoforged.bus.api.IEventBus;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static void register(IEventBus bus) {
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
    }
}
