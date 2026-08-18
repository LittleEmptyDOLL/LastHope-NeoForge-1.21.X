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

        calculateDimensions();
    }

    private void calculateDimensions() {
        InventoryLayout layout = menu.getInventoryLayout();

        int containerWidth = layout.columns() * ContainerGuiConstants.SLOT_SIZE;
        int containerHeight = layout.rows() * ContainerGuiConstants.SLOT_SIZE;

        int guiWidth = Math.max(containerWidth, ContainerGuiConstants.PLAYER_INVENTORY_WIDTH);

        int playerAreaHeight =
                (ContainerGuiConstants.PLAYER_INVENTORY_ROWS + ContainerGuiConstants.HOTBAR_ROWS)
                        * ContainerGuiConstants.SLOT_SIZE;

        this.imageWidth = guiWidth + ContainerGuiConstants.PADDING * 2;
        this.imageHeight =
                ContainerGuiConstants.TITLE_HEIGHT
                        + containerHeight
                        + ContainerGuiConstants.SECTION_GAP
                        + playerAreaHeight
                        + ContainerGuiConstants.PADDING;
    }

    @Override
    protected void init() {
        calculateDimensions();

        this.leftPos = (this.width = this.imageWidth) / 2;
        this.topPos = (this.height = this.imageHeight) / 2;

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
                topPos + playerInventoryTop - ContainerGuiConstants.SECTION_GAP,
                0xFF202020
        );

        guiGraphics.fill(
                leftPos,
                topPos +playerInventoryTop,
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
                        + menu.getInventoryLayout().rows() * ContainerGuiConstants.SLOT_SIZE,
                0xFF303030
        );
    }
}
