package mods.betterfoliage.loader

import cpw.mods.fml.relauncher.FMLLaunchHandler
import mods.octarinecore.metaprog.Transformer
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.IASTORE

@Deprecated("ASM Transforms were converted to mixins. Once ShadersMod support is removed this class will be deleted")
class BetterFoliageTransformer : Transformer() {

    init {
        if (FMLLaunchHandler.side().isClient) asmTransformShadersMod()
    }

    @Deprecated("ShadersMod is very old at this point. Expect compatibility with it to be removed once this feature set is ported to other shader mods (Angelica, Swansong, _maybe_ Optifine).")
    fun asmTransformShadersMod() {
        // where: shadersmodcore.client.Shaders.pushEntity()
        // what: invoke BF code to overrule block data
        // why: allows us to change the block ID seen by shader programs
        transformMethod(Refs.pushEntity) {
            find(IASTORE)?.insertBefore {
                log.info("Applying Shaders.pushEntity() block id override")
                varinsn(ALOAD, 1)
                invokeStatic(Refs.getBlockIdOverride)
            } ?: log.warn("Failed to apply Shaders.pushEntity() block id override!")
        }
    }
}
