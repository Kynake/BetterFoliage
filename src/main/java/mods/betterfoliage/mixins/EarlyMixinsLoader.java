package mods.betterfoliage.mixins;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("Better Foliage core plugin / early mixin loader")
public class EarlyMixinsLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public final String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public final String getModContainerClass() {
        return null;
    }

    @Override
    public final String getSetupClass() {
        return null;
    }

    @Override
    public final void injectData(Map<String, Object> data) {}

    @Override
    public final String getAccessTransformerClass() {
        return null;
    }

    @Override
    public final String getMixinConfig() {
        return "mixins.BetterFoliage.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }
}
