package mods.betterfoliage.mixins.early.minecraft;

import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import mods.betterfoliage.mixins.interfaces.minecraft.ICustomSidePositionRenderer;

@SuppressWarnings("UnusedMixin")
@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_CustomSidePositions implements ICustomSidePositionRenderer {

    @Unique
    private double betterfoliage$separation = 0;

    @Unique
    private double betterfoliage$perSideScale = 1;

    // spotless:off
    @Unique private boolean betterfoliage$customBottom = false;
    @Unique private boolean betterfoliage$customTop = false;
    @Unique private boolean betterfoliage$customSides = false;
    // spotless:on

    @Override
    public void betterfoliage$setSidesWithCustomProperties(double separation, double scale, boolean customSides,
        boolean customTop, boolean customBottom) {
        betterfoliage$separation = separation;
        betterfoliage$perSideScale = scale / 2.0;

        betterfoliage$customBottom = customBottom;
        betterfoliage$customTop = customTop;
        betterfoliage$customSides = customSides;
    }

    @Override
    public void betterfoliage$resetCustomProperties() {
        betterfoliage$separation = 0;
        betterfoliage$perSideScale = 0;

        betterfoliage$customBottom = false;
        betterfoliage$customTop = false;
        betterfoliage$customSides = false;
    }

    /// Bottom
    @ModifyVariable(
        method = "renderFaceYNeg",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleBottomMinX(double minX) {
        return betterfoliage$customBottom ? minX - betterfoliage$perSideScale : minX;
    }

    @ModifyVariable(
        method = "renderFaceYNeg",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleBottomMaxX(double maxX) {
        return betterfoliage$customBottom ? maxX + betterfoliage$perSideScale : maxX;
    }

    @ModifyVariable(
        method = "renderFaceYNeg",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleBottomMinZ(double minZ) {
        return betterfoliage$customBottom ? minZ - betterfoliage$perSideScale : minZ;
    }

    @ModifyVariable(
        method = "renderFaceYNeg",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleBottomMaxZ(double maxZ) {
        return betterfoliage$customBottom ? maxZ + betterfoliage$perSideScale : maxZ;
    }

    @ModifyVariable(
        method = "renderFaceYNeg",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$bottomYSeparation(double y) {
        return betterfoliage$customBottom ? y - betterfoliage$separation : y;
    }

    /// Top
    @ModifyVariable(
        method = "renderFaceYPos",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleTopMinX(double minX) {
        return betterfoliage$customTop ? minX - betterfoliage$perSideScale : minX;
    }

    @ModifyVariable(
        method = "renderFaceYPos",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleTopMaxX(double maxX) {
        return betterfoliage$customTop ? maxX + betterfoliage$perSideScale : maxX;
    }

    @ModifyVariable(
        method = "renderFaceYPos",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleTopMinZ(double minZ) {
        return betterfoliage$customTop ? minZ - betterfoliage$perSideScale : minZ;
    }

    @ModifyVariable(
        method = "renderFaceYPos",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleTopMaxZ(double maxZ) {
        return betterfoliage$customTop ? maxZ + betterfoliage$perSideScale : maxZ;
    }

    @ModifyVariable(
        method = "renderFaceYPos",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$topYSeparation(double y) {
        return betterfoliage$customTop ? y + betterfoliage$separation : y;
    }

    /// North
    @ModifyVariable(
        method = "renderFaceZNeg",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleNorthMinX(double minX) {
        return betterfoliage$customSides ? minX - betterfoliage$perSideScale : minX;
    }

    @ModifyVariable(
        method = "renderFaceZNeg",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleNorthMaxX(double maxX) {
        return betterfoliage$customSides ? maxX + betterfoliage$perSideScale : maxX;
    }

    @ModifyVariable(
        method = "renderFaceZNeg",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleNorthMinY(double minY) {
        return betterfoliage$customSides ? minY - betterfoliage$perSideScale : minY;
    }

    @ModifyVariable(
        method = "renderFaceZNeg",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleNorthMaxY(double maxY) {
        return betterfoliage$customSides ? maxY + betterfoliage$perSideScale : maxY;
    }

    @ModifyVariable(
        method = "renderFaceZNeg",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$northZSeparation(double z) {
        return betterfoliage$customSides ? z - betterfoliage$separation : z;
    }

    /// South
    @ModifyVariable(
        method = "renderFaceZPos",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleSouthMinX(double minX) {
        return betterfoliage$customSides ? minX - betterfoliage$perSideScale : minX;
    }

    @ModifyVariable(
        method = "renderFaceZPos",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleSouthMaxX(double maxX) {
        return betterfoliage$customSides ? maxX + betterfoliage$perSideScale : maxX;
    }

    @ModifyVariable(
        method = "renderFaceZPos",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleSouthMinY(double minY) {
        return betterfoliage$customSides ? minY - betterfoliage$perSideScale : minY;
    }

    @ModifyVariable(
        method = "renderFaceZPos",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleSouthMaxY(double maxY) {
        return betterfoliage$customSides ? maxY + betterfoliage$perSideScale : maxY;
    }

    @ModifyVariable(
        method = "renderFaceZPos",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$southZSeparation(double z) {
        return betterfoliage$customSides ? z + betterfoliage$separation : z;
    }

    /// West
    @ModifyVariable(
        method = "renderFaceXNeg",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleWestMinY(double minY) {
        return betterfoliage$customSides ? minY - betterfoliage$perSideScale : minY;
    }

    @ModifyVariable(
        method = "renderFaceXNeg",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleWestMaxY(double maxY) {
        return betterfoliage$customSides ? maxY + betterfoliage$perSideScale : maxY;
    }

    @ModifyVariable(
        method = "renderFaceXNeg",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleWestMinZ(double minZ) {
        return betterfoliage$customSides ? minZ - betterfoliage$perSideScale : minZ;
    }

    @ModifyVariable(
        method = "renderFaceXNeg",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleWestMaxZ(double maxZ) {
        return betterfoliage$customSides ? maxZ + betterfoliage$perSideScale : maxZ;
    }

    @ModifyVariable(
        method = "renderFaceXNeg",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$westXSeparation(double x) {
        return betterfoliage$customSides ? x - betterfoliage$separation : x;
    }

    /// East
    @ModifyVariable(
        method = "renderFaceXPos",
        ordinal = 12,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleEastMinY(double minY) {
        return betterfoliage$customSides ? minY - betterfoliage$perSideScale : minY;
    }

    @ModifyVariable(
        method = "renderFaceXPos",
        ordinal = 13,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleEastMaxY(double maxY) {
        return betterfoliage$customSides ? maxY + betterfoliage$perSideScale : maxY;
    }

    @ModifyVariable(
        method = "renderFaceXPos",
        ordinal = 14,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleEastMinZ(double minZ) {
        return betterfoliage$customSides ? minZ - betterfoliage$perSideScale : minZ;
    }

    @ModifyVariable(
        method = "renderFaceXPos",
        ordinal = 15,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$scaleEastMaxZ(double maxZ) {
        return betterfoliage$customSides ? maxZ + betterfoliage$perSideScale : maxZ;
    }

    @ModifyVariable(
        method = "renderFaceXPos",
        ordinal = 11,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;enableAO:Z",
            opcode = Opcodes.GETFIELD))
    private double betterfoliage$eastXSeparation(double x) {
        return betterfoliage$customSides ? x + betterfoliage$separation : x;
    }
}
