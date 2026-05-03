package mods.betterfoliage.client.resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.generators.TextureGenerator;

public class ResourceUtils {

    public static SimpleReloadableResourceManager getResourceManager() {
        return (SimpleReloadableResourceManager) Minecraft.getMinecraft()
            .getResourceManager();
    }

    public static IResource getResource(ResourceLocation location) throws IOException {
        return getResourceManager().getResource(location);
    }

    public static List<IResourcePack> getResourcePacksWithDomain(String domain) {
        Map<String, FallbackResourceManager> resourceManagers = getResourceManager().domainResourceManagers;

        if (!resourceManagers.containsKey(domain)) {
            return Collections.emptyList();
        }

        FallbackResourceManager resourceManager = resourceManagers.get(domain);
        if (resourceManager.resourcePacks.isEmpty()) {
            return Collections.emptyList();
        }

        return Lists.reverse(resourceManager.resourcePacks);
    }

    public static HashSet<ResourceLocation> findResourcesWithPattern(String domain, String prefix, String suffix) {
        HashSet<ResourceLocation> res = new HashSet<>();

        List<IResourcePack> packs = getResourcePacksWithDomain(domain);
        for (IResourcePack pack : packs) {
            // Ignore mod's own generated packs
            if (pack instanceof TextureGenerator) {
                continue;
            }

            // Normal resource packs
            if (pack instanceof AbstractResourcePack abstractPack) {
                File packFile = abstractPack.resourcePackFile;
                if (packFile.isDirectory()) {
                    addFromFolderIfPossible(packFile, domain, prefix, suffix, res, "");
                } else if (packFile.isFile()) {
                    try (ZipFile zip = new ZipFile(packFile)) {
                        addFromZipIfPossible(zip, domain, prefix, suffix, res);
                    } catch (IOException e) {
                        BetterFoliageMod.log.error("Error reading zip file in pattern search", e);
                    }
                }
                continue;
            }

            // TODO Implement if needed
            // MC's default textures (mapped in memory)
            if (pack instanceof DefaultResourcePack) {
                BetterFoliageMod.log.warn("DefaultResourcePack objects not supported");
                continue;
            }

            // Others
            BetterFoliageMod.log.error(
                "Unsupported IResourcePack implementation: {}",
                pack.getClass()
                    .getCanonicalName());
        }

        return res;
    }

    public static boolean isMcMeta(ResourceLocation location) {
        return location.getResourcePath()
            .endsWith(".mcmeta");
    }

    public static ResourceLocation getBaseForMcMeta(ResourceLocation mcMeta) {
        int endIndex = mcMeta.getResourcePath()
            .lastIndexOf(".mcmeta");
        return endIndex > 0 ? new ResourceLocation(
            mcMeta.getResourceDomain(),
            mcMeta.getResourcePath()
                .substring(0, endIndex))
            : mcMeta;
    }

    public static boolean resourceHasMcMeta(ResourceLocation location) {
        if (isMcMeta(location)) {
            return false;
        }

        List<IResourcePack> resourcePacks = getResourcePacksWithDomain(location.getResourceDomain());
        for (IResourcePack pack : resourcePacks) {
            if (pack.resourceExists(location)) {
                // Only the highest priority asset dictates whether it'll use a .mcmeta file or not
                return pack.resourceExists(
                    new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta"));
            }
        }

        return false;
    }

    public static String convertToSpriteName(ResourceLocation location) {
        return convertToSpriteName(location, location.getResourceDomain());
    }

    public static String convertToSpriteName(ResourceLocation location, String domain) {
        String name = location.getResourcePath();
        int startIndex = name.lastIndexOf('/') + 1;
        int endIndex = name.lastIndexOf('.');

        if (startIndex <= 0 || startIndex > endIndex) {
            BetterFoliageMod.log.error("Invalid resource location: {}", location);
            return null;
        }

        return domain + ":" + name.substring(startIndex, endIndex);
    }

    public static ResourceLocation convertToResourceLocation(IIcon sprite) {
        ResourceLocation partial = new ResourceLocation(sprite.getIconName());
        return new ResourceLocation(
            partial.getResourceDomain(),
            "textures/blocks/" + partial.getResourcePath() + ".png");
    }

    private static void addFromFolderIfPossible(File dir, String domain, String prefix, String suffix,
        HashSet<ResourceLocation> output, String basePath) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String path = basePath + file.getName() + "/";
                    addFromFolderIfPossible(file, domain, prefix, suffix, output, path);
                } else if (file.isFile()) {
                    String fileName = basePath + file.getName();
                    addMatchingFiles(fileName, domain, prefix, suffix, output);
                }
            }
        }
    }

    private static void addFromZipIfPossible(ZipFile zipFile, String domain, String prefix, String suffix,
        HashSet<ResourceLocation> output) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            addMatchingFiles(entry.getName(), domain, prefix, suffix, output);
        }
    }

    private static void addMatchingFiles(String filePath, String domain, String prefix, String suffix,
        HashSet<ResourceLocation> output) {
        String rootPrefix = "assets/" + domain + "/";
        if (!(filePath.startsWith(rootPrefix + prefix) && filePath.endsWith(suffix))) {
            return;
        }

        String resourcePath = filePath.substring(rootPrefix.length());

        output.add(new ResourceLocation(domain, resourcePath));
    }
}
