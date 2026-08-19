package com.github.littleemptydoll.lasthope.container;

import com.github.littleemptydoll.lasthope.LastHope;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class ContainerGuiRender {
    private static final ResourceLocation CONTAINER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    LastHope.MODID,
                    "textures/gui/container/container.png"
            );

    private static final ResourceLocation INVENTORY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    LastHope.MODID,
                    "textures/gui/container/inventory.png"
            );

    private ContainerGuiRender() {}

    //Отрисовываем фон контейнера
    public static void renderContainer(
            GuiGraphics guiGraphics,
            ContainerGuiGeometry geometry,
            int leftPos,
            int topPos
    ) {
        int x = leftPos + geometry.containerX();
        int y = topPos + geometry.containerY();

        NineSliceRender.draw(
                guiGraphics,
                CONTAINER_TEXTURE,

                ContainerGuiConstants.CONTAINER_TEXTURE_WIDTH,
                ContainerGuiConstants.CONTAINER_TEXTURE_HEIGHT,

                ContainerGuiConstants.CONTAINER_CENTER_X,
                ContainerGuiConstants.CONTAINER_CENTER_Y,
                ContainerGuiConstants.CONTAINER_CENTER_WIDTH,
                ContainerGuiConstants.CONTAINER_CENTER_HEIGHT,

                ContainerGuiConstants.CONTAINER_BORDER_LEFT,
                ContainerGuiConstants.CONTAINER_BORDER_TOP,
                ContainerGuiConstants.CONTAINER_BORDER_RIGHT,
                ContainerGuiConstants.CONTAINER_BORDER_BOTTOM,

                x,
                y,
                geometry.containerWidth(),
                geometry.containerHeight()
        );
    }

    //Отрисовка слотов контейнера
    public static void renderContainerSlots(
            GuiGraphics guiGraphics,
            ContainerGuiGeometry geometry,
            InventoryLayout layout,
            int leftPos,
            int topPos
    ) {
        int startX = leftPos + geometry.containerX() + ContainerGuiConstants.PADDING;
        int startY = topPos + geometry.containerSlotY();

        int columns = layout.columns();;
        int rows = layout.rows();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int x = startX + column * ContainerGuiConstants.SLOT_SIZE - 1;
                int y = startY + row * ContainerGuiConstants.SLOT_SIZE - 1;

                guiGraphics.blit(
                        CONTAINER_TEXTURE,

                        x,
                        y,

                        ContainerGuiConstants.SLOT_TEXTURE_X,
                        ContainerGuiConstants.SLOT_TEXTURE_Y,

                        ContainerGuiConstants.SLOT_TEXTURE_WIDTH,
                        ContainerGuiConstants.SLOT_TEXTURE_HEIGHT,

                        ContainerGuiConstants.CONTAINER_TEXTURE_WIDTH,
                        ContainerGuiConstants.CONTAINER_TEXTURE_HEIGHT
                );
            }
        }
    }

    //Отрисовываем фон инвентаря игрока
    public static void renderPlayerInventory(
            GuiGraphics guiGraphics,
            ContainerGuiGeometry geometry,
            int leftPos,
            int topPos
    ) {
        int x = leftPos + geometry.playerInventoryX();
        int y = topPos + geometry.playerInventoryY();

        guiGraphics.blit(
                INVENTORY_TEXTURE,
                x,
                y,
                0,
                0,
                ContainerGuiConstants.PLAYER_INVENTORY_WIDTH,
                ContainerGuiConstants.PLAYER_INVENTORY_HEIGHT,
                ContainerGuiConstants.PLAYER_INVENTORY_WIDTH,
                ContainerGuiConstants.PLAYER_INVENTORY_HEIGHT
        );
    }
}
