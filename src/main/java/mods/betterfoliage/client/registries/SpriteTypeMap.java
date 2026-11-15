package mods.betterfoliage.client.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.StringUtils;

import com.github.bsideup.jabel.Desugar;

import mods.betterfoliage.client.resource.ResourceUtils;

public class SpriteTypeMap {

    @Desugar
    private record TypeMapping(String domain, String path) {}

    private final Map<TypeMapping, String> spriteMap;

    public SpriteTypeMap(ResourceLocation propsResource) {
        Map<String, String> props = ResourceUtils.assemblePropertiesByResource(propsResource);

        if (props.isEmpty()) {
            spriteMap = Collections.emptyMap();
            return;
        }

        spriteMap = new HashMap<>(props.size());

        for (Map.Entry<String, String> entry : props.entrySet()) {
            String[] location = StringUtils.split(entry.getKey(), ':');
            if (location.length == 0) continue;

            String domain, path;
            if (location.length > 1) {
                domain = location[0];
                path = location[1];
            } else {
                domain = null;
                path = location[0];
            }

            spriteMap.put(new TypeMapping(domain, path), entry.getValue());
        }
    }

    public String getSpriteType(IIcon sprite, String defaultType) {
        ResourceLocation spritePartial = new ResourceLocation(sprite.getIconName());
        for (Map.Entry<TypeMapping, String> entry : spriteMap.entrySet()) {
            TypeMapping mapping = entry.getKey();
            if (mapping.domain != null) {
                if (!spritePartial.getResourceDomain()
                    .equals(mapping.domain)) continue;
            }

            String spriteName = spritePartial.getResourcePath();
            if (spriteName.startsWith("textures/")) {
                spriteName = spriteName.substring(9);
            }

            if (spriteName.startsWith("blocks/")) {
                spriteName = spriteName.substring(7);
            }

            if (StringUtils.containsIgnoreCase(spriteName, mapping.path)) {
                return entry.getValue();
            }
        }

        return defaultType;
    }
}
