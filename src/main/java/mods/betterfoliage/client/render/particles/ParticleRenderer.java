package mods.betterfoliage.client.render.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mods.betterfoliage.BetterFoliageMod;

public abstract class ParticleRenderer extends EntityFX {

    protected Tessellator tessellator;
    protected float partialTickTime;
    protected float quadRotX, quadRotZ, quadRotYZ, quadRotXY, quadRotXZ;

    protected boolean quadMirrorHorizontally;
    protected double quadCenterX, quadCenterY, quadCenterZ;
    protected float quadRotationRadians;

    /// Other vars also use in rendering, inherited from superclass:
    ///
    /// double posX, posY, posZ;
    /// double prevPosX, prevPosY, prevPosZ;
    /// double interpPosX, interpPosY, interpPosZ;
    /// IIcon particleIcon;
    /// float particleScale;
    /// float particleRed, particleGreen, particleBlue, particleAlpha;

    protected ParticleRenderer(IIcon sprite, World world, int x, int y, int z) {
        super(world, x + 0.5, y + 0.5, z + 0.5);
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

    protected abstract void update();

    @Override
    public final void onUpdate() {
        super.onUpdate();
        update();
    }

    protected void render() {
        // Default implementation.
        // Assumes superclass vars + tessellator and partialTickTime are already set.

        quadMirrorHorizontally = false;
        quadRotationRadians = 0.0f;
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

        float minV = particleIcon.getMinV();
        float maxV = particleIcon.getMaxV();

        float firstRotX = quadRotX + quadRotXY;
        float firstRotZ = quadRotYZ + quadRotXZ;

        float secondRotX = quadRotX - quadRotXY;
        float secondRotZ = quadRotYZ - quadRotXZ;

        float rotSin = MathHelper.sin(quadRotationRadians);
        float rotCos = MathHelper.cos(quadRotationRadians);

        float rotAX = (rotCos * firstRotX + rotSin * secondRotX) * particleScale;
        float rotAY = (rotCos * quadRotZ + rotSin * -quadRotZ) * particleScale;
        float rotAZ = (rotCos * firstRotZ + rotSin * secondRotZ) * particleScale;

        float rotBX = (-rotSin * firstRotX + rotCos * secondRotX) * particleScale;
        float rotBY = (-rotSin * quadRotZ + rotCos * -quadRotZ) * particleScale;
        float rotBZ = (-rotSin * firstRotZ + rotCos * secondRotZ) * particleScale;

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
