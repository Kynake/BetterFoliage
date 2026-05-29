package mods.betterfoliage.client.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
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

import org.apache.commons.lang3.StringUtils;

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

        if (!resourceManagers.containsKey(domain)) return Collections.emptyList();

        FallbackResourceManager resourceManager = resourceManagers.get(domain);
        if (resourceManager.resourcePacks.isEmpty()) return Collections.emptyList();

        return Lists.reverse(resourceManager.resourcePacks);
    }

    public static List<InputStream> findAllResourcesAtLocation(ResourceLocation location) {
        List<IResourcePack> packs = getResourcePacksWithDomain(location.getResourceDomain());

        if (packs.isEmpty()) return Collections.emptyList();

        ArrayList<InputStream> files = new ArrayList<>(packs.size());
        for (IResourcePack pack : packs) {
            if (pack.resourceExists(location)) {
                try {
                    files.add(pack.getInputStream(location));
                } catch (IOException e) {
                    BetterFoliageMod.log.error(
                        "Error trying to fetch {} from pack {}: {}",
                        location,
                        pack.getPackName(),
                        e.getMessage());
                }
            }
        }

        files.trimToSize();
        return files;
    }

    public static boolean resourceExists(ResourceLocation location) {
        List<IResourcePack> packs = getResourcePacksWithDomain(location.getResourceDomain());
        for (IResourcePack pack : packs) {
            if (pack.resourceExists(location)) return true;
        }

        return false;
    }

    public static HashSet<ResourceLocation> findResourcesWithPattern(String domain, String prefix, String suffix) {
        HashSet<ResourceLocation> res = new HashSet<>();

        List<IResourcePack> packs = getResourcePacksWithDomain(domain);
        for (IResourcePack pack : packs) {
            // Ignore mod's own generated packs
            if (pack instanceof TextureGenerator) continue;

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

    public static boolean isPropertiesFile(ResourceLocation location) {
        return location.getResourcePath()
            .endsWith(".cfg");
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
        if (isMcMeta(location)) return false;

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

    public static Map<String, String> assemblePropertiesByResource(ResourceLocation location) {
        if (!isPropertiesFile(location)) {
            BetterFoliageMod.log.error("Failed to read properties: {} is not a properties file.", location);
            return Collections.emptyMap();
        }

        List<InputStream> files = findAllResourcesAtLocation(location);

        if (files.isEmpty()) return Collections.emptyMap();

        HashMap<String, String> properties = new HashMap<>();

        for (InputStream is : files) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    parsePropertyLine(line, properties);
                }
            } catch (IOException e) {
                BetterFoliageMod.log
                    .error("Failed to read InputStream for ResourceLocation {}. {}", location, e.getMessage());
            }
        }

        return properties;
    }

    private static void parsePropertyLine(String line, Map<String, String> outputMap) {
        line = line.trim();
        if (line.isEmpty() || StringUtils.startsWithAny(line, "#", "//", "!")) return;

        String[] parts = StringUtils.split(line, "=");
        if (parts == null || parts.length != 2) {
            BetterFoliageMod.log.warn("Skipping unparseable line: {}", line);
            return;
        }

        String key = parts[0].trim();

        if (key.isEmpty()) {
            BetterFoliageMod.log.warn("Skipping unparseable key in line: {}", line);
            return;
        }

        String value = parts[1].trim();
        if (value.isEmpty()) {
            BetterFoliageMod.log.warn("Skipping unparseable value in line: {}", line);
            return;
        }

        if (outputMap.containsKey(key)) {
            BetterFoliageMod.log
                .debug("Skipping existing key: {}. Original: {}, New: {}", key, outputMap.get(key), value);
            return;
        }

        outputMap.put(key, value);
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
