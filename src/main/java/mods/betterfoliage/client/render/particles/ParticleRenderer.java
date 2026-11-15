package mods.betterfoliage.client.render.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;
import mods.betterfoliage.utils.MathUtils;

public abstract class ParticleRenderer extends EntityFX {

    protected Tessellator tessellator;
    protected float partialTickTime;
    protected float quadRotX, quadRotZ, quadRotYZ, quadRotXY, quadRotXZ;

    protected boolean quadMirrorHorizontally;
    protected double quadCenterX, quadCenterY, quadCenterZ;

    protected float rotationRadians = 0f;
    private float previousTickRotation = 0f;

    /// Other vars also use in rendering, inherited from superclass:
    ///
    /// double posX, posY, posZ;
    /// double prevPosX, prevPosY, prevPosZ;
    /// double interpPosX, interpPosY, interpPosZ;
    /// IIcon particleIcon;
    /// float particleScale;
    /// float particleRed, particleGreen, particleBlue, particleAlpha;

    protected ParticleRenderer(IIcon sprite, World world, double x, double y, double z) {
        super(world, x, y, z);
        if (sprite != null) {
            particleIcon = sprite;
            Minecraft.getMinecraft().effectRenderer.addEffect(this);
        } else {
            BetterFoliageMod.log.error(
                "Can't spawn particle of type [{}], IIcon is null.",
                this.getClass()
                    .getCanonicalName());
            setDead();
        }
    }

    protected ParticleRenderer(IIcon sprite, World world, int x, int y, int z) {
        // Spawn particle in the center of the block
        this(sprite, world, x + 0.5, y + 0.5, z + 0.5);
    }

    protected abstract void update();

    @Override
    public final void onUpdate() {
        super.onUpdate();
        previousTickRotation = rotationRadians;
        update();
    }

    protected void render() {
        // Default implementation.
        // Assumes superclass vars + tessellator and partialTickTime are already set.

        quadMirrorHorizontally = false;
        calculateQuadCenter(posX, posY, posZ, prevPosX, prevPosY, prevPosZ);

        renderBillboardQuad();
    }

    @Override
    public final void renderParticle(Tessellator tessellator, float partialTickTime, float rotX, float rotZ,
        float rotYZ, float rotXY, float rotXZ) {
        // Default values added to all
        this.tessellator = tessellator;
        this.partialTickTime = partialTickTime;
        quadRotX = rotX;
        quadRotZ = rotZ;
        quadRotYZ = rotYZ;
        quadRotXY = rotXY;
        quadRotXZ = rotXZ;

        render();
    }

    public final void renderBillboardQuad() {
        float minU, maxU;
        if (quadMirrorHorizontally) {
            minU = particleIcon.getMaxU();
            maxU = particleIcon.getMinU();
        } else {
            minU = particleIcon.getMinU();
            maxU = particleIcon.getMaxU();
        }

        final float minV = particleIcon.getMinV();
        final float maxV = particleIcon.getMaxV();

        final float firstRotX = quadRotX + quadRotXY;
        final float firstRotZ = quadRotYZ + quadRotXZ;

        final float secondRotX = quadRotX - quadRotXY;
        final float secondRotZ = quadRotYZ - quadRotXZ;

        final float frameRotation = MathUtils.lerp(previousTickRotation, rotationRadians, partialTickTime);

        final float rotSin = MathHelper.sin(frameRotation);
        final float rotCos = MathHelper.cos(frameRotation);

        final float rotAX = (rotCos * firstRotX + rotSin * secondRotX) * particleScale;
        final float rotAY = (rotCos * quadRotZ + rotSin * -quadRotZ) * particleScale;
        final float rotAZ = (rotCos * firstRotZ + rotSin * secondRotZ) * particleScale;

        final float rotBX = (-rotSin * firstRotX + rotCos * secondRotX) * particleScale;
        final float rotBY = (-rotSin * quadRotZ + rotCos * -quadRotZ) * particleScale;
        final float rotBZ = (-rotSin * firstRotZ + rotCos * secondRotZ) * particleScale;

        tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
        tessellator.addVertexWithUV(quadCenterX - rotAX, quadCenterY - rotAY, quadCenterZ - rotAZ, maxU, maxV);
        tessellator.addVertexWithUV(quadCenterX - rotBX, quadCenterY - rotBY, quadCenterZ - rotBZ, maxU, minV);
        tessellator.addVertexWithUV(quadCenterX + rotAX, quadCenterY + rotAY, quadCenterZ + rotAZ, minU, minV);
        tessellator.addVertexWithUV(quadCenterX + rotBX, quadCenterY + rotBY, quadCenterZ + rotBZ, minU, maxV);
    }

    public final void calculateQuadCenter(double posX, double posY, double posZ, double prevX, double prevY,
        double prevZ) {
        quadCenterX = prevX + (posX - prevX) * partialTickTime - interpPosX;
        quadCenterY = prevY + (posY - prevY) * partialTickTime - interpPosY;
        quadCenterZ = prevZ + (posZ - prevZ) * partialTickTime - interpPosZ;
    }

    public final void setRGBColor(int colorARGB) {
        particleRed = (float) (colorARGB >> 16 & 0xFF) / 255.0f;
        particleGreen = (float) (colorARGB >> 8 & 0xFF) / 255.0f;
        particleBlue = (float) (colorARGB & 0xFF) / 255.0f;
    }

    @Override
    public final int getFXLayer() {
        return 1;
    }
}
