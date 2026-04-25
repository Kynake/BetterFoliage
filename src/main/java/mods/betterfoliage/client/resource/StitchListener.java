package mods.betterfoliage.client.resource;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.betterfoliage.BetterFoliageMod;

public abstract class StitchListener {

    protected StitchListener() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public final void handleStitchEvent(TextureStitchEvent.Pre event) {
        onSpriteStitch(event);
    }

    protected static String convertToSpriteName(ResourceLocation location) {
        return convertToSpriteName(location, location.getResourceDomain());
    }

    protected static String convertToSpriteName(ResourceLocation location, String domain) {
        String name = location.getResourcePath();
        int startIndex = name.lastIndexOf('/') + 1;
        int endIndex = name.lastIndexOf('.');

        if (startIndex <= 0 || startIndex > endIndex) {
            BetterFoliageMod.log.error("Invalid resource location: {}", location);
            return null;
        }

        return domain + ":" + name.substring(startIndex, endIndex);
    }

    protected abstract void onSpriteStitch(TextureStitchEvent.Pre event);
}
