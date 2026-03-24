package mods.betterfoliage.client.render;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

// TODO: Add other SubscribeEvents that need to be handled (either here or somewhere else)
public abstract class ResourceHandler {

    protected ResourceHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        onTextureStitch(event);
    }

    public abstract void onTextureStitch(TextureStitchEvent.Pre event);
}
