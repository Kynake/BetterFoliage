package mods.betterfoliage.client.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.blockfilters.BlockFilter;
import mods.natura.blocks.crops.BerryBush;
import mods.natura.blocks.crops.NetherBerryBush;
import tconstruct.world.blocks.OreberryBush;

public class BerryBushIntegration {

    private static final BlockFilter berryBush;

    static {
        BetterFoliageMod.log.info("Attempting mod integration for Berry Bushes");

        List<Class<?>> filterClasses = new ArrayList<>(3);

        if (Mod.NATURA.isLoaded()) {
            BetterFoliageMod.log.info("Initializing berry bush integration for {}", Mod.NATURA.modName);
            filterClasses.add(BerryBush.class);
            filterClasses.add(NetherBerryBush.class);
        }

        if (Mod.TCON.isLoaded()) {
            BetterFoliageMod.log.info("Initializing berry bush integration for {}", Mod.TCON.modName);
            filterClasses.add(OreberryBush.class);
        }

        if (filterClasses.isEmpty()) {
            BetterFoliageMod.log.error(
                "Error adding berry bush integration! Neither {} nor {} are present!",
                Mod.NATURA.modName,
                Mod.TCON.modName);
            berryBush = null;
        } else {
            berryBush = BlockFilter.createClassOnlyFilter(filterClasses.toArray(new Class<?>[0]));
        }
    }

    public static float applyScaleMultiplier(float baseScale, Block block) {
        if (!berryBush.matchesNoMeta(block)) {
            return baseScale;
        }

        return baseScale * (float) (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY());
    }
}
