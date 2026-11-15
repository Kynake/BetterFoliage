package mods.betterfoliage.client.resource;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class StitchListener {

    // Some SpriteSets (Leaf particles, for example) are created while the stitcher event is already running,
    // so we register them manually instead of relying on event subscription.
    protected final boolean selfRegister;

    protected StitchListener(boolean selfRegister) {
        this.selfRegister = selfRegister;
        if (!selfRegister) return;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public final void handleStitchEvent(TextureStitchEvent.Pre event) {
        if (selfRegister) {
            onSpriteStitch(event.map);
        }
    }

    public final void registerManually(TextureMap atlas) {
        if (!selfRegister) {
            onSpriteStitch(atlas);
        }
    }

    protected abstract void onSpriteStitch(TextureMap atlas);
}
