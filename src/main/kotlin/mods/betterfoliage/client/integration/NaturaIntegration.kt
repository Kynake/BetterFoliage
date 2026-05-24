package mods.betterfoliage.client.integration

//@SideOnly(Side.CLIENT)
//object NaturaIntegration {
//
//    private const val BUSH_GROWTH_STAGES = 3
//    private const val BUSH_METAS_PER_STAGE = 4
//
//    @JvmStatic val naturaLeavesModel: Array<Model?> by lazy { arrayOfNulls(BUSH_GROWTH_STAGES - 1) }
//
//    fun isBerryBush(block: Block) = Mod.NATURA.isLoaded && (block is BerryBush || block is NetherBerryBush)
//
//    // TODO: join this with the method in [TinkersIntegration]
//    fun getBerryBushData(ctx: BlockContext): ModelData {
//        val world = Minecraft.getMinecraft().theWorld
//        val aabb = ctx.block.getCollisionBoundingBoxFromPool(world, ctx.x, ctx.y, ctx.z)
//
//        val stage = getBushGrowth(ctx)
//        val scale = aabb.scale
//
//        // Berry bush is fully grown, use normal leaf model
//        if (stage == BUSH_GROWTH_STAGES - 1) {
//            return ModelData(scale, aabb.center, Client.leafRenderer.leavesModel.model)
//        }
//
//        var model = naturaLeavesModel[stage]
//        if (model == null) {
//            // On first render: Clone default leaf model and adjust scale to fit this bush's growth stage
//            model = Client.leafRenderer.leavesModel.model.clone()
//            for (i in model.quads.indices) {
//                model.quads[i] = model.quads[i].scale(scale)
//            }
//            naturaLeavesModel[stage] = model
//        }
//
//        return ModelData(scale, aabb.center, model)
//    }
//
//    private fun getBushGrowth(ctx: BlockContext) = min(ctx.meta / BUSH_METAS_PER_STAGE, BUSH_GROWTH_STAGES - 1)
//}
