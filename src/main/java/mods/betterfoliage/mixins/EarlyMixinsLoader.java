package mods.betterfoliage.mixins;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import mods.betterfoliage.loader.BetterFoliageTransformer;

// TODO remove once everything is mixins
@IFMLLoadingPlugin.TransformerExclusions({ "mods.betterfoliage.loader", "mods.betterfoliage.mixins",
    "mods.octarinecore.metaprog", "kotlin", })

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class EarlyMixinsLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    // TODO remove once everything is mixins
    @Override
    public final String[] getASMTransformerClass() {
        return new String[] { BetterFoliageTransformer.class.getCanonicalName() };
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
