package mods.betterfoliage.client.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.render.TextureProvider;
import mods.betterfoliage.utils.MathUtils;

public class TextureSet implements TextureProvider {

    private final String domain;
    private final String prefix;
    private final String suffix;

    private List<IIcon> textures;

    public TextureSet(String domain, String prefix, String suffix) {
        this.domain = domain;
        this.prefix = prefix;
        this.suffix = suffix;

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public final void onTextureStitch(TextureStitchEvent.Pre event) {
        int type = event.map.getTextureType();
        boolean isValid = (type == 0 && prefix.startsWith("textures/blocks/"))
            || (type == 1 && prefix.startsWith("textures/items/"));

        if (!isValid) {
            return;
        }

        Set<ResourceLocation> resources = ResourceManager.findResourcesWithPattern(domain, prefix, suffix);
        textures = new ArrayList<>(resources.size());

        for (ResourceLocation res : resources) {
            String name = res.getResourcePath();
            int startIndex = name.lastIndexOf('/') + 1;
            int endIndex = name.lastIndexOf('.');

            if (startIndex <= 0 || startIndex > endIndex) {
                BetterFoliageMod.log.error("Invalid resource location: {}", res);
                continue;
            }

            String textureName = domain + ":" + name.substring(startIndex, endIndex);
            textures.add(event.map.registerIcon(textureName));
        }
    }

    @Override
    public IIcon getTextureForCoord(int x, int y, int z) {
        if (textures == null || textures.isEmpty()) {
            return null;
        }

        int index = Math.abs(MathUtils.hashCoords(x, y, z) % textures.size());
        return textures.get(index);
    }
}
