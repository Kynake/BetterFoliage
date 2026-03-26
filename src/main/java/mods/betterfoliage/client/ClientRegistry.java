package mods.betterfoliage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.gui.ConfigGuiFactory;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.GrassRenderer;
import mods.octarinecore.client.render.BlockContext;

public class ClientRegistry {

    private static ClientRegistry eventListenerInstance = null;

    private static final int F8_KEYCODE = 66;
    private static KeyBinding openConfigMenu;

    private static BlockRenderer[] blockRenderers;

    private ClientRegistry() {}

    public static void preInit() {
        BetterFoliageMod.log.info("New ClientRegistry preInit()");

        if (eventListenerInstance == null) {
            eventListenerInstance = new ClientRegistry();
        }
    }

    // TODO: consider removing if no needs found
    public static void init() {
        BetterFoliageMod.log.info("New ClientRegistry init()");
    }

    public static void postInit() {
        BetterFoliageMod.log.info("New ClientRegistry postInit()");
        initBlockRenderers();

        openConfigMenu = new KeyBinding("key.betterfoliage.gui", F8_KEYCODE, BetterFoliageMod.MOD_NAME);
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(openConfigMenu);
        FMLCommonHandler.instance()
            .bus()
            .register(eventListenerInstance);
    }

    private static void initBlockRenderers() {
        blockRenderers = new BlockRenderer[] { GrassRenderer.getInstance() };
    }

    public static BlockRenderer getEligibleBlockRenderer(BlockContext ctx) {
        if (blockRenderers != null) {
            for (BlockRenderer blockRenderer : blockRenderers) {
                if (blockRenderer.isEligible(ctx)) return blockRenderer;
            }
        }
        return null;
    }

    /// Event subscription ///

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (openConfigMenu.isPressed()) {
            FMLClientHandler.instance()
                .showGuiScreen(new ConfigGuiFactory.ConfigGuiBetterFoliage(Minecraft.getMinecraft().currentScreen));
        }
    }
}
