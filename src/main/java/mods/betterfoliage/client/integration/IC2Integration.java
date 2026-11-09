package mods.betterfoliage.client.integration;

import ic2.core.block.BlockRubWood;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.blockfilters.BlockFilter;

public class IC2Integration {

    public static final BlockFilter rubberWoodFilter;

    static {
        BetterFoliageMod.log.info("Initializing mod integration: {}", Mod.IC2.modName);
        if (Mod.IC2.isLoaded()) {
            rubberWoodFilter = BlockFilter.createSingleClassFilter(BlockRubWood.class);
        } else {
            BetterFoliageMod.log.error("Tried to access IC2Integration class without {} present!", Mod.IC2.modName);
            rubberWoodFilter = null;
        }
    }
}
