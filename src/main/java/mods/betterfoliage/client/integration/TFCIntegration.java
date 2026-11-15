package mods.betterfoliage.client.integration;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.bioxx.tfc.Blocks.Flora.BlockLogHoriz;
import com.bioxx.tfc.Blocks.Flora.BlockLogNatural;
import com.bioxx.tfc.Blocks.Flora.BlockLogVert;
import com.bioxx.tfc.Blocks.Terrain.BlockGrass;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.config.blockfilters.BlockFilter;

public class TFCIntegration {

    public static final BlockFilter grass;
    public static final BlockFilter horizontalLogs;
    public static final BlockFilter verticalLogs;

    static {
        BetterFoliageMod.log.info("Initializing mod integration: {}", Mod.TFC.modName);
        if (Mod.TFC.isLoaded()) {
            grass = BlockFilter.createClassOnlyFilter(BlockGrass.class);
            horizontalLogs = BlockFilter.createClassOnlyFilter(BlockLogHoriz.class);
            verticalLogs = BlockFilter.createClassOnlyFilter(BlockLogVert.class, BlockLogNatural.class);
        } else {
            BetterFoliageMod.log.error("Tried to access TFCIntegration class without {} present!", Mod.TFC.modName);
            grass = null;
            horizontalLogs = null;
            verticalLogs = null;
        }
    }

    public static boolean isLogFacingNorthSouth(int logMeta) {
        return (logMeta & 8) == 0;
    }

    // TFC tries to provide a pre-rotated sprite for the log's sides.
    // We don't want it, as we already rotate the sprite ourselves during round log rendering
    public static IIcon unrotateHorizontalLogSprites(int side, int meta, int index, IIcon[] sideSprites,
        IIcon[] faceSprites) {
        if (isLogFacingNorthSouth(meta)) {
            return side == ForgeDirection.NORTH.ordinal() || side == ForgeDirection.SOUTH.ordinal() ? faceSprites[index]
                : sideSprites[index];

        }

        return side == ForgeDirection.WEST.ordinal() || side == ForgeDirection.EAST.ordinal() ? faceSprites[index]
            : sideSprites[index];
    }
}
