package mods.betterfoliage.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.betterfoliage.client.config.Config;
import mods.betterfoliage.utils.MathUtils;

@SideOnly(Side.CLIENT)
public final class WindTracker {

    private static final float PI2 = (float) Math.PI * 2f;

    private static WindTracker instance = null;

    public static float currentX, currentZ;

    private final Random rng;
    private long nextChangeTime;

    private float targetX, targetZ;

    public static WindTracker getInstance() {
        if (instance == null) {
            instance = new WindTracker();
        }

        return instance;
    }

    private WindTracker() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        this.rng = new Random();
    }

    @SubscribeEvent
    public void handleWorldTick(TickEvent.ClientTickEvent event) {
        if (!(Config.INSTANCE.getEnabled() & Config.fallingLeaves.INSTANCE.getEnabled())) return;
        if (event.phase != TickEvent.Phase.START) return;

        final World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        if (world.getWorldTime() >= nextChangeTime) {
            changeWindTargetDirection(world);
        }

        final float changeRate = world.isRaining() ? 0.015f : 0.005f;

        currentX += MathHelper.clamp_float(targetX - currentX, -changeRate, changeRate);
        currentZ += MathHelper.clamp_float(targetZ - currentZ, -changeRate, changeRate);
    }

    @SubscribeEvent
    public void handleWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote) {
            changeWindTargetDirection(event.world);
        }
    }

    private void changeWindTargetDirection(World world) {
        // Schedule next change to happen in the next 6 to 10 seconds;
        nextChangeTime = world.getWorldTime() + MathUtils.randomBetween(rng, 120, 200);

        final float dirRadians = PI2 * rng.nextFloat();
        float speed = Math.abs((float) (rng.nextGaussian() * Config.fallingLeaves.INSTANCE.getWindStrength()));
        if (world.isRaining()) {
            speed += Math.abs((float) (rng.nextGaussian() * Config.fallingLeaves.INSTANCE.getStormStrength()));
        }

        targetX = MathHelper.cos(dirRadians) * speed;
        targetZ = MathHelper.sin(dirRadians) * speed;
    }
}
