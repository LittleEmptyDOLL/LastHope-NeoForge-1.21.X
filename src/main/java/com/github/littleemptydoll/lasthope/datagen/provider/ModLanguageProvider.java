package com.github.littleemptydoll.lasthope.datagen.provider;

import com.github.littleemptydoll.lasthope.datagen.DatagenConstants;
import com.github.littleemptydoll.lasthope.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, DatagenConstants.MOD_ID, DatagenConstants.EN_US);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.CARDBOARD_BOX.get(), "Cardboard Box");
    }
}
