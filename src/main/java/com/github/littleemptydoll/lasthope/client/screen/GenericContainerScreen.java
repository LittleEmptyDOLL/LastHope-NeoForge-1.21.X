package com.github.littleemptydoll.lasthope.client.screen;

import com.github.littleemptydoll.lasthope.container.ContainerGuiConstants;
import com.github.littleemptydoll.lasthope.container.ContainerGuiGeometry;
import com.github.littleemptydoll.lasthope.menu.GenericContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GenericContainerScreen
        extends AbstractContainerScreen<GenericContainerMenu> {

    private ContainerGuiGeometry geometry;

    public GenericContainerScreen(
            GenericContainerMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        geometry = new ContainerGuiGeometry(
                menu.getInventoryLayout()
        );

        this.imageWidth = geometry.imageWidth();
        this.imageHeight = geometry.imageHeight();

        super.init();
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int MouseY
    ) {
        int containerX = leftPos + geometry.containerX();
        int containerY = topPos + geometry.containerY();

        int playerX = leftPos + geometry.playerInventoryX();
        int playerY = topPos + geometry.playerInventoryY();

        //Фон контейнера
        guiGraphics.fill(
                containerX,
                containerY,
                containerX + geometry.containerWidth(),
                containerY + geometry.containerHeight(),
                0xFF202020
        );
        //Фон инвентаря игрока
        guiGraphics.fill(
                playerX,
                playerY,
                playerX + geometry.playerInventoryWidth(),
                playerY + geometry.playerInventoryHeight(),
                0xFF252525
        );
    }

    private String trimTitle(Component component, int maxWidth) {
        String text = component.getString();

        if (this.font.width(text) <= maxWidth) {
            return text;
        }

        String suffix = "...";

        int availableWidth = maxWidth - this.font.width(suffix);

        if (availableWidth <= 0) {
            return suffix;
        }

        return this.font.plainSubstrByWidth(
                text,
                availableWidth
        ) + suffix;
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.render(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        int maxTitleWidth = geometry.containerWidth() - ContainerGuiConstants.PADDING / 2;

        String containerTitle =
                trimTitle(
                        this.title,
                        maxTitleWidth
                );

        int containerTitleWidth = this.font.width(containerTitle);

        int containerTitleX = (this.imageWidth - containerTitleWidth) / 2;

        int containerTitleY = (ContainerGuiConstants.TITLE_HEIGHT - this.font.lineHeight) / 2;

        guiGraphics.drawString(
                this.font,
                containerTitle,
                containerTitleX,
                containerTitleY,
                0x404040,
                false
        );

        int inventoryTitleMaxWidth = geometry.playerInventoryWidth() - ContainerGuiConstants.PADDING * 2;

        String playerTitle =
                trimTitle(
                        this.playerInventoryTitle,
                        inventoryTitleMaxWidth
                );

        int playerTitleWidth = this.font.width(playerTitle);

        int playerTitleX = (this.imageWidth - playerTitleWidth) / 2;

        int playerTitleY =
                geometry.playerInventoryY()
                        + (ContainerGuiConstants.TITLE_HEIGHT - this.font.lineHeight) / 2;

        guiGraphics.drawString(
                this.font,
                playerTitle,
                playerTitleX,
                playerTitleY,
                0x404040,
                false
        );
    }
}
