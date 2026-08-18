package com.github.littleemptydoll.lasthope.client.screen;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.registry.MenuRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(
        modid = LastHope.MODID,
        value = Dist.CLIENT
)
public final class ModScreens {
    private ModScreens() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                MenuRegistry.GENERIC_CONTAINER.get(),
                GenericContainerScreen::new
        );
    }
}
