package mods.betterfoliage.client.resource;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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

    protected abstract void onSpriteStitch(TextureStitchEvent.Pre event);
}
