package mods.betterfoliage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.registries.GrassRegistry;
import mods.betterfoliage.client.registries.LeafRegistry;
import mods.betterfoliage.client.render.BlockRenderer;
import mods.betterfoliage.client.render.features.AlgaeRenderer;
import mods.betterfoliage.client.render.features.CactusRenderer;
import mods.betterfoliage.client.render.features.CoralRenderer;
import mods.betterfoliage.client.render.features.DirtGrassLogRenderer;
import mods.betterfoliage.client.render.features.DirtGrassRenderer;
import mods.betterfoliage.client.render.features.GrassRenderer;
import mods.betterfoliage.client.render.features.LeafRenderer;
import mods.betterfoliage.client.render.features.LilypadRenderer;
import mods.betterfoliage.client.render.features.LogRenderer;
import mods.betterfoliage.client.render.features.MyceliumRenderer;
import mods.betterfoliage.client.render.features.NetherrackRenderer;
import mods.betterfoliage.client.render.features.ReedsRenderer;
import mods.betterfoliage.client.render.particles.LeafParticleRenderer;
import mods.betterfoliage.client.render.particles.SoulParticleRenderer;
import mods.betterfoliage.config.gui.ConfigsGui;

@SideOnly(Side.CLIENT)
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

        LeafRegistry.getInstance();
        GrassRegistry.getInstance();
        WindTracker.getInstance();

        initBlockRenderers();
        initParticleRenderers();

        openConfigMenu = new KeyBinding("key.betterfoliage.gui", F8_KEYCODE, BetterFoliageMod.MOD_NAME);
        cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(openConfigMenu);
        FMLCommonHandler.instance()
            .bus()
            .register(eventListenerInstance);
    }

    // TODO: Consider removing the singletons here maybe
    private static void initBlockRenderers() {
        blockRenderers = new BlockRenderer[] {
            // spotless:off
            GrassRenderer.getInstance(),
            LogRenderer.getInstance(),
            MyceliumRenderer.getInstance(),
            LeafRenderer.getInstance(),
            CactusRenderer.getInstance(),
            LilypadRenderer.getInstance(),
            NetherrackRenderer.getInstance(),
            CoralRenderer.getInstance(),
            ReedsRenderer.getInstance(),
            AlgaeRenderer.getInstance(),
            DirtGrassRenderer.getInstance(),
            DirtGrassLogRenderer.getInstance(),
            // spotless:on
        };
    }

    private static void initParticleRenderers() {
        LeafParticleRenderer.initSprites();
        SoulParticleRenderer.initSprites();
    }

    public static BlockRenderer getEligibleBlockRenderer(IBlockAccess world, int x, int y, int z) {
        if (blockRenderers != null) {
            for (BlockRenderer blockRenderer : blockRenderers) {
                if (blockRenderer.isEligible(world, x, y, z)) return blockRenderer;
            }
        }
        return null;
    }

    /// Event subscription ///

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {

        // TODO Refactor the entire config system
        // - Allow for config class to use static variables
        // - Prevent unused-but-present configs from appearing in the menu. See Issue #23
        // (possibly clear them from the config file too)
        // - Complete overhaul of block lists:
        // - Configurable by prioritized property files in resource packs (Like natural textures in RPMCPatcher)
        // - Config menu should show these as read-only options.
        // - Allow adding / removing options on top (which saves them to the user config list)
        if (openConfigMenu.isPressed()) {
            FMLClientHandler.instance()
                .showGuiScreen(new ConfigsGui(Minecraft.getMinecraft().currentScreen));
        }
    }
}
