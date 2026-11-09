package mods.betterfoliage.client.commands;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.google.common.collect.ImmutableList;

import mods.betterfoliage.BetterFoliageMod;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.Logger;

public class InfoCommand extends CommandBase {
    private static final String USAGE = "/betterfoliage {block|biome}";

    @Override
    public String getCommandName() {
        return "betterfoliage";
    }

    @Override
    public List<String> getCommandAliases() {
        return ImmutableList.of("bf");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("§c" + I18n.format(USAGE)));
            return;
        }

        if (args[0].equals("biome")) {
            processBiomeCommand(sender);
            return;
        }

        if (args[0].equals("block")) {
            processBlockCommand(sender);
            return;
        }

        sender.addChatMessage(new ChatComponentText("§c" + I18n.format(USAGE)));
    }

    private void processBiomeCommand(ICommandSender sender) {
        ChunkCoordinates coords = sender.getPlayerCoordinates();
        World world = sender.getEntityWorld();

        BiomeGenBase biome = world.getBiomeGenForCoords(coords.posX, coords.posZ);

        // TODO print to chat message
        Logger log = BetterFoliageMod.log;
        log.info("Biome: {}, ID: {}", biome.biomeName, biome.biomeID);

        boolean snow = biome.getEnableSnow();
        boolean rain = !snow && biome.canSpawnLightningBolt();
        log.info("Rain? {}, Snow? {}", rain ? "YES" : "NO", snow ? "YES" : "NO");

        log.info("Temperature: {}, Humidity: {}", biome.temperature, biome.rainfall);
        printClassHierarchy(biome.getClass(), BiomeGenBase.class, sender);
    }

    private void processBlockCommand(ICommandSender sender) {
        MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
        if (target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        World world = sender.getEntityWorld();
        Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);
        int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);

        RegistryNamespaced registry = Block.blockRegistry;
        String name = registry.getNameForObject(block) + ":" + meta;

        // TODO print to chat message
        BetterFoliageMod.log.info("<{}>, ID: {}", name, registry.getIDForObject(block));
        printClassHierarchy(block.getClass(), Block.class, sender);
    }

    private static void printClassHierarchy(Class<?> leafClass, Class<?> baseClass, ICommandSender sender) {
        Logger log = BetterFoliageMod.log;
        log.info("Class hierarchy:");

        int i = 0;
        Class<?> current = leafClass;

        do {
            String classpath = current.getCanonicalName();
            if (classpath == null || classpath.isEmpty()) {
                classpath = "<anonymous class>";
            }
            log.info("[{}]: {}", i++, classpath);
            current = current.getSuperclass();
        } while (!(current == null || current == baseClass));

        if (current != null) {
            String classpath = current.getCanonicalName();
            if (classpath == null || classpath.isEmpty()) {
                classpath = "<anonymous class>";
            }
            log.info("[{}]: {} [BASE CLASS]", i, classpath);
        }

        // TODO print to chat message
    }
}
