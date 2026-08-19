package com.github.littleemptydoll.lasthope.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class NineSliceRender {
    private NineSliceRender() {}

    public static void draw(
            GuiGraphics guiGraphics,
            ResourceLocation texture,

            int textureWidth,
            int textureHeight,

            //Центральная область исходной текстуры
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,

            //Размер границ
            int left,
            int top,
            int right,
            int bottom,

            //Область отрисовки
            int x,
            int y,
            int width,
            int height
    ) {
        int centerWidth = width - left - right;
        int centerHeight = height - top - bottom;

        if (centerWidth <= 0 || centerHeight <= 0) {
            throw new IllegalArgumentException(
                    "Target size is to small for the nine-slice borders."
            );
        }

        //Исходный координаты верхней границы
        int topSourceX = sourceX;
        int topSourceY = sourceY - top;

        //Исходный координаты левой границы
        int leftSourceX = sourceX - left;
        int leftSourceY = sourceY;

        //Исходный координаты правой границы
        int rightSourceX = sourceX + sourceWidth;
        int rightSourceY = sourceY;

        //Исходный координаты нижней границы
        int bottomSourceX = sourceX;
        int bottomSourceY = sourceY + sourceHeight;

        //Верхний левый угол
        blit(
                guiGraphics,
                texture,
                x,
                y,
                leftSourceX,
                topSourceY,
                left,
                top,
                textureWidth,
                textureHeight
        );

        //Верхний правый угол
        blit(
                guiGraphics,
                texture,
                x + width - right,
                y,
                rightSourceX,
                topSourceY,
                right,
                top,
                textureWidth,
                textureHeight
        );

        //Нижний левый угол
        blit(
                guiGraphics,
                texture,
                x,
                y + height - bottom,
                leftSourceX,
                bottomSourceY,
                left,
                bottom,
                textureWidth,
                textureHeight
        );

        //Нижний правый угол
        blit(
                guiGraphics,
                texture,
                x + width - right,
                y + height - bottom,
                rightSourceX,
                bottomSourceY,
                right,
                bottom,
                textureWidth,
                textureHeight
        );

        //Верхняя граница
        tileHorizontal(
                guiGraphics,
                texture,
                x + left,
                y,
                centerWidth,
                top,
                topSourceX,
                topSourceY,
                sourceWidth,
                top,
                textureWidth,
                textureHeight
        );

        //Нижняя граница
        tileHorizontal(
                guiGraphics,
                texture,
                x + left,
                y + height - bottom,
                centerWidth,
                bottom,
                bottomSourceX,
                bottomSourceY,
                sourceWidth,
                bottom,
                textureWidth,
                textureHeight
        );

        //Левая граница
        tileVertical(
                guiGraphics,
                texture,
                x,
                y + top,
                left,
                centerHeight,
                leftSourceX,
                leftSourceY,
                left,
                sourceHeight,
                textureWidth,
                textureHeight
        );

        //Правая граница
        tileVertical(
                guiGraphics,
                texture,
                x + width - right,
                y + top,
                right,
                centerHeight,
                rightSourceX,
                rightSourceY,
                right,
                sourceHeight,
                textureWidth,
                textureHeight
        );

        //Центральная область
        tile(
                guiGraphics,
                texture,
                x + left,
                y + top,
                centerWidth,
                centerHeight,
                sourceX,
                sourceY,
                sourceWidth,
                sourceHeight,
                textureWidth,
                textureHeight
        );
    }

    private static void blit(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int x,
            int y,
            int sourceX,
            int sourceY,
            int width,
            int height,
            int textureWidth,
            int textureHeight
    ) {
        guiGraphics.blit(
                texture,
                x,
                y,
                sourceX,
                sourceY,
                width,
                height,
                textureWidth,
                textureHeight
        );
    }

    private static void tileHorizontal(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,
            int textureWidth,
            int textureHeight
    ) {
        int currentX = x;
        int remaining = width;

        while (remaining > 0) {
            int drawWidth = Math.min(sourceWidth, remaining);

            blit(
                    guiGraphics,
                    texture,
                    currentX,
                    y,
                    sourceX,
                    sourceY,
                    drawWidth,
                    sourceHeight,
                    textureWidth,
                    textureHeight
            );

            currentX += drawWidth;
            remaining -= drawWidth;
        }
    }

    private static void tileVertical(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,
            int textureWidth,
            int textureHeight
    ) {
        int currentY = y;
        int remaining = height;

        while (remaining > 0) {
            int drawHeight = Math.min(sourceHeight, remaining);

            blit(
                    guiGraphics,
                    texture,
                    x,
                    currentY,
                    sourceX,
                    sourceY,
                    sourceWidth,
                    drawHeight,
                    textureWidth,
                    textureHeight
            );

            currentY += drawHeight;
            remaining -= drawHeight;
        }
    }

    private static void tile(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int sourceX,
            int sourceY,
            int sourceWidth,
            int sourceHeight,
            int textureWidth,
            int textureHeight
    ) {
        int currentY = y;
        int remainingHeight = height;

        while (remainingHeight > 0) {
            int drawHeight = Math.min(sourceHeight, remainingHeight);

            int currentX = x;
            int remainingWidth = width;

            while (remainingWidth > 0) {
                int drawWidth = Math.min(sourceWidth, remainingWidth);

                blit(
                        guiGraphics,
                        texture,
                        currentX,
                        currentY,
                        sourceX,
                        sourceY,
                        drawWidth,
                        drawHeight,
                        textureWidth,
                        textureHeight
                );

                currentX += drawWidth;
                remainingWidth -= drawWidth;
            }

            currentY += drawHeight;
            remainingHeight -= drawHeight;
        }
    }
}
