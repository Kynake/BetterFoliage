package mods.betterfoliage.client.integration;

import ic2.core.block.BlockRubWood;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.BlockFilterSet;

public class IC2Integration {

    public static final BlockFilterSet rubberWoodFilter;

    static {
        if (Mod.IC2.isLoaded()) {
            rubberWoodFilter = BlockFilterSet.createSingleClassFilter(BlockRubWood.class);
        } else {
            BetterFoliageMod.log.error("Tried to access IC2Integration class without {} present!", Mod.IC2.modName);
            rubberWoodFilter = null;
        }
    }
}
