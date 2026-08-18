package com.github.littleemptydoll.lasthope.client.screen;

import com.github.littleemptydoll.lasthope.menu.GenericContainerMenu;
import com.github.littleemptydoll.lasthope.registry.definition.settings.InventoryLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GenericContainerScreen
        extends AbstractContainerScreen<GenericContainerMenu> {

    public GenericContainerScreen(
            GenericContainerMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        InventoryLayout layout = menu.getInventoryLayout();

        int containerWidth = layout.columns() * ContainerGuiConstants.SLOT_SIZE;
        int containerHeight = layout.rows() * ContainerGuiConstants.SLOT_SIZE;

        int guiWidth = Math.max(containerWidth, ContainerGuiConstants.PLAYER_INVENTORY_WIDTH);

        this.imageWidth = guiWidth + ContainerGuiConstants.PADDING * 2;
        this.imageHeight =
                ContainerGuiConstants.TITLE_HEIGHT
                        + containerHeight
                        + ContainerGuiConstants.SECTION_GAP
                        + ContainerGuiConstants.PLAYER_INVENTORY_HEIGHT
                        + ContainerGuiConstants.PADDING;

        super.init();
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int MouseY
    ) {
        InventoryLayout layout = menu.getInventoryLayout();

        int containerHeight = layout.rows() * ContainerGuiConstants.SLOT_SIZE;
        int containerTop = ContainerGuiConstants.TITLE_HEIGHT;

        int playerInventoryTop =
                containerTop
                + containerHeight
                + ContainerGuiConstants.SECTION_GAP;

        //Базовый фон GUI
        guiGraphics.fill(
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + playerInventoryTop - ContainerGuiConstants.SECTION_GAP / 2,
                0xFF202020
        );

        guiGraphics.fill(
                leftPos,
                topPos + playerInventoryTop - ContainerGuiConstants.SECTION_GAP / 2,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFF202020
        );
        //Внутренняя область контейнера
        guiGraphics.fill(
                leftPos + ContainerGuiConstants.PADDING,
                topPos + ContainerGuiConstants.TITLE_HEIGHT,
                leftPos + imageWidth - ContainerGuiConstants.PADDING,
                topPos + ContainerGuiConstants.TITLE_HEIGHT
                        + containerHeight,
                0xFF303030
        );
    }
}
