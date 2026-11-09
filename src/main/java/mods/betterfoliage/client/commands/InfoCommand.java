package mods.betterfoliage.client.commands;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.collect.ImmutableList;

public class InfoCommand extends CommandBase {

    private static final String USAGE = EnumChatFormatting.RED + "/betterfoliage <block|biome>";
    private static final String SEPARATOR = EnumChatFormatting.GRAY + "-----------------------------------------------";

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
            sender.addChatMessage(new ChatComponentText(USAGE));
            return;
        }

        if (args[0].equals("biome")) {
            sender.addChatMessage(new ChatComponentText(SEPARATOR));
            processBiomeCommand(sender);
            sender.addChatMessage(new ChatComponentText(SEPARATOR));
            return;
        }

        if (args[0].equals("block")) {
            sender.addChatMessage(new ChatComponentText(SEPARATOR));
            processBlockCommand(sender);
            sender.addChatMessage(new ChatComponentText(SEPARATOR));
            return;
        }

        sender.addChatMessage(new ChatComponentText(USAGE));
    }

    private void processBiomeCommand(ICommandSender sender) {
        ChunkCoordinates coords = sender.getPlayerCoordinates();
        World world = sender.getEntityWorld();

        BiomeGenBase biome = world.getBiomeGenForCoords(coords.posX, coords.posZ);

        String line1 = I18n.format("commands.betterfoliage.biome.line1", biome.biomeName, biome.biomeID);
        sender.addChatMessage(new ChatComponentText(line1));

        boolean snow = biome.getEnableSnow();
        boolean rain = !snow && biome.canSpawnLightningBolt();

        String rainLang = I18n.format(rain ? "commands.betterfoliage.yes" : "commands.betterfoliage.no");
        String snowLang = I18n.format(snow ? "commands.betterfoliage.yes" : "commands.betterfoliage.no");
        String line2 = I18n.format("commands.betterfoliage.biome.line2", rainLang, snowLang);
        sender.addChatMessage(new ChatComponentText(line2));

        String line3 = I18n.format("commands.betterfoliage.biome.line3", biome.temperature, biome.rainfall);
        sender.addChatMessage(new ChatComponentText(line3));

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
        String name = "<" + registry.getNameForObject(block) + ":" + meta + ">";

        String line = I18n.format("commands.betterfoliage.block", name, registry.getIDForObject(block));
        sender.addChatMessage(new ChatComponentText(line));

        printClassHierarchy(block.getClass(), Block.class, sender);
    }

    private static void printClassHierarchy(Class<?> leafClass, Class<?> baseClass, ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(""));
        sender.addChatMessage(new ChatComponentTranslation("commands.betterfoliage.class.title"));

        int i = 0;
        Class<?> current = leafClass;

        do {
            String classpath = current.getCanonicalName();
            if (classpath == null || classpath.isEmpty()) {
                classpath = I18n.format("commands.betterfoliage.class.anonymous") + " " + current.getName();
            }

            String line = I18n.format("commands.betterfoliage.class.line", i++, classpath);
            sender.addChatMessage(new ChatComponentText(line));

            current = current.getSuperclass();
        } while (!(current == null || current == baseClass));

        if (current != null) {
            String classpath = current.getCanonicalName();
            if (classpath == null || classpath.isEmpty()) {
                classpath = I18n.format("commands.betterfoliage.class.anonymous") + " " + current.getName();
            }

            String line = I18n.format("commands.betterfoliage.class.line", i++, classpath);
            line += " " + I18n.format("commands.betterfoliage.class.base");
            sender.addChatMessage(new ChatComponentText(line));
        }
    }
}
