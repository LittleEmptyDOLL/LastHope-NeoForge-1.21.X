package com.github.littleemptydoll.lasthope.client.model;

import com.github.littleemptydoll.lasthope.registry.definition.BlockDefinition;
import net.minecraft.resources.ResourceLocation;

public class AssetPaths {
    private AssetPaths() {}

    public static String getFilePath(BlockDefinition definition) {
        return definition.assetFolder().folder()
                + "/"
                + definition.block().getId().getPath();
    }

    public static String getBlockModelPath(BlockDefinition definition) {
        return "block/" + getFilePath(definition);
    }

    public static String getBlockModelPath(BlockDefinition definition, String modelSuffix) {
        return getBlockModelPath(definition) + "_" + modelSuffix;
    }

    public static String getBlockTexturePath(BlockDefinition definition) {
        return "texture/" + getFilePath(definition);
    }

    public static String getItemModelPath(BlockDefinition definition) {
        return "item/" + getFilePath(definition);
    }

    public static ResourceLocation getTexture(BlockDefinition definition) {
        return ResourceLocation.parse("lasthope/texture/block/" + getFilePath(definition));
    }
}
