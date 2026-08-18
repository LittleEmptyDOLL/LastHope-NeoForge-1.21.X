package com.github.littleemptydoll.lasthope.registry;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.menu.GenericContainerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuRegistry {
    private MenuRegistry() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    Registries.MENU,
                    LastHope.MODID
            );

    public static final DeferredHolder<
            MenuType<?>,
            MenuType<GenericContainerMenu>
            > GENERIC_CONTAINER = MENUS.register(
                    "generic_container",
                    () -> IMenuTypeExtension.create(
                            GenericContainerMenu::new
                    )
            );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
