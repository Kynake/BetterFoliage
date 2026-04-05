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
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.client.resource.generators.TextureGenerator;

public class ResourceManager {

    public static SimpleReloadableResourceManager getResourceManager() {
        return (SimpleReloadableResourceManager) Minecraft.getMinecraft()
            .getResourceManager();
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
            if (pack instanceof TextureGenerator) {
                // Ignore mod's own generated packs
                continue;
            }

            if (pack instanceof AbstractResourcePack abstractPack) {
                File packFile = abstractPack.resourcePackFile;
                if (packFile.isDirectory()) {
                    addFromFolderIfPossible(packFile, domain, prefix, suffix, res, "");
                    continue;
                }

                if (packFile.isFile()) {
                    try (ZipFile zip = new ZipFile(packFile)) {
                        addFromZipIfPossible(zip, domain, prefix, suffix, res);
                    } catch (IOException e) {
                        BetterFoliageMod.log.error("Error reading zip file in pattern search", e);
                    }
                }
            } else if (pack instanceof DefaultResourcePack) {
                // TODO Implement if needed
                BetterFoliageMod.log.warn("DefaultResourcePack objects not supported");
            } else {
                BetterFoliageMod.log.error(
                    "Unsupported IResourcePack implementation: {}",
                    pack.getClass()
                        .getCanonicalName());
            }
        }

        return res;
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
